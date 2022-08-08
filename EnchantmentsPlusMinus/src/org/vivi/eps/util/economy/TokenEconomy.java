package org.vivi.eps.util.economy;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;

public class TokenEconomy implements Economy {
	
	@Override
	public double changeBalance(String playername, double amount)
	{
		File file = EPS.getUserDataFile(playername);
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		double redefine = yaml.getDouble("tokens") + amount;
		yaml.set("tokens", redefine);
		try {
			yaml.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return redefine;
	}

	@Override
	public double changeBalance(Player player, double amount)
	{
		return changeBalance(player.getName(), amount);
	}

	@Override
	public double setBalance(String playername, double value)
	{
		File file = EPS.getUserDataFile(playername);
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		yaml.set("tokens", value);
		try {
			yaml.save(file);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return value;
	}
	
	@Override
	public double setBalance(Player player, double value)
	{
		return setBalance(player.getName(), value);
	}

	@Override
	public double getBalance(Player player)
	{
		return getBalance(player.getName());
	}

	@Override
	public double getBalance(String playername)
	{
		File file = EPS.getUserDataFile(playername);
		return YamlConfiguration.loadConfiguration(file).getDouble("tokens");
	}
}
