package me.oska.minecraft.listener;

import com.destroystokyo.paper.event.player.PlayerPostRespawnEvent;
import io.lumine.xikage.mythicmobs.MythicMobs;
import lombok.Getter;
import lombok.Setter;
import me.oska.plugins.mobs.ORPGMob;
import me.oska.plugins.player.ORPGPlayer;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.skill.SkillType;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Projectile;
import org.bukkit.event.Event;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.*;
import org.bukkit.projectiles.ProjectileSource;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Stream;

public class ORPGListener implements Listener {

    public class ORPGEvent<T extends Event> {

        @Getter
        private T event;

        @Getter
        @Setter
        private int damage;

        public ORPGEvent(T event) {
            this.event = event;
            this.damage = 0;
        }
    }

    public static Logger log = new Logger("ORPGListener");
    private static Set<EntityDamageEvent.DamageCause> customized;
    private static MythicMobs mm = MythicMobs.inst();

    static {
        customized = new HashSet<>();
        customized.add(EntityDamageEvent.DamageCause.ENTITY_ATTACK);
        customized.add(EntityDamageEvent.DamageCause.ENTITY_SWEEP_ATTACK);
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onDamage(EntityDamageByEntityEvent event)
    {
        // handled by ProjectileHitEvent & damage handled by BaseEntity.damageBy()
        if (event.getCause() == EntityDamageEvent.DamageCause.PROJECTILE){
            event.setDamage(0);
            return;
        }

        if (!customized.contains(event.getCause())){
            Entity victim = event.getEntity();
            if (victim instanceof Player) {
                ORPGPlayer.getByPlayer((Player) victim).damageBy(null, (int)event.getDamage());
            } else {
                ORPGMob.getByEntity(victim).damageBy(null, (int)event.getDamage());
            }
            return;
        }

        event.setDamage(0);
        ORPGEvent<EntityDamageByEntityEvent> custom = new ORPGEvent<>(event);
        Entity attacker = event.getDamager();
        Entity victim = event.getEntity();
        if (attacker instanceof Player) {
            ORPGPlayer orpg_attacker = ORPGPlayer.getByPlayer((Player) attacker);
            custom.setDamage(orpg_attacker.getDamage());

            if (victim instanceof Player) {
                // ORPGPlayerDamagePlayerEvents
                ORPGPlayer orpg_vicitm = ORPGPlayer.getByPlayer((Player) victim);
                Stream.concat(
                    orpg_attacker.getTriggerSkills(SkillType.PLAYER_DAMAGE_PLAYER),
                    orpg_vicitm.getTriggerSkills(SkillType.PLAYER_DAMAGE_PLAYER)
                ).forEach((x) -> x.playerDamagePlayer(custom, orpg_attacker, orpg_vicitm));
                orpg_vicitm.damageBy(orpg_attacker.getEntity(), custom.getDamage());
            } else {
                // ORPGPlayerDamageEntityEvent
                ORPGMob orpg_victim = ORPGMob.getByEntity(victim);
                Stream.concat(
                    orpg_attacker.getTriggerSkills(SkillType.PLAYER_DAMAGE_ENTITY),
                    orpg_victim.getTriggerSkills(SkillType.PLAYER_DAMAGE_ENTITY)
                ).forEach((x) -> x.playerDamageEntity(custom, orpg_attacker, orpg_victim));
                orpg_victim.damageBy(orpg_attacker.getEntity(), custom.getDamage());
            }

        } else if (victim instanceof Player)  {
            // ORPGEntityDamagePlayerEvent
            ORPGMob orpg_attacker = ORPGMob.getByEntity(attacker);
            custom.setDamage(orpg_attacker.getDamage());

            ORPGPlayer orpg_vicitm = ORPGPlayer.getByPlayer((Player) victim);
            Stream.concat(
                orpg_attacker.getTriggerSkills(SkillType.ENTITY_DAMAGE_PLAYER),
                orpg_vicitm.getTriggerSkills(SkillType.ENTITY_DAMAGE_PLAYER)
            ).forEach((x) -> x.entityDamagePlayer(custom, orpg_attacker, orpg_vicitm));
            orpg_vicitm.damageBy(orpg_attacker.getEntity(), custom.getDamage());
        }
    }

    @EventHandler( priority = EventPriority.MONITOR )
    public void onProjectileHit(ProjectileHitEvent event) {
        ORPGEvent<ProjectileHitEvent> custom = new ORPGEvent<>(event);
        Projectile projectile = event.getEntity();
        ProjectileSource shooter = projectile.getShooter();
        Block block = event.getHitBlock();
        Entity victim = event.getHitEntity();
        if (shooter instanceof Player) {
            ORPGPlayer orpg_shooter = ORPGPlayer.getByPlayer((Player) shooter);
            custom.setDamage(orpg_shooter.getDamage());

            if (block != null) {
                // ORPGPlayerShootBlockEvent
                orpg_shooter.getTriggerSkills(SkillType.PLAYER_SHOOT_BLOCK)
                        .forEach(x -> x.playerShootBlock(custom, orpg_shooter, block));
            } else if (victim instanceof Player) {
                // ORPGPlayerShootPlayerEvent
                ORPGPlayer orpg_victim = ORPGPlayer.getByPlayer((Player) victim);
                Stream.concat(
                        orpg_shooter.getTriggerSkills(SkillType.PLAYER_SHOOT_PLAYER),
                        orpg_victim.getTriggerSkills(SkillType.PLAYER_SHOOT_PLAYER)
                ).forEach(x -> x.playerShootPlayer(custom, orpg_shooter, orpg_victim));
                orpg_victim.damageBy(orpg_shooter.getEntity(), custom.getDamage());
            } else {
                // ORPGPlayerShootEntityEvent
                ORPGMob orpg_victim = ORPGMob.getByEntity(victim);
                Stream.concat(
                        orpg_shooter.getTriggerSkills(SkillType.PLAYER_SHOOT_ENTITY),
                        orpg_victim.getTriggerSkills(SkillType.PLAYER_SHOOT_ENTITY)
                ).forEach(x -> x.playerShootEntity(custom, orpg_shooter, orpg_victim));
            }

        } else if (shooter instanceof LivingEntity && victim instanceof Player) {
            // ORPGEntityShootPlayerEvent
            ORPGMob orpg_shooter = ORPGMob.getByEntity((Entity) shooter);
            custom.setDamage(orpg_shooter.getDamage());

            ORPGPlayer orpg_victim = ORPGPlayer.getByPlayer((Player) victim);
            Stream.concat(
                orpg_shooter.getTriggerSkills(SkillType.ENTITY_SHOOT_PLAYER),
                orpg_victim.getTriggerSkills(SkillType.ENTITY_SHOOT_PLAYER)
            ).forEach(x -> x.entityShootPlayer(custom, orpg_shooter, orpg_victim));
            orpg_victim.damageBy(orpg_shooter.getEntity(), custom.getDamage());
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onBowShoot(EntityShootBowEvent event)
    {
        ORPGEvent<EntityShootBowEvent> custom = new ORPGEvent<>(event);
        Entity shooter = event.getEntity();
        if (shooter instanceof Player)
        {
            // ORPGPlayerShootEvent
            ORPGPlayer orpg_attacker = ORPGPlayer.getByPlayer((Player)shooter);
            orpg_attacker.getTriggerSkills(SkillType.PLAYER_SHOOT)
                    .forEach(x -> x.playerShoot(custom, orpg_attacker));
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onHPRegain(EntityRegainHealthEvent event) {
        event.setCancelled(true);
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onRespawn(PlayerPostRespawnEvent event) {
        ORPGPlayer player = ORPGPlayer.getByPlayer(event.getPlayer());
        player.setHealth(player.getMaxHealth());
        player.updateHealthBar();
    }
}
