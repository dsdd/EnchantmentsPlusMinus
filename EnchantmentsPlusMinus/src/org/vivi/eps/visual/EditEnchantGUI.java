package org.vivi.eps.visual;

import java.util.Arrays;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.MemorySection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.util.Language;

public class EditEnchantGUI implements Listener {

	private Inventory inv = null;
	private Player player = null;
	private boolean editing = false;
	private String key = null;
	private EPSConfiguration config = null;
	
	public EditEnchantGUI(Player player, Enchantment enchant)
	{
		Bukkit.getPluginManager().registerEvents(this, EPS.plugin);
		this.player = player;
        config = EPSConfiguration.getConfiguration(enchant);
		Map<String, Object> entries = config.getValues(true);
		int size = (int) (Math.ceil((double)entries.size()/9)*9);
		inv = Bukkit.createInventory(null, size, "Modifying "+EPS.getDictionary().getName(enchant).toUpperCase());
		for (Map.Entry<String, Object> entry : entries.entrySet())
		{
			if (entry.getValue() instanceof MemorySection)
				continue;
			
			ItemStack i = new ItemStack(Material.BOOK, 1);
			ItemMeta meta = i.getItemMeta();
			meta.setDisplayName(ChatColor.GREEN+entry.getKey().substring(0,1).toUpperCase()+entry.getKey().substring(1).toLowerCase().replace(".", ": "));
			meta.setLore(Arrays.asList(ChatColor.DARK_GREEN+entry.getValue().toString(), ChatColor.BLACK+entry.getKey()));
			i.setItemMeta(meta);
			inv.addItem(i);
		}
		player.openInventory(inv);
	}
	
	@EventHandler
	public void onInventoryClick(InventoryClickEvent e)
	{
		if (e.getClickedInventory() != e.getInventory())
			return;
		
		if (!isSimilarInventory(inv, e.getInventory()))
			return;
		
		ItemStack clickedItem = e.getCurrentItem();
		
		if (clickedItem == null || clickedItem.getType() == Material.AIR)
			return;
		
		player.closeInventory();
		key = clickedItem.getItemMeta().getLore().get(1).replace("�0", "");
		player.sendMessage(Language.getLangMessage("modifying-config").replace("%entry%", key));
		editing = true;
	}
	
	@EventHandler
	public void onChat(AsyncPlayerChatEvent e)
	{
		if (e.getPlayer() == player && editing)
		{
			e.setCancelled(true);
			if (!e.getMessage().equals("cancel")) // Not using ignore case just in case ;)
			{
				config.set(key, toObject(e.getMessage()));
				config.save();
				EPS.reloadConfigs();
				Language.sendMessage(player, "modified-config");
				Bukkit.getScheduler().runTask(EPS.plugin, new Runnable()
						{
							@Override
							public void run() {
								EnchantGUI.openInventory(player, EnchantGUI.guiNames.get(player));
								
							}
						});
			}
		}
	}
	
	public static boolean isSimilarInventory(Inventory first, Inventory second)
	{
	    if(first == null && second == null) return true;
	    if((first == null && second != null) || (first != null && second == null)) return false;
	    if(first.getType() != second.getType()) return false;
	    ItemStack[] firstContents = first.getContents();
	    ItemStack[] secondContents = second.getContents();
	    if(firstContents.length != secondContents.length) return false;
	    for(int i = 0; i < firstContents.length; i++)
	    {
	        if(firstContents[i] == null && secondContents[i] == null) continue;
	        else if(firstContents[i] == null && secondContents[i] != null) return false;
	        else if(secondContents[i] == null && firstContents[i] != null) return false;
	        else if(!firstContents[i].isSimilar(secondContents[i])) return false;
	    }
	    return true;
	}
	
	private static Object toObject(String s)
	{
		if (isNumeric(s))
			return Double.parseDouble(s);
		else if (isBoolean(s))
			return Boolean.parseBoolean(s);
		else if (s.length() == 1)
			return s.charAt(0);
		else
			return s;
	}
	
	private static boolean isNumeric(String s) 
	{ 
		  try {  
		    Double.parseDouble(s);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
	}
	
	private static boolean isBoolean(String s) 
	{ 
		  try {  
		    Boolean.parseBoolean(s);  
		    return true;
		  } catch(NumberFormatException e){  
		    return false;  
		  }  
	}
}
