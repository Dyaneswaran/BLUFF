package UI;

import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

import Database.PlayerRecord;
import Database.RecordsDAO;
import Game.BluffGame;
import Game.Card;
import Game.Player;

public class BluffGameUI {
	private BluffGame game;
	private Player currentPlayer;
	private JFrame frame;
	private JPanel handPanel, discardPilePanel;
	private JTextField rankField;
	private JLabel currentPlayerLabel;
	private JButton playButton, passButton, bluffButton, quitButton;
	private JPanel discardPanelWrapper;
	private JLabel rankIconLabel;
	private List<Card> selectedCards;
	private Map<Card, JLabel> cardToLabelMap = new HashMap<>();
	private final RecordsDAO recordsDAO;

	public BluffGameUI(List<String> playerNames) {
		game = new BluffGame(playerNames);
		selectedCards = new ArrayList<>();
		recordsDAO = new RecordsDAO();

		setupFrame();
		setupPanels();
		setupButtons();
		setupCurrentPlayerPanel();

		updateUI();
		frame.setVisible(true);
	}

	private void setupFrame() {
		frame = new JFrame("Bluff Card Game");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setSize(800, 550);
		frame.setLayout(new BorderLayout());
		frame.getContentPane().setBackground(new Color(30, 120, 80));
	}

	private void setupPanels() {
		discardPanelWrapper = new JPanel();
		discardPanelWrapper.setLayout(new BorderLayout());
		discardPilePanel = new JPanel();
		discardPilePanel.setBorder(BorderFactory.createTitledBorder("Discard Pile : "));
		discardPilePanel.setBackground(new Color(50, 200, 120));
		discardPilePanel.setPreferredSize(new Dimension(200, 200));

		rankIconLabel = new JLabel();
		rankIconLabel.setHorizontalAlignment(SwingConstants.CENTER);
		rankIconLabel.setVerticalAlignment(SwingConstants.CENTER);
		rankIconLabel.setBackground(new Color(50, 200, 120));
		rankIconLabel.setPreferredSize(new Dimension(200, 200));
		rankIconLabel.setBorder(BorderFactory.createTitledBorder("Current Rank to Play : "));

		discardPanelWrapper.add(discardPilePanel, BorderLayout.CENTER);
		discardPanelWrapper.add(rankIconLabel, BorderLayout.SOUTH);

		frame.add(discardPanelWrapper, BorderLayout.EAST);

		handPanel = new JPanel();
		handPanel.setBorder(BorderFactory.createTitledBorder("Your Hand : "));
		handPanel.setBackground(new Color(50, 200, 120));
		frame.add(handPanel, BorderLayout.CENTER);
	}

	private void setupCurrentPlayerPanel() {
		JPanel currentPlayerPanel = new JPanel();
		currentPlayerPanel.setLayout(new BorderLayout());
		currentPlayerPanel.setBackground(Color.LIGHT_GRAY);

		currentPlayerLabel = new JLabel("Current Player: ");
		currentPlayerLabel.setHorizontalAlignment(SwingConstants.LEFT);
		currentPlayerLabel.setFont(new Font("Arial", Font.BOLD, 18));
		currentPlayerLabel.setForeground(Color.BLACK);
		currentPlayerLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
		currentPlayerPanel.add(currentPlayerLabel, BorderLayout.CENTER);

		quitButton = new JButton("Quit Game");
		styleButton(quitButton, new Color(220, 20, 60), Color.WHITE);
		quitButton.addActionListener(e -> handleQuitAction());
		currentPlayerPanel.add(quitButton, BorderLayout.EAST);

		frame.add(currentPlayerPanel, BorderLayout.NORTH);
	}

