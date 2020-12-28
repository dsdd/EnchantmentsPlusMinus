package org.whyisthisnecessary.eps.util;

import java.io.IOException;

import org.bukkit.ChatColor;
import org.whyisthisnecessary.eps.Main;

public class LangUtil {
	
	/**Returns the language message defined in lang.yml
	 * 
	 * @param langkey The key that correlates to a defined message
	 * @return Returns the language message defined in lang.yml
	 */
	public static String getLangMessage(String langkey)
	{
		return ChatColor.translateAlternateColorCodes('&',Main.LangConfig.getString("prefix")) + ChatColor.translateAlternateColorCodes('&', Main.LangConfig.getString("messages."+langkey));
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
