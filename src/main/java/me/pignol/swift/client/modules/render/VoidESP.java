package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.Colors;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.opengl.GL11;

import java.util.ArrayList;
import java.util.List;

public class VoidESP extends Module {

    private final Value<Integer> searchDelay = new Value<>("SearchDelay", 100, 0, 2000);
    private final Value<Float> range = new Value<>("Range", 6.0F, 0.0F, 12.0F);

    private final StopWatch timer = new StopWatch();

    private List<BlockPos> voidHoles = new ArrayList<>();

    public VoidESP() {
        super("VoidESP", Category.RENDER);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (timer.passed(searchDelay.getValue())) {
            voidHoles = getVoidHoles();
            timer.reset();
        }
    }

    private List<BlockPos> getVoidHoles() {
        List<BlockPos> holes = new ArrayList<>();
        final float radius = range.getValue();
        final int posX = (int) mc.player.posX;
        final int posZ = (int) mc.player.posZ;
        for (int x = posX - (int) radius; x <= posX + radius; ++x) {
            for (int z = posZ - (int) radius; z <= posZ + radius; ++z) {
                if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) < radius * radius) {
                    final Chunk chunk = mc.world.getChunk(x >> 4, z >> 4);
                    if (chunk.getBlockState(x, 0, z).getBlock() == Blocks.AIR) {
                        holes.add(new BlockPos(x, 0, z));
                    }
                }
            }
        }
        return holes;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent eventt) {
        RenderUtil.enableGL3D();
        for (int i = 0, size = voidHoles.size(); i < size; ++i) {
            BlockPos pos = voidHoles.get(i);
            GL11.glLineWidth(2.0F);
            RenderUtil.drawWireframeBox(RenderUtil.getRenderBB(pos), Colors.RED, true);
        }
        RenderUtil.disableGL3D();
    }

}
