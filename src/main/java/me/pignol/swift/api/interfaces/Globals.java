package me.pignol.swift.api.interfaces;

import net.minecraft.client.Minecraft;

public interface Globals {
    Minecraft mc = Minecraft.getMinecraft();

    default boolean isNull() {
        return mc.player == null || mc.world == null;
    }
}
