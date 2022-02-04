package me.pignol.swift.client.modules.render;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;

public class Search extends Module {

    public static Search INSTANCE;

    private final Value<Boolean> softReload = new Value<>("SoftReload", true);

    public Search() {
        super("Search", Category.RENDER);
        INSTANCE = this;
    }

    @Override
    public void onEnable() {
        if (softReload.getValue()) {
            mc.addScheduledTask(
                    () -> {
                        int x = (int) mc.getRenderViewEntity().posX;
                        int y = (int) mc.getRenderViewEntity().posY;
                        int z = (int) mc.getRenderViewEntity().posZ;

                        int distance = mc.gameSettings.renderDistanceChunks * 16;

                        mc.renderGlobal.markBlockRangeForRenderUpdate(
                                x - distance, y - distance, z - distance, x + distance, y + distance, z + distance);
                    });
        } else {
         mc.renderGlobal.loadRenderers();
        }
    }

}
