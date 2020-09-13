package me.oska.plugins;

import lombok.Getter;
import lombok.Setter;
import me.oska.plugins.openjpa.AbstractRepository;
import me.oska.plugins.openjpa.converter.ItemConverter;
import org.bukkit.inventory.ItemStack;

import javax.persistence.*;
import java.util.UUID;

@Entity
@Table
public class ORPGItem {
    private static AbstractRepository<ORPGItem> repository = new AbstractRepository(ORPGItem.class);

    @Getter
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
