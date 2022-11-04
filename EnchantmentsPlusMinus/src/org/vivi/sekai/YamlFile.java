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
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.vivi.eps.EPS;

/**
 * A {@code File} with automatic {@code FileConfiguration} linking. I dont know
 * the actual term so im calling it linking
 * 
 * @author vivisan
 *
 */
public class YamlFile<T extends FileConfiguration> extends File
{
	private static final long serialVersionUID = -778885166783198770L;
	private T fileConfiguration;
	private Charset charset = null;

	/**
	 * Creates a new {@code YamlFile} instance from a parent abstract path name and
	 * a child pathname string.
	 * 
	 * @param parent  The parent abstract pathname
	 * @param child   The child pathname string
	 * @param charset {@code Charset} used to load the configuration in the file.
	 */
	public YamlFile(File parent, String child, Charset charset)
	{
		super(parent, child);
		this.charset = charset;
	}

	/**
	 * Creates a new {@code YamlFile} instance from a parent abstract path name and
	 * a child pathname string.
	 * 
	 * @param parent The parent abstract pathname
	 * @param child  The child pathname string
	 */
	public YamlFile(File parent, String child)
	{
		super(parent, child);
	}

	/**
	 * Returns the {@code Charset} used to load the {@code FileConfiguration} linked
	 * to the file
	 * 
	 * @return {@code Charset} used to load the {@code FileConfiguration} linked to
	 *         the file
	 */
	public Charset getCharset()
	{
		return charset;
	}

	/**
	 * Sets the {@code Charset} used to load the {@code FileConfiguration} linked to
	 * the file
	 * 
	 * @param charset {@code Charset} to set to
	 */
	public void setCharset(Charset charset)
	{
		this.charset = charset;
	}

	/**
	 * Reads and loads the file to the specified {@code FileConfiguration} using
	 * predefined {@code Charset} links it to the file.
	 */
	public void loadYaml(T configurationToLoad)
	{
		fileConfiguration = configurationToLoad;
		try
		{
			if (charset == null)
				fileConfiguration.load(this);
			else
				fileConfiguration.load(new InputStreamReader(new FileInputStream(this), charset));
		} catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Gets the {@code FileConfiguration} linked to this file.
	 * 
	 * @return {@code FileConfiguration} linked to this file
	 */
	public FileConfiguration getYaml()
	{
		return fileConfiguration;
	}

	/**
	 * Saves the {@code FileConfiguration} linked to this file to disk. By default,
	 * this operation is done asynchronously.
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
				fileConfiguration.save(this);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Saves the {@code FileConfiguration} linked to this file to disk. By default,
	 * this operation is done asynchronously.
	 * 
	 */
	public void saveYaml()
	{
		saveYaml(true);
	}

	public Set<String> getKeys(boolean deep)
	{
		return fileConfiguration.getKeys(deep);
	}

	public Map<String, Object> getValues(boolean deep)
	{
		return fileConfiguration.getValues(deep);
	}

	public boolean contains(String path)
	{
		return fileConfiguration.contains(path);
	}

	public boolean contains(String path, boolean ignoreDefault)
	{
		return fileConfiguration.contains(path, ignoreDefault);
	}

	public boolean isSet(String path)
	{
		return fileConfiguration.isSet(path);
	}

	public String getCurrentPath()
	{
		return fileConfiguration.getCurrentPath();
	}

	public Configuration getRoot()
	{
		return fileConfiguration.getRoot();
	}

	public Object get(String path)
	{
		return fileConfiguration.get(path);
	}

	public Object get(String path, Object def)
	{
		return fileConfiguration.get(path, def);
	}

	public void set(String path, Object value)
	{
		fileConfiguration.set(path, value);
	}

	public ConfigurationSection createSection(String path)
	{
		return fileConfiguration.createSection(path);
	}

	public ConfigurationSection createSection(String path, Map<?, ?> map)
	{
		return fileConfiguration.createSection(path, map);
	}

	public String getString(String path)
	{
		return fileConfiguration.getString(path);
	}

	public String getString(String path, String def)
	{
		return fileConfiguration.getString(path, def);
	}

	public boolean isString(String path)
	{
		return fileConfiguration.isString(path);
	}

	public int getInt(String path)
	{
		return fileConfiguration.getInt(path);
	}

	public int getInt(String path, int def)
	{
		return fileConfiguration.getInt(path, def);
	}

	public boolean isInt(String path)
	{
		return fileConfiguration.isInt(path);
	}

	public boolean getBoolean(String path)
	{
		return fileConfiguration.getBoolean(path);
	}

	public boolean getBoolean(String path, boolean def)
	{
		return fileConfiguration.getBoolean(path, def);
	}

	public boolean isBoolean(String path)
	{
		return fileConfiguration.isBoolean(path);
	}

	public double getDouble(String path)
	{
		return fileConfiguration.getDouble(path);
	}

	public double getDouble(String path, double def)
	{
		return fileConfiguration.getDouble(path, def);
	}

	public boolean isDouble(String path)
	{
		return fileConfiguration.isDouble(path);
	}

	public long getLong(String path)
	{
		return fileConfiguration.getLong(path);
	}

	public long getLong(String path, long def)
	{
		return fileConfiguration.getLong(path);
	}

	public boolean isLong(String path)
	{
		return fileConfiguration.isLong(path);
	}

	public List<?> getList(String path)
	{
		return fileConfiguration.getList(path);
	}

	public List<?> getList(String path, List<?> def)
	{
		return fileConfiguration.getList(path, def);
	}

	public boolean isList(String path)
	{
		return fileConfiguration.isList(path);
	}

	public List<String> getStringList(String path)
	{
		return fileConfiguration.getStringList(path);
	}

	public List<Integer> getIntegerList(String path)
	{
		return fileConfiguration.getIntegerList(path);
	}

	public List<Boolean> getBooleanList(String path)
	{
		return fileConfiguration.getBooleanList(path);
	}

	public List<Double> getDoubleList(String path)
	{
		return fileConfiguration.getDoubleList(path);
	}

	public List<Float> getFloatList(String path)
	{
		return fileConfiguration.getFloatList(path);
	}

	public List<Long> getLongList(String path)
	{
		return fileConfiguration.getLongList(path);
	}

	public List<Byte> getByteList(String path)
	{
		return fileConfiguration.getByteList(path);
	}

	public List<Character> getCharacterList(String path)
	{
		return fileConfiguration.getCharacterList(path);
	}

	public List<Short> getShortList(String path)
	{
		return fileConfiguration.getShortList(path);
	}

	public List<Map<?, ?>> getMapList(String path)
	{
		return fileConfiguration.getMapList(path);
	}

	public <A> A getObject(String path, Class<A> clazz)
	{
		return fileConfiguration.getObject(path, clazz);
	}

	public <A> A getObject(String path, Class<A> clazz, A def)
	{
		return fileConfiguration.getObject(path, clazz, def);
	}

	public <A extends ConfigurationSerializable> A getSerializable(String path, Class<A> clazz)
	{
		return fileConfiguration.getSerializable(path, clazz);
	}

	public <A extends ConfigurationSerializable> A getSerializable(String path, Class<A> clazz, A def)
	{
		return fileConfiguration.getSerializable(path, clazz, def);
	}

	public Vector getVector(String path)
	{
		return fileConfiguration.getVector(path);
	}

	public Vector getVector(String path, Vector def)
	{
		return fileConfiguration.getVector(path, def);
	}

	public boolean isVector(String path)
	{
		return fileConfiguration.isVector(path);
	}

	public OfflinePlayer getOfflinePlayer(String path)
	{
		return fileConfiguration.getOfflinePlayer(path);
	}

	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def)
	{
		return fileConfiguration.getOfflinePlayer(path, def);
	}

