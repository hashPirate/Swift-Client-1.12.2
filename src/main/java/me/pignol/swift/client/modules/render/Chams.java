package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class Chams extends Module {

    public static final Chams INSTANCE = new Chams();

    public final Value<Boolean> players = new Value<>("Players", true);
    public final Value<Boolean> crystals = new Value<>("Crystals", true);

    public final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    public final Value<Integer> alpha = new Value<>("Alpha", 100, 0, 255);

    public final Value<Float> crystalScale = new Value<>("CrystalScale", 1.0F, 0.1F, 1.0F, 0.05F);

    public Chams() {
        super("Chams", Category.RENDER);
    }

}
