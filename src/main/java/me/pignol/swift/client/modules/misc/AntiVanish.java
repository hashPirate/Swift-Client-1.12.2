package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.server.SPacketPlayerListItem;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiVanish extends Module {

    public AntiVanish() {
        super("AntiVanish", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketRead(PacketEvent.Send event) {
        if (event.getPacket() instanceof SPacketPlayerListItem) {
            SPacketPlayerListItem tabList = (SPacketPlayerListItem) event.getPacket();
            for (SPacketPlayerListItem.AddPlayerData playerData : tabList.getEntries()) {
                if (mc.getConnection().getPlayerInfo(playerData.getProfile().getId()) == null && playerData.getProfile().getName() != null) {
                    ChatUtil.sendMessage(playerData.getProfile().getName() + " has vanished.");
                }
            }
        }
    }

}
