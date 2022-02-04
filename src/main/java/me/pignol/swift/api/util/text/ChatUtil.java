package me.pignol.swift.api.util.text;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;

public class ChatUtil implements Globals {

    public static String getPrefix() {
        if (ManageModule.INSTANCE.prefix.getValue()) {
            if (ManageModule.INSTANCE.rainbowPrefix.getValue()) {
                return "\u00a7+<" + ManageModule.INSTANCE.clientName.getValue() + ">\u00a7r ";
            }
            return TextColor.coloredString("<", ManageModule.INSTANCE.bracketColor.getValue()) + TextColor.coloredString(ManageModule.INSTANCE.clientName.getValue(), ManageModule.INSTANCE.nameColor.getValue()) + TextColor.coloredString(">", ManageModule.INSTANCE.bracketColor.getValue()) + " ";
        }
        return "";
    }

    public static void sendMessage(String message) {
        if (mc.ingameGUI != null) {
            mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(getPrefix() + message));
        }
    }

    public static void sendMessage(String message, int id) {
        if (mc.ingameGUI != null) {
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(new TextComponentString(getPrefix() + message), id);
        }
    }

    public static void deleteMessage(int id) {
        if (mc.ingameGUI != null) {
            mc.ingameGUI.getChatGUI().deleteChatLine(id);
        }
    }

    public static void sendComponent(ITextComponent component, int id) {
        if (mc.ingameGUI != null) {
            mc.ingameGUI.getChatGUI().printChatMessageWithOptionalDeletion(component, id);
        }
    }

}