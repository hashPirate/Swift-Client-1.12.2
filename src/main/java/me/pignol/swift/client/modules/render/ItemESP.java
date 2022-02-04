package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.culling.Frustum;
import net.minecraft.client.renderer.culling.ICamera;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.List;

public class ItemESP extends Module {

    public ItemESP() {
        super("ItemESP", Category.RENDER);
    }

    private final Value<Boolean> scaleing = new Value<>("Scale", false);
    private final Value<Float> factor = new Value<>("Factor", 0.5f, 0.1f, 1.0f);
    private final Value<Boolean> smartScale = new Value<>("SmartScale", false);
    private final Value<Float> size = new Value<>("Size", 6.4f, 0.1f, 20.0f);

    private final ICamera frustum = new Frustum();

    @SubscribeEvent
    public void onRender3D(final Render3DEvent event) {
        if (mc.getRenderViewEntity() == null) return;
        frustum.setPosition(mc.getRenderViewEntity().posX, mc.getRenderViewEntity().posY, mc.getRenderViewEntity().posZ);
        final List<Entity> entities = mc.world.loadedEntityList;
        final int size = entities.size();
        final float partialTicks = mc.getRenderPartialTicks();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GlStateManager.SourceFactor.SRC_ALPHA, GlStateManager.DestFactor.ONE_MINUS_SRC_ALPHA, GlStateManager.SourceFactor.ONE, GlStateManager.DestFactor.ZERO);
        GlStateManager.enableTexture2D();
        GlStateManager.disableDepth();

        for (int i = 0; i < size; i++) {
            final Entity entity = entities.get(i);
            if (entity instanceof EntityItem && frustum.isBoundingBoxInFrustum(entity.getRenderBoundingBox())) {
                double x = interpolate(entity.lastTickPosX, entity.posX, partialTicks) - mc.renderManager.renderPosX;
                double y = interpolate(entity.lastTickPosY, entity.posY, partialTicks) - mc.renderManager.renderPosY;
                double z = interpolate(entity.lastTickPosZ, entity.posZ, partialTicks) - mc.renderManager.renderPosZ;
                renderNameTag((EntityItem) entity, x, y, z, mc.getRenderPartialTicks());
            }
        }

        GlStateManager.enableDepth();
        GlStateManager.disableBlend();
    }

    private void renderNameTag(EntityItem item, double x, double y, double z, float delta) {
        Entity camera = mc.getRenderViewEntity();
        assert camera != null;
        double originalPositionX = camera.posX;
        double originalPositionY = camera.posY;
        double originalPositionZ = camera.posZ;
        camera.posX = interpolate(camera.prevPosX, camera.posX, delta);
        camera.posY = interpolate(camera.prevPosY, camera.posY, delta);
        camera.posZ = interpolate(camera.prevPosZ, camera.posZ, delta);

        String displayTag = item.getItem().getItem().getItemStackDisplayName(item.getItem()) + " x" + (item.getItem().stackSize);
        double distance = camera.getDistance(x + mc.getRenderManager().viewerPosX, y + mc.getRenderManager().viewerPosY, z + mc.getRenderManager().viewerPosZ);
        int width = FontManager.getInstance().getStringWidth(displayTag) / 2;

        if (distance <= 8 && smartScale.getValue()) {
            distance = 8;
        }

        double scale = (0.0018 + size.getValue() * (distance * factor.getValue())) / 1000.0;

        if (!scaleing.getValue()) {
            scale = size.getValue() / 100.0;
        }

        GlStateManager.pushMatrix();
        GlStateManager.translate((float) x, (float) y, (float) z);
        GlStateManager.rotate(-mc.getRenderManager().playerViewY, 0.0F, 1.0F, 0.0F);
        GlStateManager.rotate(mc.getRenderManager().playerViewX, mc.gameSettings.thirdPersonView == 2 ? -1.0F : 1.0F, 0.0F, 0.0F);
        GlStateManager.scale(-scale, -scale, scale);
        FontManager.getInstance().drawStringWithShadow(displayTag, -width, -(8), -1);
        GlStateManager.popMatrix();

        camera.posX = originalPositionX;
        camera.posY = originalPositionY;
        camera.posZ = originalPositionZ;
    }

    private double interpolate(double previous, double current, float delta) {
        return (previous + (current - previous) * delta);
    }

}
