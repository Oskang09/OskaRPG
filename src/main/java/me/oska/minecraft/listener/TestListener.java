package me.oska.minecraft.listener;

import me.oska.plugins.ORPGItem;
import me.oska.plugins.ORPGPlayer;
import me.oska.plugins.logger.Logger;
import me.oska.plugins.orpg.Skill;
import me.oska.plugins.orpg.SkillType;
import me.oska.plugins.wehouse.WeHouseAPI;
import me.oska.plugins.wehouse.WeHouseCutOptions;
import me.oska.plugins.wehouse.WeHousePasteOptions;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestListener implements Listener {

    public static Logger log = new Logger("TestListener");

    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (event.getClickedBlock().getType() == Material.WHITE_WOOL) {
                WeHouseCutOptions options = new WeHouseCutOptions(player, block, 5, 4);
                log.withTracker("wehouse cut",() -> WeHouseAPI.cut(options));
            }

            if (event.getClickedBlock().getType() == Material.BLACK_WOOL) {
                WeHousePasteOptions options = new WeHousePasteOptions(player, block, 5);
                log.withTracker("wehouse paste",() -> WeHouseAPI.paste(options));
            }
        }
    }
}
