package org.vivi.sekai.inventory;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.vivi.sekai.Sekai;

public class GUIBuilder implements Listener
{

	private static final Set<GUIBuilder> GUI_BUILDERS = new HashSet<GUIBuilder>();

	private Inventory inventory;
	private boolean isWritable = true;

	private GUIBuilder(Inventory inventory)
	{
		this.inventory = inventory;
	}

	public static GUIBuilder build(Inventory inventory)
	{
		for (GUIBuilder guiBuilder : GUI_BUILDERS)
			if (Sekai.isSameInventory(guiBuilder.inventory, inventory))
				return guiBuilder;
		return new GUIBuilder(inventory);
	}

	public static GUIBuilder build(InventoryHolder owner, int size, String title)
	{
		return build(Bukkit.createInventory(owner, size, title));
	}

	public Inventory toInventory()
	{
		return inventory;
	}

	public GUIBuilder registerEvents(Plugin plugin)
	{
		Bukkit.getPluginManager().registerEvents(this, plugin);
		return this;
	}

	public GUIBuilder clear()
	{
		inventory.clear();
		return this;
	}

	public GUIBuilder constructSquare(ItemStack itemStack, int derive, int length, boolean hollow)
	{
		for (int x = 0; x < length; x++)
			for (int y = 0; y < length; y++)
				if (!hollow || (x == length - 1 || x == 0 || y == length - 1 || y == 0))
					inventory.setItem(x + y * 9 + derive, itemStack);

		return this;
	}

	public GUIBuilder constructBorder(ItemStack itemStack)
	{
		int rows = inventory.getSize() / 9;
		for (int i = 0; i < 9; i++)
		{
			inventory.setItem(i, itemStack);
			inventory.setItem(i + inventory.getSize() - 9, itemStack);
		}

		for (int i = 1; i < rows; i++)
		{
			inventory.setItem((9 * i) - 1, itemStack);
			inventory.setItem((9 * i) - 9, itemStack);
		}
		return this;
	}

	public GUIBuilder setSlot(int slot, ItemStack itemStack)
	{
		inventory.setItem(slot, itemStack);
		return this;
	}

	/**
	 * Clears the {@code Inventory} and fills all slots with the specified
	 * {@code ItemStack}.
	 * 
	 * @param itemStack Item to be used to fill the {@code Inventory}
	 * @return Self
	 */
	public GUIBuilder fill(ItemStack itemStack)
	{
		for (int i = 0; i < inventory.getSize(); i++)
			inventory.setItem(i, itemStack);
		return this;
	}

	public GUIBuilder setWritable(boolean isWritable)
	{
		this.isWritable = isWritable;
		return this;
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryClick(final InventoryClickEvent e) throws IOException
	{
		if (isWritable || !Sekai.isSameInventory(e.getInventory(), inventory))
			return;

		if (e.getClickedInventory() != e.getInventory() && e.getAction() != InventoryAction.MOVE_TO_OTHER_INVENTORY)
			return;

		e.setCancelled(true);
	}

	@EventHandler(priority = EventPriority.HIGH)
	public void onInventoryDrag(final InventoryDragEvent e)
	{
		if (!isWritable && Sekai.isSameInventory(e.getView().getTopInventory(), inventory))
			e.setCancelled(true);
	}
}
