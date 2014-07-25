package au.com.blogspot.thepimaster.gamemode;

import java.util.logging.Logger;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.event.EventException;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.java.JavaPlugin;

import au.com.blogspot.thepimaster.tools.ModeOptions;
import au.com.blogspot.thepimaster.tools.NullListener;
import au.com.blogspot.thepimaster.tools.TypedEventExecutor;

public class GameModeRememberer extends JavaPlugin {

	private final Logger logger = Logger.getLogger(GameModeRememberer.class.getName());
	public static JavaPlugin plugin;	

	@Override
	public void onEnable() {
		plugin = this;
		boolean firstRun = getConfig().getBoolean("firstRunComplete", false);
		if (!firstRun) {
			logger.finer("Setting up first run configuration");
			saveDefaultConfig();
			reloadConfig();
			getConfig().set("firstRunComplete", true);
			saveConfig();
		}
		Bukkit.getPluginManager().registerEvent(PlayerChangedWorldEvent.class, //
				NullListener.NULL_LISTENER, EventPriority.HIGH, new GameModeChangeExecutor(this), this);
		Bukkit.getPluginManager().registerEvent(PlayerChangedWorldEvent.class, //
				NullListener.NULL_LISTENER, EventPriority.HIGH, new TriggerExecutor(this), this);
	}

	
}
