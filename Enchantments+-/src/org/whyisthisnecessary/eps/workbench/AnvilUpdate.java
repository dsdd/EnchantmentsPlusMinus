package org.whyisthisnecessary.eps.workbench;

import java.util.Map;

import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.legacy.NameUtil;
import org.whyisthisnecessary.eps.visual.EnchantMetaWriter;

public class AnvilUpdate implements Listener {

	@EventHandler
	public void onInventoryClick(PrepareAnvilEvent e)
	{
		AnvilInventory anvil = e.getInventory();
		
		if (anvil.getItem(0) == null) return;
		
		ItemStack slot1 = anvil.getItem(0);
		ItemStack slot2 = anvil.getItem(1);
		if (slot2 == null)
		{
			anvil.setItem(2, slot1);
		}
		else
		{
			ItemStack item = new ItemStack(slot1.getType(), slot1.getAmount());	
			ItemMeta meta = item.getItemMeta();
			meta.setDisplayName(slot1.getItemMeta().getDisplayName());
			item.setItemMeta(meta);
			
			Map<Enchantment, Integer> map = CustomEnchantedBook.combineEnchants(slot1, slot2, false);
			for (Map.Entry<Enchantment, Integer> entry : map.entrySet())
			{
				int maxlevel = Main.Config.getInt("enchants."+NameUtil.getName(entry.getKey())+".maxlevel");
				if (maxlevel != 0 && entry.getValue() > maxlevel)
					item.addUnsafeEnchantment(entry.getKey(), maxlevel);
				else
					item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
			}
			ItemMeta lore = EnchantMetaWriter.getWrittenMeta(item);
			item.setItemMeta(lore);
			
			anvil.setItem(2, item);
		}
	}
}
