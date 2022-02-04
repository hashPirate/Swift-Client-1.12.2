package me.pignol.swift.client.modules.render;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class FullBrightModule extends Module {

    public final static FullBrightModule INSTANCE = new FullBrightModule();

    public FullBrightModule() {
        super("Fullbright", Category.RENDER);
    }

    private float oldGamma = -1;

    @Override
    public void onDisable() {
        if (oldGamma != -1) {
            mc.gameSettings.gammaSetting = oldGamma;
            mc.renderGlobal.loadRenderers();
        }
    }

    @Override
    public void onEnable() {
        oldGamma = mc.gameSettings.gammaSetting;
        mc.gameSettings.gammaSetting = 100F;
    }

}
