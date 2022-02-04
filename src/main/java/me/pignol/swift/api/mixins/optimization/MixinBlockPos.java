package me.pignol.swift.api.mixins.optimization;

import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3i;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;

@Mixin(BlockPos.class)
public abstract class MixinBlockPos extends Vec3i {

    public MixinBlockPos(int x, int y, int z) {
        super(x, y, z);
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos up() {
        return new BlockPos(this.getX(), this.getY() + 1, this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos up(int distance) {
        return new BlockPos(this.getX(), this.getY() + distance, this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos down() {
        return new BlockPos(this.getX(), this.getY() - 1, this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos down(int distance) {
        return new BlockPos(this.getX(), this.getY() - distance, this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos north() {
        return new BlockPos(this.getX(), this.getY(), this.getZ() - 1);
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos north(int distance) {
        return new BlockPos(this.getX(), this.getY(), this.getZ() - distance);
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos south() {
        return new BlockPos(this.getX(), this.getY(), this.getZ() + 1);
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos south(int distance) {
        return new BlockPos(this.getX(), this.getY(), this.getZ() + distance);
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos west() {
        return new BlockPos(this.getX() - 1, this.getY(), this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos west(int distance) {
        return new BlockPos(this.getX() - distance, this.getY(), this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos east() {
        return new BlockPos(this.getX() + 1, this.getY(), this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos east(int distance) {
        return new BlockPos(this.getX() + distance, this.getY(), this.getZ());
    }

    /**
     * @author e
     * @reason e
     */
    @Overwrite
    public BlockPos offset(EnumFacing direction) {
        return new BlockPos(this.getX() + direction.getXOffset(), this.getY() + direction.getYOffset(), this.getZ() + direction.getZOffset());
    }

}