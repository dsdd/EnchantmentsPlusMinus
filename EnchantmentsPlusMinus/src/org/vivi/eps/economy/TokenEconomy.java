package org.vivi.eps.economy;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.vivi.eps.util.DataUtil;

public class TokenEconomy implements Economy {
	
	@Override
	public Integer changeBalance(String playername, Integer amount)
	{
		File file = DataUtil.getUserDataFile(playername);
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		int redefine = yaml.getInt("tokens") + amount;
		yaml.set("tokens", redefine);
		DataUtil.saveConfig(yaml, file);
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
		File file = DataUtil.getUserDataFile(playername);
		FileConfiguration yaml = YamlConfiguration.loadConfiguration(file);
		yaml.set("tokens", value);
		DataUtil.saveConfig(yaml, file);
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
		File file = DataUtil.getUserDataFile(playername);
		return YamlConfiguration.loadConfiguration(file).getInt("tokens");
	}
}
