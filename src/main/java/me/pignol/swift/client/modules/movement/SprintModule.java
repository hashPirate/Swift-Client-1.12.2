package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SprintModule extends Module {

    public static SprintModule INSTANCE;

    public static Value<Mode> mode = new Value<>("Mode", Mode.LEGIT);

    public SprintModule() {
        super("Sprint", Category.MOVEMENT);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() == mc.player) {
            switch (mode.getValue()) {
                case RAGE:
                    if ((mc.gameSettings.keyBindForward.isKeyDown() || mc.gameSettings.keyBindBack.isKeyDown() || mc.gameSettings.keyBindLeft.isKeyDown() || mc.gameSettings.keyBindRight.isKeyDown()) && !(mc.player.isSneaking() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6f)) {
                        mc.player.setSprinting(true);
                    }
                    break;
                case LEGIT:
                    if (mc.gameSettings.keyBindForward.isKeyDown() && !(mc.player.isSneaking() || mc.player.isHandActive() || mc.player.collidedHorizontally || mc.player.getFoodStats().getFoodLevel() <= 6f) && mc.currentScreen == null) {
                        mc.player.setSprinting(true);
                    }
                    break;
            }
        }
    }

    public enum Mode {
        LEGIT, RAGE
    }

}
