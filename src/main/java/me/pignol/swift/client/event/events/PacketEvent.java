package me.pignol.swift.client.event.events;

import net.minecraft.network.Packet;
import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

public class PacketEvent extends Event {

    private final Packet<?> packet;

    public PacketEvent(Packet<?> packet) {
        this.packet = packet;
    }

    public Packet<?> getPacket() {
        return packet;
    }

    @Cancelable
    public static class Receive extends PacketEvent {

        public Receive(Packet<?> packet) {
            super(packet);
        }

    }

    @Cancelable
    public static class Send extends PacketEvent {

        public Send(Packet<?> packet) {
            super(packet);
        }

    }

}
