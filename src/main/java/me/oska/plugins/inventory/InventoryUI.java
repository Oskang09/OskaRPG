package me.oska.plugins.inventory;

import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;

public abstract class InventoryUI<T> implements Cloneable {

    public static void register(JavaPlugin plugin) {
        InventoryListener.register(plugin);
    }

    @Getter
    protected Inventory inventory;

    private final Map<Integer, Consumer<InventoryClickEvent>> actions;

    @Getter
    protected T state;

    protected abstract InventoryOptions options();
    protected abstract T initialState();
    protected abstract void render();
    protected abstract void disposeState();

    protected void setState(Function<T, T> update) {
        setState(update.apply(this.state));
    }

    protected void setState(T state) {
        this.state = state;
        this.render();
    }

    protected void set(int index, ItemStack item) {
        this.set(index, item, null);
    }

    protected void set(int index, ItemStack item, Consumer<InventoryClickEvent> action) {
        this.inventory.setItem(index, item);
        if (action != null) {
            this.actions.put(index, action);
        }
    }

    protected void set(int[] index, ItemStack item) {
        this.set(index, item, null);
    }

    protected void set(int[] index, ItemStack item, Consumer<InventoryClickEvent> action) {
        for ( int i = 0; i < index.length; i++) {
            this.inventory.setItem(i, item);
            if (action != null) {
                this.actions.put(i, action);
            }
        }
    }

    protected void goTo(Player player, InventoryUI inventoryUI) {
        InventoryListener.goTo(player, inventoryUI);
    }

    protected void goBack(Player player) {
        InventoryListener.goBack(player);
    }

    void click(InventoryClickEvent event) {
        Consumer<InventoryClickEvent> action = this.actions.getOrDefault(event.getSlot(), null);
        if (action != null) {
            action.accept(event);
        }
    }

    public InventoryUI(InventoryType type, String title) {
        this();
        this.inventory = Bukkit.createInventory(null, type, title);
    }

    public InventoryUI(InventoryType type) {
        this();
        this.inventory = Bukkit.createInventory(null, type);
    }

    public InventoryUI(String title, int size) {
        this();
        this.inventory = Bukkit.createInventory(null, size, title);
    }

    private InventoryUI() {
        if (!InventoryListener.isRegistered()) {
            throw new IllegalStateException("Disabling InventoryUI because InventoryListener hasn't registered.");
        }
        this.actions = new HashMap<>();
    }

    @Override
    protected InventoryUI clone() {
        try {
            return (InventoryUI) super.clone();
        } catch (CloneNotSupportedException e) {
            e.printStackTrace();
        }
        return null;
    }
}