	private void setupButtons() {
		JPanel controlPanel = new JPanel();
		controlPanel.setLayout(new FlowLayout(FlowLayout.CENTER, 20, 10));
		controlPanel.setBackground(Color.LIGHT_GRAY);

		rankField = new JTextField(4);
		rankField.setFont(new Font("Arial", Font.BOLD, 20));
		rankField.setPreferredSize(new Dimension(50, 40));
		rankField.setBackground(new Color(255, 255, 200));
		rankField.setForeground(Color.BLACK);
		rankField.setHorizontalAlignment(JTextField.CENTER);

		playButton = new JButton("PLAY");
		styleButton(playButton, new Color(34, 139, 34), Color.WHITE); // Green button with white text

		passButton = new JButton("PASS");
		styleButton(passButton, new Color(70, 130, 180), Color.WHITE); // Blue button with white text

		bluffButton = new JButton("CALL BLUFF");
		styleButton(bluffButton, new Color(220, 20, 60), Color.WHITE); // Red button with white text

		JLabel rankLabel = new JLabel();
		rankLabel.setText("Declare Rank : ");
		rankLabel.setFont(new Font("Arial", Font.BOLD, 20));

		controlPanel.add(rankLabel);
		controlPanel.add(rankField);
		controlPanel.add(playButton);
		controlPanel.add(passButton);
		controlPanel.add(bluffButton);
		frame.add(controlPanel, BorderLayout.SOUTH);

		playButton.addActionListener(e -> handlePlayAction());
		passButton.addActionListener(e -> handlePassAction());
		bluffButton.addActionListener(e -> handleBluffAction());
	}

	private void updateUI() {
		SwingUtilities.invokeLater(() -> {

			if (game.getCurrentPlayer().getHand().isEmpty()) {
				showGameOverMessage(game.getCurrentPlayer());
				return;
			}

			currentPlayer = game.getCurrentPlayer();
			if (game.getPlayersWhoPassed().contains(game.getLastPlayerToPlay())) {
				currentPlayerLabel.setText(game.getLastPlayerToPlay().getName()
						+ " has passed his turn. It is your turn..." + currentPlayer.getName());
			} else {
				if (game.getCurrentDeclaredRank().isEmpty()) {
					currentPlayerLabel.setText("New Round. It is your turn..." + currentPlayer.getName());
				} else {
					currentPlayerLabel.setText(game.getLastPlayerToPlay().getName() + " has played "
							+ game.getLastTurnCards().size() + " cards as " + game.getCurrentDeclaredRank()
							+ ". It is your turn..." + currentPlayer.getName());
				}
			}

			boolean isUserTurn = currentPlayer.equals(game.getCurrentPlayer());
			playButton.setEnabled(isUserTurn && !game.hasPlayerPassed(currentPlayer));
			passButton.setEnabled(isUserTurn && !game.isNewRound());
			bluffButton.setEnabled(!currentPlayer.equals(game.getLastPlayerToPlay()) && !game.isNewRound()
					&& !game.getPlayersWhoPassed().contains(game.getLastPlayerToPlay()));

			updateRankPanel();
			updateHandPanel();
			updateDiscardPile();
		});
	}

	private void styleButton(JButton button, Color background, Color foreground) {
		button.setUI(new RoundedButtonUI(background, foreground));
		button.setFont(new Font("Arial", Font.BOLD, 16));
		button.setPreferredSize(new Dimension(120, 40));
		button.setFocusPainted(false);
		button.setContentAreaFilled(false);
		button.setBorderPainted(false);
		button.setOpaque(false);
	}

	private void toggleCardSelection(Card card, JLabel cardLabel) {
		if (selectedCards.contains(card)) {
			selectedCards.remove(card);
			cardLabel.setBorder(BorderFactory.createEmptyBorder());
		} else {
			selectedCards.add(card);
			cardLabel.setBorder(BorderFactory.createLineBorder(Color.BLUE, 4));
		}
	}

	private void handlePlayAction() {
		if (selectedCards.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "You must select at least one card to play!");
			return;
		}

		String declaredRank = rankField.getText().trim().toUpperCase();
		List<String> validRanks = Arrays.asList("A", "K", "Q", "J");
		if (declaredRank.isEmpty()) {
			JOptionPane.showMessageDialog(frame, "You must declare a rank to start the round!");
			return;
		} else if (!validRanks.contains(declaredRank)) {
			JOptionPane.showMessageDialog(frame, "Invalid rank! You must declare one of: A, K, Q, J");
			return;
		}

