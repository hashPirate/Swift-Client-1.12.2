package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.client.renderer.entity.RenderItemFrame;
import net.minecraft.entity.item.EntityItemFrame;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(RenderItemFrame.class)
public class MixinRenderItemFrame {

    @Inject(method = "doRender", at = @At("HEAD"), cancellable = true)
    public void doRender(EntityItemFrame entity, double x, double y, double z, float entityYaw, float partialTicks, CallbackInfo ci) {
        if (NoRenderModule.INSTANCE.isEnabled()  && NoRenderModule.INSTANCE.itemframes.getValue()) {
            ci.cancel();
        }
    }

}
