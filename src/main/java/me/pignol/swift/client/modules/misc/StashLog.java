package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityShulkerBox;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StashLog extends Module {

    public StashLog() {
        super("StashLog", Category.MISC);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (event.getStage() == Stage.PRE)
            return;

        for (TileEntity entity : mc.world.loadedTileEntityList) {
            if (entity instanceof TileEntityShulkerBox) {

            }
        }
    }

}
