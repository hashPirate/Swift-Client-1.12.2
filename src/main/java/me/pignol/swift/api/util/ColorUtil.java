package me.pignol.swift.api.util;

import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

public class ColorUtil {

    public static void glColor(final int hex) {
        GL11.glColor4f((hex >> 16 & 0xFF) / 255.0F, (hex >> 8 & 0xFF) / 255.0F, (hex & 0xFF) / 255.0F, (hex >> 24 & 0xFF) / 255.0F);
    }

    public static int toRGBA(int r, int g, int b, int a) {
        return (r << 16) + (g << 8) + (b) + (a << 24);
    }

    public static int toRGBA(int r, int g, int b) {
        return toRGBA(r, g, b, 255);
    }

    public static int toRGBA(float r, float g, float b, float a) {
        return toRGBA((int)(r * 255.0F), (int)(g * 255.0F), (int)(b * 255.0F), (int)(a * 255.0F));
    }

    public static int changeAlpha(int origColor, final int userInputedAlpha) {
        origColor = origColor & 0x00FFFFFF;
        return (userInputedAlpha << 24) | origColor;
    }

    public static int getDurabilityColor(ItemStack stack) {
        return MathHelper.hsvToRGB(Math.max(0.0F, (float) (1.0F - (double)stack.getItemDamage() / (double)stack.getMaxDamage())) / 3.0F, 1.0F, 1.0F);
    }

}
