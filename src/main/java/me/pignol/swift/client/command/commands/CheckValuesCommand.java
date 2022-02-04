package me.pignol.swift.client.command.commands;

import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.command.Command;
import me.pignol.swift.client.managers.ModuleManager;
import me.pignol.swift.client.modules.Module;

import java.util.List;

public class CheckValuesCommand extends Command {

    public CheckValuesCommand() {
        super("value", new String[]{});
    }

    @Override
    public void run(String[] args) {
        if (args.length < 1) return;

        try {
            Module module = ModuleManager.getInstance().getModules()
                    .stream()
                    .filter(mod -> mod.getName().equalsIgnoreCase(args[1]))
                    .findFirst()
                    .orElse(null);
            if (module != null) {
                StringBuilder values = new StringBuilder();

                List<Value> list = module.getValues();

                for (Value value : list) {
                    boolean isLast = value == list.get(list.size() - 1);
                    values.append(value.getName() + (isLast ? "." : ","));
                }

                ChatUtil.sendMessage("Values for the module " + args[1] + " are " + values);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }

}
