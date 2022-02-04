package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.Colors;
import me.pignol.swift.api.util.objects.Hole;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.managers.HoleManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class HoleESPModule extends Module {

    public final Value<Integer> safeRed = new Value<>("SafeRed", 0, 0, 255);
    public final Value<Integer> safeGreen = new Value<>("SafeGreen", 255, 0, 255);
    public final Value<Integer> safeBlue = new Value<>("SafeBlue", 0, 0, 255);

    public final Value<Integer> unsafeRed = new Value<>("UnsafeRed", 255, 0, 255);
    public final Value<Integer> unsafeGreen = new Value<>("UnsafeGreen", 0, 0, 255);
    public final Value<Integer> unsafeBlue = new Value<>("UnsafeBlue", 0, 0, 255);

    public final Value<Integer> alpha = new Value<>("Alpha", 100, 0, 255);
    public final Value<Integer> height = new Value<>("Height", 100, 0, 255);
    public final Value<Float> width = new Value<>("Width", 1F, 0.1F, 3.0F);

    public final Value<Boolean> onlySafe = new Value<>("OnlySafe", false);
    public final Value<Boolean> down = new Value<>("Down", false);
    public final Value<Boolean> outline = new Value<>("Outline", false);
    public final Value<Boolean> wireframe = new Value<>("Wireframe", false);
    public final Value<Boolean> wireframeTop = new Value<>("WireframeTop", false, v -> wireframe.getValue());
    public final Value<Float> wireWidth = new Value<>("WireWidth", 1F, 0.1F, 3.0F, v -> wireframe.getValue());
    public final Value<Integer> wireAlpha = new Value<>("Alpha", 100, 0, 255);

    public HoleESPModule() {
        super("HoleESP", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        List<Hole> holes;
        synchronized (HoleManager.getInstance().getHoles()) {
            holes = HoleManager.getInstance().getHoles();
        }

        final int safeColor = Colors.toRGBA(safeRed.getValue(), safeGreen.getValue(), safeBlue.getValue(), alpha.getValue());
        final int unsafeColor = Colors.toRGBA(unsafeRed.getValue(), unsafeGreen.getValue(), unsafeBlue.getValue(), alpha.getValue());
        final float height = this.height.getValue() / 100.0F;

        RenderUtil.enableGL3D(width.getValue());
        for (int i = 0, size = holes.size(); i < size; i++) {
            final Hole hole = holes.get(i);
            final BlockPos pos = hole.getPos();
            final boolean safe = hole.isSafe();
            if (onlySafe.getValue() && !safe) {
                continue;
            }
            final AxisAlignedBB bb = new AxisAlignedBB(
                    pos.getX() - mc.getRenderManager().viewerPosX,
                    (pos.getY() - mc.getRenderManager().viewerPosY) - (down.getValue() ? 1 : 0),
                    pos.getZ() - mc.getRenderManager().viewerPosZ,
                    pos.getX() + 1 - mc.getRenderManager().viewerPosX,
                    (pos.getY() + height - mc.getRenderManager().viewerPosY) - (down.getValue() ? 1 : 0),
                    pos.getZ() + 1 - mc.getRenderManager().viewerPosZ
            );

            RenderUtil.drawFilledBox(bb, safe ? safeColor : unsafeColor);
            if (outline.getValue()) {
                RenderUtil.drawBoundingBox(bb, width.getValue(), safe ? safeColor : unsafeColor, 255);
            }
            if (wireframe.getValue()) {
                GlStateManager.glLineWidth(wireWidth.getValue());
                RenderUtil.drawWireframeBox(bb, safe ? safeColor : unsafeColor, wireframeTop.getValue());
            }
        }
        RenderUtil.disableGL3D();
    }

}
