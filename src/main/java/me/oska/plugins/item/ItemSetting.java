package me.oska.plugins.item;

import de.tr7zw.nbtapi.NBTItem;
import lombok.Getter;
import me.oska.plugins.hibernate.converter.InstantConverter;
import me.oska.plugins.hibernate.converter.ItemStackConverter;
import me.oska.plugins.player.GraphicLevel;
import me.oska.plugins.skill.ORPGSkill;
import org.bukkit.inventory.ItemStack;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;
import java.util.stream.Collectors;

@javax.persistence.Entity(name = "item_setting")
@Table(name = "item_setting")
public class ItemSetting {
    public static final String NBT_KEY = "oskarpg:basic_item";
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
        NBTItem nbti = new NBTItem(item);
        nbti.setString("clazz", NBT_KEY);
        nbti.setString("type", this.type.toString());
        nbti.setString("id", id);
        return nbti.getItem();
    }

    @Getter
    @Id
    private String id;

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

    @Column(columnDefinition = "jsonb")
    @Convert(converter = ItemStackConverter.class)
    protected ItemStack item;

    @Getter
    @Type(type = "jsonb")
    @Column(name = "skills", columnDefinition = "jsonb")
    protected Set<String> skills;

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

    public Set<ORPGSkill> getSkill() {
        if (orpgSkills == null) {
            orpgSkills = skills.parallelStream().map(ORPGSkill::getSkillById).collect(Collectors.toSet());
        }
        return orpgSkills;
    }
}
