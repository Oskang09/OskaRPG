package me.oska.plugins.player;

import lombok.Getter;
import me.oska.plugins.LevelObject;
import me.oska.plugins.event.Events;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.skill.ORPGSkill;
import me.oska.plugins.skill.Skill;
import me.oska.plugins.skill.SkillType;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.time.Instant;
import java.util.*;
import java.util.stream.Stream;

public class ORPGPlayer extends LevelObject {
    private static Logger log;
    private static AbstractRepository<PlayerData> repository;
    private static Map<String, ORPGPlayer> players;

    public static void register(JavaPlugin plugin) {
        log = new Logger("ORPGPlayer");
        repository = new AbstractRepository<>(PlayerData.class);
        players = new HashMap<>();

        Events.listen(PlayerJoinEvent.class, (event) -> true, (event) -> {
            Player player = event.getPlayer();
            ORPGPlayer.onJoin(player);
        });

        Events.listen(PlayerQuitEvent.class, (event) -> true, (event) -> {
           ORPGPlayer.getByPlayer(event.getPlayer()).onQuit();
        });
    }

    public static ORPGPlayer getByOfflinePlayer(OfflinePlayer player) {
        try {
            ORPGPlayer orpgPlayer = new ORPGPlayer();
            orpgPlayer.playerData = repository.find(player.getUniqueId().toString());
            return orpgPlayer;
        } catch (RunicException e) {
            return null;
        }
    }

    public static ORPGPlayer getByPlayer(Player player) {
        return players.getOrDefault(player.getUniqueId().toString(), null);
    }

    @Getter
    private Player player;

    private List<ORPGSkill> skills;

    @Getter
    private Stats strength;

    @Getter
    private Stats dexterity;

    @Getter
    private Stats accuracy;

    @Getter
    private Stats range;

    @Getter
    private Stats damage;

    @Getter
    private Stats health;

    @Getter
    private PlayerData playerData;

    protected ORPGPlayer() {}

    public static ORPGPlayer onJoin(Player player) {
        PlayerData instance = null;
        try {
            instance = repository.findOrCreate(
                    player.getUniqueId().toString(),
                    () -> { 
                        PlayerData entity = new PlayerData();
                        entity.uuid = player.getUniqueId().toString();
                        entity.level = 1;
                        entity.experience = 0;
                        entity.balance = 0;
                        entity.permissions = new HashSet<>();
                        entity.createdAt = Instant.now();
                        entity.updatedAt = Instant.now();
                        entity.lastOnline = Instant.now();
                        return entity;
                    }
            );
            instance.isOnline = true;
            repository.edit(instance);
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to join & update", player.getUniqueId());
            player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
        }

        ORPGPlayer orpgPlayer = new ORPGPlayer();
        orpgPlayer.player = player;
        orpgPlayer.playerData = instance;
        return players.put(instance.uuid, orpgPlayer);
    }

    public void onQuit() {
        players.remove(player.getUniqueId().toString());
        playerData.isOnline = false;
        playerData.lastOnline = Instant.now();
    }

    public void save() throws RunicException {
        playerData.updatedAt = Instant.now();
        repository.edit(playerData);
    }

    public Stream<Skill> getTriggerSkills(SkillType type) {
        return skills.parallelStream().filter(x -> x.getSkill().trigger(this, type)).map(ORPGSkill::getSkill);
    }

    public boolean hasPermission(String permission) {
        return playerData.permissions.contains(permission);
    }

    public String withdrawBalance(double amount) {
        playerData.balance -= (int)amount;
        try {
            save();
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to withdraw balance %d", playerData.uuid, amount);
            player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
            return message;
        }
        return null;
    }

    public String depositBalance(double amount) {
        playerData.balance += (int)amount;
        try {
            save();
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to deposit balance %d", playerData.uuid, amount);
            player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
            return message;
        }
        return null;
    }

    public void addPermission(String permission) {
        if (playerData.permissions.add(permission)) {
            try {
                save();
            } catch (RunicException e) {
                String message = String.format("Player %s has failed to add new permission %d", playerData.uuid, permission);
                player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
            }
        }
    }

    public void removePermission(String permission) {
        if (playerData.permissions.remove(permission)) {
            try {
                save();
            } catch (RunicException e) {
                String message = String.format("Player %s has failed to remove new permission %d", playerData.uuid, permission);
                player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
            }
        }
    }

    public int getBalance() {
        return playerData.balance;
    }

    @Override
    public int getLevel() {
        return playerData.level;
    }

    @Override
    public int getExperience() {
        return playerData.experience;
    }
}
