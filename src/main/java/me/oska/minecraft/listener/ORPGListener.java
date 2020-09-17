package me.oska.minecraft.listener;

import io.lumine.xikage.mythicmobs.MythicMobs;
import me.oska.plugins.player.ORPGPlayer;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.skill.SkillType;
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

import java.util.HashSet;
import java.util.Set;

public class ORPGListener implements Listener {

    public static Logger log = new Logger("DamageAPI");
    private static Set<EntityDamageEvent.DamageCause> listenTo;
    private static MythicMobs mm = MythicMobs.inst();

    static {
        listenTo = new HashSet<>();
        listenTo.add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        listenTo.add(EntityDamageEvent.DamageCause.ENTITY_EXPLOSION);
        listenTo.add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onDamage(EntityDamageByEntityEvent event)
    {
        if (!listenTo.contains(event.getCause())){
            return;
        }

        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (attacker instanceof Player) {
            ORPGPlayer orpg_attacker = ORPGPlayer.getByPlayer((Player) attacker);
            if (victim instanceof Player) {
                // ORPGPlayerDamagePlayerEvent
                ORPGPlayer orpg_vicitm = ORPGPlayer.getByPlayer((Player) victim);
                orpg_attacker.getTriggerSkills(SkillType.PLAYER_DAMAGE_PLAYER)
                        .forEach((x) -> x.playerDamagePlayer(event, orpg_attacker, orpg_vicitm));
            } else {
                // ORPGPlayerDamageEntityEvent
                orpg_attacker.getTriggerSkills(SkillType.PLAYER_DAMAGE_ENTITY)
                        .forEach((x) -> x.playerDamageEntity(event, orpg_attacker, (LivingEntity) victim));
            }
        } else if (victim instanceof Player)  {
            // ORPGEntityDamagePlayerEvent
            ORPGPlayer orpg_vicitm = ORPGPlayer.getByPlayer((Player) victim);
            orpg_vicitm.getTriggerSkills(SkillType.ENTITY_DAMAGE_PLAYER)
                    .forEach((x) -> x.entityDamagePlayer(event, (LivingEntity) attacker, orpg_vicitm));
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onProjectileHit(ProjectileHitEvent event) {
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        Block block = event.getHitBlock();
        Entity victim = event.getHitEntity();
        if (shooter instanceof Player) {
            ORPGPlayer orpg_shooter = ORPGPlayer.getByPlayer((Player) shooter);
            if (block != null) {
                // ORPGPlayerShootBlockEvent
                orpg_shooter.getTriggerSkills(SkillType.PLAYER_SHOOT_BLOCK)
                        .forEach(x -> x.playerShootBlock(event, orpg_shooter, block));
            } else if (victim != null) {
                if (victim instanceof Player) {
                    // ORPGPlayerShootPlayerEvent
                    ORPGPlayer orpg_victim = ORPGPlayer.getByPlayer((Player) victim);
                    orpg_shooter.getTriggerSkills(SkillType.PLAYER_SHOOT_PLAYER)
                            .forEach(x -> x.playerShootPlayer(event, orpg_shooter, orpg_victim));
                } else if (victim instanceof LivingEntity) {
                    // ORPGPlayerShootEntityEvent
                    orpg_shooter.getTriggerSkills(SkillType.PLAYER_SHOOT_ENTITY)
                            .forEach(x -> x.playerShootEntity(event, orpg_shooter, (LivingEntity) victim));
                }
            }
        } else if (shooter instanceof LivingEntity && victim instanceof Player) {
            // ORPGEntityShootPlayerEvent
            ORPGPlayer orpg_victim = ORPGPlayer.getByPlayer((Player) victim);
            orpg_victim.getTriggerSkills(SkillType.ENTITY_SHOOT_PLAYER)
                    .forEach(x -> x.entityShootPlayer(event, (LivingEntity) shooter, orpg_victim));
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onBowShoot(EntityShootBowEvent event)
    {
        Entity shooter = event.getEntity();
        if (shooter instanceof Player)
        {
            // ORPGPlayerShootEvent
            ORPGPlayer orpg_attacker = ORPGPlayer.getByPlayer((Player)shooter);
            orpg_attacker.getTriggerSkills(SkillType.PLAYER_SHOOT)
                    .forEach(x -> x.playerShoot(event, orpg_attacker));

        }
    }
}
