package me.pignol.swift.api.mixins;

import me.pignol.swift.api.util.ColorUtil;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.modules.other.ColorsModule;
import me.pignol.swift.client.modules.other.FontModule;
import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiNewChat;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

import java.awt.*;

@Mixin(GuiNewChat.class)
public class MixinGuiNewChat extends Gui {

    @Shadow @Final private Minecraft mc;

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiNewChat;drawRect(IIIII)V"))
    public void drawChat(int left, int top, int right, int bottom, int color) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.chatBox.getValue()) {
            return;
        }
        drawRect(left, top, right, bottom, color);
    }

    @Redirect(method = "drawChat", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/FontRenderer;drawStringWithShadow(Ljava/lang/String;FFI)I"))
    private int drawStringWithShadow(FontRenderer fontRenderer, String text, float x, float y, int color) {
        if (FontModule.INSTANCE.isEnabled() && FontModule.INSTANCE.syncChat.getValue()) {
            FontManager.getInstance().drawStringWithShadow(text, x, y, color);
            return 0;
        }
        if (text.contains("\u00a7+")) {
            drawRainbowString(text, x, y, Color.HSBtoRGB(ColorsModule.INSTANCE.hue, ColorsModule.INSTANCE.saturation.getValue() / 255.0F, ColorsModule.INSTANCE.brightness.getValue() / 255.0F), ColorsModule.INSTANCE.factor.getValue(), color >> 24 & 0xFF);
            return 0;
        }
        return Minecraft.getMinecraft().fontRenderer.drawStringWithShadow(text, x, y, color);
    }

    public void drawRainbowString(String text, float x, float y, int startColor, float factor, int alpha) {
        Color currentColor = new Color(startColor);
        float hueIncrement = 1.0f / factor;
        float currentHue = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[0];
        float saturation = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[1];
        float brightness = Color.RGBtoHSB(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), null)[2];
        int currentWidth = 0;
        boolean shouldRainbow = true;
        boolean shouldContinue = false;
        for (int i = 0; i < text.length(); ++i) {
            char currentChar = text.charAt(i);
            char nextChar = text.charAt(MathHelper.clamp(i + 1, 0, text.length() - 1));
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                shouldRainbow = false;
            } else if ((String.valueOf(currentChar) + nextChar).equals("\u00a7+")) {
                shouldRainbow = true;
            }
            if (shouldContinue) {
                shouldContinue = false;
                continue;
            }
            if ((String.valueOf(currentChar) + nextChar).equals("\u00a7r")) {
                String escapeString = text.substring(i);
                mc.fontRenderer.drawStringWithShadow(escapeString, x + (float)currentWidth, y, ColorUtil.toRGBA(255, 255, 255, alpha));
                break;
            }
            mc.fontRenderer.drawStringWithShadow(String.valueOf(currentChar).equals("\u00a7") ? "" : String.valueOf(currentChar), x + (float)currentWidth, y, shouldRainbow ? ColorUtil.toRGBA(currentColor.getRed(), currentColor.getGreen(), currentColor.getBlue(), alpha) : ColorUtil.toRGBA(255, 255, 255, alpha));
            if (String.valueOf(currentChar).equals("\u00a7")) {
                shouldContinue = true;
            }
            currentWidth += mc.fontRenderer.getStringWidth(String.valueOf(currentChar));
            if (String.valueOf(currentChar).equals(" ")) continue;
            currentColor = new Color(Color.HSBtoRGB(currentHue, saturation, brightness));
            currentHue += hueIncrement;
        }
    }

}
