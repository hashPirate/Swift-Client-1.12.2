package me.pignol.swift.api.util;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.client.shader.ShaderGroup;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

public class BlurUtil {


    /*private static final Minecraft mc = Minecraft.getMinecraft();
    private static ShaderGroup blurShader;
    private static Framebuffer buffer;
    private static int lastScale;
    private static int lastScaleWidth;
    private static int lastScaleHeight;
    private static final ResourceLocation shader = new ResourceLocation("shaders/post/blur.json");

    public static void initFboAndShader() {
        try {
            blurShader = new ShaderGroup(mc.getTextureManager(), mc.getResourceManager(), mc.getFramebuffer(), shader);
            blurShader.createBindFramebuffers(mc.displayWidth, mc.displayHeight);
            buffer = blurShader.getListFramebuffers().get(0);
        } catch (final Exception e) {
        }
    }

    private static void setShaderConfigs(final float intensity, final float blurWidth, final float blurHeight) {
        ((AccessorShaderGroup) blurShader).getListShaders().get(0).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((AccessorShaderGroup) blurShader).getListShaders().get(1).getShaderManager().getShaderUniform("Radius").set(intensity);
        ((AccessorShaderGroup) blurShader).getListShaders().get(0).getShaderManager().getShaderUniform("BlurDir").set(blurWidth, blurHeight);
        ((AccessorShaderGroup) blurShader).getListShaders().get(1).getShaderManager().getShaderUniform("BlurDir").set(blurHeight, blurWidth);
    }

    public static void blurArea(final int x, final int y, final int width, final int height, final float intensity, final float blurWidth, final float blurHeight) {
        final ScaledResolution scale = new ScaledResolution(mc);
        final int factor = scale.getScaleFactor();
        final int factor2 = scale.getScaledWidth();
        final int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        if (OpenGlHelper.isFramebufferEnabled()) {

            buffer.framebufferClear();

            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor, height * factor);

            setShaderConfigs(intensity, blurWidth, blurHeight);
            buffer.bindFramebuffer(true);
            blurShader.render(mc.getRenderPartialTicks());

            mc.getFramebuffer().bindFramebuffer(true);

            GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
            buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
            GlStateManager.disableBlend();
            GL11.glScalef(factor, factor, 0);
        }
    }

    public static void blurScreen(final float intensity, final float blurWidth, final float blurHeight) {
        final ScaledResolution scale = new ScaledResolution(mc);
        final int factor = scale.getScaleFactor();
        final int factor2 = scale.getScaledWidth();
        final int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        if (OpenGlHelper.isFramebufferEnabled()) {

            buffer.framebufferClear();

            //GL11.glEnable(GL11.GL_SCISSOR_TEST);
            //GL11.glScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor, height * factor);

            setShaderConfigs(intensity, blurWidth, blurHeight);
            buffer.bindFramebuffer(true);
            blurShader.render(mc.getRenderPartialTicks());

            mc.getFramebuffer().bindFramebuffer(true);

           // GL11.glDisable(GL11.GL_SCISSOR_TEST);
            GlStateManager.enableBlend();
            GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
            buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
            GlStateManager.disableBlend();
            GL11.glScalef(factor, factor, 0);
        }
    }

    public static void blurArea(final int x, final int y, final int width, final int height, final float intensity) {
        final ScaledResolution scale = new ScaledResolution(mc);
        final int factor = scale.getScaleFactor();
        final int factor2 = scale.getScaledWidth();
        final int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        buffer.framebufferClear();

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x, (mc.displayHeight - (y) - height), width, (height));

        setShaderConfigs(intensity, 1, 1);
        buffer.bindFramebuffer(true);
        blurShader.render(mc.getRenderPartialTicks());

        mc.getFramebuffer().bindFramebuffer(true);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);

        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ZERO, GL11.GL_ONE);
        buffer.framebufferRenderExt(mc.displayWidth, mc.displayHeight, false);
        GlStateManager.disableBlend();
        GL11.glScalef(factor, factor, 0);
        RenderHelper.enableGUIStandardItemLighting();
    }

    public static void blurAreaBoarder(final int x, final int y, final int width, final int height, final float intensity, final float blurWidth, final float blurHeight) {
        final ScaledResolution scale = new ScaledResolution(mc);
        final int factor = scale.getScaleFactor();
        final int factor2 = scale.getScaledWidth();
        final int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        GL11.glScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor, height * factor);

        setShaderConfigs(intensity, blurWidth, blurHeight);
        buffer.bindFramebuffer(true);
        blurShader.render(mc.getRenderPartialTicks());

        mc.getFramebuffer().bindFramebuffer(true);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void blurAreaBoarder(final int x, final int y, final int width, final int height, final float intensity) {
        final ScaledResolution scale = new ScaledResolution(mc);
        final int factor = scale.getScaleFactor();
        final int factor2 = scale.getScaledWidth();
        final int factor3 = scale.getScaledHeight();
        if (lastScale != factor || lastScaleWidth != factor2 || lastScaleHeight != factor3 || buffer == null || blurShader == null) {
            initFboAndShader();
        }
        lastScale = factor;
        lastScaleWidth = factor2;
        lastScaleHeight = factor3;

        GL11.glScissor(x * factor, (mc.displayHeight - (y * factor) - height * factor), width * factor, (height) * factor);
        GL11.glEnable(GL11.GL_SCISSOR_TEST);

        setShaderConfigs(intensity, 1, 0);
        buffer.bindFramebuffer(true);
        blurShader.render(mc.getRenderPartialTicks());

        mc.getFramebuffer().bindFramebuffer(true);

        GL11.glDisable(GL11.GL_SCISSOR_TEST);
    }

    public static void end() {
        if (blurShader == null) return;

        ((AccessorShaderGroup) blurShader).getListShaders().forEach(shader -> shader.getShaderManager().endShader());
    }*/

}
