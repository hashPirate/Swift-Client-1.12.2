package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.NoRenderModule;
import me.pignol.swift.client.modules.render.ViewmodelModule;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.ItemRenderer;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumHandSide;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ItemRenderer.class)
public abstract class MixinItemRenderer {

    @Final
    @Shadow
    public Minecraft mc;

    public boolean injection = true;

    @Shadow
    public abstract void renderItemInFirstPerson(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_);

    //This is made this way to work around futures mixin with higher priority
    @Inject(method = "renderItemInFirstPerson(Lnet/minecraft/client/entity/AbstractClientPlayer;FFLnet/minecraft/util/EnumHand;FLnet/minecraft/item/ItemStack;F)V", at = @At("HEAD"), cancellable = true)
    public void renderItemInFirstPersonHook(AbstractClientPlayer player, float p_187457_2_, float p_187457_3_, EnumHand hand, float p_187457_5_, ItemStack stack, float p_187457_7_, CallbackInfo info) {
        if (injection) {
            info.cancel();
            float xOffset = ViewmodelModule.INSTANCE.isEnabled() ? (hand == EnumHand.MAIN_HAND ? ViewmodelModule.INSTANCE.offsetX.getValue() : ViewmodelModule.INSTANCE.offsetXOff.getValue()) : 0;
            injection = false;
            ViewmodelModule.rendering = true;
            this.renderItemInFirstPerson(player, p_187457_2_, p_187457_3_, hand, p_187457_5_ + xOffset, stack, p_187457_7_);
            ViewmodelModule.rendering = false;
            injection = true;
        }
    }

    @Redirect(method = "updateEquippedItem", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;getCooledAttackStrength(F)F"))
    public float oldSwingHook(EntityPlayerSP instance, float v) {
        if (ViewmodelModule.INSTANCE.isEnabled() && ViewmodelModule.INSTANCE.oldSwing.getValue()) {
            return 1.0F;
        }
        return instance.getCooledAttackStrength(1.0F);
    }

    @Inject(method = "renderFireInFirstPerson", at = @At("HEAD"), cancellable = true)
    public void noEntityFireHook(CallbackInfo ci) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.entityFire.getValue()) {
            ci.cancel();
        }
    }

    @Redirect(method = "renderOverlays", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isEntityInsideOpaqueBlock()Z"))
    public boolean renderSuffocationOverlay(EntityPlayerSP instance) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.suffocation.getValue()) {
            return false;
        }
        return instance.isEntityInsideOpaqueBlock();
    }

    /**
     * @author
     */
    @Overwrite
    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded) {
        if (!heldStack.isEmpty()) {
            Item item = heldStack.getItem();
            Block block = Block.getBlockFromItem(item);
            GlStateManager.pushMatrix();
            boolean flag = mc.renderItem.shouldRenderItemIn3D(heldStack) && block.getRenderLayer() == BlockRenderLayer.TRANSLUCENT;

            if (flag) {
                GlStateManager.depthMask(false);
            }

            mc.renderItem.renderItem(heldStack, entitylivingbaseIn, transform, leftHanded);

            if (flag) {
                GlStateManager.depthMask(true);
            }

            GlStateManager.popMatrix();
        }
    }

    @Inject(method = "renderItemSide", at = @At("HEAD"))
    public void renderItemSide(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        boolean left = transform == ItemCameraTransforms.TransformType.FIRST_PERSON_LEFT_HAND;
        boolean right = transform == ItemCameraTransforms.TransformType.FIRST_PERSON_RIGHT_HAND;
        ViewmodelModule.rendering = true;
        if ((left || right) && ViewmodelModule.INSTANCE.isEnabled()) {
            GlStateManager.scale(ViewmodelModule.INSTANCE.scaleX.getValue(), ViewmodelModule.INSTANCE.scaleY.getValue(), ViewmodelModule.INSTANCE.scaleZ.getValue());
            /*GlStateManager.rotate(ViewmodelModule.INSTANCE.rotateX.getValue(), 1, 0, 0);
            GlStateManager.rotate(ViewmodelModule.INSTANCE.rotateY.getValue(), 0, 1, 0);
            GlStateManager.rotate(ViewmodelModule.INSTANCE.rotateZ.getValue(), 0, 0, 1);*/
        }
    }

    @Inject(method = "renderItemSide", at = @At("TAIL"))
    public void renderItemSidetail(EntityLivingBase entitylivingbaseIn, ItemStack heldStack, ItemCameraTransforms.TransformType transform, boolean leftHanded, CallbackInfo ci) {
        ViewmodelModule.rendering = false;
    }

    @Inject(method = "transformFirstPerson", at = @At("HEAD"))
    public void transformFirstPerson(EnumHandSide hand, float p_187453_2_, CallbackInfo ci) {
        if (ViewmodelModule.INSTANCE.isEnabled()) {
            boolean left = hand == EnumHandSide.LEFT;
            if ((ViewmodelModule.INSTANCE.pause.getValue() && !ViewmodelModule.INSTANCE.pauseY.getValue()) || checkHand(left)) {
                GlStateManager.translate(ViewmodelModule.INSTANCE.translateX.getValue() * (left ? 1 : -1), ViewmodelModule.INSTANCE.translateY.getValue(), ViewmodelModule.INSTANCE.translateZ.getValue());
            }
            if (!ViewmodelModule.INSTANCE.pause.getValue() || checkHand(left)) {
                GlStateManager.translate(ViewmodelModule.INSTANCE.translateX.getValue() * (left ? 1 : -1), 0, ViewmodelModule.INSTANCE.translateZ.getValue());
            }
        }
    }

    private boolean checkHand(boolean left) {
        if (mc.player.isHandActive()) {
            return (mc.player.getActiveHand() != EnumHand.OFF_HAND || !left) && (mc.player.getActiveHand() != EnumHand.MAIN_HAND || left);
        }
        return true;
    }

}
