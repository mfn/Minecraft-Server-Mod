import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Plugin to keep track of players.
 * 
 * Currently only supports Mysql as storage.
 * 
 * @author mfn
 * 
 */
public class TrackPlayerPlugin extends Plugin {
	private static final Logger log = Logger.getLogger("Minecraft");
	/**
	 * The interval in ms to write the currently known player locations to the
	 * database.
	 */
	private int updateInterval; // ms
	/**
	 * Store player id/location until updateInterval went by and flush to
	 * datasource
	 */
	private Hashtable<Integer, Location> playerLocations;
	private Listener listener;

	@Override
	public void initialize() {
		PropertiesFile serverProps = new PropertiesFile("server.properties");
		// set default to update database every 5 seconds
		updateInterval = serverProps.getInt("trackplayer-updateinterval", 5000);
		playerLocations = new Hashtable<Integer, Location>();
		new Thread(new Timer(updateInterval, new Mysql(), playerLocations))
				.start();
		etc.getLoader().addListener(PluginLoader.Hook.PLAYER_MOVE, listener,
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
		 * Act on player movement
		 */
		public void onPlayerMove(Player player, Location from, Location to) {
			if (player.getSqlId() == -1) {
				// only work with players already in the database
				return;
			}
			playerLocations.put(player.getSqlId(), to);
		}

	}

	private class Timer implements Runnable {
		private int updateInterval;
		private Mysql datasource;
		private Hashtable<Integer, Location> playerLocations;

		private Timer(int updateInterval, Mysql datasource,
				Hashtable<Integer, Location> playerLocations) {
			this.updateInterval = updateInterval;
			this.datasource = datasource;
			this.playerLocations = playerLocations;
		}

		@Override
		public void run() {
			while (true) {
				try {
					Thread.sleep(updateInterval);
					datasource.updatePlayerLocations(playerLocations);
				} catch (InterruptedException e) {
					e.printStackTrace();
					break;
				}

			}
		}
	}

	private class Mysql {
		private String driver, username, password, db;
		private final Object playerLocationLock = new Object();
		final private String createTable = "CREATE TABLE IF NOT EXISTS `users_location` (`user_id` int(11) NOT NULL, `x` float NOT NULL, `y` float NOT NULL, `z` float NOT NULL, `rotx` double NOT NULL, `roty` double NOT NULL, `last_update` timestamp NOT NULL default CURRENT_TIMESTAMP, PRIMARY KEY  (`user_id`));";

		public Mysql() {
			PropertiesFile properties = new PropertiesFile("mysql.properties");
			driver = properties.getString("driver", "com.mysql.jdbc.Driver");
			username = properties.getString("user", "root");
			password = properties.getString("pass", "root");
			db = properties.getString("db",
					"jdbc:mysql://localhost:3306/minecraft");

			try {
				Class.forName(driver);
			} catch (ClassNotFoundException ex) {
				log.log(Level.SEVERE, "Unable to find class " + driver, ex);
			}
			ensureTableExists();
		}

		private Connection getConnection() {
			try {
				return DriverManager.getConnection(db
						+ "?autoReconnect=true&user=" + username + "&password="
						+ password);
			} catch (SQLException ex) {
				log.log(Level.SEVERE, "Unable to retreive connection", ex);
			}
			return null;
		}

		private void ensureTableExists() {
			Connection conn = null;
			PreparedStatement ps = null;
			ResultSet rs = null;
			try {
				conn = getConnection();
				ps = conn.prepareStatement(createTable);
				ps.execute();
			} catch (SQLException ex) {
				log.log(Level.SEVERE,
						"Unable to create required users_location table", ex);
			} finally {
				try {
					if (ps != null) {
						ps.close();
					}
					if (rs != null) {
						rs.close();
					}
					if (conn != null) {
						conn.close();
					}
				} catch (SQLException ex) {
				}
			}
		}

		public void updatePlayerLocations(
				Hashtable<Integer, Location> playerLocations) {
			if (playerLocations.size() == 0) {
				return;
			}
			synchronized (playerLocations) {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					conn = getConnection();
					ps = conn
							.prepareStatement("INSERT INTO users_location (user_id, x,y,z,rotx,roty) VALUES(?, ?,?,?,?,?) ON DUPLICATE KEY UPDATE x = ?, y = ?, z = ?, rotx = ?, roty = ?, last_update = NOW()");
					Enumeration<Integer> e = playerLocations.keys();
					while (e.hasMoreElements()) {
						int id = e.nextElement();
						Location location = playerLocations.get(id);
						ps.setInt(1, id);

						// for the insert
						ps.setDouble(2, location.x);
						ps.setDouble(3, location.y);
						ps.setDouble(4, location.z);
						ps.setFloat(5, location.rotX);
						ps.setFloat(6, location.rotY);

						// for the update
						ps.setDouble(7, location.x);
						ps.setDouble(8, location.y);
						ps.setDouble(9, location.z);
						ps.setFloat(10, location.rotX);
						ps.setFloat(11, location.rotY);
						ps.addBatch();

					}
					ps.executeBatch();
					playerLocations.clear();
				} catch (SQLException ex) {
					log.log(Level.SEVERE, "Unable to update player location",
							ex);
				} finally {
					try {
						if (ps != null) {
							ps.close();
						}
						if (rs != null) {
							rs.close();
						}
						if (conn != null) {
							conn.close();
						}
					} catch (SQLException ex) {
					}
				}
			}
		}

		/**
		 * @deprecated Currently unused
		 */
		public void updatePlayerLocation(Player player, Location location) {
			if (-1 == player.getSqlId()) {
				// only work with players already in the database
				return;
			}
			synchronized (playerLocationLock) {
				Connection conn = null;
				PreparedStatement ps = null;
				ResultSet rs = null;
				try {
					conn = getConnection();
					ps = conn
							.prepareStatement("INSERT INTO users_location (user_id, x,y,z,rotx,roty) VALUES(?, ?,?,?,?,?) ON DUPLICATE KEY UPDATE x = ?, y = ?, z = ?, rotx = ?, roty = ?");
					ps.setInt(1, player.getSqlId());

					// for the insert
					ps.setDouble(2, location.x);
					ps.setDouble(3, location.y);
					ps.setDouble(4, location.z);
					ps.setFloat(5, location.rotX);
					ps.setFloat(6, location.rotY);

					// for the update
					ps.setDouble(7, location.x);
					ps.setDouble(8, location.y);
					ps.setDouble(9, location.z);
					ps.setFloat(10, location.rotX);
					ps.setFloat(11, location.rotY);

					ps.executeUpdate();
				} catch (SQLException ex) {
					log.log(Level.SEVERE, "Unable to update player location",
							ex);
				} finally {
					try {
						if (ps != null) {
							ps.close();
						}
						if (rs != null) {
							rs.close();
						}
						if (conn != null) {
							conn.close();
						}
					} catch (SQLException ex) {
					}
				}
			}
		}
	}
}
