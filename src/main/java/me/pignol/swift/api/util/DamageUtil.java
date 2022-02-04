package me.pignol.swift.api.util;

import me.pignol.swift.api.interfaces.Globals;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.entity.Entity;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.CombatRules;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.NonNullList;
import net.minecraft.util.math.*;
import net.minecraft.world.Explosion;
import net.minecraft.world.World;

import javax.annotation.Nullable;
import java.util.List;

public class DamageUtil implements Globals {

    private final static DamageSource DAMAGE_SOURCE        = DamageSource.causeExplosionDamage(new Explosion(mc.world, mc.player, 0, 0, 0, 6.0F, false, true));
    public final static NBTTagCompound EMPTY_TAG_COMPOUND =  new NBTTagCompound();

    public static float calculate(double x, double y, double z, EntityPlayer base) {
        return calculate(x, y, z, base, base.getEntityBoundingBox());
    }

    public static float calculate(double x, double y, double z, EntityPlayer base, AxisAlignedBB boundingBox) {
        double distance = base.getDistance(x, y, z) / 12.0D;
        if (distance > 1.0D) {
            return 0.0F;
        } else {
            final float density = getBlockDensity(new Vec3d(x, y, z), boundingBox);
            final double densityDistance = distance = (1.0D - distance) * density;
            float damage = CombatRules.getDamageAfterAbsorb(getDifficultyMultiplier((float) ((densityDistance * densityDistance + distance) / 2.0D * 7.0D * 12.0D + 1.0D)), base.getTotalArmorValue(), (float) base.getEntityAttribute(SharedMonsterAttributes.ARMOR_TOUGHNESS).getAttributeValue());

            final int modifierDamage = getEnchantmentModifierDamage(base.inventory.armorInventory, DAMAGE_SOURCE);
            if (modifierDamage > 0) {
                damage = CombatRules.getDamageAfterMagicAbsorb(damage, modifierDamage);
            }

            final PotionEffect resistance = base.getActivePotionEffect(MobEffects.RESISTANCE);
            if (resistance != null) {
                damage = damage * (25 - (resistance.getAmplifier() + 1) * 5) / 25.0F;
            }

            return Math.max(damage, 0.0F);
        }
    }

