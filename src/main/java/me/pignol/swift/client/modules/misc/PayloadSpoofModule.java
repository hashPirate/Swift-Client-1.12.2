package me.pignol.swift.client.modules.misc;

import io.netty.buffer.Unpooled;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.client.CPacketCustomPayload;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PayloadSpoofModule extends Module {
    
    public PayloadSpoofModule() {
        super("PayloadSpoof", Category.MISC);
        setEnabled(true);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketCustomPayload) {
            final CPacketCustomPayload packet = (CPacketCustomPayload) event.getPacket();
            if (packet.getChannelName().equals("MC|Brand")) {
                packet.data = (new PacketBuffer(Unpooled.buffer()).writeString("vanilla"));
            }
        }
    }
    
}
