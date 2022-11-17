package org.vivi.sekai.inventory;

import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class GUIBuilder {

	private Inventory inventory;
	
	public GUIBuilder(Inventory inventory)
	{
		this.inventory = inventory;
	}
	
	public GUIBuilder clear()
	{
		inventory.clear();
		return this;
	}
	
	public GUIBuilder constructSquare(ItemStack item, int derive, int length, boolean hollow)
	{
		for (int x=0;x<length;x++)
			for (int y=0;y<length;y++)
				if (!hollow || (x==length-1 || x==0 || y==length-1 || y==0))
					inventory.setItem(x+y*9+derive, item);
					
		return this;
	}
	
	public GUIBuilder constructBorder(ItemStack item)
	{
		int rows = inventory.getSize()/9;
		for (int i=0;i<9;i++)
		{
			inventory.setItem(i, item);
			inventory.setItem(i+inventory.getSize()-9, item);
		}
			
		for (int i=1;i<rows;i++)
		{
			inventory.setItem((9*i)-1, item);
			inventory.setItem((9*i)-9, item);
		}
		return this;
	}
	
	public Inventory toInventory()
	{
		return inventory;
	}
}
