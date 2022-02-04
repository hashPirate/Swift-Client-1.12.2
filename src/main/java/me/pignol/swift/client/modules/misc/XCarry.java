package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketCloseWindow;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class XCarry extends Module
{

    private final Value<Boolean> forced = new Value<>("Forced", true);

    public XCarry()
    {
        super("XCarry", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event)
    {
        if (event.getPacket() instanceof CPacketCloseWindow)
        {
            CPacketCloseWindow packet = (CPacketCloseWindow) event.getPacket();
            if (packet.windowId == mc.player.inventoryContainer.windowId)
            {
                event.setCanceled(forced.getValue() || checkSlots());
            }
        }
    }

    public boolean checkSlots() {
        for (int i = 1; i <= 4; ++i) {
            if (!mc.player.inventoryContainer.getSlot(i).getStack().isEmpty()) {
                return true;
            }
        }
        return false;
    }

}
