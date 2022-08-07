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
	public Integer changeBalance(String playername, Integer amount)
	{
		return changeBalance(Bukkit.getOfflinePlayer(EPS.getUUID(playername)), amount);
	}

	@Override
	public Integer changeBalance(Player player, Integer amount)
	{
		EconomyResponse e = amount > 0 ? economy.depositPlayer(player, amount) : economy.withdrawPlayer(player, -amount);
		return (int)e.balance;
	}
	
	public Integer changeBalance(OfflinePlayer player, Integer amount)
	{
		EconomyResponse e = amount > 0 ? economy.depositPlayer(player, amount) : economy.withdrawPlayer(player, -amount);
		return (int)e.balance;
	}

	@Override
	public Integer setBalance(String playername, Integer value)
	{
		return setBalance(Bukkit.getOfflinePlayer(EPS.getUUID(playername)), value);
	}
	
	@Override
	public Integer setBalance(Player player, Integer value)
	{
		EconomyResponse e = value-economy.getBalance(player) > 0 ? economy.depositPlayer(player, value-economy.getBalance(player)) : economy.withdrawPlayer(player, economy.getBalance(player)-value);
		return (int)e.balance;
	}
	
	public Integer setBalance(OfflinePlayer player, Integer value)
	{
		EconomyResponse e = value-economy.getBalance(player) > 0 ? economy.depositPlayer(player, value-economy.getBalance(player)) : economy.withdrawPlayer(player, economy.getBalance(player)-value);
		return (int)e.balance;
	}

	@Override
	public Integer getBalance(Player player)
	{
		return (int)economy.getBalance(player);
	}

	@Override
	public Integer getBalance(String playername)
	{
		return (int)economy.getBalance(Bukkit.getOfflinePlayer(EPS.getUUID(playername)));
	}
}
