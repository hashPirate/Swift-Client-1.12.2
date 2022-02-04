package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.render.RenderUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.Render3DEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.Vec3d;

import java.util.HashMap;
import java.util.Map;

public class PacketESP extends Module {

    private final Value<Boolean> chorus = new Value<>("Chorus", true);
    private final Value<Integer> time = new Value<>("Ms", 2000, 0, 5000);
    private final Value<Integer> startAlpha = new Value<>("StartAlpha", 150, 0, 255);


    private final static ResourceLocation CHORUS = new ResourceLocation("textures/items/chorus_fruit.png");

    private final Map<Vec3d, StopWatch> positions = new HashMap<>();


    public PacketESP() {
        super("PacketESP", Category.RENDER);
    }

    //@SubscribeEvent
    public void onRender2D(Render3DEvent event) {
        if (isNull()) {
            return;
        }

        RenderUtil.enableGL3D();

    }

   // @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            if (packet.getSound() == SoundEvents.ITEM_CHORUS_FRUIT_TELEPORT) {
                positions.put(new Vec3d(packet.getX(), packet.getY(), packet.getZ()), new StopWatch());
            }
        }
    }

}
