package me.oska.minecraft.listener;

import me.oska.plugins.ORPGPlayer;
import me.oska.plugins.logger.Logger;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ORPGListener implements Listener {

    public static Logger log = new Logger("DamageAPI");

    @EventHandler( priority = EventPriority.MONITOR )
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (event.getCause() != EntityDamageEvent.DamageCause.CONTACT) {
            return;
        }

        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (attacker instanceof Player) {
            if (victim instanceof Player) {
                // ORPGPlayerDamagePlayerEvent
                ORPGPlayer orpg_attacker = ORPGPlayer.getByPlayer((Player) attacker);
                ORPGPlayer orpg_vicitm = ORPGPlayer.getByPlayer((Player) victim);

            } else {
                // ORPGPlayerDamageEntityEvent
            }
        } else if (victim instanceof Player)  {
            // ORPGEntityDamagePlayerEvent
        }
    }

    @EventHandler
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        Block block = event.getHitBlock();
        Entity victim = event.getHitEntity();
        if (shooter instanceof Player) {
            if (block != null) {
                // ORPGPlayerShootBlockEvent
            } else if (victim != null) {
                if (victim instanceof Player) {
                    // ORPGPlayerShootPlayerEvent
                } else if (victim instanceof LivingEntity) {
                    // ORPGPlayerShootEntityEvent
                }
            }
        } else if (shooter instanceof LivingEntity && victim instanceof Player) {
            // ORPGEntityShootPlayerEvent
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onBowShoot(EntityShootBowEvent event)
    {
        Entity shooter = event.getEntity();
        if (shooter instanceof Player)
        {
            // ORPGPlayerShootEvent
        }
    }
}
