package me.oska.plugins.entity;

import lombok.Getter;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.converter.ItemStackConverter;
import org.bukkit.inventory.ItemStack;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.Set;

@Entity(name = "orpg_item")
@Table(name = "orpg_item")
public class ORPGItem extends BaseEntity {
    private static final AbstractRepository<ORPGItem> repository = new AbstractRepository<>(ORPGItem.class);

    @Getter
    @Column(columnDefinition = "jsonb")
    @Convert(converter = ItemStackConverter.class)
    private ItemStack item;

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

    @Type(type = "jsonb")
    @Column(name = "skills", columnDefinition = "jsonb")
    private Set<String> skillIds;

    @Transient
    private Set<ORPGSkill> skills;

    public ORPGItem() {}
}
