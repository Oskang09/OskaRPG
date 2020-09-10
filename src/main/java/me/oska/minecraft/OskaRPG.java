package me.oska.minecraft;

import me.oska.minecraft.listener.ORPGListener;
import me.oska.minecraft.listener.PlayerListener;
import me.oska.minecraft.listener.TestListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class OskaRPG extends JavaPlugin {

    private static OskaRPG instance;

    public OskaRPG() {
        instance = this;
        // Workaround for Hibernate
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

    public static File getLoggerFolder() {
        File file = new File(getInstance().getDataFolder(), "logs");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getWeHouseFolder() {
        File file = new File(getInstance().getDataFolder(), "wehouse");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static OskaRPG getInstance(){
        return instance;
    }

    @Override
    public void onEnable() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdirs();
        }

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new TestListener(), this);
        manager.registerEvents(new PlayerListener(), this);
        manager.registerEvents(new ORPGListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
