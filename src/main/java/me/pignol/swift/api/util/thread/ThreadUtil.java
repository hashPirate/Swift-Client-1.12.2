package me.pignol.swift.api.util.thread;

import me.pignol.swift.api.interfaces.Globals;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ThreadUtil implements Globals
{

    public static void scheduleEvent(Event event)
    {
        mc.addScheduledTask(() -> MinecraftForge.EVENT_BUS.post(event));
    }

}
