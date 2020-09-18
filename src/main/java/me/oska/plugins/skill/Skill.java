package me.oska.plugins.skill;

import me.oska.plugins.mobs.ORPGMob;
import me.oska.plugins.player.ORPGPlayer;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public abstract class Skill {
    public abstract boolean useTick();
    public abstract void onTick();
    public abstract void onEquip(ORPGPlayer player);
    public abstract boolean trigger(ORPGPlayer player, SkillType type);
    public abstract boolean trigger(ORPGMob mob, SkillType type);
    public void playerDamagePlayer(EntityDamageByEntityEvent event, ORPGPlayer attacker, ORPGPlayer victim) {}
    public void playerDamageEntity(EntityDamageByEntityEvent event, ORPGPlayer attacker, LivingEntity victim) {}
    public void entityDamagePlayer(EntityDamageByEntityEvent event, LivingEntity attacker, ORPGPlayer victim) {}
    public void playerShoot(EntityShootBowEvent event, ORPGPlayer shooter) {}
    public void playerShootBlock(ProjectileHitEvent event, ORPGPlayer shooter, Block block) {}
    public void playerShootEntity(ProjectileHitEvent event, ORPGPlayer attacker, LivingEntity victim) {}
    public void playerShootPlayer(ProjectileHitEvent event, ORPGPlayer attacker, ORPGPlayer victim) {}
    public void entityShootPlayer(ProjectileHitEvent event, LivingEntity attacker, ORPGPlayer victim) {}
}