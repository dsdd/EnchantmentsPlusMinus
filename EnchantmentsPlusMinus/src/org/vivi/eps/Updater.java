package org.vivi.eps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

import org.bukkit.configuration.file.YamlConfiguration;
import org.vivi.eps.util.DataUtil;
import org.vivi.eps.util.LangUtil;
import org.vivi.ids.IDS;

import com.google.common.io.Files;

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
		
		Main.plugin.addDefault(Main.Config, "open-enchant-gui-on-right-click", true);
		
		if (Main.Config.isSet("misc.applyfortuneon"))
    	{
    		Main.Config.set("applyfortuneon", Main.Config.get("misc.applyfortuneon"));
    		Main.Config.set("misc.applyfortuneon", null);
    	}
		if (!Main.Config.isSet("applyfortuneon"))
		{
			Main.Config.set("applyfortuneon", Arrays.asList("COAL", "LAPIS_LAZULI", "REDSTONE", "DIAMOND", "EMERALD", "QUARTZ"));
			DataUtil.saveConfig(Main.Config, Main.ConfigFile);
		}

    	org.eps.tokenrewards.EnchantProcessor.setMiscToDef("playerkilltokens");
    	org.eps.tokenrewards.EnchantProcessor.setMiscToDef("mobkilltokens");
    	org.eps.tokenrewards.EnchantProcessor.setMiscToDef("miningtokens");
    	
    	if (Main.Config.isSet("misc"))
    	{
    		Main.Config.set("misc", null);
    		DataUtil.saveConfig(Main.Config, Main.ConfigFile);
    	}
    	
    	// 1.3r - 1.4r compatibility
    	LangUtil.setDefaultLangMessage("token-pouch", "&eToken Pouch");
    	LangUtil.setDefaultLangMessage("claimed-token-pouch", "&2You received %tokens% tokens from the token pouch!");
    	LangUtil.setDefaultLangMessage("token-pouch-lore-1", "&7%tokens% Tokens");
    	LangUtil.setDefaultLangMessage("token-pouch-lore-2", "&7Right-Click to claim tokens in the pouch!");
    	LangUtil.setDefaultLangMessage("startup-message", "&2Thank you for using Enchantments+-!");
    	
    	// 1.4r - 1.5r compatibility
    	File checker = new File(Main.DataFolder, "updates.ids");
    	if (!checker.exists())
    		Main.createNewFile(checker);
    	IDS ids = new IDS(checker);
    	if (!ids.idExists("1.4r-updated"))
		{
			try {
				Files.move(Main.InCPTFile, new File(Main.DataFolder, "incompatibilities.yml"));
			} catch (IOException e) {
				e.printStackTrace();
			}
			Main.saveDefaultFile("/incompatibilities.yml", Main.InCPTFile);
		Main.InCPTConfig = YamlConfiguration.loadConfiguration(Main.InCPTFile);
			
		}
    	ids.set("1.4r-updated", true);
    	try {
			ids.save();
		} catch (IOException e) {
			e.printStackTrace();
		}
    	
    	LangUtil.setDefaultLangMessage("no-enchant-config-found", "&cCould not find the config for %enchant%! Automatically making one...");
    	LangUtil.setDefaultLangMessage("balance-display-in-gui", "&aYour Tokens � &e%balance%");
    	LangUtil.setDefaultLangMessage("invalid-enchant", "&cCould not find specified enchant. Try using /eps reload.");
    	
    	// 1.5r - 1.6r compatibility
    	setDefault("anvil-combining-enabled", true);
    	setDefault("use-money-economy-instead-of-tokens", false);
    	setDefault("show-vanilla-enchants-in-lore", true);
    	setDefault("show-enchant-descriptions-in-lore", true);
    	
    	// 1.6r - 1.7r compatibility
    	LangUtil.setDefaultLangMessage("cannot-open-gui", "&cYou cannot open the GUI at this time.");
    	LangUtil.setDefaultLangMessage("boosted-activate", "&aBoosted fortune!");
    	setDefault("enchant-lore-color", "&9");
    	setDefault("custom-lore-color.depth_strider", "&2");
    	setDefault("use-action-bar-instead-of-chat-inventory-full", true);
    	setDefault("disabled-enchants", new ArrayList<String>(Arrays.asList(new String[]{"an enchant you don't like"})));
    	setDefault("use-custom-fortune", true);
    	LangUtil.setDefaultLangMessage("next-page", "&aNext page");
    	LangUtil.setDefaultLangMessage("modify-gui", "&aModify GUI - Admin only");
    	LangUtil.setDefaultLangMessage("modify-lore-1", "&7Left-Click to edit this enchant");
    	LangUtil.setDefaultLangMessage("modify-lore-2", "&7Right-Click to remove this enchant");
    	LangUtil.setDefaultLangMessage("modifying-config", "&aType in the new value of %entry%.");
    	LangUtil.setDefaultLangMessage("modified-config", "&aChanged value!");
	
	}
	
	public static void setDefault(String path, Object value)
	{
		if (!Main.Config.isSet(path))
		{
			Main.Config.set(path, value);
			try {
				Main.Config.save(Main.ConfigFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
