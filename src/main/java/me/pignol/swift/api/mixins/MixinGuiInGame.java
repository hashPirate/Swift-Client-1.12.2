package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.other.HudModule;
import net.minecraft.client.gui.GuiIngame;
import net.minecraft.client.gui.ScaledResolution;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngame.class)
public class MixinGuiInGame {

    @Inject(method = "renderPotionEffects", at = @At("HEAD"), cancellable = true)
    public void renderPotionEffects(ScaledResolution resolution, CallbackInfo ci) {
        if (HudModule.INSTANCE.hideEffects.getValue()) {
            ci.cancel();
        }
    }

}

