package me.pignol.swift.api.mixins;

import com.mojang.authlib.GameProfile;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.MoveEvent;
import me.pignol.swift.client.event.events.PushEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.CommandManager;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.managers.SpeedManager;
import me.pignol.swift.client.managers.SwitchManager;
import me.pignol.swift.client.modules.movement.NoSlowModule;
import me.pignol.swift.client.modules.movement.SprintModule;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.AbstractClientPlayer;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.entity.MoverType;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EntityPlayerSP.class)
public abstract class MixinEntityPlayerSP extends AbstractClientPlayer {

    @Shadow
    protected Minecraft mc;

    public MixinEntityPlayerSP(World worldIn, GameProfile playerProfile) {
        super(worldIn, playerProfile);
    }

    @Inject(method = "resetActiveHand", at = @At("HEAD"), cancellable = true)
    public void reset(CallbackInfo ci) {
        if (SwitchManager.getInstance().dontReset()) {
            ci.cancel();
        }
    }

    @Redirect(method = "move", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/AbstractClientPlayer;move(Lnet/minecraft/entity/MoverType;DDD)V"))
    public void move(AbstractClientPlayer abstractClientPlayer, MoverType type, double x, double y, double z) {
        final MoveEvent event = new MoveEvent(x, y, z);
        MinecraftForge.EVENT_BUS.post(event);
        if (!event.isCanceled()) {
            super.move(type, event.getX(), event.getY(), event.getZ());
        }
    }

    @Inject(method = "onUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;onUpdateWalkingPlayer()V", shift = At.Shift.BEFORE), cancellable = true)
    public void walkingPre(CallbackInfo ci) {
        if (mc.player == null || mc.world == null)
            return;
        RotationManager.getInstance().updateRotations();
        if (MinecraftForge.EVENT_BUS.post(new UpdateEvent(Stage.PRE))) {
            ci.cancel();
        }
        SpeedManager.getInstance().update();
    }

    @Inject(method = "onUpdateWalkingPlayer", at = @At("RETURN"))
    public void walkingPost(CallbackInfo ci) {
        if (mc.player == null || mc.world == null)
            return;
        MinecraftForge.EVENT_BUS.post(new UpdateEvent(Stage.POST));
        if (!ManageModule.INSTANCE.debugRotations.getValue()) {
            RotationManager.getInstance().restoreRotations();
        }
        SpeedManager.getInstance().update();
    }

    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z", opcode = 0))
    public boolean noSlowHook(EntityPlayerSP instance) {
        if (NoSlowModule.INSTANCE.isEnabled()) {
            return false;
        }
        return instance.isHandActive();
    }

    @Inject(method = "pushOutOfBlocks", at = @At("HEAD"), cancellable = true)
    public void noPushHook(double d2, double f, double blockpos, CallbackInfoReturnable<Boolean> cir) {
        PushEvent event = new PushEvent(PushEvent.Type.BLOCK);
        MinecraftForge.EVENT_BUS.post(event);
        if (event.isCanceled()) {
            cir.cancel();
        }
    }

    @Inject(method = "sendChatMessage", at = @At("HEAD"), cancellable = true)
    public void sendChatMessage(String message, CallbackInfo ci) {
        if (message.startsWith(CommandManager.getInstance().getPrefix())) {
            ci.cancel();
            CommandManager.getInstance().onMessage(message);
        }
    }


    @Redirect(method = "onLivingUpdate", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;setSprinting(Z)V", ordinal = 2))
    public void onLivingUpdate(EntityPlayerSP entityPlayerSP, boolean sprinting) {
        if (SprintModule.INSTANCE.isEnabled() && SprintModule.mode.getValue() == SprintModule.Mode.RAGE && (mc.player.movementInput.moveForward != 0.0f || mc.player.movementInput.moveStrafe != 0.0f)) {
            entityPlayerSP.setSprinting(true);
        } else {
            entityPlayerSP.setSprinting(sprinting);
        }
    }

}
