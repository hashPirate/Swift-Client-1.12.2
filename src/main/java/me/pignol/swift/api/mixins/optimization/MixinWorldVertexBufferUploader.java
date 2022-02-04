package me.pignol.swift.api.mixins.optimization;

import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.WorldVertexBufferUploader;
import net.minecraft.client.renderer.vertex.VertexFormat;
import net.minecraft.client.renderer.vertex.VertexFormatElement;
import org.lwjgl.opengl.GL11;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

import java.nio.ByteBuffer;
import java.util.List;

@Mixin(WorldVertexBufferUploader.class)
public class MixinWorldVertexBufferUploader {

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public void draw(BufferBuilder bufferBuilderIn) {
        final int vertexCount = bufferBuilderIn.getVertexCount();
        if (vertexCount > 0) {
            final VertexFormat vertexformat = bufferBuilderIn.getVertexFormat();
            final int i = vertexformat.getSize();
            final ByteBuffer bytebuffer = bufferBuilderIn.getByteBuffer();
            final List<VertexFormatElement> list = vertexformat.getElements();
            final int size = list.size();
            for (int j = 0; j < size; ++j) {
                bytebuffer.position(vertexformat.getOffset(j));
                list.get(j).getUsage().preDraw(vertexformat, j, i, bytebuffer);
            }

            GL11.glDrawArrays(bufferBuilderIn.getDrawMode(), 0, vertexCount);
            int i1 = 0;

            while (i1 < size) {
                list.get(i1).getUsage().postDraw(vertexformat, i1, i, bytebuffer);
                ++i1;
            }
        }

        bufferBuilderIn.reset();
    }

}
