package me.oska.plugins.inventory;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomInventory {

    private Inventory inventory;
    private InventoryOptions options;
    private Map<Integer, Runnable> actions;

    public abstract void load();

    public void open(Player player) {
        if (this.options.isStatic() && !this.options.isLoaded()) {
            load();
            this.options.setLoaded(true);
        }

        player.openInventory(getInventory());
    }

    public Inventory getInventory() {
        return inventory;
    }

    protected void option(InventoryOptions options) {
        this.options = options;
    }

    protected void set(int index, ItemStack item) {
        set(index, item, null);
    }

    protected void set(int index, ItemStack item, Runnable action) {
        this.inventory.setItem(index, item);
        if (action != null) {
            this.actions.put(index, action);
        }
    }

    protected void set(int[] index, ItemStack item) {
        set(index, item, null);
    }

    protected void set(int[] index, ItemStack item, Runnable action) {
        for ( int i = 0; i < index.length; i++) {
            this.inventory.setItem(i, item);
            if (action != null) {
                this.actions.put(i, action);
            }
        }
    }

    public CustomInventory(InventoryType type, String title) {
        super();
        inventory = Bukkit.createInventory(null, type, title);
    }

    public CustomInventory(InventoryType type) {
        super();
        inventory = Bukkit.createInventory(null, type);
    }

    public CustomInventory(String title, int size) {
        super();
        inventory = Bukkit.createInventory(null, size, title);
    }

    private CustomInventory() {
        if (!InventoryListener.isRegistered()) {
            throw new IllegalStateException("Disabling CustomInventory because InventoryListener hasn't registered.");
        }
        this.actions = new HashMap<>();
    }
}
