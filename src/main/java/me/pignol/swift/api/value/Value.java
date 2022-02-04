package me.pignol.swift.api.value;

import me.pignol.swift.client.event.events.ValueEvent;
import net.minecraftforge.common.MinecraftForge;

import java.util.function.Predicate;

public class Value<T> {

    private final String name;

    private final T defaultValue;
    private T value;

    private T min;
    private T max;
    private T inc;

    private Predicate<T> visibility;

    public Value(String name, T value) {
        this.name = name;
        this.defaultValue = value;
        this.value = value;
    }

    public Value(String name, T value, T min, T max) {
        this(name, value);
        this.min = min;
        this.max = max;
    }

    public Value(String name, T value, T min, T max, T inc) {
        this(name, value);
        this.min = min;
        this.max = max;
        this.inc = inc;
    }

    public Value(String name, T value, Predicate<T> visibility) {
        this(name, value);
        this.visibility = visibility;
    }

    public Value(String name, T value, T min, T max, Predicate<T> visibility) {
        this(name, value, min, max);
        this.visibility = visibility;
    }

    public Value(String name, T value, T min, T max, T inc, Predicate<T> visibility) {
        this(name, value, min, max, inc);
        this.visibility = visibility;
    }

    public T getValue() {
        return this.value;
    }

    public void setValue(T value) {
        if (min != null && max != null) {
            if (((Number)min).floatValue() > ((Number) value).floatValue()) {
                value = min;
            }
            if (((Number)max).floatValue() < ((Number) value).floatValue()) {
                value = max;
            }
        }
        this.value = value;
        MinecraftForge.EVENT_BUS.post(new ValueEvent(this));
    }

    public T getMin() {
        return min;
    }

    public T getMax() {
        return max;
    }

    public T getInc()
    {
        return inc;
    }


    public String getName() {
        return name;
    }

    public T getDefaultValue() {
        return defaultValue;
    }

    public boolean isVisible() {
        if (visibility == null) {
            return true;
        }
        return visibility.test(getValue());
    }

}
