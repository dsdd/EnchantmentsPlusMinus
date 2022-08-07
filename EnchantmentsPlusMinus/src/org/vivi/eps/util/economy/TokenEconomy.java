package org.vivi.eps.util.economy;

import java.io.File;
import java.io.IOException;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;

public class TokenEconomy implements Economy {
	
	@Override
	public Integer changeBalance(String playername, Integer amount)
	{
		File file = EPS.getUserDataFile(playername);
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		int redefine = yaml.getInt("tokens") + amount;
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
	public Integer changeBalance(Player player, Integer amount)
	{
		return changeBalance(player.getName(), amount);
	}

	@Override
	public Integer setBalance(String playername, Integer value)
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
	public Integer setBalance(Player player, Integer value)
	{
		return setBalance(player.getName(), value);
	}

	@Override
	public Integer getBalance(Player player)
	{
		return getBalance(player.getName());
	}

	@Override
	public Integer getBalance(String playername)
	{
		File file = EPS.getUserDataFile(playername);
		return YamlConfiguration.loadConfiguration(file).getInt("tokens");
	}
}
