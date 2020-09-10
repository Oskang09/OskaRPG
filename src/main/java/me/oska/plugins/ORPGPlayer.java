package me.oska.plugins;

import me.oska.plugins.openjpa.AbstractRepository;
import me.oska.plugins.openjpa.exception.RunicException;
import org.bukkit.entity.Player;

import javax.persistence.*;
import java.util.HashMap;
import java.util.Map;

@Entity
@Table
public class ORPGPlayer {
    private static Map<String, ORPGPlayer> players = new HashMap();
    private static AbstractRepository<ORPGPlayer> repository = new AbstractRepository(ORPGPlayer.class);

    @Transient
    private Player player;

    @Id
    private String uuid;
    private String name;

    public static ORPGPlayer load(Player player) {
        ORPGPlayer instance = null;
        try {
            instance = repository.findOrCreate(
                    player.getUniqueId().toString(),
                    () -> { 
                        ORPGPlayer entity = new ORPGPlayer();
                        entity.uuid = player.getUniqueId().toString();
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

    public void save() {
        players.remove(uuid);
    }

    public static ORPGPlayer getByPlayer(Player player) {
        String uuid = player.getUniqueId().toString();
        return players.getOrDefault(uuid, null);
    }

    public static ORPGPlayer getByUUID(String uuid) {
        return players.getOrDefault(uuid, null);
    }

    public Player getPlayer() {
        return player;
    }
}
