package me.oska.plugins.vault;

import me.oska.plugins.player.ORPGPlayer;
import me.oska.plugins.player.PlayerData;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.util.List;

public class ORPGEconomy implements Economy {
    @Override
    public boolean isEnabled() {
        return true;
    }

    @Override
    public String getName() {
        return "ORPGEconomy";
    }

    @Override
    public int fractionalDigits() {
        return 0;
    }

    @Override
    public String format(double amount) {
        return "$ " + amount;
    }

    @Override
    public boolean hasAccount(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        return ORPGPlayer.getByPlayer(player) != null;
    }

    @Override
    public boolean hasAccount(OfflinePlayer player) {
        return ORPGPlayer.getByOfflinePlayer(player) != null;
    }

    @Override
    public boolean hasAccount(String playerName, String worldName) {
        return hasAccount(playerName);
    }

    @Override
    public boolean hasAccount(OfflinePlayer player, String worldName) {
        return hasAccount(player);
    }

    @Override
    public double getBalance(String playerName) {
        Player player = Bukkit.getPlayer(playerName);
        ORPGPlayer orpgPlayer = ORPGPlayer.getByPlayer(player);
        return orpgPlayer.getBalance();
    }

    @Override
    public double getBalance(OfflinePlayer player) {
        ORPGPlayer orpgPlayer = ORPGPlayer.getByOfflinePlayer(player);
        return orpgPlayer.getBalance();
    }

    @Override
    public double getBalance(String playerName, String world) {
        return getBalance(playerName);
    }

    @Override
    public double getBalance(OfflinePlayer player, String world) {
        return getBalance(player);
    }

    @Override
    public boolean has(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        ORPGPlayer orpgPlayer = ORPGPlayer.getByPlayer(player);
        return orpgPlayer.getBalance() >= amount;
    }

    @Override
    public boolean has(OfflinePlayer player, double amount) {
        ORPGPlayer orpgPlayer = ORPGPlayer.getByOfflinePlayer(player);
        return orpgPlayer.getBalance() >= amount;
    }

    @Override
    public boolean has(String playerName, String worldName, double amount) {
        return has(playerName, amount);
    }

    @Override
    public boolean has(OfflinePlayer player, String worldName, double amount) {
        return has(player, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        ORPGPlayer orpgPlayer = ORPGPlayer.getByPlayer(player);
        String message = orpgPlayer.withdrawBalance(amount);

        EconomyResponse response = new EconomyResponse(
            amount,
            orpgPlayer.getBalance(),
            EconomyResponse.ResponseType.SUCCESS,
                message
        );
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, double amount) {
        ORPGPlayer orpgPlayer = ORPGPlayer.getByOfflinePlayer(player);
        String message = orpgPlayer.withdrawBalance(amount);

        EconomyResponse response = new EconomyResponse(
                amount,
                orpgPlayer.getBalance(),
                EconomyResponse.ResponseType.SUCCESS,
                message
        );
        return response;
    }

    @Override
    public EconomyResponse withdrawPlayer(String playerName, String worldName, double amount) {
        return withdrawPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse withdrawPlayer(OfflinePlayer player, String worldName, double amount) {
        return withdrawPlayer(player, amount);
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, double amount) {
        Player player = Bukkit.getPlayer(playerName);
        ORPGPlayer orpgPlayer = ORPGPlayer.getByPlayer(player);
        String message = orpgPlayer.depositBalance(amount);

        EconomyResponse response = new EconomyResponse(
            amount,
            orpgPlayer.getBalance(),
            EconomyResponse.ResponseType.SUCCESS,
            message
        );
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, double amount) {
        ORPGPlayer orpgPlayer = ORPGPlayer.getByOfflinePlayer(player);
        String message = orpgPlayer.depositBalance(amount);

        EconomyResponse response = new EconomyResponse(
                amount,
                orpgPlayer.getBalance(),
                EconomyResponse.ResponseType.SUCCESS,
                message
        );
        return response;
    }

    @Override
    public EconomyResponse depositPlayer(String playerName, String worldName, double amount) {
        return depositPlayer(playerName, amount);
    }

    @Override
    public EconomyResponse depositPlayer(OfflinePlayer player, String worldName, double amount) {
        return depositPlayer(player, amount);
    }

    @Override
    public boolean createPlayerAccount(String playerName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(String playerName, String worldName) {
        return true;
    }

    @Override
    public boolean createPlayerAccount(OfflinePlayer player, String worldName) {
        return true;
    }

    @Override
    public boolean hasBankSupport() {
        return false;
    }

    @Override
    public String currencyNamePlural() {
        return null;
    }

    @Override
    public String currencyNameSingular() {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, String player) {
        return null;
    }

    @Override
    public EconomyResponse createBank(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse deleteBank(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankBalance(String name) {
        return null;
    }

    @Override
    public EconomyResponse bankHas(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankWithdraw(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse bankDeposit(String name, double amount) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankOwner(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, String playerName) {
        return null;
    }

    @Override
    public EconomyResponse isBankMember(String name, OfflinePlayer player) {
        return null;
    }

    @Override
    public List<String> getBanks() {
        return null;
    }
}
