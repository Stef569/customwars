package com.customwars.client.model.game;

import com.customwars.client.model.Observable;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.apache.log4j.Logger;
import tools.Args;

import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

/**
 * A basic game that has
 * a Game state[Idle, Started, Gameover], a map, players and the current turn.
 *
 * The players list contains the players that are in the game, excluding the neutral player.
 * Neutral players are always in the IDLE state
 * Human and AI players are ACTIVE or DESTROYED.
 *
 * @author Stefan
 */
public class TurnBasedGame implements Observable {
  private static final Logger logger = Logger.getLogger(TurnBasedGame.class);
  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  static enum GameState {
    IDLE, STARTED, GAME_OVER
  }

  Map<Tile> map;                // The map containing all the tiles
  Turn turn;                    // The current turn+turn limit
  final Player neutralPlayer;   // Idle neutral player for cities that are not owned.
  private List<Player> players; // The Human and AI players that are in this game
  private Player activePlayer;  // There can only be one active player in a game at any time
  private GameState state;

  public TurnBasedGame(Map<Tile> map, List<Player> players, int turnLimit, int dayLimit) {
    Args.checkForNull(map);
    Args.checkForNull(players);
    Args.validateBetweenMinMax(turnLimit, -1, Integer.MAX_VALUE, "turn limit");
    Args.validateBetweenMinMax(dayLimit, -1, Integer.MAX_VALUE, "day limit");

    this.map = map;
    this.players = players;
    this.neutralPlayer = new Player(Player.NEUTRAL_PLAYER_ID, Color.GRAY, true, null, "Neutral", 0, -1, false);
    this.turn = new Turn(0, 1, turnLimit, dayLimit);
    this.state = GameState.IDLE;
  }

