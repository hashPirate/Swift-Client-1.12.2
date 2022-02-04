package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class Tooltips extends Module {

    private final static Tooltips INTANCE = new Tooltips();

    public final Value<Boolean> numbers = new Value<>("Numbers", true);

    public Tooltips() {
        super("Tooltips", Category.MISC);
    }

    public static Tooltips getInstance() {
        return INTANCE;
    }

}
