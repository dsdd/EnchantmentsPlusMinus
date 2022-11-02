package org.vivi.sekai;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.OfflinePlayer;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.vivi.eps.EPS;

/**
 * A File with automatic YamlConfiguration linking
 * 
 * No value caching man
 * 
 * @author vivisan
 *
 */
public class YamlFile extends File
{
	private static final long serialVersionUID = -778885166783198770L;
	private YamlConfiguration yamlConfiguration = new YamlConfiguration();
	private Charset charset = null;

	/**
	 * Creates a new YamlFile instance from a parent abstract path name and a child
	 * pathname string.
	 * 
	 * 
	 * @param parent  The parent abstract pathname
	 * @param child   The child pathname string
	 * @param charset Charset used to load the YAML configuration in the file.
	 */
	public YamlFile(File parent, String child, Charset charset)
	{
		super(parent, child);
		this.charset = charset;
	}

	/**
	 * Creates a new YamlFile instance from a parent abstract path name and a child
	 * pathname string.
	 * 
	 * @param parent The parent abstract pathname
	 * @param child  The child pathname string
	 */
	public YamlFile(File parent, String child)
	{
		super(parent, child);
	}

	/**
	 * Returns the Charset used to load the YAML configuration in the file
	 * 
	 * @return Charset used to load the YAML configuration in the file
	 */
	public Charset getCharset()
	{
		return charset;
	}

	/**
	 * Sets the Charset used to load the YAML configuration in the file
	 * 
	 * @param charset Charset to set to
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset;
	}

	/**
	 * Reads and loads the file to a YamlConfiguration.
	 * 
	 * @return YamlConfiguration from YamlConfiguration.loadConfiguration()
	 */
	public YamlConfiguration loadYaml()
	{
		if (charset == null)
			yamlConfiguration = YamlConfiguration.loadConfiguration(this);
		else
			try
			{
				yamlConfiguration.load(new InputStreamReader(new FileInputStream(this), charset));
			} catch (IOException | InvalidConfigurationException e)
			{
				e.printStackTrace();
			}
		return yamlConfiguration;
	}

	/**
	 * Gets the YAML configuration linked to this file.
	 * 
	 * @return YAML configuration linked to this file
	 */
	public YamlConfiguration getYaml()
	{
		return yamlConfiguration;
	}

	/**
	 * Saves the YAML configuration linked to this file to disk. By default, this
	 * operation is done asynchronously.
	 * 
	 * @param isAsync Whether saving should be done asynchronously or not
	 */
	public void saveYaml(boolean isAsync)
	{
		if (isAsync)
			Bukkit.getScheduler().runTaskAsynchronously(EPS.plugin, new Runnable() {

				public void run()
				{
					saveYaml(false);
				}

			});
		else
			try
			{
				yamlConfiguration.save(this);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Saves the YAML configuration linked to this file to disk. By default, this
	 * operation is done asynchronously.
	 * 
	 */
	public void saveYaml()
	{
		saveYaml(true);
	}

	public Set<String> getKeys(boolean deep)
	{
		return yamlConfiguration.getKeys(deep);
	}

	public Map<String, Object> getValues(boolean deep)
	{
		return yamlConfiguration.getValues(deep);
	}

	public boolean contains(String path)
	{
		return yamlConfiguration.contains(path);
	}

	public boolean contains(String path, boolean ignoreDefault)
	{
		return yamlConfiguration.contains(path, ignoreDefault);
	}

	public boolean isSet(String path)
	{
		return yamlConfiguration.isSet(path);
	}

	public String getCurrentPath()
	{
		return yamlConfiguration.getCurrentPath();
	}

	public Configuration getRoot()
	{
		return yamlConfiguration.getRoot();
	}

	public Object get(String path)
	{
		return yamlConfiguration.get(path);
	}

	public Object get(String path, Object def)
	{
		return yamlConfiguration.get(path, def);
	}

	public void set(String path, Object value)
	{
		yamlConfiguration.set(path, value);
	}

	public ConfigurationSection createSection(String path)
	{
		return yamlConfiguration.createSection(path);
	}

	public ConfigurationSection createSection(String path, Map<?, ?> map)
	{
		return yamlConfiguration.createSection(path, map);
	}

	public String getString(String path)
	{
		return yamlConfiguration.getString(path);
	}

	public String getString(String path, String def)
	{
		return yamlConfiguration.getString(path, def);
	}

	public boolean isString(String path)
	{
		return yamlConfiguration.isString(path);
	}

	public int getInt(String path)
	{
		return yamlConfiguration.getInt(path);
	}

	public int getInt(String path, int def)
	{
		return yamlConfiguration.getInt(path, def);
	}

	public boolean isInt(String path)
	{
		return yamlConfiguration.isInt(path);
	}

	public boolean getBoolean(String path)
	{
		return yamlConfiguration.getBoolean(path);
	}

	public boolean getBoolean(String path, boolean def)
	{
		return yamlConfiguration.getBoolean(path, def);
	}

	public boolean isBoolean(String path)
	{
		return yamlConfiguration.isBoolean(path);
	}

	public double getDouble(String path)
	{
		return yamlConfiguration.getDouble(path);
	}

	public double getDouble(String path, double def)
	{
		return yamlConfiguration.getDouble(path, def);
	}

	public boolean isDouble(String path)
	{
		return yamlConfiguration.isDouble(path);
	}

	public long getLong(String path)
	{
		return yamlConfiguration.getLong(path);
	}

	public long getLong(String path, long def)
	{
		return yamlConfiguration.getLong(path);
	}

	public boolean isLong(String path)
	{
		return yamlConfiguration.isLong(path);
	}

	public List<?> getList(String path)
	{
		return yamlConfiguration.getList(path);
	}

	public List<?> getList(String path, List<?> def)
	{
		return yamlConfiguration.getList(path, def);
	}

	public boolean isList(String path)
	{
		return yamlConfiguration.isList(path);
	}

	public List<String> getStringList(String path)
	{
		return yamlConfiguration.getStringList(path);
	}

	public List<Integer> getIntegerList(String path)
	{
		return yamlConfiguration.getIntegerList(path);
	}

	public List<Boolean> getBooleanList(String path)
	{
		return yamlConfiguration.getBooleanList(path);
	}

	public List<Double> getDoubleList(String path)
	{
		return yamlConfiguration.getDoubleList(path);
	}

	public List<Float> getFloatList(String path)
	{
		return yamlConfiguration.getFloatList(path);
	}

	public List<Long> getLongList(String path)
	{
		return yamlConfiguration.getLongList(path);
	}

	public List<Byte> getByteList(String path)
	{
		return yamlConfiguration.getByteList(path);
	}

	public List<Character> getCharacterList(String path)
	{
		return yamlConfiguration.getCharacterList(path);
	}

	public List<Short> getShortList(String path)
	{
		return yamlConfiguration.getShortList(path);
	}

	public List<Map<?, ?>> getMapList(String path)
	{
		return yamlConfiguration.getMapList(path);
	}

	public <T> T getObject(String path, Class<T> clazz)
	{
		return yamlConfiguration.getObject(path, clazz);
	}

	public <T> T getObject(String path, Class<T> clazz, T def)
	{
		return yamlConfiguration.getObject(path, clazz, def);
	}

	public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz)
	{
		return yamlConfiguration.getSerializable(path, clazz);
	}

	public <T extends ConfigurationSerializable> T getSerializable(String path, Class<T> clazz, T def)
	{
		return yamlConfiguration.getSerializable(path, clazz, def);
	}

	public Vector getVector(String path)
	{
		return yamlConfiguration.getVector(path);
	}

	public Vector getVector(String path, Vector def)
	{
		return yamlConfiguration.getVector(path, def);
	}

	public boolean isVector(String path)
	{
		return yamlConfiguration.isVector(path);
	}

	public OfflinePlayer getOfflinePlayer(String path)
	{
		return yamlConfiguration.getOfflinePlayer(path);
	}

	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def)
	{
		return yamlConfiguration.getOfflinePlayer(path, def);
	}

