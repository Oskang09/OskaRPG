package me.oska.plugins.inventory;

import lombok.Getter;
import lombok.Setter;

public class InventoryOptions {

    public static InventoryOptions DEFAULT = new InventoryOptions();

    @Getter
    @Setter
    public boolean clickableByDefault;

    @Getter
    @Setter
    public boolean closableByEvent;

    public InventoryOptions() {
        clickableByDefault = false;
        closableByEvent = true;
    }

}
