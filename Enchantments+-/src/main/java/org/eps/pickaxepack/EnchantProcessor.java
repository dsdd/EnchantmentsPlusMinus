package org.eps.pickaxepack;

import java.util.ArrayList;
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
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.whyisthisnecessary.eps.EPS;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.EPSConfiguration;
import org.whyisthisnecessary.eps.api.Reloadable;
import org.whyisthisnecessary.eps.dependencies.VaultHook;
import org.whyisthisnecessary.eps.economy.Economy;
import org.whyisthisnecessary.eps.util.LangUtil;

public class EnchantProcessor implements Listener, Reloadable {
	
	private boolean allowenchantmods = true;
	private boolean enchantprocessed = false;
	private Collection<Location> saves = new ArrayList<Location>();
	private Random temp = new Random();
	private Material endframe = EPS.onLegacy() ? Material.matchMaterial("ENDER_PORTAL_FRAME") : Material.matchMaterial("END_PORTAL_FRAME");
	private Economy economy = EPS.getEconomy();
	
    public EnchantProcessor(Plugin plugin)
    {
    	Bukkit.getPluginManager().registerEvents(this, plugin);
    	setDefault(EPSConfiguration.getConfiguration(CustomEnchants.TOKENCHARITY), "random-range", "%lvl%");
    	setDefault(EPSConfiguration.getConfiguration(CustomEnchants.CHARITY), "random-range", "%lvl%");
    	setDefault(EPSConfiguration.getConfiguration(CustomEnchants.TOKENBLOCKS), "random-range", "%lvl%");
    	setDefault(EPSConfiguration.getConfiguration(CustomEnchants.MONEYBLOCKS), "random-range", "%lvl%");
    }
    
	@EventHandler
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (!allowenchantmods)
			return;
		ItemStack mainhand = e.getPlayer().getInventory().getItemInMainHand();
		if (mainhand == null)
            return;
		if (!mainhand.hasItemMeta())
		    return;
		if (e.getPlayer().getGameMode() == GameMode.CREATIVE || e.getPlayer().getGameMode() == GameMode.SPECTATOR)
			return;
		if (e.getPlayer().getInventory().firstEmpty() == -1) {
			LangUtil.sendMessage(e.getPlayer(), "inventoryfull");
            return; }
		if (e.getBlock().getState() instanceof Container)
            return;
		if (e.isCancelled())
			return;
		enchantprocessed = false;
		Collection<ItemStack> drops = getDrops(e, e.getBlock());
		ItemMeta mainmeta = mainhand.getItemMeta();
		
