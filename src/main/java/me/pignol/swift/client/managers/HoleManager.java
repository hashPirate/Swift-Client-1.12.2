package me.pignol.swift.client.managers;

import me.pignol.swift.api.interfaces.Globals;
import me.pignol.swift.api.util.FacingUtil;
import me.pignol.swift.api.util.objects.StopWatch;
import me.pignol.swift.api.util.objects.Hole;
import me.pignol.swift.client.modules.other.ManageModule;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.init.Blocks;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.chunk.Chunk;
import net.minecraftforge.common.MinecraftForge;

import java.util.ArrayList;
import java.util.List;

public class HoleManager implements Globals {

    private static final HoleManager INSTANCE = new HoleManager();

    public static HoleManager getInstance() {
        return INSTANCE;
    }

    public void load() {
        MinecraftForge.EVENT_BUS.register(this);
    }

    private final StopWatch timer = new StopWatch();

    private List<Hole> holes = new ArrayList<>();

    public void onThread() {
        if (mc.player == null || mc.world == null)
            return;
        if (timer.passed(ManageModule.INSTANCE.holeSearchDelay.getValue())) {
            holes = calculateHoles();
            timer.reset();
        }
    }

    public List<Hole> calculateHoles() {
        final List<Hole> holes = new ArrayList<>();
        final float radius = ManageModule.INSTANCE.holeRange.getValue();
        final float yRadius = ManageModule.INSTANCE.holeRangeY.getValue();
        final int posX = MathHelper.floor(mc.player.posX);
        final int posY = MathHelper.floor(mc.player.posY);
        final int posZ = MathHelper.floor(mc.player.posZ);
        final BlockPos.PooledMutableBlockPos pos = BlockPos.PooledMutableBlockPos.retain();
        for (int x = posX - (int) radius; x <= posX + radius; ++x) {
            for (int y = posY - (int) yRadius; y < posY + yRadius; ++y) {
                for (int z = posZ - (int) radius; z <= posZ + radius; ++z) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        pos.setPos(x, y, z);

                        final int originX = pos.getX();
                        final int originY = pos.getY();
                        final int originZ = pos.getZ();
                        final int value = getHoleSafety(pos);
                        switch (value) {
                            case 2:
                                holes.add(new Hole(new BlockPos(originX, originY, originZ), true));
                                break;
                            case 1:
                                holes.add(new Hole(new BlockPos(originX, originY, originZ), false));
                                break;
                        }
                    }
                }
            }
        }
        pos.release();
        return holes;
    }

    public int getHoleSafety(final BlockPos.MutableBlockPos offset) {
        final int originX = offset.getX();
        final int originY = offset.getY();
        final int originZ = offset.getZ();
        final Chunk chunk = mc.world.getChunk(originX >> 4, originZ >> 4);
        for (int i = 0; i < 3; i++) {
            if (chunk.getBlockState(originX, originY + i, originZ).getMaterial() != Material.AIR) {
                return 0;
            }
        }

        boolean bedrock = true;
        for (EnumFacing f : FacingUtil.VALUESNOUP) {
            offset.setPos(originX + f.getXOffset(), originY + f.getYOffset(), originZ + f.getZOffset());
            final Block block = mc.world.getBlockState(offset).getBlock();
            if (block != Blocks.BEDROCK) {
                if (block != Blocks.OBSIDIAN && block != Blocks.ENDER_CHEST) {
                    return 0;
                }
                bedrock = false;
            }
        }

        offset.setPos(originX, originY, originZ);
        return bedrock ? 2 : 1;
    }



    public List<Hole> getHoles() {
        return holes;
    }

}
