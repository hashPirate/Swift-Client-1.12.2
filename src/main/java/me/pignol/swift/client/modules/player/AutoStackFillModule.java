package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoStackFillModule extends Module {

    private final Value<Integer> threshold = new Value<>("Threshold", 32, 1, 64);
    private final Value<Integer> delay = new Value<>("Delay", 50, 0, 1000);

    private final StopWatch timer = new StopWatch();

    public AutoStackFillModule() {
        super("AutoStackFill", Category.PLAYER);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (timer.passed(delay.getValue())) {
            for (int i = 0; i < 9; ++i) {
                if (refillSlot(i)) {
                    timer.reset();
                    return;
                }
            }
        }
    }

    private boolean refillSlot(int slot) {
        ItemStack stack = mc.player.inventory.getStackInSlot(slot);

        if (stack.isEmpty() || stack.getCount() > threshold.getValue() || stack.getItem() == Items.AIR || !stack.isStackable() || stack.getCount() >= stack.getMaxStackSize()) {
            return false;
        }

        for (int i = 9; i < 36; ++i) {
            final ItemStack itemStack = mc.player.inventory.getStackInSlot(i);
            if (!itemStack.isEmpty() && canMergeWith(stack, itemStack)) {
                mc.playerController.windowClick(mc.player.inventoryContainer.windowId, i, 0, ClickType.QUICK_MOVE, mc.player);
                mc.playerController.updateController();
                return true;
            }
        }
        return false;
    }

    private boolean canMergeWith(ItemStack first, ItemStack second) {
        return first.getItem() == second.getItem() && first.getDisplayName().equals(second.getDisplayName());
    }

}
