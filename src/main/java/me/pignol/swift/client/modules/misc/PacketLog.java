package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.managers.config.FileManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketPlayerDigging;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class PacketLog extends Module {

    private final Value<Boolean> clearOnDisable = new Value<>("ClearOnDisable", true);

    private final File file;
    private BufferedWriter writer;

    public PacketLog() {
        super("PacketLog", Category.MISC);
        file = new File(FileManager.getInstance().getMainPath() + "/PacketLogger.txt");
        try {
            if (!file.exists()) {
                file.createNewFile();
            } else {
                file.delete();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (writer == null) {
            try {
                writer = new BufferedWriter(new FileWriter(file));
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        if (event.getPacket() instanceof CPacketPlayer) {
            try {
                CPacketPlayer packet = (CPacketPlayer) event.getPacket();
                writer.write("CPACKETPLAYER XYZ: " + packet.x + ", " + packet.y + ", " + packet.z + "\n");
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (event.getPacket() instanceof CPacketPlayerDigging && ((CPacketPlayerDigging) event.getPacket()).getAction() == CPacketPlayerDigging.Action.RELEASE_USE_ITEM) {
            try {
                writer.write("RELEASEUSEITEM" + "\n");
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (event.getPacket() instanceof CPacketUseEntity) {
            CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            try {
                Entity entity = mc.world.getEntityByID(packet.entityId);
                String name = entity != null ? entity.getName() : "NULL";
                writer.write("CPACKETUSEENTITY ACTION:" + packet.action.name() + " ENTITY:" + name  + "\n");
                writer.flush();
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }

    @Override
    public void onEnable() {
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Override
    public void onDisable() {
        try {
            if (clearOnDisable.getValue()) {
                file.delete();
                file.createNewFile();
            }
            writer.flush();
            writer.close();
            writer = null; // no mem leaks Plz
        } catch (Exception ex) {

        }
    }
}
