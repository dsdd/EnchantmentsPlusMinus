package org.vivi.eps.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion {
       
	/** Creates a new hook for use.
	 */
    public PlaceholderAPIHook() {
    	this.register();
    	Bukkit.getConsoleSender().sendMessage(ChatColor.GREEN+"Successfully hooked into PlaceholderAPI!");
    }
   
    @Override
    public boolean canRegister() {
        return true;
    }
    
    @Override
    public String getAuthor() {
        return "vivisan";
    }
    
    @Override
    public String getIdentifier() {
        return "eps";
    }
    
    @Override
    public String getPlugin() {
        return "EnchantmentsPlusMinus";
    }
    
    @Override
    public String getVersion() {
        return EPS.plugin.getDescription().getVersion();
    }
   
   
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {

        if (identifier.equals("tokens")) {
            return Integer.toString(EPS.getEconomy().getBalance(p.getName()));
        }
        return null;
    }
}