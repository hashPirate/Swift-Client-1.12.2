package me.pignol.swift.client.command.commands;

import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.ModuleManager;
import me.pignol.swift.client.modules.Module;

public class ToggleCommand extends Command {

    public ToggleCommand() {
        super("Toggle", new String[]{"t"});
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) return;

        ModuleManager.getInstance().getModules()
                .stream()
                .filter(mod -> mod.getName().equalsIgnoreCase(args[1]))
                .findFirst()
                .ifPresent(Module::toggle);
    }

}
