package org.whyisthisnecessary.eps.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.EnchantmentStorageMeta;
import org.bukkit.inventory.meta.ItemMeta;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.legacy.Label;
import org.whyisthisnecessary.eps.legacy.NameUtil;

public class EnchantMetaWriter implements Listener {
	
	protected static Map<Enchantment, String> enchantnames = new HashMap<Enchantment, String>();
	
	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(InventoryClickEvent e)
	{
		ItemStack item = e.getCurrentItem();
		if (item == null) return;
		if (item.getType().equals(Material.ENCHANTED_BOOK)) return;
		ItemMeta meta = getWrittenMeta(item);
		if (meta == null) return;
		if (meta.getLore() != item.getItemMeta().getLore())
			e.getCurrentItem().setItemMeta(meta);
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		if (item == null) return;
		if (item.getType().equals(Material.ENCHANTED_BOOK)) return;
		ItemMeta meta = getWrittenMeta(item);
		if (meta == null) return;
		if (meta.getLore() != item.getItemMeta().getLore())
			item.setItemMeta(meta);
	}
	
	@EventHandler
	public void onInventoryOpen(InventoryOpenEvent e)
	{
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		if (item == null) return;
		if (item.getType().equals(Material.ENCHANTED_BOOK)) return;
		ItemMeta meta = getWrittenMeta(item);
		if (meta == null) return;
		if (meta.getLore() != item.getItemMeta().getLore())
			item.setItemMeta(meta);
	}
	
	private static List<String> getWrittenEnchantLore(ItemStack item)
	{
		ItemMeta meta = item.getItemMeta();
		if (meta == null)
			return (new ArrayList<String>(Arrays.asList()));
		Map<Enchantment, Integer> map = meta.getEnchants();
		List<String> list = meta.getLore();
		
		if (Main.Config.getBoolean("show-enchant-lore") == false)
			return meta.getLore();
		
		if (list == null) list = new ArrayList<String>(Arrays.asList());
		
		Collection<Enchantment> enchants = Label.values();
		
		for (Enchantment enchant : enchants)
		{
			for (int i=0;i<list.size();i++)
			{
				String[] split = list.get(i).split(" ");
				int numIndex = split.length-1;
				String val = split[0];
				for (int v=1;v<numIndex;v++)
					val = val + " " + split[v];
				if (split.length < 2)
					continue;
				if (!val.equals(ChatColor.GRAY+enchantnames.get(enchant)))
					continue;
				if (!(isNumeric(split[numIndex])|| isRomanNumeral(split[numIndex])))
					continue;
				list.remove(i);
			}
		}
		
		for (Map.Entry<Enchantment,Integer> entry : map.entrySet())  
		{
			if (enchants.contains(entry.getKey()))
			{
				String name = enchantnames.get(entry.getKey());
				String lore = ChatColor.GRAY+name+" "+ getNumber(entry.getValue());
			    list.add(0, lore);
			}
		}
		return list;
	}
	
	/** Gets the modified ItemMeta of the ItemStack.
	 * Only lore is modified to match custom enchant lore.
	 * 
	 * @param item The item to modify
	 * @return The modified ItemMeta
	 */
	public static ItemMeta getWrittenMeta(ItemStack item)
	{
		if (Main.Config.getBoolean("show-enchant-lore") == false) return item.getItemMeta();
		List<String> lore = EnchantMetaWriter.getWrittenEnchantLore(item);
		ItemMeta meta = item.getItemMeta();
		if (meta != null)
		if (lore != null)
		meta.setLore(lore);
		return meta;
	}
	
	/** Checks if roman numerals are enabled, then gets the String of it.
	 * 
	 * @param num The number
	 * @return The String number
	 */
	public static String getNumber(Integer num)
	{
		if (Main.Config.getBoolean("use-roman-numerals") == false)
			return num.toString();
		else
			return getRomanNumeral(num);
	}
	
	/** Gets the roman numeral of the specified number.
	 * Only counts to ten.
	 * 
	 * @param num The number
	 * @return The roman numberal of the number
	 */
	public static String getRomanNumeral(Integer num)
	{
		switch (num)
		{
			case 1:
				return "I";
			case 2:
				return "II";
			case 3:
				return "III";
			case 4:
				return "IV";
			case 5:
				return "V";
			case 6:
				return "VI";
			case 7:
				return "VII";
			case 8:
				return "VIII";
			case 9:
				return "IX";
			case 10:
				return "X";
			default:
				return num.toString();
		}
	}
	
	/** Checks if the String is a roman numeral
	 * Only counts to ten.
	 * 
	 * @param num The number
	 * @return If the String is a roman numeral
	 */
	public static Boolean isRomanNumeral(String num)
	{
		switch (num)
		{
			case "I":
			case "II":
			case "III":
			case "IV":
			case "V":
			case "VI":
			case "VII":
			case "VIII":
			case "IX":
			case "X":
				return true;
			default:
				return false;
		}
	}
	
	private static boolean isNumeric(String strNum) 
	{
	    try
	    {
	    	Integer integer = Integer.parseInt(strNum);
	    	integer++;
	    	return true;
	    }
	    catch (NumberFormatException e)
	    {
	    	return false;
	    }
	}
	
	public static void registerEnchantNames()
	{
		for (Enchantment enchant : Label.values())
			enchantnames.put(enchant, WordUtils.capitalizeFully(NameUtil.getName(enchant).replaceAll("_", " ")));
	}
	
	public static List<String> getWrittenEnchantLoreBook(ItemStack item)
	{
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		if (meta == null)
			return (new ArrayList<String>(Arrays.asList()));
		Map<Enchantment, Integer> map = meta.getStoredEnchants();
		List<String> list = meta.getLore();
		
		if (Main.Config.getBoolean("show-enchant-lore") == false)
			return meta.getLore();
		
		if (list == null) list = new ArrayList<String>(Arrays.asList());
		
		Collection<Enchantment> enchants = Label.values();
		
		for (Enchantment enchant : enchants)
		{
			for (int i=0;i<list.size();i++)
			{
				String[] split = list.get(i).split(" ");
				int numIndex = split.length-1;
				String val = split[0];
				for (int v=1;v<numIndex;v++)
					val = val + " " + split[v];
				if (split.length < 2)
					continue;
				if (!val.equals(ChatColor.GRAY+enchantnames.get(enchant)))
					continue;
				if (!(isNumeric(split[numIndex])|| isRomanNumeral(split[numIndex])))
					continue;
				list.remove(i);
			}
		}
		
		for (Map.Entry<Enchantment,Integer> entry : map.entrySet())  
		{
			if (enchants.contains(entry.getKey()))
			{
				String name = enchantnames.get(entry.getKey());
				String lore = ChatColor.GRAY+name+" "+ getNumber(entry.getValue());
			    list.add(0, lore);
			}
		}
		return list;
	}
	
	public static EnchantmentStorageMeta getWrittenMetaBook(ItemStack item)
	{
		if (Main.Config.getBoolean("show-enchant-lore") == false) return (EnchantmentStorageMeta)item.getItemMeta();
		List<String> lore = EnchantMetaWriter.getWrittenEnchantLoreBook(item);
		EnchantmentStorageMeta meta = (EnchantmentStorageMeta) item.getItemMeta();
		if (meta != null)
		if (lore != null)
		meta.setLore(lore);
		return meta;
	}
}
