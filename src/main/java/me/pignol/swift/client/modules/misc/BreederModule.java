package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.RotationUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.network.play.client.CPacketUseEntity;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class BreederModule extends Module {

    private final Value<Boolean> rotate = new Value<>("Rotate", true);
    private final Value<Float> range = new Value<>("Range", 5.0F, 1.0F, 6.0F);

    public BreederModule() {
        super("Breeder", Category.MISC);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (event.getStage() == Stage.POST) {
            return;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityAnimal && mc.player.getDistanceSq(entity) < MathUtil.square(range.getValue())) {
                EntityAnimal animal = (EntityAnimal) entity;
                if (animal.getHealth() > 0 && !animal.isDead) {
                    if (!animal.isChild() && !animal.isInLove()) {
                        int breedability = getBreedability(animal);
                        if (breedability != 0) {
                            if (rotate.getValue()) {
                                float[] rots = RotationUtil.getRotations(animal.posX, animal.posY, animal.posZ);
                                RotationManager.getInstance().setPlayerRotations(rots[0], rots[1]);
                            }
                            mc.getConnection().sendPacket(new CPacketUseEntity(animal, breedability == 1 ? EnumHand.MAIN_HAND : EnumHand.OFF_HAND));
                            return;
                        }
                    }
                }
            }
        }
    }

    public int getBreedability(EntityAnimal animal) {
        if (animal.isBreedingItem(mc.player.getHeldItemMainhand())) {
            return 1;
        } else if (animal.isBreedingItem(mc.player.getHeldItemOffhand())) {
            return 2;
        }
        return 0;
    }

}
