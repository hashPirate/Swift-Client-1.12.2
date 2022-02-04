package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.gui.GuiDownloadTerrain;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoRotate extends Module
{

    private final Value<Boolean> removeFlags = new Value<>("RemoveFlags", true);

    public NoRotate()
    {
        super("NoRotate", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event)
    {
        if (event.getPacket() instanceof SPacketPlayerPosLook && !isNull() && !(mc.currentScreen instanceof GuiDownloadTerrain))
        {
            SPacketPlayerPosLook packet = (SPacketPlayerPosLook) event.getPacket();
            packet.pitch = mc.player.rotationPitch;
            packet.yaw = mc.player.rotationPitch;
            if (removeFlags.getValue())
            {
                packet.flags.remove(SPacketPlayerPosLook.EnumFlags.X_ROT);
                packet.flags.remove(SPacketPlayerPosLook.EnumFlags.Y_ROT);
            }
        }
    }

}
