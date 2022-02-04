package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class ReverseStep extends Module {

    private final Value<Float> speed = new Value<>("Speed", 0.5F, 0.1F, 1.0F, 0.1F);
    private final Value<Float> height = new Value<>("Height", 3.00F, 1.0F, 10.0F);

    public ReverseStep() {
        super("ReverseStep", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.onGround
                && !EntityUtil.isInLiquid(mc.player)
                && !mc.player.isOnLadder()
        ) {
            if (!mc.player.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(0.0, -height.getValue(), 0.0)).isEmpty()) {
                mc.player.motionY -= speed.getValue();
            }
        }
    }

}
