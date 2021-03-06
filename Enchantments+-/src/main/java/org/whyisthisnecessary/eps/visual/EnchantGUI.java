package org.whyisthisnecessary.eps.visual;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.whyisthisnecessary.eps.EPS;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.EPSConfiguration;
import org.whyisthisnecessary.eps.economy.Economy;
import org.whyisthisnecessary.eps.util.Dictionary;
import org.whyisthisnecessary.eps.util.LangUtil;

public class EnchantGUI implements Listener {

	public static Map<Player, Inventory> GUIs = new HashMap<Player, Inventory>();
	public static Map<Player, String> guiNames = new HashMap<Player, String>();
	public static Map<List<String>, List<Enchantment>> incpts = new HashMap<List<String>, List<Enchantment>>();
	private static List<Material> hoes = new ArrayList<Material>(Arrays.asList(new Material[]{Material.WOODEN_HOE, Material.STONE_HOE, Material.IRON_HOE, Material.matchMaterial("GOLDEN_HOE"), Material.matchMaterial("GOLD_HOE"), Material.DIAMOND_HOE, Material.matchMaterial("NETHERITE_HOE")}));
	private static final Economy economy = EPS.getEconomy();
	private static final Dictionary dictionary = EPS.getDictionary();
	public static void setupGUI(Player player)
	{
		Inventory inv = Bukkit.createInventory(player, 36, "Enchantments");
		createPanes(inv);
		GUIs.put(player, inv);
		guiNames.put(player, "null");
	}
	
	public static void openInventory(Player player, String listname)
	{
		guiNames.put(player, listname);
		player.openInventory(GUIs.get(player));
		loadInventory(player, listname);
		
	}
	
	public static void loadInventory(Player p, String guiname)
	{
		Inventory inv = GUIs.get(p);
		inv.clear();
		createPanes(inv);
		List<String> l = Main.GuisConfig.getStringList("guis."+guiname+".enchants");
        for (String i : l)
        	add(p, inv, i);

        ItemStack i = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(LangUtil.getLangMessage("balance-display-in-gui").replaceAll("%balance%", economy.getBalance(p.getName()).toString()));
        i.setItemMeta(meta);
        inv.setItem(4, i);
        inv.setItem(31, i);
	}
	
