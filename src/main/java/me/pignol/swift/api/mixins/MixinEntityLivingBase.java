package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.DeathEvent;
import me.pignol.swift.client.event.events.LiquidJumpEvent;
import me.pignol.swift.client.managers.SwitchManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.network.datasync.DataParameter;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityLivingBase.class)
public abstract class MixinEntityLivingBase extends MixinEntity {

    @Shadow
    @Final
    private static DataParameter<Float> HEALTH;

    @Inject(method = "notifyDataManagerChange", at = @At("HEAD"))
    public void notifyDataManagerChangeHook(DataParameter<?> key, CallbackInfo info) {
        if (key.equals(HEALTH)) {
            if (dataManager.get(HEALTH) <= 0.0) {
                MinecraftForge.EVENT_BUS.post(new DeathEvent(EntityLivingBase.class.cast(this)));
            }
        }
    }

    @Inject(method = "resetActiveHand", at = @At("HEAD"), cancellable = true)
    public void reset(CallbackInfo ci) {
        if (SwitchManager.getInstance().dontReset()) {
            ci.cancel();
        }
    }

    @Inject(method = "updateActiveHand", at = @At("HEAD"), cancellable = true)
    public void reset2(CallbackInfo ci) {
        if (SwitchManager.getInstance().dontReset()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleJumpWater", at = @At("HEAD"), cancellable=true)
    private void handleJumpWater(CallbackInfo ci) {
        LiquidJumpEvent event = new LiquidJumpEvent();
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

    @Inject(method = "handleJumpLava", at = @At("HEAD"), cancellable=true)
    private void handleJumpLava(CallbackInfo ci) {
        LiquidJumpEvent event = new LiquidJumpEvent();
        if (event.isCanceled()) {
            ci.cancel();
        }
    }

}
