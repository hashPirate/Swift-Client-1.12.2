package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.network.Packet;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BowSpoof extends Module {

    private boolean hs;
    int tiki;
    private long lastHsTime;
    long percent;

    public BowSpoof() { super("BowSpoof", Category.COMBAT); }

    private final Value<Boolean> bypass = new Value<>("Bypass", true);
    private final Value<Boolean> autoFire = new Value<>("AutoFire", true);
    private final Value<Double> spoofs = new Value<>("Spoofs",0.0,0.0,30.0);
    private final Value<Double> timeout = new Value<>("Time",0.0,0.0,20.0);


    public void doUseItem() {
        mc.player.connection.sendPacket((Packet)new CPacketPlayerDigging(CPacketPlayerDigging.Action.RELEASE_USE_ITEM, BlockPos.ORIGIN, mc.player.getHorizontalFacing()));
        mc.player.connection.sendPacket((Packet)new CPacketPlayerTryUseItem(EnumHand.MAIN_HAND));
        mc.player.stopActiveHand();
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (autoFire.getValue() && mc.player.getHeldItem(EnumHand.MAIN_HAND).getItem() instanceof ItemBow && mc.player.isHandActive() && mc.player.getItemInUseMaxCount() >= 4 && autoFire.getValue() && this.percent >= 100L) {
            ++this.tiki;
            if (this.tiki >= 12) {
                this.doUseItem();
                this.tiki = 0;
            }
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayerDigging) {
            final CPacketPlayerDigging packet = (CPacketPlayerDigging)event.getPacket();
            if (packet.getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
                final ItemStack handStack = mc.player.getHeldItem(EnumHand.MAIN_HAND);
                if (!handStack.isEmpty() && handStack.getItem() != null && handStack.getItem() instanceof ItemBow && System.currentTimeMillis() - this.lastHsTime >= (timeout.getValue() * 1000)) {
                    this.hs = true;
                    this.lastHsTime = System.currentTimeMillis();
                    mc.player.connection.sendPacket((Packet)new CPacketEntityAction((Entity)mc.player, CPacketEntityAction.Action.START_SPRINTING));
                    for (int index = 0; index < ((Number)this.spoofs.getValue()).intValue() * 10; ++index) {
                        if (this.bypass.getValue()) {
                            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-5, mc.player.posZ, false));
                            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0E-5, mc.player.posZ, true));
                        }
                        else {
                            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY - 1.0E-5, mc.player.posZ, true));
                            mc.player.connection.sendPacket((Packet)new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0E-5, mc.player.posZ, false));
                        }
                    }
                    this.hs = false;
                }
            }
        }
    }


}
