package me.pignol.swift.client.modules.player;

import me.pignol.swift.client.event.events.UpdateEvent;
import me.pignol.swift.client.modules.Category;
import me.pignol.swift.client.modules.Module;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;

public class TunnelSpeed extends Module {

    public TunnelSpeed() {
        super("TunnelSpeed", Category.MOVEMENT);
    }

    @SubscribeEvent
    public void onUpdate(UpdateEvent event) {
        BlockPos pos = new BlockPos(mc.player.posX, mc.player.posY + 2.0, mc.player.posZ);
        BlockPos pos2 = new BlockPos(mc.player.posX, mc.player.posY - 1.0, mc.player.posZ);
        if (mc.world.getBlockState(pos).getBlock() != Blocks.AIR && mc.world.getBlockState(pos).getBlock() != Blocks.PORTAL && mc.world.getBlockState(pos).getBlock() != Blocks.END_PORTAL && mc.world.getBlockState(pos).getBlock() != Blocks.WATER && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_WATER && mc.world.getBlockState(pos).getBlock() != Blocks.LAVA && mc.world.getBlockState(pos).getBlock() != Blocks.FLOWING_LAVA && mc.world.getBlockState(pos2).getBlock() != Blocks.ICE && mc.world.getBlockState(pos2).getBlock() != Blocks.FROSTED_ICE && mc.world.getBlockState(pos2).getBlock() != Blocks.PACKED_ICE && !mc.player.isInWater()) {
            float yaw = (float)Math.toRadians(mc.player.rotationYaw);
            if (mc.gameSettings.keyBindForward.isKeyDown() && !mc.gameSettings.keyBindSneak.isKeyDown() && mc.player.onGround) {
                mc.player.motionX -= Math.sin(yaw) * 0.15;
                mc.player.motionZ += Math.cos(yaw) * 0.15;
            }
        }
    }

}
