package au.com.blogspot.thepimaster.gamemode.triggers;

import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.plaf.basic.BasicBorders.MarginBorder;

import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wolf;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.inventory.ItemStack;

import au.com.blogspot.thepimaster.gamemode.GameModeRememberer;

public class InventoryIO {

	private static Logger logger = Logger.getLogger(InventoryIO.class.getName());

	public static class SaveInventory extends Triggers {
		@Override
		public void go(PlayerChangedWorldEvent event) {
			Player player = event.getPlayer();
			File playerFile = getPlayerFile(event.getFrom().getName(), player.getName());
			YamlConfiguration config = YamlConfiguration.loadConfiguration(playerFile);

			ItemStack[] inv = player.getInventory().getContents();
			for (int i = 0; i < inv.length; i++) {
				config.set("inv." + i, inv[i]);
			}
			config.set("xp", player.getExp());
			config.set("level", player.getLevel());
			config.set("helment", player.getInventory().getHelmet());
			config.set("chest", player.getInventory().getChestplate());
			config.set("leg", player.getInventory().getLeggings());
			config.set("boot", player.getInventory().getBoots());

			logger.finer("Saved inventory to " + playerFile.getAbsolutePath());

			try {
				config.save(playerFile);
			} catch (IOException e) {
				logger.severe("Couldn't save players inventory " + e.getMessage());
			}
		}
	}

	public static class LoadInventory extends Triggers {

		@Override
		public void go(PlayerChangedWorldEvent event) {
			Player player = event.getPlayer();
			File playerFile = getPlayerFile(player.getWorld().getName(), player.getName());
			YamlConfiguration config = null;
			if (playerFile.exists()) {
				config = YamlConfiguration.loadConfiguration(playerFile);
			} else {
				config = new YamlConfiguration();
			}

			ItemStack[] inv = player.getInventory().getContents();
			for (int i = 0; i < inv.length; i++) {
				player.getInventory().setItem(i, config.getItemStack("inv." + i, new ItemStack(Material.AIR)));
				// inv[i] = config.getItemStack("inv." + i, new
				// ItemStack(Material.AIR));
			}
			player.setExp((float) config.getDouble("xp"));
			player.setLevel(config.getInt("level"));
			player.getInventory().setHelmet(config.getItemStack("helmet"));
			player.getInventory().setChestplate(config.getItemStack("chest"));
			player.getInventory().setLeggings(config.getItemStack("leg"));
			player.getInventory().setBoots(config.getItemStack("boot"));

			logger.finer("Loaded inventory from " + playerFile.getAbsolutePath());
		}
	}

	private static File getPlayerFile(String worldName, String playerName) {
		File worldFolder = new File(GameModeRememberer.plugin.getDataFolder(), worldName);
		if (worldFolder.exists() == false) {
			if (worldFolder.mkdirs() == false) {
				logger.severe("Unable to make directory " + worldFolder.getAbsolutePath());
			}
		}
		File playerFile = new File(worldFolder, playerName);
		return playerFile;
	}
}
