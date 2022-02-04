package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.server.SPacketSpawnObject;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiLag extends Module {

    private final Value<Boolean> fireworks = new Value<>("Fireworks", true);

    private static final int FIREWORK_TYPE = 76;

    public AntiLag() {
        super("AntiLag", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSpawnObject) {
            int id = ((SPacketSpawnObject) event.getPacket()).getType();
            if (id == FIREWORK_TYPE && fireworks.getValue()) {
                event.setCanceled(true);
            }
        }
    }

}
