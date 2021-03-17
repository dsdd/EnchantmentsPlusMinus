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
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.whyisthisnecessary.eps.EPS;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.EPSConfiguration;
import org.whyisthisnecessary.eps.api.Reloadable;
import org.whyisthisnecessary.eps.api.TimeTracker;
import org.whyisthisnecessary.eps.dependencies.VaultHook;
import org.whyisthisnecessary.eps.economy.Economy;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.visual.EnchantGUI;

import net.md_5.bungee.api.ChatMessageType;
import net.md_5.bungee.api.chat.TextComponent;

public class EnchantProcessor implements Listener, Reloadable {
	
	private boolean modified_by_enchant = false;
	private boolean looping = false;
	private Collection<Location> saves = new ArrayList<Location>();
	private Random temp = new Random();
	private final Material endframe = EPS.onLegacy() ? Material.matchMaterial("ENDER_PORTAL_FRAME") : Material.matchMaterial("END_PORTAL_FRAME");
	private final Economy economy = EPS.getEconomy();
	private boolean use_action_bar = Main.Config.getBoolean("use-action-bar-instead-of-chat-inventory-full");
	private String inventoryfull = LangUtil.getLangMessage("inventoryfull");
	private boolean fortuneEnabled = Main.Config.getBoolean("use-custom-fortune");
	private List<String> applyFortuneOn = Main.Config.getStringList("applyfortuneon");
	private final Plugin plugin;
	private EPSConfiguration hasteConfig = EPSConfiguration.getConfiguration(CustomEnchants.HASTE);
	private EPSConfiguration tbConfig = EPSConfiguration.getConfiguration(CustomEnchants.TOKENBLOCKS);
	private EPSConfiguration mbConfig = EPSConfiguration.getConfiguration(CustomEnchants.MONEYBLOCKS);
	private EPSConfiguration charityConfig = EPSConfiguration.getConfiguration(CustomEnchants.CHARITY);
	private EPSConfiguration tcConfig = EPSConfiguration.getConfiguration(CustomEnchants.TOKENCHARITY);
	private EPSConfiguration explosiveConfig = EPSConfiguration.getConfiguration(CustomEnchants.EXPLOSIVE);
	private EPSConfiguration excavateConfig = EPSConfiguration.getConfiguration(CustomEnchants.EXCAVATE);
	private EPSConfiguration diamondConfig = EPSConfiguration.getConfiguration(CustomEnchants.DIAMOND);
	private EPSConfiguration vmConfig = EPSConfiguration.getConfiguration(CustomEnchants.VEIN_MINER);
	private EPSConfiguration tpConfig = EPSConfiguration.getConfiguration(CustomEnchants.TELEPATHY);
	private EPSConfiguration asConfig = EPSConfiguration.getConfiguration(CustomEnchants.AUTOSMELT);
	private EPSConfiguration boostedConfig = EPSConfiguration.getConfiguration(CustomEnchants.BOOSTED);
	private TimeTracker boostedCooldown = new TimeTracker();
	private List<Player> boosted = new ArrayList<Player>();
	private List<Material> fortuneapply = new ArrayList<Material>();
	
    public EnchantProcessor(Plugin plugin)
    {
    	Bukkit.getPluginManager().registerEvents(this, plugin);
    	this.plugin = plugin;
    	setDefault(tcConfig, "random-range", "%lvl%");
    	setDefault(charityConfig, "random-range", "%lvl%");
    	setDefault(tbConfig, "random-range", "%lvl%");
    	setDefault(mbConfig, "random-range", "%lvl%");
    	fortuneapply.clear();
    	for (String i : applyFortuneOn)
			fortuneapply.add(Material.matchMaterial(i));
    }
    
	@EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
	public void onBlockBreak(BlockBreakEvent e)
	{
		if (looping)
			return;
		Player player = e.getPlayer();
		ItemStack mainhand = player.getInventory().getItemInMainHand();
		if (mainhand == null)
            return;
		if (!mainhand.hasItemMeta())
		    return;
		if (player.getGameMode() == GameMode.CREATIVE || player.getGameMode() == GameMode.SPECTATOR)
			return;
		if (player.getInventory().firstEmpty() == -1) {
			if (use_action_bar)
				player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent.fromLegacyText(inventoryfull));
			else
				LangUtil.sendMessage(player, "inventoryfull");
            return; }
		if (e.getBlock().getState() instanceof Container)
            return;
		modified_by_enchant = false;
		Collection<ItemStack> drops = getDrops(mainhand, e.getBlock(), player);
		ItemMeta mainmeta = mainhand.getItemMeta();
		
