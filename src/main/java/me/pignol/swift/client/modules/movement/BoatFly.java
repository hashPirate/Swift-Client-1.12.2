package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.util.MovementUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.item.EntityBoat;
import net.minecraft.network.play.client.CPacketConfirmTeleport;
import net.minecraft.network.play.client.CPacketInput;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.client.CPacketVehicleMove;
import net.minecraft.network.play.server.SPacketMoveVehicle;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.event.entity.living.LivingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BoatFly extends Module {

    public Value<Double> speed = new Value<>("Speed", 3.0, 1.0, 10.0);
    public Value<Double> verticalSpeed = new Value<>("VerticalSpeed", 3.0, 1.0, 10.0);
    public Value<Boolean> noKick = new Value<>("No-Kick", true);
    public Value<Boolean> packet = new Value<>("Packet", true);
    public Value<Integer> packets = new Value<>("Packets", 3, 1, 5, v -> this.packet.getValue());
    public Value<Integer> interact = new Value<>("Delay", 2, 1, 20);
    public static BoatFly INSTANCE;
    private EntityBoat target;
    private int teleportID;

    public BoatFly() {
        super("BoatFly", Category.MOVEMENT);
        INSTANCE = this;
    }

    @SubscribeEvent
    public void onUpdate(LivingEvent.LivingUpdateEvent event) {
        if (event.getEntityLiving() != mc.player)
            return;
        if (mc.player == null) {
            return;
        }
        if (mc.world == null || mc.player.getRidingEntity() == null) {
            return;
        }
        if (mc.player.getRidingEntity() instanceof EntityBoat) {
            this.target = (EntityBoat)mc.player.ridingEntity;
        }
        mc.player.getRidingEntity().setNoGravity(true);
        mc.player.getRidingEntity().motionY = 0.0;
        if (mc.gameSettings.keyBindJump.isKeyDown()) {
            mc.player.getRidingEntity().onGround = false;
            mc.player.getRidingEntity().motionY = this.verticalSpeed.getValue() / 10.0;
        }
        if (mc.gameSettings.keyBindSprint.isKeyDown()) {
            mc.player.getRidingEntity().onGround = false;
            mc.player.getRidingEntity().motionY = -(this.verticalSpeed.getValue() / 10.0);
        }
        double[] normalDir = MovementUtil.strafe(this.speed.getValue() / 2.0);
        if (mc.player.movementInput.moveStrafe != 0.0f || mc.player.movementInput.moveForward != 0.0f) {
            mc.player.getRidingEntity().motionX = normalDir[0];
            mc.player.getRidingEntity().motionZ = normalDir[1];
        } else {
            mc.player.getRidingEntity().motionX = 0.0;
            mc.player.getRidingEntity().motionZ = 0.0;
        }
        if (this.noKick.getValue()) {
            if (mc.gameSettings.keyBindJump.isKeyDown()) {
                if (mc.player.ticksExisted % 8 < 2) {
                    mc.player.getRidingEntity().motionY = -0.04f;
                }
            } else if (mc.player.ticksExisted % 8 < 4) {
                mc.player.getRidingEntity().motionY = -0.08f;
            }
        }
        this.handlePackets(mc.player.getRidingEntity().motionX, mc.player.getRidingEntity().motionY, mc.player.getRidingEntity().motionZ);
    }

    public void handlePackets(double x, double y, double z) {
        if (this.packet.getValue()) {
            Vec3d vec = new Vec3d(x, y, z);
            if (mc.player.getRidingEntity() == null) {
                return;
            }
            Vec3d position = mc.player.getRidingEntity().getPositionVector().add(vec);
            mc.player.getRidingEntity().setPosition(position.x, position.y, position.z);
            mc.player.connection.sendPacket(new CPacketVehicleMove(mc.player.getRidingEntity()));
            for (int i = 0; i < this.packets.getValue(); ++i) {
                mc.player.connection.sendPacket(new CPacketConfirmTeleport(this.teleportID++));
            }
        }
    }

    @SubscribeEvent
    public void onSendPacket(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketVehicleMove && mc.player.isRiding() && mc.player.ticksExisted % this.interact.getValue() == 0) {
            mc.playerController.interactWithEntity(mc.player, mc.player.ridingEntity, EnumHand.OFF_HAND);
        }
        if ((event.getPacket() instanceof CPacketPlayer.Rotation || event.getPacket() instanceof CPacketInput) && mc.player.isRiding()) {
            event.setCanceled(true);
        }
    }

    @SubscribeEvent
    public void onReceivePacket(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketMoveVehicle && mc.player.isRiding()) {
            event.setCanceled(true);
        }
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            this.teleportID = ((SPacketPlayerPosLook)event.getPacket()).teleportId;
        }
    }

}
