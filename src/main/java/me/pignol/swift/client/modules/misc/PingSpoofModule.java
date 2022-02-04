package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketKeepAlive;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class PingSpoofModule extends Module {

    private final Value<Boolean> seconds = new Value<>("Seconds", false);
    private final Value<Integer> delay = new Value<>("Delay", 20, 0, 1000);
    private final Value<Boolean> extraPacket = new Value<>("Packet", true);

    private final Queue<Packet<?>> packets = new ConcurrentLinkedQueue<>();
    private final StopWatch timer = new StopWatch();
    private boolean receive = true;

    public PingSpoofModule() {
        super("PingSpoof", Category.MISC);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        clearQueue();
    }

    @Override
    public void onDisable() {
        clearQueue();
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (receive && mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && event.getPacket() instanceof CPacketKeepAlive) {
            packets.add(event.getPacket());
            event.setCanceled(true);
        }
    }

    public void clearQueue() {
        if (mc.player != null && !mc.isSingleplayer() && mc.player.isEntityAlive() && !seconds.getValue() && timer.passed(delay.getValue())) {
            double limit = MathUtil.getIncremental(Math.random() * 10.0, 1.0);
            receive = false;
            for (int i = 0; i < limit; i++) {
                Packet<?> packet = packets.poll();
                if (packet != null) {
                    mc.player.connection.sendPacket(packet);
                }
            }

            if (extraPacket.getValue()) {
                mc.player.connection.sendPacket(new CPacketKeepAlive(10000));
            }
            
            timer.reset();
            receive = true;
        }
    }

}
