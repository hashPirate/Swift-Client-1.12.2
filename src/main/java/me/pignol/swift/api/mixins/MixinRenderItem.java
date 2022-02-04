package me.pignol.swift.api.mixins;

import me.pignol.swift.api.util.FacingUtil;
import me.pignol.swift.client.modules.other.ColorsModule;
import me.pignol.swift.client.modules.other.ManageModule;
import me.pignol.swift.client.modules.render.EnchantGlintModule;
import me.pignol.swift.client.modules.render.ViewmodelModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderItem;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArg;

import java.awt.*;
import java.util.List;

@Mixin(RenderItem.class)
public abstract class MixinRenderItem {

    @Final
    @Shadow
    private static ResourceLocation RES_ITEM_GLINT;

    @Shadow
    public abstract void renderQuads(BufferBuilder renderer, List<BakedQuad> quads, int color, ItemStack stack);

    @ModifyArg(method = "renderEffect", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/renderer/RenderItem;renderModel(Lnet/minecraft/client/renderer/block/model/IBakedModel;I)V"), index = 1)
    public int renderEffect(int color) {
        EnchantGlintModule instance = EnchantGlintModule.INSTANCE;
        if (instance.isEnabled()) {
            return instance.rainbow.getValue() ? Color.HSBtoRGB(ColorsModule.INSTANCE.hue, ColorsModule.INSTANCE.saturation.getValue() / 255.0F, ColorsModule.INSTANCE.brightness.getValue() / 255.0F) : ((0xFF) << 24) | ((instance.red.getValue() & 0xFF) << 16) | ((instance.green.getValue() & 0xFF) << 8) | ((instance.blue.getValue() & 0xFF));
        }
        return color;
    }

    /**
     * @author e
     * @reason inline
     */
    @Overwrite
    public void renderModel(IBakedModel model, ItemStack stack) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
        for (EnumFacing enumfacing : FacingUtil.VALUES) {
            this.renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), -1, stack);
        }
        this.renderQuads(bufferbuilder, model.getQuads(null, null, 0L), -1, stack);
        tessellator.draw();
    }

    /**
     * @author e
     * @reason inline
     */
    @Overwrite
    public void renderModel(IBakedModel model, int color) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        final ItemStack EMPTY = ItemStack.EMPTY;
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
        for (EnumFacing enumfacing : FacingUtil.VALUES) {
            this.renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), color, EMPTY);
        }
        this.renderQuads(bufferbuilder, model.getQuads(null, null, 0L), color, EMPTY);
        tessellator.draw();
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    private void renderModel(IBakedModel model, int color, ItemStack stack) {
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder bufferbuilder = tessellator.getBuffer();
        bufferbuilder.begin(7, DefaultVertexFormats.ITEM);
        for (EnumFacing enumfacing : FacingUtil.VALUES) {
            this.renderQuads(bufferbuilder, model.getQuads(null, enumfacing, 0L), color, stack);
        }
        this.renderQuads(bufferbuilder, model.getQuads(null, null, 0L), color, stack);
        tessellator.draw();
    }

    /**
     * @author w
     */
    @Overwrite
    public void renderItem(ItemStack stack, IBakedModel model) {
        if (!stack.isEmpty()) {
            GlStateManager.pushMatrix();
            GlStateManager.translate(-0.5F, -0.5F, -0.5F);

            boolean useCustomAlpha = ViewmodelModule.INSTANCE.isEnabled() && ViewmodelModule.rendering;

            int clr = ((ViewmodelModule.INSTANCE.alpha.getValue() & 0xFF) << 24) |
                    ((0xFF) << 16) |
                    ((0xFF) << 8)  |
                    ((0xFF));

            if (model.isBuiltInRenderer()) {
                if (useCustomAlpha) {
                    GlStateManager.color(1, 1, 1, ViewmodelModule.INSTANCE.alpha.getValue() / 255.0F);
                } else {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                }
                GlStateManager.enableRescaleNormal();
                stack.getItem().getTileEntityItemStackRenderer().renderByItem(stack);
            } else {
                renderModel(model, useCustomAlpha ? clr : -1, stack);
                if (stack.hasEffect()) {
                    this.renderEffect(model);
                }
            }
            GlStateManager.popMatrix();
        }
    }

    /**
     * @author e
     */
    @Overwrite
    private void renderEffect(IBakedModel model) {
        GlStateManager.depthMask(false);
        GlStateManager.depthFunc(514);
        GlStateManager.disableLighting();
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_COLOR, GlStateManager.DestFactor.ONE);
        Minecraft.getMinecraft().renderEngine.bindTexture(RES_ITEM_GLINT);
        GlStateManager.matrixMode(5890);
        GlStateManager.pushMatrix();
        GlStateManager.scale(8.0F, 8.0F, 8.0F);
        float f = (float)(Minecraft.getSystemTime() % 3000L) / 3000.0F / 8.0F;
        GlStateManager.translate(f, 0.0F, 0.0F);
        GlStateManager.rotate(-50.0F, 0.0F, 0.0F, 1.0F);
        this.renderModel(model, -8372020);
        GlStateManager.popMatrix();
        if (!ManageModule.INSTANCE.renderGlintOnce.getValue()) {
            GlStateManager.pushMatrix();
            GlStateManager.scale(8.0F, 8.0F, 8.0F);
            float f1 = (float) (Minecraft.getSystemTime() % 4873L) / 4873.0F / 8.0F;
            GlStateManager.translate(-f1, 0.0F, 0.0F);
            GlStateManager.rotate(10.0F, 0.0F, 0.0F, 1.0F);
            this.renderModel(model, -8372020);
            GlStateManager.popMatrix();
        }
        GlStateManager.matrixMode(5888);
        GlStateManager.blendFunc(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
        GlStateManager.enableLighting();
        GlStateManager.depthFunc(515);
        GlStateManager.depthMask(true);
        Minecraft.getMinecraft().renderEngine.bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
    }

}