	public boolean isOfflinePlayer(String path)
	{
		return fileConfiguration.isOfflinePlayer(path);
	}

	public ItemStack getItemStack(String path)
	{
		return fileConfiguration.getItemStack(path);
	}

	public ItemStack getItemStack(String path, ItemStack def)
	{
		return fileConfiguration.getItemStack(path, def);
	}

	public boolean isItemStack(String path)
	{
		return fileConfiguration.isItemStack(path);
	}

	public Color getColor(String path)
	{
		return fileConfiguration.getColor(path);
	}

	public Color getColor(String path, Color def)
	{
		return fileConfiguration.getColor(path, def);
	}

	public boolean isColor(String path)
	{
		return fileConfiguration.isColor(path);
	}

	public Location getLocation(String path)
	{
		return fileConfiguration.getLocation(path);
	}

	public Location getLocation(String path, Location def)
	{
		return fileConfiguration.getLocation(path, def);
	}

	public boolean isLocation(String path)
	{
		return fileConfiguration.isLocation(path);
	}

	public ConfigurationSection getConfigurationSection(String path)
	{
		return fileConfiguration.getConfigurationSection(path);
	}

	public boolean isConfigurationSection(String path)
	{
		return fileConfiguration.isConfigurationSection(path);
	}

	public ConfigurationSection getDefaultSection()
	{
		return fileConfiguration.getDefaultSection();
	}

	public void addDefault(String path, Object value)
	{
		fileConfiguration.addDefault(path, value);
	}

	public List<String> getComments(String path)
	{
		return fileConfiguration.getComments(path);
	}

	public List<String> getInlineComments(String path)
	{
		return fileConfiguration.getInlineComments(path);
	}

	public void setComments(String path, List<String> comments)
	{
		fileConfiguration.setComments(path, comments);
	}

	public void setInlineComments(String path, List<String> comments)
	{
		fileConfiguration.setInlineComments(path, comments);
	}

	public void setDefault(String path, Object value)
	{
		if (!fileConfiguration.isSet(path))
			fileConfiguration.set(path, value);
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
		if (fileConfiguration.contains(pathToSupersede))
		{
			fileConfiguration.set(supersedingPath, pathToSupersede);
			fileConfiguration.set(pathToSupersede, null);
		}
	}

}
