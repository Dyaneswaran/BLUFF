package Database;

import java.sql.*;
import java.util.*;

public class RecordsDAO {
	private static final String DB_FILE = "resources/records.db";
	private static final String DB_URL = "jdbc:sqlite:" + DB_FILE;

	// Fetch all player records from the database
	public Map<String, PlayerRecord> getAllPlayerRecords() {
		Map<String, PlayerRecord> playerRecords = new HashMap<>();
		String fetchSQL = "SELECT * FROM PlayerRecords";

		try (Connection connection = DriverManager.getConnection(DB_URL);
				Statement statement = connection.createStatement();
				ResultSet resultSet = statement.executeQuery(fetchSQL)) {

			while (resultSet.next()) {
				String playerName = resultSet.getString("playerName");
				int gamesPlayed = resultSet.getInt("gamesPlayed");
				int wins = resultSet.getInt("wins");
				int losses = resultSet.getInt("losses");
				float winPercentage = resultSet.getFloat("winPercentage");

				PlayerRecord record = new PlayerRecord(playerName, gamesPlayed, wins, losses, winPercentage);
				playerRecords.put(playerName, record);
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return playerRecords;
	}

	// Insert or update a player record
	public void saveOrUpdatePlayerRecord(PlayerRecord record) {
		String upsertSQL = """
				INSERT INTO PlayerRecords (playerName, gamesPlayed, wins, losses)
				VALUES (?, ?, ?, ?)
				ON CONFLICT(playerName) DO UPDATE SET
				    gamesPlayed = excluded.gamesPlayed,
				    wins = excluded.wins,
				    losses = excluded.losses;
				""";

		try (Connection connection = DriverManager.getConnection(DB_URL);
				PreparedStatement preparedStatement = connection.prepareStatement(upsertSQL)) {

			preparedStatement.setString(1, record.getPlayerName());
			preparedStatement.setInt(2, record.getGamesPlayed());
			preparedStatement.setInt(3, record.getWins());
			preparedStatement.setInt(4, record.getLosses());

			preparedStatement.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	// Fetch a single player record by name
	public PlayerRecord getPlayerRecord(String playerName) {
		String fetchSQL = "SELECT * FROM PlayerRecords WHERE playerName = ?";
		PlayerRecord record = null;

		try (Connection connection = DriverManager.getConnection(DB_URL);
				PreparedStatement preparedStatement = connection.prepareStatement(fetchSQL)) {

			preparedStatement.setString(1, playerName);

			try (ResultSet resultSet = preparedStatement.executeQuery()) {
				if (resultSet.next()) {
					int gamesPlayed = resultSet.getInt("gamesPlayed");
					int wins = resultSet.getInt("wins");
					int losses = resultSet.getInt("losses");
					float winPercentage = resultSet.getFloat("winPercentage");

					record = new PlayerRecord(playerName, gamesPlayed, wins, losses, winPercentage);
				}
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return record;
	}
}
