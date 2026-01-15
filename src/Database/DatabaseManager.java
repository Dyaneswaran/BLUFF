package Database;

import java.io.File;
import java.sql.*;

public class DatabaseManager {
	private static final String DB_FILE = "resources/records.db";
	private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

	public static void main(String[] args) {
		createDatabase();
	}

	public static void createDatabase() {
		try {
			File dbFile = new File(DB_FILE);
			if (dbFile.exists()) {
				System.out.println("Database file already exists: " + DB_FILE);
				return;
			}

			try (Connection connection = DriverManager.getConnection(DB_URL)) {
				if (connection != null) {
					System.out.println("Database created successfully: " + DB_FILE);
					initializeDatabase(connection);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private static void initializeDatabase(Connection connection) {
		String createTableSQL = """
				CREATE TABLE IF NOT EXISTS PlayerRecords (
				    playerName TEXT PRIMARY KEY,
				    gamesPlayed INTEGER DEFAULT 0,
				    wins INTEGER DEFAULT 0,
				    losses INTEGER DEFAULT 0,
				    winPercentage REAL GENERATED ALWAYS AS ((wins * 100.0) / NULLIF(gamesPlayed, 0)) STORED
				);
				""";

		try (Statement statement = connection.createStatement()) {
			statement.execute(createTableSQL);
			System.out.println("PlayerRecords table initialized.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}