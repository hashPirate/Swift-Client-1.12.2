package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;

public class Blink extends Module {

    private final Queue<Packet<?>> packets = new LinkedList<>();

    public Blink() {
        super("Blink", Category.PLAYER);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer) {
            event.setCanceled(true);
            packets.add(event.getPacket());
        }
    }

    @Override
    public void onDisable() {
        while (!packets.isEmpty()) {
            Packet<?> packet = packets.poll();
            mc.getConnection().sendPacket(packet);
        }
    }

}
