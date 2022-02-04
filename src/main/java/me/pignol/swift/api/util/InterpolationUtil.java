package me.pignol.swift.api.util;

import net.minecraft.entity.Entity;
import net.minecraft.util.math.Vec3d;

public class InterpolationUtil {

    public static Vec3d interpolateEntity(final Entity entity, final float time) {
        return new Vec3d(
                entity.lastTickPosX + (entity.posX - entity.lastTickPosX) * time,
                entity.lastTickPosY + (entity.posY - entity.lastTickPosY) * time,
                entity.lastTickPosZ + (entity.posZ - entity.lastTickPosZ) * time
        );
    }

}
