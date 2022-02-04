package me.pignol.swift.api.util.runnables;

import me.pignol.swift.api.util.DamageUtil;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.client.managers.SafetyManager;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;

import java.util.List;

public class SafetyRunnable implements Runnable {

    private final SafetyManager manager;
    private final List<Entity> crystals;

    public SafetyRunnable(SafetyManager manager,  List<Entity> crystals) {
        this.manager = manager;
        this.crystals = crystals;
    }

    @Override
    public void run() {
        final EntityPlayer player = Minecraft.getMinecraft().player;
        for (int i = 0, size = crystals.size(); i < size; ++i) {
            final Entity entity = crystals.get(i);
            if (entity instanceof EntityEnderCrystal && !entity.isDead) {
                float damage = DamageUtil.calculate(entity, player);
                if (damage > EntityUtil.getHealth(player) + 1.0) {
                    manager.setSafe(false);
                    return;
                }
            }
        }

        manager.setSafe(true);
    }

}
