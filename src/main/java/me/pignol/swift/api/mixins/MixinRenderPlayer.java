package me.pignol.swift.api.mixins;

import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.modules.misc.NameProtectModule;
import me.pignol.swift.client.modules.render.HeadRotations;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.util.ResourceLocation;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderPlayer.class)
public class MixinRenderPlayer {

    @Final
    @Shadow
    private boolean smallArms;

    private static final ResourceLocation STEVE = new ResourceLocation("textures/entity/steve.png");
    private static final ResourceLocation ALEX = new ResourceLocation("textures/entity/alex.png");

    private float
            renderPitch,
            renderYaw,
            renderHeadYaw,
            prevRenderHeadYaw,
            lastRenderHeadYaw = 0,
            prevRenderPitch,
            lastRenderPitch = 0;

    @Inject(method = "doRender", at = @At("HEAD"))
    private void rotateBegin(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (HeadRotations.INSTANCE.shouldRotate() && entity == Minecraft.getMinecraft().player) {
            prevRenderHeadYaw = entity.prevRotationYawHead;
            prevRenderPitch = entity.prevRotationPitch;
            renderPitch = entity.rotationPitch;
            renderYaw = entity.rotationYaw;
            renderHeadYaw = entity.rotationYawHead;
            entity.rotationPitch = RotationManager.getInstance().getPitch();
            entity.prevRotationPitch = lastRenderPitch;
            entity.rotationYaw = RotationManager.getInstance().getYaw();
            entity.rotationYawHead = RotationManager.getInstance().getYaw();
            entity.prevRotationYawHead = lastRenderHeadYaw;
        }
    }

    @Inject(method = "doRender", at = @At("RETURN"))
    private void rotateEnd(AbstractClientPlayer entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (HeadRotations.INSTANCE.shouldRotate() && entity == Minecraft.getMinecraft().player) {
            lastRenderHeadYaw = entity.rotationYawHead;
            lastRenderPitch = entity.rotationPitch;
            entity.rotationPitch = renderPitch;
            entity.rotationYaw = renderYaw;
            entity.rotationYawHead = renderHeadYaw;
            entity.prevRotationYawHead = prevRenderHeadYaw;
            entity.prevRotationPitch = prevRenderPitch;
        }
    }


    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public ResourceLocation getEntityTexture(AbstractClientPlayer entity) {
        if (entity == Minecraft.getMinecraft().player && NameProtectModule.INSTANCE.isEnabled() && NameProtectModule.INSTANCE.getFakeSkin()) {
            return smallArms ? ALEX : STEVE;
        }
        return entity.getLocationSkin();
    }


}
