package org.whyisthisnecessary.eps.util;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.whyisthisnecessary.eps.Main;

public class LangUtil {
	
	private LangUtil() {}
	
	/**Returns the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to a defined message
	 * @return Returns the language message defined in lang.yml
	 */
	public static String getLangMessage(String langkey)
	{
		String prefix = Main.LangConfig.getString("prefix");
		String message =  Main.LangConfig.getString("messages."+langkey);
		
		if (prefix == null)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid prefix text! Please change your prefix in lang.yml.");
			prefix = "";
		}
		if (message == null)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid message text! Please change "+langkey+" in lang.yml.");
			message = "&cAn unexpected error occured while attempting to perform this command";
		}
		
		return ChatColor.translateAlternateColorCodes('&',prefix + message);
	}
	
	/**Sets the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to the defined message you want to set
	 * @param message The message you want to set the lang key to
	 */
	public static void setLangMessage(String langkey, String message)
	{
		Main.LangConfig.set("messages."+langkey, message);
		if (Main.LangFile.exists())
		{
            try {
            	Main.LangConfig.save(Main.LangFile);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
	
	/**Sets the language message defined in lang.yml if it does not already exist
	 * 
	 * @param langkey The key that correlates to the defined message you want to set
	 * @param message The message you want to set the lang key to
	 */
	public static void setDefaultLangMessage(String langkey, String message)
	{
		if (Main.LangConfig.get("messages."+langkey) == null)
		{
			Main.LangConfig.set("messages."+langkey, message);
			if (Main.LangFile.exists())
			{
				try {
					Main.LangConfig.save(Main.LangFile);
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
}
