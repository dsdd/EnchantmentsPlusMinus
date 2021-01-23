package org.eps.tokenrewards;

import java.util.HashMap;
import java.util.Map;
import java.util.Random;

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
import org.whyisthisnecessary.eps.legacy.LegacyUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

public class EnchantProcessor implements Listener {

	public Map<Player, Integer> blocklog = new HashMap<Player, Integer>();
	
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
	
	 @SuppressWarnings("deprecation")
	 @EventHandler
	 public void onKill(EntityDeathEvent e)
	 {
		 if (e.getEntity().getKiller() == null) return;
		 Player killer = e.getEntity().getKiller();
		 if (killer instanceof Player)
		 {
		     if ((boolean)ConfigUtil.getConfig().get("mobkilltokens.enabled")==false) return;
			 Integer tokenmin = ConfigUtil.getConfig().getInt("mobkilltokens.min");
			 Integer tokenmax = ConfigUtil.getConfig().getInt("mobkilltokens.max");
			 Integer tokens = new Random().nextInt(tokenmax-tokenmin)+tokenmin;
			 String name = "";
			 if (LegacyUtil.isLegacy() || !((Object) e.getEntityType() instanceof Keyed))
				 name = e.getEntity().getType().getName();
			 else
				 name = e.getEntity().getType().getKey().getKey();
			 killer.sendMessage(LangUtil.getLangMessage("mobkill").replaceAll("%tokens%", tokens.toString()).replaceAll("%mob%", name));
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
			 if (ConfigUtil.getConfig().getBoolean("playerkilltokens.enabled")==false) return;
			 Integer tokenmin = ConfigUtil.getConfig().getInt("playerkilltokens.min");
			 Integer tokenmax = ConfigUtil.getConfig().getInt("playerkilltokens.max");
		     Player killed = (Player) e.getEntity();
		     Integer tokens = new Random().nextInt(tokenmax-tokenmin)+tokenmin;
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
			if (blocklog.get(e.getPlayer()) > ConfigUtil.getConfig().getInt("miningtokens.blockstobreak"))
			{
				blocklog.put(e.getPlayer(), 0);
				int min = ConfigUtil.getConfig().getInt("miningtokens.min");
				Integer tokens = new Random().nextInt(ConfigUtil.getConfig().getInt("miningtokens.max")-min)+min;
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
