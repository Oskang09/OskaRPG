package me.oska.plugins.player;

import lombok.Getter;
import me.oska.plugins.BaseItem;
import me.oska.plugins.BasicAttribute;
import me.oska.plugins.ConvertibleAttribute;
import me.oska.plugins.LevelObject;
import me.oska.plugins.event.Events;
import me.oska.plugins.hibernate.PostgresEvent;
import me.oska.plugins.item.ItemType;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.skill.ORPGSkill;
import me.oska.plugins.skill.Skill;
import me.oska.plugins.skill.SkillType;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.java.JavaPlugin;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
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

    protected Map<ItemType, BaseItem> equipments;
    private List<ORPGSkill> skills;
    protected ConvertibleAttribute strength;
    protected ConvertibleAttribute dexterity;
    protected ConvertibleAttribute accuracy;
    protected BasicAttribute range;
    protected BasicAttribute damage;
    protected BasicAttribute health;

    @Getter
    private PlayerData playerData;

    private Runnable listener;

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
        orpgPlayer.listener = Events.listen(PostgresEvent.class, x -> x.getAction().equals("UPDATE_PLAYER:" + player.getUniqueId().toString()),
            (x) -> {
                try {
                    orpgPlayer.reload();
                } catch (RunicException e) {
                    String message = String.format("Player %s has failed to reload", player.getUniqueId());
                    player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
                }
            }
        );
        orpgPlayer.playerData = instance;
        return players.put(instance.uuid, orpgPlayer);
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

    public Stream<Skill> getTriggerSkills(SkillType type) {
        return skills.parallelStream().filter(x -> x.getSkill().trigger(this, type)).map(ORPGSkill::getSkill);
    }

    public ItemStack getItem(ItemType type) {
        BaseItem item = equipments.getOrDefault(type, null);
        if (item != null) {
            return item.getItem();
        }
        return new ItemStack(Material.AIR);
    }

    public void unequipItem(ItemType type) {
        if (type == ItemType.MATERIAL) {
            return;
        }
        equipments.remove(type);
    }

    public void equipItem(ItemType type, BaseItem item) {
        if (type == ItemType.MATERIAL) {
            return;
        }
        equipments.put(type, item);
    }

    public void reload() throws RunicException {
        playerData = repository.find(playerData.uuid);
    }

    public void save() throws RunicException {
        playerData.updatedAt = Instant.now();
        repository.edit(playerData);
    }

    public void onQuit() {
        players.remove(player.getUniqueId().toString());
        if (listener != null) {
            listener.run();
        }

        playerData.isOnline = false;
        playerData.lastOnline = Instant.now();
        try {
            save();
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to save when logout", playerData.uuid);
            log.toFile(message, e);
        }
    }

    public void equipmentCalculation() {
        AtomicInteger strength = new AtomicInteger(0);
        AtomicInteger accuracy = new AtomicInteger(0);
        AtomicInteger dexterity = new AtomicInteger(0);
        AtomicInteger damage = new AtomicInteger(0);
        AtomicInteger range = new AtomicInteger(0);
        AtomicInteger health = new AtomicInteger(0);

        equipments.entrySet().parallelStream().forEach(x -> {
            BaseItem base = x.getValue();
            strength.getAndAdd(base.getStrength());
            accuracy.getAndAdd(base.getAccuracy());
            dexterity.getAndAdd(base.getDexterity());
            damage.getAndAdd(base.getDamage());
            range.getAndAdd(base.getRange());
            health.getAndAdd(base.getHealth());
            skills.addAll(base.getSkills());
            playerData.equipments.put(base.getType().toString(), base.getKey());
        });

        this.strength = new ConvertibleAttribute(strength.get());
        this.dexterity = new ConvertibleAttribute(dexterity.get());
        this.accuracy = new ConvertibleAttribute(accuracy.get());
        this.damage = new BasicAttribute(damage.get());
        this.range = new BasicAttribute(range.get());
        this.health = new BasicAttribute(health.get());
        this.damage.setConversion(this.strength.convertDamage());
        this.range.setConversion(this.dexterity.convertRange());
        this.health.setConversion(this.accuracy.convertHealth());
        this.skills.parallelStream().
            filter(x -> x.getSkill().trigger(this, SkillType.PASSIVE)).
            forEach((x) ->x.getSkill().onEquip(this));

        try {
            save();
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to calculate equipments stats", playerData.uuid);
            player.sendMessage("Error occurs please contact admin with tracker id - " + log.toFile(message, e));
        }
    }

    public boolean hasPermission(String permission) {
        return playerData.permissions.contains(permission);
    }

    public String withdrawBalance(double amount) {
        try {
            reload();
            playerData.balance -= (int)amount;
            save();
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to withdraw balance %d", playerData.uuid, amount);
            return "Error occurs please contact admin with tracker id - " + log.toFile(message, e);
        }
        return null;
    }

    public String depositBalance(double amount) {
        try {
            reload();
            playerData.balance += (int)amount;
            save();
        } catch (RunicException e) {
            String message = String.format("Player %s has failed to deposit balance %d", playerData.uuid, amount);
            String properError = "Error occurs please contact admin with tracker id - " + log.toFile(message, e);
            if (player != null) {
                player.sendMessage(properError);
            }
            return properError;
        }
        return null;
    }

    public void addPermission(String permission) {
        if (!playerData.permissions.contains(permission)) {
            try {
                reload();
                if (playerData.permissions.add(permission)) {
                    save();
                }
            } catch (RunicException e) {
                String message = String.format("Player %s has failed to add new permission %d", playerData.uuid, permission);
                String properError = "Error occurs please contact admin with tracker id - " + log.toFile(message, e);
                if (player != null) {
                    player.sendMessage(properError);
                }
            }
        }
    }

    public void removePermission(String permission) {
        if (playerData.permissions.contains(permission)) {
            try {
                reload();
                if (playerData.permissions.remove(permission)) {
                    save();
                }
            } catch (RunicException e) {
                String message = String.format("Player %s has failed to remove new permission %d", playerData.uuid, permission);
                String properError = "Error occurs please contact admin with tracker id - " + log.toFile(message, e);
                if (player != null) {
                    player.sendMessage(properError);
                }
            }
        }
    }
}