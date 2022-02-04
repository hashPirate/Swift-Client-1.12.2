package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.player.FastPlaceModule;
import net.minecraft.init.Items;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.concurrent.ThreadLocalRandom;

public class FakeRotationModule extends Module {

    public final Value<Boolean> pauseOnXp = new Value<>("PauseOnXP", true);
    public final Value<Boolean> pauseOnMiddleClick = new Value<>("MiddleClick", true, v -> pauseOnXp.getValue());

    public FakeRotationModule() {
        super("FakeRotation", Category.MISC);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (event.getStage() == Stage.PRE) {
            if (ItemUtil.isHolding(Items.EXPERIENCE_BOTTLE) && pauseOnXp.getValue())
                return;
			if (pauseOnMiddleClick.getValue() && FastPlaceModule.INSTANCE.isEnabled() && FastPlaceModule.INSTANCE.shouldMend()) return;

            int yaw = ThreadLocalRandom.current().nextInt(-90, 90);
            int pitch = ThreadLocalRandom.current().nextInt(-180, 180);
            RotationManager.getInstance().setPlayerRotations(yaw, pitch);
        }
    }

}
