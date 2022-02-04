package me.pignol.swift.client.event.events;

import net.minecraftforge.fml.common.eventhandler.Cancelable;
import net.minecraftforge.fml.common.eventhandler.Event;

@Cancelable
public class TravelEvent extends Event {

    public float strafe;
    public float vertical;
    public float forward;

    public TravelEvent(float strafe, float vertical, float forward)
    {
        this.strafe = strafe;
        this.vertical = vertical;
        this.forward = forward;
    }

}
