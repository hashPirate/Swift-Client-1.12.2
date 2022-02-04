package me.pignol.swift.client.command.commands;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.ModuleManager;
import org.lwjgl.input.Keyboard;

public class BindCommand extends Command {

    public BindCommand() {
        super("Bind", new String[]{"b"});
    }

    @Override
    public void run(String[] args) {
        if (args.length < 2) {
            return;
        }

        try {
            ModuleManager.getInstance().getModules()
                    .stream()
                    .filter(mod -> mod.getName().equalsIgnoreCase(args[1]))
                    .findFirst()
                    .ifPresent(module -> {
                        module.setKey(Keyboard.getKeyIndex(args[2].toUpperCase()));
                        ChatUtil.sendMessage(ChatFormatting.GREEN + module.getName() + " was bound to " + Keyboard.getKeyName(Keyboard.getKeyIndex(args[2].toUpperCase())));
                    });
        } catch (Exception ex) {
            ChatUtil.sendMessage(ChatFormatting.RED + ex.getMessage(), -4);
        }
    }

}
