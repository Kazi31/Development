package xyz.olivermartin.multichat.database;

import java.io.File;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import xyz.olivermartin.multichat.spigotbridge.SQLNameManager;

public class DatabaseManager {

	private static DatabaseManager instance;

	public static DatabaseManager getInstance() {
		return instance;
	}

	static {
		instance = new DatabaseManager();
	}

	/* END STATIC */

	private File databasePathSQLite;
	private String databaseURLMySQL;
	private String databaseUsernameMySQL;
	private String databasePasswordMySQL;

	private DatabaseMode databaseMode = DatabaseMode.SQLite;

	private Map<String, GenericDatabase> databases;

	private DatabaseManager() {
		databases = new HashMap<String, GenericDatabase>();
	}

	////////////

	public static void main(String args[]) throws SQLException {
		DatabaseManager.getInstance().setPathSQLite(new File("C:\\multichat\\db\\"));
		DatabaseManager.getInstance().createDatabase("multichatspigot.db");

		Optional<GenericDatabase> odb = DatabaseManager.getInstance().getDatabase("multichatspigot.db");

		if (odb.isPresent()) {

			GenericDatabase db = odb.get();
			UUID uuid1 = UUID.randomUUID();

			try {
				db.connectToDatabase();
				db.execute("DROP TABLE IF EXISTS name_data;");
				db.update("CREATE TABLE name_data(id VARCHAR(128) PRIMARY KEY, f_name VARCHAR(255), u_name VARCHAR(255), u_nick VARCHAR(255), f_nick VARCHAR(255));");
				db.update("INSERT INTO name_data VALUES ('" + uuid1.toString() + "', 'Revilo410', 'revilo410', 'revilo', '&4Revilo');");
				ResultSet results = db.query("SELECT * FROM name_data;");
				while (results.next()) {
					System.out.println(results.getString("u_nick"));
				}
				db.disconnectFromDatabase();
			} catch (SQLException e) {
				e.printStackTrace();
			}

			SQLNameManager sqlnm = new SQLNameManager();
			System.out.println(sqlnm.getCurrentName(uuid1));
			System.out.println(sqlnm.getName(uuid1));

			//if (sqlnm.getUUIDFromUnformattedNickname("Test").isPresent()) System.out.println(":(");
			//if (sqlnm.getUUIDFromUnformattedNickname("revilo").isPresent()) System.out.println(":)");

			System.out.println(sqlnm.getUUIDFromName("Revilo410").get());

			System.out.println(sqlnm.getUUIDFromNickname("Revilo").get());

			System.out.println("!!!");

			UUID uuid2 = UUID.randomUUID();
			sqlnm.testRegisterFakePlayer(uuid2, "Johno");

			System.out.println(sqlnm.getUUIDFromName("Johno").get());

			sqlnm.testRegisterFakePlayer(uuid2, "Johno2");

			System.out.println(sqlnm.getUUIDFromName("Johno2").get());

			sqlnm.setNickname(uuid2, "JonnyBoy");

			System.out.println(sqlnm.getCurrentName(uuid2));

			sqlnm.removeNickname(uuid2);

			System.out.println(sqlnm.getCurrentName(uuid2));

		}

	}

	////////////

	public void setPathSQLite(File path) {
		this.databasePathSQLite = path;
	}

	public void setURLMySQL(String url) {
		this.databaseURLMySQL = url;
	}

	public void setUsernameMySQL(String user) {
		this.databaseUsernameMySQL = user;
	}

	public void setPasswordMySQL(String pass) {
		this.databasePasswordMySQL = pass;
	}

	public void setMode(DatabaseMode dbm) {
		databaseMode = dbm;
	}

	public GenericDatabase createDatabase(String name) throws SQLException {
		return createDatabase(name, name);
	}

	public boolean isReady() {
		switch (databaseMode) {
		case MySQL:
			if (databaseURLMySQL != null && databaseUsernameMySQL != null && databasePasswordMySQL != null) {
				return true;
			} else {
				return false;
			}
		case SQLite:
		default:
			return databasePathSQLite != null;
		}

	}

	/**
	 * Generic class to create a sqlite database
	 * @throws SQLException 
	 */
	public GenericDatabase createDatabase(String databaseName, String fileName) throws SQLException {

		if (!isReady()) throw new RuntimeException("MultiChat Database Manager Not Ready!");

		switch (databaseMode) {
		case MySQL:

			databases.put(databaseName.toLowerCase(), new MySQLDatabase(databaseURLMySQL, fileName, databaseUsernameMySQL, databasePasswordMySQL));

			return databases.get(databaseName.toLowerCase());

		case SQLite:
		default:

			if (!databasePathSQLite.exists()) {
				databasePathSQLite.mkdirs();
			}

			databases.put(databaseName.toLowerCase(), new SQLiteDatabase(databasePathSQLite, fileName));

			return databases.get(databaseName.toLowerCase());

		}

	}

	public Optional<GenericDatabase> getDatabase(String databaseName) {
		if (databases.containsKey(databaseName.toLowerCase())) {
			return Optional.of(databases.get(databaseName.toLowerCase()));
		} else {
			return Optional.empty();
		}
	}

	public void removeDatabase(String databaseName) throws SQLException {
		if (databases.containsKey(databaseName.toLowerCase())) {
			GenericDatabase gdb = databases.get(databaseName.toLowerCase());
			gdb.disconnectFromDatabase();
			databases.remove(databaseName.toLowerCase());
		}
	}

}