package me.pignol.swift.api.util;

import net.minecraft.util.EnumFacing;

public class FacingUtil {

    public static final EnumFacing[] VALUES = EnumFacing.values();
    public static final EnumFacing[] VALUESNOUP = new EnumFacing[]{
            EnumFacing.DOWN,
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.EAST
    };
    public static final EnumFacing[] VALUESNODOWN = new EnumFacing[]{
            EnumFacing.UP,
            EnumFacing.NORTH,
            EnumFacing.SOUTH,
            EnumFacing.WEST,
            EnumFacing.EAST
    };

}
