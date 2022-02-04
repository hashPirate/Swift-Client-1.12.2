package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.NoArmorRender;
import net.minecraft.client.renderer.entity.layers.LayerArmorBase;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.inventory.EntityEquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(LayerArmorBase.class)
public class MixinLayerArmorBase {

    @Inject(method = "renderArmorLayer", at = @At("HEAD"), cancellable = true)
    public void renderArmorLayer(EntityLivingBase entityLivingBaseIn, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale, EntityEquipmentSlot slotIn, CallbackInfo ci) {
        if (NoArmorRender.getInstance().isEnabled()) {
            if (shouldCancel(slotIn)) {
                ci.cancel();
            }
        }
    }

    private boolean shouldCancel(EntityEquipmentSlot slot) {
        switch (slot) {
            case FEET:
                return NoArmorRender.getInstance().noJordans.getValue();
            case LEGS:
                return NoArmorRender.getInstance().noLegs.getValue();
            case CHEST:
                return NoArmorRender.getInstance().noChest.getValue();
            case HEAD:
                return NoArmorRender.getInstance().noHelmet.getValue();
            default:
                return false;
        }
    }

}
