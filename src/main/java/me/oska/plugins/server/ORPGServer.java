package me.oska.plugins.server;

import com.google.gson.Gson;
import com.volmit.react.React;
import com.volmit.react.api.ISampler;
import lombok.Getter;
import me.oska.plugins.hibernate.BaseEntity;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.converter.InstantConverter;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.logger.Logger;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Map;

@Entity(name = "orpg_server")
@Table(name = "orpg_server")
public class ORPGServer extends BaseEntity {
    private static Logger log;
    private static ORPGServer server;
    private static Gson gson;
    private static AbstractRepository<ORPGServer> repository;
    protected ORPGServer() {}

    public static String getServerID() {
        return System.getenv("OSKARPG_SERVER_ID");
    }

    public static ORPGServer getServer() {
        return server;
    }

    private static Runnable getSamplerUpdater(){
        return () -> {
            Map<String, ISampler> maps = React.instance.sampleController.getSamplers();
            server.sampler = gson.toJson(maps);
            server.lastUpdated = Instant.now();
            repository.editAsync(server, null, null);
        };
    }
    public static void register(String serverId, JavaPlugin plugin) {
        gson = new Gson();
        log = new Logger("ORPGServer");
        repository = new AbstractRepository(ORPGServer.class);

        try {
            server = repository.find(System.getenv("OSKARPG_SERVER_ID"));
            Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, getSamplerUpdater(), 0, 20 * 5);
            AbstractRepository.listen("minecraft_server", 5000, (notification) -> {
                AsyncServerUpdateEvent event = gson.fromJson(notification.getParameter(), AsyncServerUpdateEvent.class);
                plugin.getServer().getPluginManager().callEvent(event);
            });
        } catch (RunicException | SQLException e) {
            e.printStackTrace();
        }
    }

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private Status status;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    private String sampler;

    @Convert(converter = InstantConverter.class)
    @Column(name = "updatedAt")
    private Instant lastUpdated;

    @Getter
    @Column(columnDefinition = "text")
    private String header;

    @Getter
    @Column(columnDefinition = "text")
    private String footer;
}