    public static float calculate(BlockPos pos) {
        return calculate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, mc.player);
    }

    public static float calculate(Entity crystal, EntityPlayer base) {
        return calculate(crystal.posX, crystal.posY, crystal.posZ, base);
    }

    public static float calculate(BlockPos pos, EntityPlayer base) {
        return calculate(pos.getX() + 0.5, pos.getY() + 1, pos.getZ() + 0.5, base);
    }

    public static int getEnchantmentModifierDamage(NonNullList<ItemStack> stacks, DamageSource source) {
        int modifier = 0;

        final int stacksSize = stacks.size();
        for (int i = 0; i < stacksSize; ++i) {
            final ItemStack stack = stacks.get(i);
            if (!stack.isEmpty()) {
                final NBTTagList nbttaglist = stack.getEnchantmentTagList();
                final int tagCount = nbttaglist.tagCount();
                for (int index = 0; index < tagCount; ++index) {
                    final int j = getCompoundTagAt(nbttaglist, index).getShort("id");
                    final int k = getCompoundTagAt(nbttaglist, index).getShort("lvl");
                    final Enchantment enchantment = Enchantment.getEnchantmentByID(j);

                    if (enchantment != null) {
                        modifier += enchantment.calcModifierDamage(k, source);
                    }
                }
            }
        }

        return modifier;
    }

    private static NBTTagCompound getCompoundTagAt(NBTTagList list, int i) {
        final List<NBTBase> nbtTagCompoundList = list.tagList;
        final int size = nbtTagCompoundList.size();
        if (i >= 0 && i < size) {
            NBTBase nbtbase = list.get(i);

            if (nbtbase.getId() == 10)
            {
                return (NBTTagCompound)nbtbase;
            }
        }

        return EMPTY_TAG_COMPOUND;
    }

    public static float getDifficultyMultiplier(final float distance) {
        switch (mc.world.getDifficulty()) {
            case PEACEFUL:
                return 0.0F;
            case EASY:
                return Math.min(distance / 2.0F + 1.0F, distance);
            case HARD:
                return distance * 3.0F / 2.0F;
        }

        return distance;
    }

    public static float getBlockDensity(final Vec3d vec, final AxisAlignedBB bb) {
        final double x = 1.0D / ((bb.maxX - bb.minX) * 2.0D + 1.0D);
        final double y = 1.0D / ((bb.maxY - bb.minY) * 2.0D + 1.0D);
        final double z = 1.0D / ((bb.maxZ - bb.minZ) * 2.0D + 1.0D);
        final double xFloor = (1.0D - Math.floor(1.0D / x) * x) / 2.0D;
        final double zFloor = (1.0D - Math.floor(1.0D / z) * z) / 2.0D;

        if (x >= 0.0D && y >= 0.0D && z >= 0.0D) {
            int air = 0;
            int traced = 0;

            for (float a = 0.0F; a <= 1.0F; a = (float) ((double) a + x)) {
                for (float b = 0.0F; b <= 1.0F; b = (float) ((double) b + y)) {
                    for (float c = 0.0F; c <= 1.0F; c = (float) ((double) c + z)) {
                        final double xOff = bb.minX + (bb.maxX - bb.minX) * (double) a;
                        final double yOff = bb.minY + (bb.maxY - bb.minY) * (double) b;
                        final double zOff = bb.minZ + (bb.maxZ - bb.minZ) * (double) c;

                        if (rayTraceBlocks(new Vec3d(xOff + xFloor, yOff, zOff + zFloor), vec, false, false, false) == null) {
                            air++;
                        }

                        traced++;
                    }
                }
            }

            return (float) air / (float) traced;
        } else {
            return 0.0F;
        }
    }

    private static final RayTraceResult RESULT = new RayTraceResult(Vec3d.ZERO, EnumFacing.DOWN);

    public static RayTraceResult calculateIntercept(AxisAlignedBB bb, Vec3d vecA, Vec3d vecB)
    {
        Vec3d vec3d = collideWithXPlane(bb, bb.minX, vecA, vecB);
        EnumFacing enumfacing = EnumFacing.WEST;
        Vec3d vec3d1 = collideWithXPlane(bb, bb.maxX, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.EAST;
        }

        vec3d1 = collideWithYPlane(bb, bb.minY, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.DOWN;
        }

        vec3d1 = collideWithYPlane(bb, bb.maxY, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.UP;
        }

        vec3d1 = collideWithZPlane(bb, bb.minZ, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.NORTH;
        }

        vec3d1 = collideWithZPlane(bb, bb.maxZ, vecA, vecB);

        if (vec3d1 != null && isClosest(vecA, vec3d, vec3d1))
        {
            vec3d = vec3d1;
            enumfacing = EnumFacing.SOUTH;
        }

        RESULT.typeOfHit = RayTraceResult.Type.BLOCK;
        return vec3d == null ? null : copy(vec3d, enumfacing, BlockPos.ORIGIN);
    }

    private static boolean isClosest(Vec3d p_186661_1_, @Nullable Vec3d p_186661_2_, Vec3d p_186661_3_)
    {
        return p_186661_2_ == null || p_186661_1_.squareDistanceTo(p_186661_3_) < p_186661_1_.squareDistanceTo(p_186661_2_);
    }

    private static Vec3d collideWithXPlane(AxisAlignedBB bb, double p_186671_1_, Vec3d p_186671_3_, Vec3d p_186671_4_)
    {
        Vec3d vec3d = p_186671_3_.getIntermediateWithXValue(p_186671_4_, p_186671_1_);
        return vec3d != null && bb.intersectsWithYZ(vec3d) ? vec3d : null;
    }


    private static Vec3d collideWithYPlane(AxisAlignedBB bb, double p_186663_1_, Vec3d p_186663_3_, Vec3d p_186663_4_)
    {
        Vec3d vec3d = p_186663_3_.getIntermediateWithYValue(p_186663_4_, p_186663_1_);
        return vec3d != null && bb.intersectsWithXZ(vec3d) ? vec3d : null;
    }

    private static Vec3d collideWithZPlane(AxisAlignedBB bb, double p_186665_1_, Vec3d p_186665_3_, Vec3d p_186665_4_)
    {
        Vec3d vec3d = p_186665_3_.getIntermediateWithZValue(p_186665_4_, p_186665_1_);
        return vec3d != null && bb.intersectsWithXY(vec3d) ? vec3d : null;
    }
    
    protected static RayTraceResult rayTrace(BlockPos pos, Vec3d start, Vec3d end, AxisAlignedBB boundingBox)
    {
        final Vec3d vec3d = new Vec3d(start.x - pos.getX(), start.y - pos.getY(), start.z - pos.getZ());
        final Vec3d vec3d1 = new Vec3d(end.x - pos.getX(), end.y - pos.getY(), end.z - pos.getZ());
        final RayTraceResult raytraceresult = calculateIntercept(boundingBox, vec3d, vec3d1);
        return raytraceresult == null ? null : copy(raytraceresult.hitVec.add(pos.getX(), pos.getY(), pos.getZ()), raytraceresult.sideHit, pos);
    }

    public static RayTraceResult copy(Vec3d hitVecIn, EnumFacing sideHitIn, BlockPos blockPosIn) {
        RESULT.hitInfo = hitVecIn;
        RESULT.sideHit = sideHitIn;
        RESULT.blockPos = blockPosIn;
        return RESULT;
    }

    public static RayTraceResult collisionRayTrace(IBlockState blockState, World worldIn, BlockPos pos, Vec3d start, Vec3d end)
    {
        return rayTrace(pos, start, end, blockState.getBoundingBox(worldIn, pos));
    }

    public static RayTraceResult rayTraceBlocks(Vec3d vec31, Vec3d vec32, final boolean stopOnLiquid, final boolean ignoreNoBox, final boolean returnLastUncollidableBlock) {
        int x1 = MathHelper.floor(vec31.x);
        int y1 = MathHelper.floor(vec31.y);
        int z1 = MathHelper.floor(vec31.z);
        final int x2 = MathHelper.floor(vec32.x);
        final int y2 = MathHelper.floor(vec32.y);
        final int z2 = MathHelper.floor(vec32.z);

        final World world = mc.world;
        final BlockPos.MutableBlockPos pos = new BlockPos.MutableBlockPos(x1, y1, z1);
        final IBlockState state = world.getBlockState(pos);
        final Block block = state.getBlock();

        if ((!ignoreNoBox || state.getCollisionBoundingBox(world, pos) != Block.NULL_AABB) && block.canCollideCheck(state, stopOnLiquid)) {
            final RayTraceResult raytraceresult = collisionRayTrace(state, world, pos, vec31, vec32);
            RESULT.typeOfHit = RayTraceResult.Type.BLOCK;
            if (raytraceresult != null) {
                return raytraceresult;
            }
        }

        RayTraceResult raytraceresult2 = null;
        int k1 = 200;

        while (k1-- >= 0) {
            if (x1 == x2 && y1 == y2 && z1 == z2) {
                return returnLastUncollidableBlock ? raytraceresult2 : null;
            }

            boolean flag2 = true;
            boolean flag = true;
            boolean flag1 = true;
            double d0 = 999.0D;
            double d1 = 999.0D;
            double d2 = 999.0D;

            if (x2 > x1) {
                d0 = (double) x1 + 1.0D;
            } else if (x2 < x1) {
                d0 = (double) x1 + 0.0D;
            } else {
                flag2 = false;
            }

            if (y2 > y1) {
                d1 = (double) y1 + 1.0D;
            } else if (y2 < y1) {
                d1 = (double) y1 + 0.0D;
            } else {
                flag = false;
            }

            if (z2 > z1) {
                d2 = (double) z1 + 1.0D;
            } else if (z2 < z1) {
                d2 = (double) z1 + 0.0D;
            } else {
                flag1 = false;
            }

            double d3 = 999.0D;
            double d4 = 999.0D;
            double d5 = 999.0D;
            double d6 = vec32.x - vec31.x;
            double d7 = vec32.y - vec31.y;
            double d8 = vec32.z - vec31.z;

            if (flag2) {
                d3 = (d0 - vec31.x) / d6;
            }

            if (flag) {
                d4 = (d1 - vec31.y) / d7;
            }

            if (flag1) {
                d5 = (d2 - vec31.z) / d8;
            }

            if (d3 == -0.0D) {
                d3 = -1.0E-4D;
            }

            if (d4 == -0.0D) {
                d4 = -1.0E-4D;
            }

            if (d5 == -0.0D) {
                d5 = -1.0E-4D;
            }

            EnumFacing enumfacing;

            if (d3 < d4 && d3 < d5) {
                enumfacing = x2 > x1 ? EnumFacing.WEST : EnumFacing.EAST;
                vec31 = new Vec3d(d0, vec31.y + d7 * d3, vec31.z + d8 * d3);
            } else if (d4 < d5) {
                enumfacing = y2 > y1 ? EnumFacing.DOWN : EnumFacing.UP;
                vec31 = new Vec3d(vec31.x + d6 * d4, d1, vec31.z + d8 * d4);
            } else {
                enumfacing = z2 > z1 ? EnumFacing.NORTH : EnumFacing.SOUTH;
                vec31 = new Vec3d(vec31.x + d6 * d5, vec31.y + d7 * d5, d2);
            }

            x1 = MathHelper.floor(vec31.x) - (enumfacing == EnumFacing.EAST ? 1 : 0);
            y1 = MathHelper.floor(vec31.y) - (enumfacing == EnumFacing.UP ? 1 : 0);
            z1 = MathHelper.floor(vec31.z) - (enumfacing == EnumFacing.SOUTH ? 1 : 0);
            pos.setPos(x1, y1, z1);
            final IBlockState state1 = mc.world.getBlockState(pos);
            final Block block1 = state1.getBlock();

            if (!ignoreNoBox || state1.getMaterial() == Material.PORTAL || state1.getCollisionBoundingBox(mc.world, pos) != Block.NULL_AABB) {
                if (block1.canCollideCheck(state1, stopOnLiquid)) {
                    final RayTraceResult raytraceresult1 = collisionRayTrace(state1, world, pos, vec31, vec32);
                    RESULT.typeOfHit = RayTraceResult.Type.BLOCK;
                    if (raytraceresult1 != null) {
                        return raytraceresult1;
                    }
                } else {
                    RESULT.typeOfHit = RayTraceResult.Type.MISS;
                    copy(vec31, enumfacing, pos);
                    raytraceresult2 = copy(vec31, enumfacing, pos);
                }
            }
        }

        return returnLastUncollidableBlock ? raytraceresult2 : null;
    }

}
