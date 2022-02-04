package me.pignol.swift.api.util;

import net.minecraft.util.EnumFacing;

public class DirectionUtil {

    public static final Direction[] DIRECTIONS = Direction.values();

    public static String convertToCoords(EnumFacing direction) {
        switch (direction) {
            case WEST:
                return "-X";
            case EAST:
                return "+X";
            case SOUTH:
                return "+Z";
            case NORTH:
                return "-Z";
            default:
                return "INVALID";
        }
    }

    public static String convertToCoords(Direction direction) {
        switch (direction) {
            case W:
                return "-X";
            case E:
                return "+X";
            case S:
                return "+Z";
            case N:
                return "-Z";
            default:
                return "INVALID";
        }
    }


    public enum Direction {
        N,
        W,
        S,
        E
    }

}
