package me.oska.minecraft.listener;

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
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;
import org.bukkit.projectiles.ProjectileSource;

public class ORPGListener implements Listener {

    public static Logger log = new Logger("DamageAPI");

    @EventHandler( priority = EventPriority.MONITOR )
    public void onDamage(EntityDamageByEntityEvent event)
    {
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (attacker instanceof Player) {
            if (victim instanceof Player) {
                log.toConsole("ORPGPlayerDamagePlayerEvent", null);
            } else {
                log.toConsole("ORPGPlayerDamageEntityEvent", null);
            }
        } else if (victim instanceof Player)  {
            log.toConsole("ORPGEntityDamagePlayerEvent", null);
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
                log.toConsole("ORPGPlayerShootBlockEvent", null);
            } else if (victim != null) {
                if (victim instanceof Player) {
                    log.toConsole("ORPGPlayerShootPlayerEvent", null);
                } else if (victim instanceof LivingEntity) {
                    log.toConsole("ORPGPlayerShootEntityEvent", null);
                }
            }
        } else if (shooter instanceof LivingEntity && victim instanceof Player) {
            log.toConsole("ORPGEntityShootPlayerEvent", null);
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onBowShoot(EntityShootBowEvent event)
    {
        Entity shooter = event.getEntity();
        if (shooter instanceof Player)
        {
            log.toConsole("ORPGPlayerShootEvent", null);
        }
    }
}
