package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.ValueEvent;
import me.pignol.swift.client.managers.FontManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FontModule extends Module {

    public static FontModule INSTANCE = new FontModule();

    public boolean receive = false;

    public final Value<String> font = new Value<>("FontName", "Verdana");
    public final Value<Integer> size = new Value<>("Size", 18, 0, 25);
    public final Value<Boolean> antiAlias = new Value<>("AntiAlias", true);
    public final Value<Boolean> fractionalMetrics = new Value<>("FractionalMetrics", true);
    public final Value<Boolean> syncChat = new Value<>("SyncChat", true);

    public FontModule() {
        super("Font", Category.OTHER, false, true);
    }

    @Override
    public void onDisable() {
        FontManager.getInstance().setCustom(false);
        HudModule.INSTANCE.sortModules();
    }

    @Override
    public void onEnable() {
        FontManager.getInstance().setCustom(true);
        HudModule.INSTANCE.sortModules();
    }

    @SubscribeEvent
    public void onValueChange(ValueEvent event) {
        Value value = event.getValue();
        if (receive) {
            if (value == font || value == antiAlias || value == size || value == fractionalMetrics) {
                FontManager.getInstance().updateFontRenderer();
            }
        }
    }

}
