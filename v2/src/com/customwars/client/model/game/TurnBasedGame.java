package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.GameObject;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;
import tools.Args;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * A basic game that has
 * a state, map, players and the current turn.
 * It can be in 3 states:
 * IDLE, IN_GAME(active), GAME_OVER(destroyed)
 *
 * The game is over when:
 * The turn limit is reached or
 * When all enemies of the active player have been destroyed.
 *
 * @author Stefan
 */
public class TurnBasedGame extends GameObject implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(TurnBasedGame.class);
  Map<Tile> map;                // The map containing all the units/cities
  Turn turn;                    // The current turn+turn limit
  private List<Player> players; // The players that are in this game
  private Player activePlayer;  // There can only be one active player in a game at any time

  /**
   * Start a game with no turn specified
   * The subclass is responsible for setting the turn
   */
  public TurnBasedGame(Map<Tile> map, List<Player> players) {
    this(map, players, null);
  }

  public TurnBasedGame(Map<Tile> map, List<Player> players, Turn turn) {
    this.map = map;
    this.players = players;
    this.turn = turn;

    for (Player player : players) {
      player.addPropertyChangeListener(this);
    }
  }

  // ---------------------------------------------------------------------------
  // Actions
  // ---------------------------------------------------------------------------
  /**
   * Start the turn and end the turn of the gameStarter
   * This has the unwanted effect of increasing the turn by 1, so the turn count is put back to 0
   *
   * @param gameStarter The player that starts this game
   */
  public void startGame(Player gameStarter) {
    canStartGame(gameStarter);
    setActivePlayer(gameStarter);
    setState(GameObjectState.ACTIVE);
    startTurn(gameStarter);
  }

  public void endTurn() throws NotYourTurnException {
    endTurn(getActivePlayer());
  }

  /**
   * Ends this turn for the invoker Player. Only the active player can end his turn.
   *
   * @param invoker player trying to end his turn
   * @throws NotYourTurnException When the invoker is not the activePlayer
   */
  public void endTurn(Player invoker) throws NotYourTurnException {
    canEndTurn(invoker);
    Player nextActivePlayer = getNextActivePlayer(activePlayer);
    increaseTurn();
    invoker.endTurn();
    map.endTurn(invoker);
    startTurn(nextActivePlayer);
  }

  private void increaseTurn() {
    int oldVal = getTurn();
    turn.increaseTurn();
    if (turn.isTurnLimitReached()) {
      endGame();
    }
    firePropertyChange("turn", oldVal, turn);
  }

  /**
   * Check if we can end a turn
   *
   * @param invoker The player trying to end his turn
   * @throws NotYourTurnException when the invoking player cannot end his turn
   */
  private void canEndTurn(Player invoker) throws NotYourTurnException {
    Args.checkForNull(invoker, "requesting EndOfTurn Player cannot be null");

    // Game check:
    if (isIdle())
      throw new IllegalStateException("Trying to endTurn on a not started Game");

    if (isGameOver())
      throw new IllegalStateException("Trying to endTurn on a ended Game");

    // Player check:
    Player currentActivePlayer = getActivePlayer();                 // current player
    Player nextActivePlayer = getNextActivePlayer(activePlayer);    // Next player to be active

    if (currentActivePlayer == null) {
      throw new IllegalStateException("No ActivePlayer");
    }

    if (nextActivePlayer == null) {
      throw new IllegalStateException("No player after ActivePlayer: " + currentActivePlayer);
    }

    if (invoker != currentActivePlayer) {
      throw new NotYourTurnException("Player " + invoker + " cannot end his turn , because it is not the Active player in the Game\n" +
              "Expected=" + getActivePlayer().getName());
    }
  }

  private void startTurn(Player nextActivePlayer) {
    nextActivePlayer.startTurn();
    map.startTurn(nextActivePlayer);
    setActivePlayer(nextActivePlayer);
  }

  private void endGame() {
    setState(GameObjectState.DESTROYED);
  }

  public void changePlayerName(String from, String to) {
    Player p = getPlayer(from);
    p.setName(to);
  }

  private void setActivePlayer(Player p) {
    Player oldVal = this.activePlayer;
    this.activePlayer = p;
    firePropertyChange("activePlayer", oldVal, p);
  }

  public boolean isGameOver() {
    return isDestroyed();
  }

  public int getTurn() {
    return turn.getTurnCount();
  }

  public int getDay() {
    int day, turnCount, playerCount;

    turnCount = turn.getTurnCount();
    playerCount = players.size();

    if (playerCount > 0) {
      day = (turnCount / playerCount) + 1;
    } else {
      day = -1;
    }
    return day;
  }

  public Map<Tile> getMap() {
    return map;
  }

  public Iterable<Player> getAllPlayers() {
    return new Iterable<Player>() {
      public Iterator<Player> iterator() {
        return players.iterator();
      }
    };
  }

  public Player getNextActivePlayer(Player player) {
    int playerSkipCount = 0;
    Player nextPlayer = getNextPlayer(player);
    while (nextPlayer.isDestroyed()) {
      nextPlayer = getNextPlayer(nextPlayer);
      if (playerSkipCount++ == players.size()) {
        return null;
      }
    }
    return nextPlayer;
  }

  public Player getNextPlayer(Player player) {
    int nextPlayerIndex = players.indexOf(player) + 1;
    if (nextPlayerIndex == players.size()) {
      return players.get(0);
    }
    return players.get(nextPlayerIndex);
  }

  public Player getActivePlayer() {
    return activePlayer;
  }

  /**
   * Get a player by his playerName
   * if there is no player with this playerName then
   * null will be returned
   * playerName is case sensitive!
   */
  public Player getPlayer(String playerName) {
    Player result = null;

    for (Player p : players) {
      if (p.getName().equals(playerName)) {
        result = p;
      }
    }

    if (result == null) {
      logger.warn("Unknown Playername: " + playerName + " available players:" + getAllPlayers());
      return null;
    } else {
      return result;
    }
  }

  /**
   * Get a player by his ID
   * if there is no player with this ID then
   * null will be returned
   * The ID starts from 0 to maxPlayers
   */
  public Player getPlayer(int id) {
    for (Player p : getAllPlayers()) {
      if (p.getId() == id) {
        return p;
      }
    }
    return null;
  }

  /**
   * Does the Player p comes directly after the activePlayer
   */
  public boolean isNextPlayer(Player p) {
    return players.indexOf(activePlayer) + 1 == players.indexOf(p);
  }

  /**
   * Check if we can Start the game
   * is the game already in progress? or still loading?
   * is a map provided?
   *
   * @param gameStarter The Player that started this game
   * @throws IllegalStateException when the game cannot be started
   */
  protected void canStartGame(Player gameStarter) {
    if (!isIdle()) {
      throw new IllegalStateException("Game is in a Illegal state " + getState() + " to start, a game cannot be started 2X.");
    }

    if (map == null) {
      throw new IllegalStateException("No map set");
    }

    if (turn == null || turn.isTurnLimitReached()) {
      throw new IllegalStateException("turn is null or turn limit reached");
    }

    validatePlayers();
  }

  /**
   * The amount of players in the map equals the amount of players provided
   * Are all players set
   * do they have unique colors
   * do they have unique ids
   * fires IllegalStateException when one of the above statements is false
   */
  protected void validatePlayers() {
    if (players == null)
      throw new IllegalArgumentException("No players set");

    if (map.getNumPlayers() != players.size()) {
      throw new IllegalStateException("The amount of players in the map (" + map.getNumPlayers() + ") " +
              "does not equal the amount of players given (" + players.size() + ")");
    }

    for (Player p : getAllPlayers()) {
      if (p == null) {
        throw new IllegalStateException("A player is null");
      }

      // Compare this player p with the other players
      for (Player player : getAllPlayers()) {
        if (p == player) continue;

        if (p.getColor() == player.getColor()) {
          throw new IllegalStateException("Player " + p + " has the same color as player " + player);
        }

        if (p.getId() == player.getId()) {
          throw new IllegalStateException("Player " + p + " has the same id as player " + player);
        }
      }
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (evt.getSource().getClass() == Player.class) {
      if (propertyName.equals("unit")) {
        if (isActive() && allEnemiesDead()) {
          endGame();
        }
      }
    }
  }

  private boolean allEnemiesDead() {
    List<Player> enemies = getEnemies();

    if (enemies.size() == 0) {
      return true;
    }

    for (Player enemy : enemies) {
      if (enemy.isDestroyed()) {
        return true;
      }
    }
    return false;
  }

  private List<Player> getEnemies() {
    List<Player> enemies = new ArrayList<Player>();
    for (Player p : getAllPlayers()) {
      if (!p.isAlliedWith(activePlayer)) {
        enemies.add(p);
      }
    }
    return enemies;
  }
}
