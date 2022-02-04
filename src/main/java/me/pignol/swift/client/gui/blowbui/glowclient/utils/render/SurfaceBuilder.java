package me.pignol.swift.client.gui.blowbui.glowclient.utils.render;

import me.pignol.swift.api.util.render.font.CFontRenderer;
import me.pignol.swift.client.modules.other.ClickGuiModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.DestFactor;
import net.minecraft.client.renderer.GlStateManager.SourceFactor;
import net.minecraft.util.math.MathHelper;
import org.lwjgl.opengl.GL11;

import java.util.Stack;

public class SurfaceBuilder {
    private final Stack settings = new Stack();
    private final SurfaceBuilder.RenderSettings DEFAULT_SETTINGS = new SurfaceBuilder.RenderSettings();

    public static void enableBlend() {
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(SourceFactor.SRC_ALPHA, DestFactor.ONE_MINUS_SRC_ALPHA, SourceFactor.ONE, DestFactor.ZERO);
    }

    public static void enableFontRendering() {
        GlStateManager.disableDepth();
    }

    public SurfaceBuilder.RenderSettings current() {
        return !this.settings.isEmpty() ? (SurfaceBuilder.RenderSettings) this.settings.peek() : this.DEFAULT_SETTINGS;
    }

    public SurfaceBuilder reset() {
        return this.reset(15);
    }

    public SurfaceBuilder reset(int flags) {
        SurfaceBuilder.RenderSettings current = this.current();
        if ((flags & 1) == 1) {
            current.resetColor();
        }

        if ((flags & 2) == 2) {
            current.resetScale();
        }

        if ((flags & 4) == 4) {
            current.resetTranslation();
        }

        if ((flags & 8) == 8) {
            current.resetRotation();
        }

        return this;
    }


    public SurfaceBuilder color(double r, double g, double b, double a) {
        this.current().setColor4d(new double[]{MathHelper.clamp(r, 0.0D, 1.0D), MathHelper.clamp(g, 0.0D, 1.0D), MathHelper.clamp(b, 0.0D, 1.0D), MathHelper.clamp(a, 0.0D, 1.0D)});
        return this;
    }

    public SurfaceBuilder color(int buffer) {
        return this.color((double) (buffer >> 16 & 255) / 255.0D, (double) (buffer >> 8 & 255) / 255.0D, (double) (buffer & 255) / 255.0D, (double) (buffer >> 24 & 255) / 255.0D);
    }

    public SurfaceBuilder color(int r, int g, int b, int a) {
        return this.color((double) r / 255.0D, (double) g / 255.0D, (double) b / 255.0D, (double) a / 255.0D);
    }

    public SurfaceBuilder width(double width) {
        GlStateManager.glLineWidth((float) width);
        return this;
    }

    public SurfaceBuilder fontRenderer(CFontRenderer fontRenderer) {
        this.current().setFontRenderer(fontRenderer);
        return this;
    }

    public SurfaceBuilder text(String text, double x, double y, boolean shadow) {
        if (ClickGuiModule.INSTANCE.customFont.getValue() && this.current().hasFontRenderer()) {
            this.current().getFontRenderer().drawString(text, (float) x, (float)y + 1.0F, Colors.toRGBA(this.current().getColor4d()), shadow);
        } else {
            GlStateManager.pushMatrix();
            GlStateManager.translate(x, y + 1.0D, 0.0D);
            Minecraft.getMinecraft().fontRenderer.drawString(text, 0.0F, 0.0F, Colors.toRGBA(this.current().getColor4d()), shadow);
            GlStateManager.popMatrix();
        }
        return this;
    }

    public SurfaceBuilder text(String text, double x, double y) {
        return this.text(text, x, y, false);
    }

    public SurfaceBuilder task(Runnable task) {
        task.run();
        return this;
    }

    private double _getScaled(int index, double p) {
        return p * (1.0D / this.current().getScale3d()[index]);
    }

