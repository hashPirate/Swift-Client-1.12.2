package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class LiquidPlace extends Module {

    private static LiquidPlace INSTANCE;

    //public final Value<Float> distance = new Value<>("Distance", 0.0F, 0.0F, 5.0F, 0.10F);

    public LiquidPlace() {
        super("LiquidPlace", Category.MISC);
        INSTANCE = this;
    }

    public static LiquidPlace getInstance() {
        return INSTANCE;
    }

}
