package me.pignol.swift.api.mixins;

import me.pignol.swift.client.modules.render.NoRenderModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockRendererDispatcher.class)
public class MixinBlockRendererDispatcher {

    @Inject(method = "renderBlock", at = @At("HEAD"), cancellable = true)
    public void renderBlock(IBlockState state, BlockPos pos, IBlockAccess blockAccess, BufferBuilder bufferBuilderIn, CallbackInfoReturnable<Boolean> cir) {
        if (state.getBlock() == Blocks.VINE && NoRenderModule.INSTANCE.isEnabled() && NoRenderModule.INSTANCE.vines.getValue() && pos.getY() > 8) {
            cir.setReturnValue(false);
            cir.cancel();
        }
    }

}
