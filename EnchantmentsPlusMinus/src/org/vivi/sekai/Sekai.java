package org.vivi.sekai;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.bukkit.command.PluginCommand;
import org.bukkit.plugin.Plugin;
import org.vivi.sekai.CommandProxy.CommandOptions;

/**
 * All in one dumpster fire library class
 * 
 * @author vivisan
 *
 */
public class Sekai
{
	private static final CommandProxy commandProxy = new CommandProxy();
	private static final char[] suffixes = new char[] { 'k', 'M', 'B', 'T', 'q', 'Q', 's', 'S' };
	/**
	 * Saves a resource from the specified Spigot plugin JAR to the specified
	 * destination if it does not exist
	 * 
	 * @param resourcePath The path to copy from
	 * @param dest         The file you want to copy to
	 */
	public static void saveDefaultFile(Plugin plugin, String resourcePath, File dest)
	{
		if (!dest.exists())
			try
			{
				dest.createNewFile();
				copyResource(plugin, resourcePath, dest);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
	}

	/**
	 * Copies a resource in the given plugin's jar from the specified internal file
	 * path to the specified file
	 * 
	 * @param internalPath The file path to copy from
	 * @param dest         The file to paste into
	 */
	public static void copyResource(Plugin plugin, String internalPath, File dest)
	{
		try
		{
			InputStream is = null;
			OutputStream os = null;
			try
			{
				is = plugin.getClass().getResourceAsStream(internalPath);
				os = new FileOutputStream(dest);
				byte[] buffer = new byte[1024];
				int length;
				while ((length = is.read(buffer)) != -1)
				{
					os.write(buffer, 0, length);
				}
			} catch (Exception e)
			{
				e.printStackTrace();
			} finally
			{
				is.close();
				os.close();
			}
		} catch (IOException e)
		{
		}
	}

	public static void registerCommand(PluginCommand command, CommandOptions options)
	{
		commandProxy.commandOptionsMap.put(command, options);
		command.setExecutor(commandProxy);
	}

	public static void connectCommand(PluginCommand command, Runnable runnable)
	{
		commandProxy.commandActivationMap.put(command, runnable);
	}

	/**
	 * Abbreviates a given number using suffixes, rounded to 2 decimal places. e.g.
	 * Converts 1,581,195 to 1.58M
	 * 
	 * @param value Value to abbreviate
	 * @return Abbreviated number in k, M, B, T and Q
	 */
	public static String abbreviate(double value)
	{
		if (value < 1000)
			return Double.toString(value);
		else
		{
			int exp = (int) (Math.log10(value) / 3);
			double norm = Math.floor(value * (100 / (Math.pow(1000, exp)))) * 0.01;
			return String.format("%." + 2 + "f%s", norm, suffixes[exp - 1]);
		}
	}

}
