package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.BetterScaledResolution;
import me.pignol.swift.api.util.Colors;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render2DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.Entity;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class CrosshairModule extends Module {

    public static final CrosshairModule INSTANCE = new CrosshairModule();

    private final Value<Boolean> debug = new Value<>("Debug", false);
    private final Value<Boolean> dynamic = new Value<>("Dynamic", true);
    private final Value<Boolean> outline = new Value<>("Outline", true);
    private final Value<Boolean> attackIndicator = new Value<>("AttackIndicator", true);
    private final Value<Boolean> indicatorOutline = new Value<>("IndicatorOutline", true);
    private final Value<Float> width = new Value<>("Width", 1.0f, 0.5f, 10.0f);
    private final Value<Float> gap = new Value<>("Gap", 3.0f, 0.5f, 10.0f);
    private final Value<Float> length = new Value<>("Length", 7.0f, 0.5f, 100.0f);
    private final Value<Float> dynamicGap = new Value<>("DynamicGap", 1.5f, 0.5f, 10.0f);
    private final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    private final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    private final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    private final Value<Integer> alpha = new Value<>("Alpha", 255, 0, 255);
    private final Value<Boolean> staticRainbow = new Value<>("LightnessCycle", false);

    private boolean outlinee; // chinese stuffs

    public CrosshairModule() {
        super("Crosshair", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender2D(Render2DEvent event) {
        if (debug.getValue()) {
            int l = BetterScaledResolution.getInstance().getScaledWidth();
            int i1 = BetterScaledResolution.getInstance().getScaledHeight();
            float partialTicks = mc.getRenderPartialTicks();
            GlStateManager.pushMatrix();
            GlStateManager.translate((float) (l / 2), (float) (i1 / 2), 0);
            Entity entity = mc.getRenderViewEntity();
            GlStateManager.rotate(entity.prevRotationPitch + (entity.rotationPitch - entity.prevRotationPitch) * partialTicks, -1.0F, 0.0F, 0.0F);
            GlStateManager.rotate(entity.prevRotationYaw + (entity.rotationYaw - entity.prevRotationYaw) * partialTicks, 0.0F, 1.0F, 0.0F);
            GlStateManager.scale(-1.0F, -1.0F, -1.0F);
            OpenGlHelper.renderDirections(10);
            GlStateManager.popMatrix();
            return;
        }

        final int color = staticRainbow.getValue() ? color(2, 100) : Colors.toRGBA(red.getValue(), green.getValue(), blue.getValue(), alpha.getValue());
        final ScaledResolution resolution = BetterScaledResolution.getInstance();
        final float middlex = resolution.getScaledWidth() / 2F;
        final float middley = resolution.getScaledHeight() / 2F;
        outlinee = outline.getValue();
        drawBordered(middlex - (width.getValue()), middley - (gap.getValue() + length.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middlex + (width.getValue()), middley - (gap.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), 0.5F, color, 0xff000000);
        drawBordered(middlex - (width.getValue()), middley + (gap.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middlex + (width.getValue()), middley + (gap.getValue() + length.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), 0.5F, color, 0xff000000);
        drawBordered(middlex - (gap.getValue() + length.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley - (width.getValue()), middlex - (gap.getValue()) - ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley + (width.getValue()), 0.5F, color, 0xff000000);
        drawBordered(middlex + (gap.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley - (width.getValue()), middlex + (gap.getValue() + length.getValue()) + ((isMoving() && dynamic.getValue()) ? dynamicGap.getValue() : 0), middley + (width.getValue()), 0.5F, color, 0xff000000);

        if (attackIndicator.getValue()) {
            GameSettings gamesettings = mc.gameSettings;

            outlinee = indicatorOutline.getValue();

            if (gamesettings.thirdPersonView == 0) {
                if (mc.playerController.isSpectator() && mc.pointedEntity == null) {
                    RayTraceResult raytraceresult = mc.objectMouseOver;

                    if (raytraceresult == null || raytraceresult.typeOfHit != RayTraceResult.Type.BLOCK) {
                        return;
                    }

                    BlockPos blockpos = raytraceresult.getBlockPos();

                    net.minecraft.block.state.IBlockState state = mc.world.getBlockState(blockpos);
                    if (!state.getBlock().hasTileEntity(state) || !(mc.world.getTileEntity(blockpos) instanceof IInventory)) {
                        return;
                    }
                }

                int l = resolution.getScaledWidth();
                int i1 = resolution.getScaledHeight();

                GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.ONE_MINUS_DST_COLOR, GlStateManager.DestFactor.ONE_MINUS_SRC_COLOR, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
                GlStateManager.enableAlpha();

                if (mc.gameSettings.attackIndicator == 1) {
                    float f = mc.player.getCooledAttackStrength(0.0F);

                    int i = i1 / 2 - 7 + 16;
                    int j = l / 2 - 8;

                    if (f < 1.0F) {
                        int k = (int) (f * 17.0F);
                        drawBordered(j, i, j + 16, i + 4, 0.5F, 0x00000000, 0xFF000000);
                        drawBordered(j, i, j + k, i + 4, 0.5F, color, 0xFF000000);
                    }
                }
            }
        }
    }


    public void drawBordered(float x, float y, float x2, float y2, float thickness, int inside, int outline) {
        float fix = 0.0f;
        if (thickness < 1.0f) {
            fix = 1.0f;
        }
        RenderUtil.drawRect(x + thickness, y + thickness, x2 - thickness, y2 - thickness, inside);
        if (outlinee) {
            RenderUtil.drawRect(x, y + 1.0f - fix, x + thickness, y2, outline);
            RenderUtil.drawRect(x, y, x2 - 1.0f + fix, y + thickness, outline);
            RenderUtil.drawRect(x2 - thickness, y, x2, y2 - 1.0f + fix, outline);
            RenderUtil.drawRect(x + 1.0f - fix, y2 - thickness, x2, y2, outline);
        }
    }

    public boolean isMoving() {
        return mc.player.moveForward != 0 || mc.player.moveStrafing != 0;
    }

    public int color(int index, int count) {
        float[] hsb = new float[3];
        Color.RGBtoHSB(red.getValue(),green.getValue(),blue.getValue(), hsb);

        float brightness = Math.abs(((getOffset() + (index / (float) count) * 2) % 2) - 1);
        brightness = 0.4f + (0.4f * brightness);

        hsb[2] = brightness % 1f;
        Color clr = new Color(Color.HSBtoRGB(hsb[0],hsb[1], hsb[2]));
        return new Color(clr.getRed(),clr.getGreen(),clr.getBlue(),alpha.getValue()).getRGB();
    }

    private static float getOffset() {
        return (System.currentTimeMillis() % 2000) / 1000f;
    }

}
