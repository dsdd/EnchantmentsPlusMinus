package org.vivi.eps.util.economy;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.vivi.sekai.dependencies.VaultHook;

import net.milkbowl.vault.economy.EconomyResponse;

public class VaultEconomy implements Economy
{

	private net.milkbowl.vault.economy.Economy economy = VaultHook.getEconomy();
	private Map<UUID, OfflinePlayer> uuidCache = new HashMap<UUID, OfflinePlayer>();
	
	private OfflinePlayer getOfflinePlayer(UUID playerUUID)
	{
		if (uuidCache.containsKey(playerUUID))
			return uuidCache.get(playerUUID);
		OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerUUID);
		if (offlinePlayer.hasPlayedBefore())
		{
			uuidCache.put(playerUUID, offlinePlayer);
			return offlinePlayer;
		}
		return null;
	}

	@Override
	public double getBalance(UUID playerUUID)
	{
		Player player = Bukkit.getPlayer(playerUUID);
		if (player != null)
			return getBalance(player);
		OfflinePlayer offlinePlayer = getOfflinePlayer(playerUUID);
		return offlinePlayer.hasPlayedBefore() ? getBalance(offlinePlayer) : 0; // pff
	}

	@Override
	public double changeBalance(UUID playerUUID, double amount)
	{
		Player player = Bukkit.getPlayer(playerUUID);
		if (player != null)
			return changeBalance(player, amount);
		OfflinePlayer offlinePlayer = getOfflinePlayer(playerUUID);
		return offlinePlayer.hasPlayedBefore() ? changeBalance(offlinePlayer, amount) : 0; // pff
	}

	@Override
	public double setBalance(UUID playerUUID, double value)
	{
		Player player = Bukkit.getPlayer(playerUUID);
		if (player != null)
			return changeBalance(player, value);
		OfflinePlayer offlinePlayer = getOfflinePlayer(playerUUID);
		return offlinePlayer.hasPlayedBefore() ? setBalance(offlinePlayer, value) : 0; // pff
	}

	@Override
	public double changeBalance(Player player, double amount)
	{
		EconomyResponse e = amount > 0 ? economy.depositPlayer(player, amount)
				: economy.withdrawPlayer(player, -amount);
		return e.balance;
	}

	public double changeBalance(OfflinePlayer player, double amount)
	{
		return (amount > 0 ? economy.depositPlayer(player, amount)
				: economy.withdrawPlayer(player, -amount)).balance;
	}

	@Override
	public double setBalance(Player player, double value)
	{
		return (value - economy.getBalance(player) > 0
				? economy.depositPlayer(player, value - economy.getBalance(player))
				: economy.withdrawPlayer(player, economy.getBalance(player) - value)).balance;
	}

	public double setBalance(OfflinePlayer player, double value)
	{
		return (value - economy.getBalance(player) > 0
				? economy.depositPlayer(player, value - economy.getBalance(player))
				: economy.withdrawPlayer(player, economy.getBalance(player) - value)).balance;
	}

	@Override
	public double getBalance(Player player)
	{
		return economy.getBalance(player);
	}
	
	public double getBalance(OfflinePlayer player)
	{
		return economy.getBalance(player);
	}

}
