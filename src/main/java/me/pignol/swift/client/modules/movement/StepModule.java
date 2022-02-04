package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.util.MovementUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.MoveEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.TimerManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class StepModule extends Module {

    private final Value<Mode> mode = new Value<>("Mode", Mode.NORMAL);
    private final Value<Float> stepHeight = new Value<>("StepHeight", 1f, 0.5f, 4f, 0.5f);
    private final Value<Boolean> upwards = new Value<>("Upwards", true);
    private final Value<Boolean> useTimer = new Value<>("UseTimer", false);

    public static final double[] oneBlockNCP = {0.42, 0.753};
    public static final double[] tallOneBlockNCP = {0.42, 0.75, 1.0, 1.16, 1.23, 1.2};
    public static final double[] twoBlockNCP = {0.42, 0.78, 0.63, 0.51, 0.9, 1.21, 1.45, 1.43};
    public static final double[] field_1734 = {0.425, 0.821, 0.699, 0.599, 1.022, 1.372, 1.652, 1.869, 2.019, 1.907};

    private final StopWatch timer = new StopWatch();

    private int curStep = 0;
    private int curMotionStep = 0;

    public StepModule() {
        super("Step", Category.PLAYER);
    }

    @SubscribeEvent
    public void onPlayerMove(MoveEvent event) {
        if (mode.getValue() != Mode.MOTION || !upwards.getValue()) return;

        if (!mc.player.collidedHorizontally)
            return;
        if (mc.player.onGround && canStep()) {
            mc.player.motionY = 0.0D;
            event.setY(0.41999998688698D);

            curMotionStep = 1;
        } else if (curMotionStep == 1) {
            event.setY(0.33319999363422D);
            curMotionStep = 2;
        } else if (curMotionStep == 2) {
            float rotationYaw = mc.player.rotationYaw;
            if (mc.player.moveForward < 0.0F)
                rotationYaw += 180.0F;
            float forward = 1.0F;
            if (mc.player.moveForward < 0.0F) {
                forward = -0.5F;
            } else if (mc.player.moveForward > 0.0F) {
                forward = 0.5F;
            }
            if (mc.player.moveStrafing > 0.0F)
                rotationYaw -= 90.0F * forward;
            if (mc.player.moveStrafing < 0.0F)
                rotationYaw += 90.0F * forward;

            float yaw = (float) Math.toRadians(rotationYaw);

            event.setY(0.24813599859094704D);
            event.setX(-MathHelper.sin(yaw) * 0.7D);
            event.setZ(MathHelper.cos(yaw) * 0.7D);

            curMotionStep = 0;
        }
    }

    @SubscribeEvent
    public void onUpdateWalkingPlayer(UpdateEvent event) {
        if (event.getStage() == Stage.POST)
            return;


        if (upwards.getValue() && !mc.player.isInWater() && mc.player.onGround && !mc.player.isOnLadder() && !mc.player.movementInput.jump && mc.player.collidedVertically && (double) mc.player.fallDistance < 0.1) {
            if (mode.getValue() == Mode.VANILLA) {
                mc.player.stepHeight = stepHeight.getValue();
            } else if (mode.getValue() == Mode.NORMAL) {
                if (!timer.passed(320)) {
                    return;
                }

                double currentStepHeight = getCurrentStepHeight();

                if (currentStepHeight == 0.0D) {
                    return;
                }

                if (currentStepHeight <= 1.0D) {
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75D, mc.player.posZ, mc.player.onGround));
                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ);
                    return;
                }

                if (currentStepHeight <= stepHeight.getValue() && currentStepHeight <= 1.5D) {
                    event.setCanceled(true);
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.0D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.23D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.2D, mc.player.posZ, mc.player.onGround));
                    mc.player.setPosition(mc.player.posX, mc.player.posY + currentStepHeight, mc.player.posZ);
                    return;
                }

                if (currentStepHeight <= stepHeight.getValue()) {
                    event.setCanceled(true);
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.42D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.7800000000000002D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.63D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.51D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.9D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.21D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.45D, mc.player.posZ, mc.player.onGround));
                    mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.43D, mc.player.posZ, mc.player.onGround));
                    mc.player.setPosition(mc.player.posX, mc.player.posY + currentStepHeight, mc.player.posZ);
                }
            } else if (mode.getValue() == Mode.NCP) {
                double[] dir = MovementUtil.strafe(0.1);

                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.0, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 0.6, dir[1])).isEmpty() && stepHeight.getValue() >= 1.0){
                    for (double v : oneBlockNCP) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }

                    if (useTimer.getValue()){
                        TimerManager.getInstance().updateTimer(this, 15, 0.6F);
                    }

                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.0, mc.player.posZ);
                    curStep = 1;
                }
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.4, dir[1])).isEmpty() && stepHeight.getValue() >= 1.5){
                    for (double v : tallOneBlockNCP) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }

                    if (useTimer.getValue()){
                        TimerManager.getInstance().updateTimer(this, 15, 0.35F);
                    }

                    mc.player.setPosition(mc.player.posX, mc.player.posY + 1.5, mc.player.posZ);
                    curStep = 1;
                }
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.1, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 1.9, dir[1])).isEmpty() && stepHeight.getValue() >= 2.0){
                    for (double v : twoBlockNCP) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }

                    if (useTimer.getValue()){
                        TimerManager.getInstance().updateTimer(this, 15, 0.25F);
                    }

                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
                    curStep = 2;
                }
                if (mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.6, dir[1])).isEmpty() && !mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(dir[0], 2.4, dir[1])).isEmpty() && stepHeight.getValue() >= 2.5){
                    for (double v : field_1734) {
                        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + v, mc.player.posZ, mc.player.onGround));
                    }

                    if (useTimer.getValue()){
                        TimerManager.getInstance().updateTimer(this, 15, 0.15F);
                    }

                    mc.player.setPosition(mc.player.posX, mc.player.posY + 2.5, mc.player.posZ);
                    curStep = 2;
                }
            }
        } else if (mode.getValue() == Mode.VANILLA) {
            mc.player.stepHeight = 0.5F;
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            timer.reset();
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.world == null || mc.player == null) return;

        if (mc.player.getRidingEntity() != null) {
            timer.reset();
        }

        if (useTimer.getValue()){
            if (curStep == 0){
                TimerManager.getInstance().resetTimer(this);
            } else{
                curStep--;
            }
        } else {
            TimerManager.getInstance().resetTimer(this);
        }
    }

    @Override
    public void onDisable() {
        mc.player.stepHeight = 0.5F;
        TimerManager.getInstance().resetTimer(this);
    }

    private boolean canStep() {
        float rotationYaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0F)
            rotationYaw += 180.0F;
        float forward = 1.0F;
        if (mc.player.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (mc.player.moveStrafing > 0.0F)
            rotationYaw -= 90.0F * forward;
        if (mc.player.moveStrafing < 0.0F)
            rotationYaw += 90.0F * forward;

        float yaw = (float) Math.toRadians(rotationYaw);

        double x = -MathHelper.sin(yaw) * 0.4D;
        double z = MathHelper.cos(yaw) * 0.4D;
        return mc.world.getCollisionBoxes(mc.player, mc.player.getEntityBoundingBox().offset(x, 1.001335979112147D, z)).isEmpty();
    }

    private double getCurrentStepHeight() {
        boolean collided = (mc.player.onGround && mc.player.collidedHorizontally);

        if (!collided) {
            return 0.0D;
        }

        double maximumY = -1.0D;

        float rotationYaw = mc.player.rotationYaw;
        if (mc.player.moveForward < 0.0F)
            rotationYaw += 180.0F;
        float forward = 1.0F;
        if (mc.player.moveForward < 0.0F) {
            forward = -0.5F;
        } else if (mc.player.moveForward > 0.0F) {
            forward = 0.5F;
        }
        if (mc.player.moveStrafing > 0.0F)
            rotationYaw -= 90.0F * forward;
        if (mc.player.moveStrafing < 0.0F)
            rotationYaw += 90.0F * forward;

        AxisAlignedBB expandedBB = mc.player.getEntityBoundingBox().offset(0.0D, 0.05D, 0.0D).grow(0.05D);
        expandedBB = expandedBB.setMaxY(expandedBB.maxY + (stepHeight.getValue()));

        for (AxisAlignedBB axisAlignedBB : mc.world.getCollisionBoxes(mc.player, expandedBB)) {
            if (axisAlignedBB.maxY > maximumY)
                maximumY = axisAlignedBB.maxY;
        }

        maximumY -= mc.player.posY;
        return (maximumY > 0.0D && maximumY <= stepHeight.getValue()) ? maximumY : 0.0D;
    }

    private enum Mode {
        VANILLA, NORMAL, NCP, MOTION
    }

}
