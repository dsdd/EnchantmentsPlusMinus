package org.vivi.sekai;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.Charset;

import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

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
	 * @param resource The path to copy from
	 * @param dest     The file you want to copy to
	 */
	public static void saveDefaultFile(Plugin plugin, String resource, File dest)
	{
		if (!dest.exists())
		{
			try
			{
				dest.createNewFile();
				Sekai.copyResource(plugin, resource, dest);
			} catch (IOException e)
			{
				e.printStackTrace();
			}
		}
	}

	/**
	 * Copies a resource in the specified plugin's jar from the specified internal
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
	 * Downloads a file from the specified URL.
	 * 
	 * @param filePath The path to download to
	 * @param fromUrl  The URL to download from
	 * @return The file
	 */
	public static File downloadFile(String filePath, String fromUrl)
	{
		try
		{
			File localFile = new File(filePath);
			if (!localFile.exists())
			{
				localFile.createNewFile();
			}
			URL url = new URL(fromUrl);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(filePath));
			URLConnection conn = url.openConnection();
			((HttpURLConnection) conn).setRequestMethod("GET");
			conn.setRequestProperty("User-Agent",
					"  Mozilla/5.0 (Windows; U; Windows NT 6.0; en-US; rv:1.9.1.2) Gecko/20090729 Firefox/3.5.2 (.NET CLR 3.5.30729)");
			conn.connect();
			InputStream in = conn.getInputStream();
			byte[] buffer = new byte[16384];

			int numRead;
			while ((numRead = in.read(buffer)) != -1)
			{
				out.write(buffer, 0, numRead);
			}
			if (in != null)
			{
				in.close();
			}
			if (out != null)
			{
				out.close();
			}
			out.flush();

			return localFile;
		} catch (Exception e)
		{
			return null;
		}
	}

	/**
	 * Gets the JAR file of a plugin
	 * 
	 * @param plugin The Spigot plugin
	 * @return The file
	 */
	public static File getJarFile(Plugin plugin)
	{
		try
		{
			Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
			getFileMethod.setAccessible(true);
			File file = (File) getFileMethod.invoke((JavaPlugin) plugin);
			return file;
		} catch (Exception e)
		{
			e.printStackTrace();
			return null;
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
	 * Abbreviates a value by using suffixes using a logarithm implementation.
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
