package me.pignol.swift.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.util.math.MathHelper;

public class BetterScaledResolution extends ScaledResolution {

    private final static BetterScaledResolution INSTANCE = new BetterScaledResolution(Minecraft.getMinecraft());

    public static BetterScaledResolution getInstance() {
        return INSTANCE;
    }

    private double scaledWidthD, scaledHeightD;
    private int scaledWidth;
    private int scaledHeight;
    private int scaleFactor;

    public BetterScaledResolution(Minecraft mc) {
        super(mc);
    }

    public void update(Minecraft minecraftClient) {
        this.scaledWidth = minecraftClient.displayWidth;
        this.scaledHeight = minecraftClient.displayHeight;
        this.scaleFactor = 1;
        boolean flag = minecraftClient.isUnicode();
        int i = minecraftClient.gameSettings.guiScale;

        if (i == 0) {
            i = 1000;
        }

        while (this.scaleFactor < i && this.scaledWidth / (this.scaleFactor + 1) >= 320 && this.scaledHeight / (this.scaleFactor + 1) >= 240) {
            ++this.scaleFactor;
        }

        if (flag && this.scaleFactor % 2 != 0 && this.scaleFactor != 1) {
            --this.scaleFactor;
        }

        scaledWidthD = (double) this.scaledWidth / (double) this.scaleFactor;
        scaledHeightD = (double) this.scaledHeight / (double) this.scaleFactor;
        this.scaledWidth = MathHelper.ceil(scaledWidthD);
        this.scaledHeight = MathHelper.ceil(scaledHeightD);
    }

    @Override
    public int getScaledWidth() {
        return this.scaledWidth;
    }

    @Override
    public int getScaledHeight() {
        return this.scaledHeight;
    }

    @Override
    public int getScaleFactor() {
        return this.scaleFactor;
    }

    @Override
    public double getScaledWidth_double() {
        return this.scaledWidthD;
    }

    @Override
    public double getScaledHeight_double() {
        return this.scaledHeightD;
    }

}
