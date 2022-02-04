package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.SwitchManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.Vec3i;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.util.HashSet;
import java.util.Set;

import static me.pignol.swift.api.util.RotationUtil.RotationType;

public class Surround extends Module {

    private final Value<RotationType> rotation = new Value<>("Rotation", RotationType.NONE);
    private final Value<Integer> delay = new Value<>("Delay", 0, 0, 300);
    private final Value<Integer> blocksPerTick = new Value<>("Blocks/Tick", 10, 1, 20);
    private final Value<Boolean> center = new Value<>("Center", false);
    private final Value<Boolean> attack = new Value<>("Attack", false);
    private final Value<Boolean> bypassCancel = new Value<>("BypassCancel", true);
    private final Value<Integer> attackDelay = new Value<>("AttackDelay", 100, 0, 1000, v -> attack.getValue());
    private final Value<Boolean> floor = new Value<>("Floor", false);
    private final Value<Integer> extender = new Value<>("Extend", 2, 0, 10);
    private final Value<Integer> retries = new Value<>("Retries", 4, 0, 25);
    private final Value<Integer> ticksExisted = new Value<>("TicksExisted", 15, 0, 30);

    private final Object2IntOpenHashMap<BlockPos> retryMap = new Object2IntOpenHashMap<>();
    private final Set<Vec3d> extendingBlocks = new HashSet<>();
    private final StopWatch timer = new StopWatch();
    private final StopWatch retryTimer = new StopWatch();
    private final StopWatch attackTimer = new StopWatch();

    public static boolean isPlacing;

    private boolean onEchest;
    private boolean placingEChest;
    private boolean didPlace;
    private int placements, extenders, obbySlot;
    private double startPosY;

    public Surround() {
        super("Surround", Category.COMBAT);
    }

    @Override
    public void onDisable() {
        obbySlot = -1;
        onEchest = false;
        didPlace = false;
        placements = 0;
        isPlacing = false;
    }

