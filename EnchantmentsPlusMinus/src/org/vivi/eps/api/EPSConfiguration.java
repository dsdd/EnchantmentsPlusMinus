package org.vivi.eps.api;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

import org.apache.commons.lang.Validate;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.vivi.eps.EPS;
import org.vivi.eps.Main;
import org.vivi.eps.util.DataUtil;
import org.vivi.eps.util.Dictionary;
import org.vivi.eps.util.LangUtil;

/** An implementation of {@link Configuration}.
 * Meant for use for easy writing and reading of Enchantments+- configuration files.
 * 
 * @author TreuGames
 *
 */
public class EPSConfiguration extends YamlConfiguration {

	private File file = null;
	private Map<String, String> cachedStrings = new HashMap<String, String>();
	private static Dictionary dictionary = EPS.getDictionary();
	private Map<String, Map<Integer, Double>> cachedAutofills = new HashMap<String, Map<Integer, Double>>();
	
	// This map is used to improve efficiency of getting values from each enchant config
	protected static Map<Enchantment, EPSConfiguration> fgMap = new HashMap<Enchantment, EPSConfiguration>();
	
	/** Reloads all enchant configurations
     */
    public static void reloadConfigs()
    {
    	Enchantment[] enchants = Enchantment.values();
    	for (Enchantment enchant : enchants)
    	{
    		File enchantfile = new File(Main.EnchantsFolder, EPS.getDictionary().getName(enchant)+".yml");
    		if (enchantfile.exists())
    			fgMap.put(enchant, EPSConfiguration.loadConfiguration(enchantfile));
    	}
    }

	public static EPSConfiguration loadConfiguration(File file)
	{
		Validate.notNull(file, "File cannot be null");

        EPSConfiguration config = new EPSConfiguration();

        try {
            config.load(file);
        } catch (FileNotFoundException ex) {
        } catch (IOException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        } catch (InvalidConfigurationException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Cannot load " + file, ex);
        }
        config.file = file;

        return config;
	}
	
	/**
	 * Returns the value of the specified path.
	 * Automatically fills %lvl%.
	 * 
	 * @param enchlvl The enchant's level
	 * @param path The path you want to search for.
	 * @return Returns the double value of the specified path.
	 */
	public double getAutofilledDouble(Integer enchlvl, String path)
    {
    	String value = getCachedString(path);
    	value = value.replaceAll("%lvl%", enchlvl.toString());
    	Map<Integer, Double> map = cachedAutofills.get(path);
    	if (map == null)
    	{
    		Map<Integer, Double> newMap = new HashMap<Integer, Double>();
    		cachedAutofills.put(path, newMap);
    		map = newMap;
    	}
    	Double d = map.get(enchlvl);
    	double eval = eval(value);
    	if (d == null)
    	{
    		map.put(enchlvl, eval);
    		d = eval;
    	}
	    return d;
    }
	
	/**
	 * Returns the value of the specified path.
	 * Automatically fills %lvl%.
	 * 
	 * @param enchlvl The enchant's level
	 * @param path The path you want to search for.
	 * @return Returns the Integer value of the specified path.
	 */
	public int getAutofilledInt(Integer enchlvl, String path)
    {
    	return (int)getAutofilledDouble(enchlvl, path);
    }
	
	/** Sets the path to the specified value if it isn't already set.
     * Automatically saves.
     * 
     * @param path The path to be set to
     * @param value The value to set to the path
     */
    public void setDefault(String path, Object value)
    {
    	setConfigValue(path, value);
    }
	
    private void setConfigValue(String path, Object replace)
    {
		if (!isSet(path))
		{
			set(path, replace);
			DataUtil.saveConfig(this, file);
		}
    }
    
    /** Automatically fills the max level, scrap value, upgrade material, upgrade description,
     *  cost type, cost start value and value for easier value setting.
     *  
     *  Max level is set to 10, scrap value is set to half cost, upgrade icon is set to BOOK,
     *  upgrade description is set to the specified description (so people can understand what the
     *  enchant does), the cost type is set to linear and the start value and value is set to the 
     *  specified cost.
     * 
     * @param enchant The enchant to fill
     * @param desc The description to fill
     * @param cost The cost you want to set
     */
    public void autoFillEnchantConfig(String desc, Integer cost)
    {
    	setDefault("maxlevel", 10);
    	setDefault("scrapvalue", cost/2);
    	setDefault("upgradeicon", Material.BOOK.name());
    	setDefault("upgradedesc", desc);
    	setDefault("cost.type", "linear");
    	setDefault("cost.startvalue", cost);
    	setDefault("cost.value", cost);
    }
    
    /**
	 * Returns the file which correlates to this configuration.
	 * 
	 * @param enchant The correlating enchant
	 * @return Returns the file which correlates to the enchant's configuration.
	 */
	public File getEnchantFile()
	{
		return file;
	}
	
	/** Gets the configuration of the enchant
	 * Should not be used by plugins, use loadConfiguration() instead!
	 * 
	 * @param enchant The enchant
	 * @return The configuration of the enchant
	 */
	public static EPSConfiguration getConfiguration(Enchantment enchant)
	{
		EPSConfiguration config = fgMap.get(enchant);
		if (config != null)
			return config;
		else
		{
			String enchname = EPS.getDictionary().getName(enchant);
			String msg = LangUtil.getLangMessage("no-enchant-config-found");
			if (msg != "")
				Bukkit.getConsoleSender().sendMessage(msg.replaceAll("%enchant%", enchname));
			File file = new File(Main.EnchantsFolder, enchname+".yml");
			if (!file.exists())
			{
				EPSConfiguration config1 = EPSConfiguration.loadConfiguration(file);
				fgMap.put(enchant, config1);
				return config1;
			}
			else
			{
				Main.createNewFile(file);
				EPSConfiguration config1 = EPSConfiguration.loadConfiguration(file);
				config1.autoFillEnchantConfig(dictionary.getDefaultDescription(enchant), dictionary.getDefaultCost(enchant));
				try {
					config1.save(file);
				} catch (IOException e) {
					e.printStackTrace();
				}
				fgMap.put(enchant, config1);
				return config1;
			}
		}
	}
    
