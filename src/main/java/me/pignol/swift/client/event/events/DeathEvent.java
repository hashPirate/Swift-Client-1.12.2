package me.pignol.swift.client.event.events;

import net.minecraft.entity.EntityLivingBase;
import net.minecraftforge.fml.common.eventhandler.Event;

public class DeathEvent extends Event {

    private final EntityLivingBase entity;

    public DeathEvent(EntityLivingBase entity) {
        this.entity = entity;
    }

    public EntityLivingBase getEntity() {
        return entity;
    }

}
