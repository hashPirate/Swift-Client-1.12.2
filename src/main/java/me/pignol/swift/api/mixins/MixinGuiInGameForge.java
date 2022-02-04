package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.CrosshairModule;
import net.minecraftforge.client.GuiIngameForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiIngameForge.class)
public class MixinGuiInGameForge {

    @Inject(method = "renderCrosshairs", at = @At("HEAD"), cancellable = true, remap = false)
    public void renderCrosshairs(float partialTicks, CallbackInfo ci) {
        if (CrosshairModule.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

}
