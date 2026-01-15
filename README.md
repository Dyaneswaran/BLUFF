# Bluff Card Game (Java)

This application was built as my `CS-GY 9053 Introduction to Java` Final Project.

---

Bluff Card Game is a **1v1 turn-based card game** built in **Java** with **Swing GUI**, where players try to win by discarding all their cards — using strategy, deception, and well-timed bluff calls.

This project includes:
- ✅ **Swing UI** for interactive gameplay  
- ✅ **Multithreading** for smooth deck shuffling, card distribution, and UI updates  
- ✅ **SQLite Database** to track player stats (wins/losses, games played, win %)  
- ✅ External libraries: `sqlite-jdbc`, `slf4j`, and `json`

---

## 🎮 Game Overview

Players place cards face-down and **declare a rank** (ex: “J”).  
Opponents can **call a bluff** if they suspect the player is lying.

- If the bluff is correct → the bluffer picks up the pile  
- If the bluff is wrong → the challenger picks up the pile  

The first player to **empty their hand** wins.

---

## ✨ Features

- **Interactive Gameplay**
  - Play one or more cards per turn
  - Declare rank and bluff if you want
  - Call bluff or pass with real-time UI feedback

- **Turn-Based Mechanics**
  - Proper enforcement of turn order
  - Bluff calls allowed only on valid moves
  - Round logic handled cleanly

- **Player Statistics (SQLite)**
  - Automatically stores:
    - Player name
    - Games played
    - Wins / Losses
    - Win Percentage

- **Leaderboard View**
  - Displays updated stats after each match

---

## 🛠 Tech Stack

- **Language:** Java (JDK 17+)
- **UI:** Java Swing
- **Database:** SQLite (`records.db`)
- **Libraries:**  
  - `sqlite-jdbc`
  - `slf4j-api`, `slf4j-simple`
  - `json`

---

## 🚀 How to Run

### ✅ Prerequisites
- Install **JDK 17 or later**
- Use any IDE like:
  - IntelliJ IDEA
  - Eclipse

> No extra installations required — external JARs and database (`records.db`) are already included.

---

### ▶️ Run Instructions
1. **Import the Project**
   - Eclipse: `File → Import → Existing Projects into Workspace`
   - IntelliJ: `Open` the extracted project folder

2. **Verify Dependencies**
   Confirm the `lib/` folder includes:
   - `json-20140107.jar`
   - `sqlite-jdbc-3.47.0.0.jar`
   - `slf4j-api-2.0.16.jar`
   - `slf4j-simple-2.0.16.jar`

   Also confirm:
   - `resources/records.db` exists

3. **Run the Application**
   - Go to: `src/UI/BluffGameUI.java`
   - Right-click → **Run as Java Application**

4. **Enter Player Names**
   - Enter two player names in the console when prompted  
   ✅ Player 1 gets the first move

---

## 📜 Rules of the Game

### 1) Objective
Be the first player to **discard all your cards**.

### 2) Setup
- A shuffled deck is dealt equally
- This project currently deals **only face cards (J, Q, K, A)** for faster gameplay  
  ✅ You can enable the full deck by uncommenting the line mentioned in `Deck.java`

### 3) Playing Cards
On your turn:
- Select card(s) in the UI
- Enter a declared rank in the rank field (example: `J`)
- Place your cards onto the discard pile face-down

### 4) Bluffing
You may lie about the rank you played.
Example:
- Play a King but declare “J”
- Or play mixed cards and declare them all as one rank

### 5) Calling a Bluff
A player can call bluff on the **most recent play**.
- You **cannot call bluff** if the opponent **passed last turn**

Outcome:
- Bluff correct → bluffer picks up discard pile
- Bluff wrong → challenger picks up discard pile

### 6) Passing
- You cannot pass on the first move of a round
- Once you pass, you cannot play again until the next round  
✅ You *can still* call bluff after passing (when turn returns to you)

### 7) Round Ends
If all players pass → round ends  
The last player who played starts the next round.

### 8) Winning
Winning is checked when the turn returns to the player who has **0 cards left**.

---

## License
This project is developed for educational purposes and is open for personal use.

---

## Author
**Dyaneswaran Namasivayam**  
Email: dt2543@nyu.edu
