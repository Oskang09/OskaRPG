package me.oska.plugins.entity;

import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.hibernate.exception.RunicException;
import me.oska.plugins.orpg.ServerStatus;

import javax.persistence.*;

@Entity(name = "orpg_server")
@Table(name = "orpg_server")
public class ORPGServer extends BaseEntity {
    private static AbstractRepository<ORPGServer> repository = new AbstractRepository(ORPGServer.class);

    @Id
    private String id;

    @Enumerated(EnumType.STRING)
    private ServerStatus status;

    protected ORPGServer() {}

    public static ORPGServer loadServer(String id) {
        ORPGServer instance = null;
        try {
            instance = repository.find(id);
        } catch (RunicException e) {
            e.printStackTrace();
        }
        return instance;
    }
}
