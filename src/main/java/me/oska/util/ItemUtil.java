package me.oska.util;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;

public class ItemUtil {

    private Material material;
    private String displayName;
    private List<String> lore;

    private ItemUtil(Material material) {
        this.material = material;
        this.displayName = material.name();
        this.lore = new ArrayList<>();
    }

    public ItemUtil addLore(String lore) {
        this.lore.add(lore);
        return this;
    }

    public ItemUtil setLore(List<String> lore) {
        this.lore = lore;
        return this;
    }

    public ItemStack toItem() {
        ItemStack item = new ItemStack(this.material);
        ItemMeta meta = Bukkit.getItemFactory().getItemMeta(this.material);
        meta.setUnbreakable(true);
        meta.setDisplayName(this.displayName);
        meta.setLore(this.lore);
        meta.addItemFlags(ItemFlag.HIDE_UNBREAKABLE, ItemFlag.HIDE_POTION_EFFECTS, ItemFlag.HIDE_ENCHANTS, ItemFlag.HIDE_ATTRIBUTES);
        item.setItemMeta(meta);
        return item;
    }

    public static ItemUtil of(Material material) {
        return new ItemUtil(material);
    }
}
