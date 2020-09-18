package me.oska.plugins.inventory;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.PlayerSwapHandItemsEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;

public class InventoryListener implements Listener {

    private static Map<String, LinkedList<InventoryUI>> inventories;
    private static boolean registered;
    protected static void register(JavaPlugin plugin) {
        registered = true;
        inventories = new HashMap<>();

        plugin.getServer().getPluginManager().registerEvents(new InventoryListener(), plugin);
    }

    protected static boolean isRegistered() {
        return registered;
    }

    protected static void goTo(Player player, InventoryUI inventoryUI) {
        String uuid = player.getUniqueId().toString();
        inventories.compute(uuid, (__, lists) -> {
            if (lists == null) {
                lists = new LinkedList<>();
            }

            InventoryUI instance = inventoryUI.clone();
            lists.addFirst(instance);
            player.closeInventory();

            instance.state = instance.initialState();
            player.openInventory(instance.getInventory());
            instance.render();
            return lists;
        });
    }

    protected static void goBack(Player player) {
        String uuid = player.getUniqueId().toString();
        inventories.compute(uuid, (__, lists) -> {
            InventoryUI last = lists.removeFirst();
            player.closeInventory();
            last.disposeState();
            if (lists.size() == 0) {
                return null;
            }

            InventoryUI instance = lists.getFirst();
            player.openInventory(instance.getInventory());
            return lists;
        });
    }

    private InventoryListener() {}

    @EventHandler(priority = EventPriority.MONITOR)
    public void onOpen(PlayerSwapHandItemsEvent event) {
        Player player = event.getPlayer();
        if (player.isSneaking()) {
            event.setCancelled(true);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClick(InventoryClickEvent event) {
        String uuid = event.getWhoClicked().getUniqueId().toString();
        LinkedList<InventoryUI> inventoryList = inventories.getOrDefault(uuid, null);
        if (inventoryList == null || inventoryList.size() == 0) {
            return;
        }

        InventoryUI inventory = inventoryList.getFirst();
        if (event.getClickedInventory() == inventory.getInventory()) {
            event.setCancelled(!inventory.options().clickableByDefault);
            inventory.click(event);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        String uuid = event.getPlayer().getUniqueId().toString();
        LinkedList<InventoryUI> inventoryList = inventories.getOrDefault(uuid, null);
        if (inventoryList == null || inventoryList.size() == 0) {
            return;
        }

        InventoryUI inventory = inventoryList.getFirst();
        if (inventory.options().closableByEvent) {
            goBack(player);
            return;
        }
        player.openInventory(inventory.getInventory());
    }

}
