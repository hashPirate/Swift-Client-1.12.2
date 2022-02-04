package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.ToastEvent;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.gui.toasts.GuiToast;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiToast.class)
public class MixinGuiToast {

    @Inject(method = "drawToast", at = @At("HEAD"), cancellable = true)
    public void drawToast(ScaledResolution resolution, CallbackInfo ci) {
        if (MinecraftForge.EVENT_BUS.post(new ToastEvent())) {
            ci.cancel();
        }
    }

}
