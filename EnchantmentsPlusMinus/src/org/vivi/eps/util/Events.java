package org.vivi.eps.util;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.PrepareAnvilEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.AnvilInventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.vivi.eps.EPS;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.items.CustomEnchantedBook;
import org.vivi.eps.util.economy.Economy;
import org.vivi.eps.visual.EnchantGUI;
import org.vivi.eps.visual.EnchantMetaWriter;
import org.vivi.epsbuiltin.enchants.Durability;

public class Events implements Listener, Reloadable {

	public Map<Player, Integer> blocklog = new HashMap<Player, Integer>();
	private Random random = new Random();
	private Economy economy = EPS.getEconomy();
	private String miningTokensGet = Language.getLangMessage("miningtokensget");
	private String playerKillGet = Language.getLangMessage("playerkill");
	private String mobKillGet = Language.getLangMessage("mobkill");
	
	public Events() 
	{
		EPS.registerReloader(this);

		setDefault("playerkilltokens.enabled", true);
		setDefault("playerkilltokens.min", 25);
		setDefault("playerkilltokens.max", 50);
		setDefault("mobkilltokens.enabled", true);
		setDefault("mobkilltokens.min", 5);
		setDefault("mobkilltokens.max", 10);
		Language.setDefaultLangMessage("playerkill", "&aYou received %tokens% tokens for killing %victim%!");
		Language.setDefaultLangMessage("mobkill", "&aYou received %tokens% tokens for killing %mob%!");
		
		setDefault("miningtokens.enabled", true);
		setDefault("miningtokens.min", 25);
		setDefault("miningtokens.max", 50);
		setDefault("miningtokens.blockstobreak", 1000);
		Language.setDefaultLangMessage("miningtokensget", "&aYou received %tokens% tokens for mining!");
	}	
	
	@EventHandler
	 public void onKill(EntityDeathEvent e)
	 {
		 if (!ConfigSettings.isMobKillRewardEnabled()) 
	    	 return;
		 if (e.getEntity() instanceof Player)
			 return;
		 if (e.getEntity().getKiller() == null) 
			 return;
		 Player killer = e.getEntity().getKiller();
		 if (killer instanceof Player)
		 {
			 int tokens = random.nextInt(ConfigSettings.getMobKillRewardMax()-ConfigSettings.getMobKillRewardMin())+ConfigSettings.getMobKillRewardMin()+1;
			 String name = e.getEntityType().name();
			 killer.sendMessage(mobKillGet.replaceAll("%tokens%", Integer.toString(tokens)).replaceAll("%mob%", WordUtils.capitalizeFully(name.replaceAll("_", " ").toLowerCase())));
			 economy.changeBalance(killer, tokens);
		 }
	 }
	 
	 @EventHandler
	 public void onPlayerDeath(PlayerDeathEvent e)
	 {
		 if (!ConfigSettings.isPlayerKillRewardEnabled()) 
			 return;
		 if (e.getEntity().getKiller() == null) return;
		 Player killer = e.getEntity().getKiller();
		 if (killer instanceof Player)
		 {
		     Player killed = (Player) e.getEntity();
		     if (killer == killed)
		    	 return;
		     int tokens = random.nextInt(ConfigSettings.getPlayerKillRewardMax()-ConfigSettings.getPlayerKillRewardMin())+ConfigSettings.getPlayerKillRewardMin()+1;
		     killer.sendMessage(playerKillGet.replaceAll("%tokens%", Integer.toString(tokens)).replaceAll("%victim%", killed.getName()));
		     economy.changeBalance(killer, tokens);
		 }
	 }

	 @EventHandler(ignoreCancelled = true)
	 public void onBlockBreak(BlockBreakEvent e)
	 {
		if (!ConfigSettings.isMiningRewardEnabled()) 
			return;
		if (blocklog.containsKey(e.getPlayer()))
		{
			blocklog.put(e.getPlayer(), blocklog.get(e.getPlayer())+1);
			if (blocklog.get(e.getPlayer()) >= ConfigSettings.getMiningRewardBlocksToBreak())
			{
				blocklog.put(e.getPlayer(), 0);
				Integer tokens = random.nextInt(ConfigSettings.getMiningRewardMax()-ConfigSettings.getMiningRewardMin())+ConfigSettings.getMiningRewardMin()+1;
				economy.changeBalance(e.getPlayer(), tokens);
				e.getPlayer().sendMessage(miningTokensGet.replaceAll("%tokens%", tokens.toString()));
			}
		}
	 }
	 
	@EventHandler
	public void onInteract(PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR))
		{
			Player p = e.getPlayer();
			ItemStack item = p.getInventory().getItemInMainHand();
			
			if (item.getItemMeta() != null)
			if (item.getItemMeta().hasLore())
			{
				String str = item.getItemMeta().getLore().get(0);
				if (str.startsWith(ChatColor.BLACK+"T:"))
				{
					int tokens = Integer.parseInt(str.split(":")[1]);
					EPS.getEconomy().changeBalance(p, tokens);
					p.getInventory().remove(item);
					p.sendMessage(Language.getLangMessage("claimed-token-pouch").replaceAll("%tokens%", Integer.toString(tokens)));
				}
			}
		}
	}

	@EventHandler
	public void onBlockPlace(BlockPlaceEvent e)
	{
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItemInMainHand();
		
		if (item.getItemMeta() != null)
		if (item.getItemMeta().hasLore())
		{
			String str = item.getItemMeta().getLore().get(0);
			if (str.startsWith(ChatColor.BLACK+"T:"))
			{
				e.setCancelled(true);
			}
		}
	}
	
	@EventHandler
	public void onInventoryClick(PrepareAnvilEvent e)
	{
		if (!ConfigSettings.isAnvilCombiningEnabled() || EPS.getMCVersion() < 12)
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
			int maxlevel = EPSConfiguration.getConfiguration(entry.getKey(), true).getInt("maxlevel");
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
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		EnchantMetaWriter.refreshItem(e.getPlayer().getInventory().getItemInMainHand());
	}
	
	@EventHandler
	public void onJoin(PlayerJoinEvent e) throws IOException
	{
		Player player = e.getPlayer();
		File dataFile = new File(EPS.dataFolder, player.getUniqueId().toString()+".yml");
		EPS.uuidDataStoreData.set(player.getName(), player.getUniqueId().toString());
		try {
			EPS.uuidDataStoreData.save(EPS.uuidDataStore);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		
		if (!dataFile.exists())
			dataFile.createNewFile();
		
		FileConfiguration dataConfig = YamlConfiguration.loadConfiguration(dataFile);
		dataConfig.set("tokens", dataConfig.get("tokens", 0));
		dataConfig.save(dataFile);
		EnchantGUI.setupGUI(e.getPlayer());
		
		if (!blocklog.containsKey(e.getPlayer()))
			 blocklog.put(e.getPlayer(), 0);
	}
	
	public static void setDefault(String path, Object replace)
    {
		 if (!EPS.configData.isSet(path))
		 {
			 EPS.configData.set(path, replace);
			 if (EPS.configFile.exists())
			 {
					try {
						EPS.configData.save(EPS.configFile);
					} catch (IOException e) {
						e.printStackTrace();
					}
			 }
		 }
    } 

	@Override
	public void reload() 
	{
		miningTokensGet = Language.getLangMessage("miningtokensget");
		playerKillGet = Language.getLangMessage("playerkill");
		mobKillGet = Language.getLangMessage("mobkill");
	}
}
