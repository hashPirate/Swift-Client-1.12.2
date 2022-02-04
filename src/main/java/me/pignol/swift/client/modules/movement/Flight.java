package me.pignol.swift.client.modules.movement;

import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.MoveEvent;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.block.BlockAir;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.network.play.server.SPacketPlayerPosLook;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class Flight extends Module {

    private final Value<Boolean> noFall = new Value<>("NoFall", false);

    private final Value<Float> acceleration = new Value<>("Acceleration", 0.5F, 0.05F, 5F, 0.05F);
    private final Value<Float> vAcceleration = new Value<>("VAcceleration", 0.5F, 0.05F, 5F, 0.05F);

    private final Value<Float> speedValue = new Value<>("Speed", 1F, 0.1F, 10F, 0.1F);
    private final Value<Float> vSpeedValue = new Value<>("VSpeed", 1F, 0.1F, 10F, 0.1F);
    private final Value<Float> upFactor = new Value<>("UpFactor", 0.5F, 0.1F, 1F, 0.1F);
    private final Value<Float> maxSpeedValue = new Value<>("MaxSpeed", 1F, 0.1F, 10F, 0.1F);

    private final Value<Glide> glide = new Value<>("Glide", Glide.CONSTANT);
    private final Value<Float> glideSpeed = new Value<>("GlideSpeed", 1F, 0.1F, 10F, 0.1F, v -> glide.getValue() != Glide.OFF);
    private final Value<Integer> glideInterval = new Value<>("GlideInterval", 3, 1, 20, 1, v -> glide.getValue() == Glide.DYNAMIC);
    private final Value<Integer> glideTicks = new Value<>("GlideTicks", 1, 1, 5, 1, v -> glide.getValue() == Glide.DYNAMIC);

    private final Value<AntiKick> antiKick = new Value<>("AntiKick", AntiKick.NONE);
    private final Value<Integer> antiKickInterval = new Value<>("AntiKickInterval", 2, 1, 20, 1, v -> antiKick.getValue() != AntiKick.NONE);
    private final Value<Integer> antiKickTicks = new Value<>("AntiKickTicks", 1, 1, 5, 1, v -> glide.getValue() == Glide.DYNAMIC);

    private final Value<Boolean> inAir = new Value<>("InAir", true);
    private final Value<Boolean> inWater = new Value<>("InWater", true);
    private final Value<Boolean> inLava = new Value<>("InLava", true);

    public Flight() {
        super("Flight", Category.MOVEMENT);
    }

    private StopWatch setbackTimer = new StopWatch();
    private long zeroTime = -1L;

    private int glideCounter = 0;
    private int glideTicksCounter = 0;

    private int antiKickCounter = 0;
    private int antiKickTicksCounter = 0;

    @SubscribeEvent
    public void onPlayerMove(MoveEvent event) {
        if (!setbackTimer.passed(350)) return;

        final AxisAlignedBB bb = mc.player.getRidingEntity() != null ? mc.player.getRidingEntity().getEntityBoundingBox() : mc.player.getEntityBoundingBox();
        if (bb != null) {
            int y = (int) bb.minY;
            for (int x = MathHelper.floor(bb.minX); x < MathHelper.floor(bb.maxX) + 1; x++) {
                for (int z = MathHelper.floor(bb.minZ); z < MathHelper.floor(bb.maxZ) + 1; z++) {
                    final Block block = mc.world.getBlockState(new BlockPos(x, y, z)).getBlock();
                    if (block instanceof BlockAir && !inAir.getValue()) return;
                    if ((block == Blocks.LAVA || block == Blocks.FLOWING_LAVA) && !inLava.getValue()) return;
                    if ((block == Blocks.WATER || block == Blocks.FLOWING_WATER) && !inWater.getValue()) return;
                }
            }
        }

        double speed = MathUtil.lerp(0, speedValue.getValue() * 0.2625D, Math.min(1F, (System.currentTimeMillis() - zeroTime) / (1000F * acceleration.getValue())));
        double vSpeed = MathUtil.lerp(0, vSpeedValue.getValue() * 0.4D, Math.min(1F, (System.currentTimeMillis() - zeroTime) / (1000F * vAcceleration.getValue())));

        boolean canGoUp = true;

        if (antiKick.getValue() != AntiKick.NONE) {
            if (antiKickCounter < antiKickInterval.getValue()) {
                antiKickCounter++;
            } else {
                antiKickTicksCounter++;
                if (antiKickTicksCounter >= antiKickTicks.getValue()) {
                    antiKickCounter = 0;
                }
                if (antiKick.getValue() == AntiKick.NORMAL) {
                    speed = 0;
                    canGoUp = false;
                } else {
                    return;
                }
            }
        }

        double netSpeed = Math.sqrt(speed * speed + vSpeed * vSpeed);

        if (glideCounter < glideInterval.getValue()) {
            glideCounter++;
            glideTicksCounter = 0;
        }

        if (glide.getValue() == Glide.CONSTANT || (glideCounter >= glideInterval.getValue() && glide.getValue() == Glide.DYNAMIC)) {
            event.setY(-glideSpeed.getValue() * 0.01D);
            glideTicksCounter ++;
            if (glideTicksCounter >= glideTicks.getValue()) {
                glideCounter = 0;
            }
        }

        if (mc.gameSettings.keyBindJump.isKeyDown() && canGoUp) {
            event.setY(vSpeed * upFactor.getValue());
        } else if (mc.gameSettings.keyBindSneak.isKeyDown()) {
            event.setY(-vSpeed);
        }

        if (netSpeed > maxSpeedValue.getValue() * 0.6625D) {
            speed = Math.min(speedValue.getValue() * 0.2625D, Math.sqrt(netSpeed * netSpeed - event.getY() * event.getY()));
        }

        double forward = mc.player.movementInput.moveForward;
        double strafe = mc.player.movementInput.moveStrafe;
        float yaw = mc.player.rotationYaw;

        if (forward == 0.0D && strafe == 0.0D) {
            event.setX(0.0D);
            event.setZ(0.0D);
            zeroTime = System.currentTimeMillis();
        } else {
            if (forward != 0.0D) {
                if (strafe > 0.0D) {
                    yaw += (float) (forward > 0.0D ? -45 : 45);
                } else if (strafe < 0.0D) {
                    yaw += (float) (forward > 0.0D ? 45 : -45);
                }

                strafe = 0.0D;

                if (forward > 0.0D) {
                    forward = 1.0D;
                } else if (forward < 0.0D) {
                    forward = -1.0D;
                }
            }

            event.setX(forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
            event.setZ(forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));
        }
    }

    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketPlayer && noFall.getValue()) {
            ((CPacketPlayer) event.getPacket()).onGround = (true);
        }
    }

    @SubscribeEvent
    public void onPacketReceive(PacketEvent.Receive event) {
        if (event.getPacket() instanceof SPacketPlayerPosLook) {
            setbackTimer.reset();
            zeroTime = System.currentTimeMillis();
        }
    }


    public void onEnable() {
        zeroTime = System.currentTimeMillis();
        glideCounter = 0;
    }

    private enum Glide {
        OFF, CONSTANT, DYNAMIC
    }

    private enum AntiKick {
        NONE, NORMAL, TOGGLE
    }

}
