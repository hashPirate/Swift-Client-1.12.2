package me.pignol.swift.api.mixins;

import me.pignol.swift.api.util.CapeUtil;
import me.pignol.swift.client.modules.other.HudModule;
import me.pignol.swift.client.modules.render.NoInterpolationModule;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerCape;
import net.minecraft.entity.player.EnumPlayerModelParts;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(LayerCape.class)
public class MixinLayerCape {

    @Final
    @Shadow
    private RenderPlayer playerRenderer;

    /**
     * @author computer
     */
    @Overwrite
    public void doRenderLayer(AbstractClientPlayer entitylivingbaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
        if (entitylivingbaseIn.hasPlayerInfo()) {
            boolean caped = HudModule.INSTANCE.capes.getValue() && CapeUtil.CAPED_USERS.containsKey(entitylivingbaseIn.getUniqueID());
            if (!entitylivingbaseIn.isInvisible() && (caped || (entitylivingbaseIn.isWearing(EnumPlayerModelParts.CAPE) && entitylivingbaseIn.getLocationCape() != null))) {
                ItemStack itemstack = entitylivingbaseIn.getItemStackFromSlot(EntityEquipmentSlot.CHEST);

                if (itemstack.getItem() != Items.ELYTRA) {
                    GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                    this.playerRenderer.bindTexture(caped ? CapeUtil.CAPED_USERS.get(entitylivingbaseIn.getUniqueID()) : entitylivingbaseIn.getLocationCape());
                    GlStateManager.pushMatrix();
                    GlStateManager.translate(0.0F, 0.0F, 0.125F);
                    boolean noInterp = NoInterpolationModule.INSTANCE.isEnabled() && NoInterpolationModule.INSTANCE.capes.getValue();
                    double interpolatedX = noInterp ? (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.posX) : (entitylivingbaseIn.prevChasingPosX + (entitylivingbaseIn.chasingPosX - entitylivingbaseIn.prevChasingPosX) * (double) partialTicks - (entitylivingbaseIn.prevPosX + (entitylivingbaseIn.posX - entitylivingbaseIn.prevPosX) * (double) partialTicks));
                    double interpolatedY = noInterp ? (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.posY) : (entitylivingbaseIn.prevChasingPosY + (entitylivingbaseIn.chasingPosY - entitylivingbaseIn.prevChasingPosY) * (double) partialTicks - (entitylivingbaseIn.prevPosY + (entitylivingbaseIn.posY - entitylivingbaseIn.prevPosY) * (double) partialTicks));
                    double interpolatedZ = noInterp ? (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.posZ) : (entitylivingbaseIn.prevChasingPosZ + (entitylivingbaseIn.chasingPosZ - entitylivingbaseIn.prevChasingPosZ) * (double) partialTicks - (entitylivingbaseIn.prevPosZ + (entitylivingbaseIn.posZ - entitylivingbaseIn.prevPosZ) * (double) partialTicks));

                    float renderYawOffset = entitylivingbaseIn.prevRenderYawOffset + (entitylivingbaseIn.renderYawOffset - entitylivingbaseIn.prevRenderYawOffset) * partialTicks;
                    double d3 = MathHelper.sin(renderYawOffset * 0.017453292F);
                    double d4 = -MathHelper.cos(renderYawOffset * 0.017453292F);
                    float f1 = (float) interpolatedY * 10.0F;
                    f1 = MathHelper.clamp(f1, -6.0F, 32.0F);
                    float f2 = (float) (interpolatedX * d3 + interpolatedZ * d4) * 100.0F;
                    float f3 = (float) (interpolatedX * d4 - interpolatedZ * d3) * 100.0F;

                    if (f2 < 0.0F) {
                        f2 = 0.0F;
                    }

                    float f4 = entitylivingbaseIn.prevCameraYaw + (entitylivingbaseIn.cameraYaw - entitylivingbaseIn.prevCameraYaw) * partialTicks;
                    f1 = f1 + MathHelper.sin((entitylivingbaseIn.prevDistanceWalkedModified + (entitylivingbaseIn.distanceWalkedModified - entitylivingbaseIn.prevDistanceWalkedModified) * partialTicks) * 6.0F) * 32.0F * f4;

                    if (entitylivingbaseIn.isSneaking()) {
                        f1 += 25.0F;
                    }

                    GlStateManager.rotate(6.0F + f2 / 2.0F + f1, 1.0F, 0.0F, 0.0F);
                    GlStateManager.rotate(f3 / 2.0F, 0.0F, 0.0F, 1.0F);
                    GlStateManager.rotate(-f3 / 2.0F, 0.0F, 1.0F, 0.0F);
                    GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
                    this.playerRenderer.getMainModel().renderCape(0.0625F);
                    GlStateManager.popMatrix();
                }
            }
        }
    }

}
