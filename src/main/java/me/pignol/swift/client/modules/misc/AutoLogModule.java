package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoLogModule extends Module {

    private final Value<Float> health = new Value<>("Health", 15.0F, 0.0F, 36.0F);
    private final Value<Integer> totems = new Value<>("Totems", 1, 0, 5);
    private final Value<Boolean> disable = new Value<>("Disable", true);
    private final Value<Type> type = new Value<>("Type", Type.DISCONNECT);

    public AutoLogModule() {
        super("AutoLog", Category.MISC);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (ItemUtil.getItemCount(Items.TOTEM_OF_UNDYING) <= totems.getValue()) {
            if (mc.player.getHealth() + mc.player.getAbsorptionAmount() <= health.getValue()) {
                if (type.getValue() == Type.DISCONNECT) {
                    mc.world.sendQuittingDisconnectingPacket();
                } else {
                    mc.player.connection.sendPacket(new CPacketUseEntity(mc.player));
                    mc.player.connection.sendPacket(new CPacketHeldItemChange(-1));
                }
                if (disable.getValue()) {
                    setEnabled(false);
                }
            }
        }
    }

    enum Type {
        DISCONNECT,
        KICK
    }

}
