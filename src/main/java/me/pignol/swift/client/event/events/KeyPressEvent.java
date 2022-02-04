package me.pignol.swift.client.event.events;

import net.minecraftforge.fml.common.eventhandler.Event;

public class KeyPressEvent extends Event {

    private final int key;
    private final boolean state;

    public KeyPressEvent(int key, boolean state) {
        this.key = key;
        this.state = state;
    }

    public int getKey() {
        return key;
    }

    public boolean getState() {
        return state;
    }

}
