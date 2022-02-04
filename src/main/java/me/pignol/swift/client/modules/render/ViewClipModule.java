package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class ViewClipModule extends Module {

    public static final ViewClipModule INSTANCE = new ViewClipModule();

    private final Value<Float> range = new Value<>("Range", 5.0F, 0.0F, 12.0F);

    public ViewClipModule() {
        super("ViewClip", Category.RENDER);
    }

    public float getRange() {
        return range.getValue();
    }

}
