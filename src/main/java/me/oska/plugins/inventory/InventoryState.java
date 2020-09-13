package me.oska.plugins.inventory;

public abstract class InventoryState {
    public abstract boolean hasNext();
    public abstract boolean hasPrevious();
    public abstract void nextPage();
    public abstract void previousPage();
}
