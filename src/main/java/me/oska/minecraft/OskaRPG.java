package me.oska.minecraft;

import com.google.gson.Gson;
import me.oska.minecraft.listener.ORPGListener;
import me.oska.minecraft.listener.TestListener;
import me.oska.plugins.item.BasicItem;
import me.oska.plugins.item.CustomItem;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.mobs.ORPGMob;
import me.oska.plugins.player.ORPGPlayer;
import me.oska.plugins.server.ORPGServer;
import me.oska.plugins.skill.ORPGSkill;
import me.oska.plugins.event.Events;
import me.oska.plugins.inventory.InventoryUI;
import me.oska.plugins.hibernate.AbstractRepository;
import me.oska.plugins.vault.ORPGChat;
import me.oska.plugins.vault.ORPGEconomy;
import me.oska.plugins.vault.ORPGPermission;
import me.oska.plugins.wehouse.WeHouse;
import me.oska.util.BannerUtil;
import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.ServicePriority;
import org.bukkit.plugin.ServicesManager;
import org.bukkit.plugin.java.JavaPlugin;
import java.io.File;

public final class OskaRPG extends JavaPlugin {

    private static OskaRPG instance;
    private static Gson gson;

    public OskaRPG() {
        instance = this;
        gson = new Gson();
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

        BannerUtil.initialize();
        AbstractRepository.register(this);
        Events.register(this);
        Logger.register(this);

        ServicesManager service = getServer().getServicesManager();
        ORPGPermission orpgPermission = new ORPGPermission();
        service.register(Permission.class, orpgPermission, this, ServicePriority.Highest);
        service.register(Economy.class, new ORPGEconomy(), this, ServicePriority.Highest);
        service.register(Chat.class, new ORPGChat(orpgPermission), this, ServicePriority.Highest);

        InventoryUI.register(this);
        WeHouse.register(this);
        ORPGServer.register("default",this);
        ORPGSkill.register(this);
        ORPGMob.register(this);
        ORPGPlayer.register(this);
        BasicItem.register(this);
        CustomItem.register(this);

        PluginManager manager = getServer().getPluginManager();
        manager.registerEvents(new TestListener(), this);
        manager.registerEvents(new ORPGListener(), this);
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }
}
