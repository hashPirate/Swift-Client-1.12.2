package me.pignol.swift.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.Vec3d;

import java.math.BigDecimal;

public class MathUtil {

    public static float rad(final float angle) {
        return (float) (angle * Math.PI / 180);
    }

    public static double square(final double in) {
        return in * in;
    }

    public static float square(final float in) {
        return in * in;
    }

    public static int square(final int in) {
        return in * in;
    }

    public static Vec3d mult(Vec3d factor, Vec3d multiplier) {
        return new Vec3d(factor.x * multiplier.x, factor.y * multiplier.y, factor.z * multiplier.z);
    }

    public static Vec3d mult(Vec3d factor, float multiplier) {
        return new Vec3d(factor.x * multiplier, factor.y * multiplier, factor.z * multiplier);
    }

    public static Vec3d div(Vec3d factor, Vec3d divisor) {
        return new Vec3d(factor.x / divisor.x, factor.y / divisor.y, factor.z / divisor.z);
    }

    public static Vec3d div(Vec3d factor, float divisor) {
        return new Vec3d(factor.x / divisor, factor.y / divisor, factor.z / divisor);
    }

    public static double getYDifferenceSq(double firstY, double secondY) {
        double y = firstY - secondY;
        return y * y;
    }

    public static float roundFloat(float d, int decimalPlace) {
        BigDecimal bd = new BigDecimal(Float.toString(d));
        bd = bd.setScale(decimalPlace, BigDecimal.ROUND_HALF_UP);
        return bd.floatValue();
    }


    public static double lerp(double a, double b, float partial) {
        return (a * (1.0 - partial)) + (b * partial);
    }

    public static Vec3d interpolateEntity(Entity entity, float time) {
        return new Vec3d(entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time);
    }

    public static double getIncremental(final double val, final double inc) {
        final double one = 1.0 / inc;
        return Math.round(val * one) / one;
    }

    public static float[] calculateAngle(Vec3d from, Vec3d to) {
        double difX = to.x - from.x;
        double difY = (to.y - from.y) * -1.0;
        double difZ = to.z - from.z;
        double dist = MathHelper.sqrt((double)(difX * difX + difZ * difZ));
        float yD = (float)MathHelper.wrapDegrees((double)(Math.toDegrees(Math.atan2(difZ, difX)) - 90.0));
        float pD = (float)MathHelper.wrapDegrees((double)Math.toDegrees(Math.atan2(difY, dist)));
        if (pD > 90F) {
            pD = 90F;
        } else if (pD < -90F) {
            pD = -90F;
        }
        return new float[]{yD, pD};
    }

}
