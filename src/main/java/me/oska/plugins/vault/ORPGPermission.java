package me.oska.plugins.vault;

import me.oska.plugins.ORPGPlayer;
import net.milkbowl.vault.permission.Permission;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class ORPGPermission extends Permission {

    @Override
    public String getName() {
        return "ORPGPermission";
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public boolean hasSuperPermsCompat() {
        return true;
    }

    @Override
    public boolean playerHas(String world, String playerName, String permission) {
        Player player = Bukkit.getPlayer(playerName);
        ORPGPlayer orpgPlayer = ORPGPlayer.getByPlayer(player);
        return orpgPlayer.hasPermission(permission);
    }

    @Override
    public boolean playerAdd(String world, String playerName, String permission) {
        Player player = Bukkit.getPlayer(playerName);
        ORPGPlayer orpgPlayer = ORPGPlayer.getByPlayer(player);
        orpgPlayer.addPermission(permission);
        return playerAddTransient(player, permission);
    }

    @Override
    public boolean playerRemove(String world, String playerName, String permission) {
        Player player = Bukkit.getPlayer(playerName);
        ORPGPlayer orpgPlayer = ORPGPlayer.getByPlayer(player);
        orpgPlayer.removePermission(permission);
        return playerRemoveTransient(player, permission);
    }

    @Override
    public boolean groupHas(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupAdd(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean groupRemove(String world, String group, String permission) {
        return false;
    }

    @Override
    public boolean playerInGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerAddGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public boolean playerRemoveGroup(String world, String player, String group) {
        return false;
    }

    @Override
    public String[] getPlayerGroups(String world, String player) {
        return new String[0];
    }

    @Override
    public String getPrimaryGroup(String world, String player) {
        return null;
    }

    @Override
    public String[] getGroups() {
        return new String[0];
    }

    @Override
    public boolean hasGroupSupport() {
        return false;
    }
}