		try {
			List<Card> cardsToPlay = new ArrayList<>(selectedCards);

			Iterator<Card> cardIterator = cardsToPlay.iterator();

			animateCardToDiscardPile(cardIterator, () -> {
				game.playCards(currentPlayer, cardsToPlay, declaredRank);
				updateUI();
			});
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(frame, ex.getMessage());
		}
	}

	private void handlePassAction() {
		try {
			game.passTurn(currentPlayer);
			updateUI();
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(frame, ex.getMessage());
		}
	}

	private void handleBluffAction() {
		try {
			String result = game.callBluff(currentPlayer);
			JOptionPane.showMessageDialog(frame, result);

			updateUI();
		} catch (IllegalArgumentException ex) {
			JOptionPane.showMessageDialog(frame, ex.getMessage());
		}
	}

	private void handleQuitAction() {
		int confirm = JOptionPane.showConfirmDialog(frame,
				"Are you sure you want to quit the game? The opponent will be declared Winner if you do..!",
				"Quit Game", JOptionPane.YES_NO_OPTION);

		if (confirm == JOptionPane.YES_OPTION) {
			showGameOverMessage(game.getLastPlayerToPlay());
		}
	}

	private void animateCardToDiscardPile(Iterator<Card> cardIterator, Runnable onComplete) {
		if (!cardIterator.hasNext()) {
			onComplete.run();
			return;
		}

		Card card = cardIterator.next();
		JLabel cardLabel = cardToLabelMap.get(card);

		Point start = cardLabel.getLocation();
		SwingUtilities.convertPointToScreen(start, handPanel);

		Point end = discardPilePanel.getLocation();
		SwingUtilities.convertPointToScreen(end, discardPilePanel);

		int steps = 20;
		int dx = (end.x - start.x) / steps;
		int dy = (end.y - start.y) / steps;

		Timer timer = new Timer(10, new ActionListener() {
			int step = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				if (step < steps) {
					Point current = cardLabel.getLocation();
					SwingUtilities.convertPointToScreen(current, handPanel);

					current.translate(dx, dy);
					SwingUtilities.convertPointFromScreen(current, handPanel);

					cardLabel.setLocation(current);
					step++;
				} else {
					((Timer) e.getSource()).stop();

					handPanel.remove(cardLabel);
					handPanel.revalidate();
					handPanel.repaint();

					animateCardToDiscardPile(cardIterator, onComplete);
				}
			}
		});

		timer.start();
	}

	private void updateRankIcon(String rank) {
		if (rank.isEmpty()) {
			rankIconLabel.setIcon(null);
			return;
		}

		String rankImagePath = "/cards/" + rank + "_of_spades.png";
		java.net.URL resourceUrl = getClass().getResource(rankImagePath);
		ImageIcon rankIcon = new ImageIcon(resourceUrl);
		Image scaledImage = rankIcon.getImage().getScaledInstance(75, 125, Image.SCALE_SMOOTH);
		rankIconLabel.setIcon(new ImageIcon(scaledImage));
	}

	private void updateRankPanel() {
		rankField.setEditable(game.isNewRound());
		if (game.isNewRound()) {
			rankField.setText("");
			rankIconLabel.setIcon(null);
		} else {
			String currentRank = game.getCurrentDeclaredRank();
			rankField.setText(currentRank);
			updateRankIcon(currentRank);
		}
	}

	private void updateHandPanel() {
		handPanel.removeAll();
		cardToLabelMap.clear();
		selectedCards.clear();

		for (Card card : currentPlayer.getHand()) {
			String cardImagePath = "/cards/" + getCardImageFileName(card);
			ImageIcon cardIcon = new ImageIcon(getClass().getResource(cardImagePath));
			Image scaledImage = cardIcon.getImage().getScaledInstance(80, 120, Image.SCALE_SMOOTH);
			JLabel cardLabel = new JLabel(new ImageIcon(scaledImage));

			cardLabel.setBorder(BorderFactory.createEmptyBorder());
			cardLabel.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					toggleCardSelection(card, cardLabel);
				}
			});

			handPanel.add(cardLabel);
			cardToLabelMap.put(card, cardLabel);
		}

		handPanel.revalidate();
		handPanel.repaint();
	}

	private void updateDiscardPile() {
		discardPilePanel.removeAll();

		if (!game.getDiscardPile().isEmpty()) {
			String cardBackImagePath = "/cards/card_back.png";
			java.net.URL resourceUrl = getClass().getResource(cardBackImagePath);
			ImageIcon cardBackIcon = new ImageIcon(resourceUrl);
			Image scaledImage = cardBackIcon.getImage().getScaledInstance(100, 125, Image.SCALE_SMOOTH);
			discardPilePanel.add(new JLabel(new ImageIcon(scaledImage)));
		}

		discardPilePanel.revalidate();
		discardPilePanel.repaint();
	}

	private String getCardImageFileName(Card card) {
		return card.getRank().toLowerCase() + "_of_" + card.getSuit().toLowerCase() + ".png";
	}

	private void updatePlayerRecords(Player winner) {
		for (Player player : game.getPlayers()) {
			PlayerRecord record = recordsDAO.getPlayerRecord(player.getName());

			if (record == null) {
				record = new PlayerRecord(player.getName(), 0, 0, 0, 0.0f);
			}

			record.incrementGamesPlayed();
			if (player.getName().equals(winner.getName())) {
				record.incrementWins();
			} else {
				record.incrementLosses();
			}

			recordsDAO.saveOrUpdatePlayerRecord(record);
		}
	}

	private void showGameOverMessage(Player winner) {
		updatePlayerRecords(winner);
		SwingUtilities.invokeLater(() -> {
			showLeaderboard(winner);
		});
	}

	private void showLeaderboard(Player winner) {
		Map<String, PlayerRecord> records = recordsDAO.getAllPlayerRecords();

		String[] columnNames = { "Player Name", "Games Played", "Wins", "Losses", "Win %" };
		Object[][] data = new Object[records.size()][columnNames.length];

		int i = 0;
		for (PlayerRecord record : records.values()) {
			data[i][0] = record.getPlayerName();
			data[i][1] = record.getGamesPlayed();
			data[i][2] = record.getWins();
			data[i][3] = record.getLosses();
			data[i][4] = String.format("%.2f", record.getWinPercentage());
			i++;
		}

		JTable leaderboardTable = new JTable(data, columnNames);
		leaderboardTable.setEnabled(false);
		leaderboardTable.setFillsViewportHeight(true);

		JScrollPane scrollPane = new JScrollPane(leaderboardTable);

		JDialog leaderboardDialog = new JDialog(frame, "Leaderboard", true);
		leaderboardDialog.setSize(600, 400);
		leaderboardDialog.setLocationRelativeTo(frame);
		leaderboardDialog.setLayout(new BorderLayout());

		JLabel titleLabel = new JLabel(winner.getName() + " has emptied all their cards and has WON the game..!",
				SwingConstants.CENTER);
		titleLabel.setFont(new Font("Arial", Font.BOLD, 20));
		leaderboardDialog.add(titleLabel, BorderLayout.NORTH);
		leaderboardDialog.add(scrollPane, BorderLayout.CENTER);

		JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 20, 10));
		JButton newGameButton = new JButton("NEW GAME");
		JButton quitButton = new JButton("QUIT");

		newGameButton.addActionListener(e -> {
			leaderboardDialog.dispose();
			startNewGame();
		});

		quitButton.addActionListener(e -> {
			leaderboardDialog.dispose();
			System.exit(0);
		});

		buttonPanel.add(newGameButton);
		buttonPanel.add(quitButton);
		leaderboardDialog.add(buttonPanel, BorderLayout.SOUTH);

		leaderboardDialog.setVisible(true);
	}

	private void startNewGame() {
		List<String> playerNames = new ArrayList<>();
		for (Player player : game.getPlayers()) {
			playerNames.add(player.getName());
		}
		game = new BluffGame(playerNames);
		updateUI();
	}

	public static void main(String[] args) {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Welcome to Bluff !!!");
		System.out.println("\nEnter name of Player 1: ");
		String player1 = scanner.next();
		System.out.println("\nEnter name of Player 2: ");
		String player2 = scanner.next();
		scanner.close();

		SwingUtilities.invokeLater(() -> new BluffGameUI(List.of(player1, player2)));
	}
}