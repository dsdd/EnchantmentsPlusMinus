package org.whyisthisnecessary.eps.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.Reloadable;


public class LangUtil implements Reloadable {
	
	private static Map<String, String> msgs = new HashMap<String, String>();
	private static String prefix = Main.LangConfig.getString("prefix");
	public static LangUtil lang = new LangUtil();

	private LangUtil() 
	{
		reload();
	}
	
	/**Returns the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to a defined message
	 * @return Returns the language message defined in lang.yml
	 */
	public static String getLangMessage(String langkey)
	{	
		return ChatColor.translateAlternateColorCodes('&',prefix + msgs.get(langkey));
	}
	
	/**Sets the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to the defined message you want to set
	 * @param message The message you want to set the lang key to
	 */
	public static void setLangMessage(String langkey, String message)
	{
		Main.LangConfig.set("messages."+langkey, message);
		msgs.put(langkey, message);
		if (Main.LangFile.exists())
            DataUtil.saveConfig(Main.LangConfig, Main.LangFile);
	}
	
	/**Sets the language message defined in lang.yml if it does not already exist
	 * 
	 * @param langkey The key that correlates to the defined message you want to set
	 * @param message The message you want to set the lang key to
	 */
	public static void setDefaultLangMessage(String langkey, String message)
	{
		if (!msgs.containsKey(langkey))
			setLangMessage(langkey, message);
	}
	
	/** Goes through null and blank checking
	 * 
	 * @param p The sender to message
	 * @param message The message to be sent
	 */
	public static void sendMessage(Player p, String langkey)
	{
		sendMessage((CommandSender)p, langkey);
	}
	
	public static void sendMessage(CommandSender p, String langkey)
	{
		String a = getLangMessage(langkey);
		if (a == null || a == "" || a == " ")
			return;
		p.sendMessage(a);
	}

	@Override
	public void reload() 
	{
		ConfigurationSection section = Main.LangConfig.getConfigurationSection("messages");
		for (Map.Entry<String, Object> entry : section.getValues(false).entrySet())
			msgs.put(entry.getKey(), entry.getValue().toString());
	}
}
