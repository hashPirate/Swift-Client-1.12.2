package me.pignol.swift.api.mixins.optimization;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;

import java.util.List;

@Mixin(BufferBuilder.class)
public abstract class MixinBufferBuilder {

    @Shadow
    private VertexFormatElement vertexFormatElement;

    @Shadow
    private int vertexFormatIndex;

    @Shadow
    private VertexFormat vertexFormat;


    /**
     * @author pig
     */
    @Overwrite
    public void nextVertexFormatIndex() {
        final List<VertexFormatElement> elements = vertexFormat.getElements();
        final int size = elements.size();
        do {
            if (++vertexFormatIndex >= size) {
                vertexFormatIndex -= size;
            }

            vertexFormatElement = elements.get(vertexFormatIndex);
        } while (vertexFormatElement.getUsage() == VertexFormatElement.EnumUsage.PADDING);
    }


}
