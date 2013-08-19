package au.com.blogspot.thepimaster.gamemode;

import java.util.List;

import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.plugin.Plugin;

import au.com.blogspot.thepimaster.gamemode.triggers.Triggers;
import au.com.blogspot.thepimaster.tools.TypedEventExecutor;

public class TriggerExecutor extends TypedEventExecutor<PlayerChangedWorldEvent> {

	private final Plugin plugin;

	public TriggerExecutor(Plugin plugin) {
		this.plugin = plugin;
	}

	@Override
	@SuppressWarnings("unchecked")
	public void executeTyped(Listener listener, PlayerChangedWorldEvent event) throws EventException {
		String fromName = event.getFrom().getName();
		String toName = event.getPlayer().getWorld().getName();
		List<String> fromTriggers = (List<String>) plugin.getConfig().getList("worlds." + fromName + ".exit_trigger");
		if (fromTriggers != null) {
			for (String s : fromTriggers) {
				Triggers.TRIGGERS.get(s).go(event);
			}
		}
		List<String> toTriggers = (List<String>) plugin.getConfig().getList("worlds." + toName + ".enter_trigger");
		if (toTriggers != null) {
			for (String s : toTriggers) {
				Triggers.TRIGGERS.get(s).go(event);
			}
		}
	}

}
