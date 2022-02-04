package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class ViewmodelModule extends Module {

    public static ViewmodelModule INSTANCE = new ViewmodelModule();

    public static boolean rendering;

    public final Value<Float> translateX = new Value<>("TranslateX", 0f, -1f, 1f);
    public final Value<Float> translateY = new Value<>("TranslateY", 0f, -1f, 1f);
    public final Value<Float> translateZ = new Value<>("TranslateZ", 0f, -1f, 1f);

   /* public final Value<Float> rotateX = new Value<>("RotateX", 0f, -180f, 180f);
    public final Value<Float> rotateY = new Value<>("RotateY", 0f, -180f, 180f);
    public final Value<Float> rotateZ = new Value<>("RotateZ", 0f, -180f, 180f); */

    public final Value<Float> scaleX = new Value<>("ScaleX", 1f, 0f, 2f);
    public final Value<Float> scaleY = new Value<>("ScaleY", 1f, 0f, 2f);
    public final Value<Float> scaleZ = new Value<>("ScaleZ", 1f, 0f, 2f);

    public final Value<Float> offsetX = new Value<>("OffsetX", 0f, -2f, 2f);
    public final Value<Float> offsetXOff = new Value<>("OffsetXOffhand", 0f, -2f, 2f);

    public final Value<Boolean> pause = new Value<>("Pause", true);
    public final Value<Boolean> pauseY = new Value<>("PauseY", true);

    public final Value<Boolean> oldSwing = new Value<>("OldSwing", true);

    public final Value<Integer> alpha = new Value<>("Alpha", 255, 0, 255);

    public ViewmodelModule() {
        super("ViewmodelChanger", Category.RENDER, false);
    }

}
