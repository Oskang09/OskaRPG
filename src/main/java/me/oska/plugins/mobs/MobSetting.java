package me.oska.plugins.mobs;

import lombok.Getter;
import me.oska.plugins.hibernate.converter.InstantConverter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Id;
import javax.persistence.Table;
import java.time.Instant;
import java.util.*;

@javax.persistence.Entity(name = "mob_setting")
@Table(name = "mob_setting")
public class MobSetting {
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
    private Set<String> skills;

    @Convert(converter = InstantConverter.class)
    @Column(name = "createdAt")
    protected Instant createdAt;

    @Convert(converter = InstantConverter.class)
    @Column(name = "updatedAt")
    protected Instant updatedAt;
}
