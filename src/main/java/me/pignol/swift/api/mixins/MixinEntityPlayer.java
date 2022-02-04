package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.TravelEvent;
import net.minecraft.entity.MoverType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityPlayer.class)
public abstract class MixinEntityPlayer extends MixinEntityLivingBase {

    @Inject(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/entity/player/EntityPlayer;playShoulderEntityAmbientSound(Lnet/minecraft/nbt/NBTTagCompound;)V"), cancellable = true)
    public void playAmbient(CallbackInfo ci) {
        ci.cancel();
    }

    @Inject(method = "travel", at = @At("HEAD"), cancellable = true)
    public void travel(float strafe, float vertical, float forward, CallbackInfo info) {
        TravelEvent event = new TravelEvent(strafe, vertical, forward);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            move(MoverType.SELF, motionX, motionY, motionZ);
            info.cancel();
        }
    }


}
