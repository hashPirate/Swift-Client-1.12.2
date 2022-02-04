package me.pignol.swift.api.mixins.optimization;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(TileEntityRendererDispatcher.class)
public class MixinTileEntityRendererDispatcher {

    @Shadow
    private Tessellator batchBuffer;

    @Shadow
    private boolean drawingBatch;

    @Shadow
    public TextureManager renderEngine;

    /**
     * @author e
     */
    @Overwrite(remap = false)
    public void drawBatch(int pass) {
        renderEngine.bindTexture(net.minecraft.client.renderer.texture.TextureMap.LOCATION_BLOCKS_TEXTURE);
        net.minecraft.client.renderer.RenderHelper.disableStandardItemLighting();
        GlStateManager.blendFunc(org.lwjgl.opengl.GL11.GL_SRC_ALPHA, org.lwjgl.opengl.GL11.GL_ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableBlend();
        GlStateManager.disableCull();
        GlStateManager.shadeModel(org.lwjgl.opengl.GL11.GL_FLAT);
        if (pass > 0) {
            net.minecraft.util.math.Vec3d cameraPos = net.minecraft.client.renderer.ActiveRenderInfo.getCameraPosition();
            batchBuffer.getBuffer().sortVertexData((float)cameraPos.x, (float)cameraPos.y, (float)cameraPos.z);
        }
        batchBuffer.draw();

        net.minecraft.client.renderer.RenderHelper.enableStandardItemLighting();
        drawingBatch = false;
    }

}
