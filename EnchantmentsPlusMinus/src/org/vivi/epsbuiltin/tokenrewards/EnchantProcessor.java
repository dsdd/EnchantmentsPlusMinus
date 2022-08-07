package org.vivi.epsbuiltin.tokenrewards;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.vivi.eps.EPS;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;
import org.vivi.eps.util.economy.Economy;

public class EnchantProcessor implements Listener, Reloadable {

	public Map<Player, Integer> blocklog = new HashMap<Player, Integer>();
	private Random random = new Random();
	private Economy economy = EPS.getEconomy();
	private String miningTokensGet = Language.getLangMessage("miningtokensget");
	private String playerKillGet = Language.getLangMessage("playerkill");
	private String mobKillGet = Language.getLangMessage("mobkill");
	
	
	public EnchantProcessor(EPS plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
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
	 
	 @EventHandler
	 public void onJoin(PlayerJoinEvent e) 
	 {
		 if (!blocklog.containsKey(e.getPlayer()))
		 {
			 blocklog.put(e.getPlayer(), 0);
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
