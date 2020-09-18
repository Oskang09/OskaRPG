package me.oska.plugins;

import lombok.Getter;
import lombok.Setter;

public class BasicAttribute {

    @Getter
    private int value;

    @Getter
    @Setter
    private int conversion;

    public BasicAttribute() {
        this(0);
    }

    public BasicAttribute(int value) {
        this.value = value;
    }

    public int getTotal() {
        return this.value + this.conversion;
    }
}
