package org.vivi.sekai.yaml;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Color;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.block.Container;
import org.bukkit.configuration.Configuration;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;
import org.vivi.sekai.GUIHolder;
import org.vivi.sekai.enchantment.EnchantmentInfo;

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
	public T yaml;
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
	 * predefined {@code Charset} links it to the file by setting
	 * {@code YamlFile.yaml} as {@code configurationToload}.
	 */
	public void loadYaml(T configurationToLoad)
	{
		yaml = configurationToLoad;
		try
		{
			if (charset == null)
				yaml.load(this);
			else
				yaml.load(new InputStreamReader(new FileInputStream(this), charset));
		} catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
	}

	/**
	 * Saves the {@code FileConfiguration} linked to this file to disk.
	 * 
	 */
	public void saveYaml()
	{
		try
		{
			if (yaml != null)
				yaml.save(this);
		} catch (IOException e)
		{
			e.printStackTrace();
		}
	}

	public Set<String> getKeys(boolean deep)
	{
		return yaml.getKeys(deep);
	}

	public Map<String, Object> getValues(boolean deep)
	{
		return yaml.getValues(deep);
	}

	public boolean contains(String path)
	{
		return yaml.contains(path);
	}

	public boolean contains(String path, boolean ignoreDefault)
	{
		return yaml.contains(path, ignoreDefault);
	}

	public boolean isSet(String path)
	{
		return yaml.isSet(path);
	}

	public String getCurrentPath()
	{
		return yaml.getCurrentPath();
	}

	public Configuration getRoot()
	{
		return yaml.getRoot();
	}

	public Object get(String path)
	{
		return yaml.get(path);
	}

	public Object get(String path, Object def)
	{
		return yaml.get(path, def);
	}

	public void set(String path, Object value)
	{
		yaml.set(path, value);
	}

	public ConfigurationSection createSection(String path)
	{
		return yaml.createSection(path);
	}

	public ConfigurationSection createSection(String path, Map<?, ?> map)
	{
		return yaml.createSection(path, map);
	}

	public String getString(String path)
	{
		return yaml.getString(path);
	}

	public String getString(String path, String def)
	{
		return yaml.getString(path, def);
	}

	public boolean isString(String path)
	{
		return yaml.isString(path);
	}

	public int getInt(String path)
	{
		return yaml.getInt(path);
	}

	public int getInt(String path, int def)
	{
		return yaml.getInt(path, def);
	}

	public boolean isInt(String path)
	{
		return yaml.isInt(path);
	}

	public boolean getBoolean(String path)
	{
		return yaml.getBoolean(path);
	}

	public boolean getBoolean(String path, boolean def)
	{
		return yaml.getBoolean(path, def);
	}

	public boolean isBoolean(String path)
	{
		return yaml.isBoolean(path);
	}

	public double getDouble(String path)
	{
		return yaml.getDouble(path);
	}

	public double getDouble(String path, double def)
	{
		return yaml.getDouble(path, def);
	}

	public boolean isDouble(String path)
	{
		return yaml.isDouble(path);
	}

	public long getLong(String path)
	{
		return yaml.getLong(path);
	}

	public long getLong(String path, long def)
	{
		return yaml.getLong(path);
	}

	public boolean isLong(String path)
	{
		return yaml.isLong(path);
	}

	public List<?> getList(String path)
	{
		return yaml.getList(path);
	}

	public List<?> getList(String path, List<?> def)
	{
		return yaml.getList(path, def);
	}

	public boolean isList(String path)
	{
		return yaml.isList(path);
	}

	public List<String> getStringList(String path)
	{
		return yaml.getStringList(path);
	}

	public List<Integer> getIntegerList(String path)
	{
		return yaml.getIntegerList(path);
	}

	public List<Boolean> getBooleanList(String path)
	{
		return yaml.getBooleanList(path);
	}

	public List<Double> getDoubleList(String path)
	{
		return yaml.getDoubleList(path);
	}

	public List<Float> getFloatList(String path)
	{
		return yaml.getFloatList(path);
	}

	public List<Long> getLongList(String path)
	{
		return yaml.getLongList(path);
	}

	public List<Byte> getByteList(String path)
	{
		return yaml.getByteList(path);
	}

	public List<Character> getCharacterList(String path)
	{
		return yaml.getCharacterList(path);
	}

	public List<Short> getShortList(String path)
	{
		return yaml.getShortList(path);
	}

	public List<Map<?, ?>> getMapList(String path)
	{
		return yaml.getMapList(path);
	}

	public <A> A getObject(String path, Class<A> clazz)
	{
		return yaml.getObject(path, clazz);
	}

	public <A> A getObject(String path, Class<A> clazz, A def)
	{
		return yaml.getObject(path, clazz, def);
	}

	public <A extends ConfigurationSerializable> A getSerializable(String path, Class<A> clazz)
	{
		return yaml.getSerializable(path, clazz);
	}

	public <A extends ConfigurationSerializable> A getSerializable(String path, Class<A> clazz, A def)
	{
		return yaml.getSerializable(path, clazz, def);
	}

	public Vector getVector(String path)
	{
		return yaml.getVector(path);
	}

	public Vector getVector(String path, Vector def)
	{
		return yaml.getVector(path, def);
	}

	public boolean isVector(String path)
	{
		return yaml.isVector(path);
	}

	public OfflinePlayer getOfflinePlayer(String path)
	{
		return yaml.getOfflinePlayer(path);
	}

	public OfflinePlayer getOfflinePlayer(String path, OfflinePlayer def)
	{
		return yaml.getOfflinePlayer(path, def);
	}

	public boolean isOfflinePlayer(String path)
	{
		return yaml.isOfflinePlayer(path);
	}

	public ItemStack getItemStack(String path)
	{
		return yaml.getItemStack(path);
	}

	public ItemStack getItemStack(String path, ItemStack def)
	{
		return yaml.getItemStack(path, def);
	}

	public boolean isItemStack(String path)
	{
		return yaml.isItemStack(path);
	}

	public Color getColor(String path)
	{
		return yaml.getColor(path);
	}

	public Color getColor(String path, Color def)
	{
		return yaml.getColor(path, def);
	}

	public boolean isColor(String path)
	{
		return yaml.isColor(path);
	}

	public Location getLocation(String path)
	{
		return yaml.getLocation(path);
	}

	public Location getLocation(String path, Location def)
	{
		return yaml.getLocation(path, def);
	}

	public boolean isLocation(String path)
	{
		return yaml.isLocation(path);
	}

	public ConfigurationSection getConfigurationSection(String path)
	{
		return yaml.getConfigurationSection(path);
	}

	public boolean isConfigurationSection(String path)
	{
		return yaml.isConfigurationSection(path);
	}

	public ConfigurationSection getDefaultSection()
	{
		return yaml.getDefaultSection();
	}

	public void addDefault(String path, Object value)
	{
		yaml.addDefault(path, value);
	}

	public List<String> getComments(String path)
	{
		return yaml.getComments(path);
	}

	public List<String> getInlineComments(String path)
	{
		return yaml.getInlineComments(path);
	}

	public void setComments(String path, List<String> comments)
	{
		yaml.setComments(path, comments);
	}

	public void setInlineComments(String path, List<String> comments)
	{
		yaml.setInlineComments(path, comments);
	}

	public void setBySekai(String path, Object value)
	{
		if (value == null)
			return;

		ConfigurationSection configurationSection = getConfigurationSection(path);
		if (value instanceof List)
		{
			List<?> list = (List<?>) value;
			Object firstElement = list.get(0);
			if (firstElement instanceof ItemStack)
				for (int i = 0; i < list.size(); i++)
					configurationSection.set(Integer.toString(i), list.get(i));
		} else if (value instanceof Inventory)
		{
			Inventory inventory = (Inventory) value;
			InventoryHolder holder = inventory.getHolder();
			if (holder != null && holder instanceof Container)
				configurationSection.set("name", ((Container) holder).getCustomName());
			configurationSection.set("size", inventory.getSize());
			ConfigurationSection itemsConfigurationSection = configurationSection.getConfigurationSection("items");
			ItemStack[] inventoryContents = inventory.getContents();
			for (int i = 0; i < inventoryContents.length; i++)
				itemsConfigurationSection.set(Integer.toString(i), inventoryContents[i]);
		} else if (value instanceof Enchantment)
			set(path, EnchantmentInfo.getKey((Enchantment) value));
		else if (value instanceof Material)
			set(path, value.toString());
		else
		{
			set(path, value);
		}
	}

	public List<ItemStack> getItemStackListBySekai(String path)
	{
		List<ItemStack> list = new ArrayList<ItemStack>();
		for (Map.Entry<String, Object> entry : getConfigurationSection(path).getValues(false).entrySet())
			if (entry.getValue() instanceof ItemStack)
				list.add((ItemStack) entry.getValue());
		return list;
	}

	public Inventory getInventoryBySekai(String path)
	{
		ConfigurationSection configurationSection = getConfigurationSection(path);
		Inventory inventory = Bukkit.createInventory(new GUIHolder(), configurationSection.getInt("size"),
				configurationSection.getString("name"));
		for (String key : configurationSection.getConfigurationSection("items").getKeys(false))
			inventory.setItem(Integer.parseInt(key), getItemStack(path + ".items." + key));
		return inventory;
	}

	public Enchantment getEnchantmentBySekai(String path)
	{
		return EnchantmentInfo.findEnchantByKey(getString(path));
	}

	public Material getMaterialBySekai(String path)
	{
		return Material.matchMaterial(getString(path, ""));
	}

	public void supersedePath(String pathToSupersede, String supersedingPath)
	{
		if (contains(pathToSupersede))
		{
			set(supersedingPath, pathToSupersede);
			set(pathToSupersede, null);
		}
	}
}
