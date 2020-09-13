package me.oska.plugins.inventory;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.HashMap;
import java.util.Map;

public abstract class CustomInventory<T extends InventoryState> {

    @Getter
    private Inventory inventory;
    private InventoryOptions option;
    private Map<Integer, Runnable> actions;

    @Getter
    private T state;

    public abstract void render(T state);

    public void open(Player player) {
        render(this.state);
        player.openInventory(getInventory());
    }

    protected void option(InventoryOptions option) {
        this.option = option;
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

    protected void setState(T state) {
        this.state = state;
        this.render(this.state);
    }

    private Runnable getPresetAction(InventoryAction action) {
        if (action == InventoryAction.NONE) {
            return null;
        }

        if (action == InventoryAction.NEXT && this.state.hasNext()) {
            return null;
        }

        if (action == InventoryAction.PREVIOUS && this.state.hasPrevious()) {
            return null;
        }

        return () -> {
            this.inventory.clear();
            if (action == InventoryAction.NEXT) {
                this.state.nextPage();
            } else if (action == InventoryAction.PREVIOUS) {
                this.state.previousPage();
            }
            this.render(this.state);
        };
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
