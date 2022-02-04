package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.InteractEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.Block;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemFood;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class NoInteract extends Module {

    private final Value<Boolean> onlyOnFood = new Value<>("OnlyOnFood", true);
    private final Value<Boolean> anvils = new Value<>("Anvils", true);
    private final Value<Boolean> eChests = new Value<>("E-Chests", true);

    public NoInteract() {
        super("NoInteract", Category.PLAYER);
    }

    @SubscribeEvent
    public void onInteract(InteractEvent event) {
        if (isNull())
            return;

        if (onlyOnFood.getValue() && !(mc.player.getHeldItemMainhand().getItem() instanceof ItemFood)) return;

        Block block = mc.world.getBlockState(event.getPos()).getBlock();
        if (block == Blocks.ANVIL && anvils.getValue() || block == Blocks.ENDER_CHEST && eChests.getValue()) {
            event.setCanceled(true);
        }
    }

}
