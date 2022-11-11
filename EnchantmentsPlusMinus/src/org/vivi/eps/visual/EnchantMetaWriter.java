package org.vivi.eps.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EnchantFile;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.sekai.Sekai;
import org.vivi.sekai.enchantment.EnchantmentInfo;

public class EnchantMetaWriter implements Reloadable
{

	private static Map<Enchantment, ArrayList<String>> descriptionMap = new HashMap<>();
	private static List<String> allDescriptionLines = new ArrayList<String>();
	private static String prefix;
	private static List<Material> exemptions = new ArrayList<Material>();

	public EnchantMetaWriter()
	{
		prefix = ChatColor.translateAlternateColorCodes('&', ConfigSettings.getEnchantLoreColor());

		for (String s : ConfigSettings.getLoreExemptions())
			exemptions.add(Material.matchMaterial(s));
	}

	private static List<String> getWrittenEnchantLore(ItemStack item)
	{
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			return (new ArrayList<String>());
		if (!ConfigSettings.isShowEnchants())
			return meta.getLore();
		Map<Enchantment, Integer> map = meta.getEnchants();
		List<String> list = meta.getLore() == null ? new ArrayList<String>() : meta.getLore();
		Collection<Enchantment> enchants = Arrays.asList(Enchantment.values());

		for (Enchantment enchant : enchants)
		{
			for (int i = 0; i < list.size(); i++)
			{
				String s = list.get(i);
				if ((s != null && s.split(" ").length > 0 && s.contains(EnchantmentInfo.getDefaultName(enchant)))
						|| allDescriptionLines.contains(s) || s.equals(ChatColor.BLACK + "-"))
					list.remove(i);
			}
		}

		for (Map.Entry<Enchantment, Integer> entry : map.entrySet())
		{
			if (enchants.contains(entry.getKey()))
			{
				EnchantmentInfo enchantmentInfo = EnchantmentInfo.getEnchantmentInfo(entry.getKey());
				EnchantFile enchantFile = EPS.getEnchantFile(entry.getKey());
				String lore = (entry.getKey().getMaxLevel() < 2
						|| (enchantFile != null && enchantFile.getMaxLevel() < 2) ? enchantmentInfo.defaultName
								: enchantmentInfo.defaultName + " " + getNumber(entry.getValue()));
				String colorPrefix = ConfigSettings.getEnchantSpecificLoreColors().get(enchantmentInfo.key);
				lore = colorPrefix == null ? prefix + lore
						: ChatColor.translateAlternateColorCodes('&', colorPrefix) + lore;
				if (ConfigSettings.isShowEnchantDescriptions())
				{
					List<String> l = getDescription(entry.getKey());
					for (int i = l.size() - 1; i > -1; i--)
						if (l.get(i) != "")
							list.add(0, l.get(i));
				}
				list.add(0, lore);
				if (ConfigSettings.isShowEnchantDescriptions())
					list.add(0, ChatColor.BLACK + "-");
			}
		}
		return list;
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
		List<String> lore = EnchantMetaWriter.getWrittenEnchantLore(item);
		ItemMeta meta = item.getItemMeta();
		if (meta != null)
			if (lore != null)
			{
				meta.setLore(lore);
				meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
			}
		return meta;
	}

	/**
	 * Checks if roman numerals are enabled, then gets the String of it.
	 * 
	 * @param num The number
	 * @return The String number
	 */
	public static String getNumber(Integer num)
	{
		return ConfigSettings.isUseRomanNumerals() ? Sekai.getRomanNumeral(num) : num.toString();
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
					if (s.startsWith(ChatColor.GRAY + EnchantmentInfo.getDefaultName(enchant)))
						list.remove(i);
				if (allDescriptionLines.contains(s))
					list.remove(i);
			}
		}

		for (Map.Entry<Enchantment, Integer> entry : map.entrySet())
		{
			if (enchants.contains(entry.getKey()))
			{
				String lore = ChatColor.GRAY + EnchantmentInfo.getDefaultName(entry.getKey()) + " "
						+ getNumber(entry.getValue());
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

	@SuppressWarnings("unchecked")
	protected static List<String> getDescription(Enchantment enchant)
	{
		ArrayList<String> o = descriptionMap.get(enchant);
		return o == null ? new ArrayList<String>() : (ArrayList<String>) o.clone();
	}

	public static void refreshItem(ItemStack item)
	{
		if (item == null)
			return;
		if (item.getType().equals(Material.ENCHANTED_BOOK))
			return;
		if (exemptions.contains(item.getType()))
			return;
		ItemMeta meta = getWrittenMeta(item);
		if (meta == null)
			return;
		if (meta.getLore() != item.getItemMeta().getLore())
			item.setItemMeta(meta);
	}

	public static void init(Enchantment enchant)
	{
		EnchantmentInfo enchantmentInfo = EnchantmentInfo.getEnchantmentInfo(enchant);
		EnchantFile enchantFile = EPS.getEnchantFile(enchant);
		final String desc = enchantFile.getUpgradeDescription() == null ? enchantmentInfo.defaultDescription
				: enchantFile.getUpgradeDescription();
		descriptionMap.put(enchant, new ArrayList<String>() {
			private static final long serialVersionUID = -5686650364578005499L;
			{
				add("");
				if (desc.length() > 120)
					for (int i = 0; i <= (desc.length() / 90); i++)
					{
						String str = ChatColor.GRAY
								+ desc.substring(45 * i, 45 * i + 45 > desc.length() ? desc.length() : 45 * i + 45);
						add(str);
						allDescriptionLines.add(str);
					}
				else
				{
					add(ChatColor.GRAY + desc);
					allDescriptionLines.add(ChatColor.GRAY + desc);
				}
				add("");
			}
		});
	}

	@Override
	public void reload()
	{
		for (String s : ConfigSettings.getLoreExemptions())
			exemptions.add(Material.matchMaterial(s));
		for (Enchantment enchant : Enchantment.values())
		{
			EnchantmentInfo enchantmentInfo = EnchantmentInfo.getEnchantmentInfo(enchant);
			EnchantFile enchantFile = EPS.getEnchantFile(enchant);
			final String desc = enchantFile.getUpgradeDescription() == null ? enchantmentInfo.defaultDescription
					: enchantFile.getUpgradeDescription();

			descriptionMap.put(enchant, new ArrayList<String>() {
				private static final long serialVersionUID = -5686650364578005499L;
				{
					add("");
					if (desc.length() > 120)
						for (int i = 0; i <= (desc.length() / 90); i++)
						{
							String str = ChatColor.GRAY
									+ desc.substring(45 * i, 45 * i + 45 > desc.length() ? desc.length() : 45 * i + 45);
							add(str);
							allDescriptionLines.add(str);
						}
					else
					{
						add(ChatColor.GRAY + desc);
						allDescriptionLines.add(ChatColor.GRAY + desc);
					}
					add("");
				}
			});
		}
	}
}
