package me.oska.plugins;

import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface BaseEntity {
    int MINECRAFT_MAX_HEALTH = 1024;

    int getHealth();
    void setHealth(int health);
    int getMaxHealth();
    int getDamage();
    LivingEntity getEntity();

    default void updateHealthBar() {
        LivingEntity victim = getEntity();
        double percent = (double)getHealth() / getMaxHealth();
        victim.setHealth(percent * MINECRAFT_MAX_HEALTH);
    }

    default void damageBy(LivingEntity attacker, int damage) {
        LivingEntity victim = getEntity();
        int current = getHealth();
        if (current - damage < 0) {
            setHealth(0);
            if (attacker instanceof Player) {
                victim.setKiller((Player) attacker);
            }
            victim.setHealth(0);
            victim.setLastDamage(damage);
            return;
        }

        setHealth(current - damage);
        victim.setLastDamage(damage);
        updateHealthBar();
    }
}
