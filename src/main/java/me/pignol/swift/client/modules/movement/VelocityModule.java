package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.PushEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.server.SPacketEntityVelocity;
import net.minecraft.network.play.server.SPacketExplosion;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class VelocityModule extends Module {

    public static final VelocityModule INSTANCE = new VelocityModule();

    public final Value<Boolean> noEntityPush = new Value<>("NoEntityPush", true);
    public final Value<Boolean> noBlockPush = new Value<>("NoBlockPush", true);
    public final Value<Boolean> noPistonPush = new Value<>("NoPistonPush", true);

    public VelocityModule() {
        super("Velocity", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketExplosion || event.getPacket() instanceof SPacketEntityVelocity && ((SPacketEntityVelocity) event.getPacket()).getEntityID() == mc.player.getEntityId()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onPushEvent(PushEvent event) {
        if ((event.getType() == PushEvent.Type.BLOCK && noBlockPush.getValue()) || (event.getType() == PushEvent.Type.ENTITY && noEntityPush.getValue()) || (event.getType() == PushEvent.Type.PISTON && noPistonPush.getValue())) {
            event.setCanceled(true);
        }
    }

}
