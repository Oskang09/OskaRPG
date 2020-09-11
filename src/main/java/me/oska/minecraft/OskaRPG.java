package me.oska.minecraft;

import com.google.gson.Gson;
import me.oska.minecraft.listener.ORPGListener;
import me.oska.minecraft.listener.PlayerListener;
import me.oska.minecraft.listener.TestListener;
import me.oska.plugins.ORPGSkill;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class OskaRPG extends JavaPlugin {

    private static OskaRPG instance;
    private static Gson gson;

    public OskaRPG() {
        instance = this;
        gson = new Gson();
        // Workaround for Hibernate
        Thread.currentThread().setContextClassLoader(getClass().getClassLoader());
    }

    public static File getLoggerFolder() {
        File file = new File(getInstance().getDataFolder(), "log");
        if (!file.exists()) {
            file.mkdirs();
        }
        return file;
    }

    public static File getSkillFolder() {
        File file = new File(getInstance().getDataFolder(), "skill");
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

    public static Gson getGson() {
        return gson;
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
