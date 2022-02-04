package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.SoundEvents;
import net.minecraft.network.play.server.SPacketSoundEffect;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;


//The Pain never ends ima be honest
public class AntiSound extends Module
{

    private final Value<Boolean> armorEquip = new Value<>("ArmorEquip", true);

    public AntiSound() {
        super("AntiSound", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketSoundEffect) {
            final SPacketSoundEffect packet = (SPacketSoundEffect) event.getPacket();
            final SoundEvent sound = packet.getSound();

            if (armorEquip.getValue() && packet.getCategory() == SoundCategory.PLAYERS && sound == SoundEvents.ITEM_ARMOR_EQUIP_GENERIC) {
                event.setCanceled(true);
            }
        }
    }

}
