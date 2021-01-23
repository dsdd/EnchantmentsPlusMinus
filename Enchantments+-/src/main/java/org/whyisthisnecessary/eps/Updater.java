package org.whyisthisnecessary.eps;

import java.util.Arrays;

import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.util.DataUtil;

public class Updater {

	/** Makes updates between versions compatible.
	 */
	public static void makeCompatible()
	{
		// 1.1r - 1.2r compatibility
		
		if (Main.Config.isSet("guis"))
		{
			Main.GuisConfig.set("guis", Main.Config.get("guis"));
			DataUtil.saveConfig(Main.GuisConfig, Main.GuisFile);
			Main.Config.set("guis", null);
			DataUtil.saveConfig(Main.Config, Main.ConfigFile);
		}
		
		ConfigUtil.setDefault("open-enchant-gui-on-right-click", true);
		
		if (Main.Config.isSet("misc.applyfortuneon"))
    	{
    		Main.Config.set("applyfortuneon", Main.Config.get("misc.applyfortuneon"));
    		Main.Config.set("misc.applyfortuneon", null);
    	}
    	ConfigUtil.setDefault("applyfortuneon", Arrays.asList("COAL", "LAPIS_LAZULI", "REDSTONE", "DIAMOND", "EMERALD", "QUARTZ"));
	
    	org.eps.tokenrewards.EnchantProcessor.setMiscToDef("playerkilltokens");
    	org.eps.tokenrewards.EnchantProcessor.setMiscToDef("mobkilltokens");
    	org.eps.tokenrewards.EnchantProcessor.setMiscToDef("miningtokens");
    	
    	Main.Config.set("misc", null);
    	DataUtil.saveConfig(Main.Config, Main.ConfigFile);
	}
}
