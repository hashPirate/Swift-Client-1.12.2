package me.pignol.swift.client.command.commands;

import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.config.FileManager;
import me.pignol.swift.client.modules.misc.Spammer;

import java.io.File;

public class SpammerCommand extends Command
{

    public SpammerCommand() {
        super("Spammer", new String[]{
                "spammerfile",
                "spam"
        });
    }

    @Override
    public void run(String[] args)
    {
        if (args.length == 2)
        {
            File spammerFiles = FileManager.getInstance().getSpammerPath().toFile();
            for (File file : spammerFiles.listFiles()) {
                if (file.getName().startsWith(args[1])) {
                    Spammer.INSTANCE.setFile(file);
                    break;
                }
            }
        }
    }

}
