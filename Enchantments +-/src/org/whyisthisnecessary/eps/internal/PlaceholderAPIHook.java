package org.whyisthisnecessary.eps.internal;

import org.bukkit.entity.Player;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceholderAPIHook extends PlaceholderExpansion {
       
    public PlaceholderAPIHook() {
    	this.register();
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
            return Integer.toString(InternalTokenManager.GetTokens(p.getName()));
        }
        return null;
    }
}