package me.pignol.swift.client.modules.combat;

import it.unimi.dsi.fastutil.objects.ObjectOpenHashSet;
import it.unimi.dsi.fastutil.objects.ObjectSet;
import me.pignol.swift.api.util.*;
import me.pignol.swift.api.util.RotationUtil.RotationType;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemShulkerBox;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class AntiRegear extends Module {

    public final Value<RotationType> rotation = new Value<>("Rotation", RotationType.NONE);
    public final Value<Float> range = new Value<>("Range", 5.0F, 1.0F, 6.0F);
    public final Value<Float> enemyRange = new Value<>("EnemyRange", 5.0F, 1.0F, 6.0F);
    public final Value<Integer> delay = new Value<>("Delay", 150, 0, 3000);
    public final Value<Boolean> raytrace = new Value<>("Raytrace", false);
    public final Value<Boolean> antiSelf = new Value<>("AntiSelf", false);
    public final Value<Integer> antiSelfTime = new Value<>("AntiSelfTime", 10000, 0, 25000);

    private final ObjectSet<BlockPos> selfPlaced = new ObjectOpenHashSet<>();
    private final StopWatch antiSelfTimer = new StopWatch();
    private final StopWatch delayTimer = new StopWatch();

    public AntiRegear() {
        super("AntiRegear", Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.getHeldItemMainhand().getItem() instanceof ItemPickaxe && delayTimer.passed(delay.getValue())) {
            BlockPos shulkerPos = null;

            EntityPlayer target = EntityUtil.getClosestPlayer(mc.world.playerEntities);
            if (target == null || mc.player.getDistanceSq(target) > MathUtil.square(enemyRange.getValue())) {
                return;
            }

            if (antiSelfTimer.passed(antiSelfTime.getValue())) {
                selfPlaced.clear();
                antiSelfTimer.reset();
            }

            final List<TileEntity> loadedTileEntityList = mc.world.loadedTileEntityList;
            for (int i = 0, loadedTileEntityListSize = loadedTileEntityList.size(); i < loadedTileEntityListSize; i++) {
                final TileEntity tileEntity = loadedTileEntityList.get(i);
                if (tileEntity instanceof TileEntityShulkerBox) {
                    BlockPos pos = tileEntity.getPos();
                    if (mc.player.getDistanceSq(pos) < MathUtil.square(range.getValue())) {
                        if (antiSelf.getValue() && selfPlaced.contains(pos) || raytrace.getValue() && !RaytraceUtil.canBlockBeSeen(mc.player, pos, true)) {
                            continue;
                        }
                        shulkerPos = pos;
                        break;
                    }
                }
            }

            if (shulkerPos != null) {
                float[] rotations = RotationUtil.getRotations(shulkerPos);
                rotation.getValue().doRotation(rotations);

                RayTraceResult result = RaytraceUtil.getRayTraceResult(rotations[0], rotations[1], range.getValue());
                mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, shulkerPos, result.sideHit));
                mc.getConnection().sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, shulkerPos, result.sideHit));
                mc.player.swingArm(EnumHand.MAIN_HAND);
                delayTimer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && antiSelf.getValue()) {
            final CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (mc.player.getHeldItem(packet.getHand()).getItem() instanceof ItemShulkerBox) {
                selfPlaced.add(
                        packet.getPos().offset(packet.getDirection())
                );
                antiSelfTimer.reset();
            }
        }
    }

}
