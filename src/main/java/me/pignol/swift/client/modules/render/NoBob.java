package me.pignol.swift.client.modules.render;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class NoBob extends Module {

    private final static NoBob INSTANCE = new NoBob();

    public NoBob() {
        super("NoBob", Category.RENDER);
    }

    public static NoBob getInstance() {
        return INSTANCE;
    }
}
