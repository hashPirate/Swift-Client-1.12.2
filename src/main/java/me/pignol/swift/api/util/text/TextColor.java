package me.pignol.swift.api.util.text;

public class TextColor {

    //TODO put all of this inside an enum so we dont need a switch / if else chain

    public static final String SECTIONSIGN  = "\u00A7";
    public static final String BLACK        = SECTIONSIGN + "0";
    public static final String DARK_BLUE    = SECTIONSIGN + "1";
    public static final String DARK_GREEN   = SECTIONSIGN + "2";
    public static final String DARK_AQUA    = SECTIONSIGN + "3";
    public static final String DARK_RED     = SECTIONSIGN + "4";
    public static final String DARK_PURPLE  = SECTIONSIGN + "5";
    public static final String GOLD         = SECTIONSIGN + "6";
    public static final String GRAY         = SECTIONSIGN + "7";
    public static final String DARK_GRAY    = SECTIONSIGN + "8";
    public static final String BLUE         = SECTIONSIGN + "9";
    public static final String GREEN        = SECTIONSIGN + "a";
    public static final String AQUA         = SECTIONSIGN + "b";
    public static final String RED          = SECTIONSIGN + "c";
    public static final String LIGHT_PURPLE = SECTIONSIGN + "d";
    public static final String YELLOW       = SECTIONSIGN + "e";
    public static final String WHITE        = SECTIONSIGN + "f";
    public static final String OBFUSCATED   = SECTIONSIGN + "k";
    public static final String BOLD         = SECTIONSIGN + "l";
    public static final String STRIKE       = SECTIONSIGN + "m";
    public static final String UNDERLINE    = SECTIONSIGN + "n";
    public static final String ITALIC       = SECTIONSIGN + "o";
    public static final String RESET        = SECTIONSIGN + "r";
    public static final String RAINBOW      = SECTIONSIGN + "+";

    public static String coloredString(String string, Color color) {
        switch (color) {
            case AQUA:
                return AQUA + string + RESET;
            case WHITE:
                return WHITE + string + RESET;
            case BLACK:
                return BLACK + string + RESET;
            case DARK_BLUE:
                return DARK_BLUE + string + RESET;
            case DARK_GREEN:
                return DARK_GREEN + string + RESET;
            case DARK_AQUA:
                return DARK_AQUA + string + RESET;
            case DARK_RED:
                return DARK_RED + string + RESET;
            case DARK_PURPLE:
                return DARK_PURPLE + string + RESET;
            case GOLD:
                return GOLD + string + RESET;
            case DARK_GRAY:
                return DARK_GRAY + string + RESET;
            case GRAY:
                return GRAY + string + RESET;
            case BLUE:
                return BLUE + string + RESET;
            case RED:
                return RED + string + RESET;
            case GREEN:
                return GREEN + string + RESET;
            case LIGHT_PURPLE:
                return LIGHT_PURPLE + string + RESET;
            case YELLOW:
                return YELLOW + string + RESET;
            default :
                return string;
        }
    }

    public enum Color {

        NONE, WHITE, BLACK, DARK_BLUE, DARK_GREEN, DARK_AQUA, DARK_RED, DARK_PURPLE, GOLD, GRAY, DARK_GRAY, BLUE, GREEN, AQUA, RED, LIGHT_PURPLE, YELLOW
    }

}
