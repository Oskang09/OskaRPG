package me.oska.plugins.item;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
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

@javax.persistence.Entity(name = "orpg_item")
@Table(name = "orpg_item")
public class ORPGItem extends LevelObject {
    private static Logger log;
    private static Map<String, ORPGItem> activeItems;
    private static Map<String, ItemSetting> registeredItems;
    private static AbstractRepository<ItemSetting> itemRepository;
    private static AbstractRepository<ORPGItem> repository;

    public static void register(JavaPlugin plugin) {
        log = new Logger("ORPGItem");
        itemRepository = new AbstractRepository<>(ItemSetting.class);
        repository = new AbstractRepository<>(ORPGItem.class);
        activeItems = new HashMap<>();
        registeredItems = new HashMap<>();

        try {
            itemRepository.findAll().forEach(x -> registeredItems.put(x.getId(), x));
            repository.findAll().forEach(x -> {
                x.setting = registeredItems.get(x.setting_id);
                x.skills = x.setting.getSkill();
                activeItems.put(x.uuid, x);
            });
        } catch (RunicException e) {
            e.printStackTrace();
        }
    }

    public static final String NBT_KEY = "oskarpg:orpg_item";
    public static boolean is(ItemStack item) {
        return new NBTItem(item).getString("clazz") == NBT_KEY;
    }

    public static boolean is(ItemStack item, ItemType type) {
        NBTItem nbti = new NBTItem(item);
        if (nbti.getString("clazz") != NBT_KEY) {
            return false;
        }
        return ItemType.valueOf(nbti.getString("type")) == type;
    }

    public ItemStack getItem() {
        NBTItem nbti = new NBTItem(this.setting.item);
        nbti.setString("clazz", NBT_KEY);
        nbti.setString("type", this.type.toString());
        nbti.setString("id", this.uuid);
        nbti.setString("owner", this.player_id);
        return nbti.getItem();
    }

    @Getter
    @Id
    private String uuid;

    @Getter
    private Integer strength;

    @Getter
    private Integer dexterity;

    @Getter
    private Integer accuracy;

    @Getter
    private Integer range;

    @Getter
    private Integer damage;

    @Getter
    private Integer health;

    private int experience;

    private int level;

    private String player_id;
    private String setting_id;

    @Column(columnDefinition = "VARCHAR(10) default 'MATERIAL'")
    @Enumerated(EnumType.STRING)
    protected ItemType type = ItemType.MATERIAL;

    @Getter
    @Transient
    private Set<ORPGSkill> skills;

    @Getter
    @Transient
    private ItemSetting setting;

    public ORPGItem() {

    }

    public Integer getTotalStrength() {
        return setting.getStrength() + strength;
    }

    public Integer getTotalDexterity() {
        return setting.getDexterity() + dexterity;
    }

    public Integer getTotalAccuracy() {
        return setting.getAccuracy() + accuracy;
    }

    public Integer getTotalRange() {
        return setting.getRange() + range;
    }

    public Integer getTotalDamage() {
        return setting.getDamage() + damage;
    }

    public Integer getTotalHealth() {
        return setting.getHealth() + health;
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
