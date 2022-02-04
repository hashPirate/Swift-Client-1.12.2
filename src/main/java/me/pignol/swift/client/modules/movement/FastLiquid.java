package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.util.MovementUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.MoveEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastLiquid extends Module {

    private final Value<Boolean> strafe = new Value<>("Strafe", true);
    private final Value<Float> waterSpeed = new Value<>("WaterSpeed", 0.5F, 0.00F, 2.0F, 0.01F, v -> strafe.getValue());
    private final Value<Float> lavaSpeed = new Value<>("LavaSpeed", 0.2F, 0.00F, 2.0F, 0.01F, v -> strafe.getValue());
    private final Value<Float> down = new Value<>("DownMotion", 0.1f, 0.01f, 1.0f);
    private final Value<Float> up = new Value<>("UpMotion", 0.1f, 0.01f, 1.0f);

    public FastLiquid() {
        super("FastLiquid", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onMove(MoveEvent event) {
        boolean inLava = mc.player.isInLava();
        boolean inWater = mc.player.isInWater();
        if (inLava || inWater) {
            if (mc.gameSettings.keyBindSneak.isKeyDown()) {
                event.setY(event.getY() - down.getValue());
            } else if (mc.gameSettings.keyBindJump.isKeyDown()) {
                event.setY(event.getY() + up.getValue());
            }

            double[] strafe = MovementUtil.strafe(inLava ? lavaSpeed.getValue() : waterSpeed.getValue());
            event.setX(strafe[0]);
            event.setZ(strafe[1]);
        }
    }

}
