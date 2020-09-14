package me.oska.plugins.inventory;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public class InventoryListener implements Listener {

    private static Map<Inventory, CustomInventory> inventories;
    private static boolean registered;
    public static void registerTo(JavaPlugin plugin) {
        registered = true;
        inventories = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    protected static boolean isRegistered() {
        return registered;
    }

    protected static void addInventory(CustomInventory inv) {
        inventories.put(inv.getInventory(), inv);
    }

    protected static void removeInventory(CustomInventory inv) {
        inventories.remove(inv.getInventory());
    }

    private InventoryListener() {}


    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryInteractEvent event) {
        if (!inventories.containsKey(event.getInventory())) {
            return;
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        if (!inventories.containsKey(event.getInventory())) {
            return;
        }
        inventories.remove(event.getInventory());
    }

}
