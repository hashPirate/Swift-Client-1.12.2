package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class NoInterpolationModule extends Module {

    public static final NoInterpolationModule INSTANCE = new NoInterpolationModule();

    public final Value<Boolean> capes = new Value<>("Capes", true);
    public final Value<Boolean> models = new Value<>("Models", true);

    public NoInterpolationModule() {
        super("NoInterpolation", Category.RENDER);
    }

}
