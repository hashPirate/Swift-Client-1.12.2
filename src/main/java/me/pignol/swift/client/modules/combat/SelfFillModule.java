package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.network.play.client.CPacketPlayer;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class SelfFillModule extends Module {

    private final Value<Float> height = new Value<>("Height", 5F, -5F, 5F);
    private final Value<Float> spaceCheck = new Value<>("SelfFill", 7F, 0F, 15F);
    private final Value<Boolean> preferEChests = new Value<>("EChests", true);

    public SelfFillModule() {
        super("SelfFill", Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (isNull()) {
            setEnabled(false);
            return;
        }

        int obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        int eChestSlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.ENDER_CHEST));
        if ((preferEChests.getValue() || obbySlot == -1) && eChestSlot != -1) {
            obbySlot = eChestSlot;
        } else {
            obbySlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OBSIDIAN));
        }
        if (obbySlot == -1) {
            ChatUtil.sendMessage(ChatFormatting.RED + "[SelfFill] No obsidian.", -554);
            setEnabled(false);
            return;
        }

        BlockPos startPos = new BlockPos(mc.player.getPositionVector());
        if (spaceCheck.getValue() > 0) {
            for (int i = 0; i < spaceCheck.getValue(); ++i) {
                if (mc.world.getBlockState(startPos.up(i)).getBlock() != Blocks.AIR) {
                    setEnabled(false);
                    return;
                }
            }
        }

        int startSlot = mc.player.inventory.currentItem;

        EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;
        mc.getConnection().sendPacket(new CPacketHeldItemChange(obbySlot));

        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.41, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 0.75, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.00, mc.player.posZ, true));
        mc.player.connection.sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + 1.16, mc.player.posZ, true));

        final boolean onEChest = mc.world.getBlockState(new BlockPos(mc.player.getPositionVector())).getBlock() == Blocks.ENDER_CHEST;
        BlockUtil.placeBlock(onEChest ? startPos.up() : startPos, null);
        mc.getConnection().sendPacket(new CPacketPlayer.Position(mc.player.posX, mc.player.posY + height.getValue(), mc.player.posZ, false));

        if (startSlot != -1)
            mc.getConnection().sendPacket(new CPacketHeldItemChange(startSlot));

        if (hand != null) {
            mc.player.setActiveHand(hand);
        }

        setEnabled(false);
    }


}
