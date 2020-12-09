package org.whyisthisnecessary.eps;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;

public class EnchantGUI implements Listener, InventoryHolder {

	Inventory gui = Bukkit.createInventory(null, 36, "Enchantments");
	private Main plugin;
	private Player p;
	private PlayerInventory pgui;
	private String currentgui = "";
	
	public EnchantGUI(Main main){
        this.plugin = main;
        Bukkit.getPluginManager().registerEvents(this, plugin);
        CreatePanes();
    }
	
	public void OpenInventory(Player p, String gui1)
	{
		this.p = p;
        pgui = p.getInventory();
        p.openInventory(gui);
        LoadInventory(p, gui1);
	}
	
	public void LoadInventory(Player p, String gui)
	{
		clearInventory();
		@SuppressWarnings("unchecked")
		List<String> l = (List<String>) plugin.config.getList("guis."+gui+".enchants");
        String[] list = new String[l.size()];
        list = l.toArray(list);
        for (int i=0;i<list.length;i++)
        {
        	add(list[i]);
        }
        currentgui = gui;
	}
	
	public void clearInventory()
	{
		for (int i=0;i<gui.getSize();i++)
		{
			if (gui.getItem(i) != null)
			{
			if (!(gui.getItem(i).getType() == Material.BLACK_STAINED_GLASS_PANE))
			gui.clear(i);
			}
		}
	}
	
	@Override
	public Inventory getInventory()
	{
        return gui;
    }
	
	@EventHandler
    public void onpickaxeinventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() != null) return;

        e.setCancelled(true);

        final ItemStack clickedItem = e.getCurrentItem();

        // verify current item is not null
        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();

        // Using slots click is a best option for your inventory click's
        Map<Enchantment, Integer> enchs = clickedItem.getItemMeta().getEnchants();
        Enchantment ench = Enchantment.DURABILITY;
        for (Map.Entry<Enchantment, Integer> entry : enchs.entrySet()) {
            ench = entry.getKey();
        }
        
        UpgradeItem(ench, p);
        
    }

    // Cancel dragging in our Inventory
    @EventHandler
    public void onpickaxeinventoryClick(final InventoryDragEvent e) {
        if (e.getInventory() == gui) {
          e.setCancelled(true);
        }
    }
    
    public void add(String name)
    {
    	String cost;
    	Enchantment enchant = Enchantment.getByKey(NamespacedKey.minecraft(name));
    	String displayname = name.substring(0,1).toUpperCase() + name.substring(1);
    	displayname = displayname.replaceAll("_", " ");
    	displayname = WordUtils.capitalizeFully(displayname);
    	Material material = Material.getMaterial(plugin.config.getString("enchants."+name+".upgradeicon"));
    	String desc = plugin.config.getString("enchants."+name+".upgradedesc");
    	Integer maxlevel = (Integer) plugin.config.get("enchants."+enchant.getKey().getKey()+".maxlevel");
        if (!(pgui.getItemInMainHand().getEnchantmentLevel(enchant) >= maxlevel))
        {
        	String method = plugin.config.getString("enchants."+name+".cost.type");
        	cost = Integer.toString(getCost(method, enchant));
        
        }
        else
        {
            cost = "Maxed!";
        }
        ItemStack slot = new ItemStack(material, 1);
        ItemMeta meta = slot.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+displayname);
        meta.setLore(Arrays.asList(new String[] { "", 
        		ChatColor.GRAY+desc,
        		"",
        		ChatColor.GREEN+"Cost: "+cost,
        		ChatColor.GREEN+"Max Level: "+maxlevel,
        		ChatColor.GREEN+"Current Level: "+p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant)
        		}));
        meta.addEnchant(enchant, 1, true);
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        slot.setItemMeta(meta);
        gui.setItem(gui.firstEmpty(), slot);
    }
    
    public void CreatePanes()
    {
    	ItemStack slot = new ItemStack(Material.BLACK_STAINED_GLASS_PANE, 1);
    	ItemMeta meta = slot.getItemMeta();
    	meta.setDisplayName("");
    	slot.setItemMeta(meta);
    	for (int i=0;i<9;i++)
    	gui.setItem(i, slot);
    	gui.setItem(9, slot);
    	gui.setItem(17, slot);
    	gui.setItem(18, slot);
    	gui.setItem(26, slot);
    	for (int i=27;i<36;i++)
        	gui.setItem(i, slot);
    }
    
    public Integer getCost(String type, Enchantment enchant)
    {
    	Integer enchlvl = pgui.getItemInMainHand().getEnchantmentLevel(enchant);
    	if (type.equalsIgnoreCase("manual"))
    	{
    		return plugin.config.getInt("enchants."+enchant.getKey().getKey()+".cost."+(enchlvl+1));
    	}
    	else if (type.equalsIgnoreCase("linear"))
    	{
    		Integer startvalue = plugin.config.getInt("enchants."+enchant.getKey().getKey()+".cost.startvalue");
    		Integer value = plugin.config.getInt("enchants."+enchant.getKey().getKey()+".cost.value");
    		return (startvalue+value*enchlvl-value);
    	}
    	else if (type.equalsIgnoreCase("exponential"))
    	{
    		Double multi = plugin.config.getDouble("enchants."+enchant.getKey().getKey()+".cost.multi");
    		Integer startvalue = plugin.config.getInt("enchants."+enchant.getKey().getKey()+".cost.startvalue");
    		return (int)(startvalue*(Math.pow(multi, enchlvl)));
    	}
    	else
    	{
    		return 0;
    	}
    }
    
    public void UpgradeItem(Enchantment enchant, Player p)
    {
    	String method = plugin.config.getString("enchants."+enchant.getKey().getKey()+".cost.type");
    	Integer cost = (getCost(method, enchant));
    	if (!(pgui.getItemInMainHand().getEnchantmentLevel(enchant) >= plugin.config.getInt("enchants."+enchant.getKey().getKey()+".maxlevel")))
    	{
    		if (TokenManager.GetTokens(p.getName()) >= cost)
            {
	        	p.sendMessage(plugin.translatebukkittext("messages.upgradedpickaxe"));
	        	Integer newvalue = TokenManager.GetTokens(p.getName()) - cost;
	        	TokenManager.SetTokens(p.getName(), newvalue);
	        	p.getInventory().getItemInMainHand().addUnsafeEnchantment(enchant, pgui.getItemInMainHand().getEnchantmentLevel(enchant)+1);
	        	clearInventory();
	        	LoadInventory(p, currentgui);
            }
	        else
	        {
	        	p.sendMessage(plugin.translatebukkittext("messages.insufficienttokens"));
	        }
    	}
    	else
    	{
    		p.sendMessage(plugin.translatebukkittext("messages.maxedupgrade"));
    	}

    }
}
