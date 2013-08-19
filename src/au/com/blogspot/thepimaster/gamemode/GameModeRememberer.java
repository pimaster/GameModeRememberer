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
				NullListener.NULL_LISTENER, EventPriority.HIGH, new MyEventExecutor(), this);
		Bukkit.getPluginManager().registerEvent(PlayerChangedWorldEvent.class, //
				NullListener.NULL_LISTENER, EventPriority.HIGH, new TriggerExecutor(this), this);
	}

	class MyEventExecutor extends TypedEventExecutor<PlayerChangedWorldEvent> {

		@Override
		public void executeTyped(Listener listener, PlayerChangedWorldEvent event) throws EventException {
			String fromName = event.getFrom().getName();
			String toName = event.getPlayer().getWorld().getName();

			logger.finer("Player change from " + fromName + " to " + toName);

			// Save old game mode if required
			String fromWorldMode = getConfig().getString("worlds." + fromName + ".mode");
			if (fromWorldMode != null) {
				ModeOptions fromMode = ModeOptions.valueOf(fromWorldMode);
				if (fromMode == ModeOptions.REMEMBER) {
					GameMode fromGameMode = GameMode.valueOf(getConfig().getString("worlds." + fromName + ".gamemode"));
					String rememberWorldString = getConfig().getString("players." + event.getPlayer().getName() + "." + fromName);
					if (event.getPlayer().getGameMode() != fromGameMode) {
						if (rememberWorldString == null || GameMode.valueOf(rememberWorldString) != event.getPlayer().getGameMode()) {
							logger.info("The player has a gamemode different to default. Saving");
							getConfig().set("players." + event.getPlayer().getName() + "." + fromName,
									event.getPlayer().getGameMode().toString());
							saveConfig();
						}
					}
					if (rememberWorldString != null && event.getPlayer().getGameMode() == fromGameMode) {
						logger.info("The player used to have a unique gamemode but now it is the default");
						getConfig().set("players." + event.getPlayer().getName(), null);
						saveConfig();
					}
				}
			}

			// Change to new gamemode
			String newWorldMode = getConfig().getString("worlds." + toName + ".mode");
			if (newWorldMode != null) {
				ModeOptions toMode = ModeOptions.valueOf(newWorldMode);
				GameMode toGameMode = GameMode.valueOf(getConfig().getString("worlds." + toName + ".gamemode"));

				if (toMode == ModeOptions.REMEMBER) {
					String rememberWorldString = getConfig().getString("players." + event.getPlayer().getName() + "." + toName);
					if (rememberWorldString != null) {
						toGameMode = GameMode.valueOf(rememberWorldString);
					}
				}
				if (event.getPlayer().getGameMode() != toGameMode) {
					logger.finer("Changing game mode to " + toGameMode);
					event.getPlayer().setGameMode(toGameMode);
				}
			} else {
				logger.fine("There is no setting for " + toName);
			}
		}
	}
}
