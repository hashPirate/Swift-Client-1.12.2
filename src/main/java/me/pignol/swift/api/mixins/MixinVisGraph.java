package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.movement.PacketFly;
import me.pignol.swift.client.modules.render.Freecam;
import net.minecraft.client.renderer.chunk.VisGraph;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(VisGraph.class)
public class MixinVisGraph {

    @Inject(method = "setOpaqueCube", at = @At("HEAD"), cancellable = true)
    public void setOpaqueCube(BlockPos pos, CallbackInfo ci) {
        if (PacketFly.INSTANCE.isEnabled() || Freecam.INSTANCE.isEnabled()) {
            ci.cancel();
        }
    }

}
