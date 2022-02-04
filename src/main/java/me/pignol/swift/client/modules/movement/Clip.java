package me.pignol.swift.client.modules.movement;

import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Clip extends Module {

    //cant sleep Cause im battling Devils

     double[] packets = new double[] {0.41, 0.75, 1, 1.16 };

    public Clip() {
        super("Clip", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if(mc.player.onGround) {
            mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
            for (double jumpOffset : packets) {
                mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + jumpOffset, mc.player.posZ, true));
               mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, -(mc.player.posY + 5), mc.player.posZ, true));
               mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));

            }
        }
    }
}



