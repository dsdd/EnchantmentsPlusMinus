package org.whyisthisnecessary.eps;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.UnknownDependencyException;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin implements Listener, CommandExecutor {
    
	private Main plugin;
	private File DataFolder;
	private File PackFolder;
	private File userstore;
	public FileConfiguration config;
	private FileConfiguration usconfig;
	public List<String> list;
	public List<Material> fortuneapply = new ArrayList<Material>(Arrays.asList());
	
	@Override
	public void onEnable() 
	{
    	plugin = this;
   
    	saveDefaultConfig();

	    Bukkit.getPluginManager().registerEvents(this, plugin);
		DataFolder = new File(getDataFolder(), "data");
        if (!DataFolder.exists()) {
            DataFolder.mkdirs();
         }
        PackFolder = new File(getDataFolder(), "packs");
        
        userstore = new File(DataFolder, "usernamestore.yml");
	    if (!userstore.exists()) {
	        userstore.getParentFile().mkdirs();
	        try {
				userstore.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	     }
	    usconfig = YamlConfiguration.loadConfiguration(userstore);
	    config = plugin.getConfig();
	    
	    plugin.getCommand("eps").setExecutor(this);
	    plugin.getCommand("tokens").setExecutor(new gettokens(this));
    	plugin.getCommand("enchants").setExecutor(new enchants(this));
    	plugin.getCommand("scrap").setExecutor(new scrap(this));
    	
    	list =  config.getStringList("misc.applyfortuneon");
    	for (int i=0;i<list.size();i++)
    	fortuneapply.add(Material.getMaterial(list.get(i)));
    	
    	if (!PackFolder.exists()) {
            PackFolder.mkdirs();
            
        }
    	else
    	{
    		File[] files = PackFolder.listFiles();
    	    if (files != null)
    	    {
    	    	for (File file : files)
    	    	{
    	    		Plugin plugin = null;
					try {
						plugin = Bukkit.getPluginManager().loadPlugin(file);
						Bukkit.getPluginManager().enablePlugin(plugin);
					} catch (UnknownDependencyException | InvalidDescriptionException | InvalidPluginException e) {
						e.printStackTrace();
					}
    	    	}

    	    }
    	}
	}
    
    @EventHandler
	public void onJoin(PlayerJoinEvent e) 
	{
		Player p = e.getPlayer();

		UUID uuid = p.getUniqueId();
		String stringuuid = uuid.toString();
			
		File datafile = new File(DataFolder, stringuuid+".yml");
	    if (!datafile.exists()) {
	        datafile.getParentFile().mkdirs();
	        try {
				datafile.createNewFile();
			} catch (IOException e1) {
				e1.printStackTrace();
			}
	     }
	        
	    FileConfiguration dfconfig = YamlConfiguration.loadConfiguration(datafile);
	        
		dfSetDefault(dfconfig, "uuid", stringuuid);
		dfSetDefault(dfconfig, "player-name", p.getName());
		dfSetDefault(dfconfig, "tokens", 0);
		usconfig.set(p.getName(), stringuuid);
		
		try {
			dfconfig.save(datafile);
			usconfig.save(userstore);
		} catch (IOException e1) {
			e1.printStackTrace();
		}
	}
	
	public void dfSetDefault(FileConfiguration dfconfig, String s1, Object s2)
	{
		if (dfconfig.get(s1) == null)
		{
			dfconfig.set(s1, s2);
		}
	}
	
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (args.length == 0)
		{
			sender.sendMessage(ChatColor.RED + "Usage:");
			sender.sendMessage(ChatColor.RED + "/eps reload");
			sender.sendMessage(ChatColor.RED + "/eps settokens [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps changetokens [plr] [amount]");
			sender.sendMessage(ChatColor.RED + "/eps enchant [enchant] [lvl]");
			return false;
		}
		
		if (args[0].equalsIgnoreCase("reload"))
		{
			String perm = "eps.admin.reload";
			if (!sender.hasPermission(perm))
			{
				sender.sendMessage(translatebukkittext("insufficientpermission"));
				return false;
			}
			reloadConfig();
			config = plugin.getConfig();
			usconfig = YamlConfiguration.loadConfiguration(userstore);
			sender.sendMessage(translatebukkittext("messages.reloadconfig"));
			list = config.getStringList("misc.applyfortuneon");
			for (int i=0;i<list.size();i++)
	    	fortuneapply.add(Material.getMaterial(list.get(i)));
			File[] files = PackFolder.listFiles();
    	    if (files != null)
    	    {
    	    	for (File file : files)
    	    	{
    	    		Plugin plugin = null;
					try {
						plugin = Bukkit.getPluginManager().loadPlugin(file);
						Bukkit.getPluginManager().disablePlugin(plugin);
						Bukkit.getPluginManager().enablePlugin(plugin);
					} catch (UnknownDependencyException | InvalidDescriptionException | InvalidPluginException e) {
						e.printStackTrace();
					}
    	    	}

    	    }
			return false;
		}
		
		if (args[0].equalsIgnoreCase("settokens"))
		{
			String perm = "eps.admin.settokens";
			if (!sender.hasPermission(perm))
			{
				sender.sendMessage(translatebukkittext("insufficientpermission"));
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps settokens [player] [amount]"));
				return true;
			}
			try
			{
			TokenManager.SetTokens(args[1],Integer.parseInt(args[2]));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aSet "+args[1]+"'s tokens to "+Integer.parseInt(args[2])));
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps settokens [player] [amount]"));
			}
			return false;
		}
		
		if (args[0].equalsIgnoreCase("changetokens"))
		{
			String perm = "eps.admin.changetokens";
			if (!sender.hasPermission(perm))
			{
				sender.sendMessage(translatebukkittext("insufficientpermission"));
				return false;
			}
			if (args.length < 3)
			{
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changetokens [player] [amount]"));
				return true;
			}
			try
			{
			TokenManager.ChangeTokens(args[1],Integer.parseInt(args[2]));
			sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&aChanged "+args[1]+"'s tokens by "+Integer.parseInt(args[2])));
			}
			catch(NullPointerException e)
			{
				e.printStackTrace();
				sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps changetokens [player] [amount]"));
			}
			return false;
		}
		
		if (args[0].equalsIgnoreCase("enchant"))
		{
			String perm = "eps.admin.enchant";
			
			if (!(sender instanceof Player))
			{
				sender.sendMessage(translatebukkittext("messages.invalidplayertype"));
				return true;
			}
			
			Player p = (Player) sender;
			
			if (p.hasPermission(perm))
	        {
				if (args.length < 3)
				{
					sender.sendMessage(ChatColor.translateAlternateColorCodes('&', "&cUsage: /eps enchant [enchant] [lvl]"));
					return true;
				}
				if (p.getInventory().getItemInMainHand().getAmount() > 0)
				{
					p.getInventory().getItemInMainHand().addUnsafeEnchantment(Enchantment.getByKey(NamespacedKey.minecraft(args[1])), Integer.parseInt(args[2]));
					return true;
				}
				else
				{
					p.sendMessage(translatebukkittext("messages.invaliditem"));
					return false;
				}
	        }
			else
			{
				p.sendMessage(translatebukkittext("messages.insufficientpermission"));
				return false;
			}
		}
		
		return false;
	}
	
	public String translatebukkittext(String text)
	{
		return ChatColor.translateAlternateColorCodes('&',config.getString("prefix")) + ChatColor.translateAlternateColorCodes('&', config.getString(text));
	}
}
