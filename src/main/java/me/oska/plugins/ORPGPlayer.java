package me.oska.plugins;

import lombok.Getter;
import me.oska.minecraft.OskaRPG;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.openjpa.AbstractRepository;
import me.oska.plugins.openjpa.exception.RunicException;
import me.oska.plugins.orpg.*;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.permissions.PermissionAttachment;

import javax.persistence.*;
import java.util.*;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Entity
@Table
//@NamedNativeQueries({
//    @NamedNativeQuery(
//        name = "ORPGPlayer.getByName",
//        query = "SELECT * FROM orpgplayer WHERE name = :1",
//        resultClass = ORPGPlayer.class
//    )
//})
public class ORPGPlayer {
    private static final int BASE_XP = 700;
    private static final Logger log = new Logger("ORPG - Player");
    private static final Map<String, ORPGPlayer> players = new HashMap<>();
    private static final AbstractRepository<ORPGPlayer> repository = new AbstractRepository<>(ORPGPlayer.class);

    public static double getXpAtLevel(int level) {
        return BASE_XP * Math.sqrt(level) + ( BASE_XP * level);
    }

    public static ORPGPlayer getByOfflinePlayer(OfflinePlayer player) {
        try {
            return repository.find(player.getUniqueId().toString());
        } catch (RunicException e) {
            return null;
        }
    }

    public static ORPGPlayer getByPlayer(Player player) {
        return players.getOrDefault(player.getUniqueId().toString(), null);
    }

    @Transient
    @Getter
    private Player player;

    @Transient
    private List<ORPGSkill> skills;

    @Transient
    @Getter
    private Stats strength;

    @Transient
    @Getter
    private Stats dexterity;

    @Transient
    @Getter
    private Stats accuracy;

    @Transient
    @Getter
    private Stats range;

    @Transient
    @Getter
    private Stats damage;

    @Transient
    @Getter
    private Stats health;

    @Id
    private String uuid;

    @Getter
    private Integer level;

    @Getter
    private double xp;

    @ElementCollection
    private HashSet<String> permissions;

    @Getter
    @Enumerated(EnumType.STRING)
    private GraphicLevel graphic;

    @Getter
    @Enumerated(EnumType.STRING)
    private PlayerStatus status;

    @Getter
    private int balance;

    protected ORPGPlayer() {}

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
                        entity.balance = 0;
                        entity.permissions = new HashSet<>();
                        entity.skills = new ArrayList<>();
                        return entity;
                    }
            );

            instance.player = player;
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to create new account", player.getUniqueId());
            player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
        }
        return players.put(instance.uuid, instance);
    }

    public void save() {
        players.remove(uuid);
    }

    public Stream<Skill> getTriggerSkills(SkillType type) {
        return skills.parallelStream().filter(x -> x.getSkill().trigger(this, type)).map(ORPGSkill::getSkill);
    }

    public boolean hasPermission(String permission) {
        return permissions.contains(permission);
    }

    public String withdrawBalance(double amount) {
        this.balance -= (int)amount;
        try {
            repository.edit(this);
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to withdraw balance %d", this.uuid, amount);
            player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
            return message;
        }
        return null;
    }

    public String depositBalance(double amount) {
        this.balance += (int)amount;
        try {
            repository.edit(this);
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to deposit balance %d", this.uuid, amount);
            player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
            return message;
        }
        return null;
    }

    public void addPermission(String permission) {
        if (permissions.add(permission)) {
            try {
                repository.edit(this);
            } catch (RunicException e) {
                e.printStackTrace();
            }
        }
    }

    public void removePermission(String permission) {
        if (permissions.remove(permission)) {
            try {
                repository.edit(this);
            } catch (RunicException e) {
                e.printStackTrace();
            }
        }
    }

    public double getTotalXp() {
        return IntStream.range(1, this.level).mapToDouble(ORPGPlayer::getXpAtLevel).sum() + this.xp;
    }
}
