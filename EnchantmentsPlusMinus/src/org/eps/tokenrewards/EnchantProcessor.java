package org.eps.tokenrewards;

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
import org.vivi.eps.Main;
import org.vivi.eps.api.Reloadable;
import org.vivi.eps.economy.Economy;
import org.vivi.eps.util.DataUtil;
import org.vivi.eps.util.LangUtil;

public class EnchantProcessor implements Listener, Reloadable {

	public Map<Player, Integer> blocklog = new HashMap<Player, Integer>();
	private Random random = new Random();
	private Economy economy = EPS.getEconomy();
	private int blocksToBreak = Main.Config.getInt("miningtokens.blockstobreak");
	private int miningTokensMin = Main.Config.getInt("miningtokens.min");
	private int miningTokensMax = Main.Config.getInt("miningtokens.max");
	private String miningTokensGet = LangUtil.getLangMessage("miningtokensget");
	private boolean miningTokensEnabled = Main.Config.getBoolean("miningtokens.enabled");
	private int playerKillTokensMin = Main.Config.getInt("playerkilltokens.min");
	private int playerKillTokensMax = Main.Config.getInt("playerkilltokens.max");
	private String playerKillGet = LangUtil.getLangMessage("playerkill");
	private boolean playerKillTokensEnabled = Main.Config.getBoolean("playerkilltokens.enabled");
	private int mobKillTokensMin = Main.Config.getInt("mobkilltokens.min");
	private int mobKillTokensMax = Main.Config.getInt("mobkilltokens.max");
	private String mobKillGet = LangUtil.getLangMessage("mobkill");
	private boolean mobKillTokensEnabled = Main.Config.getBoolean("mobkilltokens.enabled");
	
	
	public EnchantProcessor(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);
		EPS.registerReloader(this);

		setDefault("playerkilltokens.enabled", true);
		setDefault("playerkilltokens.min", 25);
		setDefault("playerkilltokens.max", 50);
		setDefault("mobkilltokens.enabled", true);
		setDefault("mobkilltokens.min", 5);
		setDefault("mobkilltokens.max", 10);
		LangUtil.setDefaultLangMessage("playerkill", "&aYou received %tokens% tokens for killing %victim%!");
		LangUtil.setDefaultLangMessage("mobkill", "&aYou received %tokens% tokens for killing %mob%!");
		
		setDefault("miningtokens.enabled", true);
		setDefault("miningtokens.min", 25);
		setDefault("miningtokens.max", 50);
		setDefault("miningtokens.blockstobreak", 1000);
		LangUtil.setDefaultLangMessage("miningtokensget", "&aYou received %tokens% tokens for mining!");
	}	
	
	 @EventHandler
	 public void onKill(EntityDeathEvent e)
	 {
		 if (!mobKillTokensEnabled) 
	    	 return;
		 if (e.getEntity() instanceof Player)
			 return;
		 if (e.getEntity().getKiller() == null) 
			 return;
		 Player killer = e.getEntity().getKiller();
		 if (killer instanceof Player)
		 {
			 int tokens = random.nextInt(mobKillTokensMax-mobKillTokensMin)+mobKillTokensMin+1;
			 String name = e.getEntityType().name();
			 killer.sendMessage(mobKillGet.replaceAll("%tokens%", Integer.toString(tokens)).replaceAll("%mob%", WordUtils.capitalizeFully(name.replaceAll("_", " ").toLowerCase())));
			 economy.changeBalance(killer, tokens);
		 }
	 }
	 
	 @EventHandler
	 public void onPlayerDeath(PlayerDeathEvent e)
	 {
		 if (!playerKillTokensEnabled) 
			 return;
		 if (e.getEntity().getKiller() == null) return;
		 Player killer = e.getEntity().getKiller();
		 if (killer instanceof Player)
		 {
		     Player killed = (Player) e.getEntity();
		     if (killer == killed)
		    	 return;
		     int tokens = random.nextInt(playerKillTokensMax-playerKillTokensMin)+playerKillTokensMin+1;
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
		if (!miningTokensEnabled) 
			return;
		if (blocklog.containsKey(e.getPlayer()))
		{
			blocklog.put(e.getPlayer(), blocklog.get(e.getPlayer())+1);
			if (blocklog.get(e.getPlayer()) >= blocksToBreak)
			{
				blocklog.put(e.getPlayer(), 0);
				Integer tokens = random.nextInt(miningTokensMax-miningTokensMin)+miningTokensMin+1;
				economy.changeBalance(e.getPlayer(), tokens);
				e.getPlayer().sendMessage(miningTokensGet.replaceAll("%tokens%", tokens.toString()));
			}
		}
	 }
	 
	 public static void setMiscToDef(String key)
	 {
		 if (Main.Config.isSet("misc."+key))
	    	{
	    		Main.Config.set(key, Main.Config.get("misc."+key));
	    		Main.Config.set("misc."+key, null);
	    	}
	 }
	 
	 public static void setDefault(String path, Object replace)
     {
		 if (!Main.Config.isSet(path))
		 {
			 Main.Config.set(path, replace);
			 if (Main.ConfigFile.exists())
			 {
				 DataUtil.saveConfig(Main.Config, Main.ConfigFile);
			 }
		 }
     } 

	@Override
	public void reload() 
	{
		blocksToBreak = Main.Config.getInt("miningtokens.blockstobreak");
		miningTokensMin = Main.Config.getInt("miningtokens.min");
		miningTokensMax = Main.Config.getInt("miningtokens.max");
		miningTokensGet = LangUtil.getLangMessage("miningtokensget");
		miningTokensEnabled = Main.Config.getBoolean("miningtokens.enabled");
		playerKillTokensMin = Main.Config.getInt("playerkilltokens.min");
		playerKillTokensMax = Main.Config.getInt("playerkilltokens.max");
		playerKillGet = LangUtil.getLangMessage("playerkill");
		playerKillTokensEnabled = Main.Config.getBoolean("playerkilltokens.enabled");
		mobKillTokensMin = Main.Config.getInt("mobkilltokens.min");
		mobKillTokensMax = Main.Config.getInt("mobkilltokens.max");
		mobKillGet = LangUtil.getLangMessage("mobkill");
		mobKillTokensEnabled = Main.Config.getBoolean("mobkilltokens.enabled");
	}
	
}
