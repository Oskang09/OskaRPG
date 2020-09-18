package me.oska.plugins.hibernate;

import com.google.gson.Gson;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;
import org.jetbrains.annotations.NotNull;

public class PostgresEvent extends Event {
    private static Gson gson = new Gson();
    private static final HandlerList HANDLERS = new HandlerList();

    @NotNull
    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Getter
    private String action;
    
    private String data;

    public <T> T getData(Class<T> clazz) {
        return gson.fromJson(data, clazz);
    }
}
