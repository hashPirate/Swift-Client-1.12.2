package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BlockHighlight extends Module {

    public static BlockHighlight INSTANCE;

    private final Value<Float> lineWidth = new Value<>("LineWidth", 1.0F, 0.1F, 4.0F);

    public BlockHighlight() {
        super("BlockHighlight", Category.RENDER);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (mc.objectMouseOver != null && mc.objectMouseOver.typeOfHit == RayTraceResult.Type.BLOCK) {
            BlockPos pos = mc.objectMouseOver.getBlockPos();
            if (pos != null) {
                Entity player = mc.getRenderViewEntity();
                if (player != null) {
                    IBlockState state = mc.world.getBlockState(pos);
                    AxisAlignedBB bounding = state.getSelectedBoundingBox(mc.world, pos);
                    float partialTicks = mc.getRenderPartialTicks();
                    double x = player.lastTickPosX + (player.posX - player.lastTickPosX) * (double) partialTicks;
                    double y = player.lastTickPosY + (player.posY - player.lastTickPosY) * (double) partialTicks;
                    double z = player.lastTickPosZ + (player.posZ - player.lastTickPosZ) * (double) partialTicks;
                    RenderUtil.enableGL3D();
                    RenderUtil.drawBoundingBox(bounding.offset(-x, -y, -z), lineWidth.getValue(), ColorsModule.INSTANCE.getColor());
                    RenderUtil.disableGL3D();
                }
            }
        }
    }

}
