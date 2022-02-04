package me.pignol.swift.client.modules.movement;

import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.block.BlockStairs;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class FastStairs extends Module {
    
    public FastStairs() {
        super("FastStairs", Category.MOVEMENT);
    }
    
    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        if (mc.player.moveForward > 0.01
                && mc.player.onGround
                && mc.world.getBlockState(new BlockPos(mc.player.posX, mc.player.posY - 1, mc.player.posZ)).getBlock() instanceof BlockStairs) {

            mc.player.jump();
        }
    }
    
}