	public static double eval(final String str) {
	    return new Object() {
	        int pos = -1, ch;

	        void nextChar() {
	            ch = (++pos < str.length()) ? str.charAt(pos) : -1;
	        }

	        boolean eat(int charToEat) {
	            while (ch == ' ') nextChar();
	            if (ch == charToEat) {
	                nextChar();
	                return true;
	            }
	            return false;
	        }

	        double parse() {
	            nextChar();
	            double x = parseExpression();
	            if (pos < str.length()) throw new RuntimeException("Unexpected: " + (char)ch);
	            return x;
	        }

	        // Grammar:
	        // expression = term | expression `+` term | expression `-` term
	        // term = factor | term `*` factor | term `/` factor
	        // factor = `+` factor | `-` factor | `(` expression `)`
	        //        | number | functionName factor | factor `^` factor

	        double parseExpression() {
	            double x = parseTerm();
	            for (;;) {
	                if      (eat('+')) x += parseTerm(); // addition
	                else if (eat('-')) x -= parseTerm(); // subtraction
	                else return x;
	            }
	        }

	        double parseTerm() {
	            double x = parseFactor();
	            for (;;) {
	                if      (eat('*')) x *= parseFactor(); // multiplication
	                else if (eat('/')) x /= parseFactor(); // division
	                else return x;
	            }
	        }

	        double parseFactor() {
	            if (eat('+')) return parseFactor(); // unary plus
	            if (eat('-')) return -parseFactor(); // unary minus

	            double x;
	            int startPos = this.pos;
	            if (eat('(')) { // parentheses
	                x = parseExpression();
	                eat(')');
	            } else if ((ch >= '0' && ch <= '9') || ch == '.') { // numbers
	                while ((ch >= '0' && ch <= '9') || ch == '.') nextChar();
	                x = Double.parseDouble(str.substring(startPos, this.pos));
	            } else if (ch >= 'a' && ch <= 'z') { // functions
	                while (ch >= 'a' && ch <= 'z') nextChar();
	                String func = str.substring(startPos, this.pos);
	                x = parseFactor();
	                if (func.equals("sqrt")) x = Math.sqrt(x);
	                else if (func.equals("sin")) x = Math.sin(Math.toRadians(x));
	                else if (func.equals("cos")) x = Math.cos(Math.toRadians(x));
	                else if (func.equals("tan")) x = Math.tan(Math.toRadians(x));
	                else throw new RuntimeException("Unknown function: " + func);
	            } else {
	                throw new RuntimeException("Unexpected: " + (char)ch);
	            }

	            if (eat('^')) x = Math.pow(x, parseFactor()); // exponentiation

	            return x;
	        }
	    }.parse();
	}
	
	/**
	 * Just saving but easier.
	 */
	public void save()
	{
		try {
			save(file);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/**
	 * Gets the requested Material by path. 
	 *
     * If the Material does not exist but a default value has been specified, this will return the default value. If the Material does not exist and no default value was specified, this will return null.
	 * 
	 * @param path Path of the Material to get.
	 * 
	 * @return Requested Material.
	 */
	public Material getMaterial(String path)
	{
		return Material.matchMaterial(getString(path));
	}
	
	/**
	 * Gets the requested Material by path. 
	 *
     * If the Material does not exist but a default value has been specified, this will return the default value. If the Material does not exist and no default value was specified, this will return null.
	 * 
	 * @param path Path of the Material to get.
	 * @param def The default value to return if the path is not found or isnot a Material.
	 * 
	 * @return Requested Material.
	 */
	public Material getMaterial(String path, Material def)
	{
		Material m = getMaterial(path);
		return m == null ? def : m;
	}
	
	/**
	 * Gets the requested Enchantment by path. 
	 *
     * If the Enchantment does not exist but a default value has been specified, this will return the default value. If the Enchantment does not exist and no default value was specified, this will return null.
	 * 
	 * @param path Path of the Enchantment to get.
	 * 
	 * @return Requested Enchantment.
	 */
	public Enchantment getEnchantment(String path)
	{
		return EPS.getDictionary().findEnchant(getString(path));
	}
	
	/**
	 * Gets the requested Enchantment by path. 
	 *
     * If the Enchantment does not exist but a default value has been specified, this will return the default value. If the Enchantment does not exist and no default value was specified, this will return null.
	 * 
	 * @param path Path of the Enchantment to get.
	 * 
	 * @return Requested Enchantment.
	 */
	public Enchantment getEnchantment(String path, Enchantment def)
	{
		Enchantment e = EPS.getDictionary().findEnchant(getString(path));
		return e == null ? def : e;
	}
	
	private String getCachedString(String path)
	{
		String cachedString = cachedStrings.get(path);
		if (cachedString == null)
		{
			cachedString = getString(path);
			cachedStrings.put(path, cachedString);
		}
		return cachedString;
	}
	
	public class EPSConfigReloader implements Reloadable {

		@Override
		public void reload() {
			EPSConfiguration.reloadConfigs();
		}
		
	}
}


