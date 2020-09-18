package me.oska.plugins.item;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.oska.plugins.BaseItem;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.converter.InstantConverter;
import me.oska.plugins.hibernate.converter.ItemStackConverter;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.skill.ORPGSkill;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@javax.persistence.Entity(name = "basic_item")
@Table(name = "basic_item")
public class BasicItem implements BaseItem {
    public static final String NBT_KEY = "oskarpg:basic_item";

    private static Logger log;
    private static Map<String, BasicItem> items;
    private static AbstractRepository<BasicItem> repository;

    public static void register(JavaPlugin plugin) {
        log = new Logger("ORPGItem");
        items = new HashMap<>();
        repository = new AbstractRepository<>(BasicItem.class);

        try {
            repository.findAll().forEach(x -> {
                x.getSkills();
                items.put(x.getId(), x);
            });
        } catch (RunicException e) {
            e.printStackTrace();
        }
    }

    public static BasicItem getItemById(String id) {
        return items.getOrDefault(id, null);
    }

    public static BasicItem from(ItemStack item) {
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
        NBTItem nbti = new NBTItem(item);
        nbti.setString("clazz", NBT_KEY);
        nbti.setString("type", type.toString());
        nbti.setString("id", id);
        return nbti.getItem();
    }

    @Override
    public String getKey() {
        return NBT_KEY + ":" + id;
    }

    @Getter
    @Id
    private String id;

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

    @Column(columnDefinition = "jsonb")
    @Convert(converter = ItemStackConverter.class)
    protected ItemStack item;

    @Type(type = "jsonb")
    @Column(name = "skills", columnDefinition = "jsonb")
    protected Set<String> skills;

    @Getter
    @Column(columnDefinition = "VARCHAR(10) default 'MATERIAL'")
    @Enumerated(EnumType.STRING)
    protected ItemType type = ItemType.MATERIAL;

    @Convert(converter = InstantConverter.class)
    @Column(name = "createdAt")
    protected Instant createdAt;

    @Convert(converter = InstantConverter.class)
    @Column(name = "updatedAt")
    protected Instant updatedAt;

    @Transient
    protected Set<ORPGSkill> orpgSkills;

    @Override
    public Set<ORPGSkill> getSkills() {
        if (orpgSkills == null) {
            orpgSkills = skills.parallelStream().map(ORPGSkill::getSkillById).collect(Collectors.toSet());
        }
        return orpgSkills;
    }
}
