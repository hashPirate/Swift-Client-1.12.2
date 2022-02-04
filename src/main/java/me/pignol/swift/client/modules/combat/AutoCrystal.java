package me.pignol.swift.client.modules.combat;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.managers.SwitchManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemFood;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.network.play.server.SPacketDestroyEntities;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class AutoCrystal extends Module {

    private final Value<Values> setting = (new Value<>("Settings", Values.PLACE));

    //PLACE
    public Value<Boolean> place = (new Value<>("Place", true, v -> setting.getValue() == Values.PLACE));
    public Value<Integer> placeDelay = (new Value<>("PlaceDelay", 0, 0, 1000, v -> setting.getValue() == Values.PLACE && place.getValue()));
    public Value<Float> placeRange = (new Value<>("PlaceRange", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.PLACE && place.getValue()));
    public Value<Float> placeTrace = (new Value<>("PlaceTrace", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.PLACE && place.getValue()));

    //BREAK
    public Value<Boolean> explode = (new Value<>("Break", true, v -> setting.getValue() == Values.BREAK));
    public Value<Boolean> predict = (new Value<>("Predict", true, v -> setting.getValue() == Values.BREAK));
    public Value<Integer> breakDelay = (new Value<>("BreakDelay", 0, 0, 1000, v -> setting.getValue() == Values.BREAK && explode.getValue()));
    public Value<Float> breakRange = (new Value<>("BreakRange", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.BREAK && explode.getValue()));
    public Value<Float> breakTrace = (new Value<>("BreakTrace", 6.0f, 0.0f, 6.0f, v -> setting.getValue() == Values.BREAK && explode.getValue()));
    //public Value<Boolean> inhibit = new Value<>("Inhibit", false, v -> setting.getValue() == Values.BREAK && explode.getValue());

    //RENDER
    public Value<Boolean> render = (new Value<>("Render", true, v -> setting.getValue() == Values.RENDER));
    private final Value<Integer> alpha = (new Value<>("Alpha", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Value<Boolean> outline = (new Value<>("Outline", true, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Value<Boolean> singleRender = (new Value<>("SingleRender", true, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Value<Integer> renderTime = (new Value<>("RenderTimeMS", 500, 0, 2000, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Value<Boolean> rainbow = new Value<>("Rainbow", true, v -> setting.getValue() == Values.RENDER);
    private final Value<Float> lineWidth = (new Value<>("LineWidth", 1.5f, 0.1f, 5.0f, v -> setting.getValue() == Values.RENDER && render.getValue() && outline.getValue()));
    /*public Value<Boolean> fadeRender = (new Value<>("Fade", true, v -> setting.getValue() == Values.RENDER && render.getValue()));
    private final Value<Integer> red = (new Value<>("Red", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue()));
    private final Value<Integer> green = (new Value<>("Green", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue()));
    private final Value<Integer> blue = (new Value<>("Blue", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue()));
    public Value<Boolean> customOutline = (new Value<>("CustomLine", false, v -> setting.getValue() == Values.RENDER && render.getValue() && outline.getValue()));
    private final Value<Integer> cRed = (new Value<>("OL-Red", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue() && customOutline.getValue() && outline.getValue()));
    private final Value<Integer> cGreen = (new Value<>("OL-Green", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue() && customOutline.getValue() && outline.getValue()));
    private final Value<Integer> cBlue = (new Value<>("OL-Blue", 255, 0, 255, v -> setting.getValue() == Values.RENDER && render.getValue() && customOutline.getValue() && outline.getValue()));*/

    //MISC

    public Value<Float> minDamage = (new Value<>("MinDamage", 4.0f, 0.1f, 36.0f, 0.1F, v -> setting.getValue() == Values.MISC));
    public Value<Float> facePlace = (new Value<>("FacePlace", 8.0f, 0.1f, 36.0f, 0.1F, v -> setting.getValue() == Values.MISC));
    public Value<Float> maxSelf = (new Value<>("MaxSelf", 8.0f, 0.1f, 36.0f, v -> setting.getValue() == Values.MISC));
    public Value<Float> armorPercent = (new Value<>("Armor%", 10.0F, 0.0f, 100.0f, 1.0F, v -> setting.getValue() == Values.MISC));
    public Value<Float> range = (new Value<>("Range", 12.0f, 0.1f, 20.0f, 0.1F, v -> setting.getValue() == Values.MISC));
    private final Value<Integer> switchCooldown = (new Value<>("Cooldown", 500, 0, 1000, 25, v -> setting.getValue() == Values.MISC));
    public Value<Switch> switchValue = new Value<>("Switch", Switch.NONE, v -> setting.getValue() == Values.MISC);
    public Value<Boolean> second = (new Value<>("Second", false, v -> setting.getValue() == Values.MISC));
    public Value<Boolean> soundRemove = (new Value<>("SoundRemove", false, v -> setting.getValue() == Values.MISC));
    public Value<Boolean> multiTask = new Value<>("MultiTask", true, v -> setting.getValue() == Values.MISC);
    public Value<Boolean> traceSides = new Value<>("TraceSides", true, v -> setting.getValue() == Values.MISC);
    public Value<Boolean> sequential = new Value<>("Sequential", true, v -> setting.getValue() == Values.MISC);

    //DOUBLEPOP
    public Value<Boolean> antiTotem = new Value<>("AntiTotem", false, v -> setting.getValue() == Values.ANTITOTEM);
    public Value<Integer> popTime = new Value<>("Time", 500, 0, 2000, 25, v -> setting.getValue() == Values.ANTITOTEM);
    public Value<Float> popDamage = new Value<>("Damage", 5.2F, 0.0F, 20.0F, v -> setting.getValue() == Values.ANTITOTEM);

    //ROTATE
    public Value<Boolean> rotate = new Value<>("Rotate", true, v -> setting.getValue() == Values.ROTATE);
    public Value<Boolean> offset = new Value<>("Offset", false, v -> setting.getValue() == Values.ROTATE && rotate.getValue());

    private final Object2LongOpenHashMap<BlockPos> renderMap = new Object2LongOpenHashMap<>();
    private final Object2LongOpenHashMap<EntityPlayer> popMap = new Object2LongOpenHashMap<>();

    private final ObjectSet<BlockPos> placedPos = new ObjectOpenHashSet<>();
    private final StopWatch breakTimer = new StopWatch();
    private final StopWatch placeTimer = new StopWatch();

    private EntityPlayer currentTarget;
    private BlockPos lastPos;
    private boolean mainHand;
    private boolean offHand;
    private boolean didOffset;

    public AutoCrystal() {
        super("AutoCrystal", Category.COMBAT);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject && explode.getValue() && predict.getValue()) {
            final SPacketSpawnObject packet = (SPacketSpawnObject) event.getPacket();
            if (packet.getType() == 51) {
                final BlockPos pos = new BlockPos(packet.getX(), packet.getY() - 1, packet.getZ());
                if (placedPos.remove(pos)) { //returns true if its in the list but removes it at the same time
                    CPacketUseEntity packetUseEntity = new CPacketUseEntity();
                    packetUseEntity.entityId = packet.getEntityID();
                    packetUseEntity.action = CPacketUseEntity.Action.ATTACK;
                    mc.getConnection().sendPacket(packetUseEntity);
                    mc.getConnection().sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));
                    if (sequential.getValue() && lastPos != null && lastPos.equals(pos)) {
                        mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(lastPos, EnumFacing.UP, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 1.0F, 0.5F));
                    }
                    breakTimer.reset();
                }
            }
        }

        if (event.getPacket() instanceof SPacketSoundEffect && soundRemove.getValue()) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getCategory() == SoundCategory.BLOCKS && packet.getSound() == SoundEvents.ENTITY_GENERIC_EXPLODE) {
                mc.addScheduledTask(() -> {
                    for (Entity entity : mc.world.loadedEntityList) {
                        if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(packet.getX(), packet.getY(), packet.getZ()) < 36) {
                            entity.setDead();
                        }
                    }
                });
            }
        }

        if (event.getPacket() instanceof SPacketEntityStatus && antiTotem.getValue()) {
            SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35 && packet.getEntity(mc.world) instanceof EntityPlayer) {
                popMap.put((EntityPlayer) packet.getEntity(mc.world), System.currentTimeMillis());
            }
        }

        if (event.getPacket() instanceof SPacketDestroyEntities) {
            final SPacketDestroyEntities packet = (SPacketDestroyEntities) event.getPacket();
            for (int id : packet.getEntityIDs()) {
                Entity entity = mc.world.getEntityByID(id);
                if (entity instanceof EntityEnderCrystal) {
                    placedPos.remove(new BlockPos(entity.posX, entity.posY - 1, entity.posZ));
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (render.getValue() && (offHand || mainHand || switchValue.getValue() == Switch.SILENT) && !renderMap.isEmpty()) {
            BlockPos removable = null;

            int color = rainbow.getValue() ? ColorsModule.INSTANCE.getRainbow() : ColorsModule.INSTANCE.getColor();
            RenderUtil.enableGL3D();
            for (Object2LongMap.Entry<BlockPos> entry : renderMap.object2LongEntrySet()) {
                BlockPos pos = entry.getKey();
                long millis = entry.getLongValue();
                long dura = System.currentTimeMillis() - millis;
                if (dura > renderTime.getValue() || pos == null) {
                    removable = pos;
                    continue;
                }

                int alpha = this.alpha.getValue();
                AxisAlignedBB bb = RenderUtil.getRenderBB(pos);
                RenderUtil.drawFilledBox(bb, color, alpha);
                if (outline.getValue()) {
                    RenderUtil.drawBoundingBox(bb, lineWidth.getValue(), color);
                }
            }

            if (removable != null) {
                renderMap.removeLong(removable);
            }

            RenderUtil.disableGL3D();
        }
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if (!rotate.getValue()) {
            doAutoCrystal();
        }
    }

    @SubscribeEvent(priority = EventPriority.HIGHEST)
    public void onUpdate(UpdateEvent event) {
        if (rotate.getValue() && event.getStage() == Stage.PRE) {
            doAutoCrystal();
        }
    }

    public void doAutoCrystal() {
        lastPos = null;
        if (isNull() || switchCooldown.getValue() > 0 && SwitchManager.getInstance().getMsPassed() < switchCooldown.getValue()) {
            return;
        }

        if (!multiTask.getValue()) {
            if (mc.player.getHeldItemMainhand().getItem() instanceof ItemFood && mc.player.isHandActive() && mc.player.getActiveHand() == EnumHand.MAIN_HAND) {
                return;
            }
        }

        offHand = mc.player.getHeldItemOffhand().getItem() == Items.END_CRYSTAL;
        mainHand = mc.player.getHeldItemMainhand().getItem() == Items.END_CRYSTAL;
        currentTarget = null;
        if (!placedPos.isEmpty() && placeTimer.passed(2500))
            placedPos.clear();

        doBreak();
        doPlace();
    }

    public void doBreak() {
        if (explode.getValue() && breakTimer.passed(breakDelay.getValue()) && (offHand || mainHand || switchValue.getValue() != Switch.NONE)) {
            Entity crystal = calculateBreak(currentTarget);
            if (crystal != null) {
                if (rotate.getValue()) {
                    float[] rotations = RotationUtil.getRotations(crystal.posX, crystal.posY, crystal.posZ);
                    float offset = this.offset.getValue() ? (didOffset ? 0.004F : -0.004F) : 0.0F;
                    RotationManager.getInstance().setPlayerRotations(rotations[0] + offset, rotations[1] + offset);
                    didOffset = !didOffset;
                }
                mc.playerController.attackEntity(mc.player, crystal);
                mc.player.swingArm(EnumHand.MAIN_HAND);
                breakTimer.reset();
            }
        }
    }

    public void doPlace() {
        if (place.getValue() && placeTimer.passed(placeDelay.getValue()) && (offHand || mainHand || switchValue.getValue() != Switch.NONE)) {
            BlockPos pos = calculatePlace();
            if (pos != null) {
                placedPos.add(pos);
                if (rotate.getValue()) {
                    float[] rotations = RotationUtil.getRotations(pos.getX() + 0.5F, pos.getY() + 1.0F, pos.getZ() + 0.5F);
                    float offset = this.offset.getValue() ? (didOffset ? 0.004F : -0.004F) : 0.0F;
                    RotationManager.getInstance().setPlayerRotations(rotations[0] + offset, rotations[1] + offset);
                    didOffset = !didOffset;
                }

                int lastSlot = -1;
                if (!offHand && !mainHand) {
                    int crystalSlot = ItemUtil.getSlotHotbar(Items.END_CRYSTAL);
                    if (crystalSlot == -1) return;
                    if (switchValue.getValue() == Switch.NORMAL) {
                        ItemUtil.switchToSlot(crystalSlot, false);
                        if (switchCooldown.getValue() > 0) {
                            return;
                        }
                    } else {
                        lastSlot = mc.player.inventory.currentItem;
                        SwitchManager.getInstance().setDontReset(true);
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(crystalSlot));
                    }
                }
                lastPos = pos;
                mc.getConnection().sendPacket(new CPacketPlayerTryUseItemOnBlock(pos, EnumFacing.UP, offHand ? EnumHand.OFF_HAND : EnumHand.MAIN_HAND, 0.5F, 1.0F, 0.5F));
                if (lastSlot != -1) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                    SwitchManager.getInstance().setDontReset(false);
                }
                placeTimer.reset();

                if (singleRender.getValue()) {
                    renderMap.clear();
                }
                renderMap.put(pos, System.currentTimeMillis());
            }
        }
    }

    public BlockPos calculatePlace() {
        BlockPos blockPos = null;
        float maxDamage = 1.0F;

        float radius = placeRange.getValue();
        EntityPlayer target = null;
        BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        for (float x = radius; x >= -radius; x--) {
            for (float z = radius; z >= -radius; z--) {
                for (float y = radius; y >= -radius; y--) {
                    pos.setPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                    final double distance = mc.player.getDistanceSq(pos);
                    if (distance > radius * radius)
                        continue;

                    if (BlockUtil.canPlaceCrystal(pos, second.getValue(), false)) {
                        if (distance > MathUtil.square(placeTrace.getValue()) && !RaytraceUtil.canBlockBeSeen(mc.player, pos, traceSides.getValue()))
                            continue;

                        final float selfDamage = DamageUtil.calculate(pos, mc.player);
                        if (selfDamage + 0.5F < EntityUtil.getHealth(mc.player) && maxSelf.getValue() > selfDamage) {
                            for (EntityPlayer player : mc.world.playerEntities) {
                                if (EntityUtil.isPlayerValid(player, range.getValue())) {
                                    float damage = DamageUtil.calculate(pos, player);
                                    if (isDoublePoppable(player, damage) || damage >= maxDamage && (damage >= minDamage.getValue() || EntityUtil.getHealth(player) <= facePlace.getValue() || ItemUtil.isArmorUnderPercent(player, armorPercent.getValue())) && (damage > selfDamage || damage > EntityUtil.getHealth(player) + 1.0F)) {
                                        maxDamage = damage;
                                        blockPos = pos.toImmutable();
                                        target = player;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        pos.release();
        currentTarget = target;
        return blockPos;
    }

    public Entity calculateBreak(EntityPlayer player) {
        Entity crystal = null;
        float maxDamage = 0.1F;
        final boolean lowArmor = armorPercent.getValue() > 0.0F && player != null && ItemUtil.isArmorUnderPercent(player, armorPercent.getValue());
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal && !entity.isDead) {
                double distance = mc.player.getDistanceSq(entity);
                if (distance > MathUtil.square(breakRange.getValue())) {
                    continue;
                }

                if (distance > MathUtil.square(breakTrace.getValue()) && !mc.player.canEntityBeSeen(entity)){
                    continue;
                }

                final float selfDamage = DamageUtil.calculate(entity, mc.player);
                if (selfDamage + 0.5F < EntityUtil.getHealth(mc.player) && maxSelf.getValue() > selfDamage) {
                    if (player != null) {
                        final float damage = DamageUtil.calculate(entity, player);
                        if (isDoublePoppable(player, damage) || damage > maxDamage && (damage >= minDamage.getValue() || EntityUtil.getHealth(player) <= facePlace.getValue() || lowArmor) || damage > EntityUtil.getHealth(player) + 1.0F) {
                            crystal = entity;
                            maxDamage = damage;
                        }
                    } else {
                        for (EntityPlayer player1 : mc.world.playerEntities) {
                            if (EntityUtil.isPlayerValid(player1, range.getValue())) {
                                final float damage = DamageUtil.calculate(entity, player1);
                                if (isDoublePoppable(player1, damage) || damage > maxDamage && (damage >= minDamage.getValue() || EntityUtil.getHealth(player1) <= facePlace.getValue() || ItemUtil.isArmorUnderPercent(player1, armorPercent.getValue())) || damage > EntityUtil.getHealth(player1) + 1.0F) {
                                    crystal = entity;
                                    maxDamage = damage;
                                }
                            }
                        }
                    }
                }
            }
        }

        return crystal;
    }

    private boolean isDoublePoppable(EntityPlayer player, float damage) {
        if (antiTotem.getValue()) {
            float health = EntityUtil.getHealth(player);
            if (health <= 5.0F && damage > health + 0.5 && damage <= this.popDamage.getValue()) {
                long ms = popMap.getLong(player);
                return System.currentTimeMillis() - ms >= popTime.getValue();
            }
        }
        return false;
    }

    public enum Switch {
        NORMAL,
        SILENT,
        NONE
    }

    public enum Values {
        PLACE,
        BREAK,
        RENDER,
        ROTATE,
        MISC,
        ANTITOTEM
    }

}