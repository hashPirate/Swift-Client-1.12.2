package me.pignol.swift.client.modules.misc;

import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityMinecartContainer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityLockableLoot;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

public class EgapFinder extends Module {

    public EgapFinder() { super("EgapFinder", Category.MISC); }

    @SubscribeEvent
    public void onWorldTick(TickEvent.WorldTickEvent event) {
        if (event.world.isRemote)
            return;
        World world = event.world;
        EntityPlayer player = null;
        if (!world.playerEntities.isEmpty()) {
            player = world.playerEntities.get(0);
            for (TileEntity tile : world.loadedTileEntityList) {
                if (tile instanceof TileEntityLockableLoot) {
                    TileEntityLockableLoot lockable = (TileEntityLockableLoot) tile;
                    if (lockable.getLootTable() != null) {
                        lockable.fillWithLoot(player);
                        for (int i = 0; i < lockable.getSizeInventory(); i++) {
                            ItemStack stack = lockable.getStackInSlot(i);
                            if (stack.getItem() == Items.GOLDEN_APPLE && stack.getItemDamage() == 1) {
                                writeToFile("Dungeon Chest with ench gapple at: " + lockable.getPos().getX() + " " + lockable.getPos().getY() + " " + lockable.getPos().getZ());
                                if (stack.getItem() == Items.ENCHANTED_BOOK &&
                                        EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0) {
                                    writeToFile("Dungeon Chest with Mending Book: " + lockable.getPos().getX() + " " + lockable.getPos().getY() + " " + lockable.getPos().getZ());
                                }
                            }
                        }
                    }
                }
                for (Entity entity : world.loadedEntityList) {
                    if (entity instanceof EntityMinecartContainer) {
                        EntityMinecartContainer cart = (EntityMinecartContainer) entity;
                        if (cart.getLootTable() != null) {
                            cart.addLoot(player);
                            for (int i = 0; i < cart.itemHandler.getSlots(); i++) {
                                ItemStack stack = cart.itemHandler.getStackInSlot(i);
                                if (stack.getItem() == Items.GOLDEN_APPLE && stack.getItemDamage() == 1) {
                                    writeToFile("Minecart with ench gapple at: " + cart.posX + " " + cart.posY + " " + cart.posZ);
                                }
                                if (stack.getItem() == Items.ENCHANTED_BOOK &&
                                        EnchantmentHelper.getEnchantmentLevel(Enchantments.MENDING, stack) > 0) {
                                    writeToFile("Minecart with Mending at: " + cart.posX + " " + cart.posY + " " + cart.posZ);

                                }

                            }

                        }
                    }
                }
            }
        }
    }

    protected static void writeToFile(String coords) {
        try(FileWriter fw = new FileWriter("EgapFinder.txt", true);
            BufferedWriter bw = new BufferedWriter(fw);
            PrintWriter out = new PrintWriter(bw)) {
            out.println(coords);
        } catch (IOException iOException) {}
    }
}
