package me.pignol.swift.client.managers;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.client.command.commands.FakePlayerCommand;
import me.pignol.swift.client.event.events.ConnectionEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.WorldEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.UUID;

import static me.pignol.swift.api.util.thread.ThreadUtil.scheduleEvent;

public class EventManager implements Globals
{

    private static final EventManager INSTANCE = new EventManager();

    public static EventManager getInstance()
    {
        return INSTANCE;
    }

    public void load()
    {
        MinecraftForge.EVENT_BUS.register(this);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event)
    {
        if (event.getPacket() instanceof SPacketPlayerListItem && !isNull())
        {
            final SPacketPlayerListItem packet = (SPacketPlayerListItem) event.getPacket();
            if (!SPacketPlayerListItem.Action.ADD_PLAYER.equals(packet.getAction()) && !SPacketPlayerListItem.Action.REMOVE_PLAYER.equals(packet.getAction()))
            {
                return;
            }

            for (SPacketPlayerListItem.AddPlayerData data : packet.getEntries())
            {
                if (data != null)
                {
                    if (!isNullOrEmpty(data.getProfile().getName()) || data.getProfile().getId() != null)
                    {
                        final UUID id = data.getProfile().getId();
                        switch (packet.getAction())
                        {
                            case ADD_PLAYER:
                                String name = data.getProfile().getName();
                                scheduleEvent(new ConnectionEvent(0, id, name));
                                break;
                            case REMOVE_PLAYER:
                                EntityPlayer entity = mc.world.getPlayerEntityByUUID(id);
                                if (entity != null)
                                {
                                    String logoutName = entity.getName();
                                    scheduleEvent(new ConnectionEvent(1, entity, id, logoutName));
                                } else
                                {
                                    scheduleEvent(new ConnectionEvent(2, id, null));
                                }
                                break;
                        }
                    }
                }
            }
        }
    }

    @SubscribeEvent
    public void onWorldChange(WorldEvent.Unload event)
    {
        FakePlayerCommand.getInstance().clear();
    }

    public boolean isNullOrEmpty(String string)
    {
        return string == null || string.isEmpty();
    }


}
