package org.vivi.eps;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;
import org.vivi.eps.util.ConfigSettings;
import org.vivi.eps.util.Language;

public class Updater
{

	/**
	 * Makes updates between versions compatible.
	 */
	public void makeCompatible()
	{
		// If you want to migrate from 1.6r and below to the latest version, use a
		// 1.9r-7 and below release as an intermediate.

		// START 1.9r-8 to 1.9r-9 conversion

		EPS.configFile.yaml.options().copyDefaults(true);
		EPS.languageFile.yaml.options().copyDefaults(true);

		EPS.configFile.supersedePath("show-enchant-lore", "show-enchants");
		EPS.configFile.supersedePath("use-money-economy-instead-of-tokens", "use-vault-economy");
		EPS.configFile.supersedePath("use-action-bar-instead-of-chat-inventory-full",
				"use-action-bar-instead-of-chat-when-inventory-full");
		EPS.configFile.supersedePath("applyfortuneon", "apply-fortune-on");
		EPS.configFile.supersedePath("playerkilltokens", "player-kill-reward");
		EPS.configFile.supersedePath("mobkilltokens", "mob-kill-reward");
		EPS.configFile.supersedePath("miningtokens", "mining-reward");
		EPS.configFile.supersedePath("custom-lore-color", "enchant-specific-lore-color");
		EPS.languageFile.supersedePath("upgradedpickaxe", "upgraded-item");

		if (EPS.configFile.contains("global-cost-type"))
		{
			EPS.configFile.set("global-cost-type", null);
			EPS.configFile.set("global-cost.enabled", false);
			EPS.configFile.set("global-cost.cost", "69420*%lvl%");
		}

		File guiLoreFile = new File(EPS.plugin.getDataFolder(), "gui_lore.yml");
		if (guiLoreFile.exists())
		{
			EPS.languageFile.set("enchant-gui-item-lore",
					YamlConfiguration.loadConfiguration(guiLoreFile).getStringList("lore"));
			guiLoreFile.delete();
		}

		EPS.configFile.addDefault("enable-enchant-signs", false);
		EPS.configFile.addDefault("abbreviate-large-numbers", true);

		EPS.configFile.set("do-not-add-lore-to", new ArrayList<String>(Arrays.asList(new String[] {
				"an item e.g. BEDROCK that you do not want lore added to due to plugin interference" })));
		EPS.configFile.set("use-custom-fortune", null);
		EPS.configFile.set("show-vanilla-enchants-in-lore", null);

		for (String key : EPS.incompatibilitiesFile.getKeys(false))
			EPS.incompatibilitiesFile.set(key + ".items", null);

		Language.setDefaultLangMessage("enchant-sign-initiating-line", "[EPSEnchant]");
		Language.setDefaultLangMessage("enchant-sign-success", "&1[Enchant]");
		Language.setDefaultLangMessage("enchant-sign-failure", "&4[Enchant]");
		Language.setDefaultLangMessage("enchants-gui-label", "Enchantments");
		EPS.languageFile.addDefault("modify-gui.toggle-label", "&aModify GUI - Admin Only");
		EPS.languageFile.addDefault("modify-gui.modify-enchant-lore",
				new ArrayList<String>(Arrays.asList(new String[] { "&7Left-Click to edit this enchant",
						"&7Right-Click to remove this enchant", "&7Shift-Left-Click to enable slot movement" })));
		EPS.languageFile.addDefault("modify-gui.add-enchant", "&aAdd Enchant");
		EPS.languageFile.addDefault("modify-gui.move-notification", "&aChoose a slot to move this enchant.");

		// Convert enchant configs
		for (File file : (EPS.enchantsFolder.listFiles()))
		{
			FileConfiguration configuration = YamlConfiguration.loadConfiguration(file);
			String costType = configuration.getString("cost.type");
			if (costType == null)
				continue;
			if (costType.equalsIgnoreCase("linear"))
				configuration.set("cost", Double.toString(configuration.getDouble("cost.value")) + " * %lvl% + "
						+ Double.toString(configuration.getDouble("cost.startvalue")));
			else if (costType.equalsIgnoreCase("exponential"))
				configuration.set("cost", Double.toString(configuration.getDouble("cost.startvalue")) + " * "
						+ Double.toString(configuration.getDouble("cost.multi")) + "^(%lvl%-1)");
			else if (costType.equalsIgnoreCase("manual"))
				configuration.set("cost.type", null);
			try
			{
				configuration.save(file);
			} catch (IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		EPS.configFile.saveYaml();
		EPS.languageFile.saveYaml();
		EPS.incompatibilitiesFile.saveYaml();
		// END

	}

	public void autoUpdate()
	{
		if (!ConfigSettings.isAutoUpdating())
			return;
		try
		{
			File versionFile = downloadFile(EPS.dataFolder.getPath() + "/version.txt",
					"https://raw.githubusercontent.com/dsdd/EnchantmentsPlusMinus/main/VERSION");
			String version = readFile(versionFile).substring(0, ((int) versionFile.length()) - 1);
			JavaPlugin epsPlugin = (JavaPlugin) EPS.plugin;
			Method getFileMethod = JavaPlugin.class.getDeclaredMethod("getFile");
			getFileMethod.setAccessible(true);
			File epsJarFile = (File) getFileMethod.invoke(epsPlugin);
			EPS.logger.log(Level.INFO, "Currently running " + epsPlugin.getDescription().getVersion());
			if (!epsPlugin.getDescription().getVersion().equalsIgnoreCase(version))
			{
				EPS.logger.log(Level.INFO, "Downloading updated plugin JAR... (" + version + ")");
				downloadFile(epsJarFile.getPath(),
						"https://github.com/dsdd/EnchantmentsPlusMinus/releases/latest/download/Enchantments+-.jar");
				EPS.logger.log(Level.INFO, "Finished downloading.");
			}
			versionFile.delete();
		} catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException e)
		{
			e.printStackTrace();
		}
	}

	private File downloadFile(String localFileName, String fromUrl)
	{
		try
		{
			File localFile = new File(localFileName);
			if (!localFile.exists())
			{
				localFile.createNewFile();
			}
			URL url = new URL(fromUrl);
			OutputStream out = new BufferedOutputStream(new FileOutputStream(localFileName));
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

	public String readFile(File file)
	{
		String str = null;
		try
		{
			str = new String(Files.readAllBytes(file.toPath()));
		} catch (IOException e)
		{
			e.printStackTrace();
		}
		return str;
	}
}
