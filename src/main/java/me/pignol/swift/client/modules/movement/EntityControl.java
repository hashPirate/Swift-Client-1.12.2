package me.pignol.swift.client.modules.movement;

import me.pignol.swift.client.event.events.EntitySteerEvent;
import me.pignol.swift.client.event.events.SaddleEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityControl extends Module {

    public EntityControl() {
        super("EntityControl", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onSaddle(SaddleEvent event) {
        event.setCanceled(true);
    }

    @SubscribeEvent
    public void onEntitySteer(EntitySteerEvent event) {
        event.setCanceled(true);
    }

}
