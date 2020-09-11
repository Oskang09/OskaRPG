package me.oska.plugins;

import me.oska.plugins.openjpa.AbstractRepository;
import me.oska.plugins.openjpa.exception.RunicException;
import me.oska.plugins.orpg.GraphicLevel;
import me.oska.plugins.orpg.Skill;
import me.oska.plugins.orpg.SkillType;
import me.oska.plugins.orpg.Stats;
import org.apache.openjpa.persistence.Externalizer;
import org.apache.openjpa.persistence.Factory;
import org.apache.openjpa.persistence.Persistent;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Entity
@Table
public class ORPGPlayer {
    private static final int BASE_XP = 700;

    private static Map<String, ORPGPlayer> players = new HashMap();
    private static AbstractRepository<ORPGPlayer> repository = new AbstractRepository(ORPGPlayer.class);

    public static double getXpAtLevel(int level) {
        return BASE_XP * Math.sqrt(level) + ( BASE_XP * level);
    }

    public static ORPGPlayer getByPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        return players.getOrDefault(uuid, null);
    }

    public static ORPGPlayer getByUUID(String uuid) {
        return players.getOrDefault(uuid, null);
    }

    @Transient
    private Player player;

    @Persistent
    @Externalizer("toSQL")
    @Factory("fromSQL")
    private List<ORPGSkill> skills;

    @Id
    private String uuid;
    private String name;
    private Integer level;
    private double xp;

    @Enumerated(EnumType.ORDINAL)
    private GraphicLevel graphic;

    @Persistent
    @Externalizer("toSQL")
    @Factory("fromSQL")
    private Stats strength;

    @Persistent
    @Externalizer("toSQL")
    @Factory("fromSQL")
    private Stats dexterity;

    @Persistent
    @Externalizer("toSQL")
    @Factory("fromSQL")
    private Stats accuracy;

    @Persistent
    @Externalizer("toSQL")
    @Factory("fromSQL")
    private Stats range;

    @Persistent
    @Externalizer("toSQL")
    @Factory("fromSQL")
    private Stats damage;

    @Persistent
    @Externalizer("toSQL")
    @Factory("fromSQL")
    private Stats health;

    private ORPGPlayer() {}

    public static ORPGPlayer load(Player player) {
        ORPGPlayer instance = null;
        try {
            instance = repository.findOrCreate(
                    player.getUniqueId().toString(),
                    () -> { 
                        ORPGPlayer entity = new ORPGPlayer();
                        entity.uuid = player.getUniqueId().toString();
                        entity.graphic = GraphicLevel.LOW;
                        entity.level = 1;
                        entity.xp = 0;
                        return entity;
                    }
            );

            instance.name = player.getName();
            instance.player = player;
            repository.edit(instance);
        } catch (RunicException e) {
            e.printStackTrace();
        }
        return players.put(player.getUniqueId().toString(), instance);
    }

    public Stream<Skill> getTriggerSkills(SkillType type) {
        return skills.parallelStream().filter(x -> x.getSkill().trigger(this, type)).map(ORPGSkill::getSkill);
    }

    public double getTotalXp() {
        return IntStream.range(1, this.level).mapToDouble(ORPGPlayer::getXpAtLevel).sum() + this.xp;
    }

    public double getCurrentXp() {
        return this.xp;
    }

    public void save() {
        players.remove(uuid);
    }

    public Player getPlayer() {
        return player;
    }
}
