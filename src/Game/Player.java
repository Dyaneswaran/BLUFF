package Game;

import java.util.ArrayList;
import java.util.List;

public class Player {
	private String name;
	private List<Card> hand;

	public Player(String name) {
		this.name = name;
		this.hand = new ArrayList<>();
	}

	public String getName() {
		return name;
	}

	public List<Card> getHand() {
		return hand;
	}

	public void addCard(Card card) {
		hand.add(card);
	}

	public void removeCard(Card card) {
		hand.remove(card);
	}

	public Card playCard(Card card) {
		if (!hand.contains(card)) {
			throw new IllegalArgumentException();
		}
		hand.remove(card);
		return card;
	}

	public void showHand() {
		System.out.println(name + "'s Hand:" + hand);
	}

	public Card getCardByIndex(int index) {
		if (index < 0 || index >= hand.size()) {
			throw new IndexOutOfBoundsException("Invalid Card Index..!");
		}
		return hand.get(index);
	}
}
