package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoFallModule extends Module {

    private final Value<Boolean> pauseOnPacketFly = new Value<>("PauseOnFly", true);

    public NoFallModule() {
        super("NoFall", Category.MOVEMENT);
        setSuffix("Packet");
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && mc.player.fallDistance > 2) {
            if (pauseOnPacketFly.getValue() && PacketFly.INSTANCE.isEnabled()) {
                return;
            }
            ((CPacketPlayer) event.getPacket()).onGround = true;
        }
    }

}
