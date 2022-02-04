package me.pignol.swift.client.modules.other;

import me.pignol.swift.api.util.text.TextColor;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.tutorial.TutorialSteps;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ManageModule extends Module {

    public static ManageModule INSTANCE = new ManageModule();

    public final Value<Boolean> friends = new Value<>("Friends", true);
    public final Value<Boolean> renderGlintOnce = new Value<>("RenderGlintOnce", true);

    private final Value<Integer> fov = new Value<>("FOV", 110, 100, 200);

    public final Value<Integer> serverNotResponding = new Value<>("ServerNotResponding", 2500, 500, 5000);

    public final Value<Integer> holeRange = new Value<>("HoleRange", 6, 0, 42);
    public final Value<Integer> holeRangeY = new Value<>("HoleYRange", 6, 0, 42);
    public final Value<Integer> holeSearchDelay = new Value<>("HoleSearchDelay", 150, 0, 1000);

    public final Value<Integer> idleSpeed = new Value<>("IdleSpeed", 500, 0, 2500);

    public final Value<Boolean> forgeHax = new Value<>("ForgeHax", false);
    public final Value<Boolean> prefix = new Value<>("Prefix", false);
    public final Value<Boolean> rainbowPrefix = new Value<>("PrefixRainbow", false);
    public final Value<TextColor.Color> bracketColor = new Value<>("BracketColor", TextColor.Color.WHITE, v -> prefix.getValue());
    public final Value<TextColor.Color> nameColor = new Value<>("NameColor", TextColor.Color.DARK_GRAY, v -> prefix.getValue());

    public final Value<String> clientName = new Value<>("ClientName", "Swift");

    public final Value<Integer> tabbedFps = new Value<>("TabbedFPS", 60, 1, 240);

    public final Value<Boolean> clearTutorial = new Value<>("ClearTutorial", true);
    public final Value<Boolean> debugRotations = new Value<>("DebugRotations", true);

    public ManageModule() {
        super("Manage", Category.OTHER, true, false);
        setDrawn(false);
        MinecraftForge.EVENT_BUS.register(this);
    }

    @Override
    public void onDisable() {
        setEnabled(true);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        mc.gameSettings.fovSetting = fov.getValue();
        if (clearTutorial.getValue()) {
            mc.gameSettings.tutorialStep = TutorialSteps.NONE;
            mc.getTutorial().setStep(TutorialSteps.NONE);
            clearTutorial.setValue(false);
        }
    }

}