  /**
   * @param gameStarter The player that starts this game
   * @throws IllegalStateException if the game is not in the IDLE state
   *                               When the turn limit is reached
   */
  public void startGame(Player gameStarter) {
    validateStartGame(gameStarter);

    for (Player player : players) {
      player.setState(GameObjectState.ACTIVE);
    }

    setActivePlayer(gameStarter);
    setState(GameState.STARTED);
    startTurn(activePlayer);
    logger.debug("Game with map " + map.getProperty("NAME") + " has started");
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
  protected void endTurn(Player invoker) throws NotYourTurnException {
    validateEndTurn(invoker);
    Player nextActivePlayer = getNextActivePlayer(activePlayer);
    increaseTurn();
    invoker.endTurn();
    map.endTurn(invoker);
    startTurn(nextActivePlayer);
  }

  /**
   * Increase turn and day
   * When the turnLimit or dayLimit is reached end the game
   */
  private void increaseTurn() {
    int oldVal = getTurn();
    turn.increaseTurn();

    if (turn.isTurnLimitReached()) {
      logger.debug("Turn limit reached " + turn);
      setState(GameState.GAME_OVER);
    }

    if (getTurn() % getActivePlayerCount() == 0) {
      turn.increaseDay();
    }

    if (turn.isDayLimitReached()) {
      logger.debug("Day limit reached " + turn);
      setState(GameState.GAME_OVER);
    }
    firePropertyChange("turn", oldVal, turn);
  }

  void startTurn(Player player) {
    player.startTurn();
    map.startTurn(player);
    setActivePlayer(player);
  }

  public void changePlayerName(String from, String to) {
    Player p = getPlayerByName(from);
    p.setName(to);
  }

  void setState(GameState newState) {
    GameState oldState = this.state;
    this.state = newState;
    firePropertyChange("state", oldState, newState);
  }

  private void setActivePlayer(Player p) {
    Player oldVal = this.activePlayer;
    this.activePlayer = p;
    firePropertyChange("activeplayer", oldVal, p);
  }

  public int getTurn() {
    return turn.getTurnCount();
  }

  public int getDayLimit() {
    return turn.getDayLimit();
  }

  public int getDay() {
    return turn.getDay();
  }

  public int getDay(int turnCount) {
    int day, playerCount = getActivePlayerCount();

    if (playerCount > 0) {
      day = (turnCount / playerCount) + 1;
    } else {
      day = -1;
    }
    return day;
  }

  public boolean isGameOver() {
    return state == GameState.GAME_OVER;
  }

  public boolean isStarted() {
    return state == GameState.STARTED;
  }

  public boolean isIdle() {
    return state == GameState.IDLE;
  }

  int getActivePlayerCount() {
    int playerCount = 0;
    for (Player player : players) {
      if (player.isActive()) {
        playerCount++;
      }
    }
    return playerCount;
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

  public List<Player> getActivePlayers() {
    List<Player> activePlayers = new ArrayList<Player>();
    for (Player player : getAllPlayers()) {
      if (player.isActive()) {
        activePlayers.add(player);
      }
    }
    return activePlayers;
  }

  /**
   * @param player the player index to start searching at in the players list
   * @return the next active player after player
   */
  public Player getNextActivePlayer(Player player) {
    Player nextActivePlayer = getNextPlayer(player);
    int playerSkipCount = 0;

    while (!nextActivePlayer.isActive()) {
      nextActivePlayer = getNextPlayer(nextActivePlayer);

      if (!isWithinPlayerBounds(++playerSkipCount)) {
        throw new AssertionError("All players skipped");
      }
    }
    return nextActivePlayer;
  }

  private Player getNextPlayer(Player player) {
    int nextPlayerIndex = players.indexOf(player) + 1;
    if (nextPlayerIndex == players.size()) {
      return players.get(0);
    }
    return players.get(nextPlayerIndex);
  }

  private boolean isWithinPlayerBounds(int index) {
    return index >= 0 && index < players.size();
  }

  public Player getActivePlayer() {
    return activePlayer;
  }

  /**
   * Get a game player by his playerName
   * playerName is case sensitive
   *
   * @throws IllegalArgumentException if the player could not be found
   */
  public Player getPlayerByName(String playerName) {
    Player result = null;

    for (Player p : players) {
      if (p.getName().equals(playerName)) {
        result = p;
      }
    }

    if (result == null) {
      throw new IllegalArgumentException("Unknown Playername: " + playerName + " available players:" + players);
    } else {
      return result;
    }
  }

  /**
   * Get a player by his ID
   * The ID can be of a game player or of the neutral player
   *
   * @throws IllegalArgumentException if the player could not be found
   */
  public Player getPlayerByID(int playerID) {
    Player result = null;

    for (Player p : players) {
      if (p.getId() == playerID) {
        result = p;
      }
    }

    if (neutralPlayer.getId() == playerID) {
      result = neutralPlayer;
    }

    if (result == null) {
      throw new IllegalArgumentException("Unknown PlayerID " + playerID + " available players:" + players);
    } else {
      return result;
    }
  }

  private void validateStartGame(Player gameStarter) {
    if (!isIdle()) {
      throw new IllegalStateException("Game is in a Illegal state " + state + " to start, a game cannot be started 2X.");
    }

    if (turn.isTurnLimitReached()) {
      throw new IllegalStateException("Turn limit reached");
    }

    if (!players.contains(gameStarter)) {
      throw new IllegalStateException("game starter" + gameStarter + " is not in the players list");
    }

    validatePlayers();
  }

  private void validatePlayers() {
    if (players == null)
      throw new IllegalArgumentException("No players set");

    // game players must be equal to num players in map
    if (map.getNumPlayers() != players.size()) {
      throw new IllegalStateException("The amount of players in the map (" + map.getNumPlayers() + ") " +
        "does not equal the amount of players given (" + (players.size()) + ")");
    }

    // Each player must be unique
    Set<Player> uniquePlayers = new HashSet<Player>(players);
    if (players.size() != uniquePlayers.size()) {
      throw new IllegalStateException("duplicate player found " + players);
    }

    for (Player p : getAllPlayers()) {
      if (p == null) {
        throw new IllegalStateException("A player is null");
      }

      if (p.isNeutral()) {
        throw new IllegalStateException("A neutral player cannot be included in the player list");
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

  /**
   * Check if the current turn can be ended
   *
   * @param invoker The player trying to end his turn
   * @throws NotYourTurnException when the invoking player cannot end his turn
   */
  private void validateEndTurn(Player invoker) throws NotYourTurnException {
    Args.checkForNull(invoker, "End turn invoker cannot be null");

    if (!isStarted())
      throw new IllegalStateException("Trying to endTurn on a not started Game");

    if (isGameOver())
      throw new IllegalStateException("Trying to endTurn on an ended Game");

    Args.checkForNull(activePlayer, "No active Player");
    Player nextPlayer = getNextActivePlayer(activePlayer);
    Args.checkForNull(nextPlayer, "No player found after player: " + activePlayer);

    if (invoker != activePlayer) {
      throw new NotYourTurnException("Player " + invoker + " cannot end his turn , because it is not the Active player in the Game\n" +
        "Expected=" + getActivePlayer().getName());
    }
  }

  void firePropertyChange(String propertyName, Object oldValue, Object newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }
}
