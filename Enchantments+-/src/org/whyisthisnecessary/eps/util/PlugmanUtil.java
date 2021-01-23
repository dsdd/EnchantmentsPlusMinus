package org.whyisthisnecessary.eps.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.URLClassLoader;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.PluginCommand;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.RegisteredListener;
import org.whyisthisnecessary.eps.Main;

public class PlugmanUtil {
	
	private PlugmanUtil() {}
	
	/** Gets a Plugin from a file.
	 * 
	 * @param file The file to get the Plugin from.
	 * @return The Plugin
	 */
	public static Plugin getPluginFromFile(File file)
	{
		File tempfolder = new File(Main.DataFolder, "Temp");
	    if (!tempfolder.exists()) tempfolder.mkdirs();
		try {
			ZipFile zipFile = new ZipFile(file);
			ZipEntry entry = zipFile.getEntry("plugin.yml");
		    InputStream is = zipFile.getInputStream(entry);
		    OutputStream os = null;
		    
		    try {
		        os = new FileOutputStream(tempfolder+"/pluginpack.yml");
		        byte[] buffer = new byte[16384];
		        int length;
		        while ((length = is.read(buffer)) != -1) {
		            os.write(buffer, 0, length);
		        }
		    }
		    catch (Exception e)
		    {
		    	e.printStackTrace();
		    }
		    finally {
		        is.close();
		        os.close();
		        zipFile.close();
		    }
		}
		catch (Exception e) { e.printStackTrace();}
		File pluginyaml = new File(tempfolder, "pluginpack.yml");
	    FileConfiguration fcg = YamlConfiguration.loadConfiguration(pluginyaml);
	    String pluginname = fcg.getString("name");
		if (Bukkit.getPluginManager().isPluginEnabled(pluginname))
		    return Bukkit.getPluginManager().getPlugin(pluginname);
		else { try {
			Bukkit.getPluginManager().loadPlugin(file); }catch (Exception e) {e.printStackTrace();}
		    pluginyaml.delete();
			return Bukkit.getPluginManager().getPlugin(pluginname); }
	}
	
	/**
     * Unload a plugin.
     *
     * @param plugin the plugin to unload
     * @return the message to send to the user.
     * 
     * By r-clancy, all credits to him
     */
	@SuppressWarnings("unchecked")
    public static Boolean unload(Plugin plugin) {

        String name = plugin.getName();

        PluginManager pluginManager = Bukkit.getPluginManager();

        SimpleCommandMap commandMap = null;

        List<Plugin> plugins = null;

        Map<String, Plugin> names = null;
        Map<String, Command> commands = null;
        Map<Event, SortedSet<RegisteredListener>> listeners = null;

        boolean reloadlisteners = true;

        if (pluginManager != null) {

            pluginManager.disablePlugin(plugin);

            try {

                Field pluginsField = Bukkit.getPluginManager().getClass().getDeclaredField("plugins");
                pluginsField.setAccessible(true);
                plugins = (List<Plugin>) pluginsField.get(pluginManager);

                Field lookupNamesField = Bukkit.getPluginManager().getClass().getDeclaredField("lookupNames");
                lookupNamesField.setAccessible(true);
                names = (Map<String, Plugin>) lookupNamesField.get(pluginManager);

                try {
                    Field listenersField = Bukkit.getPluginManager().getClass().getDeclaredField("listeners");
                    listenersField.setAccessible(true);
                    listeners = (Map<Event, SortedSet<RegisteredListener>>) listenersField.get(pluginManager);
                } catch (Exception e) {
                    reloadlisteners = false;
                }

                Field commandMapField = Bukkit.getPluginManager().getClass().getDeclaredField("commandMap");
                commandMapField.setAccessible(true);
                commandMap = (SimpleCommandMap) commandMapField.get(pluginManager);

                Field knownCommandsField = SimpleCommandMap.class.getDeclaredField("knownCommands");
                knownCommandsField.setAccessible(true);
                commands = (Map<String, Command>) knownCommandsField.get(commandMap);

            } catch (NoSuchFieldException e) {
                e.printStackTrace();
                return false;
            } catch (IllegalAccessException e) {
                e.printStackTrace();
                return false;
            }

        }

        pluginManager.disablePlugin(plugin);

        if (plugins != null && plugins.contains(plugin))
            plugins.remove(plugin);

        if (names != null && names.containsKey(name))
            names.remove(name);

        if (listeners != null && reloadlisteners) {
            for (SortedSet<RegisteredListener> set : listeners.values()) {
                for (Iterator<RegisteredListener> it = set.iterator(); it.hasNext(); ) {
                    RegisteredListener value = it.next();
                    if (value.getPlugin() == plugin) {
                        it.remove();
                    }
                }
            }
        }

        if (commandMap != null) {
            for (Iterator<Map.Entry<String, Command>> it = commands.entrySet().iterator(); it.hasNext(); ) {
                Map.Entry<String, Command> entry = it.next();
                if (entry.getValue() instanceof PluginCommand) {
                    PluginCommand c = (PluginCommand) entry.getValue();
                    if (c.getPlugin() == plugin) {
                        c.unregister(commandMap);
                        it.remove();
                    }
                }
            }
        }

        // Attempt to close the classloader to unlock any handles on the plugin's jar file.
        ClassLoader cl = plugin.getClass().getClassLoader();

        if (cl instanceof URLClassLoader) {

            try {

                Field pluginField = cl.getClass().getDeclaredField("plugin");
                pluginField.setAccessible(true);
                pluginField.set(cl, null);

                Field pluginInitField = cl.getClass().getDeclaredField("pluginInit");
                pluginInitField.setAccessible(true);
                pluginInitField.set(cl, null);

            } catch (NoSuchFieldException | SecurityException | IllegalArgumentException | IllegalAccessException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

            try {

                ((URLClassLoader) cl).close();
            } catch (IOException ex) {
                Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

        // Will not work on processes started with the -XX:+DisableExplicitGC flag, but lets try it anyway.
        // This tries to get around the issue where Windows refuses to unlock jar files that were previously loaded into the JVM.
        System.gc();

        return true;

    }
}
