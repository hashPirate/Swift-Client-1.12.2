package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class BetterTabModule extends Module {

    public static BetterTabModule INSTANCE = new BetterTabModule();

    public BetterTabModule() {
        super("BetterTab", Category.MISC, false, false);
    }

}
