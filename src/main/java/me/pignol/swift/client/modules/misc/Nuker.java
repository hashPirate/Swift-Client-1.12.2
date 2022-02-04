package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.EnumHelper;
import me.pignol.swift.api.util.FacingUtil;
import me.pignol.swift.api.util.RotationUtil;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockLiquid;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import static me.pignol.swift.api.util.BlockUtil.canBreak;

public class Nuker extends Module {

    public final Value<Mode> mode = new Value<Mode>("Mode", Mode.SELECTION);

    private enum Mode {
        SELECTION, ALL, CREATIVE
    }

    public final Value<Float> distance = new Value<Float>("Distance", 4.5f, 0.0f, 5.0f, 0.1f);
    public final Value<Boolean> fixed = new Value<Boolean>("FixedDistance", false);
    public final Value<Boolean> flat = new Value<Boolean>("Flat", false);
    public final Value<Boolean> rotate = new Value<Boolean>("Rotate", false);
    public final Value<Float> vDistance = new Value<Float>("VerticalDistance", 4.5f, 0.0f, 5.0f, 0.1f);
    public final Value<Float> hDistance = new Value<Float>("HorizontalDistance", 3f, 0.0f, 5.0f, 0.1f);

    private Block selected = null;
    private BlockPos currentPos = null;

    public Nuker() {
        super("Nuker", Category.MISC);
    }

    @Override
    public void onEnable() {
        this.selected = null;
    }

    @SubscribeEvent
    public void onWalkingUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null)
            return;

        setSuffix(EnumHelper.getCapitalizedName(mode.getValue()));
        if (event.getStage() == Stage.PRE) {
            this.currentPos = null;

            this.currentPos = this.getClosestBlock(mode.getValue() == Mode.SELECTION);

            if (this.mode.getValue().equals(Mode.CREATIVE)) {
                for (double y = Math.round(mc.player.posY - 1) + this.vDistance.getValue(); y > Math.round(mc.player.posY - 1); y -= 1.0D) {
                    for (double x = mc.player.posX - this.hDistance.getValue(); x < mc.player.posX + this.hDistance.getValue(); x += 1.0D) {
                        for (double z = mc.player.posZ - this.hDistance.getValue(); z < mc.player.posZ + this.hDistance.getValue(); z += 1.0D) {
                            final BlockPos blockPos = new BlockPos(x, y, z);
                            final Block block = mc.world.getBlockState(blockPos).getBlock();
                            if (block == Blocks.AIR || !mc.world.getBlockState(blockPos).isFullBlock())
                                continue;

                            final Vec3d eyesPos = new Vec3d(mc.player.posX, mc.player.posY + mc.player.getEyeHeight(), mc.player.posZ);
                            final Vec3d posVec = new Vec3d(blockPos).add(0.5f, 0.5f, 0.5f);
                            double distanceSqPosVec = eyesPos.squareDistanceTo(posVec);

                            for (EnumFacing side : FacingUtil.VALUES) {
                                final Vec3d hitVec = posVec.add(new Vec3d(side.getDirectionVec()).scale(0.5f));
                                double distanceSqHitVec = eyesPos.squareDistanceTo(hitVec);

                                // check if hitVec is within range (6 blocks)
                                if (distanceSqHitVec > 36)
                                    continue;

                                // check if side is facing towards player
                                if (distanceSqHitVec >= distanceSqPosVec)
                                    continue;

                                if (flat.getValue() && blockPos.getY() < mc.player.posY)
                                    continue;

                                // face block
                                if (rotate.getValue()) {
                                    final float[] rotations = RotationUtil.getRotations(hitVec.x, hitVec.y, hitVec.z);
                                    RotationManager.getInstance().setPlayerRotations(rotations[0], rotations[1]);
                                }

                                // damage block
                                if (mc.playerController.onPlayerDamageBlock(blockPos, side)) {
                                    mc.player.swingArm(EnumHand.MAIN_HAND);
                                }
                            }
                        }
                    }
                }
            } else {
                if (this.currentPos != null) {
                    if (canBreak(this.currentPos)) {
                        if (rotate.getValue()) {
                            final float[] rotations = RotationUtil.getRotations(currentPos);
                            RotationManager.getInstance().setPlayerRotations(rotations[0], rotations[1]);
                        }
                        mc.playerController.onPlayerDamageBlock(this.currentPos, mc.player.getHorizontalFacing());
                        mc.player.swingArm(EnumHand.MAIN_HAND);
                    }
                }
            }
        }
    }


    @SubscribeEvent
    public void clickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (this.mode.getValue() == Mode.SELECTION) {
            final Block block = Minecraft.getMinecraft().world.getBlockState(event.getPos()).getBlock();
            if (block != this.selected) {
                this.selected = block;
                ChatUtil.sendMessage("[Nuker] Selected block set to " + block.getLocalizedName());
                event.setCanceled(true);
            }
        }
    }

    private BlockPos getClosestBlock(boolean selection) {
        final Minecraft mc = Minecraft.getMinecraft();

        BlockPos ret = null;

        if (this.fixed.getValue()) {
            float maxVDist = this.vDistance.getValue();
            float maxHDist = this.hDistance.getValue();
            for (float x = 0; x <= maxHDist; x++) {
                for (float y = 0; y <= maxVDist; y++) {
                    for (float z = 0; z <= maxHDist; z++) {
                        for (int revX = 0; revX <= 1; revX++, x = -x) {
                            for (int revZ = 0; revZ <= 1; revZ++, z = -z) {
                                final BlockPos pos = new BlockPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                                if ((mc.world.getBlockState(pos).getBlock() != Blocks.AIR && !(mc.world.getBlockState(pos).getBlock() instanceof BlockLiquid)) && canBreak(pos)) {
                                    if (selection) {
                                        if ((this.selected == null) || !mc.world.getBlockState(pos).getBlock().equals(this.selected)) {
                                            continue;
                                        }
                                    }

                                    ret = pos;
                                }
                            }
                        }
                    }
                }
            }
        } else {
            float maxDist = this.distance.getValue();
            final BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
            for (float x = maxDist; x >= -maxDist; x--) {
                for (float y = maxDist; y >= -maxDist; y--) {
                    for (float z = maxDist; z >= -maxDist; z--) {
                        pos.setPos(mc.player.posX + x, mc.player.posY + y, mc.player.posZ + z);
                        final double dist = mc.player.getDistance(pos.getX(), pos.getY(), pos.getZ());
                        if (dist <= maxDist) {
                            final IBlockState state = mc.world.getBlockState(pos);
                            if ((state.getBlock() != Blocks.AIR && !(state.getBlock() instanceof BlockLiquid)) && canBreak(pos)) {
                                if (selection) {
                                    if ((this.selected == null) || !state.getBlock().equals(this.selected)) {
                                        continue;
                                    }
                                }

                                if (flat.getValue() && pos.getY() < mc.player.posY)
                                    continue;

                                maxDist = (float) dist;
                                ret = pos.toImmutable();
                            }
                        }
                    }
                }
            }
            pos.release();
        }
        return ret;
    }


}
