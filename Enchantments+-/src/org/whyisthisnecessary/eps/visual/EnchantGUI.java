package org.whyisthisnecessary.eps.visual;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.commons.lang.WordUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.enchantments.Enchantment;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryDragEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.whyisthisnecessary.eps.Main;
import org.whyisthisnecessary.eps.api.ConfigUtil;
import org.whyisthisnecessary.eps.legacy.LegacyUtil;
import org.whyisthisnecessary.eps.legacy.NameUtil;
import org.whyisthisnecessary.eps.util.LangUtil;
import org.whyisthisnecessary.eps.util.TokenUtil;

public class EnchantGUI implements Listener {

	public static Map<Player, Inventory> GUIs = new HashMap<Player, Inventory>();
	public static Map<Player, String> guiNames = new HashMap<Player, String>();
	
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
		@SuppressWarnings("unchecked")
		List<String> l = (List<String>) Main.Config.getList("guis."+guiname+".enchants");
        String[] list = new String[l.size()];
        list = l.toArray(list);
        for (String i : list)
        {
        	add(p, inv, i);
        }
        ItemStack i = new ItemStack(Material.GOLD_INGOT, 1);
        ItemMeta meta = i.getItemMeta();
        meta.setDisplayName(ChatColor.GREEN+"Your Tokens » "+ChatColor.YELLOW+Integer.toString(TokenUtil.getTokens(p.getName())));
        i.setItemMeta(meta);
        inv.setItem(4, i);
        inv.setItem(31, i);
	}
	
	@SuppressWarnings("deprecation")
	public static void createPanes(Inventory gui)
    {
		ItemStack slot = new ItemStack(Material.AIR, 1);
		if (LegacyUtil.isLegacy())
		{
			slot = new ItemStack(Material.matchMaterial("STAINED_GLASS_PANE"), 1, (short)15);
		}
		else
			slot = new ItemStack(Material.matchMaterial("BLACK_STAINED_GLASS_PANE"), 1);
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
    	String cost;
    	Enchantment enchant = NameUtil.getByName(NamespacedKey.minecraft(name));
    	if (enchant == null) {
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid enchantment name "+name+"!");
    		return;
    	}
    	String displayname = name.substring(0,1).toUpperCase() + name.substring(1);
    	displayname = displayname.replaceAll("_", " ");
    	displayname = WordUtils.capitalizeFully(displayname);
    	String upgradeicon = ConfigUtil.getString(enchant, "upgradeicon");
    	if (upgradeicon == null)
    	{
    		Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"No upgrade icon written in enchant config "+NameUtil.getName(enchant)+"! Setting to default BOOK.");
    	    upgradeicon = Material.BOOK.name();
    	}
    	Material material = Material.matchMaterial(upgradeicon);
    	String desc = ConfigUtil.getString(enchant, "upgradedesc");
    	Integer maxlevel = ConfigUtil.getInt(enchant, "maxlevel");
        if (!(p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant) >= maxlevel))
        {
        	String method = ConfigUtil.getString(enchant, "cost.type");
        	cost = Integer.toString(getCost(method, enchant, p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant), 1));
        
        }
        else
        {
            cost = "Maxed!";
        }
        ItemStack slot = new ItemStack(Material.BOOK, 1);;
        if (material != null)
        slot = new ItemStack(material, 1);
        else
        Bukkit.getConsoleSender().sendMessage(ChatColor.RED+"Invalid material type for enchantment "+name.toUpperCase()+". Setting to default BOOK.");
        ItemMeta meta = slot.getItemMeta();
        meta.setDisplayName(ChatColor.AQUA+displayname);
        meta.setLore(Arrays.asList(new String[] { "", 
        		ChatColor.GRAY+desc,
        		"",
        		ChatColor.GREEN+"Cost » "+ ChatColor.YELLOW +cost,
        		ChatColor.GREEN+"Max Level » "+ ChatColor.YELLOW +maxlevel,
        		ChatColor.GREEN+"Current Level » "+ ChatColor.YELLOW +p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant),
        		"",
        		ChatColor.GRAY+"» Left-Click to upgrade once",
        		ChatColor.GRAY+"» Right-Click to upgrade 5 times",
        		ChatColor.GRAY+"» Shift-Right-Click to upgrade 50 times",
        		}));
        meta.addEnchant(enchant, 1, true);
        meta.addItemFlags(new ItemFlag[] { ItemFlag.HIDE_ENCHANTS });
        slot.setItemMeta(meta);
        inv.setItem(inv.firstEmpty(), slot);
    }
	
	@EventHandler(priority = EventPriority.HIGH)
    public void onpickaxeinventoryDrag(final InventoryDragEvent e) {
		if (LegacyUtil.isLegacy())
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
        if (LegacyUtil.isLegacy())
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
        
        if (ench == null) return;
        
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
    	if (type.equalsIgnoreCase("manual"))
    	{
    		Integer val = ConfigUtil.getInt(enchant, "cost."+(enchlvl+1));
    		for (int i=1;i<multi;i++)
    		{
    			val = val + ConfigUtil.getInt(enchant, "cost."+(enchlvl+1+i));
    		}
    		return val;
    	}
    	else if (type.equalsIgnoreCase("linear"))
    	{
    		Integer startvalue = ConfigUtil.getInt(enchant, "cost.startvalue");
    		Integer value = ConfigUtil.getInt(enchant, "cost.value");
    		return (startvalue+(value*enchlvl-value)*multi);
    	}
    	else if (type.equalsIgnoreCase("exponential"))
    	{
    		Double multi1 = ConfigUtil.getDouble(enchant, "cost.multi");
    		Integer startvalue = ConfigUtil.getInt(enchant, "cost.startvalue");
    		return (int)(startvalue*Math.pow((Math.pow(multi1, enchlvl)), multi));
    	}
    	else
    	{
    		return 0;
    	}
    }
	
	public void UpgradeItem(Enchantment enchant, Player p, Integer multi)
    {
    	String method = ConfigUtil.getString(enchant, "cost.type");
    	Integer cost = (getCost(method, enchant, p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant), multi));
    	if (!(p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant)+multi-1 >= ConfigUtil.getInt(enchant, "maxlevel")))
    	{
    		if (TokenUtil.getTokens(p.getName()) >= cost)
            {
	        	p.sendMessage(LangUtil.getLangMessage("upgradedpickaxe"));
	        	Integer newvalue = TokenUtil.getTokens(p.getName()) - cost;
	        	TokenUtil.setTokens(p.getName(), newvalue);
	        	p.getInventory().getItemInMainHand().addUnsafeEnchantment(enchant, p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant)+multi);
	        	ItemMeta meta = EnchantMetaWriter.getWrittenMeta(p.getInventory().getItemInMainHand());
	        	p.getInventory().getItemInMainHand().setItemMeta(meta);
	        	loadInventory(p, guiNames.get(p));
            }
	        else
	        {
	        	p.sendMessage(LangUtil.getLangMessage("insufficienttokens"));
	        }
    	}
    	else if ((p.getInventory().getItemInMainHand().getEnchantmentLevel(enchant) >= ConfigUtil.getInt(enchant, "maxlevel")))
    	{
    		p.sendMessage(LangUtil.getLangMessage("exceedmaxlvl"));
    	}
    	else
    	{
    		p.sendMessage(LangUtil.getLangMessage("exceedmaxlvl"));
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
}
