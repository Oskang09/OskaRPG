package me.oska.plugins.skill;

import me.oska.minecraft.listener.ORPGListener;
import me.oska.plugins.mobs.ORPGMob;
import me.oska.plugins.player.ORPGPlayer;
import org.bukkit.block.Block;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityShootBowEvent;
import org.bukkit.event.entity.ProjectileHitEvent;

public abstract class Skill {
    public abstract boolean useTick();
    public abstract void onTick();
    public abstract void onEquip(ORPGPlayer player);
    public abstract boolean trigger(ORPGPlayer player, SkillType type);
    public abstract boolean trigger(ORPGMob mob, SkillType type);
    public void playerDamagePlayer(ORPGListener.ORPGEvent<EntityDamageByEntityEvent> event, ORPGPlayer attacker, ORPGPlayer victim) {}
    public void playerDamageEntity(ORPGListener.ORPGEvent<EntityDamageByEntityEvent> event, ORPGPlayer attacker, ORPGMob victim) {}
    public void entityDamagePlayer(ORPGListener.ORPGEvent<EntityDamageByEntityEvent> event, ORPGMob attacker, ORPGPlayer victim) {}
    public void playerShoot(ORPGListener.ORPGEvent<EntityShootBowEvent> event, ORPGPlayer shooter) {}
    public void playerShootBlock(ORPGListener.ORPGEvent<ProjectileHitEvent> event, ORPGPlayer shooter, Block block) {}
    public void playerShootEntity(ORPGListener.ORPGEvent<ProjectileHitEvent> event, ORPGPlayer attacker, ORPGMob victim) {}
    public void playerShootPlayer(ORPGListener.ORPGEvent<ProjectileHitEvent> event, ORPGPlayer attacker, ORPGPlayer victim) {}
    public void entityShootPlayer(ORPGListener.ORPGEvent<ProjectileHitEvent> event, ORPGMob attacker, ORPGPlayer victim) {}
}