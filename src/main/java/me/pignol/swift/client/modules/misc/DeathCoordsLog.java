package me.pignol.swift.client.modules.misc;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.gui.GuiGameOver;
import net.minecraftforge.client.event.GuiScreenEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;

public class DeathCoordsLog extends Module {

    private final Value<Boolean> copy = new Value<>("Copy", true);
    private final Value<Boolean> display = new Value<>("Display", true);

    private boolean reset = true;

    public DeathCoordsLog() {
        super("DeathCoordsLog", Category.MISC);
    }

    @SubscribeEvent
    public void onDisplayScreen(GuiScreenEvent event) {
        if (event.getGui() instanceof GuiGameOver && reset) {
            String coords = "XYZ: " + getRoundedDouble(mc.player.posX) + ", " + getRoundedDouble(mc.player.posY) + ", " + getRoundedDouble(mc.player.posZ);
            if (copy.getValue()) {
                StringSelection stringSelection = new StringSelection(coords);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
            }
            if (display.getValue()) {
                ChatUtil.sendMessage(ChatFormatting.RED + "[DeathCoordsLog] You died at " + coords + "!");
            }
            reset = false;
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (!mc.player.isDead) {
            reset = true;
        }
    }

    private String getRoundedDouble(double pos) {
        return String.format("%.2f", pos);
    }

}
