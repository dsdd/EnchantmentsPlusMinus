package org.vivi.eps.util;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.vivi.eps.EPS;
import org.vivi.eps.api.Reloadable;

public class Language implements Reloadable
{

	private static Map<String, String> msgs = new HashMap<String, String>();
	private static String prefix = EPS.languageFile.getString("prefix");
	public static Language lang = new Language();

	private Language()
	{
		reload();
	}

	/**
	 * Returns the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to a defined message
	 * @return Returns the language message defined in lang.yml
	 */
	public static String getLangMessage(String langkey, boolean includePrefix)
	{
		return ChatColor.translateAlternateColorCodes('&',
				includePrefix ? prefix + msgs.get(langkey) : msgs.get(langkey));
	}

	/**
	 * Returns the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to a defined message
	 * @return Returns the language message defined in lang.yml
	 */
	public static String getLangMessage(String langkey)
	{
		return getLangMessage(langkey, true);
	}

	/**
	 * Sets the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to the defined message you want to set
	 * @param message The message you want to set the lang key to
	 */
	public static void setLangMessage(String langkey, String message)
	{
		EPS.languageFile.set("messages." + langkey, message);
		msgs.put(langkey, message);
		EPS.languageFile.saveYaml();
	}

	/**
	 * Sets the language message defined in lang.yml if it does not already exist
	 * 
	 * @param langkey The key that correlates to the defined message you want to set
	 * @param message The message you want to set the lang key to
	 */
	public static void setDefaultLangMessage(String langkey, String message)
	{
		if (!msgs.containsKey(langkey))
			setLangMessage(langkey, message);
	}

	/**
	 * Goes through null and blank checking
	 * 
	 * @param player  The sender to message
	 * @param message The message to be sent
	 */
	public static void sendMessage(Player player, String langkey)
	{
		sendMessage((CommandSender) player, langkey);
	}

	public static void sendMessage(CommandSender sender, String langkey)
	{
		String a = getLangMessage(langkey);
		if (a == null || a == "" || a == " ")
			return;
		sender.sendMessage(a);
	}

	@Override
	public void reload()
	{
		ConfigurationSection section = EPS.languageFile.getConfigurationSection("messages");
		for (Map.Entry<String, Object> entry : section.getValues(false).entrySet())
			msgs.put(entry.getKey(), entry.getValue().toString());
	}
}
