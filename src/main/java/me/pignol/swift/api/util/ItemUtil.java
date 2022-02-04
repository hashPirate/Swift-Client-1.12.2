package me.pignol.swift.api.util;

import me.pignol.swift.api.interfaces.Globals;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;

public class ItemUtil implements Globals {

    public static float getDamageInPercent(ItemStack stack) {
        float green = ((float) stack.getMaxDamage() - (float) stack.getItemDamage()) / (float) stack.getMaxDamage();
        float red = 1.0f - green;
        return 100 - (int) (red * 100.0f);
    }

    public static boolean hasDurability(Item item) {
        return item instanceof ItemArmor || item instanceof ItemSword || item instanceof ItemTool || item instanceof ItemShield;
    }

    public static int getBestToolSlot(Block block) {
        float maxSpeed = -1.0F;
        int slot = -1;
        for (int i = 0; i < 9; i++) {
            ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (!itemStack.isEmpty()) {
                float speed = itemStack.getItem().getDestroySpeed(itemStack, block.getDefaultState());
                if (speed > maxSpeed) {
                    maxSpeed = speed;
                    slot = i;
                }
            }
        }
        return slot;
    }

    public static void switchToSlot(int slot, boolean silent) {
        if (slot >= 0 && slot <= 8) { //only valid slots
            if (!silent) {
                mc.player.inventory.currentItem = slot;
                syncHeldItem();
            } else {
                mc.getConnection().sendPacket(new CPacketHeldItemChange(slot));
            }
        }
    }

    public static int getSlotHotbar(Class<?> clazz) {
        int slot = -1;
        if (mc.player == null) {
            return slot;
        }

        for (int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem().getClass() == clazz) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getSlotHotbar(Item item) {
        int slot = -1;
        if (mc.player == null) {
            return slot;
        }

        for (int i = 8; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getSlot(final Item item) {
        int slot = -1;
        if (mc.player == null) {
            return slot;
        }

        for (int i = 44; i >= 0; --i) {
            if (mc.player.inventory.getStackInSlot(i).getItem() == item) {
                if (i < 9) {
                    i += 36;
                }
                slot = i;
                break;
            }
        }

        return slot;
    }

    public static int getItemCount(final Item item) {
        if (mc.player == null) {
            return 0;
        }

        int count = 0;

        for (int i = 0; i < 45; ++i) {
            final ItemStack stack = mc.player.inventory.getStackInSlot(i);
            if (stack.getItem() == item) {
                count += stack.getCount();
            }
        }

        return count;
    }

    public static boolean isArmorUnderPercent(EntityPlayer player, float percent) {
        for (ItemStack stack : player.inventory.armorInventory) {
            if (stack == null || stack.isEmpty || getDamageInPercent(stack) <= percent) {
                return true;
            }
        }
        return false;
    }

    public static boolean isHolding(Item item) {
        return isHolding(mc.player, item);
    }

    public static boolean isHolding(Block block) {
        return isHolding(mc.player, block);
    }

    public static boolean isHolding(EntityLivingBase entity, Item item) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();

        return ItemUtil.areSame(mainHand, item) || ItemUtil.areSame(offHand, item);
    }

    public static boolean isHolding(EntityLivingBase entity, Block block) {
        ItemStack mainHand = entity.getHeldItemMainhand();
        ItemStack offHand = entity.getHeldItemOffhand();

        return ItemUtil.areSame(mainHand, block) || ItemUtil.areSame(offHand, block);
    }

    public static boolean areSame(Block block1, Block block2) {
        return Block.getIdFromBlock(block1) == Block.getIdFromBlock(block2);
    }

    public static boolean areSame(Item item1, Item item2) {
        return Item.getIdFromItem(item1) == Item.getIdFromItem(item2);
    }

    public static boolean areSame(Block block, Item item) {
        return item instanceof ItemBlock && areSame(block, ((ItemBlock) item).getBlock());
    }

    public static boolean areSame(ItemStack stack, Block block) {
        return stack != null && areSame(block, stack.getItem());
    }

    public static boolean areSame(ItemStack stack, Item item) {
        return stack != null && areSame(stack.getItem(), item);
    }

    public static void syncHeldItem() {
        mc.playerController.syncCurrentPlayItem();
    }

}
