package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.interfaces.mixin.INetworkManager;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class CriticalsModule extends Module {

    private final Value<Mode> mode = new Value<>("Mode", Mode.PACKET);
    private final Value<Boolean> ncpStrict = new Value<>("NCP-Strict", true, v -> mode.getValue() == Mode.PACKET);
    private final Value<Boolean> boats = new Value<>("Boats", false);
    private final Value<Integer> hits = new Value<>("Hits",  5, 1, 15, v -> boats.getValue());

    public CriticalsModule() {
        super("Criticals", Category.COMBAT);
    }

    @Override
    public String getSuffix() {
        switch (mode.getValue()) {
            case JUMP:
                return "Jump";
            case PACKET:
                return "Packet";
            case MINI:
                return "Mini";
            default:
                return "";
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketUseEntity && !isNull()) {
            final CPacketUseEntity packet = (CPacketUseEntity) event.getPacket();
            if (mc.player.onGround && packet.getAction() == CPacketUseEntity.Action.ATTACK) {
                final Entity entity = packet.getEntityFromWorld(mc.world);
                if (entity instanceof EntityLivingBase) {
                    switch (mode.getValue()) {
                        case PACKET:
                            if (ncpStrict.getValue()) {
                                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.0625D, mc.player.posZ, false));
                                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.1E-5D, mc.player.posZ, false));
                            } else {
                                mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.1F, mc.player.posZ, false));
                            }
                            mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY, mc.player.posZ, false));
                            break;

                        case MINI:
                            mc.player.addVelocity(0, 0.1, 0);
                            mc.player.fallDistance = 0.1F;
                            mc.player.onGround = false;
                            break;

                        case JUMP:
                            mc.player.jump();
                            break;
                    }
                } else if (entity instanceof EntityBoat) {
                    for (int i = 0; i < hits.getValue(); ++i) {
                        ((INetworkManager) mc.getConnection().getNetworkManager()).sendPacketNoEvent(new CPacketUseEntity(entity));
                    }
                }
            }
        }
    }

    public enum Mode {
        PACKET,
        MINI,
        JUMP
    }

}
