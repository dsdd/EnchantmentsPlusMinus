package org.whyisthisnecessary.eps.api;

import java.io.File;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.legacy.NameUtil;
import org.whyisthisnecessary.eps.util.DataUtil;

public class ConfigUtil {
	
	private static ScriptEngineManager mgr = new ScriptEngineManager();
    private static ScriptEngine engine = mgr.getEngineByName("JavaScript");
    
	public static FileConfiguration getConfig()
	{
		return Main.Config;
	}
	
	public static FileConfiguration getEnchantConfig(Enchantment enchant)
	{
		return YamlConfiguration.loadConfiguration(getEnchantFile(enchant));
	}
	/**
	 * Returns the file which correlates to the enchant's configuration.
	 * 
	 * @param enchant The correlating enchant
	 * @return Returns the file which correlates to the enchant's configuration.
	 */
	public static File getEnchantFile(Enchantment enchant)
	{
		File file = new File(Main.EnchantsFolder, NameUtil.getName(enchant)+".yml");
		return file;
	}
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * Automatically fills %lvl% so you don't have to mess with script engines.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param enchlvl The enchant's level
	 * @param path The path you want to search for.
	 * @return Returns the double value of the specified path of the specified enchant's file.
	 */
	public static Double getAutofilledDouble(Enchantment enchant, Integer enchlvl, String path)
    {
    	String value = getEnchantConfig(enchant).getString(path);
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
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * Automatically fills %lvl% so you don't have to mess with script engines.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param enchlvl The enchant's level
	 * @param path The path you want to search for.
	 * @return Returns the Integer value of the specified path of the specified enchant's file.
	 */
	public static Integer getAutofilledInt(Enchantment enchant, Integer enchlvl, String path)
    {
    	String value = getEnchantConfig(enchant).getString(path);
    	value = value.replaceAll("%lvl%", enchlvl.toString());
    	Object num = 0;
	    try {
		num = engine.eval(value); } 
	    catch (ScriptException e1) {e1.printStackTrace();}
	    return (Integer) num;
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the String value of the specified path of the specified enchant's file.
	 */
	public static String getString(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getString(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the int value of the specified path of the specified enchant's file.
	 */
	public static int getInt(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getInt(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the double value of the specified path of the specified enchant's file.
	 */
	public static double getDouble(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getDouble(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the Boolean value of the specified path of the specified enchant's file.
	 */
	public static Boolean getBoolean(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).getBoolean(path);
    }
	
	/**
	 * Returns the value of the specified path of the specified enchant's file.
	 * Can be casted, but give an unchecked warning.
	 * 
	 * @param enchant The enchant correlating to the file you want to search
	 * @param path The path you want to search for.
	 * @return Returns the Object value of the specified path of the specified enchant's file.
	 */
	public static Object get(Enchantment enchant, String path)
    {
	    return getEnchantConfig(enchant).get(path);
    }
	
	/**
	 * Returns the value of the specified path of the main config's misc section.
	 * e.g. If you want to find "misc.yourpathhere.something" you would write "yourpathhere.something"
	 * 
	 * @param path The path you want to search for.
	 * @return Returns the value of the specified path of the main config's misc section.
	 */
	public static Object getMiscKey(String path)
    {
        return Main.Config.get("misc."+path);
    }

	public static void setDefaultCostType(Enchantment enchant, String type)
    {
    	setConfigValue(enchant, "cost.type", type);
    }
    
    public static void setDefaultCostStartValue(Enchantment enchant, Integer value)
    {
    	setConfigValue(enchant, "cost.startvalue", value);
    }
    
    public static void setDefaultCostValue(Enchantment enchant, Integer value)
    {
    	setConfigValue(enchant, "cost.value", value);
    }
    
    public static void setDefaultCostPrice(Enchantment enchant, Integer enchlvl, Integer price)
    {
    	setConfigValue(enchant, "cost."+enchlvl.toString(), price);
    }
    
    public static void setDefaultCostMulti(Enchantment enchant, Double multi)
    {
    	setConfigValue(enchant, "cost.multi", multi);
    }
    
    public static void setDefaultMaxLevel(Enchantment enchant, Integer level)
    {
    	setConfigValue(enchant, "maxlevel", level);
    }
    
    public static void setDefaultScrapValue(Enchantment enchant, Integer value)
    {
    	setConfigValue(enchant, "scrapvalue", value);
    }
    
    public static void setDefaultUpgradeIcon(Enchantment enchant, Material icon)
    {
    	setConfigValue(enchant, "upgradeicon", NameUtil.getMaterialName(icon));
    }
    
    public static void setDefaultUpgradeIcon(Enchantment enchant, String material)
    {
    	setConfigValue(enchant, "upgradeicon", material);
    }
    
    public static void setDefaultUpgradeDesc(Enchantment enchant, String desc)
    {
    	setConfigValue(enchant, "upgradedesc", desc);
    }
    
    public static void setDefault(Enchantment enchant, String path, Object value)
    {
    	setConfigValue(enchant, path, value);
    }
    
    public static void setDefaultMisc(String path, Object value)
    {
    	setMiscValue("misc."+path, value);
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
    
    private static void setMiscValue(String path, Object replace)
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
    
    /**
     * Creates a new file in the enchants folder with the specified enchant name.
     * 
     * @param enchant The enchant (Will be converted to its name)
     * @return The new file
     */
    public static File newEnchantFile(Enchantment enchant)
    {
    	File file = getEnchantFile(enchant);
    	if (!file.exists())
    		Main.createNewFile(file);
    	return file;
    }
    
    /**
     * Creates a new file in the enchants folder with the specified name.
     * 
     * @param enchant The enchant
     * @param name The file name
     * @return The new file
     */
    public static File newEnchantFile(Enchantment enchant, String name)
    {
    	File file = new File(Main.EnchantsFolder, name);
    	if (!file.exists())
    		Main.createNewFile(file);
    	return file;
    }
    
    private static void setConfigValue(Enchantment enchant, String path, Object replace)
    {
    	FileConfiguration config = getEnchantConfig(enchant);
    	File file = getEnchantFile(enchant);
		if (config.get(path) == null)
		{
			config.set(path, replace);
			if (file.exists())
			{
				DataUtil.saveConfig(config, file);
			}
		}
    }
}
