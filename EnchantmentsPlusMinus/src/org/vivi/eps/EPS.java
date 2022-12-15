package org.vivi.eps;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.api.EnchantHandler;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.EnchantWrapper;
import org.vivi.eps.util.economy.Economy;
import org.vivi.eps.util.economy.TokenEconomy;
import org.vivi.eps.util.economy.VaultEconomy;
import org.vivi.epsbuiltin.enchants.BuiltInEnchantsLoader;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.dependencies.Metrics;
import org.vivi.sekai.dependencies.VaultHook;
import org.vivi.sekai.enchantment.EnchantmentInfo;
import org.vivi.sekai.misc.numbers.NumberAbbreviations;
import org.vivi.sekai.misc.numbers.RomanNumeral;
import org.vivi.sekai.yaml.YamlFile;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class EPS extends JavaPlugin implements Reloadable
{
	public static EPS plugin;
	public static File dataFolder;
	public static File enchantsFolder;
	public static YamlFile<YamlConfiguration> configFile;
	public static YamlFile<YamlConfiguration> languageFile;
	public static YamlFile<YamlConfiguration> incompatibilitiesFile;
	public static YamlFile<YamlConfiguration> guisFile;
	public static YamlFile<YamlConfiguration> uuidDataStore;
	public static YamlFile<YamlConfiguration> oldEnchantNamesFile;
	public static Map<Enchantment, EnchantFile> enchantmentFilesMap = new HashMap<Enchantment, EnchantFile>();
	public static Set<Set<Enchantment>> incompatibilities = new HashSet<Set<Enchantment>>();
	public static Map<Set<Material>, List<Enchantment>> guis = new HashMap<Set<Material>, List<Enchantment>>();
	public static Map<Integer, Double> globalCostsMap = new HashMap<Integer, Double>();
	public static Logger logger;

	private static Map<Enchantment, ArrayList<String>> enchantDescriptionLinesMap = new HashMap<Enchantment, ArrayList<String>>();
	private static Set<String> oldEnchantNames = new HashSet<String>();
	private static Set<String> allDescriptionLines = new HashSet<String>();
	private static Economy economy = null;
	private static Updater updater = new Updater();
	private static Events epsEvents = new Events();
	private static Enchantment NULL_ENCHANT = null;

	@Override
	public void onEnable()
	{
		try
		{
			long startTime = System.currentTimeMillis();
			plugin = this;
			logger = getLogger();

			Field f = Enchantment.class.getDeclaredField("acceptingNew");
			f.setAccessible(true);
			f.set(null, true);
			logger.log(Level.FINE, "Enabled acceptance of new Enchantment registrations");

			reload();

			updater.makeCompatible();

			ConfigSettings configSettings = new ConfigSettings();
			configSettings.reload();
			registerReloadable(configSettings);
			EPS.registerReloadable(Language.lang);

			// Load dependencies
			new Metrics(plugin, 9735);
			if (Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
				new PlaceholderAPIHook().register();

			if (VaultHook.hook())
				logger.log(Level.INFO, "Successfully hooked into Vault.");
			setEconomy(ConfigSettings.isUseVaultEconomy() ? new VaultEconomy() : new TokenEconomy());

			// Load commands
			Commands.registerCommands();

			// Load events
			Bukkit.getPluginManager().registerEvents(epsEvents, this);
			EPS.registerReloadable(this); // This has to be reloaded first.

			configFile.saveYaml();
			epsEvents.reload();
			for (Player player : Bukkit.getOnlinePlayers())
				epsEvents.onJoin(new PlayerJoinEvent(player, null));

			// And load in the built-in enchants.

			NULL_ENCHANT = EPS.newEnchant("null");
			new BuiltInEnchantsLoader().onEnable();

			logger.log(Level.INFO,
					"Load time: " + Long.toString(System.currentTimeMillis() - startTime) + " ms (rough approx.)");

			EPS.reloadConfigs(); // hotfix for startup
		} catch (IOException | NoSuchFieldException | SecurityException | IllegalArgumentException
				| IllegalAccessException e)
		{
			e.printStackTrace();
		}
	}

	@Override
	public void onDisable()
	{
		updater.autoUpdate();
	}

	/**
	 * Gets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static Economy getEconomy()
	{
		return economy;
	}

	/**
	 * Sets the main economy of EPS.
	 * 
	 * @return The main economy of EPS
	 */
	public static void setEconomy(Economy economy)
	{
		EPS.economy = economy;
	}

	/**
	 * Gets the plugin folder (/plugins/EnchantmentsPlusMinus)
	 * 
	 * @return The plugin folder
	 */
	public static File getPluginFolder()
	{
		return EPS.plugin.getDataFolder();
	}

	/**
	 * Gets the enchants folder (/plugins/EnchantmentsPlusMinus/enchants)
	 * 
	 * @return The enchants folder
	 */
	public static File getEnchantsFolder()
	{
		return EPS.enchantsFolder;
	}

	/**
	 * Registers the specified Reloadable to be ready for listening.
	 */
	public static void registerReloadable(Reloadable r)
	{
		Reloadable.addReloadable(r);
	}

	/**
	 * Fires reload() in all registered Reloadable classes.
	 */
	public static void reloadConfigs()
	{
		logger.log(Level.INFO, "Reload fired.");
		for (Reloadable r : Reloadable.INSTANCES)
			r.reload();
	}

	/**
	 * Abbreviates a {@code double} value to a {@code String}
	 * 
	 * @param value Value to abbreviate
	 * @return Requested {@code String} containing abbreviated value
	 */
	public static String abbreviate(double value)
	{
		return ConfigSettings.isAbbreviateLargeNumbers() ? NumberAbbreviations.abbreviate(value, true)
				: Double.toString(Math.floor(value * 100) / 100);
	}

	@Override
	public void reload()
	{
		if (dataFolder == null)
			dataFolder = new File(getDataFolder(), "data");
		if (enchantsFolder == null)
			enchantsFolder = new File(getDataFolder(), "enchants");
		if (configFile == null)
			configFile = new YamlFile<YamlConfiguration>(getDataFolder(), "config.yml");
		if (languageFile == null)
			languageFile = new YamlFile<YamlConfiguration>(getDataFolder(), "lang.yml", Charset.forName("UTF-8"));
		if (guisFile == null)
			guisFile = new YamlFile<YamlConfiguration>(getDataFolder(), "guis.yml");
		if (incompatibilitiesFile == null)
			incompatibilitiesFile = new YamlFile<YamlConfiguration>(getDataFolder(), "incompatibilities.yml");
		if (uuidDataStore == null)
			uuidDataStore = new YamlFile<YamlConfiguration>(dataFolder, "usernamestore.yml");
		if (oldEnchantNamesFile == null)
			oldEnchantNamesFile = new YamlFile<YamlConfiguration>(dataFolder, "oldenchantnames.yml");

		if (!dataFolder.exists())
			dataFolder.mkdirs();

		if (!enchantsFolder.exists())
			enchantsFolder.mkdirs();

		Sekai.saveDefaultFile(EPS.plugin, "/config.yml", configFile);
		Sekai.saveDefaultFile(EPS.plugin, "/lang.yml", languageFile);
		Sekai.saveDefaultFile(EPS.plugin, "/guis.yml", guisFile);
		Sekai.saveDefaultFile(EPS.plugin, "/incompatibilities.yml", incompatibilitiesFile);

		try
		{
			if (!uuidDataStore.exists())
				uuidDataStore.createNewFile();
			if (!oldEnchantNamesFile.exists())
				oldEnchantNamesFile.createNewFile();
		} catch (IOException e)
		{
			e.printStackTrace();
		}

		configFile.loadYaml(new YamlConfiguration());
		languageFile.loadYaml(new YamlConfiguration());
		incompatibilitiesFile.loadYaml(new YamlConfiguration());
		guisFile.loadYaml(new YamlConfiguration());
		uuidDataStore.loadYaml(new YamlConfiguration());
		oldEnchantNamesFile.loadYaml(new YamlConfiguration());

		oldEnchantNames = new HashSet<String>(oldEnchantNamesFile.getStringList("old-enchant-names"));

		for (Enchantment enchant : Enchantment.values())
		{
			EnchantmentInfo enchantmentInfo = EnchantmentInfo.getEnchantmentInfo(enchant);
			EnchantFile enchantFile = EPS.getEnchantFile(enchant, false);

			if (enchantFile != null && enchantFile.exists())
			{
				enchantFile.loadYaml(new YamlConfiguration());
				if (enchantFile.getEnchantName() != null)
					enchantmentInfo.name(enchantFile.getEnchantName());

				if (enchantFile.getEnchantDescription() != null)
					enchantmentInfo.description(enchantFile.getEnchantDescription());
			}
			
			oldEnchantNames.add(enchantmentInfo.getName());
			getEnchantDescriptionLines(enchant);
		}

		oldEnchantNamesFile.set("old-enchant-names", new ArrayList<String>(oldEnchantNames));
		oldEnchantNamesFile.saveYaml();

		ConfigurationSection incompatibilitiesConfigurationSection = incompatibilitiesFile
				.getConfigurationSection("incompatibilities");
		incompatibilities.clear();

		if (incompatibilitiesConfigurationSection == null)
		{
			logger.log(Level.WARNING, "Invalid incompatibilities.yml file! Delete the current one to repair.");
			return;
		}
		for (Map.Entry<String, Object> entry : incompatibilitiesConfigurationSection.getValues(false).entrySet())
		{
			if (entry.getValue() instanceof List)
			{
				List<?> written = (List<?>) entry.getValue();
				Set<Enchantment> incompatibleEnchants = new HashSet<Enchantment>();

				logger.log(Level.FINER, "Setting incompatibilities " + entry.getKey());

				for (Object enchantKey : written)
					if (enchantKey instanceof String)
					{
						incompatibleEnchants.add(EnchantmentInfo.getEnchantByKey((String) enchantKey));
						logger.log(Level.FINER, "Added " + enchantKey);
					}

				incompatibilities.add(incompatibleEnchants);
			}
		}

		for (Map.Entry<String, Object> entry : guisFile.getConfigurationSection("guis").getValues(false).entrySet())
			if (entry.getValue() instanceof ConfigurationSection)
			{
				ConfigurationSection configurationSection = (ConfigurationSection) entry.getValue();

				Set<Material> materials = new HashSet<Material>();
				for (String materialName : configurationSection.getStringList("items"))
					if (Material.matchMaterial(materialName) != null)
						materials.add(Material.matchMaterial(materialName));

				List<Enchantment> enchants = new ArrayList<Enchantment>();
				for (String enchantKey : configurationSection.getStringList("enchants"))
					if (EnchantmentInfo.getEnchantByKey(enchantKey) != null)
						enchants.add(EnchantmentInfo.getEnchantByKey(enchantKey));

				if (!materials.isEmpty())
					guis.put(materials, enchants);
			}

		Commands.playerOnlyMessage = Language.getLangMessage("invalidplayertype");
		Commands.insufficientPermissionsMessage = Language.getLangMessage("insufficientpermission");
	}

	public static void purchaseEnchant(Player player, ItemStack itemToEnchant, Enchantment enchant, int levels)
	{
		EnchantFile enchantFile = getEnchantFile(enchant);
		ItemMeta itemMeta = itemToEnchant.getItemMeta();
		int currentLevel = itemMeta.getEnchantLevel(enchant);
		int upgradedLevel = currentLevel + levels;

		double cost = getCost(enchant, currentLevel, levels);
		if (!(upgradedLevel - 1 >= enchantFile.getMaxLevel()) || player.hasPermission("eps.admin.bypassmaxlevel"))
		{
			if (!player.hasPermission("eps.admin.bypassincompatibilities"))
			{
				for (Set<Enchantment> incompatibleEnchants : incompatibilities)
				{
					if (incompatibleEnchants.contains(enchant))
						for (Enchantment e : incompatibleEnchants)
							if (e != null)
								if (itemMeta.hasEnchant(e) && !e.equals(enchant))
								{
									Language.sendMessage(player, "lockedupgrade");
									return;
								}
				}

				if (!enchant.canEnchantItem(itemToEnchant))
				{
					Language.sendMessage(player, "lockedupgrade");
					return;
				}
			}

			if (getEconomy().getBalance(player) >= cost)
			{
				player.sendMessage(Language.getLangMessage("upgraded-item")
						.replaceAll("%enchant%", EnchantmentInfo.getName(enchant))
						.replaceAll("%lvl%", Integer.toString(upgradedLevel)));
				itemToEnchant.addUnsafeEnchantment(enchant, upgradedLevel);
				getEconomy().setBalance(player, getEconomy().getBalance(player) - cost);
				itemToEnchant.setItemMeta(EnchantMetaWriter.getWrittenMeta(itemToEnchant));
			} else
				Language.sendMessage(player, "insufficienttokens");
		} else if ((currentLevel >= enchantFile.getInt("maxlevel")))
			Language.sendMessage(player, "exceedmaxlvl");
		else
			Language.sendMessage(player, "maxedupgrade");

	}

	/**
	 * Returns the cost of the next specified levels of an enchant
	 * 
	 * @param type         The type of cost increase used
	 * @param enchant      The enchantment to calculate
	 * @param currentLevel The current enchantment level
	 * @param levels       The amount of levels to be increased by
	 * @return Returns the cost of the next specified levels of an enchant
	 */
	public static double getCost(Enchantment enchant, int currentLevel, int levels)
	{
		EnchantFile enchantFile = getEnchantFile(enchant);

		if (ConfigSettings.isGlobalCostEnabled())
		{
			String globalCostExpression = ConfigSettings.getGlobalCostExpression();
			double val = 0;
			for (int i = 0; i < levels; i++)
			{
				int level = currentLevel + 1 + i;
				Double cost = globalCostsMap.get(level);
				if (cost == null)
				{
					cost = Sekai.evaluateExpression(globalCostExpression.replaceAll("%lvl%", Integer.toString(level)));
					globalCostsMap.put(level, cost);
				}
				val += cost;
			}
			return val;

		}

		double val = 0;
		for (int i = 0; i < levels; i++)
			val += enchantFile.getCost(currentLevel + 1 + i);

		return val;
	}

	/**
	 * Registers an enchant for use. Without registering an enchant, the enchant
	 * will stay unusable.
	 * 
	 * @param enchant The enchant you want to register.
	 * @param handler An {@link EnchantHandler} to handle the specified enchant.
	 * @return Returns if the registering was successful.
	 */
	public static boolean registerEnchant(Enchantment enchant)
	{
		if (enchant == NULL_ENCHANT)
			return false;

		getEnchantFile(enchant);

		if (Arrays.asList(Enchantment.values()).contains(enchant))
		{
			getEnchantDescriptionLines(enchant);
			return false;
		} else
		{
			try
			{
				Enchantment.registerEnchantment(enchant);
				getEnchantDescriptionLines(enchant);
				logger.log(Level.FINE, "Registered " + EnchantmentInfo.getKey(enchant).toUpperCase());
				return true;
			} catch (Exception e)
			{
				e.printStackTrace();
			}
			return false;
		}
	}

	/**
	 * Registers an enchant for use. Without registering an enchant, the enchant
	 * will stay unusable.
	 * 
	 * @param handler An {@link EnchantHandler} to handle the specified enchant.
	 * @return Returns if the registering was successful.
	 */
	public static boolean registerEnchant(EnchantHandler handler)
	{
		boolean success = registerEnchant(handler.getEnchant());
		if (handler != null)
			Events.addHandler(handler);

		return success;
	}

	public static EnchantFile getEnchantFile(Enchantment enchant)
	{
		return getEnchantFile(enchant, true);
	}

	public static EnchantFile getEnchantFile(Enchantment enchant, boolean autoCreateIfNull)
	{
		EnchantFile enchantFile = enchantmentFilesMap.get(enchant);

		if ((enchantFile == null || !enchantFile.exists()))
		{
			enchantFile = new EnchantFile(enchantsFolder, EnchantmentInfo.getKey(enchant) + ".yml");

			if (autoCreateIfNull)
				try
				{
					enchantFile.createNewFile();
				} catch (IOException e)
				{
					e.printStackTrace();
				}

			if (enchantFile.exists())
			{
				enchantFile.loadYaml(new YamlConfiguration());
				enchantmentFilesMap.put(enchant, enchantFile);
			}
		}
		return enchantFile;
	}

	/**
	 * Register enchants for use. Without registering an enchant, the enchant will
	 * stay unusable.
	 * 
	 * @param enchants The enchants you want to register.
	 * @return Returns if the all enchantments were successfully registered.
	 */
	public static boolean registerEnchants(Enchantment... enchants)
	{
		boolean success = true;
		for (Enchantment enchant : enchants)
			if (registerEnchant(enchant) == false)
				success = false;
		return success;
	}

	/**
	 * Creates a custom enchant with the specified namespace and name and returns it
	 * 
	 * @param namespace The hard-coded name of this enchant
	 * @return A custom enchant with the specified namespace and name
	 */
	public static Enchantment newEnchant(String namespace)
	{
		List<String> disabledEnchants = ConfigSettings.getDisabledEnchants();
		if (disabledEnchants.contains(namespace))
			return NULL_ENCHANT;
		return Sekai.getMCVersion() < 13 ? new EnchantWrapper.Legacy(namespace) : new EnchantWrapper(namespace);
	}

	/**
	 * Returns the enchant description formatted in an organised {@code List} of
	 * {@code String} used to write ItemMeta lore.
	 * 
	 * @param enchant Enchant to get description lines of
	 * @return Requested enchant description lines
	 */
	public static ArrayList<String> getEnchantDescriptionLines(Enchantment enchant)
	{
		ArrayList<String> cachedEnchantDescriptionLines = enchantDescriptionLinesMap.get(enchant);
		if (cachedEnchantDescriptionLines != null)
			return cachedEnchantDescriptionLines;

		EnchantmentInfo enchantmentInfo = EnchantmentInfo.getEnchantmentInfo(enchant);
		EnchantFile enchantFile = EPS.getEnchantFile(enchant, false);
		if (enchantFile != null && enchantFile.getEnchantDescription() != null)
			enchantmentInfo.description(enchantFile.getEnchantDescription());

		ArrayList<String> enchantDescriptionLines = new ArrayList<String>() {
			private static final long serialVersionUID = -5686650364578005499L;
			{
				add("");
				String description = enchantmentInfo.getDescription();
				if (description.length() > 120)
					for (int i = 0; i <= (description.length() / 90); i++)
					{
						String str = ChatColor.GRAY + description.substring(45 * i,
								45 * i + 45 > description.length() ? description.length() : 45 * i + 45);
						add(str);
						allDescriptionLines.add(str);
					}
				else
				{
					add(ChatColor.GRAY + description);
					allDescriptionLines.add(ChatColor.GRAY + description);
				}
				add("");
			}
		};

		enchantDescriptionLinesMap.put(enchant, enchantDescriptionLines);
		return enchantDescriptionLines;
	}

	public class PlaceholderAPIHook extends PlaceholderExpansion
	{
		@Override
		public boolean canRegister()
		{
			return true;
		}

		@Override
		public boolean register()
		{
			boolean result = super.register();
			if (result)
				logger.log(Level.INFO, "Added EPS PlaceholderExpansion: " + getIdentifier());
			return result;
		}

		@Override
		public String getAuthor()
		{
			return "vivisan";
		}

		@Override
		public String getIdentifier()
		{
			return "eps";
		}

		@Override
		public String getPlugin()
		{
			return "EnchantmentsPlusMinus";
		}

		@Override
		public String getVersion()
		{
			return EPS.plugin.getDescription().getVersion();
		}

		@Override
		public boolean persist() {
			return true;
		}
		
		@Override
		public String onPlaceholderRequest(Player p, String identifier)
		{
			return identifier.equals("tokens") ? Double.toString(EPS.getEconomy().getBalance(p))
					: (identifier.equals("tokens_formatted") ? EPS.abbreviate(EPS.getEconomy().getBalance(p)) : null);
		}
	}

	public static class EnchantMetaWriter
	{
		private static final String NEW_LINE_PLACEHOLDER = ChatColor.BLACK + "-";

		private static List<String> getEnchantLore(ItemStack itemStack)
		{
			ItemMeta itemMeta = itemStack.getItemMeta();
			if (itemMeta == null)
				return (new ArrayList<String>());
			if (!ConfigSettings.isShowEnchants())
				return itemMeta.getLore();
			List<String> currentLore = itemMeta.getLore() == null ? new ArrayList<String>() : itemMeta.getLore();

			for (String enchantName : oldEnchantNames)
				currentLore.removeIf(s -> (s != null && s.contains(enchantName)) || EPS.allDescriptionLines.contains(s)
						|| s.equals(NEW_LINE_PLACEHOLDER));

			for (Map.Entry<Enchantment, Integer> entry : itemMeta.getEnchants().entrySet())
			{
				EnchantmentInfo enchantmentInfo = EnchantmentInfo.getEnchantmentInfo(entry.getKey());

				String colorPrefix = ConfigSettings.getEnchantSpecificLoreColors().get(enchantmentInfo.key);
				StringBuilder titleBuilder = new StringBuilder(
						colorPrefix == null ? ConfigSettings.getEnchantLoreColor()
								: ChatColor.translateAlternateColorCodes('&', colorPrefix));
				titleBuilder.append(enchantmentInfo.getName());
				if (entry.getKey().getMaxLevel() > 1)
					titleBuilder.append(" " + getLevelLabel(entry.getValue()));

				if (ConfigSettings.isShowEnchantDescriptions())
				{
					List<String> enchantDescriptionLines = new ArrayList<String>(
							EPS.getEnchantDescriptionLines(entry.getKey()));
					for (int i = enchantDescriptionLines.size() - 1; i > -1; i--)
						if (enchantDescriptionLines.get(i) != "")
							currentLore.add(0, enchantDescriptionLines.get(i));
				}
				currentLore.add(0, titleBuilder.toString());
				if (ConfigSettings.isShowEnchantDescriptions())
					currentLore.add(0, NEW_LINE_PLACEHOLDER);
			}
			return currentLore;
		}

		/**
		 * Gets the modified ItemMeta of the ItemStack. Only lore is modified to match
		 * custom enchant lore.
		 * 
		 * @param item The item to modify
		 * @return The modified ItemMeta
		 */
		public static ItemMeta getWrittenMeta(ItemStack item)
		{
			if (!ConfigSettings.isShowEnchants())
				return item.getItemMeta();
			List<String> lore = EnchantMetaWriter.getEnchantLore(item);
			ItemMeta meta = item.getItemMeta();
			if (meta != null)
				if (lore != null)
				{
					meta.setLore(lore);
					meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
				}
			return meta;
		}

		public static String getLevelLabel(int level)
		{
			return ConfigSettings.isUseRomanNumerals() ? RomanNumeral.toRomanNumeral(level) : Integer.toString(level);
		}

		public static List<String> getWrittenEnchantLoreBook(ItemStack item)
		{
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
			if (meta == null)
				return (new ArrayList<String>());
			Map<Enchantment, Integer> map = meta.getStoredEnchants();
			List<String> list = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();

			if (!ConfigSettings.isShowEnchants())
				return list;

			Collection<Enchantment> enchants = Arrays.asList(Enchantment.values());

			for (Enchantment enchant : enchants)
			{
				for (int i = 0; i < list.size(); i++)
				{
					String s = list.get(i);
					if (s.split(" ").length > 1)
						if (s.startsWith(ChatColor.GRAY + EnchantmentInfo.getName(enchant)))
							list.remove(i);
					if (EPS.allDescriptionLines.contains(s))
						list.remove(i);
				}
			}

			for (Map.Entry<Enchantment, Integer> entry : map.entrySet())
			{
				if (enchants.contains(entry.getKey()))
				{
					String lore = ChatColor.GRAY + EnchantmentInfo.getName(entry.getKey()) + " "
							+ getLevelLabel(entry.getValue());
					list.add(0, lore);
				}
			}
			return list;
		}

		public static EnchantmentStorageMeta getWrittenMetaBook(ItemStack item)
		{
			if (!ConfigSettings.isShowEnchants())
				return (EnchantmentStorageMeta) item.getItemMeta();
			List<String> lore = EnchantMetaWriter.getWrittenEnchantLoreBook(item);
			EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
			if (meta != null)
				if (lore != null)
					meta.setLore(lore);
			meta.addItemFlags(ItemFlag.HIDE_POTION_EFFECTS);
			return meta;
		}

		public static void refreshItem(ItemStack item)
		{
			if (item == null)
				return;
			if (item.getType().equals(Material.ENCHANTED_BOOK))
				return;
			if (ConfigSettings.getLoreExemptions().contains(item.getType()))
				return;
			ItemMeta meta = getWrittenMeta(item);
			if (meta == null)
				return;
			if (meta.getLore() != item.getItemMeta().getLore())
				item.setItemMeta(meta);
		}
	}
}
