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
import org.bukkit.block.Block;
import org.bukkit.block.Container;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

import net.milkbowl.vault.economy.Economy;


public class EnchantProcessor implements Listener {
	
	private boolean allowenchantmods = true;
	private boolean enchantprocessed = false;
	private Collection<Location> saves;
	private Random rand = new Random();
	
    public EnchantProcessor(Main plugin)
    {
    	Bukkit.getPluginManager().registerEvents(this, plugin);
    	saves = new ArrayList<Location>(Arrays.asList());
    	LangUtil.setDefaultLangMessage("inventoryfull", "&cYour inventory is full!");
    	LangUtil.setDefaultLangMessage("hasteactivate", "&aHaste has been activated!");
    	LangUtil.setDefaultLangMessage("tokenblocksactivate", "&aYou received %tokens% tokens from TokenBlocks!");
    	LangUtil.setDefaultLangMessage("moneyblocksactivate", "&aYou received $%money% from MoneyBlocks!");
    	LangUtil.setDefaultLangMessage("tokencharityactivate", "&aYou received %tokens% tokens from a token charity by %player%&a!");
    	LangUtil.setDefaultLangMessage("charityactivate", "&aYou received $%money% from a charity by %player%&a!");
    	ConfigUtil.autoFillEnchantConfig(CustomEnchants.MONEYBLOCKS, "Has a chance to give money while mining.", 200);
    	ConfigUtil.autoFillEnchantConfig(CustomEnchants.CHARITY, "Has a chance to give everyone money while mining.", 200);
    	ConfigUtil.autoFillEnchantConfig(CustomEnchants.TOKENCHARITY, "Has a chance to give everyone tokens while mining.", 200);
    	ConfigUtil.setDefaultEnchantKey(CustomEnchants.MONEYBLOCKS, "chance", "%lvl%/10");
    	ConfigUtil.setDefaultEnchantKey(CustomEnchants.CHARITY, "chance", "%lvl%/20");
    	ConfigUtil.setDefaultEnchantKey(CustomEnchants.TOKENCHARITY, "chance", "%lvl%/20");
    	ConfigUtil.setDefaultEnchantKey(CustomEnchants.MONEYBLOCKS, "money", "%lvl%*350");
    	ConfigUtil.setDefaultEnchantKey(CustomEnchants.CHARITY, "money", "%lvl%*150");
    	ConfigUtil.setDefaultEnchantKey(CustomEnchants.TOKENCHARITY, "tokens", "%lvl%*20");
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
			if (rand.nextInt(1000) <= ConfigUtil.getEnchantKeyDouble(CustomEnchants.EXPLOSIVE, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.EXPLOSIVE), "chance")*10)
			{
					Location loc = e.getBlock().getLocation();
					int radius = ((int)Math.cbrt(enchlvl))/2+1;
					Block[] area = sphere(loc, radius);
					count = 0;
					loc.getWorld().createExplosion(loc, 0F);
					for (int i=0;i<area.length;i++) {
						Block block = area[i];
					    if (!(block.getType() == Material.BEDROCK || block.getType() == Material.END_PORTAL_FRAME))
					    {
					        if (count > enchlvl*2) break;
					        ++count;
					        BlockBreakEvent newevent = new BlockBreakEvent(block, e.getPlayer());
					        Bukkit.getServer().getPluginManager().callEvent(newevent);
					        if (!newevent.isCancelled()) {
					        Collection<ItemStack> exdrops = getDrops(e, block);
					        block.setType(Material.AIR);
					        ItemStack[] exdropsfinal = new ItemStack[exdrops.size()];
							exdropsfinal =exdrops.toArray(exdropsfinal);
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
		        if (rand.nextInt(100) <= ConfigUtil.getEnchantKeyDouble(CustomEnchants.TELEPATHY, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TELEPATHY), "chance"))
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
		    if (lvl1 > 0 && rand.nextInt(100) < ConfigUtil.getEnchantKeyDouble(CustomEnchants.AUTOSMELT, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.AUTOSMELT), "chance"))
		    {
		    	enchantprocessed = true;
                if (m == Material.IRON_ORE)
                drop.setType(Material.IRON_INGOT);
                    
                if (m == Material.GOLD_ORE)
                drop.setType(Material.GOLD_INGOT);
                    
                if (m == Material.ANCIENT_DEBRIS)
                drop.setType(Material.NETHERITE_SCRAP);
                    
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
			if (rand.nextInt(100) <= ConfigUtil.getEnchantKeyDouble(CustomEnchants.HASTE, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.HASTE), "chance"))
			{
				e.getPlayer().sendMessage(LangUtil.getLangMessage("hasteactivate"));
				e.getPlayer().addPotionEffect(new PotionEffect(PotionEffectType.FAST_DIGGING, 60, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.HASTE)-1));
			}
		}
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.TOKENBLOCKS))
		{
			if (rand.nextInt(1000) <= ConfigUtil.getEnchantKeyDouble(CustomEnchants.TOKENBLOCKS, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TOKENBLOCKS), "chance")*10)
			{
				double explicit = ConfigUtil.getEnchantKeyDouble(CustomEnchants.TOKENBLOCKS,a.getEnchantmentLevel(CustomEnchants.TOKENBLOCKS), "tokens");
				int val = (int) explicit;
				e.getPlayer().sendMessage(LangUtil.getLangMessage("tokenblocksactivate").replaceAll("%tokens%", Integer.toString(val)));
				TokenUtil.changeTokens(e.getPlayer(), val);
			}
		}
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.MONEYBLOCKS))
		{
			if (rand.nextInt(100) <= ConfigUtil.getEnchantKeyDouble(CustomEnchants.MONEYBLOCKS, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TOKENBLOCKS), "chance"))
			{
				if (PackMain.VaultEnabled)
				{
					double explicit = ConfigUtil.getEnchantKeyDouble(CustomEnchants.MONEYBLOCKS,a.getEnchantmentLevel(CustomEnchants.MONEYBLOCKS), "money");
					int val = (int) explicit;
					e.getPlayer().sendMessage(LangUtil.getLangMessage("moneyblocksactivate").replaceAll("%money%", Integer.toString(val)));
					Economy economy = Vault.getEconomy();
					economy.depositPlayer(e.getPlayer(), val);
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
				if (rand.nextInt(100) <= ConfigUtil.getEnchantKeyDouble(CustomEnchants.CHARITY, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.CHARITY), "chance"))
				{
				double explicit = ConfigUtil.getEnchantKeyDouble(CustomEnchants.CHARITY,a.getEnchantmentLevel(CustomEnchants.CHARITY), "money");
				int val = (int) explicit;
				Bukkit.broadcastMessage(LangUtil.getLangMessage("charityactivate").replaceAll("%money%", Integer.toString(val)).replaceAll("%player%", e.getPlayer().getDisplayName()));
				Economy economy = Vault.getEconomy();
				for (Player p : Bukkit.getOnlinePlayers())
				economy.depositPlayer(p, val);
				}
			}
			else
			{
				Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"You must have Vault to use money enchants!");
			}
		}
		if (e.getPlayer().getInventory().getItemInMainHand().getItemMeta().hasEnchant(CustomEnchants.TOKENCHARITY))
		{
			if (rand.nextInt(100) <= ConfigUtil.getEnchantKeyDouble(CustomEnchants.TOKENCHARITY, e.getPlayer().getInventory().getItemInMainHand().getEnchantmentLevel(CustomEnchants.TOKENCHARITY), "chance"))
			{
			double explicit = ConfigUtil.getEnchantKeyDouble(CustomEnchants.TOKENCHARITY,a.getEnchantmentLevel(CustomEnchants.TOKENCHARITY), "tokens");
			int val = (int) explicit;
			Bukkit.broadcastMessage(LangUtil.getLangMessage("tokencharityactivate").replaceAll("%tokens%", Integer.toString(val)).replaceAll("%player%", e.getPlayer().getDisplayName()));
			for (Player p : Bukkit.getOnlinePlayers())
			TokenUtil.changeTokens(p, val);
			}
		}
	}
	
	public Block[] sphere(final Location center, final int radius) {
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
	    Block[] spherefinal = new Block[sphere.size()];
	    spherefinal = sphere.toArray(spherefinal);
	return spherefinal;
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
}
