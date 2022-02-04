package me.pignol.swift.api.interfaces.mixin;

import net.minecraft.network.Packet;

public interface INetworkManager {

    Packet<?> sendPacketNoEvent(Packet<?> packetIn);

}
