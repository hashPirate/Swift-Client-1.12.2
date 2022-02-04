package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AntiPopModule extends Module {

    private final Value<Integer> delay = new Value<>("Delay", 300, 0, 1000);
    private final Value<Float> range = new Value<>("Range", 5.0F, 0.1F, 6.0F);
    private final Value<Float> wallRange = new Value<>("WallRange", 5.0F, 0.1F, 6.0F);
    private final Value<Float> health = new Value<>("Health", 16.0F, 0.1F, 36.0F);

    private final StopWatch timer = new StopWatch();

    public AntiPopModule() {
        super("AntiPop", Category.PLAYER);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (EntityUtil.getHealth(mc.player) < health.getValue() && timer.passed(delay.getValue())) {
            Entity ignore = null;
            for (Entity entity : mc.world.loadedEntityList) {
                if (entity instanceof EntityEnderCrystal) {
                    if (ignore == null) {
                        if (entity.getEntityBoundingBox().intersects(mc.player.getEntityBoundingBox())) {
                            ignore = entity;
                        }
                    } else if (entity != ignore) {
                        if (isValid(entity) && ignore.getDistanceSq(entity) < 36) {
                            mc.getConnection().sendPacket(new CPacketUseEntity(entity));
                            mc.player.swingArm(EnumHand.MAIN_HAND);
                            timer.reset();
                            break;
                        }
                    }
                }
            }
        }
    }

    private boolean isValid(Entity crystal) {
        if (!crystal.isDead) {
            final double distance = mc.player.getDistanceSq(crystal);
            if (distance > MathUtil.square(range.getValue())) {
                return false;
            }

            if (distance > MathUtil.square(wallRange.getValue())) {
                return mc.player.canEntityBeSeen(crystal);
            }

            return true;
        }

        return false;
    }

}
