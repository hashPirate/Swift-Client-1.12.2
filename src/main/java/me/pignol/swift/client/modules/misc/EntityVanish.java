package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class EntityVanish extends Module {

    public Entity entity;

    public EntityVanish() {
        super("EntityVanish", Category.MISC);
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            return;
        }
        if (mc.player.getRidingEntity() != null) {
            mc.renderGlobal.loadRenderers();
            entity = mc.player.getRidingEntity();
            mc.player.dismountRidingEntity();
            mc.world.removeEntity(this.entity);
        }
    }

    @Override
    public void onDisable() {
        if (entity == null || mc.world == null || mc.player == null) {
            return;
        }
        entity.isDead = false;
        mc.world.loadedEntityList.add(this.entity);
        mc.player.startRiding(this.entity, true);
        entity = null;
    }


    @SubscribeEvent
    public void onClientUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) {
            return;
        }
        if (this.entity == null && mc.player.getRidingEntity() != null) {
            this.entity = mc.player.getRidingEntity();
            mc.player.dismountRidingEntity();
            mc.world.removeEntity(this.entity);
        }
        if (this.entity != null) {
            this.entity.setPosition(mc.player.posX, mc.player.posY, mc.player.posZ);
            mc.player.connection.sendPacket(new CPacketVehicleMove(this.entity));
        }
    }

}