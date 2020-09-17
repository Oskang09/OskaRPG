package me.oska.plugins.event;

import org.bukkit.event.*;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class Events {

    private static JavaPlugin plugin;
    private static PluginManager manager;
    private static Map<Class<? extends Event>, EventListener> registeredEvents;

    private Events() {}

    private static <T extends Event> void registerEvent(Class<T> eventClass, Predicate<T> predicate, Consumer<T> consumer) {
       EventListener listener = registeredEvents.getOrDefault(eventClass, null);
       if (listener == null) {
           listener = new EventListener<T>();
           registeredEvents.put(eventClass, listener);
           manager.registerEvent(eventClass, listener, EventPriority.HIGHEST, listener, plugin);
       }
       listener.add(predicate, consumer);
    }

    private static <T extends Event> void unregisterEvent(Class<T> eventClass, Predicate<T> predicate) {
        try {
            EventListener listener =registeredEvents.getOrDefault(eventClass, null);
            if (listener != null && listener.remove(predicate)) {
                Method getHandlerListMethod = eventClass.getMethod("getHandlerList");
                HandlerList handlerList = (HandlerList)getHandlerListMethod.invoke(null);
                handlerList.unregister(listener);
                registeredEvents.remove(eventClass);
            }
        } catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    public static void register(JavaPlugin pl) {
        registeredEvents = new HashMap<>();
        plugin = pl;
        manager = pl.getServer().getPluginManager();
    }

    public static <T extends Event> Runnable listen(Class<T> eventClass, Predicate<T> predicate, Consumer<T> handler) {
        registerEvent(eventClass, predicate, handler);
        return () -> unregisterEvent(eventClass, predicate);
    }
}
