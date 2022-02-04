package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.ChunkPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.ArrayList;

public class ChunkFinder extends Module {

    public static ChunkFinder INSTANCE;

    private final ArrayList<ChunkPos> newChunks = new ArrayList<>();

    public ChunkFinder() {
        super("ChunkFinder", Category.RENDER);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        RenderUtil.enableGL3D(1.0F);
        for (ChunkPos data : newChunks) {
            RenderUtil.drawBoundingBox(new AxisAlignedBB(data.getXStart(), 0.0 - mc.renderManager.renderPosY, data.getZStart(), data.getXEnd(), 0.0 - mc.renderManager.renderPosY, data.getZEnd()), 1.0F, 0xFFFF0000);
        }
        RenderUtil.disableGL3D();
    }

    public void addChunk(ChunkPos chunk) {
        newChunks.add(chunk);
    }

}
