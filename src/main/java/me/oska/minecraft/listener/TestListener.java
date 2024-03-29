package me.oska.minecraft.listener;

import io.lumine.xikage.mythicmobs.api.bukkit.events.MythicMobSpawnEvent;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.mobs.ORPGMob;
import me.oska.plugins.wehouse.WeHouse;
import me.oska.plugins.wehouse.WeHouseCutOptions;
import me.oska.plugins.wehouse.WeHousePasteOptions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.*;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.entity.EntitySpawnEvent;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestListener implements Listener {

    public static Logger log = new Logger("TestListener");

    @EventHandler
    public void spawn(MythicMobSpawnEvent event) {
        LivingEntity entity = event.getLivingEntity();
        ORPGMob.createMob(event.getMobType().getInternalName(), entity, entity.getLocation());
    }

    @EventHandler
    public void on(PlayerInteractEvent event) {

        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (event.getClickedBlock().getType() == Material.WHITE_WOOL) {
                WeHouseCutOptions options = new WeHouseCutOptions(player, block, 5, 4);
                log.withTracker("wehouse cut",() -> WeHouse.cut(options));
            }

            if (event.getClickedBlock().getType() == Material.BLACK_WOOL) {
                WeHousePasteOptions options = new WeHousePasteOptions(player, block, 5);
                log.withTracker("wehouse paste",() -> WeHouse.paste(options));
            }
        }
    }
}