	public boolean isOfflinePlayer(String path)
	{
		return yamlConfiguration.isOfflinePlayer(path);
	}

	public ItemStack getItemStack(String path)
	{
		return yamlConfiguration.getItemStack(path);
	}

	public ItemStack getItemStack(String path, ItemStack def)
	{
		return yamlConfiguration.getItemStack(path, def);
	}

	public boolean isItemStack(String path)
	{
		return yamlConfiguration.isItemStack(path);
	}

	public Color getColor(String path)
	{
		return yamlConfiguration.getColor(path);
	}

	public Color getColor(String path, Color def)
	{
		return yamlConfiguration.getColor(path, def);
	}

	public boolean isColor(String path)
	{
		return yamlConfiguration.isColor(path);
	}

	public Location getLocation(String path)
	{
		return yamlConfiguration.getLocation(path);
	}

	public Location getLocation(String path, Location def)
	{
		return yamlConfiguration.getLocation(path, def);
	}

	public boolean isLocation(String path)
	{
		return yamlConfiguration.isLocation(path);
	}

	public ConfigurationSection getConfigurationSection(String path)
	{
		return yamlConfiguration.getConfigurationSection(path);
	}

	public boolean isConfigurationSection(String path)
	{
		return yamlConfiguration.isConfigurationSection(path);
	}

	public ConfigurationSection getDefaultSection()
	{
		return yamlConfiguration.getDefaultSection();
	}

	public void addDefault(String path, Object value)
	{
		yamlConfiguration.addDefault(path, value);
	}

	public List<String> getComments(String path)
	{
		return yamlConfiguration.getComments(path);
	}

	public List<String> getInlineComments(String path)
	{
		return yamlConfiguration.getInlineComments(path);
	}

	public void setComments(String path, List<String> comments)
	{
		yamlConfiguration.setComments(path, comments);
	}

	public void setInlineComments(String path, List<String> comments)
	{
		yamlConfiguration.setInlineComments(path, comments);
	}

	public void setDefault(String path, Object value)
	{
		if (!yamlConfiguration.isSet(path))
			yamlConfiguration.set(path, value);
	}

	/**
	 * If a value exists by the specified path to supersede, the superseding path
	 * will be set to that value and the former path will be removed (set to null).
	 * 
	 * @param pathToSupersede The path that will be removed
	 * @param supersedingPath The path that will take the place of the superseded
	 *                        path
	 */
	public void supersedePath(String pathToSupersede, String supersedingPath)
	{
		if (yamlConfiguration.contains(pathToSupersede))
		{
			yamlConfiguration.set(supersedingPath, pathToSupersede);
			yamlConfiguration.set(pathToSupersede, null);
		}
	}

}
