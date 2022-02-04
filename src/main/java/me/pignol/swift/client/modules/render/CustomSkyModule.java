package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.client.event.EntityViewRenderEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CustomSkyModule extends Module {

    public static CustomSkyModule INSTANCE = new CustomSkyModule();

    public final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);

    public final Value<Boolean> noFogUpdate = new Value<>("NoFogUpdate", true);

    public CustomSkyModule() {
        super("CustomFog", Category.RENDER);
    }

    @SubscribeEvent
    public void onFogColors(EntityViewRenderEvent.FogColors event) {
        event.setRed(red.getValue() / 255.0F);
        event.setGreen(green.getValue() / 255.0F);
        event.setBlue(blue.getValue() / 255.0F);
    }

}
