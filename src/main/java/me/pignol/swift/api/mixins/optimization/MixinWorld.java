package me.pignol.swift.api.mixins.optimization;

import me.pignol.swift.client.modules.render.FullBrightModule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.*;
import net.minecraft.world.World;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;

@Mixin(World.class)
public class MixinWorld {

    private static final RayTraceResult RESULT = new RayTraceResult(Vec3d.ZERO, EnumFacing.DOWN);

    protected RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox) {
        Vec3d vec3d = start.subtract(pos.getX(), pos.getY(), pos.getZ());
        Vec3d vec3d1 = end.subtract(pos.getX(), pos.getY(), pos.getZ());
        RayTraceResult raytraceresult = boundingBox.calculateIntercept(vec3d, vec3d1);
        return raytraceresult == null ? null : copy(raytraceresult.hitVec.add((double) pos.getX(), (double) pos.getY(), (double) pos.getZ()), raytraceresult.sideHit, pos);
    }

    public RayTraceResult copy(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
        RESULT.hitInfo = hitVecIn;
        RESULT.sideHit = sideHitIn;
        RESULT.blockPos = blockPosIn;
        return RESULT;
    }


    @Inject(method = "checkLightFor", at = @At("HEAD"), cancellable = true)
    private void fullbright(CallbackInfoReturnable<Boolean> cir) {
        if (FullBrightModule.INSTANCE.isEnabled()) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = {"getLightFromNeighborsFor", "getLightFromNeighbors", "getRawLight", "getLight(Lnet/minecraft/util/math/BlockPos;Z)I", "getLight(Lnet/minecraft/util/math/BlockPos;Z)I"}, at = @At("HEAD"), cancellable = true)
    private void fullbright2(CallbackInfoReturnable<Integer> cir) {
        if (FullBrightModule.INSTANCE.isEnabled()) {
            cir.setReturnValue(15);
        }
    }

    public RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end) {
        return rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos));
    }

    /**
     * @author hollow
     * @reason dont create 200 blockpos objects for no reason
     */
    @Overwrite
    @Nullable
    public RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, boolean stopOnLiquid, boolean ignoreBlockWithoutBoundingBox, boolean returnLastUncollidableBlock) {
        if (!Double.isNaN(vec31.x) && !Double.isNaN(vec31.y) && !Double.isNaN(vec31.z)) {
            if (!Double.isNaN(vec32.x) && !Double.isNaN(vec32.y) && !Double.isNaN(vec32.z)) {
                final World world = Minecraft.getMinecraft().world;
                if (world == null) {
                    return null;
                }

                final int endX = MathHelper.floor(vec32.x);
                final int endY = MathHelper.floor(vec32.y);
                final int endZ = MathHelper.floor(vec32.z);
                int x = MathHelper.floor(vec31.x);
                int y = MathHelper.floor(vec31.y);
                int z = MathHelper.floor(vec31.z);
                final BlockPos.MutableBlockPos blockpos = new BlockPos.MutableBlockPos(x, y, z);
                final IBlockState iblockstate = world.getBlockState(blockpos);
                final Block block = iblockstate.getBlock();

                if ((!ignoreBlockWithoutBoundingBox || iblockstate.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) && block.canCollideCheck(iblockstate, stopOnLiquid)) {
                    final RayTraceResult raytraceresult = collisionRayTrace(iblockstate, world, blockpos, vec31, vec32);

                    if (raytraceresult != null) {
                        return raytraceresult;
                    }
                }

                RayTraceResult raytraceresult2 = null;
                int k1 = 200;

                while (k1-- >= 0) {
                    if (x == endX && y == endY && z == endZ) {
                        return returnLastUncollidableBlock ? raytraceresult2 : null;
                    }

                    boolean flag2 = true;
                    boolean flag = true;
                    boolean flag1 = true;
                    double d0 = 999.0D;
                    double d1 = 999.0D;
                    double d2 = 999.0D;

                    if (endX > x) {
                        d0 = (double) x + 1.0D;
                    } else if (endX < x) {
                        d0 = (double) x + 0.0D;
                    } else {
                        flag2 = false;
                    }

                    if (endY > y) {
                        d1 = (double) y + 1.0D;
                    } else if (endY < y) {
                        d1 = (double) y + 0.0D;
                    } else {
                        flag = false;
                    }

                    if (endZ > z) {
                        d2 = (double) z + 1.0D;
                    } else if (endZ < z) {
                        d2 = (double) z + 0.0D;
                    } else {
                        flag1 = false;
                    }

                    double d3 = 999.0D;
                    double d4 = 999.0D;
                    double d5 = 999.0D;
                    double d6 = vec32.x - vec31.x;
                    double d7 = vec32.y - vec31.y;
                    double d8 = vec32.z - vec31.z;

                    if (flag2) {
                        d3 = (d0 - vec31.x) / d6;
                    }

                    if (flag) {
                        d4 = (d1 - vec31.y) / d7;
                    }

                    if (flag1) {
                        d5 = (d2 - vec31.z) / d8;
                    }

                    if (d3 == -0.0D) {
                        d3 = -1.0E-4D;
                    }

                    if (d4 == -0.0D) {
                        d4 = -1.0E-4D;
                    }

                    if (d5 == -0.0D) {
                        d5 = -1.0E-4D;
                    }

                    EnumFacing enumfacing;

                    if (d3 < d4 && d3 < d5) {
                        enumfacing = endX > x ? EnumFacing.WEST : EnumFacing.EAST;
                        vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
                    } else if (d4 < d5) {
                        enumfacing = endY > y ? EnumFacing.DOWN : EnumFacing.UP;
                        vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
                    } else {
                        enumfacing = endZ > z ? EnumFacing.NORTH : EnumFacing.SOUTH;
                        vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
                    }

                    x = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
                    y = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
                    z = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
                    blockpos.setPos(x, y, z);
                    final IBlockState iblockstate1 = world.getBlockState(blockpos);
                    final Block block1 = iblockstate1.getBlock();

                    if (!ignoreBlockWithoutBoundingBox || iblockstate1.getMaterial() == Material.PORTAL || iblockstate1.getCollisionBoundingBox(world, blockpos) != Block.NULL_AABB) {
                        if (block1.canCollideCheck(iblockstate1, stopOnLiquid)) {
                            final RayTraceResult raytraceresult1 = collisionRayTrace(iblockstate1, world, blockpos, vec31, vec32);

                            if (raytraceresult1 != null) {
                                return raytraceresult1;
                            }
                        } else if (returnLastUncollidableBlock) {
                            raytraceresult2 = new RayTraceResult(RayTraceResult.Type.MISS, vec31, enumfacing, blockpos);
                        }
                    }
                }

                return returnLastUncollidableBlock ? raytraceresult2 : null;
            } else {
                return null;
            }
        } else {
            return null;
        }
    }

}
