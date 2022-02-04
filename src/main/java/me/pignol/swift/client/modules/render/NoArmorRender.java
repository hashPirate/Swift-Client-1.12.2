package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class NoArmorRender extends Module {

    private static final NoArmorRender INSTANCE = new NoArmorRender();

    public final Value<Boolean> noHelmet = new Value<>("NoHelmet", true);
    public final Value<Boolean> noChest = new Value<>("NoChest", true);
    public final Value<Boolean> noLegs = new Value<>("NoLeg", true);
    public final Value<Boolean> noJordans = new Value<>("NoJ's", true);

    public NoArmorRender() {
        super("NoArmorRender", Category.RENDER);
    }

    public static NoArmorRender getInstance() {
        return INSTANCE;
    }

}
