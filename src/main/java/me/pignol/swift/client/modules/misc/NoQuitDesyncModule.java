package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class NoQuitDesyncModule extends Module {

    public NoQuitDesyncModule() {
        super("NoQuitDesync", Category.MISC);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event) {
        if ((mc.world == null) && mc.getConnection() != null) {
            mc.getConnection().sendPacket(new CPacketHeldItemChange(-1));
            mc.getConnection().getNetworkManager().closeChannel(new TextComponentString("hi"));
        }
    }

}
