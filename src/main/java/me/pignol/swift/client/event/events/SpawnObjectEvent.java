package me.pignol.swift.client.event.events;

import net.minecraft.entity.Entity;
import net.minecraftforge.fml.common.eventhandler.Event;

public class SpawnObjectEvent extends Event {

    private final Entity entity;

    public SpawnObjectEvent(Entity packet) {
        this.entity = packet;
    }

    public Entity getEntity() {
        return entity;
    }

}
