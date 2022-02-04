package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class AspectRatio extends Module {

    public static final AspectRatio INSTANCE = new AspectRatio();

    public final Value<Integer> worldX = new Value<>("WorldResX", mc.displayWidth, 0, 1920);
    public final Value<Integer> worldY = new Value<>("WorldResY", mc.displayHeight, 0, 1080);

    public AspectRatio() {
        super("AspectRatio", Category.RENDER);
    }

}
