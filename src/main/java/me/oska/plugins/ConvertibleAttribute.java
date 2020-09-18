package me.oska.plugins;

import lombok.Getter;

public class ConvertibleAttribute {

    @Getter
    private int value;

    public ConvertibleAttribute() {
        this(0);
    }

    public ConvertibleAttribute(int value) {
        this.value = value;
    }

    public int convertDamage() {
        return (int)Math.ceil(value * 0.1);
    }

    public int convertRange() {
        return (int)Math.ceil(value * 0.1);
    }

    public int convertHealth() {
        return (int)Math.ceil(value * 0.1);
    }
}
