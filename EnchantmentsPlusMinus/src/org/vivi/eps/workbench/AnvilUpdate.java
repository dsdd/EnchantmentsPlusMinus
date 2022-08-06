package org.vivi.eps.workbench;

import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.eps.pvppack.Durability;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.visual.EnchantMetaWriter;

public class AnvilUpdate implements Listener, Reloadable {

	private boolean ace = EPS.configData.getBoolean("anvil-combining-enabled");
	
	public AnvilUpdate()
	{
		EPS.registerReloader(this);
	}
	
	@EventHandler
	public void onInventoryClick(PrepareAnvilEvent e)
	{
		if (ace == false)
			return;
		AnvilInventory anvil = e.getInventory();
		
		if (anvil.getItem(0) == null) return;
		
		ItemStack slot1 = anvil.getItem(0);
		ItemStack slot2 = anvil.getItem(1);
		ItemStack item = new ItemStack(slot1.getType(), slot1.getAmount());
		ItemMeta meta = item.getItemMeta();
		meta.setDisplayName(anvil.getRenameText());
		meta.setLore(slot1.getItemMeta().getLore());
		item.setItemMeta(meta);
		Durability dura1 = new Durability(item);
		Durability dura2 = new Durability(slot1);
		dura1.setDurability(dura2.getDurability());
		
		Map<Enchantment, Integer> enchantments = slot2 == null ? CustomEnchantedBook.getEnchants(slot1) : CustomEnchantedBook.combineEnchants(slot1, slot2, false);

		int cost = 1;
		for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
			int maxlevel = EPSConfiguration.getConfiguration(entry.getKey()).getInt("maxlevel");
			if (maxlevel != 0 && entry.getValue() > maxlevel)
			{
				item.addUnsafeEnchantment(entry.getKey(), maxlevel);
				cost = cost + maxlevel;
			}
			else
			{
				item.addUnsafeEnchantment(entry.getKey(), entry.getValue());
				cost = cost + entry.getValue();
			}
		}
		final int cost1 = cost;
		ItemMeta lore = EnchantMetaWriter.getWrittenMeta(item);
		item.setItemMeta(lore);
		e.setResult(item);
		Bukkit.getServer().getScheduler().runTask(EPS.plugin, () -> e.getInventory().setRepairCost(cost1));
	}

	@Override
	public void reload() {
		ace = EPS.configData.getBoolean("anvil-combining-enabled");
	}
}
