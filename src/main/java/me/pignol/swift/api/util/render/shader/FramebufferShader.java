/*
 * LiquidBounce Hacked Client
 * A free open source mixin-based injection hacked client for Minecraft using Minecraft Forge.
 * https://github.com/CCBlueX/LiquidBounce/
 */
package me.pignol.swift.api.util.render.shader;

import me.pignol.swift.api.util.BetterScaledResolution;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL20.glUseProgram;

/**
 * @author TheSlowly
 */
public abstract class FramebufferShader extends Shader  {

    private static Framebuffer framebuffer;

    protected float red, green, blue, alpha = 1F;
    protected float radius = 2F;
    protected float quality = 1F;

    public FramebufferShader(final String fragmentShader) {
        super(fragmentShader);
    }

    public void startDraw(final float partialTicks) {
        GlStateManager.enableAlpha();

        GlStateManager.pushMatrix();
        GlStateManager.pushAttrib();

        framebuffer = setupFrameBuffer(framebuffer);
        framebuffer.framebufferClear();
        framebuffer.bindFramebuffer(true);
        mc.entityRenderer.setupCameraTransform(partialTicks, 0);
    }

    public void stopDraw(final int color, final float radius, final float quality) {
        glEnable(GL_BLEND);
        glBlendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        mc.getFramebuffer().bindFramebuffer(true);

        red = ((color >> 16) & 0xFF) / 255F;
        green = ((color >> 8) & 0xFF) / 255F;
        blue = ((color) & 0xFF) / 255F;
        alpha = ((color >> 24) & 0xFF) / 255F;
        this.radius = radius;
        this.quality = quality;

        mc.entityRenderer.disableLightmap();
        RenderHelper.disableStandardItemLighting();

        startShader();
        mc.entityRenderer.setupOverlayRendering();
        drawFramebuffer(framebuffer);
        stopShader();

        mc.entityRenderer.disableLightmap();

        GlStateManager.popMatrix();
        GlStateManager.popAttrib();
    }

    public Framebuffer setupFrameBuffer(Framebuffer frameBuffer) {
        if (frameBuffer != null && checkFramebuffer()) {
            frameBuffer.framebufferClear();
        } else {
            frameBuffer = new Framebuffer(mc.displayWidth, mc.displayHeight, true);
        }
        return frameBuffer;
    }

    public boolean checkFramebuffer() {
        return framebuffer.framebufferHeight == mc.displayHeight && framebuffer.framebufferWidth == mc.displayWidth;
    }

    public void drawFramebuffer(final Framebuffer framebuffer) {
        final ScaledResolution scaledResolution = BetterScaledResolution.getInstance();

        glBindTexture(GL_TEXTURE_2D, framebuffer.framebufferTexture);
        glBegin(GL_QUADS);
        glTexCoord2d(0, 1);
        glVertex2d(0, 0);
        glTexCoord2d(0, 0);
        glVertex2d(0, scaledResolution.getScaledHeight());
        glTexCoord2d(1, 0);
        glVertex2d(scaledResolution.getScaledWidth(), scaledResolution.getScaledHeight());
        glTexCoord2d(1, 1);
        glVertex2d(scaledResolution.getScaledWidth(), 0);
        glEnd();
        glUseProgram(0);
    }
}
