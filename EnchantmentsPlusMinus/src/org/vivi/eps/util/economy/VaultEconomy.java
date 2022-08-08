package org.vivi.eps.util.economy;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.dependencies.VaultHook;

import net.milkbowl.vault.economy.EconomyResponse;

public class VaultEconomy implements Economy {

	private net.milkbowl.vault.economy.Economy economy = VaultHook.getEconomy();
	
	@Override
	public double changeBalance(String playername, double amount)
	{
		return changeBalance(Bukkit.getOfflinePlayer(EPS.getUUID(playername)), amount);
	}

	@Override
	public double changeBalance(Player player, double amount)
	{
		EconomyResponse e = amount > 0 ? economy.depositPlayer(player, amount) : economy.withdrawPlayer(player, -amount);
		return e.balance;
	}
	
	public double changeBalance(OfflinePlayer player, double amount)
	{
		EconomyResponse e = amount > 0 ? economy.depositPlayer(player, amount) : economy.withdrawPlayer(player, -amount);
		return e.balance;
	}

	@Override
	public double setBalance(String playername, double value)
	{
		return setBalance(Bukkit.getOfflinePlayer(EPS.getUUID(playername)), value);
	}
	
	@Override
	public double setBalance(Player player, double value)
	{
		EconomyResponse e = value-economy.getBalance(player) > 0 ? economy.depositPlayer(player, value-economy.getBalance(player)) : economy.withdrawPlayer(player, economy.getBalance(player)-value);
		return e.balance;
	}
	
	public double setBalance(OfflinePlayer player, double value)
	{
		EconomyResponse e = value-economy.getBalance(player) > 0 ? economy.depositPlayer(player, value-economy.getBalance(player)) : economy.withdrawPlayer(player, economy.getBalance(player)-value);
		return e.balance;
	}

	@Override
	public double getBalance(Player player)
	{
		return economy.getBalance(player);
	}

	@Override
	public double getBalance(String playername)
	{
		return economy.getBalance(Bukkit.getOfflinePlayer(EPS.getUUID(playername)));
	}
}
