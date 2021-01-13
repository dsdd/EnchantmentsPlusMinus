package org.eps.pickaxepack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.dependencies.VaultHook;
import org.whyisthisnecessary.eps.legacy.LegacyUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

public class EnchantProcessor implements Listener {
	
	private boolean allowenchantmods = true;
	private boolean enchantprocessed = false;
	private Collection<Location> saves;
	private Random temp = new Random();
	private static Material endframe = null;
	
    public EnchantProcessor(Plugin plugin)
    {
    	Bukkit.getPluginManager().registerEvents(this, plugin);
    	saves = new ArrayList<Location>(Arrays.asList());
    	if (LegacyUtil.isLegacy())
    		endframe = Material.matchMaterial("ENDER_PORTAL_FRAME");
    	else
    		endframe = Material.matchMaterial("END_PORTAL_FRAME");
    }
    
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (!allowenchantmods)
			return;
		if (e.getPlayer().getInventory().getItemInMainHand() == null)
            return;
		if (!e.getPlayer().getInventory().getItemInMainHand().hasItemMeta())
		    return;
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR)
			return;
		if (e.getPlayer().getInventory().firstEmpty() == -1) {
			e.getPlayer().sendMessage(LangUtil.getLangMessage("inventoryfull"));
            return; }
		if (e.getBlock().getState() instanceof Container)
            return;
		enchantprocessed = false;
		Collection<ItemStack> drops = getDrops(e, e.getBlock());
		
		
		afterBlockBreak(e, 1);
        if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(Enchantment.LOOT_BONUS_BLOCKS))
        	enchantprocessed = true;
		
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.EXPLOSIVE))
		{
			if (e.isCancelled()) return;
			allowenchantmods = false;
			enchantprocessed = true;
			Integer enchlvl = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.EXPLOSIVE);
		    
		    
			int count = 0;
			if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.EXPLOSIVE, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.EXPLOSIVE), "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = ((int)Math.cbrt(enchlvl))/2+1;
					Block[] area = sphere(loc, radius);
					count = 0;
					loc.getWorld().createExplosion(loc, 0F);
					for (Block block : area) {
					    if (!(block.getType() == Material.BEDROCK || block.getType() == endframe))
					    {
					        if (count > enchlvl*2) break;
					        ++count;
					        BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
					        Bukkit.getServer().getPluginManager().callEvent(newevent);
					        if (!newevent.isCancelled()) {
					        Collection<ItemStack> exdrops = getDrops(e, block);
					        e.getPlayer().giveExp(getExp(block));
					        block.setType(Material.AIR);
					        ItemStack[] exdropsfinal = new ItemStack[exdrops.size()];
							exdropsfinal = exdrops.toArray(exdropsfinal);
							for (int i2=0;i2<exdropsfinal.length;i2++)
							{
					            drops.add(exdropsfinal[i2]);
							}
					        }
					    }
					    
					}
					afterBlockBreak(e, count);
			}
			allowenchantmods = true;
		}
		
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.EXCAVATE))
		{
			if (e.isCancelled()) return;
			allowenchantmods = false;
			enchantprocessed = true;
			Integer enchlvl = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.EXCAVATE);
		    
		    
			int count = 0;
			if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.EXCAVATE, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.EXCAVATE), "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = ((int)Math.cbrt(enchlvl))/2+1;
					List<Block> area = new ArrayList<Block>(Arrays.asList());
					World world = e.getBlock().getWorld();
					for (int x=-radius;x<radius+1;x++)
						for (int y=-radius;y<radius+1;y++)
								for (int z=-radius;z<radius+1;z++)
									area.add(world.getBlockAt(new Location(world,x+loc.getBlockX(),y+loc.getBlockY(),z+loc.getBlockZ())));
							count = 0;
					for (Block block : area) {
					    if (!(block.getType() == Material.BEDROCK || block.getType() == endframe))
					    {
					        if (count > enchlvl*2) break;
					        ++count;
					        BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
					        Bukkit.getServer().getPluginManager().callEvent(newevent);
					        if (!newevent.isCancelled()) {
					        Collection<ItemStack> exdrops = getDrops(e, block);
					        e.getPlayer().giveExp(getExp(block));
					        block.setType(Material.AIR);
					        ItemStack[] exdropsfinal = new ItemStack[exdrops.size()];
							exdropsfinal = exdrops.toArray(exdropsfinal);
							for (int i2=0;i2<exdropsfinal.length;i2++)
							{
					            drops.add(exdropsfinal[i2]);
							}
					        }
					    }
					    
					}
					afterBlockBreak(e, count);
			}
			allowenchantmods = true;
		}
		
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.DIAMOND))
		{
			if (e.isCancelled()) return;
			allowenchantmods = false;
			enchantprocessed = true;
			Integer enchlvl = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.DIAMOND);
		    
		    
			int count = 0;
			if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.DIAMOND, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.DIAMOND), "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = ((int)Math.cbrt(enchlvl))/2+1;
					Block[] area = diamond(loc, radius);
					count = 0;
					for (Block block : area) {
					    if (!(block.getType() == Material.BEDROCK || block.getType() == endframe))
					    {
					        if (count > enchlvl*2) break;
					        ++count;
					        BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
					        Bukkit.getServer().getPluginManager().callEvent(newevent);
					        if (!newevent.isCancelled()) {
					        Collection<ItemStack> exdrops = getDrops(e, block);
					        e.getPlayer().giveExp(getExp(block));
					        block.setType(Material.AIR);
					        ItemStack[] exdropsfinal = new ItemStack[exdrops.size()];
							exdropsfinal = exdrops.toArray(exdropsfinal);
							for (int i2=0;i2<exdropsfinal.length;i2++)
							{
					            drops.add(exdropsfinal[i2]);
							}
					        }
					    }
					    
					}
					afterBlockBreak(e, count);
			}
			allowenchantmods = true;
		}
		
		ItemStack[] dropsfinal = new ItemStack[drops.size()];
		dropsfinal = drops.toArray(dropsfinal);
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.TELEPATHY))
		{
			Integer lvl1 = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TELEPATHY);
			if (lvl1 > 0) {
		        if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.TELEPATHY, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TELEPATHY), "chance"))
		        {
		        	e.setDropItems(false);
		        	for (ItemStack drop : dropsfinal) {
						e.getPlayer().getInventory().addItem(drop); }
		        	enchantprocessed = false;
		            return;
		        }
			}
		}
		if (enchantprocessed == true)
		{
		e.setDropItems(false);
		for (ItemStack drop : dropsfinal)
			e.getBlock().getWorld().dropItemNaturally(e.getBlock().getLocation(), drop);
		}
		enchantprocessed = false;
	}
	
	public Collection<ItemStack> getDrops(BlockBreakEvent e, Block block)
	{
		ItemStack pitem = e.getPlayer().getInventory().getItemInMainHand();
		ItemStack[] drops = new ItemStack[block.getDrops(pitem).size()];
		drops = block.getDrops(pitem).toArray(drops);
		Collection<ItemStack> dropfinal = new ArrayList<ItemStack>(Arrays.asList());
		Integer lvl = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(Enchantment.LOOT_BONUS_BLOCKS);
		Integer lvl1 = e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.AUTOSMELT);
		
		for (ItemStack drop : drops)
        {
			Material m = drop.getType();
		    if (getFortuneDrops().contains(m) && !(saves.contains(block.getLocation()))) {
		    	enchantprocessed = true;
		    	drop.setAmount(getDropCount(lvl, new Random())); }
		    if (lvl1 > 0 && getNext() < ConfigUtil.getAutofilledDouble(CustomEnchants.AUTOSMELT, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.AUTOSMELT), "chance"))
		    {
		    	enchantprocessed = true;
                if (m == Material.IRON_ORE)
                drop.setType(Material.IRON_INGOT);
                    
                if (m == Material.GOLD_ORE)
                drop.setType(Material.GOLD_INGOT);
                    
                if (m == Material.matchMaterial("ANCIENT_DEBRIS"))
                drop.setType(Material.matchMaterial("NETHERITE_SCRAP"));
                    
                if (m == Material.COBBLESTONE)
                drop.setType(Material.STONE);
		    }
		    dropfinal.add(drop);
        }
		return dropfinal;
	}
	
	public void afterBlockBreak(BlockBreakEvent e, Integer multi)
	{
		ItemStack a = e.getPlayer().getInventory().getItemInMainHand();
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.HASTE))
		{
			if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.HASTE, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.HASTE), "chance"))
			{
				e.getPlayer().sendMessage(LangUtil.getLangMessage("hasteactivate"));
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.HASTE)-1));
			}
		}
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.TOKENBLOCKS))
		{
			if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.TOKENBLOCKS, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TOKENBLOCKS), "chance"))
			{
				double explicit = ConfigUtil.getAutofilledDouble(CustomEnchants.TOKENBLOCKS,a.getEnchantmentLevel(CustomEnchants.TOKENBLOCKS), "tokens");
				int val = (int) explicit;
				e.getPlayer().sendMessage(LangUtil.getLangMessage("tokenblocksactivate").replaceAll("%tokens%", Integer.toString(val)));
				TokenUtil.changeTokens(e.getPlayer(), val);
			}
		}
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.MONEYBLOCKS))
		{
			if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.MONEYBLOCKS, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TOKENBLOCKS), "chance"))
			{
				if (PackMain.VaultEnabled)
				{
					double explicit = ConfigUtil.getAutofilledDouble(CustomEnchants.MONEYBLOCKS,a.getEnchantmentLevel(CustomEnchants.MONEYBLOCKS), "money");
					int val = (int) explicit;
					e.getPlayer().sendMessage(LangUtil.getLangMessage("moneyblocksactivate").replaceAll("%money%", Integer.toString(val)));
					VaultHook.getEconomy().depositPlayer(e.getPlayer(), val);
				}
				else
				{
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You must have Vault to use money enchants!");
				}
			}
		}
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.CHARITY))
		{
			if (PackMain.VaultEnabled)
			{
				if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.CHARITY, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.CHARITY), "chance"))
				{
				double explicit = ConfigUtil.getAutofilledDouble(CustomEnchants.CHARITY,a.getEnchantmentLevel(CustomEnchants.CHARITY), "money");
				int val = (int) explicit;
				Bukkit.broadcastMessage(LangUtil.getLangMessage("charityactivate").replaceAll("%money%", Integer.toString(val)).replaceAll("%player%", e.getPlayer().getDisplayName()));
				for (Player p : Bukkit.getOnlinePlayers())
				VaultHook.getEconomy().depositPlayer(p, val);
				}
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You must have Vault to use money enchants!");
			}
		}
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.TOKENCHARITY))
		{
			if (getNext() <= ConfigUtil.getAutofilledDouble(CustomEnchants.TOKENCHARITY, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TOKENCHARITY), "chance"))
			{
			double explicit = ConfigUtil.getAutofilledDouble(CustomEnchants.TOKENCHARITY,a.getEnchantmentLevel(CustomEnchants.TOKENCHARITY), "tokens");
			int val = (int) explicit;
			Bukkit.broadcastMessage(LangUtil.getLangMessage("tokencharityactivate").replaceAll("%tokens%", Integer.toString(val)).replaceAll("%player%", e.getPlayer().getDisplayName()));
			for (Player p : Bukkit.getOnlinePlayers())
			TokenUtil.changeTokens(p, val);
			}
		}
	}
	
	public Block[] sphere(final Location center, int radius) {
		radius = radius + 1;
	    ArrayList<Block> sphere = new ArrayList<Block>();
	    for (int Y = -radius; Y < radius; Y++) {
	      for (int X = -radius; X < radius; X++) {
	         for (int Z = -radius; Z < radius; Z++) {
	            if (Math.sqrt((X * X) + (Y * Y) + (Z * Z)) <= radius) {
	               final Block block = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(), Z + center.getBlockZ());
	               sphere.add(block);
	            }
	         }
	      }
	    }
	return sphere.toArray(new Block[sphere.size()]);
	}
	
	public Block[] diamond(final Location center, int radius)
	{
		ArrayList<Block> diamond = new ArrayList<Block>();
		for (int Y = -radius; Y < radius+1; Y++)
		{
			int r = radius - Math.abs(Y);
			for (int X = -radius; X < radius+1; X++)
			{
				for (int Z = -radius; Z < radius+1; Z++)
				{
					if (X > -r && X < r && Z > -r && Z < r)
					{	
						if (Math.abs(Y)+1 != radius)
						if (Math.abs(X) == r-1 && Math.abs(Z) == r-1) continue;
						
						Block block = center.getWorld().getBlockAt(X + center.getBlockX(), Y + center.getBlockY(), Z + center.getBlockZ());
						diamond.add(block);
					}
				}
			}
		}
		return diamond.toArray(new Block[diamond.size()]);
	}
	
	public int getDropCount(int i, Random random) {
        int j = random.nextInt(i + 2) - 1;
 
        if (j < 0) {
            j = 0;
        }
 
        return (j + 1);
    }
	
	@SuppressWarnings("unchecked")
	public List<Material> getFortuneDrops()
    {
    	List<Material> fortuneapply = new ArrayList<Material>(Arrays.asList());
    	List<String> list;
    	list = (List<String>) ConfigUtil.getMiscKey("applyfortuneon");
    	for (String i : list)
    	fortuneapply.add(Material.getMaterial(i));
    	return fortuneapply;
    }
	
	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		saves.add(e.getBlock().getLocation());
	}
	
	public int getExp(Block block)
	{
		switch (block.getType())
		{
			case COAL_ORE:
				return temp.nextInt(3);
			case REDSTONE_ORE:
				return temp.nextInt(4)+1;
			case LAPIS_ORE:
				return temp.nextInt(4)+2;
			case DIAMOND_ORE:
				return temp.nextInt(5)+3;
			case EMERALD_ORE:
				return temp.nextInt(5)+3;
			default:
				return 0;
		}
	}
	
	public double getNext()
	{
		return temp.nextDouble()*100;
	}
	
}
