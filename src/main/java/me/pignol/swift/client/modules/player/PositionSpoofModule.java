package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PositionSpoofModule extends Module {

    public static PositionSpoofModule INSTANCE;

    public PositionSpoofModule() {
        super("PositionSpoof", Category.MISC);
        INSTANCE = this;
    }

    public Value<Integer> runs = new Value<>("Runs", 10, 1, 300);
    public Value<Boolean> onground = new Value<>("Onground", true);

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging) event.getPacket()).getAction().equals(CPacketPlayerDigging.Action.RELEASE_USE_ITEM)) {
            if (ItemUtil.isHolding(Items.BOW)) {
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SPRINTING));
                for (int i = 0; i < runs.getValue(); i++) {
                    if (onground.getValue()) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-10, mc.player.posZ, false));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0E-10, mc.player.posZ, true));
                    } else {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0E-10, mc.player.posZ, true));
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-10, mc.player.posZ, false));
                    }
                }
                mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SPRINTING));
            }
        }
    }

}