		if (mainmeta.hasEnchant(CustomEnchants.HASTE))
		{
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.HASTE);
			if (getNext() < hasteConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				LangUtil.sendMessage(player, "hasteactivate");
				player.addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, enchlvl-1));
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.TOKENBLOCKS))
		{
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TOKENBLOCKS);
			if (getNext() < tbConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				int randomrange = tbConfig.getAutofilledInt(enchlvl, "random-range");
				int tokens = tbConfig.getAutofilledInt(enchlvl, "tokens")+temp.nextInt(randomrange*2)-randomrange;
				String m = LangUtil.getLangMessage("tokenblocksactivate").replaceAll("%tokens%", Integer.toString(tokens));
				if (m != "")
					player.sendMessage(m);
				economy.changeBalance(player, tokens);
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.MONEYBLOCKS))
		{
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.MONEYBLOCKS);
			if (getNext() < mbConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				if (PackMain.VaultEnabled)
				{
					int randomrange = mbConfig.getAutofilledInt(enchlvl, "random-range");
					int money = mbConfig.getAutofilledInt(enchlvl, "money")+temp.nextInt(randomrange*2)-randomrange;
					String m = LangUtil.getLangMessage("moneyblocksactivate").replaceAll("%money%", Integer.toString(money));
					if (m != "")
						player.sendMessage(m);
					VaultHook.getEconomy().depositPlayer(player, money);
				}
				else
					Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You must have Vault to use money enchants!");
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.CHARITY))
		{
			if (PackMain.VaultEnabled)
			{
				int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.CHARITY);
				if (getNext() < charityConfig.getAutofilledDouble(enchlvl, "chance"))
				{
					int randomrange = charityConfig.getAutofilledInt(enchlvl, "random-range");
					int money = charityConfig.getAutofilledInt(enchlvl, "money")+temp.nextInt(randomrange*2)-randomrange;
					String m = LangUtil.getLangMessage("charityactivate").replaceAll("%money%", Integer.toString(money)).replaceAll("%player%", player.getDisplayName());
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
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TOKENCHARITY);
			if (getNext() < tcConfig.getAutofilledDouble(enchlvl, "chance"))
			{
				int randomrange = tcConfig.getAutofilledInt(enchlvl, "random-range");
				int tokens = tcConfig.getAutofilledInt(enchlvl, "tokens")+temp.nextInt(randomrange*2)-randomrange;
				String m = LangUtil.getLangMessage("tokencharityactivate").replaceAll("%tokens%", Integer.toString(tokens)).replaceAll("%player%", player.getDisplayName());
				if (m != "")
					Bukkit.broadcastMessage(m);
				for (Player p : Bukkit.getOnlinePlayers())
					economy.changeBalance(p, tokens);
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.EXPLOSIVE))
		{
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.EXPLOSIVE);
		    
		    if (getNext() < explosiveConfig.getAutofilledDouble(enchlvl, "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = (int) (Math.floor(enchlvl/2)+1);
					List<Block> area = sphere(loc, radius);
					loc.getWorld().createExplosion(loc, 0F);
					
					for (Block block : area) 
					{
					    if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == Material.AIR || block.getType() == endframe)
					    	continue;
					    
					    looping = true;
					    BlockBreakEvent newevent = new BlockBreakEvent(block, player);
					    Bukkit.getPluginManager().callEvent(newevent);
					    if (newevent.isCancelled())
					    	continue;
					    
					    player.giveExp(getExp(block));
					    drops.addAll(getDrops(mainhand, block, player));
					    block.setType(Material.AIR);
					}
					
					modified_by_enchant = true;
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.EXCAVATE))
		{
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.EXCAVATE);
		    
		    
			if (getNext() < excavateConfig.getAutofilledDouble(enchlvl, "chance"))
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
				        if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == endframe || block.getType() == Material.AIR)
				        	continue;
							    
				        looping = true;
						BlockBreakEvent newevent = new BlockBreakEvent(block, player);
						Bukkit.getServer().getPluginManager().callEvent(newevent);
						if (newevent.isCancelled())
							continue;
													    
						player.giveExp(getExp(block));
						drops.addAll(getDrops(mainhand, block, player));
						block.setType(Material.AIR);
					}
					
					modified_by_enchant = true;
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.DIAMOND))
		{
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.DIAMOND);
		    
		    
			if (getNext() < diamondConfig.getAutofilledDouble(enchlvl, "chance"))
			{
					Location loc = e.getBlock().getLocation();
					int radius = enchlvl/2;
					Block[] area = diamond(loc, radius);
					for (Block block : area) 
					{
						if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == Material.AIR || block.getType() == endframe)
					    	continue;
					    
					    looping = true;
					    BlockBreakEvent newevent = new BlockBreakEvent(block, player);
					    Bukkit.getServer().getPluginManager().callEvent(newevent);
					    if (newevent.isCancelled())
					    	continue;
					    
					    
					    player.giveExp(getExp(block));
					    drops.addAll(getDrops(mainhand, block, player));
					    block.setType(Material.AIR);
					}
					
					modified_by_enchant = true;
			}
		}
		
		if (mainmeta.hasEnchant(CustomEnchants.VEIN_MINER))
		{
			int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.VEIN_MINER);
		    
			if (getNext() < vmConfig.getAutofilledDouble(enchlvl, "chance"))
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
						if (block == e.getBlock() || block.getType() == Material.BEDROCK || block.getType() == Material.AIR || block.getType() == endframe)
					    	continue;
							    
				        looping = true;
						BlockBreakEvent newevent = new BlockBreakEvent(block, player);
						Bukkit.getServer().getPluginManager().callEvent(newevent);
						if (newevent.isCancelled())
							continue;
						
						player.giveExp(getExp(block));
						drops.addAll(getDrops(mainhand, block, player));
						block.setType(Material.AIR);
					}
					
					modified_by_enchant = true;
			}
		}
		
		looping = false;
		if (mainmeta.hasEnchant(CustomEnchants.TELEPATHY))
		{
			if (!e.isCancelled())
			{
				int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.TELEPATHY);
			    if (getNext() < tpConfig.getAutofilledDouble(enchlvl, "chance"))
			    {
		        	e.setDropItems(false);
					player.getInventory().addItem(drops.toArray(new ItemStack[drops.size()]));
					modified_by_enchant = true;
		            return;
		        }
			}
		}
		
		if (mainmeta.hasEnchant(Enchantment.LOOT_BONUS_BLOCKS) && fortuneEnabled)
			modified_by_enchant = true;
		
		if (modified_by_enchant)
		{
			e.setDropItems(false);
			World world = e.getBlock().getWorld();
			Location loc = e.getBlock().getLocation();
			for (ItemStack drop : drops)
				if (!drop.getType().equals(Material.AIR) || drop.getType().equals(Material.CAVE_AIR) || drop.getType().equals(Material.VOID_AIR))
				world.dropItemNaturally(loc, drop);
		}
	}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			ItemStack mainitem = e.getPlayer().getInventory().getItemInMainHand();
			ItemMeta mainmeta = mainitem.getItemMeta();
			if (mainmeta != null)
				if (mainmeta.hasEnchant(CustomEnchants.BOOSTED))
				{
					Player player = e.getPlayer();
					if (player.isSneaking())
					{
						int enchlvl = mainmeta.getEnchantLevel(CustomEnchants.BOOSTED);
						double cooldown = boostedConfig.getAutofilledDouble(enchlvl, "cooldown")*50;
						long lastuse = boostedCooldown.getLastUse(player);
						if (lastuse <= cooldown)
							player.sendMessage(LangUtil.getLangMessage("cooldown-error").replaceAll("%secs%", Double.toString(Math.floor(((cooldown-lastuse)/1000)*10)/10)));
						else
						{
							boostedCooldown.use(player);
							LangUtil.sendMessage(player, "boosted-activate");
							boosted.add(player);
							EnchantGUI.setOpenable(player, false);
							int duration = boostedConfig.getAutofilledInt(enchlvl, "duration");
							Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(plugin, new Runnable(){
						         public void run()
						         {
						        	 boosted.remove(player);
						         }
						     }, (long)duration);
						}
					}
				}
		}
	}
	
	private Collection<ItemStack> getDrops(ItemStack item, Block block, Player p)
	{
		ItemMeta mainmeta = item.getItemMeta();
		Collection<ItemStack> drops = block.getDrops(item);
		int fortunelvl = mainmeta.getEnchantLevel(Enchantment.LOOT_BONUS_BLOCKS);
		int aslvl = mainmeta.getEnchantLevel(CustomEnchants.AUTOSMELT);
		
		if (drops.isEmpty() || drops == null)
			return (new ArrayList<ItemStack>());
		
		for (ItemStack drop : drops)
        {			
			if (fortunelvl > 0)
			{	
				if (fortuneEnabled)
				    if (getFortuneDrops().contains(drop.getType()) && !(saves.contains(block.getLocation())))
				    	drop.setAmount(getDropCount(fortunelvl, temp) * (boosted.contains(p) ? 5 : 1));
			}
		    if (aslvl > 0 && getNext() < asConfig.getAutofilledDouble(aslvl, "chance"))
                drop.setType(getSmelted(drop.getType()));
        }
		return drops;
	}
	
	private List<Block> sphere(final Location center, int radius) {
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
	
	private int getDropCount(int i, Random random) {
        int j = random.nextInt(i + 2) - 1;
        return j < 0 ? 0 : j + 1;
    }
	
	private List<Material> getFortuneDrops()
    {
    	return fortuneapply;
    }
	
	@EventHandler
	private void onBlockPlace(BlockPlaceEvent e)
	{
		saves.add(e.getBlock().getLocation());
	}
	
	private int getExp(Block block)
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
	
	private Material getSmelted(Material m)
	{
		if (m.equals(Material.COBBLESTONE))
        	return Material.STONE;
        else if (m.equals(Material.IRON_ORE))
        	return Material.IRON_INGOT;
        else if (m.equals(Material.GOLD_ORE))
        	return Material.GOLD_INGOT;
        else if (m.equals(Material.matchMaterial("ANCIENT_DEBRIS")))
        	return Material.matchMaterial("NETHERITE_SCRAP");
        else
        	return m;
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
		use_action_bar = Main.Config.getBoolean("use-action-bar-instead-of-chat-inventory-full");
		inventoryfull = LangUtil.getLangMessage("inventoryfull");
		fortuneEnabled = Main.Config.getBoolean("use-custom-fortune");
		applyFortuneOn = Main.Config.getStringList("applyfortuneon");
		fortuneapply.clear();
		for (String i : applyFortuneOn)
			fortuneapply.add(Material.matchMaterial(i));
	}
}