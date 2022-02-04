package me.pignol.swift.api.util;

import me.pignol.swift.api.interfaces.Globals;
import net.minecraft.entity.Entity;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

import java.util.Optional;

public class RaytraceUtil implements Globals {

    public static boolean canBlockBeSeen(Entity entity, BlockPos pos, boolean proper) {
        if (proper) {
            return raytracePlaceCheck(entity, pos);
        }
        return mc.world.rayTraceBlocks(PositionUtil.getEyesPos(entity), new Vec3d(pos.getX() + 0.5D, pos.getY() + 1.0D, pos.getZ() + 0.5D), false, false, false) == null;
    }

    public static RayTraceResult getRayTraceResult(float yaw, float pitch, float distance) {
        Vec3d vec3d = PositionUtil.getEyesPos(mc.player);
        Vec3d lookVec = RotationUtil.getVec3d(yaw, pitch);
        Vec3d rotations = vec3d.add(lookVec.x * (double)distance, lookVec.y * (double)distance, lookVec.z * (double)distance);
        return Optional.ofNullable(
                mc.world.rayTraceBlocks(vec3d, rotations, false, false, false)).orElseGet(()
                -> new RayTraceResult(RayTraceResult.Type.MISS, new Vec3d(0.5, 1.0, 0.5), EnumFacing.UP, BlockPos.ORIGIN));
    }

    public static boolean raytracePlaceCheck(Entity entity, BlockPos pos) {
        return getFacing(entity, pos, false) != null;
    }

    public static EnumFacing getFacing(Entity entity, BlockPos pos, boolean verticals) {
        Vec3d eyePos = PositionUtil.getEyesPos(entity);
        for (EnumFacing facing : FacingUtil.VALUESNODOWN) {
            RayTraceResult result = mc.world.rayTraceBlocks(eyePos, new Vec3d(pos.getX() + 0.5 + facing.getDirectionVec().getX() * 1.0 / 2.0, pos.getY() + 0.5 + facing.getDirectionVec().getY() * 1.0 / 2.0, pos.getZ() + 0.5 + facing.getDirectionVec().getZ() * 1.0 / 2.0), false, true, false);

            if (result != null && result.typeOfHit == RayTraceResult.Type.BLOCK && result.getBlockPos().equals(pos)) {
                return facing;
            }
        }

        if (verticals) {
            if (pos.getY() > mc.player.posY + mc.player.getEyeHeight()) {
                return EnumFacing.DOWN;
            }

            return EnumFacing.UP;
        }

        return null;
    }


}
