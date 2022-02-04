package me.pignol.swift.api.mixins.optimization;

import me.pignol.swift.client.modules.render.FullBrightModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.chunk.Chunk;
import net.minecraft.world.chunk.storage.ExtendedBlockStorage;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import static net.minecraft.world.chunk.Chunk.NULL_BLOCK_STORAGE;

@Mixin(Chunk.class)
public class MixinChunk {

    @Final
    @Shadow
    private ExtendedBlockStorage[] storageArrays;

    @Inject(method = {"getLightFor", "getLightSubtracted"}, at = @At("HEAD"), cancellable = true)
    public void fullbright(CallbackInfoReturnable<Integer> cir) {
        if (FullBrightModule.INSTANCE.isEnabled()) {
            cir.setReturnValue(15);
        }
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public IBlockState getBlockState(final int x, final int y, final int z) {
        if (y >= 0 && y >> 4 < this.storageArrays.length) {
            final ExtendedBlockStorage extendedblockstorage = this.storageArrays[y >> 4];

            if (extendedblockstorage != NULL_BLOCK_STORAGE) {
                return extendedblockstorage.get(x & 15, y & 15, z & 15);
            }
        }

        return Blocks.AIR.getDefaultState();
    }


}
