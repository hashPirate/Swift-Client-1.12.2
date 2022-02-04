package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.client.gui.GuiBossOverlay;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GuiBossOverlay.class)
public class MixinGuiBossOverlay {

    @Inject(method = "renderBossHealth", at = @At("HEAD"), cancellable = true)
    public void renderBossHealth(CallbackInfo ci) {
        if (NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.bossbar.getValue()) {
            ci.cancel();
        }
    }

}
