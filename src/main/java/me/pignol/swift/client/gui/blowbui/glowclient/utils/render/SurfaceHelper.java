package me.pignol.swift.client.gui.blowbui.glowclient.utils.render;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.api.util.render.font.CFontRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.ItemStack;
import org.lwjgl.opengl.GL11;

import javax.annotation.Nullable;

public class SurfaceHelper implements Globals {

   public static double getStringWidth(CFontRenderer fontRenderer, String text) {
      return fontRenderer == null ? (double)Minecraft.getMinecraft().fontRenderer.getStringWidth(text) : (double)fontRenderer.getStringWidth(text);
   }

   public static void drawRect(float x, float y, float w, float h, int color) {
      GL11.glLineWidth(1.0F);
      drawRect(x, y, x + w, y + h, color, false);
   }

   public static void drawRect(float left, float top, float right, float bottom, int color, boolean b)
   {
      if (left < right)
      {
         float i = left;
         left = right;
         right = i;
      }

      if (top < bottom)
      {
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
      GlStateManager.color(f, f1, f2, f3);
      bufferbuilder.begin(7, DefaultVertexFormats.POSITION);
      bufferbuilder.pos((double)left, (double)bottom, 0.0D).endVertex();
      bufferbuilder.pos((double)right, (double)bottom, 0.0D).endVertex();
      bufferbuilder.pos((double)right, (double)top, 0.0D).endVertex();
      bufferbuilder.pos((double)left, (double)top, 0.0D).endVertex();
      tessellator.draw();
      GlStateManager.enableTexture2D();
      GlStateManager.disableBlend();
   }



   public static void drawItem(ItemStack item, int x, int y) {
      mc.getRenderItem().renderItemAndEffectIntoGUI(item, x, y);
   }

   public static void drawItemOverlay(ItemStack stack, int x, int y) {
      mc.getRenderItem().renderItemOverlayIntoGUI(mc.fontRenderer, stack, x, y, null);
   }

   public static void drawItem(ItemStack item, double x, double y) {
      GlStateManager.pushMatrix();
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      GlStateManager.enableLighting();
      mc.getRenderItem().zLevel = 100.f;
      renderItemAndEffectIntoGUI(mc.player, item, x, y, 16.D);
      mc.getRenderItem().zLevel = 0.f;
      GlStateManager.popMatrix();
      GlStateManager.disableLighting();
      GlStateManager.enableDepth();
      GlStateManager.color(1.f, 1.f, 1.f, 1.f);
   }

   public static void drawTexturedRect(
           int x, int y, int textureX, int textureY, int width, int height, int zLevel) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder BufferBuilder = tessellator.getBuffer();
      BufferBuilder.begin(7, DefaultVertexFormats.POSITION_TEX);
      BufferBuilder.pos(x + 0, y + height, zLevel)
              .tex(
                      (float) (textureX + 0) * 0.00390625F,
                      (float) (textureY + height) * 0.00390625F)
              .endVertex();
      BufferBuilder.pos(x + width, y + height, zLevel)
              .tex(
                      (float) (textureX + width) * 0.00390625F,
                      (float) (textureY + height) * 0.00390625F)
              .endVertex();
      BufferBuilder.pos(x + width, y + 0, zLevel)
              .tex(
                      (float) (textureX + width) * 0.00390625F,
                      (float) (textureY + 0) * 0.00390625F)
              .endVertex();
      BufferBuilder.pos(x + 0, y + 0, zLevel)
              .tex(
                      (float) (textureX + 0) * 0.00390625F,
                      (float) (textureY + 0) * 0.00390625F)
              .endVertex();
      tessellator.draw();
   }

   public static void drawItemWithOverlay(ItemStack item, double x, double y, double scale) {
      GlStateManager.pushMatrix();
      RenderHelper.enableGUIStandardItemLighting();
      GlStateManager.disableLighting();
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableColorMaterial();
      GlStateManager.enableLighting();
      mc.getRenderItem().zLevel = 100.f;
      renderItemAndEffectIntoGUI(mc.player, item, x, y, 16.D);
      renderItemOverlayIntoGUI(mc.fontRenderer, item, x, y, null, scale);
      mc.getRenderItem().zLevel = 0.f;
      GlStateManager.popMatrix();
      GlStateManager.disableLighting();
      GlStateManager.enableDepth();
      GlStateManager.color(1.f, 1.f, 1.f, 1.f);
   }

   protected static void renderItemAndEffectIntoGUI(@Nullable EntityLivingBase living, final ItemStack stack, double x, double y, double scale) {
      if (!stack.isEmpty()) {
         mc.getRenderItem().zLevel += 50.f;
         try {
            renderItemModelIntoGUI(
                    stack, x, y, mc.getRenderItem().getItemModelWithOverrides(stack, null, living), scale);
         } catch (Throwable t) {
         } finally {
            mc.getRenderItem().zLevel -= 50.f;
         }
      }
   }

