package me.pignol.swift.client.modules.player;

import com.sun.org.apache.xpath.internal.operations.Bool;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityPig;
import net.minecraft.util.MovementInput;
import net.minecraft.world.chunk.EmptyChunk;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class PigSpeed extends Module {

    private final Value<Boolean> flight = new Value<>("Flight", false);
    private final Value<Integer> speed = new Value<>("Speed",1,0,200);
    private final Value<Boolean> stop = new Value<>("Stop",true);

    public PigSpeed() {
        super("PigSpeed", Category.PLAYER);
    } //everytime i make dis module my life gets even worse is it a coincidence or na

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if ((mc.world != null) && (mc.player.getRidingEntity() != null)) {
            Entity riding = mc.player.getRidingEntity();
            if (riding instanceof EntityPig) {
                ridePig(riding);
            }
        }
    }

    private void ridePig(Entity entity) {
        if (!flight.getValue()) {
            entity.motionY = -0.4D;
        }

        if (flight.getValue()) {
            if (mc.gameSettings.keyBindJump.isKeyDown())
                entity.motionY = speed.getValue();
        }

        moveForward(entity, speed.getValue() * 3.8D);
    }


    private void moveForward(Entity entity, double speed) {
        if (entity != null) {
            MovementInput movementInput = mc.player.movementInput;

            double forward = movementInput.moveForward;
            double strafe = movementInput.moveStrafe;
            boolean movingForward = forward != 0;
            boolean movingStrafe = strafe != 0;
            float yaw = mc.player.rotationYaw;

            if (!movingForward && !movingStrafe) {
                setEntitySpeed(entity, 0, 0);
            } else {
                if (forward != 0.0D) {
                    if (strafe > 0.0D) {
                        yaw += (forward > 0.0D ? -45 : 45);
                    } else if (strafe < 0.0D) {
                        yaw += (forward > 0.0D ? 45 : -45);
                    }
                    strafe = 0.0D;
                    if (forward > 0.0D) {
                        forward = 1.0D;
                    } else {
                        forward = -1.0D;
                    }
                }

                double motX = (forward * speed * Math.cos(Math.toRadians(yaw + 90.0F)) + strafe * speed * Math.sin(Math.toRadians(yaw + 90.0F)));
                double motZ = (forward * speed * Math.sin(Math.toRadians(yaw + 90.0F)) - strafe * speed * Math.cos(Math.toRadians(yaw + 90.0F)));

                if (isBorderingChunk(entity, motX, motZ))
                    motX = motZ = 0;

                setEntitySpeed(entity, motX, motZ);
            }
        }
    }

    private void setEntitySpeed(Entity entity, double motX, double motZ) {
        entity.motionX = motX;
        entity.motionZ = motZ;
    }

    private boolean isBorderingChunk(Entity entity, double motX, double motZ) {
        return stop.getValue() && mc.world.getChunk((int) (entity.posX + motX) >> 4, (int) (entity.posZ + motZ) >> 4) instanceof EmptyChunk;
    }
}