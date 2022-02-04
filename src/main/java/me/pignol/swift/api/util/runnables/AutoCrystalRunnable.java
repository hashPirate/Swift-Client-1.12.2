package me.pignol.swift.api.util.runnables;

import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;

import java.util.List;

public class AutoCrystalRunnable implements Runnable {

    public double x;
    public double y;
    public double z;

    public AutoCrystalRunnable(double x, double y, double z) {
        this.x = x;
        this.y = y;
        this.z = z;
    }

    @Override
    public void run() {
        final List<Entity> loadedEntityList = Minecraft.getMinecraft().world.loadedEntityList;
        final int size = loadedEntityList.size();
        for (int i = 0; i < size; i++) {
            Entity entity = loadedEntityList.get(i);
            if (entity instanceof EntityEnderCrystal && entity.getDistanceSq(x, y, z) < 36) {
                entity.setDead();
            }
        }
    }

}
