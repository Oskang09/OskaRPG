package me.oska.plugins.inventory;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class InventoryListener implements org.bukkit.event.Listener {

    private static Map<Inventory, InventoryUI> inventories;
    private static boolean registered;
    protected static void register(JavaPlugin plugin) {
        registered = true;
        inventories = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    protected static boolean isRegistered() {
        return registered;
    }

    protected static void addInventory(InventoryUI inv) {
        inventories.put(inv.getInventory(), inv);
    }

    protected static void removeInventory(InventoryUI inv) {
        inventories.remove(inv.getInventory());
    }

    private InventoryListener() {}


    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event) {
        InventoryUI inventory = inventories.getOrDefault(event.getInventory(), null);
        if (inventory == null) {
            return;
        }

        event.setCancelled(true);
        if (!(event.getWhoClicked() instanceof Player)) {
            return;
        }
        inventory.click(event.getSlot());
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        if (!inventories.containsKey(event.getInventory())) {
            return;
        }
        inventories.remove(event.getInventory());
    }

}
