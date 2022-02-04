package me.pignol.swift.api.util.render;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import org.lwjgl.opengl.GL11;

import static org.lwjgl.opengl.GL11.*;

public class RenderUtil {

    public static void enableGL3D() {
        enableGL3D(1.0F);
    }

    public static void enableGL3D(final float lineWidth) {
        GL11.glLineWidth(lineWidth);
        GlStateManager.disableAlpha();
        GlStateManager.enableBlend();
        GlStateManager.blendFunc(GL_SRC_ALPHA, GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.disableTexture2D();
        GlStateManager.disableDepth();
        //GlStateManager.disableLighting();
        GL11.glEnable(GL_LINE_SMOOTH);
        glHint(GL_LINE_SMOOTH_HINT, GL_NICEST);
    }

    public static void disableGL3D() {
        GL11.glDisable(GL_LINE_SMOOTH);
        //GlStateManager.enableLighting();
        GlStateManager.enableDepth();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.enableAlpha();
    }

    public static void enableGL2D() {
        GL11.glPushMatrix();
        GL11.glEnable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_TEXTURE_2D);
        GL11.glBlendFunc(GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
    }

    public static void disableGL2D() {
        GL11.glEnable(GL11.GL_TEXTURE_2D);
        GL11.glDisable(GL11.GL_BLEND);
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GL11.glPopMatrix();
    }

    public static AxisAlignedBB getRenderBB(BlockPos pos, float height) {
        final Minecraft mc = Minecraft.getMinecraft();
        return new AxisAlignedBB(
                pos.getX() - mc.getRenderManager().viewerPosX,
                pos.getY() - mc.getRenderManager().viewerPosY,
                pos.getZ() - mc.getRenderManager().viewerPosZ,
                pos.getX() + 1 - mc.getRenderManager().viewerPosX,
                pos.getY() + height - mc.getRenderManager().viewerPosY,
                pos.getZ() + 1 - mc.getRenderManager().viewerPosZ
        );
    }

    public static AxisAlignedBB getRenderBB(BlockPos pos) {
        return getRenderBB(pos, 1.0F);
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, float red, float green, float blue, float alpha) {
        GL11.glLineWidth(width);
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder vertexbuffer = tessellator.getBuffer();
        GlStateManager.color(red, green, blue, alpha);
        vertexbuffer.begin(3, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tessellator.draw();

        vertexbuffer.begin(1, DefaultVertexFormats.POSITION);
        vertexbuffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        vertexbuffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        vertexbuffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        tessellator.draw();
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
        drawBoundingBox(bb, width, red, green, blue, alpha);
    }

    public static void drawBoundingBox(AxisAlignedBB bb, float width, int color, int alpha) {
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
        drawBoundingBox(bb, width, red, green, blue, alpha / 255.0F);
    }

    public static void drawFilledBox(AxisAlignedBB bb, int color) {
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
        drawFilledBox(bb, red, green, blue, alpha);
    }

    public static void drawFilledBox(AxisAlignedBB bb, int color, int alpha) {
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
        drawFilledBox(bb, red, green, blue, alpha / 255.0F);
    }

    public static void drawFilledBox(AxisAlignedBB bb, float red, float green, float blue, float alpha) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        GlStateManager.color(red, green, blue, alpha);
        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();

        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tessellator.draw();
    }

    public static void drawWireframeBox(AxisAlignedBB bb, int color, boolean top) {
        final float alpha = (color >> 24 & 0xFF) / 255.0F;
        final float red = (color >> 16 & 0xFF) / 255.0F;
        final float green = (color >> 8 & 0xFF) / 255.0F;
        final float blue = (color & 0xFF) / 255.0F;
        GlStateManager.color(red, green, blue, alpha);

        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();

        buffer.begin(GL11.GL_LINES, DefaultVertexFormats.POSITION);
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        buffer.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        buffer.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        if (top) {
            buffer.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
            buffer.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
            buffer.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        }
        tessellator.draw();
    }

    public static void drawFilledBox(AxisAlignedBB bb) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();

        bufferbuilder.begin(GL_QUADS, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();

        bufferbuilder.pos(bb.maxX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.maxX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();

        bufferbuilder.pos(bb.minX, bb.minY, bb.minZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.minY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.maxZ).endVertex();
        bufferbuilder.pos(bb.minX, bb.maxY, bb.minZ).endVertex();
        tessellator.draw();
    }

    public static void drawBorderedRectFixed(float x, float y, float width, float height, float lineSize, int color, int borderColor) {
        drawRectFixed(x, y, width, height, color);
        drawRectFixed(x, y, lineSize, height, borderColor);
        drawRectFixed(x, y, width, lineSize, borderColor);
        drawRectFixed(x + width - lineSize, y, lineSize, height, borderColor);
        drawRectFixed(x, y + height - lineSize, width, lineSize, borderColor);
    }

    public static void drawRectFixed(float x, float y, float w, float h, int color) {
        float lvt_5_2_;
        float p_drawRect_2_ = x + w;
        float p_drawRect_3_ = y + h;
        if (x < p_drawRect_2_) {
            lvt_5_2_ = x;
            x = p_drawRect_2_;
            p_drawRect_2_ = lvt_5_2_;
        }

        if (y < p_drawRect_3_) {
            lvt_5_2_ = y;
            y = p_drawRect_3_;
            p_drawRect_3_ = lvt_5_2_;
        }

        float alpha = (float) (color >> 24 & 255) / 255.0F;
        float red = (float) (color >> 16 & 255) / 255.0F;
        float green = (float) (color >> 8 & 255) / 255.0F;
        float blue = (float) (color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder builder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        builder.begin(7, DefaultVertexFormats.POSITION);
        builder.pos(x, p_drawRect_3_, 0.0D).endVertex();
        builder.pos(p_drawRect_2_, p_drawRect_3_, 0.0D).endVertex();
        builder.pos(p_drawRect_2_, y, 0.0D).endVertex();
        builder.pos(x, y, 0.0D).endVertex();
        tessellator.draw();
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
        GlStateManager.disableBlend();
    }

    public static void drawRect(float left, float top, float right, float bottom, int color)
    {
        if (left < right)
        {
            float i = left;
            left = right;
            right = i;
        }

        if (top < bottom) {
            float j = top;
            top = bottom;
            bottom = j;
        }

        float f3 = (float)(color >> 24 & 255) / 255.0F;
        float f = (float)(color >> 16 & 255) / 255.0F;
        float f1 = (float)(color >> 8 & 255) / 255.0F;
        float f2 = (float)(color & 255) / 255.0F;
        Tessellator tessellator = Tessellator.getInstance();
        BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL_NICEST);
        GlStateManager.color(f, f1, f2, f3);
        bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
        bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
        bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
        bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
        tessellator.draw();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawOutlineRect(float left, float top, float right, float bottom, int paramColor) {
        final float alpha = (float) (paramColor >> 24 & 0xFF) / 255F;
        final float red = (float) (paramColor >> 16 & 0xFF) / 255F;
        final float green = (float) (paramColor >> 8 & 0xFF) / 255F;
        final float blue = (float) (paramColor & 0xFF) / 255F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(red, green, blue, alpha);
        GL11.glEnable(GL11.GL_LINE_SMOOTH);
        GL11.glHint(GL11.GL_LINE_SMOOTH_HINT, GL11.GL_NICEST);
        bufferbuilder.begin(2, DefaultVertexFormats.POSITION);
        bufferbuilder.pos(left, bottom, 0).endVertex();
        bufferbuilder.pos(right, bottom, 0).endVertex();
        bufferbuilder.pos(right, top, 0).endVertex();
        bufferbuilder.pos(left, top, 0).endVertex();
        tessellator.draw();
        GL11.glDisable(GL11.GL_LINE_SMOOTH);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

    public static void drawOutlinedRect(double x, double y, double w, double h, int color, float width) {
        float r = (float)(color >> 16 & 0xFF) / 255.0f;
        float g = (float)(color >> 8 & 0xFF) / 255.0f;
        float b = (float)(color & 0xFF) / 255.0f;
        float a = (float)(color >> 24 & 0xFF) / 255.0f;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        GlStateManager.enableBlend();
        GlStateManager.disableTexture2D();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.color(r, g, b, a);
        GL11.glLineWidth(width);
        buffer.begin(2, DefaultVertexFormats.POSITION);
        buffer.pos(x, y, 0.0).endVertex();
        buffer.pos(x, y + h, 0.0).endVertex();
        buffer.pos(x + w, y + h, 0.0).endVertex();
        buffer.pos(x + w, y, 0.0).endVertex();
        tessellator.draw();
        GlStateManager.color(1.0f, 1.0f, 1.0f);
        GlStateManager.enableTexture2D();
        GlStateManager.disableBlend();
    }

}
