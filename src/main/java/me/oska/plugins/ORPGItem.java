package me.oska.plugins;

import lombok.Getter;
import lombok.Setter;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.converter.ItemConverter;
import org.bukkit.inventory.ItemStack;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table
public class ORPGItem extends BaseEntity {
    private static AbstractRepository<ORPGItem> repository = new AbstractRepository(ORPGItem.class);

    @Getter
    @Column(columnDefinition = "jsonb")
    @Convert(converter = ItemConverter.class)
    private ItemStack item;

    @Id
    private String uuid;

    @Getter
    @Setter
    private String owner;

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

    public ORPGItem() {
        this.uuid = UUID.randomUUID().toString();
    }
}
