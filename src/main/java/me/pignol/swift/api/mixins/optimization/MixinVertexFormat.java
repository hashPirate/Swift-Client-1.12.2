package me.pignol.swift.api.mixins.optimization;

import it.unimi.dsi.fastutil.ints.IntArrayList;
import net.minecraft.client.renderer.vertex.VertexFormat;
import org.spongepowered.asm.mixin.*;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.List;

@Mixin(VertexFormat.class)
public class MixinVertexFormat {

    @Shadow
    @Final
    @Mutable
    private List<Integer> offsets;

    @Inject(method = "<init>()V", at = @At("RETURN"))
    public void reinit(CallbackInfo ci) {
        offsets = new IntArrayList();
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public int getOffset(int index) {
        return ((IntArrayList)offsets).getInt(index);
    }

}
