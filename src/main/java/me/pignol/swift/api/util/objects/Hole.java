package me.pignol.swift.api.util.objects;

import net.minecraft.util.math.BlockPos;

public class Hole {

    private final BlockPos pos;
    private final boolean safe;

    public Hole(BlockPos pos, boolean safe) {
        this.pos = pos;
        this.safe = safe;
    }

    public BlockPos getPos() {
        return pos;
    }

    public boolean isSafe() {
        return safe;
    }

}
