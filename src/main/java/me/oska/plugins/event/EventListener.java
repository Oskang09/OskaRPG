package me.oska.plugins.event;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;
import java.util.Map;
import java.util.TreeMap;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class EventListener<T extends Event> implements Listener, EventExecutor {

    private Map<Predicate<T>, Consumer<T>> predicateHandler;

    public EventListener() {
        this.predicateHandler = new HashMap<>();
    }

    protected void add(Predicate<T> predicate, Consumer<T> consumer) {
        predicateHandler.put(predicate, consumer);
    }

    protected boolean remove(Predicate<T> predicate) {
        predicateHandler.remove(predicate);
        return predicateHandler.size() == 0;
    }

    @Override
    public void execute(@NotNull Listener listener, @NotNull Event event) {
        T genericEvent = (T)event;

        predicateHandler.entrySet().parallelStream().forEach(
            (entry) -> {
                if (entry.getKey().test(genericEvent)) {
                    entry.getValue().accept(genericEvent);
                }
            }
        );
    }
}
