package org.eps.globalenchants;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.bukkit.Bukkit;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerItemHeldEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.eps.pvppack.Durability;
import org.vivi.eps.api.EPSConfiguration;
import org.vivi.eps.api.Reloadable;

public class EnchantProcessor implements Listener, Reloadable {

	private Random random = new Random();
	private EPSConfiguration flyConfig = EPSConfiguration.getConfiguration(CustomEnchants.FLY);
	private EPSConfiguration expConfig = EPSConfiguration.getConfiguration(CustomEnchants.EXPERIENCE);
	private EPSConfiguration sdConfig = EPSConfiguration.getConfiguration(CustomEnchants.SOUL_DESTRUCTION);
	private Map<Player, List<ItemStack>> itemsToKeep = new HashMap<Player, List<ItemStack>>();
	private boolean flyingEnabled = flyConfig.getBoolean("enabled");
	private boolean on = false;
	private List<Player> flying = new ArrayList<Player>();
	
    public EnchantProcessor(Plugin plugin)
    {
    	Bukkit.getPluginManager().registerEvents(this, plugin);
    }
    
    @EventHandler(priority = EventPriority.MONITOR, ignoreCancelled = true)
    public void onBlockBreak(BlockBreakEvent e)
    {
    	Player player = e.getPlayer();
		ItemStack mainhand = player.getInventory().getItemInMainHand();
		if (mainhand == null)
            return;
		if (!mainhand.hasItemMeta())
		    return;
		ItemMeta meta = mainhand.getItemMeta();
		if (meta.hasEnchant(CustomEnchants.EXPERIENCE))
		{
			int exp = e.getExpToDrop();
			if (exp > 0)
			{
				double multi = expConfig.getAutofilledDouble(meta.getEnchantLevel(CustomEnchants.EXPERIENCE), "exp-multi");
				e.setExpToDrop((int) (e.getExpToDrop()*multi));
			}
		}
    }
    
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerItemHeld(PlayerItemHeldEvent e)
	{
		if (e.isCancelled())
			return;
		
		Player p = e.getPlayer();
		ItemStack item = p.getInventory().getItem(e.getNewSlot());
		
		if (flying.contains(p))
		{
			p.setAllowFlight(false);
			p.setFlying(false);
			flying.remove(p);
		}
		
		if (item == null)
			return;
		
		ItemMeta meta = item.getItemMeta();
		if (flyingEnabled)
			if (meta.hasEnchant(CustomEnchants.FLY))
			{
				p.setAllowFlight(true);
				p.setFlying(true);
				flying.add(p);
			}
			
	}
	
	@EventHandler
	public void onPlayerDeath(PlayerDeathEvent e)
	{
		Player k = e.getEntity().getKiller();
		ItemStack imh = k == null ? null : k.getInventory().getItemInMainHand();
		int lvl = imh == null ? 0 : imh.getItemMeta().getEnchantLevel(CustomEnchants.SOUL_DESTRUCTION);
		List<ItemStack> soulbound = new ArrayList<ItemStack>();
		for (ItemStack i : e.getDrops())
			if (i.getItemMeta().hasEnchant(CustomEnchants.SOULBOUND))
				if (lvl > 0)
					if (getNext() < sdConfig.getAutofilledDouble(lvl, "chance"))
						continue;
					else
						soulbound.add(i);
				else
					soulbound.add(i);
		e.getDrops().removeAll(soulbound);
		itemsToKeep.put(e.getEntity(), soulbound);
	}
	
	@EventHandler
	public void onPlayerRespawn(PlayerRespawnEvent e)
	{
		for (ItemStack i : itemsToKeep.get(e.getPlayer()))
			e.getPlayer().getInventory().addItem(i);
		itemsToKeep.put(e.getPlayer(), new ArrayList<ItemStack>());
	}
	
	@EventHandler(priority = EventPriority.MONITOR)
	public void onPlayerMove(PlayerMoveEvent e)
	{
		if (e.getTo().getBlockX() == e.getFrom().getBlockX() && e.getTo().getBlockY() == e.getFrom().getBlockY())
			return;
		if	(on)
		{
			on = false;
			return;
		}
		if (e.isCancelled())
			return;
		
		ItemStack item = e.getPlayer().getInventory().getItemInMainHand();
		
		if (item == null)
			return;
		
		ItemMeta meta = item.getItemMeta();
		
		if (meta == null)
			return;
		if (meta.hasEnchant(CustomEnchants.REPAIR))
			new Durability(item).incrementDurability(1);
		on = !on;
	}
	
	public double getNext()
	{
		return random.nextDouble()*100;
	}
	
	@Override
	public void reload() 
	{
		flyConfig = EPSConfiguration.getConfiguration(CustomEnchants.FLY);
		expConfig = EPSConfiguration.getConfiguration(CustomEnchants.EXPERIENCE);
		sdConfig = EPSConfiguration.getConfiguration(CustomEnchants.SOUL_DESTRUCTION);
		itemsToKeep = new HashMap<Player, List<ItemStack>>();
	}
}