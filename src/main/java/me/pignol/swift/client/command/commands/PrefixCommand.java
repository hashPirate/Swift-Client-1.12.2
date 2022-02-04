package me.pignol.swift.client.command.commands;

import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.CommandManager;

public class PrefixCommand extends Command {

    public PrefixCommand() {
        super("Prefix", new String[]{});
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) {
            return;
        }
        CommandManager.getInstance().setPrefix(args[1]);
    }

}
