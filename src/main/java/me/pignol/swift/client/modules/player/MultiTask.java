package me.pignol.swift.client.modules.player;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class MultiTask extends Module {

    public static MultiTask INSTANCE;

    public MultiTask() {
        super("MultiTask", Category.PLAYER);
        INSTANCE = this;
    }

}
