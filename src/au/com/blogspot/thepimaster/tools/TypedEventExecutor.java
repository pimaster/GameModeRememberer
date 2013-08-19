package au.com.blogspot.thepimaster.tools;

import java.util.logging.Logger;

import org.bukkit.event.Event;
import org.bukkit.event.EventException;
import org.bukkit.event.Listener;
import org.bukkit.plugin.EventExecutor;

public abstract class TypedEventExecutor<T extends Event> implements EventExecutor {

	private static final Logger logger = Logger.getLogger(TypedEventExecutor.class.getName());

	@SuppressWarnings("unchecked")
	public final void execute(Listener listener, Event event) throws EventException {
		try {
			executeTyped(listener, (T) event);
		} catch (ClassCastException e) {
			logger.severe("I am meant to receive a given type but it was a lie " + e.getMessage());
		}
	}

	public abstract void executeTyped(Listener listener, T event) throws EventException;
}
