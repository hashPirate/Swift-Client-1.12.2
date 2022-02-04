package me.pignol.swift.client.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import it.unimi.dsi.fastutil.objects.Object2IntOpenHashMap;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.event.events.DeathEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.play.server.SPacketEntityStatus;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent;

public class TotemPopCounterModule extends Module {

    public static TotemPopCounterModule INSTANCE = new TotemPopCounterModule();

    private final Object2IntMap<String> popMap = new Object2IntOpenHashMap<>();

    public TotemPopCounterModule() {
        super("TotemPopCounter", Category.MISC);
    }

    @SubscribeEvent
    public void onPacketRecieve(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketEntityStatus && !isNull()) {
            final SPacketEntityStatus packet = (SPacketEntityStatus) event.getPacket();
            if (packet.getOpCode() == 35) {
                final Entity entity = packet.getEntity(mc.world);
                if (entity instanceof EntityPlayer) {
                    String name = entity.getName();
                    boolean contains = popMap.containsKey(name);
                    boolean isSelf = entity == mc.player;
                    boolean blue = isSelf || FriendManager.getInstance().isFriend(name);
                    popMap.put(name, contains ? popMap.get(name) + 1 : 1);
                    int pops = popMap.getInt(name);
                    ChatUtil.sendMessage((blue ? ChatFormatting.AQUA : ChatFormatting.RED) + ((isSelf ? "You" : name) + ChatFormatting.LIGHT_PURPLE + (isSelf ? " have " : " has ") + "popped \u00a74" + pops + "\u00a7d " + (pops == 1 ? "time" : "times") + " in total!"), -entity.getEntityId());
                }
            }
        }
    }

    @SubscribeEvent
    public void onDisconnect(FMLNetworkEvent.ClientDisconnectionFromServerEvent event) {
        popMap.clear();
    }

    @SubscribeEvent
    public void onDeath(DeathEvent event) {
        final EntityLivingBase entity = event.getEntity();
        if (entity instanceof EntityPlayer) {
            final String name = entity.getName();
            if (popMap.containsKey(name)) {
                final boolean isSelf = entity == mc.player;
                final boolean blue = isSelf || FriendManager.getInstance().isFriend(name);
                final int pops = popMap.getInt(name);
                ChatUtil.sendMessage((blue ? ChatFormatting.AQUA : ChatFormatting.RED) + ((isSelf ? "You" : name) + ChatFormatting.LIGHT_PURPLE + " died after popping \u00a7a" + popMap.get(name) + "\u00a7d" + (pops == 1 ? " totem!" : " totems!")), -entity.getEntityId());
                popMap.remove(name);
            }
        }
    }

    public Object2IntMap<String> getPopMap() {
        return popMap;
    }

}
