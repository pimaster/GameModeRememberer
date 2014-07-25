package au.com.blogspot.thepimaster.gamemode;

import java.util.logging.Logger;

import org.bukkit.GameMode;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;

import au.com.blogspot.thepimaster.tools.ModeOptions;
import au.com.blogspot.thepimaster.tools.TypedEventExecutor;

public class GameModeChangeExecutor extends TypedEventExecutor<PlayerChangedWorldEvent> {

	private final Logger logger = Logger.getLogger(GameModeRememberer.class.getName());
	private Plugin plugin;

	public GameModeChangeExecutor(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	public void executeTyped(Listener listener, PlayerChangedWorldEvent event) throws EventException {

		boolean saveRequired = false;
		String fromName = event.getFrom().getName();
		String toName = event.getPlayer().getWorld().getName();

		logger.finer("Player change from " + fromName + " to " + toName);

		try
		{
		
		// Save old game mode if required
		String fromWorldMode = plugin.getConfig().getString("worlds." + fromName + ".mode");
		if (fromWorldMode != null) {
			ModeOptions fromMode = ModeOptions.valueOf(fromWorldMode);
			if (fromMode == ModeOptions.REMEMBER) {
				GameMode fromGameMode = GameMode.valueOf(plugin.getConfig().getString("worlds." + fromName + ".gamemode"));
				String rememberWorldString = plugin.getConfig().getString(
						"players." + event.getPlayer().getUniqueId().toString() + "." + fromName);
				{ // transition code
					String rememberWorldStringOld = plugin.getConfig().getString("players." + event.getPlayer().getName() + "." + fromName);
					if (rememberWorldStringOld != null) {
						rememberWorldString = rememberWorldStringOld;
						plugin.getConfig().set("players." + event.getPlayer().getUniqueId().toString() + "." + fromName,
								rememberWorldString);
						plugin.getConfig().set("players." + event.getPlayer().getName() + "." + fromName, null);
						saveRequired = true;
					}
				}
				if (event.getPlayer().getGameMode() != fromGameMode) {
					if (rememberWorldString == null || GameMode.valueOf(rememberWorldString) != event.getPlayer().getGameMode()) {
						logger.info("The player has a gamemode different to default. Saving");
						plugin.getConfig().set("players." + event.getPlayer().getUniqueId().toString() + "." + fromName,
								event.getPlayer().getGameMode().toString());
						saveRequired = true;
					}
				}
				if (rememberWorldString != null && event.getPlayer().getGameMode() == fromGameMode) {
					logger.info("The player used to have a unique gamemode but now it is the default");
					plugin.getConfig().set("players." + event.getPlayer().getName(), null);
					saveRequired = true;
				}
			}
		}

		// Change to new gamemode
		String newWorldMode = plugin.getConfig().getString("worlds." + toName + ".mode");
		if (newWorldMode != null) {
			ModeOptions toMode = ModeOptions.valueOf(newWorldMode);
			GameMode toGameMode = GameMode.valueOf(plugin.getConfig().getString("worlds." + toName + ".gamemode"));

			if (toMode == ModeOptions.REMEMBER) {
				String rememberWorldString = plugin.getConfig().getString(
						"players." + event.getPlayer().getUniqueId().toString() + "." + toName);
				{// transition code
					String rememberWorldStringOld = plugin.getConfig().getString("players." + event.getPlayer().getName() + "." + toName);
					if (rememberWorldStringOld != null) {
						rememberWorldString = rememberWorldStringOld;
						plugin.getConfig().set("players." + event.getPlayer().getUniqueId().toString() + "." + toName, rememberWorldString);
						plugin.getConfig().set("players." + event.getPlayer().getName() + "." + toName, null);
						saveRequired = true;
					}
				}
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
		finally
		{
			if(saveRequired)
			{
				plugin.saveConfig();
			}
		}
	}
}