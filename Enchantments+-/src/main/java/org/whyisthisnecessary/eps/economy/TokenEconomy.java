package org.whyisthisnecessary.eps.economy;

import java.io.File;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.util.DataUtil;

import com.osiris.dyml.DYModule;
import com.osiris.dyml.DreamYaml;

public class TokenEconomy implements Economy {
	
	@Override
	public Integer changeBalance(String playername, Integer amount)
	{
		File file = DataUtil.getUserDataFile(playername);
		DreamYaml yaml = new DreamYaml(file).load();
		DYModule module = yaml.add("tokens");
		int redefine = module.asInt() + amount;
		module.setValue(redefine);
		yaml.save();
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
		DreamYaml yaml = new DreamYaml(file).load();
		DYModule module = yaml.add("tokens");
		module.setValue(value);
		yaml.save();
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
		DreamYaml yaml = new DreamYaml(file).load();
		DYModule module = yaml.add("tokens");
		return module.asInt();
	}
}