   private static void renderItemModelIntoGUI(
           ItemStack stack, double x, double y, IBakedModel bakedmodel, double scale) {
      GlStateManager.pushMatrix();
      mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      mc.getTextureManager()
              .getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE)
              .setBlurMipmap(false, false);
      GlStateManager.enableRescaleNormal();
      GlStateManager.enableAlpha();
      GlStateManager.alphaFunc(516, 0.1F);
      GlStateManager.enableBlend();
      GlStateManager.blendFunc(
              GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA);
      GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);

      GlStateManager.translate(x, y, 100.0F + mc.getRenderItem().zLevel);
      GlStateManager.translate(8.0F, 8.0F, 0.0F);
      GlStateManager.scale(1.0F, -1.0F, 1.0F);
      GlStateManager.scale(scale, scale, scale);

      if (bakedmodel.isGui3d()) {
         GlStateManager.enableLighting();
      } else {
         GlStateManager.disableLighting();
      }

      bakedmodel =
              net.minecraftforge.client.ForgeHooksClient.handleCameraTransforms(
                      bakedmodel, ItemCameraTransforms.TransformType.GUI, false);
      mc.getRenderItem().renderItem(stack, bakedmodel);
      GlStateManager.disableAlpha();
      GlStateManager.disableRescaleNormal();
      GlStateManager.disableLighting();
      GlStateManager.popMatrix();
      mc.getTextureManager().bindTexture(TextureMap.LOCATION_BLOCKS_TEXTURE);
      mc.getTextureManager().getTexture(TextureMap.LOCATION_BLOCKS_TEXTURE).restoreLastBlurMipmap();
   }

   protected static void renderItemOverlayIntoGUI(
           FontRenderer fr,
           ItemStack stack,
           double xPosition,
           double yPosition,
           @Nullable String text,
           double scale) {
      final double SCALE_RATIO = 1.23076923077D;

      if (!stack.isEmpty()) {
         if (stack.getCount() != 1 || text != null) {
            String s = text == null ? String.valueOf(stack.getCount()) : text;
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableBlend();
            fr.drawStringWithShadow(
                    s,
                    (float) (xPosition + 19 - 2 - fr.getStringWidth(s)),
                    (float) (yPosition + 6 + 3),
                    16777215);
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
            // Fixes opaque cooldown overlay a bit lower
            // TODO: check if enabled blending still screws things up down the line.
            GlStateManager.enableBlend();
         }

         if (stack.getItem().showDurabilityBar(stack)) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            GlStateManager.disableAlpha();
            GlStateManager.disableBlend();
            double health = stack.getItem().getDurabilityForDisplay(stack);
            int rgbfordisplay = stack.getItem().getRGBDurabilityForDisplay(stack);
            int i = Math.round(13.0F - (float) health * 13.0F);
            int j = rgbfordisplay;
            draw(xPosition + (scale / 8.D), yPosition + (scale / SCALE_RATIO), 13, 2, 0, 0, 0, 255);
            draw(
                    xPosition + (scale / 8.D),
                    yPosition + (scale / SCALE_RATIO),
                    i,
                    1,
                    j >> 16 & 255,
                    j >> 8 & 255,
                    j & 255,
                    255);
            GlStateManager.enableBlend();
            GlStateManager.enableAlpha();
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }

         EntityPlayerSP entityplayersp = Minecraft.getMinecraft().player;
         float f3 =
                 entityplayersp == null
                         ? 0.0F
                         : entityplayersp
                         .getCooldownTracker()
                         .getCooldown(stack.getItem(), Minecraft.getMinecraft().getRenderPartialTicks());

         if (f3 > 0.0F) {
            GlStateManager.disableLighting();
            GlStateManager.disableDepth();
            GlStateManager.disableTexture2D();
            draw(xPosition, yPosition + scale * (1.0F - f3), 16, scale * f3, 255, 255, 255, 127);
            GlStateManager.enableTexture2D();
            GlStateManager.enableLighting();
            GlStateManager.enableDepth();
         }
      }
   }

   private static void draw(
           double x, double y, double width, double height, int red, int green, int blue, int alpha) {
      Tessellator tessellator = Tessellator.getInstance();
      BufferBuilder renderer = tessellator.getBuffer();
      renderer.begin(7, DefaultVertexFormats.POSITION_COLOR);
      renderer
              .pos(x + 0, y + 0, 0.0D)
              .color(red, green, blue, alpha)
              .endVertex();
      renderer
              .pos(x + 0, y + height, 0.0D)
              .color(red, green, blue, alpha)
              .endVertex();
      renderer
              .pos(x + width, y + height, 0.0D)
              .color(red, green, blue, alpha)
              .endVertex();
      renderer
              .pos(x + width, y + 0, 0.0D)
              .color(red, green, blue, alpha)
              .endVertex();
      Tessellator.getInstance().draw();
   }
   
}
