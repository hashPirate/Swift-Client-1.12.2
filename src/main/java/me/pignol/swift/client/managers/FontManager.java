package me.pignol.swift.client.managers;

import me.pignol.swift.api.util.render.font.CFontRenderer;
import me.pignol.swift.client.modules.misc.NameProtectModule;
import me.pignol.swift.client.modules.other.FontModule;
import net.minecraft.client.Minecraft;
import org.apache.commons.lang3.StringUtils;

import java.awt.*;

public class FontManager {

    private static final FontManager INSTANCE = new FontManager();

    public static FontManager getInstance() {
        return INSTANCE;
    }

    private boolean isCustom;

    private CFontRenderer renderer = new CFontRenderer(new Font("Verdana", 0, 18), true, true);

    public void updateFontRenderer() {
        renderer = new CFontRenderer(new Font(FontModule.INSTANCE.font.getValue(), 0, FontModule.INSTANCE.size.getValue()), FontModule.INSTANCE.antiAlias.getValue(), FontModule.INSTANCE.fractionalMetrics.getValue());
    }

    public float drawStringWithShadow(String text, float x, float y, int color) {
        if (NameProtectModule.INSTANCE.isEnabled()) {
            text = StringUtils.replace(text, Minecraft.getMinecraft().getSession().getUsername(), NameProtectModule.INSTANCE.getFakeName());
        }
        if (isCustom) {
            return renderer.drawStringWithShadow(text, x, y, color);
        }
        return Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public void drawString(String text, float x, float y, int color, boolean shadow) {
        if (isCustom) {
            renderer.drawString(text, x, y, color, shadow);
            return;
        }
        Minecraft.getMinecraft().fontRenderer.drawString(text, x, y, color, shadow);
    }

    public int getHeight() {
        if (isCustom) {
            return renderer.getHeight();
        }
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public int getStringHeight(String text) {
        if (isCustom) {
            return renderer.getStringHeight(text);
        }
        return Minecraft.getMinecraft().fontRenderer.FONT_HEIGHT;
    }

    public int getStringWidth(String text) {
        if (NameProtectModule.INSTANCE.isEnabled()) {
            text = StringUtils.replace(text, Minecraft.getMinecraft().getSession().getUsername(), NameProtectModule.INSTANCE.getFakeName());
        }
        if (isCustom) {
            return renderer.getStringWidth(text);
        }
        return Minecraft.getMinecraft().fontRenderer.getStringWidth(text);
    }

    public void setCustom(boolean custom) {
        isCustom = custom;
    }

    public CFontRenderer getCFontRenderer() {
        return this.renderer;
    }

}
