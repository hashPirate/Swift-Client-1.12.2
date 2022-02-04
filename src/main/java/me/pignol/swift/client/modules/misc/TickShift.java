package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.MovementUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TickShift extends Module {

    private final Value<Integer> ticksVal = new Value<>("Ticks", 18, 1, 100);
    private final Value<Boolean> reset = new Value<>("Reset", false);
    private final Value<Integer> resetMS = new Value<>("ResetMS", 2500, 1, 5000, v -> reset.getValue());
    private final Value<Float> timer = new Value<>("Timer", 1.2F, 1.0F, 3.0F);

    private final StopWatch resetTimer = new StopWatch();

    private int tick = 0;

    public TickShift() {
        super("TickShift", Category.MISC);
    }

    @Override
    public void onEnable() {
        tick = 0;
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (tick <= 0) {
            tick = 0;
            mc.timer.tickLength = 50f;
        }

        if (tick > 0 && MovementUtil.isMoving()) {
            tick--;
            mc.timer.tickLength = 50f / timer.getValue();
            resetTimer.reset();
        }

        if (!MovementUtil.isMoving())
            tick++;
        if (tick >= ticksVal.getValue())
            tick = ticksVal.getValue();
    }

    @Override
    public void onDisable() {
        mc.timer.tickLength = 50f;
    }

}
