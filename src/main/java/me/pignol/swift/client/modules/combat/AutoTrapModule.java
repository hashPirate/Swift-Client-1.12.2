package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

import static me.pignol.swift.api.util.RotationUtil.RotationType;

public class AutoTrapModule extends Module {

    private final Value<RotationType> rotation = new Value<>("Rotation", RotationType.NONE);
    private final Value<Integer> delay = new Value<>("Delay/Place", 50, 0, 250);
    private final Value<Integer> blocksPerPlace = new Value<>("Block/Place", 8, 1, 30);
    private final Value<Double> targetRange = new Value<>("TargetRange", 10.0, 0.0, 20.0);
    private final Value<Double> range = new Value<>("PlaceRange", 6.0, 0.0, 10.0);
    private final Value<Boolean> antiScaffold = new Value<>("AntiScaffold", false);
    private final Value<Boolean> antiStep = new Value<>("AntiStep", false);
    private final Value<Boolean> legs = new Value<>("Legs", false);
    private final Value<Boolean> smartLegs = new Value<>("SmartLegs", false);
    private final Value<Boolean> platform = new Value<>("Platform", false);
    private final Value<Boolean> antiDrop = new Value<>("AntiDrop", false);
    private final Value<Boolean> entityCheck = new Value<>("NoBlock", true);
    private final Value<Boolean> attackCrystals = new Value<>("Attack", true);
    private final Value<Integer> attackDelay = new Value<>("AttackDelay", 100, 0, 500);
    private final Value<Integer> retryer = new Value<>("Retries", 4, 1, 15);
    private final Value<Boolean> render = new Value<>("Render", true);
    private final Value<Boolean> renderOutline = new Value<>("Outline", true);
    private final Value<Integer> alpha = new Value<>("Alpha", 50, 0, 255);

    private List<Vec3d> placeTargets = new ArrayList<>();
    private boolean didPlace = false;
    public EntityPlayer target;
    private int placements = 0;
    private int obbySlot;
    private boolean didSwitch;
    public static boolean isPlacing = false;

    private final Map<BlockPos, Integer> retries = new HashMap<>();
    private final StopWatch retryTimer = new StopWatch();
    private final StopWatch attackTimer = new StopWatch();
    private final StopWatch timer = new StopWatch();

    public AutoTrapModule() {
        super("AutoTrap", Category.COMBAT);
    }

    @Override
    public void onEnable() {
        if (isNull()) return;
        retries.clear();
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (target != null && render.getValue() && placeTargets.size() != 0) {
            RenderUtil.enableGL3D();
            final BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
            for (int i = 0, placeTargetsSize = placeTargets.size(); i < placeTargetsSize; i++)
            {
                final Vec3d vec = placeTargets.get(i);
                pos.setPos(MathHelper.floor(vec.x), MathHelper.floor(vec.y), MathHelper.floor(vec.z));
                AxisAlignedBB bb = RenderUtil.getRenderBB(pos);
                RenderUtil.drawFilledBox(bb, ColorsModule.INSTANCE.getColor(), alpha.getValue());
                if (renderOutline.getValue())
                {
                    RenderUtil.drawBoundingBox(bb, 1.0F, ColorsModule.INSTANCE.getColor());
                }
            }
            pos.release();
            RenderUtil.disableGL3D();
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateEvent event) {
        if (check()) {
            return;
        }

        obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        if (obbySlot == -1) {
            ChatUtil.sendMessage(ChatFormatting.RED + "[AutoTrap] No obsidian.", -554);
            setEnabled(false);
            return;
        }

        EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;
        int lastSlot = mc.player.inventory.currentItem;

        doTrap();

        if (didSwitch) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
            if (hand != null)
                mc.player.setActiveHand(hand);
        }

        if (didPlace) {
            timer.reset();
        }
    }

    private void doTrap() {
        boolean needsHelpingBlock = BlockUtil.isPositionPlaceable(new BlockPos(target.getPositionVector().add(0, 2, 0))) == 2;
        boolean needsLegs = false;

        if (smartLegs.getValue()) {
            for (Vec3d offset : BlockUtil.getUnsafeBlocksFromVec3d(target.getPositionVector(), 1, false)) {
                if (offset == null)
                    continue;
                BlockPos pos = new BlockPos(target.getPositionVector().add(offset));
                if (BlockUtil.isPositionPlaceable(pos) != 3) {
                    needsLegs = true;
                    break;
                }
            }
        }

        List<Vec3d> placeTargets = BlockUtil.targets(target.getPositionVector(), antiScaffold.getValue(), antiStep.getValue(), smartLegs.getValue() ? needsLegs : legs.getValue(), platform.getValue(), antiDrop.getValue(), needsHelpingBlock);
        placeList(placeTargets);
    }

    private void placeList(List<Vec3d> list) {
        list.sort((vec3d, vec3d2) -> Double.compare(mc.player.getDistanceSq(vec3d2.x, vec3d2.y, vec3d2.z), mc.player.getDistanceSq(vec3d.x, vec3d.y, vec3d.z)));
        list.sort(Comparator.comparingDouble(vec3d -> vec3d.y));
        this.placeTargets = list;

        for (Vec3d vec3d : list) {
            BlockPos position = new BlockPos(vec3d);
            int placeability = BlockUtil.isPositionPlaceable(position);

            if (placeability == 1) {
                if (attackCrystals.getValue() && attackTimer.passed(attackDelay.getValue())) {
                    for (Entity entity : mc.world.getEntitiesWithinAABB(Entity.class, new AxisAlignedBB(position))) {
                        if (entity instanceof EntityEnderCrystal) {
                            mc.getConnection().sendPacket(new CPacketUseEntity(entity));
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            attackTimer.reset();
                            break;
                        }
                    }
                }

                if (entityCheck.getValue() && (retries.get(position) == null || retries.get(position) < retryer.getValue())) {
                    placeBlock(position);
                    retries.put(position, (retries.get(position) == null ? 1 : (retries.get(position) + 1)));
                    retryTimer.reset();
                    continue;
                }
            }

            if (placeability == 3) {
                placeBlock(position);
            }
        }
    }

    private boolean check() {
        isPlacing = false;
        didPlace = false;
        didSwitch = false;
        placements = 0;

        if (retryTimer.passed(2000)) {
            retries.clear();
            retryTimer.reset();
        }

        target = getClosestPlayer(targetRange.getValue(), true);

        return target == null || !timer.passed(delay.getValue());
    }

    public EntityPlayer getClosestPlayer(double range, boolean untrapped) {
        double maxDistance = 999.0D;
        EntityPlayer target = null;

        range *= range;

        for (EntityPlayer player : mc.world.playerEntities) {
            if (player != mc.player) {
                final double distance = player.getDistanceSq(mc.player);
                if (range > distance && player.getHealth() > 0 && !player.isDead && !FriendManager.getInstance().isFriend(player.getName()))
                {
                    if (untrapped && !BlockUtil.isTrapped(player, false, false, false, false, false, false))
                        continue;
                    if (distance < maxDistance)
                    {
                        maxDistance = distance;
                        target = player;
                    }
                }
            }
        }

        if (untrapped && target == null) {
            return getClosestPlayer(range, false);
        }

        return target;
    }

    private void placeBlock(BlockPos pos) {
        if (placements < blocksPerPlace.getValue() && mc.player.getDistanceSq(pos) <= (range.getValue() * range.getValue())) {
            if (!didSwitch) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));
                didSwitch = true;
            }
            isPlacing = true;
            BlockUtil.placeBlock(pos, rotation.getValue());
            didPlace = true;
            placements++;
        }
    }


}
