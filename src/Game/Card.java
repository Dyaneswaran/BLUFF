package Game;

public class Card {
	private String rank;
	private String suit;

	public Card(String rank, String suit) {
		this.rank = rank;
		this.suit = suit;
	}

	public String getRank() {
		return rank;
	}

	public String getSuit() {
		return suit;
	}

	// Override toString for easy display
	@Override
	public String toString() {
		return rank + " of " + suit;
	}
}
