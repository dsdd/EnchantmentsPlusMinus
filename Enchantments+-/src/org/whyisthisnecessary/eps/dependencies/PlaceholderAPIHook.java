package org.whyisthisnecessary.eps.dependencies;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.util.TokenUtil;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion {
       
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
        return "whyisthisnecessary";
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
        return "1.0.0";
    }
   
   
    @Override
    public String onPlaceholderRequest(Player p, String identifier) {

        if (identifier.equals("tokens")) {
            return Integer.toString(TokenUtil.getTokens(p.getName()));
        }
        return null;
    }
}