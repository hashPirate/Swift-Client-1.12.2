package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.ColorUtil;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityExpBottle;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityESP extends Module {

    private final Value<Integer> fillAlpha = new Value<>("FillAlpha", 50, 0, 255);
    private final Value<Integer> outlineAlpha = new Value<>("OutlineAlpha", 50, 0, 255);
    private final Value<Float> lineWidth = new Value<>("Width", 1.0F, 0.1F, 5.0F, 0.1F);

    public EntityESP() {
        super("EntityESP", Category.RENDER);
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        RenderUtil.enableGL3D(lineWidth.getValue());
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityExpBottle) {
                if (fillAlpha.getValue() > 0) {
                    RenderUtil.drawFilledBox(new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX, entity.getEntityBoundingBox().minY - 0.0 - entity.posY, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ), ColorUtil.changeAlpha(ColorsModule.INSTANCE.getColor(), fillAlpha.getValue()));
                }
                if (outlineAlpha.getValue() > 0) {
                    RenderUtil.drawBoundingBox(new AxisAlignedBB(entity.getEntityBoundingBox().minX - 0.05 - entity.posX, entity.getEntityBoundingBox().minY - 0.0 - entity.posY, entity.getEntityBoundingBox().minZ - 0.05 - entity.posZ, entity.getEntityBoundingBox().maxX + 0.05 - entity.posX, entity.getEntityBoundingBox().maxY + 0.1 - entity.posY, entity.getEntityBoundingBox().maxZ + 0.05 - entity.posZ), lineWidth.getValue(), ColorUtil.changeAlpha(ColorsModule.INSTANCE.getColor(), outlineAlpha.getValue()));
                }
            }
        }
        RenderUtil.disableGL3D();
    }

}
