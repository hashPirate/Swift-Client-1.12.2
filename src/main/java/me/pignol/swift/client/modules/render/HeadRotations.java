package me.pignol.swift.client.modules.render;

import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class HeadRotations extends Module {

    public static final HeadRotations INSTANCE = new HeadRotations();

    private float yaw;
    private float pitch;

    public HeadRotations() {
        super("HeadRotations", Category.RENDER, false,false);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event)
    {
        if (event.getPacket() instanceof CPacketPlayer)
        {
            CPacketPlayer packet = (CPacketPlayer) event.getPacket();
            yaw = packet.yaw;
            pitch = packet.pitch;
        }
    }

    public float getPitch()
    {
        return pitch;
    }

    public float getYaw()
    {
        return yaw;
    }

    public boolean shouldRotate()
    {
        return isEnabled() && RotationManager.getInstance().isRotated();
    }

}
