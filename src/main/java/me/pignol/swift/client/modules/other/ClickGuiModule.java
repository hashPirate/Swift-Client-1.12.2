package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.gui.blowbui.glowclient.clickgui.ClickGUI;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class ClickGuiModule extends Module {

    public static final ClickGuiModule INSTANCE = new ClickGuiModule();

    public final Value<Boolean> moduleButtons = new Value<>("ModuleButtons", true);
    public final Value<Boolean> moduleDisabledButtons = new Value<>("DisabledModuleButtons", true);
    public final Value<Integer> red = new Value<>("Red", 255, 0, 255);
    public final Value<Integer> green = new Value<>("Green", 255, 0, 255);
    public final Value<Integer> blue = new Value<>("Blue", 255, 0, 255);
    public final Value<Integer> alpha = new Value<>("Alpha", 80, 0, 255);
    public final Value<Integer> disabledRed = new Value<>("DisabledRed", 255, 0, 255);
    public final Value<Integer> disabledGreen = new Value<>("DisabledGreen", 255, 0, 255);
    public final Value<Integer> disabledBlue = new Value<>("DisabledBlue", 255, 0, 255);
    public final Value<Integer> disabledAlpha = new Value<>("DisabledAlpha", 80, 0, 255);
    public final Value<Integer> lineAlpha = new Value<>("LineAlpha", 80, 0, 255);
    public final Value<Integer> hoverAlpha = new Value<>("HoverAlpha", 80, 0, 255);
    public final Value<Integer> categoryRed = new Value<>("CategoryRed", 255, 0, 255);
    public final Value<Integer> categoryGreen = new Value<>("CategoryGreen", 255, 0, 255);
    public final Value<Integer> categoryBlue = new Value<>("CategoryBlue", 255, 0, 255);
    public final Value<Integer> categoryAlpha = new Value<>("CategoryAlpha", 80, 0, 255);
    public final Value<Integer> backgroundRed = new Value<>("BackgroundRed", 0, 0, 255);
    public final Value<Integer> backgroundGreen = new Value<>("BackgroundGreen", 0, 0, 255);
    public final Value<Integer> backgroundBlue = new Value<>("BackgroundBlue", 0, 0, 255);
    public final Value<Integer> backgroundAlpha = new Value<>("BackgroundAlpha", 80, 0, 255);

    public final Value<Integer> textEnabledRed = new Value<>("TextEnabledRed", 0, 0, 255);
    public final Value<Integer> textEnabledGreen = new Value<>("TextEnabledGreen", 0, 0, 255);
    public final Value<Integer> textEnabledBlue = new Value<>("TextEnabledBlue", 0, 0, 255);

    public final Value<Integer> textDisabledRed = new Value<>("TextDisabledRed", 0, 0, 255);
    public final Value<Integer> textDisabledGreen = new Value<>("TextDisabledGreen", 0, 0, 255);
    public final Value<Integer> textDisabledBlue = new Value<>("TextDisabledBlue", 0, 0, 255);

    public final Value<Boolean> closeSettings = new Value<>("CloseSettings", true);
    public final Value<Boolean> customFont = new Value<>("CustomFont", true);

    public ClickGuiModule() {
        super("ClickGUI", Category.OTHER, false, false);
        setDrawn(false);
        new ClickGUI();
    }

    @Override
    public void onEnable() {
        mc.displayGuiScreen(ClickGUI.getInstance());
    }

}
