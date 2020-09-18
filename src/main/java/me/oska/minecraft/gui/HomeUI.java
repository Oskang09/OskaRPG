package me.oska.minecraft.gui;

import me.oska.plugins.inventory.InventoryUI;

public class HomeUI extends InventoryUI<HomeUI.State> {

    public class State {

    }

    public HomeUI() {
        super("OskaRPG - Home", 64);
    }

    @Override
    protected boolean closableByEvent() {
        return true;
    }

    @Override
    protected State initialState() {
        return null;
    }

    @Override
    protected void render() {

    }

    @Override
    protected void disposeState() {

    }
}
