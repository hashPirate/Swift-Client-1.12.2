package me.pignol.swift.api.mixins;

import me.pignol.swift.Swift;
import me.pignol.swift.api.util.BetterScaledResolution;
import me.pignol.swift.client.event.events.InteractEvent;
import me.pignol.swift.client.event.events.KeyPressEvent;
import me.pignol.swift.client.managers.ModuleManager;
import me.pignol.swift.client.managers.SafetyManager;
import me.pignol.swift.client.modules.other.ManageModule;
import me.pignol.swift.client.modules.player.MultiTask;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.EnumActionResult;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.common.MinecraftForge;
import org.lwjgl.input.Keyboard;
import org.lwjgl.opengl.Display;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = Minecraft.class, priority = Integer.MAX_VALUE - 1)
public abstract class MixinMinecraft {

    @Shadow
    public WorldClient world;

    @Shadow
    public static Minecraft getMinecraft() {
        return null;
    }

    @Inject(method = "runTickKeyboard", at = @At(value = "INVOKE", remap = false, target = "Lorg/lwjgl/input/Keyboard;getEventKey()I", ordinal = 0, shift = At.Shift.BEFORE))
    private void onKeyboard(CallbackInfo callbackInfo) {
        final int i = Keyboard.getEventKey() == 0 ? Keyboard.getEventCharacter() + 256 : Keyboard.getEventKey();
        MinecraftForge.EVENT_BUS.post(new KeyPressEvent(i, Keyboard.getEventKeyState()));
    }

    @Inject(method = "loadWorld(Lnet/minecraft/client/multiplayer/WorldClient;Ljava/lang/String;)V", at = @At("HEAD"))
    private void clearLoadedMaps(WorldClient worldClientIn, String loadingMessage, CallbackInfo ci) {
        if (worldClientIn != world) {
            Minecraft.class.cast(this).entityRenderer.getMapItemRenderer().clearLoadedMaps();
        }
    }

    @Inject(method = "shutdownMinecraftApplet", at = @At("HEAD"))
    public void shutdownMinecraft(CallbackInfo ci) {
        Swift.getInstance().unload();
    }

    @Inject(method = "getLimitFramerate", at = @At("HEAD"), cancellable = true)
    public void getLimitFramerate(CallbackInfoReturnable<Integer> cir) {
        if (!Display.isActive()) {
            cir.setReturnValue(ManageModule.INSTANCE.tabbedFps.getValue());
        }
    }

    @Redirect(method = "rightClickMouse", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;processRightClickBlock(Lnet/minecraft/client/entity/EntityPlayerSP;Lnet/minecraft/client/multiplayer/WorldClient;Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/util/EnumFacing;Lnet/minecraft/util/math/Vec3d;Lnet/minecraft/util/EnumHand;)Lnet/minecraft/util/EnumActionResult;"))
    public EnumActionResult processRightclick(PlayerControllerMP instance, EntityPlayerSP ret, WorldClient iblockstate, BlockPos bypass, EnumFacing block, Vec3d enumactionresult, EnumHand i) {
        Minecraft mc = Minecraft.getMinecraft();
        if (MinecraftForge.EVENT_BUS.post(new InteractEvent(bypass))) {
            return EnumActionResult.FAIL;
        }
        return instance.processRightClickBlock(mc.player, mc.world, bypass, mc.objectMouseOver.sideHit, mc.objectMouseOver.hitVec, i);
    }

    @Inject(method = "runTick", at = @At("RETURN"))
    public void runTick(CallbackInfo callbackInfo) {
        BetterScaledResolution.getInstance().update(Minecraft.getMinecraft());
        try {
            if (Minecraft.getMinecraft().player == null) {
                return;
            }
            SafetyManager.getInstance().update();
            try {
                ModuleManager.getInstance().cyclicBarrier.await();
            }
            catch (Throwable t) {
            }
        }
        catch (Throwable t) {
            t.printStackTrace();
        }
    }

    @Redirect(method = "sendClickBlockToController", at = @At(value = "INVOKE", target = "Lnet/minecraft/client/entity/EntityPlayerSP;isHandActive()Z"))
    private boolean isHandActiveWrapper(EntityPlayerSP playerSP) {
        if (MultiTask.INSTANCE.isEnabled())
            return false;
        return playerSP.isHandActive();
    }

    @Redirect(method = "rightClickMouse" , at = @At(value = "INVOKE", target = "Lnet/minecraft/client/multiplayer/PlayerControllerMP;getIsHittingBlock()Z", ordinal = 0))
    private boolean isHittingBlockHook(PlayerControllerMP playerControllerMP) {
        if (MultiTask.INSTANCE.isEnabled())
            return false;
        return playerControllerMP.getIsHittingBlock();
    }

    /**
     * @author e
     * @reason  e
     */
    @Overwrite
    private void startTimerHackThread() {
    }

}