    public double getScaledX(double x) {
        return this._getScaled(0, x);
    }

    private static class RenderSettings {
        private static final double[] EMPTY_VECTOR3D = new double[]{0.0D, 0.0D, 0.0D};
        private static final double[] EMPTY_VECTOR4D = new double[]{0.0D, 0.0D, 0.0D, 0.0D};
        private double[] color4d;
        private double[] scale3d;
        private double[] translate3d;
        private double[] rotated4d;
        private boolean autoApply;
        private CFontRenderer fontRenderer;

        private RenderSettings() {
            this.color4d = EMPTY_VECTOR4D;
            this.scale3d = EMPTY_VECTOR3D;
            this.translate3d = EMPTY_VECTOR3D;
            this.rotated4d = EMPTY_VECTOR4D;
            this.autoApply = true;
            this.fontRenderer = null;
        }

        public double[] getColor4d() {
            return this.color4d;
        }

        public void setColor4d(double[] color4d) {
            this.color4d = color4d;
            if (this.autoApply) {
                this.applyColor();
            }

        }

        public double[] getScale3d() {
            return this.scale3d;
        }

        public void setScale3d(double[] scale3d) {
            this.scale3d = scale3d;
            if (this.autoApply) {
                this.applyScale();
            }
        }

        public CFontRenderer getFontRenderer() {
            return this.fontRenderer;
        }

        public void setFontRenderer(CFontRenderer fontRenderer) {
            this.fontRenderer = fontRenderer;
        }

        public void setAutoApply(boolean autoApply) {
            this.autoApply = autoApply;
        }

        public boolean hasColor() {
            return this.color4d != EMPTY_VECTOR4D;
        }

        public boolean hasScale() {
            return this.scale3d != EMPTY_VECTOR3D;
        }

        public boolean hasTranslation() {
            return this.translate3d != EMPTY_VECTOR3D;
        }

        public boolean hasRotation() {
            return this.rotated4d != EMPTY_VECTOR4D;
        }

        public boolean hasFontRenderer() {
            return this.fontRenderer != null;
        }

        public void applyColor() {
            if (this.hasColor()) {
                GL11.glColor4d(this.color4d[0], this.color4d[1], this.color4d[2], this.color4d[3]);
            }

        }

        public void applyScale() {
            if (this.hasScale()) {
                GL11.glScaled(this.scale3d[0], this.scale3d[1], this.scale3d[2]);
            }

        }

        public void applyTranslation() {
            if (this.hasTranslation()) {
                GL11.glTranslated(this.translate3d[0], this.translate3d[1], this.translate3d[2]);
            }

        }

        public void applyRotation() {
            if (this.hasRotation()) {
                GL11.glRotated(this.rotated4d[0], this.rotated4d[1], this.rotated4d[2], this.rotated4d[3]);
            }

        }

        public void clearColor() {
            this.color4d = EMPTY_VECTOR4D;
        }

        public void clearScale() {
            this.scale3d = EMPTY_VECTOR3D;
        }

        public void clearTranslation() {
            this.translate3d = EMPTY_VECTOR3D;
        }

        public void clearRotation() {
            this.rotated4d = EMPTY_VECTOR4D;
        }

        public void resetColor() {
            if (this.hasColor()) {
                this.clearColor();
                GL11.glColor4d(1.0D, 1.0D, 1.0D, 1.0D);
            }

        }

        public void resetScale() {
            if (this.hasScale()) {
                this.clearScale();
                GL11.glScaled(1.0D, 1.0D, 1.0D);
            }

        }

        public void resetTranslation() {
            if (this.hasTranslation()) {
                this.clearTranslation();
                GL11.glTranslated(0.0D, 0.0D, 0.0D);
            }

        }

        public void resetRotation() {
            if (this.hasRotation()) {
                this.clearRotation();
                GL11.glRotated(0.0D, 0.0D, 0.0D, 0.0D);
            }

        }
    }
}
