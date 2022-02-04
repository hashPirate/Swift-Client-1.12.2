package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.util.Colors;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.awt.*;

public class ColorsModule extends Module {

    public static ColorsModule INSTANCE = new ColorsModule();

    private final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    private final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    private final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);

    public final Value<Integer> friendRed = new Value<>("FriendRed", 0, 0, 255);
    public final Value<Integer> friendGreen = new Value<>("FriendGreen", 255, 0, 255);
    public final Value<Integer> friendBlue = new Value<>("FriendBlue", 255, 0, 255);

    public final Value<Integer> speed = new Value<>("Speed", 20, 0, 100);
    public final Value<Integer> factor = new Value<>("Factor", 100, 0, 200);

    public final Value<Integer> saturation = new Value<>("Saturation", 255, 0, 255);
    public final Value<Integer> brightness = new Value<>("Brightness", 255, 0, 255);

    public float hue;

    public ColorsModule() {
        super("Colors", Category.OTHER, true, true);
        setDrawn(false);
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public void onUpdate(UpdateEvent event) {
        int colorSpeed = 101 - this.speed.getValue();
        this.hue = (float)(System.currentTimeMillis() % (long)(360 * colorSpeed)) / (360.0f * (float)colorSpeed);
    }

    public int getRainbow() {
        return Color.HSBtoRGB(hue, saturation.getValue() / 255.0F, brightness.getValue() / 255.0F);
    }

    public int getColor() {
        return Colors.toRGBA(red.getValue(), green.getValue(), blue.getValue(), 255);
    }

    public int getFriendColor() {
        return Colors.toRGBA(friendRed.getValue(), friendGreen.getValue(), friendBlue.getValue(), 255);
    }

}
