package org.whyisthisnecessary.eps.api;

import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.util.DataUtil;

public class ConfigUtil {
	
	private static ScriptEngineManager mgr = new ScriptEngineManager();
    private static ScriptEngine engine = mgr.getEngineByName("JavaScript");
    
	public static FileConfiguration getConfig()
	{
		return Main.Config;
	}
	
	public static Double getEnchantKeyDouble(Enchantment enchant, Integer enchlvl, String enchkey)
    {
    	String value = Main.Config.getString("enchants."+enchant.getKey().getKey()+"."+enchkey);
    	value = value.replaceAll("%lvl%", enchlvl.toString());
    	Object num = 0;
	    try {
		num = engine.eval(value); } 
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
	
	public static Integer getEnchantKeyInt(Enchantment enchant, Integer enchlvl, String enchkey)
    {
    	String value = Main.Config.getString("enchants."+enchant.getKey().getKey()+"."+enchkey);
    	value = value.replaceAll("%lvl%", enchlvl.toString());
    	Object num = 0;
	    try {
		num = engine.eval(value); } 
	    catch (ScriptException e1) {e1.printStackTrace();}
	    return (Integer) num;
    }
	
	public static String getEnchantKeyString(Enchantment enchant, String enchkey)
    {
    	String value = Main.Config.getString("enchants."+enchant.getKey().getKey()+"."+enchkey);
	    return value;
    }
	
	public static Boolean getEnchantKeyBoolean(Enchantment enchant, String enchkey)
    {
    	Boolean value = Main.Config.getBoolean("enchants."+enchant.getKey().getKey()+"."+enchkey);
	    return value;
    }
	
	public static Object get(Enchantment enchant, String enchkey)
    {
    	Object value = Main.Config.getString("enchants."+enchant.getKey().getKey()+"."+enchkey);
	    return value;
    }
	
	public static Object getMiscKey(String misckey)
    {
    	Object value = Main.Config.get("misc."+misckey);
        return value;
    }

	public static void setDefaultCostType(Enchantment enchant, String type)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.type", type);
    }
    
    public static void setDefaultCostStartValue(Enchantment enchant, Integer value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.startvalue", value);
    }
    
    public static void setDefaultCostValue(Enchantment enchant, Integer value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.value", value);
    }
    
    public static void setDefaultCostPrice(Enchantment enchant, Integer enchlvl, Integer price)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost."+enchlvl.toString(), price);
    }
    
    public static void setDefaultCostMulti(Enchantment enchant, Double multi)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".cost.multi", multi);
    }
    
    public static void setDefaultMaxLevel(Enchantment enchant, Integer level)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".maxlevel", level);
    }
    
    public static void setDefaultScrapValue(Enchantment enchant, Integer value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".scrapvalue", value);
    }
    
    public static void setDefaultUpgradeIcon(Enchantment enchant, Material icon)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".upgradeicon", icon.getKey().getKey());
    }
    
    public static void setDefaultUpgradeIcon(Enchantment enchant, String material)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".upgradeicon", material);
    }
    
    public static void setDefaultUpgradeDesc(Enchantment enchant, String desc)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+".upgradedesc", desc);
    }
    
    public static void setDefaultEnchantKey(Enchantment enchant, String enchantkey, Object value)
    {
    	setConfigValue("enchants."+enchant.getKey().getKey()+"."+enchantkey, value);
    }
    
    public static void setDefaultMiscKey(String misckey, Object value)
    {
    	setConfigValue("misc."+misckey, value);
    }
    
    public static void autoFillEnchantConfig(Enchantment enchant, String desc, Integer cost)
    {
    	setDefaultMaxLevel(enchant, 10);
    	setDefaultScrapValue(enchant, cost/2);
    	setDefaultUpgradeIcon(enchant, Material.BOOK);
    	setDefaultUpgradeDesc(enchant, desc);
    	setDefaultCostType(enchant, "linear");
    	setDefaultCostStartValue(enchant, cost);
    	setDefaultCostValue(enchant, cost);
    }
    
    private static void setConfigValue(String path, Object replace)
    {
		if (Main.Config.get(path) == null)
		{
			Main.Config.set(path, replace);
			if (Main.ConfigFile.exists())
			{
				DataUtil.saveConfig(Main.Config, Main.ConfigFile);
			}
		}
    }
}