		if (mainmeta.hasEnchant(CustomEnchants.HASTE))
		{
			if (getNext() <= EPSConfiguration.getConfiguration(CustomEnchants.HASTE).getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.HASTE), "chance"))
			{
				LangUtil.sendMessage(e.getPlayer(), "hasteactivate");
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, mainmeta.getEnchantLevel(CustomEnchants.HASTE)-1));
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.TOKENBLOCKS))
		{
			EPSConfiguration config = EPSConfiguration.getConfiguration(CustomEnchants.TOKENBLOCKS);
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TOKENBLOCKS);
			if (getNext() <= config.getAutofilledDouble(enchlvl, "chance"))
			{
				int randomrange = config.getAutofilledInt(enchlvl, "random-range");
				int tokens = config.getAutofilledInt(enchlvl, "tokens")+temp.nextInt(randomrange*2)-randomrange;
				String m = LangUtil.getLangMessage("tokenblocksactivate").replaceAll("%tokens%", Integer.toString(tokens));
				if (m != "")
					e.getPlayer().sendMessage(m);
				economy.changeBalance(e.getPlayer(), tokens);
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.MONEYBLOCKS))
		{
			EPSConfiguration config = EPSConfiguration.getConfiguration(CustomEnchants.MONEYBLOCKS);
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.MONEYBLOCKS);
			if (getNext() <= config.getAutofilledDouble(enchlvl, "chance"))
			{
				if (PackMain.VaultEnabled)
				{
					int randomrange = config.getAutofilledInt(enchlvl, "random-range");
					int money = config.getAutofilledInt(enchlvl, "money")+temp.nextInt(randomrange*2)-randomrange;
					String m = LangUtil.getLangMessage("moneyblocksactivate").replaceAll("%money%", Integer.toString(money));
					if (m != "")
						e.getPlayer().sendMessage(m);
					VaultHook.getEconomy().depositPlayer(e.getPlayer(), money);
				}
				else
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You must have Vault to use money enchants!");
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.CHARITY))
		{
			if (PackMain.VaultEnabled)
			{
				EPSConfiguration config = EPSConfiguration.getConfiguration(CustomEnchants.CHARITY);
				int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.CHARITY);
				if (getNext() <= config.getAutofilledDouble(enchlvl, "chance"))
				{
					int randomrange = config.getAutofilledInt(enchlvl, "random-range");
					int money = config.getAutofilledInt(enchlvl, "money")+temp.nextInt(randomrange*2)-randomrange;
					String m = LangUtil.getLangMessage("charityactivate").replaceAll("%money%", Integer.toString(money)).replaceAll("%player%", e.getPlayer().getDisplayName());
					if (m != "")
						Bukkit.broadcastMessage(m);
					for (Player p : Bukkit.getOnlinePlayers())
						VaultHook.getEconomy().depositPlayer(p, money);
				}
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You must have Vault to use money enchants!");
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.TOKENCHARITY))
		{
			EPSConfiguration config = EPSConfiguration.getConfiguration(CustomEnchants.TOKENCHARITY);
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TOKENCHARITY);
			if (getNext() <= config.getAutofilledDouble(enchlvl, "chance"))
			{
				int randomrange = config.getAutofilledInt(enchlvl, "random-range");
				int tokens = config.getAutofilledInt(enchlvl, "tokens")+temp.nextInt(randomrange*2)-randomrange;
				String m = LangUtil.getLangMessage("tokencharityactivate").replaceAll("%tokens%", Integer.toString(tokens)).replaceAll("%player%", e.getPlayer().getDisplayName());
				if (m != "")
					Bukkit.broadcastMessage(m);
				for (Player p : Bukkit.getOnlinePlayers())
					economy.changeBalance(p, tokens);
			}
		}

        if (mainmeta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS))
        	enchantprocessed = true;
		
		if (mainmeta.hasEnchant(CustomEnchants.EXPLOSIVE))
		{
			if (e.isCancelled()) return;
			allowenchantmods = false;
			enchantprocessed = true;
			Integer enchlvl = mainmeta.getEnchantLevel(CustomEnchants.EXPLOSIVE);
		    
		    if (getNext() <= EPSConfiguration.getConfiguration(CustomEnchants.EXPLOSIVE).getAutofilledDouble(enchlvl, "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = (int) (Math.floor(enchlvl/2)+1);
					List<Block> area = sphere(loc, radius);
					loc.getWorld().createExplosion(loc, 0F);
					
					for (Block block : area) 
					{
					    if (block.getType() == Material.BEDROCK || block.getType() == Material.AIR || block.getType() == endframe)
					    	continue;
					    
					    BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
					    Bukkit.getServer().getPluginManager().callEvent(newevent);
					    if (newevent.isCancelled())
					    	continue;
					    					    
					    Collection<ItemStack> exdrops = getDrops(e, block);
					    e.getPlayer().giveExp(getExp(block));
					    block.setType(Material.AIR);
					    
						for (ItemStack i : exdrops)
					        drops.add(i);
					}
			}
			allowenchantmods = true;
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.EXCAVATE))
		{
			if (e.isCancelled()) return;
			allowenchantmods = false;
			enchantprocessed = true;
			Integer enchlvl = mainmeta.getEnchantLevel(CustomEnchants.EXCAVATE);
		    
		    
			if (getNext() <= EPSConfiguration.getConfiguration(CustomEnchants.EXCAVATE).getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.EXCAVATE), "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = enchlvl/2;
					List<Block> area = new ArrayList<Block>();
					World world = e.getBlock().getWorld();
					for (int x=-radius;x<radius;x++)
						for (int y=-radius;y<radius;y++)
								for (int z=-radius;z<radius;z++)
									area.add(world.getBlockAt(new Location(world,x+loc.getBlockX(),y+loc.getBlockY(),z+loc.getBlockZ())));
					for (Block block : area) 
					{
				        if (block.getType() == Material.BEDROCK || block.getType() == endframe)
				        	continue;
				        
				        if (block.getType() == Material.AIR)
				        	continue;
							    
						BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
						Bukkit.getServer().getPluginManager().callEvent(newevent);
						if (newevent.isCancelled())
							continue;
													    
						Collection<ItemStack> exdrops = getDrops(e, block);
						e.getPlayer().giveExp(getExp(block));
						block.setType(Material.AIR);
						
						for (ItemStack i : exdrops)
							drops.add(i);
					}
			}
			allowenchantmods = true;
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.DIAMOND))
		{
			if (e.isCancelled()) return;
			allowenchantmods = false;
			enchantprocessed = true;
			Integer enchlvl = mainmeta.getEnchantLevel(CustomEnchants.DIAMOND);
		    
		    
			if (getNext() <= EPSConfiguration.getConfiguration(CustomEnchants.DIAMOND).getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.DIAMOND), "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = enchlvl/2;
					Block[] area = diamond(loc, radius);
					for (Block block : area) 
					{
					    if (block.getType() == Material.BEDROCK || block.getType() == endframe)
					    	continue;

					    if (block.getType() == Material.AIR)
				        	continue;
					    
					    BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
					    Bukkit.getServer().getPluginManager().callEvent(newevent);
					    if (newevent.isCancelled())
					    	continue;
					    
					    
					    Collection<ItemStack> exdrops = getDrops(e, block);
					    e.getPlayer().giveExp(getExp(block));
					    block.setType(Material.AIR); 
						for (ItemStack i : exdrops)
					        drops.add(i);
					}
			}
			allowenchantmods = true;
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.VEIN_MINER))
		{
			if (e.isCancelled()) return;
			allowenchantmods = false;
			enchantprocessed = true;
			Integer enchlvl = mainmeta.getEnchantLevel(CustomEnchants.VEIN_MINER);
		    
		    
			if (getNext() <= EPSConfiguration.getConfiguration(CustomEnchants.VEIN_MINER).getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.VEIN_MINER), "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = enchlvl/2;
					List<Block> area = new ArrayList<Block>();
					World world = e.getBlock().getWorld();
					for (int x=-radius;x<radius;x++)
						for (int y=-radius;y<radius;y++)
								for (int z=-radius;z<radius;z++)
								{
									Block block = world.getBlockAt(new Location(world,x+loc.getBlockX(),y+loc.getBlockY(),z+loc.getBlockZ()));
									if (block.getType().equals(e.getBlock().getType()))
									area.add(block);
								}
					for (Block block : area) 
					{
				        if (block.getType() == Material.BEDROCK || block.getType() == endframe)
				        	continue;
				        
				        if (block.getType() == Material.AIR)
				        	continue;
							    
						BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
						Bukkit.getServer().getPluginManager().callEvent(newevent);
						if (newevent.isCancelled())
							continue;
						
							    
						Collection<ItemStack> exdrops = getDrops(e, block);
						e.getPlayer().giveExp(getExp(block));
						block.setType(Material.AIR);
						
						for (ItemStack i : exdrops)
							drops.add(i);
					}
			}
			allowenchantmods = true;
		}
		
		ItemStack[] dropsfinal = new ItemStack[drops.size()];
		dropsfinal = drops.toArray(dropsfinal);
		if (mainmeta.hasEnchant(CustomEnchants.TELEPATHY))
		{
			Integer lvl1 = mainmeta.getEnchantLevel(CustomEnchants.TELEPATHY);
			if (lvl1 > 0) {
		        if (getNext() <= EPSConfiguration.getConfiguration(CustomEnchants.TELEPATHY).getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.TELEPATHY), "chance"))
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
		ItemStack mainhand = e.getPlayer().getInventory().getItemInMainHand();
		ItemMeta mainmeta = mainhand.getItemMeta();
		Collection<ItemStack> drops = block.getDrops(mainhand);
		Collection<ItemStack> dropfinal = new ArrayList<ItemStack>();
		Integer lvl = mainmeta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
		Integer lvl1 = mainmeta.getEnchantLevel(CustomEnchants.AUTOSMELT);
		
		if (drops.isEmpty() || drops == null)
			return (new ArrayList<ItemStack>());
		
		for (ItemStack drop : drops)
        {
			Material m = drop.getType();
			if (lvl > 0)
			{	
			    if (getFortuneDrops().contains(m) && !(saves.contains(block.getLocation()))) {
			    	enchantprocessed = true;
			    	drop.setAmount(getDropCount(lvl, temp)); }
			}
		    if (lvl1 > 0 && getNext() < EPSConfiguration.getConfiguration(CustomEnchants.AUTOSMELT).getAutofilledDouble(mainmeta.getEnchantLevel(CustomEnchants.AUTOSMELT), "chance"))
		    {
		    	enchantprocessed = true;
                if (m.equals(Material.COBBLESTONE))
                	drop.setType(Material.STONE);
                if (m.equals(Material.IRON_ORE))
                	drop.setType(Material.IRON_INGOT);
                if (m.equals(Material.GOLD_ORE))
                	drop.setType(Material.GOLD_INGOT);
                if (m == Material.matchMaterial("ANCIENT_DEBRIS"))
            	    drop.setType(Material.matchMaterial("NETHERITE_SCRAP"));
		    }
		    dropfinal.add(drop);
        }
		return dropfinal;
	}
	
	public List<Block> sphere(final Location center, int radius) {
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
	return sphere;
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
	
	public List<Material> getFortuneDrops()
    {
    	List<Material> fortuneapply = new ArrayList<Material>();
    	List<String> list;
    	list = Main.Config.getStringList("applyfortuneon");
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
	
	public void setDefault(EPSConfiguration config, String path, Object value)
	{
		if (!config.isSet(path))
			config.set(path, value);
		config.save();
	}

	@Override
	public void reload() {
		
	}
	
}
