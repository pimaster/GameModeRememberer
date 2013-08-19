package au.com.blogspot.thepimaster.gamemode.triggers;

import org.bukkit.Material;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

public class ClearInventoryTrigger extends Triggers {

	public void go(PlayerChangedWorldEvent event) {
		event.getPlayer().getInventory().clear();
		event.getPlayer().getEquipment().setHelmet(new ItemStack(Material.AIR));
		event.getPlayer().getEquipment().setChestplate(new ItemStack(Material.AIR));
		event.getPlayer().getEquipment().setLeggings(new ItemStack(Material.AIR));
		event.getPlayer().getEquipment().setBoots(new ItemStack(Material.AIR));
		event.getPlayer().setExp(0);
		event.getPlayer().setLevel(0);
		
	}
}
