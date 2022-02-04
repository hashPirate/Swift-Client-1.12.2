package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.util.Colors;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.DamageBlockEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.managers.ServerManager;
import me.pignol.swift.client.managers.SwitchManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.network.play.server.SPacketBlockChange;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import javax.annotation.Nonnull;

import static net.minecraftforge.common.ForgeHooks.canHarvestBlock;

public class SpeedMine extends Module {

    private final Value<Boolean> rotate = new Value<>("Rotate", true);
    private final Value<Boolean> doubleStart = new Value<>("DoubleStart", false);
    private final Value<Boolean> instant = new Value<>("Instant", true);
    private final Value<Boolean> limit = new Value<>("Limit", false);
    private final Value<Boolean> render = new Value<>("Render", false);
    private final Value<Boolean> tpsSync = new Value<>("TpsSync", false);
    private final Value<Switch> switchValue = new Value<>("Switch", Switch.NORMAL);
    private final Value<Boolean> swing = new Value<>("Swing", switchValue.getValue() == Switch.SILENT);
    private final Value<Float> delay = new Value<>("Delay", 1.0F, 0F, 5.0F);

    public SpeedMine() {
        super("SpeedMine", Category.PLAYER);
    }

    private StopWatch replaceTimer = new StopWatch();
    private boolean isReplacing = false;
    private boolean isMining = false;
    private BlockPos lastPos = null;
    private EnumFacing lastFacing = null;
    private StopWatch rotateTimer = new StopWatch();
    private StopWatch tpsTimer = new StopWatch();
    private float damage;

    public void onEnable() {
        isReplacing = false;
        isMining = false;
        lastPos = null;
        lastFacing = null;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (lastPos != null && render.getValue()) {
            AxisAlignedBB bb = RenderUtil.getRenderBB(lastPos);
            RenderUtil.enableGL3D();
            RenderUtil.drawFilledBox(bb, Colors.GRAY, 50);
            RenderUtil.drawBoundingBox(bb, 1.0F, Colors.GRAY);
            RenderUtil.disableGL3D();
        }
    }

    private void updateProgress() {
        if (lastPos == null) {
            return;
        }

        IBlockState state = mc.world.getBlockState(lastPos);
        damage += blockStrength(state, mc.player, mc.world, lastPos);
    }

    public float blockStrength(@Nonnull IBlockState state, @Nonnull EntityPlayer player, @Nonnull World world, @Nonnull BlockPos pos)
    {
        float hardness = state.getBlockHardness(world, pos);
        if (hardness < 0.0F)
        {
            return 0.0F;
        }

        if (!canHarvestBlock(state.getBlock(), player, world, pos))
        {
            return player.getDigSpeed(state, pos) / hardness / 100F;
        }
        else
        {
            return player.getDigSpeed(state, pos) / hardness / 30F;
        }
    }

