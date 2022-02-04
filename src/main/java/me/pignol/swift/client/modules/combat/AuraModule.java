package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.util.RotationUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.Stage;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.managers.RotationManager;
import me.pignol.swift.client.managers.ServerManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.Item;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemSword;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AuraModule extends Module {

    public static AuraModule INSTANCE = new AuraModule();

    private final Value<TpsSync> tpsSync = new Value<>("TPS-Sync", TpsSync.LATEST);
    private final Value<Float> range = new Value<>("Range", 5f, 0f, 6f);
    private final Value<Boolean> rotate = new Value<>("Rotate", true);
    private final Value<Boolean> rotateOffset = new Value<>("RotateOffset", true, v -> rotate.getValue());
    private final Value<Boolean> alwaysRotate = new Value<>("AlwaysRotate", false, v -> rotate.getValue());
    private final Value<Boolean> raytrace = new Value<>("Raytrace", true);
    private final Value<Float> raytraceRange = new Value<>("RaytraceRange", 2.0F, 0.0F, 5.0F, v -> raytrace.getValue());
    private final Value<Boolean> pauseOnSurround = new Value<>("PauseOnSurround", true, v -> rotate.getValue());
    private final Value<Boolean> weaponsOnly = new Value<>("WeaponsOnly", true, v -> rotate.getValue());
    private final Value<Boolean> hide = new Value<>("Hide", false, v -> weaponsOnly.getValue());

    private EntityPlayer lastTarget = null;
    private boolean offsetState, rotatedThisTick;

    public AuraModule() {
        super("Aura", Category.COMBAT);
        setSuffix("Single");
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event)
    {
        if (event.getStage() == Stage.POST)
            return;

        if (Surround.isPlacing && pauseOnSurround.getValue())
        {
            return;
        }

        rotatedThisTick = false;
        EntityPlayer currentTarget = null;

        if (!isValidTarget(lastTarget))
        {
            lastTarget = null;
        }

        double maxDistance = -1.0D;
        for (EntityPlayer entity : mc.world.playerEntities)
        {
            if (isValidTarget(entity))
            {
                final double distance = entity.getDistance(mc.player);

                if ((maxDistance == -1.0D || distance < maxDistance))
                {
                    maxDistance = distance;
                    currentTarget = entity;
                }
            }
        }

        if (weaponsOnly.getValue()) {
            Item held = mc.player.getHeldItemMainhand().getItem();
            if (!(held instanceof ItemSword) && !(held instanceof ItemAxe)) {
                if (hide.getValue()) {
                    setDrawn(false);
                }
                return;
            } else if (hide.getValue()) {
                setDrawn(true);
            }
        }

        if (lastTarget != null && alwaysRotate.getValue()) {
            doRotate(lastTarget);
        }

        TpsSync sync = tpsSync.getValue();
        if (currentTarget != null && mc.player.getCooledAttackStrength(sync.getTicks()) >= 1.0F) {
            doRotate(currentTarget);
            mc.playerController.attackEntity(mc.player, currentTarget);
            mc.player.swingArm(EnumHand.MAIN_HAND);
            lastTarget = currentTarget;
        }
    }

    private void doRotate(EntityPlayer entity) {
        if (rotate.getValue() && !rotatedThisTick) {
            float offset = rotateOffset.getValue() ? (offsetState ? 0.004F : -0.004F) : 0;
            offsetState = !offsetState;

            float[] angles = RotationUtil.getRotations(entity.posX, entity.posY + 1, entity.posZ);
            RotationManager.getInstance().setPlayerRotations(angles[0] + offset, angles[1] + offset);
            rotatedThisTick = true;
        }
    }

    public boolean isValidTarget(EntityPlayer player) {
        if (player == null || player == mc.player || player.isDead || player.getHealth() <= 0 || FriendManager.getInstance().isFriend(player.getName())) {
            return false;
        }

        float distance = player.getDistance(mc.player);
        if (distance > range.getValue() || raytrace.getValue() && !mc.player.canEntityBeSeen(player) && distance > raytraceRange.getValue()) {
            return false;
        }

        return true;
    }

    public EntityPlayer getLastTarget() {
        return lastTarget;
    }

    public enum Mode {
        SINGLE,
        FOCUS
    }

    public enum TpsSync {
        AVERAGE {
            @Override
            public float getTicks()
            {
                return 20.0F - ServerManager.getInstance().getAverageTPS();
            }
        },
        LATEST {
            @Override
            public float getTicks()
            {
                return 20.0F - ServerManager.getInstance().getTPS();
            }
        },
        NONE {
            @Override
            public float getTicks()
            {
                return 0;
            }
        };

        public abstract float getTicks();
    }

}
