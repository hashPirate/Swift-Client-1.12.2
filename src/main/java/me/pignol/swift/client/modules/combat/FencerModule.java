package me.pignol.swift.client.modules.combat;

import com.mojang.realmsclient.gui.ChatFormatting;
import me.pignol.swift.api.util.BlockUtil;
import me.pignol.swift.api.util.EntityUtil;
import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.text.ChatUtil;
import me.pignol.swift.api.value.Value;
import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.Item;
import net.minecraft.network.play.client.CPacketHeldItemChange;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FencerModule extends Module {

    private final Value<Integer> delay = new Value<>("Delay", 500, 0, 2000);

    private final StopWatch timer = new StopWatch();

    public FencerModule() {
        super("Fencer", Category.COMBAT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (timer.passed(delay.getValue())) {
            timer.reset();
            EntityPlayer target = EntityUtil.getClosestPlayer(6.0F);
            if (target != null && target.onGround) {
                EnumHand hand = mc.player.isHandActive() ? mc.player.getActiveHand() : null;
                int fenceSlot = ItemUtil.getSlotHotbar(Item.getItemFromBlock(Blocks.OAK_FENCE));
                int lastSlot = mc.player.inventory.currentItem;
                if (fenceSlot == -1) {
                    setEnabled(false);
                    ChatUtil.sendMessage(ChatFormatting.RED + "[Fencer] No fences.");
                    return;
                }

                BlockPos placePos = null;
                for (BlockPos pos : BlockUtil.getUnsafeBlocks(target.getPositionVector(), 0, false)) {
                    final IBlockState state = mc.world.getBlockState(pos);
                    if (state.getBlock().material.isReplaceable() && !target.getEntityBoundingBox().intersects(state.getBoundingBox(mc.world, pos).offset(pos))) {
                        placePos = pos;
                    }
                }

                if (placePos != null) {
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(fenceSlot));
                    BlockUtil.placeBlock(placePos, null);
                    mc.getConnection().sendPacket(new CPacketHeldItemChange(lastSlot));
                    if (hand != null) {
                        mc.player.setActiveHand(hand);
                    }
                }
            }
        }
    }

}
