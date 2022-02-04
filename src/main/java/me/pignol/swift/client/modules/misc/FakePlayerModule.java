package me.pignol.swift.client.modules.misc;

import com.mojang.authlib.GameProfile;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.event.events.ValueEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.entity.EntityOtherPlayerMP;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.MoverType;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

import java.util.LinkedList;
import java.util.Queue;
import java.util.UUID;

public class FakePlayerModule extends Module {

    private final Value<Boolean> record = new Value<>("Record", true);
    private final Value<Boolean> play = new Value<>("Play", true);

    private final Queue<Location> recordedPositions = new LinkedList<>();

    private EntityOtherPlayerMP fake;

    public FakePlayerModule() {
        super("FakePlayer", Category.MISC);
    }

    @SubscribeEvent
    public void onValueChange(ValueEvent event) {
        if (event.getValue() == record && record.getValue()) {
            recordedPositions.clear();
        }
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (record.getValue()) {
            Location location = new Location(mc.player.posX, mc.player.posY, mc.player.posZ);
            location.moveForward = mc.player.moveForward;
            location.moveStrafe = mc.player.moveStrafing;
            location.moveVertical = mc.player.moveVertical;
            location.yaw = mc.player.rotationYaw;
            location.pitch = mc.player.rotationPitch;
            recordedPositions.add(location);
        }

        if (fake == null)
            return;

        fake.setHeldItem(EnumHand.OFF_HAND, new ItemStack(Items.TOTEM_OF_UNDYING));

        if (play.getValue() && fake != null) {
            Location loc = recordedPositions.poll();
            if (loc != null) {
                fake.setLocationAndAngles(loc.posX, loc.posY, loc.posZ, loc.yaw, loc.pitch);
                fake.rotationYawHead = loc.yaw;
                travel(fake, loc.moveStrafe, loc.moveVertical, loc.moveForward);
            } else {
                play.setValue(false);
            }
        }
    }

    public void travel(EntityLivingBase entity, float strafe, float vertical, float forward) {
        double d0 = entity.posY;
        float f1 = 0.8F;
        float f2 = 0.02F;
        float f3 = (float) EnchantmentHelper.getDepthStriderModifier(entity);

        if (f3 > 3.0F) {
            f3 = 3.0F;
        }

        if (!entity.onGround) {
            f3 *= 0.5F;
        }

        if (f3 > 0.0F) {
            f1 += (0.54600006F - f1) * f3 / 3.0F;
            f2 += (entity.getAIMoveSpeed() - f2) * f3 / 3.0F;
        }

        entity.moveRelative(strafe, vertical, forward, f2);
        entity.move(MoverType.SELF, entity.motionX, entity.motionY, entity.motionZ);
        entity.motionX *= (double) f1;
        entity.motionY *= 0.800000011920929D;
        entity.motionZ *= (double) f1;

        if (!entity.hasNoGravity()) {
            entity.motionY -= 0.02D;
        }

        if (entity.collidedHorizontally && entity.isOffsetPositionInLiquid(entity.motionX, entity.motionY + 0.6000000238418579D - entity.posY + d0, entity.motionZ)) {
            entity.motionY = 0.30000001192092896D;
        }
    }

    @Override
    public void onEnable() {
        if (mc.player == null || mc.world == null) {
            setEnabled(false);
            return;
        }

        if (fake == null) {
            GameProfile profile = new GameProfile(UUID.randomUUID(), "fakeplayer");
            fake = new EntityOtherPlayerMP(mc.world, profile);
            fake.inventory.copyInventory(mc.player.inventory);
            fake.copyLocationAndAnglesFrom(mc.player);
            fake.setHealth(mc.player.getHealth());
            fake.onGround = mc.player.onGround;
            mc.world.addEntityToWorld(-999, fake);
        }
    }

    @Override
    public void onDisable() {
        if (mc.player == null || mc.world == null) {
            return;
        }

        if (fake != null) {
            mc.world.removeEntity(fake);
            recordedPositions.clear();
            fake = null;
        }
    }

    private static class Location {
        double posX, posY, posZ;
        float moveForward;
        float moveStrafe;
        float moveVertical;
        float yaw;
        float pitch;

        public Location(double posX, double posY, double posZ) {
            this.posX = posX;
            this.posY = posY;
            this.posZ = posZ;
        }


        public void setMoveForward(float moveForward)
        {
            this.moveForward = moveForward;
        }

        public void setMoveStrafe(float moveStrafe)
        {
            this.moveStrafe = moveStrafe;
        }

        public void setMoveVertical(float moveVertical)
        {
            this.moveVertical = moveVertical;
        }

        public void setPitch(float pitch)
        {
            this.pitch = pitch;
        }

        public void setYaw(float yaw)
        {
            this.yaw = yaw;
        }

    }

}
