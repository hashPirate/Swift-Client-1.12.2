package me.pignol.swift.client.managers;

import com.mojang.realmsclient.gui.ChatFormatting;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.command.commands.*;
import me.pignol.swift.client.modules.Module;

public class CommandManager {

    private static final CommandManager INSTANCE = new CommandManager();

    private final ObjectArrayList<Command> commands = new ObjectArrayList<>();

    private String prefix = ".";

    public static CommandManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        commands.add(new BindCommand());
        commands.add(new ToggleCommand());
        commands.add(new CheckValuesCommand());
        commands.add(new DrawnCommand());
        commands.add(new FriendCommand());
        commands.add(new PrefixCommand());
        commands.add(new HistoryCommand());
        commands.add(new SpammerCommand());
        commands.add(FakePlayerCommand.getInstance());
    }

    public void onMessage(String message) {
        String[] args = message.substring(prefix.length()).split(" ");
        for (Command command : commands) {
            if (command.getName().equalsIgnoreCase(args[0])) {
                command.run(args);
                return;
            }
            for (String alias : command.getAliases()) {
                if (alias.equalsIgnoreCase(args[0])) {
                    command.run(args);
                    return;
                }
            }
        }

        for (Module module : ModuleManager.getInstance().getModules()) {
            if (args[0].equalsIgnoreCase(module.getDisplayName())) {
                for (Value value : module.getValues()) {
                    if (value.getName().equalsIgnoreCase(args[1])) {
                        if (value.getValue() instanceof Integer) {
                            Integer valueOf = Integer.valueOf(args[2]);
                            value.setValue(valueOf);
                            ChatUtil.sendMessage("Set " + value.getName() + " in " + module.getName() + " to " + valueOf);
                        } else if (value.getValue() instanceof Float) {
                            Float valueOf = Float.valueOf(args[2]);
                            value.setValue(valueOf);
                            ChatUtil.sendMessage("Set " + value.getName() + " in " + module.getName() + " to " + valueOf);
                        } else if (value.getValue() instanceof Boolean) {
                            Boolean valueOf = Boolean.valueOf(args[2]);
                            value.setValue(valueOf);
                            ChatUtil.sendMessage("Set " + value.getName() + " in " + module.getName() + " to " + valueOf);;
                        }
                        return;
                    }
                }
            }
        }

        ChatUtil.sendMessage(ChatFormatting.RED + "Cant find a command named " + args[0]);
    }

    public String getPrefix() {
        return prefix;
    }

    public void setPrefix(String prefix) {
        this.prefix = prefix;
    }

}
