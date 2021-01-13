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
		ConfigUtil.setDefaultMisc("playerkilltokens.enabled", true);
		ConfigUtil.setDefaultMisc("playerkilltokens.min", 25);
		ConfigUtil.setDefaultMisc("playerkilltokens.max", 50);
		ConfigUtil.setDefaultMisc("mobkilltokens.enabled", true);
		ConfigUtil.setDefaultMisc("mobkilltokens.min", 5);
		ConfigUtil.setDefaultMisc("mobkilltokens.max", 10);
		LangUtil.setDefaultLangMessage("playerkill", "&aYou received %tokens% tokens for killing %victim%!");
		LangUtil.setDefaultLangMessage("mobkill", "&aYou received %tokens% tokens for killing %mob%!");
		
		ConfigUtil.setDefaultMisc("miningtokens.enabled", true);
		ConfigUtil.setDefaultMisc("miningtokens.min", 25);
		ConfigUtil.setDefaultMisc("miningtokens.max", 50);
		ConfigUtil.setDefaultMisc("miningtokens.blockstobreak", 1000);
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
		     if ((boolean)ConfigUtil.getMiscKey("mobkilltokens.enabled")==false) return;
			 Integer tokenmin = (int)ConfigUtil.getMiscKey("mobkilltokens.min");
			 Integer tokenmax = (int)ConfigUtil.getMiscKey("mobkilltokens.max");
			 Integer tokens = new Random().nextInt(tokenmax-tokenmin)+tokenmin;
			 String name = "";
			 if (LegacyUtil.isLegacy())
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
			 if ((boolean)ConfigUtil.getMiscKey("playerkilltokens.enabled")==false) return;
			 Integer tokenmin = (int)ConfigUtil.getMiscKey("playerkilltokens.min");
			 Integer tokenmax = (int)ConfigUtil.getMiscKey("playerkilltokens.max");
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
			if (blocklog.get(e.getPlayer()) > (int)ConfigUtil.getMiscKey("miningtokens.blockstobreak"))
			{
				blocklog.put(e.getPlayer(), 0);
				Integer tokens = new Random().nextInt((int)ConfigUtil.getMiscKey("miningtokens.max")-(int)ConfigUtil.getMiscKey("miningtokens.min"))+(int)ConfigUtil.getMiscKey("miningtokens.min");
				TokenUtil.changeTokens(e.getPlayer(), tokens);
				e.getPlayer().sendMessage(LangUtil.getLangMessage("miningtokensget").replaceAll("%tokens%", tokens.toString()));
			}
		}
	 }
	
}
