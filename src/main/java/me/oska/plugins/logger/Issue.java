package me.oska.plugins.logger;

import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity(name = "issues")
@Table(name = "issues")
public class Issue {
    @Id
    @Setter
    private String trackId;

    @Setter
    private String title;

    @Setter
    private String message;

    @Setter
    private String stack;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Setter
    private List<String> players;

    @Setter
    private String timestamp;

    public Issue(String trackId) {
        if (trackId == null) {
            this.trackId = UUID.randomUUID().toString();
        }
        this.players = new ArrayList<>();
    }
}
