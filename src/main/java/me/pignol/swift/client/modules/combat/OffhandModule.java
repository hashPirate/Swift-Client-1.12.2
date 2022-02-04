package me.pignol.swift.client.modules.combat;

import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.managers.SafetyManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.init.Items;
import net.minecraft.inventory.ClickType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemSword;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent;

public class OffhandModule extends Module
{

    private final Value<Boolean> totem = new Value<>("Totem", true);
    private final Value<Boolean> swordGapple = new Value<>("SwordGapple", true);
    private final Value<Float> enemyRange = new Value<>("EnemyRange", 18.0F, 0.0F, 36.0F);
    private final Value<Float> crystalHealth = new Value<>("C-Health", 16.0F, 0.0F, 36.0F, v -> !totem.getValue());
    private final Value<Float> crystalHoleHealth = new Value<>("C-H-Health", 16.0F, 0.0F, 36.0F, v -> !totem.getValue());
    private final Value<Float> gappleHealth = new Value<>("G-Health", 16.0F, 0.0F, 36.0F, v -> swordGapple.getValue());
    private final Value<Float> gappleHoleHealth = new Value<>("G-H-Health", 16.0F, 0.0F, 36.0F, v -> swordGapple.getValue());
    private final Value<Integer> delay = new Value<>("Delay", 250, 0, 2000);

    private final StopWatch timer = new StopWatch();


    private boolean gappling;

    public OffhandModule()
    {
        super("Offhand", Category.COMBAT);
    }

    @SubscribeEvent
    public void onTick(TickEvent.ClientTickEvent event)
    {
        if (isNull() || mc.currentScreen instanceof GuiContainer && !(mc.currentScreen instanceof GuiInventory))
        {
            return;
        }

        boolean safe = SafetyManager.getInstance().isSafe();
        gappling = mc.gameSettings.keyBindUseItem.isKeyDown() && mc.player.getHeldItemMainhand().getItem() instanceof ItemSword;
        Item item = getItem(safe, gappling);
        if (mc.player.getHeldItemOffhand().getItem() != item)
        {
            int slot = getItemSlot(item);
            if (slot != -1)
            {
                slot = slot < 9 ? slot + 36 : slot;
                if (item != Items.TOTEM_OF_UNDYING && !timer.passed(delay.getValue()))
                {
                    return;
                }
                windowClick(slot);
                windowClick(45);
                windowClick(slot);
                mc.playerController.updateController();
                timer.reset();
            }
        }
    }

    private int getItemSlot(Item itemIn)
    {
        for (int i = 45; i > 0; i--)
        {
            final Item item = mc.player.inventory.getStackInSlot(i).getItem();
            if (item == itemIn)
            {
                if (i < 9)
                {
                    i += 36;
                }
                return i;
            }
        }

        return -1;
    }

    public void windowClick(int slotId)
    {
        mc.playerController.windowClick(0, slotId, 0, ClickType.PICKUP, mc.player);
    }

    @Override
    public String getDisplayName()
    {
        if (totem.getValue())
        {
            return "AutoTotem";
        }
        return gappling ? "OffhandGapple" : "OffhandCrystal";
    }

    private Item getItem(boolean safe, boolean gapple)
    {
        Item item = Items.TOTEM_OF_UNDYING;

        if (!safe)
        {
            return item;
        }

        if (enemyRange.getValue() > 0.0F && !gapple)
        {
            if (EntityUtil.getClosestPlayer(enemyRange.getValue()) == null)
            {
                return item;
            }
        }

        boolean inHole = BlockUtil.isSafeFast(mc.player);
        if (EntityUtil.getHealth(mc.player) >= getHealth(inHole, gapple))
        {
            item = gapple ? Items.GOLDEN_APPLE : totem.getValue() ? item : Items.END_CRYSTAL;
        }

        return item;
    }

    private float getHealth(boolean safe, boolean gapple)
    {
        return gapple ? (safe ? gappleHoleHealth.getValue() : gappleHealth.getValue()) : (safe ? crystalHoleHealth.getValue() : crystalHealth.getValue());
    }

}
