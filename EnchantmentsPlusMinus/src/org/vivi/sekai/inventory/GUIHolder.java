package org.vivi.sekai.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public class GUIHolder implements InventoryHolder {

	private Inventory inventory;
	
	public GUIHolder(Inventory inventory)
	{
		this.inventory = inventory;
	}
	
	@Override
	public Inventory getInventory() {
		return inventory;
	}
}
