package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

import java.util.ArrayList;
import java.util.List;

public class VisualRangeModule extends Module {

    public VisualRangeModule() {
        super("VisualRange", Category.MISC);
    }

    private final List<EntityPlayer> seenPlayers = new ArrayList<>();

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
        }

        final List<EntityPlayer> players = mc.world.playerEntities;
        for (int i = 0, playersSize = players.size(); i < playersSize; i++) {
            final EntityPlayer player = players.get(i);
            if (player == mc.player || seenPlayers.contains(player)) {
                continue;
            }
            ChatUtil.sendMessage(player.getName() + " has been spotted");
            seenPlayers.add(player);
            return;
        }

        if (players.size() > 0) {
            int seenSize = seenPlayers.size();
            for (int i = 0; i < seenSize; i++) {
                final EntityPlayer player = seenPlayers.get(i);
                if (player == mc.player || players.contains(player)) {
                    continue;
                }
                ChatUtil.sendMessage(player.getName() + " left ur range");
                seenPlayers.remove(player);
                return;
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        seenPlayers.clear();
    }

}
