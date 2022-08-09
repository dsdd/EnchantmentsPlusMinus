package org.vivi.eps.util;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.enchantments.EnchantmentTarget;
import org.bukkit.inventory.ItemStack;
import org.vivi.eps.EPS;

public class Wrapper extends Enchantment {
	
	private static final Dictionary dictionary = EPS.getDictionary();
	private final String name;
	private final List<Enchantment> incompatibilities = new ArrayList<Enchantment>();
	
	public Wrapper(String namespace, String name) 
	{
		super(NamespacedKey.minecraft(namespace));
		this.name = name;
	}

	// Defaults to true for now.
	@Override
	public boolean canEnchantItem(ItemStack arg0) 
	{
		return true;
	}

	@Override
	public boolean conflictsWith(Enchantment arg0) 
	{
		for (Enchantment enchant : incompatibilities)
			if (enchant.equals(arg0))
				return true;
		
		for (String key : EPS.incompatibilitiesData.getKeys(false))
		{
			List<String> incompatibilities = EPS.incompatibilitiesData.getStringList(key);
			boolean contains = false;
			for (String enchantName : incompatibilities)
				if (enchantName.equalsIgnoreCase(dictionary.getName(this)))
				{
					contains = true;
					break;
				}
			if (contains)
				for (String enchantName : incompatibilities)
					if (enchantName.equalsIgnoreCase(dictionary.getName(arg0)))
					{
						this.incompatibilities.add(arg0);
						return true;
					}
						
			
		}
		return false;
	}

	@Override
	public EnchantmentTarget getItemTarget() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public int getMaxLevel() {
		// TODO Auto-generated method stub
		return 32767;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name;
	}

	@Override
	public int getStartLevel() {
		// TODO Auto-generated method stub
		return 0;
	}

	public boolean isCursed() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isTreasure() {
		// TODO Auto-generated method stub
		return false;
	}
}
