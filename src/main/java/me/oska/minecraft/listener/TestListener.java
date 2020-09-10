package me.oska.minecraft.listener;

import me.oska.plugins.wehouse.WeHouseAPI;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;

public class TestListener implements Listener {
    @EventHandler
    public void on(PlayerInteractEvent event) {
        if (event.getAction() == Action.LEFT_CLICK_BLOCK) {
            Player player = event.getPlayer();
            Block block = event.getClickedBlock();
            if (event.getClickedBlock().getType() == Material.WHITE_WOOL) {
                WeHouseAPI.cut(player, block.getX(), block.getY(), block.getZ(), 5, 4);
            }

            if (event.getClickedBlock().getType() == Material.BLACK_WOOL) {
                WeHouseAPI.paste(player, block.getX(), block.getY(), block.getZ(), 5);
            }
        }
    }
}
