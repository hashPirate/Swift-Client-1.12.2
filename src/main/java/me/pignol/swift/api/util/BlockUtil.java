package me.pignol.swift.api.util;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityEnderCrystal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.network.play.client.CPacketAnimation;
import net.minecraft.network.play.client.CPacketEntityAction;
import net.minecraft.network.play.client.CPacketPlayerTryUseItemOnBlock;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.*;
import net.minecraft.world.chunk.Chunk;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class BlockUtil {

    private final static Minecraft mc = Minecraft.getMinecraft();

    public static final Vec3d[] antiDropOffsetList = {
            new Vec3d(0, -2, 0),
    };

    public static final Vec3d[] platformOffsetList = {
            new Vec3d(0, -1, 0),
            new Vec3d(0, -1, -1),
            new Vec3d(0, -1, 1),
            new Vec3d(-1, -1, 0),
            new Vec3d(1, -1, 0)
    };

    public static final Vec3d[] legOffsetList = {
            new Vec3d(-1, 0, 0),
            new Vec3d(1, 0, 0),
            new Vec3d(0, 0, -1),
            new Vec3d(0, 0, 1)
    };

    public static final Vec3d[] offsetList = {
            new Vec3d(1, 1, 0),
            new Vec3d(-1, 1, 0),
            new Vec3d(0, 1, 1),
            new Vec3d(0, 1, -1),
            new Vec3d(0, 2, 0)
    };

    public static final Vec3d[] antiStepOffsetList = {
            new Vec3d(-1, 2, 0),
            new Vec3d(1, 2, 0),
            new Vec3d(0, 2, 1),
            new Vec3d(0, 2, -1),
    };

    public static final Vec3d[] antiScaffoldOffsetList = {
            new Vec3d(0, 3, 0)
    };

    public static final Vec3i[] OFFSETS_VECI = {
            new Vec3i(1, 0, 0),
            new Vec3i(-1, 0, 0),
            new Vec3i(0, 0, 1),
            new Vec3i(0, 0, -1),
            new Vec3i(0, -1, 0)
    };

    public static final Vec3i[] OFFSETS_VECIh = {
            new Vec3i(2, 0, 0),
            new Vec3i(-2, 0, 0),
            new Vec3i(0, 0, 2),
            new Vec3i(0, 0, -2),
    };

    public static final BlockPos[] OFFSETS = {
            new BlockPos(1, 0, 0),
            new BlockPos(-1, 0, 0),
            new BlockPos(0, 0, 1),
            new BlockPos(0, 0, -1),
            new BlockPos(0, -1, 0)
    };

    /**
     * mutables work on this too
     */
    public static boolean canPlaceCrystal(final BlockPos pos, boolean second, boolean raytrace) {
        final Chunk chunk = mc.world.getChunk(pos);
        final Block block = chunk.getBlockState(pos).getBlock();
        if (block != Blocks.BEDROCK && block != Blocks.OBSIDIAN) {
            return false;
        }

        final int posX = pos.getX();
        final int posY = pos.getY();
        final int posZ = pos.getZ();

        if (chunk.getBlockState(posX, posY + 1, posZ).getBlock() != Blocks.AIR || chunk.getBlockState(posX, posY + 2, posZ).getBlock() != Blocks.AIR) {
            return false;
        }

        if (raytrace && !RaytraceUtil.raytracePlaceCheck(mc.player, pos)) {
            return false;
        }

        for (Entity entity : mc.world.loadedEntityList) {
            if (entity != null && !(EntityUtil.isDead(entity) || entity instanceof EntityEnderCrystal)) {
                if (entity.getEntityBoundingBox().intersects(new AxisAlignedBB(posX, posY + 1, posZ, posX + 1, posY + (second ? 3 : 2), posZ + 1))) {
                    return false;
                }
            }
        }
        return true;
    }

    public static ObjectArrayList<BlockPos> getSphere(final float radius, final float yRadius) {
        final ObjectArrayList<BlockPos> sphere = new ObjectArrayList<>();
        final int posX = (int) mc.player.posX;
        final int posY = (int) mc.player.posY;
        final int posZ = (int) mc.player.posZ;
        for (int x = posX - (int) radius; x <= posX + radius; ++x) {
            for (int y = posY - (int) yRadius; y < posY + yRadius; ++y) {
                for (int z = posZ - (int) radius; z <= posZ + radius; ++z) {
                    if ((posX - x) * (posX - x) + (posZ - z) * (posZ - z) + (posY - y) * (posY - y) < radius * radius) {
                        sphere.add(new BlockPos(x, y, z));
                    }
                }
            }
        }
        return sphere;
    }

    public static boolean isReplaceable(BlockPos pos) {
        final IBlockState state = mc.world.getBlockState(pos);
        return state.getMaterial().isReplaceable();
    }

    public static int isPositionPlaceable(BlockPos pos) {
        final IBlockState state = mc.world.getBlockState(pos);
        if (!state.getMaterial().isReplaceable()) {
            return 0;
        }

        final List<Entity> loadedEntityList = mc.world.loadedEntityList;
        final AxisAlignedBB bb = new AxisAlignedBB(pos);
        for (int i = 0, loadedEntityListSize = loadedEntityList.size(); i < loadedEntityListSize; i++) {
            final Entity entity = loadedEntityList.get(i);
            if (entity != null && !EntityUtil.isDead(entity) && entity.getEntityBoundingBox().intersects(bb)) {
                if (entity instanceof EntityEnderCrystal) {
                    return 1;
                }

                if (entity instanceof EntityPlayer) {
                    return 4;
                }
            }
        }

        for (EnumFacing side : FacingUtil.VALUES) {
            final BlockPos neighbour = pos.offset(side);
            final IBlockState neighbourState = mc.world.getBlockState(neighbour);
            if (neighbourState.getBlock().canCollideCheck(neighbourState, false)) {
                if (!neighbourState.getMaterial().isReplaceable() && canBeClicked(neighbourState)) {
                    return 3;
                }
            }
        }

        return 2;
    }

    public static List<Vec3d> getBlockBlocks(Entity entity) {
        List<Vec3d> vec3ds = new ArrayList<>();
        AxisAlignedBB bb = entity.getEntityBoundingBox();
        double y = entity.posY;
        double minX = Math.floor(bb.minX);
        double minZ = Math.floor(bb.minZ);
        double maxX = Math.floor(bb.maxX);
        double maxZ = Math.floor(bb.maxZ);
        if (minX != maxX) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(maxX, y, minZ));
            if (minZ != maxZ) {
                vec3ds.add(new Vec3d(minX, y, maxZ));
                vec3ds.add(new Vec3d(maxX, y, maxZ));
                return vec3ds;
            }
        } else if (minZ != maxZ) {
            vec3ds.add(new Vec3d(minX, y, minZ));
            vec3ds.add(new Vec3d(minX, y, maxZ));
            return vec3ds;
        }
        vec3ds.add(entity.getPositionVector());
        return vec3ds;
    }

    public static List<Vec3d> targets(Vec3d vec3d, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean needsHelping) {
        List<Vec3d> placeTargets = new ArrayList<>();
        if (antiDrop) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, antiDropOffsetList));
        }

        if (platform) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, platformOffsetList));
        }

        if (legs) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, legOffsetList));
        }

        Collections.addAll(placeTargets, convertVec3ds(vec3d, offsetList));

        if (antiStep) {
            Collections.addAll(placeTargets, convertVec3ds(vec3d, antiStepOffsetList));
        } else if (needsHelping) {
            List<Vec3d> vec3ds = getUnsafeBlocksFromVec3d(vec3d, 2, false);
            vec3ds.sort(Comparator.comparing(pos -> mc.player.getDistanceSq(vec3d.add(pos.x, 0, 0).x, vec3d.add(0, pos.y, 0).y, vec3d.add(0, 0, pos.z).z)));
            for (Vec3d vector : vec3ds) {
                BlockPos position = new BlockPos(vec3d).add(vector.x, vector.y, vector.z);
                switch (BlockUtil.isPositionPlaceable(position)) {
                    case 0:
                        break;
                    case 1:
                    case 2:
                        continue;
                    case 3:
                        placeTargets.add(vec3d.add(vector));
                        break;
                }
                break;
            }
        }

        if (antiScaffold){
            Collections.addAll(placeTargets, BlockUtil.convertVec3ds(vec3d, antiScaffoldOffsetList));
        }

        placeTargets.removeIf(pos -> BlockUtil.isPositionPlaceable(new BlockPos(pos)) == 0);

        return placeTargets;
    }

    public static boolean isTrapped(EntityPlayer player, boolean antiScaffold, boolean antiStep, boolean legs, boolean platform, boolean antiDrop, boolean needsHelping) {
        return targets(player.getPositionVector(), antiScaffold, antiStep, legs, platform, antiDrop, needsHelping).size() == 0;
    }

    public static Vec3d[] convertVec3ds(Vec3d vec3d, Vec3d[] input) {
        Vec3d[] output = new Vec3d[input.length];
        for (int i = 0; i < input.length; i++) {
            output[i] = vec3d.add(input[i]);
        }
        return output;
    }

    public static boolean placeBlock(BlockPos pos, RotationUtil.RotationType rotation) {
        for (EnumFacing side : FacingUtil.VALUES) {
            final BlockPos neighbor = pos.offset(side);
            final IBlockState neighborState = mc.world.getBlockState(neighbor);
            if (neighborState.getBlock().canCollideCheck(neighborState, false)) {
                final boolean sneak = !mc.player.isSneaking() && neighborState.getBlock().onBlockActivated(mc.world, pos, mc.world.getBlockState(pos), mc.player, EnumHand.MAIN_HAND, side, 0.5f, 0.5f, 0.5f);
                if (sneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.START_SNEAKING));
                }
                if (rotation != null) {
                    final float[] angles = RotationUtil.getRotations(neighbor, side.getOpposite());
                    rotation.doRotation(angles);
                }

                mc.player.connection.sendPacket(new CPacketPlayerTryUseItemOnBlock(neighbor, side.getOpposite(), EnumHand.MAIN_HAND, 0.5f, 0.5f, 0.5f));
                mc.player.connection.sendPacket(new CPacketAnimation(EnumHand.MAIN_HAND));

                if (sneak) {
                    mc.player.connection.sendPacket(new CPacketEntityAction(mc.player, CPacketEntityAction.Action.STOP_SNEAKING));
                }
                return true;
            }
        }
        return false;
    }

    public static Vec3d[] getOffsets(int y, boolean floor) {
        Vec3d[] offsets = new Vec3d[floor ? 5 : 4];
        offsets[0] = new Vec3d(-1, y, 0);
        offsets[1] = new Vec3d(1, y, 0);
        offsets[2] = new Vec3d(0, y, -1);
        offsets[3] = new Vec3d(0, y, 1);
        if (floor) {
            offsets[4] = new Vec3d(0, y - 1, 0);
        }
        return offsets;
    }

    public static Vec3i[] getOffsetsI(int y, boolean floor) {
        Vec3i[] offsets = new Vec3i[floor ? 5 : 4];
        offsets[0] = new Vec3i(-1, y, 0);
        offsets[1] = new Vec3i(1, y, 0);
        offsets[2] = new Vec3i(0, y, -1);
        offsets[3] = new Vec3i(0, y, 1);
        if (floor) {
            offsets[4] = new Vec3i(0, y - 1, 0);
        }
        return offsets;
    }

    public static boolean isSafe(final EntityPlayer player, final int height, final boolean floor) {
        final BlockPos pos = new BlockPos(player.posX, player.posY, player.posZ);
        final Vec3d[] offsets = getOffsets(height, floor);
        for (final Vec3d vector : offsets) {
            final BlockPos targetPos = pos.add(vector.x, vector.y, vector.z);
            final IBlockState state = mc.world.getBlockState(targetPos);
            if (state.getMaterial().isReplaceable()) {
                return false;
            }
        }
        return true;
    }

    public static boolean isSafeFast(final EntityPlayer player) {
        final int originX = MathHelper.floor(player.posX);
        final int originY = MathHelper.floor(player.posY);
        final int originZ = MathHelper.floor(player.posZ);
        for (final Vec3i vector : OFFSETS_VECI) {
            final int posX = originX + vector.getX();
            final int posY = originY + vector.getY();
            final int posZ = originZ + vector.getZ();
            final Chunk chunk = mc.world.getChunk(posX >> 4, posZ >> 4);
            if (chunk.getBlockState(posX, posY, posZ).getMaterial().isReplaceable()) {
                return false;
            }
        }
        return true;
    }

    public static Vec3d[] getUnsafeBlocksArray(Vec3d pos, int height, boolean floor) {
        final List<Vec3d> unsafe = getUnsafeBlocksFromVec3d(pos, height, floor);
        final Vec3d[] vec3ds = new Vec3d[unsafe.size()];
        return unsafe.toArray(vec3ds);
    }

    public static List<Vec3d> getUnsafeBlocksFromVec3d(final Vec3d pos, final int height, final boolean floor) {
        final int originX = MathHelper.floor(pos.x);
        final int originY = MathHelper.floor(pos.y);
        final int originZ = MathHelper.floor(pos.z);
        final List<Vec3d> vec3ds = new ArrayList<>();
        final Vec3d[] offsets = getOffsets(height, floor);
        for (Vec3d vector : offsets) {
            final int x = originX + (int) vector.x;
            final int y = originY + (int) vector.y;
            final int z = originZ + (int) vector.z;
            final Chunk chunk = mc.world.getChunk(x >> 4, z >> 4);
            final IBlockState state = chunk.getBlockState(x, y, z);
            if (state.getMaterial().isReplaceable()) {
                vec3ds.add(vector);
            }
        }
        return vec3ds;
    }

    public static List<BlockPos> getUnsafeBlocks(final Vec3d vecPos, final int height, final boolean floor) {
        final int originX = MathHelper.floor(vecPos.x);
        final int originY = MathHelper.floor(vecPos.y);
        final int originZ = MathHelper.floor(vecPos.z);
        final List<BlockPos> blocks = new ArrayList<>();
        for (final Vec3i vector : getOffsetsI(height, floor)) {
            int x = originX + vector.getX();
            int y = originY + vector.getY();
            int z = originZ + vector.getZ();
            final Chunk chunk = mc.world.getChunk(x >> 4, z >> 4);
            final IBlockState state = chunk.getBlockState(x, y, z);
            if (state.getMaterial().isReplaceable()) {
                blocks.add(new BlockPos(x, y, z));
            }
        }
        return blocks;
    }

    public static boolean canBreak(BlockPos pos) {
        final IBlockState blockState = mc.world.getBlockState(pos);
        final Block block = blockState.getBlock();
        return block.getBlockHardness(blockState, Minecraft.getMinecraft().world, pos) != -1;
    }

    public static boolean canBeClicked(IBlockState state) {
        return state.getBlock().canCollideCheck(state, false);
    }

    public static Vec3d[] getHelpingBlocks(Vec3d vec3d) {
        return new Vec3d[] {
                new Vec3d(vec3d.x, vec3d.y - 1, vec3d.z),
                new Vec3d(vec3d.x != 0 ? vec3d.x * 2 : vec3d.x, vec3d.y, vec3d.x != 0 ? vec3d.z : vec3d.z * 2),
                new Vec3d(vec3d.x == 0 ? vec3d.x + 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z + 1),
                new Vec3d(vec3d.x == 0 ? vec3d.x - 1 : vec3d.x, vec3d.y, vec3d.x == 0 ? vec3d.z : vec3d.z - 1),
                new Vec3d(vec3d.x, vec3d.y + 1, vec3d.z)
        };
    }



}
