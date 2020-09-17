package me.oska.minecraft.listener;

import io.lumine.xikage.mythicmobs.MythicMobs;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobDeathEvent;
import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import io.lumine.xikage.mythicmobs.api.exceptions.InvalidMobTypeException;
import me.oska.plugins.entity.ORPGMob;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

public class EntityListener implements Listener {

    private MythicMobs mm = MythicMobs.inst();

    @EventHandler
    public void onSpawn(MythicMobSpawnEvent event) throws InvalidMobTypeException {
//        Entity entity = mm.getAPIHelper().spawnMythicMob("SkeletalMinion", event.getLocation());
//        ORPGMob mob = ORPGMob.createMob("SkeletalMinion", entity, entity.getLocation());
        event.getMob().getAuraRegistry();
    }

    @EventHandler
    public void onDeath(MythicMobDeathEvent event) {
        event.setDrops(null); // override with our own drops.
        ORPGMob.getByEntity(event.getEntity()).dispose();
    }
}
