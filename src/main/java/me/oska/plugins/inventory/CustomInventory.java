package me.oska.plugins.inventory;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomInventory<T> {

    @Getter
    private Inventory inventory;
    @Getter
    private InventoryOptions options;
    private Map<Integer, Runnable> actions;

    @Getter
    protected T state;

    protected abstract void render();
    protected abstract T initialState();

    public void openInventory(Player player) {
        this.state = this.initialState();
        this.render();

        InventoryListener.addInventory(this);
        player.openInventory(this.inventory);
    }

    protected void setState(T state) {
        this.state = state;
        this.render();
    }

    protected void option(InventoryOptions options) {
        this.options = options;
    }

    protected void set(int index, ItemStack item) {
        this.set(index, item, null);
    }

    protected void set(int index, ItemStack item, Runnable action) {
        this.inventory.setItem(index, item);
        if (action != null) {
            this.actions.put(index, action);
        }
    }

    protected void set(int[] index, ItemStack item) {
        this.set(index, item, null);
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
        this.inventory = Bukkit.createInventory(null, type, title);
    }

    public CustomInventory(InventoryType type) {
        super();
        this.inventory = Bukkit.createInventory(null, type);
    }

    public CustomInventory(String title, int size) {
        super();
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    private CustomInventory() {
        if (!InventoryListener.isRegistered()) {
            throw new IllegalStateException("Disabling CustomInventory because InventoryListener hasn't registered.");
        }
        this.actions = new HashMap<>();
    }
}