    @SubscribeEvent
    public void onDamageBlock(DamageBlockEvent event) {
        if (mc.player == null || mc.world == null) return;

        if (canBreak(event.getPos())) {
            rotateTimer.reset();
            int lastSlot = -1;
            if (switchValue.getValue() != Switch.NONE) {
                int slot = ItemUtil.getBestToolSlot(mc.world.getBlockState(event.getPos()).getBlock());
                if (slot != -1 && mc.player.inventory.currentItem != slot) {
                    if (switchValue.getValue() == Switch.SILENT) {
                        lastSlot = mc.player.inventory.currentItem;
                        SwitchManager.getInstance().setDontReset(true);
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
                    } else {
                        mc.player.inventory.currentItem = slot;
                        mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
                    }
                }
            }
            if (event.getPos().equals(lastPos)) {
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
            } else {
                if (doubleStart.getValue()) {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing().getOpposite()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                } else {
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, event.getPos(), event.getFacing()));
                }
            }
            damage = 0.0F;
            mc.player.swingArm(EnumHand.MAIN_HAND);
            if (lastSlot != -1) {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                SwitchManager.getInstance().setDontReset(false);
            }
            lastPos = event.getPos();
            lastFacing = event.getFacing();
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateEvent event) {
        if (event.getStage() != Stage.PRE)
            return;

        updateProgress();
        if (lastPos != null && mc.world.getBlockState(lastPos).getBlock() == Blocks.AIR) {
            lastPos = null;
            damage = 0.0F;
        }
        if (!tpsSync.getValue() || tpsTimer.passed((long) (1000 / ServerManager.getInstance().getTPS()))) {
            updateProgress();
        }

        if (lastPos != null && lastFacing != null && switchValue.getValue() == Switch.SILENT) {
            int slot = ItemUtil.getBestToolSlot(mc.world.getBlockState(lastPos).getBlock());
            if (slot != -1 && mc.player.inventory.currentItem != slot) {
                int lastSlot = -1;
                if (switchValue.getValue() != Switch.NONE) {
                    if (mc.player.inventory.currentItem != slot) {
                        if (switchValue.getValue() == Switch.SILENT) {
                            lastSlot = mc.player.inventory.currentItem;
                            SwitchManager.getInstance().setDontReset(true);
                            mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
                        } else {
                            mc.player.inventory.currentItem = slot;
                            mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
                        }
                    }
                }
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, lastPos, lastFacing));
                mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastPos, lastFacing));
                if (swing.getValue()) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                }
                if (lastSlot != -1) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                    SwitchManager.getInstance().setDontReset(false);
                }
            }
        }

        if (instant.getValue()) {
            if (lastPos != null && lastFacing != null) {
                if (isReplacing && replaceTimer.passed((long) (100 * delay.getValue()))) {
                    mc.player.swingArm(EnumHand.MAIN_HAND);
                    mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.STOP_DESTROY_BLOCK, lastPos, lastFacing));
                    if (limit.getValue()) {
                        mc.player.connection.sendPacket(new CPacketPlayerDigging(CPacketPlayerDigging.Action.START_DESTROY_BLOCK, lastPos, lastFacing.getOpposite()));
                    }
                    isReplacing = false;
                }
            }

            if (lastPos != null && lastFacing != null && !rotateTimer.passed(3500) && rotate.getValue()) {
                Vec3d vec3d = new Vec3d(lastPos.getX() + 0.5 + lastFacing.getDirectionVec().getX() * 0.5, lastPos.getY() + 0.5 + lastFacing.getDirectionVec().getY() * 0.5, lastPos.getZ() + 0.5 + lastFacing.getDirectionVec().getZ() * 0.5);
                float[] angle = MathUtil.calculateAngle(mc.player.getPositionEyes(mc.getRenderPartialTicks()), new Vec3d(vec3d.x, vec3d.y, vec3d.z));
                RotationManager.getInstance().setPlayerRotations(angle[0], angle[1]);
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (instant.getValue() && event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock) {
            if (((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getPos().offset(((CPacketPlayerTryUseItemOnBlock) event.getPacket()).getDirection()).equals(lastPos)) {
                isReplacing = true;
                replaceTimer.reset();
            }
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (instant.getValue() && event.getPacket() instanceof SPacketBlockChange) {
            SPacketBlockChange packet = (SPacketBlockChange) event.getPacket();
            if (packet.getBlockPosition().equals(lastPos) && packet.getBlockState().getBlock() != Blocks.AIR && mc.player.getDistance(packet.getBlockPosition().getX() + 0.5, packet.getBlockPosition().getY() + 0.5, packet.getBlockPosition().getZ() + 0.5) < 6) {
                if (!(packet.getBlockState().getBlock().equals(mc.world.getBlockState(packet.getBlockPosition()).getBlock()))) {
                    isReplacing = true;
                    replaceTimer.reset();
                }
            }
        }
    }

    private double getDestroySpeed(Block block, ItemStack stack){
        float speedMultiplier = stack.getDestroySpeed(block.getDefaultState());
        float damage;

        if (stack.canHarvestBlock(block.getDefaultState())) {
            damage = speedMultiplier / block.blockHardness / 30;
        } else {
            damage = speedMultiplier / block.blockHardness / 100;
        }

        return (float) Math.ceil(1 / damage);
    }

    private boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        return blockState.getBlockHardness(mc.world, pos) != -1;
    }

    public enum Switch {
        SILENT,
        NORMAL,
        NONE
    }

}
