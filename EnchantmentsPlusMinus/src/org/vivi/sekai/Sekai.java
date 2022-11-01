package org.vivi.sekai;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.nio.charset.Charset;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

/**
 * All in one dumpster fire library class Don't judge the na
 * 
 * @author vivisan
 *
 */
public class Sekai
{
	public static char[] suffixes = new char[] { 'k', 'M', 'B', 'T', 'q', 'Q', 's', 'S' };

	/**
	 * Saves a resource from the specified Spigot plugin JAR to the specified
	 * destination if it does not exist
	 * 
	 * @param resourcePath The path to copy from
	 * @param dest     The file you want to copy to
	 */
	public static void saveDefaultFile(Plugin plugin, String resourcePath, File dest)
	{
		if (!dest.exists())
		{
			try
			{
				dest.createNewFile();
				Sekai.copyResource(plugin, resourcePath, dest);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Copies a resource in the given plugin's jar from the specified internal
	 * file path to the specified file
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

	/**
	 * Creates a new file, similar to file.createNewFile(), but without having to
	 * write try/catch
	 * 
	 * @param file The file to create
	 * @return Whether the file has been successfully created.
	 */
	public static boolean createNewFile(File file)
	{
		try
		{
			file.createNewFile();
			return true;
		} catch (IOException e)
		{
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Loads a YAML file using UTF-8 format, allowing the reading of Unicode
	 * characters.
	 * 
	 * @param file The file to load
	 * @return Loaded YAML configuration
	 */
	public static YamlConfiguration loadUTF8Configuration(File file)
	{
		YamlConfiguration config = new YamlConfiguration();
		try
		{
			config.load(new InputStreamReader(new FileInputStream(file), Charset.forName("UTF-8")));
		} catch (IOException | InvalidConfigurationException e)
		{
			e.printStackTrace();
		}
		return config;
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
			return Short.toString((short) value);
		else
		{
			int exp = (int) (Math.log10(value) / 3);
			char suffix = suffixes[exp - 1];
			double norm = Math.floor(value * (100 / (Math.pow(1000, exp)))) * 0.01;
			return String.format("%." + 2 + "f%s", norm, suffix);
		}
	}

}
