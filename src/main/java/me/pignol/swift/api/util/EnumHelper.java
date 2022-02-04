package me.pignol.swift.api.util;

import java.util.Locale;

public class EnumHelper {

    public static Enum<?> next(Enum<?> entry) {
        Enum<?>[] array = entry.getDeclaringClass().getEnumConstants();
        return array.length - 1 == entry.ordinal() ? array[0] : array[entry.ordinal() + 1];
    }

    public static Enum<?> previous(Enum<?> entry) {
        Enum<?>[] array = entry.getDeclaringClass().getEnumConstants();
        return entry.ordinal() == 0 ? array[array.length - 1] : array[entry.ordinal() - 1];
    }

    public static Enum<?> fromString(Enum<?> initial, String name) {
        Class<? extends Enum<?>> clazz = initial.getDeclaringClass();
        for (Enum<?> constant : clazz.getEnumConstants()) {
            if (constant.name().equalsIgnoreCase(name)) {
                return constant;
            }
        }
        return initial;
    }

    public static String getCapitalizedName(String name) {
        String firstLetter = name.substring(0, 1);
        String remainingLetters = name.substring(1);
        return firstLetter.toUpperCase(Locale.ROOT) + remainingLetters.toLowerCase(Locale.ROOT);
    }

    public static String getCapitalizedName(Enum<?> enu) {
        String name = enu.name();
        return getCapitalizedName(name);
    }

}
