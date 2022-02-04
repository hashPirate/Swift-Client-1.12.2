package me.pignol.swift.client.modules.misc;

import it.unimi.dsi.fastutil.objects.Object2LongMap;
import it.unimi.dsi.fastutil.objects.Object2LongOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.other.ColorsModule;
import net.minecraft.client.renderer.BufferBuilder;
import net.minecraft.client.renderer.Tessellator;
import net.minecraft.client.renderer.vertex.DefaultVertexFormats;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderPearl;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.*;

public class PearlTraceModule extends Module {

    private final Value<Boolean> message = new Value<>("Message", true);
    private final Value<Integer> renderTime = new Value<>("RenderTimeMS", 500, 0, 10000);

    private final Object2ObjectOpenHashMap<UUID, List<Vec3d>> positions = new Object2ObjectOpenHashMap<>();
    private final Object2LongOpenHashMap<UUID> time = new Object2LongOpenHashMap<>();

    public PearlTraceModule() {
        super("PearlTrace", Category.RENDER);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        UUID removable = null;
        for (Object2LongMap.Entry<UUID> entry : time.object2LongEntrySet()) {
            long passed = System.currentTimeMillis() - entry.getLongValue();
            if (passed > renderTime.getValue()) {
                removable = entry.getKey();
                break;
            }
        }

        if (removable != null) {
            positions.remove(removable);
            time.removeLong(removable);
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderPearl) {
                UUID uuid = entity.getUniqueID();
                if (!positions.containsKey(uuid)) {
                    if (message.getValue()) {
                        for (EntityPlayer player : mc.world.playerEntities) {
                            if (player != mc.player && player.getDistanceSq(entity) < 4) {
                                ChatUtil.sendMessage(player.getName() + " threw a pearl facing " + entity.getHorizontalFacing());
                                break;
                            }
                        }
                    }
                    positions.put(uuid, new ArrayList<>(Collections.singletonList(entity.getPositionVector())));
                    time.put(uuid, System.currentTimeMillis());
                } else {
                    time.replace(uuid, System.currentTimeMillis());
                    List<Vec3d> vec3ds = positions.get(uuid);
                    vec3ds.add(entity.getPositionVector());
                }
            }
        }
    }

    @SubscribeEvent
    public void onRender3D(Render3DEvent event) {
        if (isNull()) {
            return;
        }
        final double x = mc.renderManager.renderPosX;
        final double y = mc.renderManager.renderPosY;
        final double z = mc.renderManager.renderPosZ;
        final int color = ColorsModule.INSTANCE.getColor();
        float red = (color >> 16 & 0xFF) / 255.0F;
        float green = (color >> 8 & 0xFF) / 255.0F;
        float blue = (color & 0xFF) / 255.0F;
        final Tessellator tessellator = Tessellator.getInstance();
        final BufferBuilder buffer = tessellator.getBuffer();
        RenderUtil.enableGL3D();
        buffer.begin(1, DefaultVertexFormats.POSITION_COLOR);
        for (final Map.Entry<UUID, List<Vec3d>> entry : this.positions.entrySet()) {
            final List<Vec3d> vec3ds = entry.getValue();
            if (vec3ds.size() <= 2) {
                continue;
            }
            final int size = vec3ds.size();
            for (int i = 1; i < size; ++i) {
                final Vec3d vec = vec3ds.get(i);
                final Vec3d previousVec = vec3ds.get(i - 1);
                buffer.pos(vec.x - x, vec.y - y, vec.z - z).color(red, green, blue, 1.0F).endVertex();
                buffer.pos(previousVec.x - x, previousVec.y - y, previousVec.z - z).color(red, green, blue, 1.0F).endVertex();
            }
        }
        tessellator.draw();
        RenderUtil.disableGL3D();
    }

}
