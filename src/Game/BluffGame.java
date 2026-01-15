package Game;

import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class BluffGame {
	private List<Player> players;
	private Deck deck;
	private List<Card> discardPile;
	private List<Card> lastTurnCards;
	private int currentPlayerIndex;
	private String currentDeclaredRank;
	private Set<Player> playersWhoPassed;
	private Player lastPlayerToPlay;
	private boolean isFirstTurnOfRound;
	private boolean isDeckShuffled = false;
	private List<GameUpdateListener> listeners = new ArrayList<>();

	public BluffGame(List<String> playerNames) {
		players = new ArrayList<>();
		for (String name : playerNames) {
			players.add(new Player(name));
		}

		deck = new Deck();
		discardPile = new ArrayList<>();
		lastTurnCards = new ArrayList<>();
		currentPlayerIndex = 0;
		currentDeclaredRank = "";
		playersWhoPassed = new HashSet<>();
		isFirstTurnOfRound = true;

		deck.shuffleInBackground(() -> {
			isDeckShuffled = true;
			dealCards();
		});
	}

	public void addGameUpdateListener(GameUpdateListener listener) {
		listeners.add(listener);
	}

	private void notifyListeners() {
		for (GameUpdateListener listener : listeners) {
			listener.onUpdate();
		}
	}

	public List<Player> getPlayers() {
		return players;
	}

	public List<Card> getDiscardPile() {
		return new ArrayList<>(discardPile);
	}

	public Player getLastPlayerToPlay() {
		return lastPlayerToPlay;
	}

	public List<Card> getLastTurnCards() {
		return lastTurnCards;
	}

	public Player getCurrentPlayer() {
		return players.get(currentPlayerIndex);
	}

	public Set<Player> getPlayersWhoPassed() {
		return playersWhoPassed;
	}

	public String getCurrentDeclaredRank() {
		return currentDeclaredRank;
	}

	public boolean hasPlayerPassed(Player player) {
		return playersWhoPassed.contains(player);
	}

	private void moveToNextPlayer() {
		currentPlayerIndex = (currentPlayerIndex + 1) % players.size();
	}

	public boolean isNewRound() {
		return isFirstTurnOfRound;
	}

	private boolean isBluff() {
		for (Card card : lastTurnCards) {
			if (!card.getRank().equalsIgnoreCase(currentDeclaredRank)) {
				return true;
			}
		}
		return false;
	}

	private void dealCards() {
		if (!isDeckShuffled) {
			System.out.println("Cannot deal cards until deck is shuffled!");
			return;
		}

		ExecutorService executor = Executors.newFixedThreadPool(players.size());

		while (deck.remainingCards() > 0) {
			for (Player player : players) {
				executor.submit(() -> {
					synchronized (deck) {
						if (deck.remainingCards() > 0) {
							player.addCard(deck.drawCard());
						}
					}
				});
			}
		}
		executor.shutdown();
		try {
			if (!executor.awaitTermination(60, java.util.concurrent.TimeUnit.SECONDS)) {
				System.err.println("Timeout: Some threads did not finish in time!");
			}
		} catch (InterruptedException e) {
			System.err.println("Thread was interrupted while waiting for termination.");
			Thread.currentThread().interrupt();
		}

		notifyListeners();
	}

	private void resetRound() {
		discardPile.clear();
		lastTurnCards.clear();
		currentDeclaredRank = "";
		isFirstTurnOfRound = true;
		playersWhoPassed.clear();
	}

	public void playCards(Player player, List<Card> cardsToPlay, String declaredRank) {
		if (!getCurrentPlayer().equals(player)) {
			throw new IllegalArgumentException("It's not your turn!");
		}
		if (playersWhoPassed.contains(player)) {
			throw new IllegalArgumentException("You have already passed this round and cannot play!");
		}

		discardPile.addAll(cardsToPlay);
		lastTurnCards.clear();
		lastTurnCards.addAll(cardsToPlay);
		player.getHand().removeAll(cardsToPlay);

		if (currentDeclaredRank.isEmpty()) {
			currentDeclaredRank = declaredRank;
			isFirstTurnOfRound = false;
		}

		// Update turn
		lastPlayerToPlay = player;
		moveToNextPlayer();

		notifyListeners();
	}

	public void passTurn(Player player) {
		if (!getCurrentPlayer().equals(player)) {
			throw new IllegalArgumentException("It's not your turn!");
		}
		if (isFirstTurnOfRound) {
			throw new IllegalArgumentException("You cannot pass on the first turn of a new round!");
		}

		if (!playersWhoPassed.contains(player)) {
			playersWhoPassed.add(player);
		}

		lastPlayerToPlay = player;
		if (playersWhoPassed.size() == players.size()) {
			resetRound();
		} else {
			moveToNextPlayer();
		}

		notifyListeners();
	}

	public String callBluff(Player challenger) {
		if (lastPlayerToPlay == null) {
			throw new IllegalStateException("No cards have been played yet!");
		}
		if (challenger.equals(lastPlayerToPlay)) {
			throw new IllegalArgumentException("You cannot call a bluff on yourself!");
		}

		boolean bluffDetected = isBluff();

		if (bluffDetected) {
			lastPlayerToPlay.getHand().addAll(discardPile);
			resetRound();
			return challenger.getName() + " successfully called a bluff! " + lastPlayerToPlay.getName()
					+ " picks up the discard pile.";
		} else {
			challenger.getHand().addAll(discardPile);
			resetRound();
			currentPlayerIndex = players.indexOf(lastPlayerToPlay);
			return challenger.getName() + " called a bluff but was wrong! They pick up the discard pile.";
		}
	}
}