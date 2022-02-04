package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.EntitySteerEvent;
import me.pignol.swift.client.event.events.SaddleEvent;
import net.minecraft.entity.passive.AbstractHorse;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(AbstractHorse.class)
public class MixinAbstractHorse {

    @Inject(method = "isHorseSaddled", at = @At("HEAD"), cancellable = true)
    public void isHorseSaddled(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftForge.EVENT_BUS.post(new SaddleEvent())) {
            cir.setReturnValue(true);
        }
    }

    @Inject(method = "canBeSteered", at = @At("HEAD"), cancellable = true)
    public void canBeSteered(CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftForge.EVENT_BUS.post(new EntitySteerEvent())) {
            cir.setReturnValue(true);
        }
    }


}
