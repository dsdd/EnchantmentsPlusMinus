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
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.visual.EnchantMetaWriter;

public class AnvilUpdate implements Listener {

	@EventHandler
	public void onInventoryClick(PrepareAnvilEvent e)
	{
		if (Main.Config.getBoolean("anvil-combining-enabled") == false)
			return;
		AnvilInventory anvil = e.getInventory();
		
		if (anvil.getItem(0) == null || anvil.getRepairCost() == 0) return;
		
		ItemStack slot1 = anvil.getItem(0);
		ItemStack slot2 = anvil.getItem(1);
		ItemStack item = new ItemStack(slot1.getType(), slot1.getAmount());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(anvil.getRenameText());
		item.setItemMeta(meta);
		
		Map<Enchantment, Integer> enchantments;

		if (slot2 == null) {
			enchantments = CustomEnchantedBook.getEnchants(slot1);
		} else {
			enchantments = CustomEnchantedBook.combineEnchants(slot1, slot2, false);
		}
		
		for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			int maxlevel = ConfigUtil.getInt(entry.getKey(), "maxlevel");
			if (maxlevel != 0 && entry.getValue() > maxlevel)
				item.addUnsafeEnchantment(entry.getKey(), maxlevel);
			else
				item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
		}
		
		ItemMeta lore = EnchantMetaWriter.getWrittenMeta(item);
		item.setItemMeta(lore);
		e.setResult(item);
	}
}
