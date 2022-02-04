package me.pignol.swift.client.modules.movement;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class NoSlowModule extends Module {

    public static final NoSlowModule INSTANCE = new NoSlowModule();

    public NoSlowModule() {
        super("NoSlowdown", Category.MOVEMENT);
    }

}
