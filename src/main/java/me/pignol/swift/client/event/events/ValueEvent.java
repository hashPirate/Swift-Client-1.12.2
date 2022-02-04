package me.pignol.swift.client.event.events;

import me.pignol.swift.api.value.Value;
import net.minecraftforge.fml.common.eventhandler.Event;

public class ValueEvent extends Event {

    private final Value<?> value;

    public ValueEvent(Value<?> value) {
        this.value = value;
    }

    public Value<?> getValue() {
        return value;
    }

}
