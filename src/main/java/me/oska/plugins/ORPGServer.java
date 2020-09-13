package me.oska.plugins;

import me.oska.plugins.openjpa.AbstractRepository;
import me.oska.plugins.openjpa.exception.RunicException;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity
@Table
public class ORPGServer {
    private static AbstractRepository<ORPGServer> repository = new AbstractRepository(ORPGServer.class);

    @Id
    private String id;

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
