package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.util.MathUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.FriendManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import me.pignol.swift.client.modules.player.FastPlaceModule;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.*;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class AutoArmor extends Module {

    public Value<Integer> delay = new Value<>("Delay", 100, 1, 1000, 25);
    public Value<Boolean> pantsBlastPrio = new Value<>("BlastPants", true);
    public Value<Boolean> lessPackets = new Value<>("ShiftClick", false);
    public Value<Boolean> pauseInInv = new Value<>("PauseInInv", false);
    public Value<Boolean> mendWhileXP = new Value<>("AutoMend", false);
    public Value<Integer> mendPercent = new Value<>("%", 80, 5, 100, v -> mendWhileXP.getValue());
    public Value<Boolean> deathCheck = new Value<>("DangerCheck", true, v -> mendWhileXP.getValue());
    public Value<Float> enemyDistance = new Value<>("EnemyRange", 5.0F, 1.0F, 13.5F, vis -> deathCheck.getValue() && mendWhileXP.getValue());
    public Value<Float> crystalDistance = new Value<>("CrystalRange", 3.0F, 1.0F, 6.5F, vis -> deathCheck.getValue() && mendWhileXP.getValue());

    public AutoArmor() {
        super("AutoArmor", Category.COMBAT);
    }

    private final StopWatch timer = new StopWatch();

    @SubscribeEvent
    public void onClientUpdate(UpdateEvent event) {
        if (mc.player == null || mc.world == null || isScreenBad()) {
            return;
        }
        boolean doMend = (mendWhileXP.getValue() && Mouse.isButtonDown(1) && (FastPlaceModule.INSTANCE.shouldMend() || mc.player.getHeldItemMainhand().getItem() instanceof ItemExpBottle));
        if (deathCheck.getValue()) {
            // player check
            for (EntityPlayer target : mc.world.playerEntities) {
                if ((target == mc.player || FriendManager.getInstance().isFriend(target.getName()))) {
                    continue;
                }
                if (((target).getHealth() <= 0) || target.isDead) {
                    continue;
                }
                if ((mc.player.getDistance(target) > 13.5)) {
                    continue;
                }
                if (mc.player.getDistance(target) <= enemyDistance.getValue()) {
                    doMend = false;
                }
            }
            //crystal check
            if (crystalsNearby(MathUtil.square(crystalDistance.getValue()))) {
                doMend = false;
            }
        }
        if (doMend) {
            for (ItemStack is : mc.player.inventory.armorInventory) {
                if (is.isEmpty()) {
                    continue;
                }
                int dmg = 0;
                final int itemDurability = is.getMaxDamage() - is.getItemDamage();
                final float green = (is.getMaxDamage() - (float) is.getItemDamage()) / is.getMaxDamage();
                final float red = 1.0f - green;
                dmg = 100 - (int) (red * 100.0f);
                if (dmg >= mendPercent.getValue()) {
                    unequipArmor(getTypeFromItem(is.getItem()));
                }
            }
        } else {
            for (int i = 3; i >= 0; i--) {
                if (mc.player.inventory.armorInventory.get(i).isEmpty()) {
                    equipArmor(i);
                    break;
                }
            }
        }
    }

    private boolean isScreenBad() {
        if (mc.currentScreen instanceof GuiContainer) {
            if (!(mc.currentScreen instanceof GuiInventory)) {
                return true;
            } else {
                return pauseInInv.getValue();
            }
        }
        return false;
    }

    private boolean crystalsNearby(float distanceSq) {
        for (Entity entity : mc.world.loadedEntityList) {
            if (entity instanceof EntityEnderCrystal) {
                if (mc.player.getDistanceSq(entity) > distanceSq) {
                    return true;
                }
            }
        }
        return false;
    }

    private void unequipArmor(ArmorType armorType) {
        int bestSlot = -1;
        for (int i = 9; i <= 44; i++) {
            Item item = mc.player.inventoryContainer.getSlot(i).getStack().getItem();
            if (item != null && item instanceof ItemAir) {
                bestSlot = i;
            }
        }

        if (bestSlot != -1) {
            if (armorType != null) {
                if (timer.passed(delay.getValue())) {
                    if (lessPackets.getValue()) {
                        mc.playerController.windowClick(0, getSlotByType(armorType), 0, ClickType.QUICK_MOVE, mc.player);
                    } else {
                        if (getSlotByType(armorType) != -1) {
                            mc.playerController.windowClick(0, getSlotByType(armorType), 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(0, bestSlot, 0, ClickType.PICKUP, mc.player);
                            mc.playerController.windowClick(0, getSlotByType(armorType), 0, ClickType.PICKUP, mc.player);
                            mc.playerController.updateController();
                        }
                    }
                    timer.reset();
                }
            }
        }
    }

    private void equipArmor(int slot) {
        ArmorType armorType = getArmorTypeFromSlot(slot);
        int bestSlot = -1;
        int bestRating = -1;

        for (int i = 9; i <= 44; i++) {
            Item item = mc.player.inventoryContainer.getSlot(i).getStack().getItem();
            if (item instanceof ItemArmor && getTypeFromItem(item) == armorType) {
                int damageReduction = ((ItemArmor) item).damageReduceAmount;
                if (getTypeFromItem(item) == ArmorType.PANTS && pantsBlastPrio.getValue()) {
                    if (EnchantmentHelper.getEnchantmentLevel(Enchantments.BLAST_PROTECTION, mc.player.inventoryContainer.getSlot(i).getStack()) > 0) {
                        bestSlot = i;
                        bestRating = damageReduction;
                    }
                } else {
                    if (damageReduction >= bestRating) {
                        bestSlot = i;
                        bestRating = damageReduction;
                    }
                }
            }
        }

        if (bestSlot != -1 && bestRating != -1) {
            if (timer.passed(delay.getValue())) {
                if (lessPackets.getValue()) {
                    mc.playerController.windowClick(0, bestSlot, 0, ClickType.QUICK_MOVE, mc.player);
                } else {
                    if (getSlotByType(armorType) != -1) {
                        mc.playerController.windowClick(0, bestSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(0, getSlotByType(armorType), 0, ClickType.PICKUP, mc.player);
                        mc.playerController.windowClick(0, bestSlot, 0, ClickType.PICKUP, mc.player);
                        mc.playerController.updateController();
                    }
                }
                timer.reset();
            }
        }
    }

    @Override
    public void onEnable() {
        timer.reset();
    }

    private ArmorType getTypeFromItem(Item item) {
        if (Items.DIAMOND_HELMET.equals(item) || Items.GOLDEN_HELMET.equals(item) || Items.IRON_HELMET.equals(item) || Items.CHAINMAIL_HELMET.equals(item) || Items.LEATHER_HELMET.equals(item)) {
            return ArmorType.HELMET;
        } else if (Items.DIAMOND_CHESTPLATE.equals(item) || Items.GOLDEN_CHESTPLATE.equals(item) || Items.IRON_CHESTPLATE.equals(item) || Items.CHAINMAIL_CHESTPLATE.equals(item) || Items.LEATHER_CHESTPLATE.equals(item)) {
            return ArmorType.CHESTPLATE;
        } else if (Items.DIAMOND_LEGGINGS.equals(item) || Items.GOLDEN_LEGGINGS.equals(item) || Items.IRON_LEGGINGS.equals(item) || Items.CHAINMAIL_LEGGINGS.equals(item) || Items.LEATHER_LEGGINGS.equals(item)) {
            return ArmorType.PANTS;
        } else if (Items.DIAMOND_BOOTS.equals(item) || Items.GOLDEN_BOOTS.equals(item) || Items.IRON_BOOTS.equals(item) || Items.CHAINMAIL_BOOTS.equals(item) || Items.LEATHER_BOOTS.equals(item)) {
            return ArmorType.BOOTS;
        }
        return null;
    }

    private ArmorType getArmorTypeFromSlot(int slot) {
        switch (slot) {
            case 3:
                return ArmorType.HELMET;
            case 2:
                return ArmorType.CHESTPLATE;
            case 1:
                return ArmorType.PANTS;
            case 0:
                return ArmorType.BOOTS;
            default:
                return null;
        }
    }

    private int getSlotByType(ArmorType type) {
        switch (type) {
            case HELMET:
                return 5;
            case CHESTPLATE:
                return 6;
            case PANTS:
                return 7;
            case BOOTS:
                return 8;
            default:
                return -1;
        }
    }

    private enum ArmorType {
        HELMET,
        CHESTPLATE,
        PANTS,
        BOOTS;
    }

}