    @Override
    public void onEnable() {
        if (isNull()) {
            setEnabled(false);
            return;
        }
        obbySlot = -1;
        if (center.getValue()) {
            BlockPos centerPos = mc.player.getPosition();
            double x = centerPos.getX() + 0.5D;
            double y = centerPos.getY();
            double z = centerPos.getZ() + 0.5D;

            mc.player.connection.sendPacket(new CPacketPlayer.Position(x, y, z, true));
            mc.player.setPosition(x, y, z);
        }

        startPosY = mc.player.posY;
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (rotation.getValue() != RotationType.NORMAL) {
            doFeetPlace();
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (rotation.getValue() == RotationType.NORMAL) {
            doFeetPlace();
        }
    }

    public void doFeetPlace() {
        isPlacing = false;
        if (check()) {
            return;
        }


        if (bypassCancel.getValue()) {
            SwitchManager.getInstance().setDontReset(true);
        }
        Vec3d[] unsafeBlocks = BlockUtil.getUnsafeBlocksArray(mc.player.getPositionVector(), onEchest ? 1 : 0, floor.getValue());
        if (unsafeBlocks.length != 0) {
            placeBlocks(mc.player.getPositionVector(), unsafeBlocks, true, false, false);
        }

        if (extender.getValue() > 0) {
            processExtendingBlocks();
        }

        if (attack.getValue()) {
            doAttack();
        }

        if (bypassCancel.getValue()) {
            SwitchManager.getInstance().setDontReset(false);
        }

        if (didPlace) {
            timer.reset();
        }
    }

    private void doAttack() {
        if (attackTimer.passed(attackDelay.getValue())) {
            BlockPos playerPos = new BlockPos(mc.player.getPositionVector());
            Entity crystal = null;
            for (Vec3i offset : BlockUtil.OFFSETS_VECIh) {
                BlockPos pos = playerPos.add(offset.getX(), offset.getY(), offset.getZ());
                for (Entity entity : mc.world.loadedEntityList) {
                    if (entity instanceof EntityEnderCrystal) {
                        if (entity.getPosition().equals(pos)) {
                            crystal = entity;
                            break;
                        }
                    }
                }
            }

            if (crystal != null) {
                mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                attackTimer.reset();
            }
        }
    }



    private boolean placeBlocks(Vec3d pos, Vec3d[] vec3ds, boolean hasHelpingBlocks, boolean isHelping, boolean extending) {
        int helpings = 0;
        if (pos == null) return false;
        boolean gotHelp;

        BlockPos pos1 = new BlockPos(pos);
        for (Vec3d vec3d : vec3ds) {
            if (vec3d == null) {
                continue;
            }
            gotHelp = true;
            helpings++;
            if (isHelping && helpings > 1) {
                return false;
            }
            BlockPos position = pos1.add(vec3d.x, vec3d.y, vec3d.z);
            BlockInfo info = isPositionPlaceable(position);
            Entity entity = info.blockingEntity;
            switch (info.state) {
                case -1:
                    continue;
                case 1:
                    if (entity instanceof EntityLivingBase) {
                        if (!extending && extender.getValue() > 0 && extenders < extender.getValue()) {
                            placeBlocks(mc.player.getPositionVector().add(vec3d), BlockUtil.getUnsafeBlocksArray(mc.player.getPositionVector().add(vec3d), 0, floor.getValue()), hasHelpingBlocks, false, true);
                            extendingBlocks.add(vec3d);
                            extenders++;
                        }
                    } else {
                        if (entity instanceof EntityEnderCrystal && attack.getValue()) {
                            if (attackTimer.passed(attackDelay.getValue())) {
                                Entity hittable = null;
                                for (Vec3i offset : BlockUtil.OFFSETS_VECIh) {
                                    for (Entity crystal : mc.world.loadedEntityList) {
                                        if (crystal instanceof EntityEnderCrystal) {
                                            if (crystal.getDistanceSq(mc.player) > 36)
                                                continue;
                                            if (crystal.getPosition().equals(position)) {
                                                float damage = DamageUtil.calculate(crystal, mc.player);
                                                if (damage > 8 || damage > EntityUtil.getHealth(mc.player) - 4.0F)
                                                    continue;
                                                hittable = crystal;
                                                break;
                                            }
                                        }
                                    }
                                }

                                if (hittable != null) {
                                    mc.playerController.attackEntity(mc.player, hittable);
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                    attackTimer.reset();
                                }
                            }
                        }
                        if (entity instanceof EntityEnderCrystal && entity.ticksExisted >= ticksExisted.getValue()) {
                            continue;
                        }
                        if (retryMap.getInt(position) == 0 || retryMap.getInt(position) < retries.getValue()) {
                            placeBlock(position);
                            retryMap.put(position, retryMap.getInt(position) + 1);
                            retryTimer.reset();
                            continue;
                        }
                    }
                    continue;
                case 2:
                    if (hasHelpingBlocks) {
                        gotHelp = placeBlocks(pos, BlockUtil.getHelpingBlocks(vec3d), false, true, true);
                    } else {
                        continue;
                    }
                case 3:
                    if (gotHelp) {
                        placeBlock(position);
                    }
                    if (isHelping) {
                        return true;
                    }
            }
        }
        return false;
    }

    private void processExtendingBlocks() {
        if (extendingBlocks.size() == 2 && extenders < extender.getValue()) {
            Vec3d[] array = new Vec3d[2];
            int i = 0;
            for (Vec3d vec3d : extendingBlocks) {
                array[i] = vec3d;
                i++;
            }
            int placementsBefore = placements;
            if (areClose(array) != null) {
                placeBlocks(areClose(array), BlockUtil.getUnsafeBlocksArray(areClose(array), 0, floor.getValue()), true, false, true);
            }

            if (placementsBefore < placements) {
                extendingBlocks.clear();
            }
        } else if (extendingBlocks.size() > 2 || !(extenders < extender.getValue())) {
            extendingBlocks.clear();
        }
    }

    private Vec3d areClose(Vec3d[] vec3ds) {
        int matches = 0;
        for (Vec3d vec3d : vec3ds) {
            for (Vec3d pos : BlockUtil.getUnsafeBlocksArray(mc.player.getPositionVector(), 0, floor.getValue())) {
                if (vec3d.equals(pos)) {
                    matches++;
                }
            }
        }
        if (matches == 2) {
            return mc.player.getPositionVector().add(vec3ds[0].add(vec3ds [1]));
        }
        return null;
    }


    private boolean check() {
        if (isNull()) {
            return true;
        }

        isPlacing = false;

        if (mc.player.posY > startPosY) {
            setEnabled(false);
            return true;
        }

        placingEChest = false;
        obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
            if (obbySlot == -1) {
                ChatUtil.sendMessage(ChatFormatting.RED + "[Surround] No obsidian.", -554);
                setEnabled(false);
                return true;
            } else {
                placingEChest = true;
            }
        }

        onEchest = mc.player.onGround && (mc.player.posY - (int) mc.player.posY != 0);
        didPlace = false;
        extenders = 1;
        placements = 0;

        if (retryTimer.passed(500)) {
            retryMap.clear();
            retryTimer.reset();
        }

        return !timer.passed(delay.getValue());
    }

    private BlockInfo isPositionPlaceable(BlockPos pos) {
        final IBlockState state = mc.world.getBlockState(pos);
        if (!state.getMaterial().isReplaceable()) {
            return new BlockInfo(0, null);
        }

        final AxisAlignedBB bb = placingEChest ? Blocks.ENDER_CHEST.getBoundingBox(state, mc.world, pos).offset(pos) : new AxisAlignedBB(pos);
        for (Entity entity : mc.world.getEntitiesWithinAABBExcludingEntity(null, bb)) {
            if (!entity.isDead && entity.preventEntitySpawning) {
                return new BlockInfo(1, entity);
            }
        }

        for (EnumFacing side : FacingUtil.VALUES) {
            final BlockPos neighbour = pos.offset(side);
            final IBlockState nState = mc.world.getBlockState(neighbour);
            if (nState.getBlock().canCollideCheck(nState, false)) {
                if (!nState.getMaterial().isReplaceable() && BlockUtil.canBeClicked(nState)) {
                    return new BlockInfo(3, null);
                }
            }
        }

        return new BlockInfo(2, null);
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerTick.getValue()) {
            isPlacing = true;
            int lastSlot = mc.player.inventory.currentItem;
            mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
            BlockUtil.placeBlock(pos, rotation.getValue());
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            didPlace = true;
            placements++;
        }
    }

    public static class BlockInfo {

        private final int state;
        private final Entity blockingEntity;

        public BlockInfo(int state, Entity entity) {
            this.state = state;
            this.blockingEntity = entity;
        }

        public Entity getBlockingEntity() {
            return blockingEntity;
        }

        public int getState() {
            return state;
        }

    }

}
