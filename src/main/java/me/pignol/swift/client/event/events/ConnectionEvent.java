package me.pignol.swift.client.event.events;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.eventhandler.Event;

import java.util.UUID;

public class ConnectionEvent extends Event {

    private int stage;
    private final UUID uuid;
    private final EntityPlayer entity;
    private final String name;

    public ConnectionEvent(int stage, UUID uuid, String name) {
        this.stage = stage;
        this.uuid = uuid;
        this.name = name;
        this.entity = null;
    }

    public ConnectionEvent(int stage, EntityPlayer entity, UUID uuid, String name) {
        this.stage = stage;
        this.entity = entity;
        this.uuid = uuid;
        this.name = name;
    }

    public String getName() {
        return this.name;
    }

    public UUID getUuid() {
        return this.uuid;
    }

    public EntityPlayer getEntity() {
        return this.entity;
    }

    public int getStage() {
        return stage;
    }

}
