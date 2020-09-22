package me.oska.plugins.logger;

import lombok.Setter;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity(name = "issues")
@Table(name = "issues")
public class Issue {

    @Setter
    @Id
    private String trackId;

    @Setter
    private String title;

    @Setter
    @Column(columnDefinition = "text")
    private String message;

    @Setter
    @Column(columnDefinition = "text")
    private String stack;

    @Type(type = "jsonb")
    @Column(columnDefinition = "jsonb")
    @Setter
    private List<String> players;

    @Setter
    private String timestamp;

    @Setter
    private String serverId;

    public Issue() {
        this.players = new ArrayList<>();
    }
}
