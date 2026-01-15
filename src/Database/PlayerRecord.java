package Database;

public class PlayerRecord {
	private final String playerName;
	private int gamesPlayed;
	private int wins;
	private int losses;

	public PlayerRecord(String playerName, int gamesPlayed, int wins, int losses, float winPercentage) {
		this.playerName = playerName;
		this.gamesPlayed = gamesPlayed;
		this.wins = wins;
		this.losses = losses;
	}

	public String getPlayerName() {
		return playerName;
	}

	public int getGamesPlayed() {
		return gamesPlayed;
	}

	public int getWins() {
		return wins;
	}

	public int getLosses() {
		return losses;
	}

	public float getWinPercentage() {
		return gamesPlayed == 0 ? 0 : (wins * 100.0f / gamesPlayed);
	}

	public void incrementGamesPlayed() {
		gamesPlayed++;
	}

	public void incrementWins() {
		wins++;
	}

	public void incrementLosses() {
		losses++;
	}
}