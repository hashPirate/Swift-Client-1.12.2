package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BuildHeight extends Module
{

    public BuildHeight()
    {
        super("BuildHeight", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event)
    {
        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock)
        {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (packet.getPos().getY() >= 255)
            {
                if (packet.getDirection() == EnumFacing.UP)
                {
                    packet.placedBlockDirection = EnumFacing.DOWN;
                }
            }
        }
    }

}
