package me.pignol.swift.api.mixins;

import me.pignol.swift.client.event.events.ClickBlockEvent;
import me.pignol.swift.client.event.events.DamageBlockEvent;
import me.pignol.swift.client.modules.player.ReachModule;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.MinecraftForge;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerControllerMP.class)
public class MixinPlayerControllerMP {

    @Inject(method = "clickBlock", at = @At("HEAD"), cancellable = true)
    public void onClickBlock(BlockPos pos, EnumFacing facing, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftForge.EVENT_BUS.post(new ClickBlockEvent(pos, facing))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "onPlayerDamageBlock", at = @At("HEAD"), cancellable = true)
    public void onDamageBlock(BlockPos posBlock, EnumFacing directionFacing, CallbackInfoReturnable<Boolean> cir) {
        if (MinecraftForge.EVENT_BUS.post(new DamageBlockEvent(posBlock, directionFacing))) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

    @Inject(method = "getBlockReachDistance", at = @At("RETURN"), cancellable = true)
    public void reach(CallbackInfoReturnable<Float> cir) {
        if (ReachModule.INSTANCE.isEnabled()) {
            cir.setReturnValue(cir.getReturnValueF() + ReachModule.INSTANCE.reachAdd.getValue());
        }
    }

}
