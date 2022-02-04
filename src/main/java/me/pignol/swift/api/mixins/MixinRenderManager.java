package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.NoInterpolationModule;
import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderManager.class)
public abstract class MixinRenderManager {

    @Shadow
    public double renderPosX;

    @Shadow
    public double renderPosY;

    @Shadow
    public double renderPosZ;

    @Shadow
    public abstract void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_);

    @Inject(method = "renderEntity", at = @At("HEAD"), cancellable = true)
    public void renderEntity(Entity entityIn, double x, double y, double z, float yaw, float partialTicks, boolean p_188391_10_, CallbackInfo ci) {
        if (entityIn instanceof EntityPig && NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.pigs.getValue()) {
            ci.cancel();
        }
    }

    /**
     * @author e
     */
    @Overwrite
    public void renderEntityStatic(Entity entityIn, float partialTicks, boolean p_188388_3_) {
        if (entityIn.ticksExisted == 0) {
            entityIn.lastTickPosX = entityIn.posX;
            entityIn.lastTickPosY = entityIn.posY;
            entityIn.lastTickPosZ = entityIn.posZ;
        }

        double d0;
        double d1;
        double d2;

        if (NoInterpolationModule.INSTANCE.isEnabled()) {
            d0 = entityIn.posX;
            d1 = entityIn.posY;
            d2 = entityIn.posZ;
        } else {
            d0 = entityIn.lastTickPosX + (entityIn.posX - entityIn.lastTickPosX) * (double) partialTicks;
            d1 = entityIn.lastTickPosY + (entityIn.posY - entityIn.lastTickPosY) * (double) partialTicks;
            d2 = entityIn.lastTickPosZ + (entityIn.posZ - entityIn.lastTickPosZ) * (double) partialTicks;
        }

        float f = entityIn.prevRotationYaw + (entityIn.rotationYaw - entityIn.prevRotationYaw) * partialTicks;

        int i = entityIn.getBrightnessForRender();

        if (entityIn.isBurning()) {
            i = 15728880;
        }

        int j = i % 65536;
        int k = i / 65536;
        OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, (float) j, (float) k);
        GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
        this.renderEntity(entityIn, d0 - this.renderPosX, d1 - this.renderPosY, d2 - this.renderPosZ, f, partialTicks, p_188388_3_);
    }

}
