package me.pignol.swift.api.mixins;

import com.google.common.base.Predicate;
import me.pignol.swift.api.util.CollectionUtil;
import me.pignol.swift.client.event.events.Render2DEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.player.ReachModule;
import me.pignol.swift.client.modules.render.*;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.client.renderer.EntityRenderer;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.entity.Entity;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.opengl.Display;
import org.lwjgl.util.glu.Project;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.List;

@Mixin(EntityRenderer.class)
public abstract class MixinEntityRenderer {

    @Shadow
    private boolean lightmapUpdateNeeded;

    @Final
    @Shadow
    private Minecraft mc;

    @Final
    @Shadow
    private int[] lightmapColors;

    @Final
    @Shadow
    private DynamicTexture lightmapTexture;


    private float fogColorRed;
    private float fogColorGreen;
    private float fogColorBlue;

    @Inject(method = "applyBobbing", at = @At("HEAD"), cancellable = true)
    public void applyBobbing(float f, CallbackInfo ci) {
        if (NoBob.getInstance().isEnabled()) {
            ci.cancel();
        }
    }

    @Inject(method = "updateCameraAndRender", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/gui/GuiIngame;renderGameOverlay(F)V"))
    public void renderGameOverlay(float partialTicks, long nanoTime, CallbackInfo ci) {
        MinecraftForge.EVENT_BUS.post(new Render2DEvent());
    }

    @Redirect(method = "getMouseOver", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/WorldClient;getEntitiesInAABBexcluding(Lnet/minecraft/entity/Entity;Lnet/minecraft/util/math/AxisAlignedBB;Lcom/google/common/base/Predicate;)Ljava/util/List;"))
    public List<?> getEntities(WorldClient instance, Entity entity, AxisAlignedBB axisAlignedBB, Predicate<Entity> predicate) {
        if (ReachModule.INSTANCE.isEnabled() && ReachModule.INSTANCE.entityTrace.getValue().cancelEntityTrace()) {
            return CollectionUtil.EMPTY_LIST;
        }
        return instance.getEntitiesInAABBexcluding(entity, axisAlignedBB, predicate);
    }

    @Inject(method = "renderHand", at = @At(value = "HEAD"))
    private void renderHand(float partialTicks, int pass, CallbackInfo ci) {
        if (Display.isActive() || Display.isVisible()) {
            MinecraftForge.EVENT_BUS.post(new Render3DEvent());
            GlStateManager.color(1F, 1F, 1F, 1F);
        }
    }

    @Inject(method = "isDrawBlockOutline", at = @At("HEAD"), cancellable = true)
    public void isDrawblockoutline(CallbackInfoReturnable<Boolean> cir) {
        if (BlockHighlight.INSTANCE.isEnabled()) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Redirect(method = "renderWorldPass", at = @At(value = "INVOKE", target = "Lorg/lwjgl/util/glu/Project;gluPerspective(FFFF)V"))
    private void onRenderWorldPass(float y, float ratio, float zNear, float zFar) {
        Project.gluPerspective(y, AspectRatio.INSTANCE.isEnabled() ? ((float)AspectRatio.INSTANCE.worldX.getValue() / (float)AspectRatio.INSTANCE.worldY.getValue()) : ratio, zNear, zFar);
    }

    @Inject(method = "updateLightmap", at = @At("HEAD"), cancellable = true)
    public void fullbrightHook(float partialTicks, CallbackInfo ci) {
        if (FullBrightModule.INSTANCE.isEnabled()) {
            if (this.lightmapUpdateNeeded) {
                this.mc.profiler.startSection("lightTex");
                World world = this.mc.world;
                if (world != null) {
                    for (int i = 0; i < 256; ++i) {
                        this.lightmapColors[i] = 0xFFFFFFFF;
                    }
                    this.lightmapTexture.updateDynamicTexture();
                    this.lightmapUpdateNeeded = false;
                    this.mc.profiler.endSection();
                }
            }
            ci.cancel();
        }
    }

    @ModifyVariable(method = "orientCamera", ordinal = 3, at = @At(value = "STORE", ordinal = 0), require = 1)
    private double changeCameraDistance(double var1) {
        return ViewClipModule.INSTANCE.isEnabled() ? ViewClipModule.INSTANCE.getRange() : var1;
    }

    @ModifyVariable(method = "orientCamera", ordinal = 7, at = @At(value = "STORE", ordinal = 0), require = 1)
    private double cameraClip(double var1) {
        return ViewClipModule.INSTANCE.isEnabled() ? ViewClipModule.INSTANCE.getRange() : var1;
    }

    @Inject(method = "displayItemActivation(Lnet/minecraft/item/ItemStack;)V", at = @At("HEAD"), cancellable = true)
    public void preDisplayItemActivation(ItemStack stack, CallbackInfo callbackInfo) {
        if (stack.getItem() == Items.TOTEM_OF_UNDYING && NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.totemAnimation.getValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "renderItemActivation(IIF)V", at = @At("HEAD"), cancellable = true)
    public void preRenderItemActivation(int a, int b, float c, CallbackInfo callbackInfo) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.totemAnimation.getValue()) {
            callbackInfo.cancel();
        }
    }

    @Inject(method = "hurtCameraEffect", at = @At("HEAD"), cancellable = true)
    public void noHurtcamHook(float entitylivingbase, CallbackInfo ci) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.hurtcam.getValue()) {
            ci.cancel();
        }
    }

    @Inject(method = "updateFogColor", at = @At("HEAD"), cancellable = true)
    public void updateFogColor(float partialTicks, CallbackInfo ci) {
        if (CustomSkyModule.INSTANCE.noFogUpdate.getValue()) {
            ci.cancel();
            fogColorRed = 1.0F;
            fogColorGreen = 1.0F;
            fogColorBlue = 1.0F;
        }
    }

    @Inject(method = "drawNameplate", at = @At("HEAD"), cancellable = true)
    private static void drawNameplate(FontRenderer fontRendererIn, String str, float x, float y, float z, int verticalShift, float viewerYaw, float viewerPitch, boolean isThirdPersonFrontal, boolean isSneaking, CallbackInfo ci) {
        if (NametagsModule.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

}
