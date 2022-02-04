package me.pignol.swift.client.modules.misc;

import me.pignol.swift.api.util.ItemUtil;
import me.pignol.swift.client.event.events.DamageBlockEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class AutoTool extends Module {

    public AutoTool() {
        super("AutoTool", Category.MISC);
    }

    @SubscribeEvent
    public void onClickBlock(DamageBlockEvent event) {
        BlockPos pos = event.getPos();
        if (pos != null) {
            IBlockState state = mc.world.getBlockState(event.getPos());
            if (state.getBlock().material != Material.AIR) {
                int slot = ItemUtil.getBestToolSlot(state.getBlock());
                if (slot != -1 && mc.player.inventory.currentItem != slot) {
                    mc.player.inventory.currentItem = ItemUtil.getBestToolSlot(state.getBlock());
                    mc.playerController.updateController();
                }
            }
        }
    }

}
