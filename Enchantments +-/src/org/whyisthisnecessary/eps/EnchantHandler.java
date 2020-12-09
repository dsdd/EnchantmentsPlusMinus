package org.whyisthisnecessary.eps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class EnchantHandler {
	
	private FileConfiguration config;
	private JavaPlugin plugin;
	private ScriptEngineManager mgr = new ScriptEngineManager();
    private ScriptEngine engine = mgr.getEngineByName("JavaScript");
	
	public EnchantHandler(JavaPlugin plugin)
	{
        this.plugin = plugin;
        File file = new File(plugin.getDataFolder().getParentFile(), "EnchantmentsPlusMinus");
        if (file.exists())
        {
        	File file1 = new File(file, "config.yml");
            config = YamlConfiguration.loadConfiguration(file1);
        }
        else
        {
        	Bukkit.broadcastMessage(ChatColor.RED + "Critical error: Could not find config file for EPS!");
        	Bukkit.getPluginManager().disablePlugin(plugin);
        }
	}
	
	public FileConfiguration getConfig()
	{
		return config;
	}
	
	public void setDefaultConfigMessage(String msg, String text)
	{
		if (config.get("packs."+plugin.getName().toLowerCase()+".messages."+msg) == null)
		{
			config.set("packs."+plugin.getName().toLowerCase()+".messages."+msg, text);
			File configfile = new File(plugin.getDataFolder(), "config.yml");
			if (configfile.exists())
			{
				try {
					config.save(configfile);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
	
	public void setConfigMessage(String msg, String text)
	{
		config.set("packs."+plugin.getName().toLowerCase()+".messages."+msg, text);
		File configfile = new File(plugin.getDataFolder(), "config.yml");
		if (configfile.exists())
		{
            try {
				config.save(configfile);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
    public String getConfigMessage(String msg)
    {
    	return ChatColor.translateAlternateColorCodes('&',config.getString("prefix")) + ChatColor.translateAlternateColorCodes('&', config.getString("packs."+plugin.getName().toLowerCase()+".messages."+msg));
    }
    
    public Integer getValueInt(Player p, Enchantment enchant)
    {
    	Integer enchlvl = p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant);
    	Object num = 0;
	    Integer chance1 = 0;
	    try {
		num = engine.eval(String.format(config.getString("enchants."+enchant.getKey().getKey()+".value"),enchlvl.toString())); } 
	    catch (ScriptException e1) {e1.printStackTrace();}
	    if (num instanceof Double)
	    {
	    Double num1 = (Double)num;
	    chance1 = num1.intValue();
	    }
	    else
	    {
	    	chance1 = (Integer)num;
	    }
	    return chance1;
    }
    
    public Integer getEnchantChanceInt(Player p, Enchantment enchant)
    {
    	Integer enchlvl = p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant);
    	Object num = 0;
	    Integer chance1 = 0;
	    try {
		num = engine.eval(String.format(config.getString("enchants."+enchant.getKey().getKey()+".chance"),enchlvl.toString())); } 
	    catch (ScriptException e1) {e1.printStackTrace();}
	    if (num instanceof Double)
	    {
	    Double num1 = (Double)num;
	    chance1 = num1.intValue();
	    }
	    else
	    {
	    	chance1 = (Integer)num;
	    }
	    return chance1;
    }
    
    public Double getEnchantChance(Player p, Enchantment enchant)
    {
    	Integer enchlvl = p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant);
    	Object num = 0;
	    try {
		num = engine.eval(String.format(config.getString("enchants."+enchant.getKey().getKey()+".chance"),enchlvl.toString())); } 
	    catch (ScriptException e1) {e1.printStackTrace();}
	    if (num instanceof Double)
	    {
	        return (Double) num;
	    }
	    else
	    {
	    	return Double.valueOf((Integer)num);
	    }
    }
    
    public List<Material> getFortuneDrops()
    {
    	List<Material> fortuneapply = new ArrayList<Material>(Arrays.asList());
    	List<String> list;
    	list = config.getStringList("misc.applyfortuneon");
    	for (int i=0;i<list.size();i++)
    	fortuneapply.add(Material.getMaterial(list.get(i)));
    	return fortuneapply;
    }
    
    public void ChangeTokens(Player p, Integer amount)
    {
    	TokenManager.ChangeTokens(p.getName(), amount);
    }
    
    public void SetTokens(Player p, Integer amount)
    {
    	TokenManager.SetTokens(p.getName(), amount);
    }
    
    public Integer GetTokens(Player p)
    {
    	return TokenManager.GetTokens(p.getName());
    }
}
