package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class  EnchantGlintModule extends Module {

    public static EnchantGlintModule INSTANCE = new EnchantGlintModule();

    public final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    public final Value<Boolean> rainbow = new Value<>("Rainbow", false);

    public EnchantGlintModule() {
        super("EnchantGlint", Category.RENDER, false, false);
    }

}
