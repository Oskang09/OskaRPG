package me.oska.plugins.player;

import lombok.Getter;
import me.oska.plugins.hibernate.BaseEntity;
import me.oska.plugins.hibernate.converter.InstantConverter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

//@NamedNativeQueries({
//    @NamedNativeQuery(
//        name = "ORPGPlayer.getByName",
//        query = "SELECT * FROM orpgplayer WHERE name = :1",
//        resultClass = ORPGPlayer.class
//    )
//})
@javax.persistence.Entity(name = "player_data")
@Table(name = "player_data")
public class PlayerData extends BaseEntity {

    @Id
    protected String uuid;
    protected int level;
    protected int experience;
    protected int balance;
    protected int strength;
    protected int dexterity;
    protected int accuracy;
    protected int range;
    protected int damage;
    protected int health;

    @Column(name = "isOnline")
    protected boolean isOnline;

    @Convert(converter = InstantConverter.class)
    @Column(name = "lastOnlineAt")
    protected Instant lastOnline;

    @Convert(converter = InstantConverter.class)
    @Column(name = "createdAt")
    protected Instant createdAt;

    @Convert(converter = InstantConverter.class)
    @Column(name = "updatedAt")
    protected Instant updatedAt;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    protected Set<String> permissions;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    protected Set<String> constumes;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    protected Set<String> equipments;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    protected Set<String> accessories;

    @Column(columnDefinition = "VARCHAR(6) default 'LOW'")
    @Enumerated(EnumType.STRING)
    protected GraphicLevel graphic = GraphicLevel.LOW;

    @Column(columnDefinition = "VARCHAR(8) default 'VERIFIED'")
    @Enumerated(EnumType.STRING)
    protected Status status = Status.VERIFIED;
}
