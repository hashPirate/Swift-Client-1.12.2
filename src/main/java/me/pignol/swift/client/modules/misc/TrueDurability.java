package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class TrueDurability extends Module {

    private static final TrueDurability INSTANCE = new TrueDurability();

    public TrueDurability() {
        super("TrueDurability", Category.MISC);
    }

    public static TrueDurability getInstance() {
        return INSTANCE;
    }
}
