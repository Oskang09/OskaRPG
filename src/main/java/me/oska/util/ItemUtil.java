package me.oska.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ItemUtil {

    private Material material;

    private ItemUtil(Material material) {
        this.material = material;
    }

    public ItemStack toItem() {
        ItemStack item = new ItemStack(this.material);
        return item;
    }

    public static ItemUtil of(Material material) {
        return new ItemUtil(material);
    }
}
