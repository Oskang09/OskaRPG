package me.oska.plugins.server;

import me.oska.plugins.hibernate.BaseEntity;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.logger.Logger;
import org.bukkit.plugin.java.JavaPlugin;

import javax.persistence.*;

@Entity(name = "orpg_server")
@Table(name = "orpg_server")
public class ORPGServer extends BaseEntity {
    private static Logger log = new Logger("ORPGServer");
    private static ORPGServer server;
    private static AbstractRepository<ORPGServer> repository = new AbstractRepository(ORPGServer.class);
    protected ORPGServer() {}

    public static ORPGServer getServer() {
        return server;
    }

    public static void register(String serverId, JavaPlugin plugin) {
        try {
            server = repository.find(serverId);
        } catch (RunicException e) {
            e.printStackTrace();
        }
    }

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;
}
