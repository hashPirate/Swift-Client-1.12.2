package me.pignol.swift.api.mixins;

import net.minecraft.client.settings.KeyBinding;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(KeyBinding.class)
public class MixinKeyBinding {

    @Shadow
    public boolean pressed;

    //we need this so inventorymove actually works
    @Inject(method = "isKeyDown", at = @At("HEAD"), cancellable = true)
    public void isKeyDown(CallbackInfoReturnable<Boolean> cir) {
        cir.setReturnValue(pressed);
        cir.cancel();
    }

}
