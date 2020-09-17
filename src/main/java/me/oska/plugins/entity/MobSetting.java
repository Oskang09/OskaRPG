package me.oska.plugins.entity;

import lombok.Getter;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.exception.RunicException;
import org.bukkit.Bukkit;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.*;

@javax.persistence.Entity(name = "mob_setting")
@Table(name = "mob_setting")
public class MobSetting {
    private static final Map<String, MobSetting> registeredMobs = new HashMap<>();
    private static final AbstractRepository<MobSetting> repository = new AbstractRepository<>(MobSetting.class);

    public static MobSetting getMobSetting(String key) {
        MobSetting setting = registeredMobs.getOrDefault(key, null);
        if (setting == null) {
            try {
                setting = repository.find(key);
                registeredMobs.put(key, setting);
            } catch (RunicException e) {
                setting = null;
            }
        }
        return setting;
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

    @Getter
    @Type(type = "jsonb")
    @Column(name = "skills", columnDefinition = "jsonb")
    private Set<String> skillIds;
}
