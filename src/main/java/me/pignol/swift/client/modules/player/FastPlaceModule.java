package me.pignol.swift.client.modules.player;

import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.PacketEvent;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.managers.SwitchManager;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Items;
import net.minecraft.item.*;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayerTryUseItem;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import org.lwjgl.input.Mouse;

public class FastPlaceModule extends Module {

    public static FastPlaceModule INSTANCE;

    private final Value<Boolean> blocks = new Value<>("Blocks", true);
    private final Value<Boolean> exp = new Value<>("Bottles", true);
    private final Value<Boolean> boatFix = new Value<>("BoatFix", true);
    private final Value<Boolean> middleclick = new Value<>("MiddleClickEXP", true);
    private final Value<Integer> stopMend = new Value<>("StopPercentageEXP",80,0,100);
    private final Value<Integer> expDelay = new Value<>("ExpDelay", 50, 0, 300);
	
    private final Value<Boolean> eatingFix = new Value<>("EatingFix", true);
    private final Value<Boolean> doubleEat = new Value<>("DoubleEat", true);
    private final Value<Integer> doubleEatDelay = new Value<>("DoubleEatDelay", 50, 0, 500);
	
    private final StopWatch eatTimer = new StopWatch();
    private final StopWatch expTimer = new StopWatch();
	
    private int lastSentSlot;
    private boolean active;
	
    public FastPlaceModule() {
        super("FastPlace", Category.PLAYER);
        INSTANCE = this;
	}
	
    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            return;
		}
		
        if (!mc.player.isHandActive()) {
            active = false;
		}
		
        Item heldItem = mc.player.getHeldItemMainhand().getItem();
        if (heldItem instanceof ItemBlock && blocks.getValue() || heldItem instanceof ItemExpBottle && exp.getValue()) {
            mc.rightClickDelayTimer = 0;
		}
		
        if (eatingFix.getValue()) {
            int currentItem = mc.player.inventory.currentItem;
            if (lastSentSlot != currentItem && mc.player.inventory.getStackInSlot(currentItem).getItem() instanceof ItemFood) {
                ItemUtil.switchToSlot(currentItem, false);
			}
		}
		
        if (shouldMend()) {
            onClick();
		}
	}
	
    @SubscribeEvent
    public void onPacketSend(PacketEvent.Send event) {
        if (event.getPacket() instanceof CPacketHeldItemChange) {
            lastSentSlot = ((CPacketHeldItemChange) event.getPacket()).getSlotId();
		}

        if (event.getPacket() instanceof CPacketPlayerTryUseItemOnBlock && boatFix.getValue()) {
            CPacketPlayerTryUseItemOnBlock packet = (CPacketPlayerTryUseItemOnBlock) event.getPacket();
            if (mc.player.getHeldItem(packet.getHand()).getItem() instanceof ItemBoat) {
                event.setCanceled(true);
            }
        }
	}
	
    @SubscribeEvent
    public void onRightClickItem(PlayerInteractEvent.RightClickItem event) {
        if (doubleEat.getValue() && event.getItemStack().getItem() instanceof ItemFood && !active && eatTimer.passed(doubleEatDelay.getValue())) {
            active = true;
            mc.getConnection().sendPacket(new CPacketPlayerTryUseItem(event.getHand()));
            eatTimer.reset();
		}
	}
	
    public boolean shouldMend() {
		if (isEnabled() && middleclick.getValue() && Mouse.isButtonDown(2)) {
		    //check if any piece of ours is low
			boolean wasAnyLower = false;
			for (int i = 0; i < 4; ++i) {
				int dura = (int) ItemUtil.getDamageInPercent(mc.player.inventory.armorInventory.get(i));
				if (dura < stopMend.getValue()) {
					wasAnyLower = true;
					break;
				}
			}
			
			return wasAnyLower;
		}
		return false;
	}
	
    private void onClick() {
        int expSlot = ItemUtil.getSlotHotbar(Items.EXPERIENCE_BOTTLE);
        if (expSlot != -1 && expTimer.passed(expDelay.getValue())) {
            int oldslot = mc.player.inventory.currentItem;

            SwitchManager.getInstance().setDontReset(true);
            mc.getConnection().sendPacket(new CPacketHeldItemChange(expSlot));
            mc.playerController.processRightClick(mc.player, mc.world, EnumHand.MAIN_HAND);
            mc.getConnection().sendPacket(new CPacketHeldItemChange(oldslot));
            SwitchManager.getInstance().setDontReset(false);

            expTimer.reset();
		}
	}
	
}
