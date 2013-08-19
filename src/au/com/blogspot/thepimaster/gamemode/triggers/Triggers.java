package au.com.blogspot.thepimaster.gamemode.triggers;

import java.util.Map;

import org.bukkit.event.player.PlayerChangedWorldEvent;

import com.google.common.collect.Maps;

public abstract class Triggers {
	public static Map<String, Triggers> TRIGGERS = Maps.newHashMap();

	public abstract void go(PlayerChangedWorldEvent event);

	static {
		Triggers.TRIGGERS.put("CLEAR_INVENTORY", new ClearInventoryTrigger());
		Triggers.TRIGGERS.put("INVENTORY_LOAD", new InventoryIO.LoadInventory());
		Triggers.TRIGGERS.put("INVENTORY_SAVE", new InventoryIO.SaveInventory());
	}
}
