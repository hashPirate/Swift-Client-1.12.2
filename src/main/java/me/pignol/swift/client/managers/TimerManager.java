package me.pignol.swift.client.managers;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Module;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TimerManager implements Globals
{

    private final static TimerManager INSTANCE = new TimerManager();

    public static TimerManager getInstance()
    {
        return INSTANCE;
    }

    private Module currentModule;
    private int priority;
    private float timerSpeed;
    private boolean active = false;
    private boolean tpsSync = false;

    public void updateTimer(Module module, int priority, float timerSpeed)
    {
        if (module == currentModule)
        {
            this.priority = priority;
            this.timerSpeed = timerSpeed;
            this.active = true;
        } else if (priority > this.priority || !this.active)
        {
            this.currentModule = module;
            this.priority = priority;
            this.timerSpeed = timerSpeed;
            this.active = true;
        }
    }

    public void resetTimer(Module module)
    {
        if (this.currentModule == module)
        {
            active = false;
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event)
    {
        if (mc.world == null || mc.player == null)
        {
            mc.timer.tickLength = 50.0f;
            return;
        }
        mc.timer.tickLength = (active ? (50.0f / timerSpeed) : 50.0f);

    }

    public boolean isTpsSync()
    {
        return tpsSync;
    }

    public void setTpsSync(boolean tpsSync)
    {
        this.tpsSync = tpsSync;
    }

}
