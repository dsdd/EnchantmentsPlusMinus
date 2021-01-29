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
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

public class EnchantProcessor implements Listener {

	public Map<Player, Integer> blocklog = new HashMap<Player, Integer>();
	private Random random = new Random();
	
	public EnchantProcessor(Main plugin) {
		Bukkit.getPluginManager().registerEvents(this, plugin);

		ConfigUtil.setDefault("playerkilltokens.enabled", true);
		ConfigUtil.setDefault("playerkilltokens.min", 25);
		ConfigUtil.setDefault("playerkilltokens.max", 50);
		ConfigUtil.setDefault("mobkilltokens.enabled", true);
		ConfigUtil.setDefault("mobkilltokens.min", 5);
		ConfigUtil.setDefault("mobkilltokens.max", 10);
		LangUtil.setDefaultLangMessage("playerkill", "&aYou received %tokens% tokens for killing %victim%!");
		LangUtil.setDefaultLangMessage("mobkill", "&aYou received %tokens% tokens for killing %mob%!");
		
		ConfigUtil.setDefault("miningtokens.enabled", true);
		ConfigUtil.setDefault("miningtokens.min", 25);
		ConfigUtil.setDefault("miningtokens.max", 50);
		ConfigUtil.setDefault("miningtokens.blockstobreak", 1000);
		LangUtil.setDefaultLangMessage("miningtokensget", "&aYou received %tokens% tokens for mining!");
	}	
	
	 @EventHandler
	 public void onKill(EntityDeathEvent e)
	 {
		 if (e.getEntity().getKiller() == null) return;
		 Player killer = e.getEntity().getKiller();
		 if (killer instanceof Player)
		 {
		     if (!Main.Config.getBoolean("mobkilltokens.enabled")) 
		    	 return;
			 Integer tokenmin = Main.Config.getInt("mobkilltokens.min");
			 Integer tokenmax = Main.Config.getInt("mobkilltokens.max");
			 Integer tokens = random.nextInt(tokenmax-tokenmin)+tokenmin;
			 String name = e.getEntityType().name();
			 killer.sendMessage(LangUtil.getLangMessage("mobkill").replaceAll("%tokens%", tokens.toString()).replaceAll("%mob%", WordUtils.capitalizeFully(name.replaceAll("_", " ").toLowerCase())));
			 TokenUtil.changeTokens(killer, tokens);
		 }
	 }
	 
	 @EventHandler
	 public void onPlayerDeath(PlayerDeathEvent e)
	 {
		 if (e.getEntity().getKiller() == null) return;
		 Player killer = e.getEntity().getKiller();
		 if (killer instanceof Player)
		 {
			 if (!Main.Config.getBoolean("playerkilltokens.enabled")) 
				 return;
			 Integer tokenmin = Main.Config.getInt("playerkilltokens.min");
			 Integer tokenmax = Main.Config.getInt("playerkilltokens.max");
		     Player killed = (Player) e.getEntity();
		     Integer tokens = random.nextInt(tokenmax-tokenmin)+tokenmin;
		     killer.sendMessage(LangUtil.getLangMessage("playerkill").replaceAll("%tokens%", tokens.toString()).replaceAll("%victim%", killed.getName()));
		     TokenUtil.changeTokens(killer, tokens);
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
	 
	 @EventHandler
	 public void onBlockBreak(BlockBreakEvent e)
	 {
		if (blocklog.containsKey(e.getPlayer()))
		{
			blocklog.put(e.getPlayer(), blocklog.get(e.getPlayer())+1);
			if (blocklog.get(e.getPlayer()) > Main.Config.getInt("miningtokens.blockstobreak"))
			{
				blocklog.put(e.getPlayer(), 0);
				int min = Main.Config.getInt("miningtokens.min");
				Integer tokens = random.nextInt(Main.Config.getInt("miningtokens.max")-min)+min;
				TokenUtil.changeTokens(e.getPlayer(), tokens);
				e.getPlayer().sendMessage(LangUtil.getLangMessage("miningtokensget").replaceAll("%tokens%", tokens.toString()));
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
	
}
