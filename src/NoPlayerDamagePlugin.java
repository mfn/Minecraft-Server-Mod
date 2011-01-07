import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Plugin to keep player alive.
 * 
 * @author mfn
 * 
 */
public class NoPlayerDamagePlugin extends Plugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	private Listener listener;

	@Override
	public void initialize() {
		etc.getLoader().addListener(PluginLoader.Hook.HEALTH_CHANGE, listener,
				this, PluginListener.Priority.MEDIUM);
	}

	public void enable() {
		log.log(Level.INFO, "Plugin " + getName() + " enabled");
		listener = new Listener();
	}

	public void disable() {
		log.log(Level.INFO, "Plugin " + getName() + " disabled");
	}

	private class Listener extends PluginListener {
		@Override
		/**
		 * Called when a players health changes.
		 * @param player
		 *              the player which health is changed.
		 * @param oldValue
		 *              old lives value
		 * @param newValue
		 *              new lives value
		 * @return
		 *      return true to stop the change.
		 */
		public boolean onHealthChange(Player player, int oldValue, int newValue) {
			return true;
		}

	}
}
