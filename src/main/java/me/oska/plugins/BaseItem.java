package me.oska.plugins;

import me.oska.plugins.item.BasicItem;
import me.oska.plugins.item.CustomItem;
import me.oska.plugins.item.ItemType;
import me.oska.plugins.skill.ORPGSkill;
import org.bukkit.inventory.ItemStack;
import java.util.Set;

public interface BaseItem {

    static BaseItem from(ItemStack item) {
        if (BasicItem.is(item)) {
            return BasicItem.from(item);
        }

        if (CustomItem.is(item)) {
            return CustomItem.from(item);
        }
        return null;
    }

    static boolean is(ItemStack item) {
        return BasicItem.is(item) || CustomItem.is(item);
    }

    static boolean is(ItemStack item, ItemType type) {
        return BasicItem.is(item, type) || CustomItem.is(item, type);
    }

    ItemStack getItem();
    ItemType getType();
    Set<ORPGSkill> getSkills();
    String getKey();
    int getStrength();
    int getDexterity();
    int getAccuracy();
    int getRange();
    int getDamage();
    int getHealth();
}
