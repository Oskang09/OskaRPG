package me.oska.plugins.item;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.oska.plugins.BaseItem;
import me.oska.plugins.LevelObject;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.skill.ORPGSkill;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@javax.persistence.Entity(name = "custom_item")
@Table(name = "custom_item")
public class CustomItem extends LevelObject implements BaseItem {
    public static final String NBT_KEY = "oskarpg:orpg_item";

    private static Logger log;
    private static Map<String, CustomItem> items;
    private static AbstractRepository<CustomItem> repository;

    public static void register(JavaPlugin plugin) {
        log = new Logger("ORPGItem");
        repository = new AbstractRepository<>(CustomItem.class);
        items = new HashMap<>();

        try {
            repository.findAll().forEach(x -> {
                x.base = BasicItem.getItemById(x.base_id);
                x.skills = x.base.getSkills();
            });
        } catch (RunicException e) {
            e.printStackTrace();
        }
    }

    public static CustomItem getItemById(String id) {
        return items.getOrDefault(id, null);
    }

    public static CustomItem from(ItemStack item) {
        NBTItem nbti = new NBTItem(item);
        String id = nbti.getString("id");
        return getItemById(id);
    }

    public static boolean is(ItemStack item) {
        return new NBTItem(item).getString("clazz").equals(NBT_KEY);
    }

    public static boolean is(ItemStack item, ItemType type) {
        NBTItem nbti = new NBTItem(item);
        if (!nbti.getString("clazz").equals(NBT_KEY)) {
            return false;
        }
        return ItemType.valueOf(nbti.getString("type")) == type;
    }

    public ItemStack getItem() {
        NBTItem nbti = new NBTItem(base.item);
        nbti.setString("clazz", NBT_KEY);
        nbti.setString("type", base.type.toString());
        nbti.setString("id", this.uuid);
        nbti.setString("owner", this.player_id);
        return nbti.getItem();
    }

    @Override
    public ItemType getType() {
        return base.type;
    }

    @Override
    public String getKey() {
        return NBT_KEY + ":" + uuid;
    }

    @Getter
    @Id
    private String uuid;

    @Getter
    private int strength;

    @Getter
    private int dexterity;

    @Getter
    private int accuracy;

    @Getter
    private int range;

    @Getter
    private int damage;

    @Getter
    private int health;

    private int experience;

    private int level;

    private String player_id;

    private String base_id;

    @Getter
    @Transient
    private Set<ORPGSkill> skills;

    @Getter
    @Transient
    private BasicItem base;

    public CustomItem() {

    }

    public Integer getTotalStrength() {
        return base.getStrength() + strength;
    }

    public Integer getTotalDexterity() {
        return base.getDexterity() + dexterity;
    }

    public Integer getTotalAccuracy() {
        return base.getAccuracy() + accuracy;
    }

    public Integer getTotalRange() {
        return base.getRange() + range;
    }

    public Integer getTotalDamage() {
        return base.getDamage() + damage;
    }

    public Integer getTotalHealth() {
        return base.getHealth() + health;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public int getExperience() {
        return experience;
    }
}