	@SuppressWarnings("deprecation")
	public static void createPanes(Inventory gui)
    {
		ItemStack slot = EPS.onLegacy() ? new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short)15) 
										: new ItemStack(Material.matchMaterial("BLACK_STAINED_GLASS_PANE"), 1);
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
	
	public static void add(Player p, Inventory inv, String name)
    {
		ItemStack mainhand = p.getInventory().getItemInMainHand();
		ItemMeta mainmeta = mainhand.getItemMeta();
    	String cost;
    	Enchantment enchant = dictionary.findEnchant(name);
    	if (enchant == null) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid enchantment name "+name+"!");
    		return;
    	}
    	EPSConfiguration config = EPSConfiguration.getConfiguration(enchant);
    	String displayname = EnchantMetaWriter.enchantnames.get(enchant);
    	Material material = config.getMaterial("upgradeicon");
    	String desc = config.getString("upgradedesc");
    	Integer maxlevel = config.getInt("maxlevel");
    	
        if (!(mainmeta.getEnchantLevel(enchant) >= maxlevel))
        {
        	String method = config.getString("cost.type");
        	cost = Integer.toString(getCost(method, enchant, mainmeta.getEnchantLevel(enchant), 1));
        }
        else
        {
        	if (!p.hasPermission("eps.admin.bypassmaxlevel"))
        		cost = "Maxed!";
        	else
        	{
        		String method = config.getString("cost.type");
            	cost = Integer.toString(getCost(method, enchant, mainmeta.getEnchantLevel(enchant), 1));
        	}
        }
        
        if (desc == null)
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid upgrade description for enchant "+name.toUpperCase()+"!");
        if (maxlevel == 0)
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Max level for enchant"+name.toUpperCase()+" is zero! Is this intentional?");
        if (material == null)
        	Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid material type for enchantment "+name.toUpperCase()+". Setting to default BOOK.");

        ItemStack slot = material == null ? new ItemStack(Material.BOOK, 1) : new ItemStack(material, 1);
        ItemMeta meta = slot.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+displayname);
        
        List<String> lore = EnchantMetaWriter.getDescription(enchant);
		        
        for (String s : Main.GuiLoreConfig.getStringList("lore"))
        	lore.add(ChatColor.translateAlternateColorCodes('&', s.replaceAll("%cost%", cost).replaceAll("%maxlevel%", maxlevel.toString()).replaceAll("%currentlevel%", Integer.toString(mainmeta.getEnchantLevel(enchant)))));
        
        meta.setLore(lore);
        meta.addEnchant(enchant, 1, true);
        meta.addItemFlags(ItemFlag.HIDE_ENCHANTS);
        slot.setItemMeta(meta);
        inv.setItem(inv.firstEmpty(), slot);
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onpickaxeinventoryDrag(final InventoryDragEvent e) {
		if (EPS.onLegacy())
		{
			if (!isSimilarInventory(e.getInventory(), GUIs.get(e.getWhoClicked()))) return;
	        if (!compareInvs(e.getInventory(), GUIs.get(e.getWhoClicked()))) return;
	          e.setCancelled(true);
		}
		else
		{
			if (e.getInventory() == GUIs.get(e.getWhoClicked()))
				e.setCancelled(true);
		}
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onpickaxeinventoryClick(final InventoryClickEvent e) {
        if (e.getInventory().getHolder() != e.getWhoClicked()) return;
        if (EPS.onLegacy())
		{
			if (!isSimilarInventory(e.getInventory(), GUIs.get(e.getWhoClicked()))) return;
	        if (!compareInvs(e.getInventory(), GUIs.get(e.getWhoClicked()))) return;
	          e.setCancelled(true);
		}
		else
		{
			if (e.getInventory() != GUIs.get(e.getWhoClicked())) return;
				e.setCancelled(true);
		}

        final ItemStack clickedItem = e.getCurrentItem();

        if (clickedItem == null || clickedItem.getType() == Material.AIR) return;

        final Player p = (Player) e.getWhoClicked();
        Map<Enchantment, Integer> enchs = clickedItem.getItemMeta().getEnchants();
        Enchantment ench = null;
        
        for (Map.Entry<Enchantment, Integer> entry : enchs.entrySet())
            ench = entry.getKey();
        
        if (ench == null) 
        	return;
        
        if (e.isLeftClick())
            UpgradeItem(ench, p, 1);
        
        else if (e.isRightClick())
        	UpgradeItem(ench, p, 5);
        
        else if (e.isShiftClick())
        	UpgradeItem(ench, p, 50);
        
    }
	
	/**Returns the cost of the next specified levels of an enchant
	 * 
	 * @param type The type of cost increase used
	 * @param enchant The enchantment to calculate
	 * @param enchlvl The current enchantment level
	 * @param multi The amount of levels to be increased by
	 * @return Returns the cost of the next specified levels of an enchant
	 */
	public static Integer getCost(String type, Enchantment enchant, Integer enchlvl, Integer multi)
    {
		EPSConfiguration config = EPSConfiguration.getConfiguration(enchant);
    	if (type.equalsIgnoreCase("manual"))
    	{
    		Integer val = config.getInt("cost."+(enchlvl+1));
    		for (int i=1;i<multi;i++)
    		{
    			val = val + config.getInt("cost."+(enchlvl+1+i));
    		}
    		return val;
    	}
    	else if (type.equalsIgnoreCase("linear"))
    	{
    		Integer startvalue = config.getInt("cost.startvalue");
    		Integer value = config.getInt("cost.value");
    		Integer fin = enchlvl == 0 ? 0 : startvalue;
    		for (int i=enchlvl+1;i<=multi;i++)
    			fin = fin + (value*multi);
    		return fin;
    	}
    	else if (type.equalsIgnoreCase("exponential"))
    	{
    		Double multi1 = config.getDouble("cost.multi");
    		Integer startvalue = config.getInt("cost.startvalue");
    		return (int)(startvalue*Math.pow((Math.pow(multi1, enchlvl+1)), multi));
    	}
    	else
    	{
    		return Integer.MAX_VALUE;
    	}
    }
	
	public void UpgradeItem(Enchantment enchant, Player p, Integer multi)
    {
		EPSConfiguration config = EPSConfiguration.getConfiguration(enchant);
		ItemStack mainhand = p.getInventory().getItemInMainHand();
		ItemMeta mainmeta = mainhand.getItemMeta();
    	String method = config.getString("cost.type");
    	Integer cost = (getCost(method, enchant, mainmeta.getEnchantLevel(enchant), multi));
    	if (!(mainmeta.getEnchantLevel(enchant)+multi-1 >= config.getInt("maxlevel")) || p.hasPermission("eps.admin.bypassmaxlevel"))
    	{
    		if (!p.hasPermission("eps.admin.bypassincompatibilities"))
    		{
    			for (Map.Entry<List<String>, List<Enchantment>> entry : incpts.entrySet())
    			{
    				if (entry.getKey().contains(mainhand.getType().name()))
    				{
    					if (entry.getValue().contains(enchant))
				    		for (Enchantment e : entry.getValue())
				    			if (e != null)
					    			if (mainmeta.hasEnchant(e) && e != enchant)
					    			{
					    				LangUtil.sendMessage(p, "lockedupgrade");
					    				return;
					    			}
    				}
    			}
    		}

    		if (economy.getBalance(p.getName()) >= cost)
            {
	        	LangUtil.sendMessage(p, "upgradedpickaxe");
	        	mainhand.addUnsafeEnchantment(enchant, mainmeta.getEnchantLevel(enchant)+multi);
	        	Integer newvalue = economy.getBalance(p.getName()) - cost;
	        	economy.setBalance(p.getName(), newvalue);
	        	mainhand.setItemMeta(EnchantMetaWriter.getWrittenMeta(mainhand));
	        	loadInventory(p, guiNames.get(p));
            }
	        else
	        {
	        	LangUtil.sendMessage(p, "insufficienttokens");
	        }
    	}
    	else if ((mainmeta.getEnchantLevel(enchant) >= config.getInt("maxlevel")))
    	{
    		LangUtil.sendMessage(p, "exceedmaxlvl");
    	}
    	else
    	{
    		LangUtil.sendMessage(p, "maxedupgrade");
    	}

    }
	
	public static boolean isSimilarInventory(Inventory first, Inventory second)
	{
	    if(first == null && second == null) return true;
	    if((first == null && second != null) || (first != null && second == null)) return false;
	    if(first.getType() != second.getType()) return false;
	    ItemStack[] firstContents = first.getContents();
	    ItemStack[] secondContents = second.getContents();
	    if(firstContents.length != secondContents.length) return false;
	    for(int i = 0; i < firstContents.length; i++)
	    {
	        if(firstContents[i] == null && secondContents[i] == null) continue;
	        else if(firstContents[i] == null && secondContents[i] != null) return false;
	        else if(secondContents[i] == null && firstContents[i] != null) return false;
	        else if(!firstContents[i].isSimilar(secondContents[i])) return false;
	    }
	    return true;
	}
	
	public static boolean compareInvs(Inventory inv0, Inventory inv1){
		if (!inv0.getHolder().equals(inv1.getHolder())) return false;
		if (inv0.getSize() != inv1.getSize()) return false;
		if (!inv0.getType().equals(inv1.getType())) return false;
		if (inv0.getSize() != inv1.getSize()) return false;
		if (inv0.getViewers().size() != inv1.getViewers().size()) return false;
		for (int index = 0; index < inv0.getSize(); index++){
		ItemStack a = inv0.getItem(index);
		ItemStack b = inv1.getItem(index);
		if (!((a == null && b == null) || a.equals(b))) return false;
		}
		for (int index = 0; index < inv0.getViewers().size(); index++){
		HumanEntity a = inv0.getViewers().get(index);
		HumanEntity b = inv1.getViewers().get(index);
		if (!a.getOpenInventory().getTitle().equals(b.getOpenInventory().getTitle())) return false;
		if (!((a == null && b == null) || a.equals(b))) return false;
		}
		return true;
		}
	
	@EventHandler
	public void onPlayerInteract(PlayerInteractEvent e)
	{
		if (e.getAction().equals(Action.RIGHT_CLICK_AIR) || e.getAction().equals(Action.RIGHT_CLICK_BLOCK))
		{
			PlayerInventory inv = e.getPlayer().getInventory();
			Material m = inv.getItemInMainHand().getType();
			if (m.equals(Material.BOW) ||
					m.equals(Material.matchMaterial("CROSSBOW")) ||
					inv.getItemInOffHand().getType().equals(Material.SHIELD) ||
					hoes.contains(m) ||
					m.equals(Material.FISHING_ROD) ||
					(e.getClickedBlock() != null && e.getClickedBlock().getType().isInteractable()))
				return;
			if (Main.Config.getBoolean("open-enchant-gui-on-right-click") == true)
				Main.EnchantsCMD.onCommand(e.getPlayer(), Bukkit.getPluginCommand("enchants"), "enchants", new String[] {"dontshow"});
		}
	}
	
	public static void setupInCPTS()
	{
		ConfigurationSection cs = Main.InCPTConfig.getConfigurationSection("incompatibilities");
		
		if (cs == null)
		{
			Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid incompatibilities.yml file! Delete the current one to generate a new one.");
			return;
		}
		Set<String> a = cs.getKeys(false);
		for (String i : a) 
		{
			List<Enchantment> enchs = new ArrayList<Enchantment>();
				for (String s : Main.InCPTConfig.getStringList("incompatibilities."+i+".enchants"))
					enchs.add(dictionary.findEnchant(s));
			incpts.put(Main.InCPTConfig.getStringList("incompatibilities."+i+".items"), enchs);
		}
	}
}
