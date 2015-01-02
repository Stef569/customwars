package com.customwars.client.model.game;

import com.customwars.client.model.Observable;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.Args;
import org.apache.log4j.Logger;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

/**
 * A basic game that has
 * a Game state[Idle, Started, Gameover], a map, players and the current turn.
 * <p/>
 * The players list contains the players that are in the game, excluding the neutral player.
 * The order of the players equals the flow of the turns. eg. Given the following players [u1, u2, u3]
 * starting the game with u1 and ending the turn makes u2 the active player
 * <p/>
 * Neutral players are always in the IDLE state
 * Human and AI players are ACTIVE or DESTROYED.
 */
public class TurnBasedGame implements Observable, Serializable {
  private static final Logger logger = Logger.getLogger(TurnBasedGame.class);
  private final PropertyChangeSupport changeSupport = new PropertyChangeSupport(this);

  static enum GameState {
    IDLE, STARTED, GAME_OVER
  }

  final Map map;                          // The map containing all the tiles
  private final Turn turn;                // The current turn + limits
  private final List<Player> players;     // The Human and AI players that are in this game
  private Player activePlayer;            // There can only be one active player in a game at any time
  private GameState state;

  /**
   * Copy Constructor
   *
   * @param otherGame game to copy
   */
  public TurnBasedGame(TurnBasedGame otherGame) {
    map = new Map(otherGame.map);
    turn = new Turn(otherGame.turn);
    players = copyPlayers(otherGame.players);
    if (otherGame.activePlayer != null) {
      activePlayer = getPlayerByID(otherGame.activePlayer.getId());
    }
    state = otherGame.state;
  }

  private List<Player> copyPlayers(Collection<Player> players) {
    List<Player> playerCopies = new ArrayList<Player>(players.size());
    for (Player player : players) {
      Player playerCopy = new Player(player);
      playerCopies.add(playerCopy);
    }
    return playerCopies;
  }

  public TurnBasedGame(Map map, List<Player> players, int dayLimit) {
    Args.checkForNull(map);
    Args.checkForNull(players);

    this.map = map;
    this.players = new ArrayList<Player>(players);
    this.turn = new Turn(dayLimit);
    this.state = GameState.IDLE;
  }

  /**
   * @param gameStarter The player that is active in the first turn.
   * @throws IllegalStateException if the game is not in the IDLE state
   *                               When the turn limit is reached
   */
  public void startGame(Player gameStarter) {
    validateStartGame(gameStarter);

    for (Player player : players) {
      player.setState(GameObjectState.ACTIVE);
      detectUnitFacingDirection(player);

      for (Unit unit : player.getArmy()) {
        unit.setDefaultOrientation();
      }
    }

    setActivePlayer(gameStarter);
    setState(GameState.STARTED);
    startTurn(activePlayer);
    logger.debug("Game with map '" + map.getMapName() + "' has started");
  }

  /**
   * The units facing direction is based on the HQ location.
   * If the HQ is on the left side of the map the units will face to the right.
   * If the HQ is on the right side of the map the units will face to the left.
   *
   * @param player The player to find the unit facing direction for
   */
  private void detectUnitFacingDirection(Player player) {
    if (player.getHq() != null) {
      Location hqLocation = player.getHq().getLocation();
      Direction hqQuadrant = map.getQuadrantFor(hqLocation);
      Direction unitFacingDir = Direction.isEastQuadrant(hqQuadrant) ? Direction.WEST : Direction.EAST;
      player.setUnitFacingDirection(unitFacingDir);
    }
  }

  /**
   * End the turn for the active player
   *
   * @see #endTurn(Player)
   */
  public void endTurn() throws NotYourTurnException {
    endTurn(activePlayer);
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
   * When the turn Limit is reached end the game
   */
  private void increaseTurn() {
    int oldVal = getTurn();
    turn.increaseTurn();

    if (getTurn() % getActivePlayerCount() == 0) {
      turn.increaseDay();
    }

    if (turn.isLimitReached()) {
      logger.debug("Day limit reached " + turn);
      setState(GameState.GAME_OVER);
    }
    firePropertyChange("turn", oldVal, turn);
  }

  void startTurn(Player player) {
    logger.info("Day " + turn.getDay() + " Starting turn for player '" + player.getName() + "'");
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
    return turn.getLimit();
  }

  public int getDay() {
    return turn.getDay();
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

  public Map getMap() {
    return map;
  }

  /**
   * Get All players in this game
   *
   * @return all players in this game, excluding the neutral player
   */
  public Iterable<Player> getAllPlayers() {
    return new Iterable<Player>() {
      public Iterator<Player> iterator() {
        return players.iterator();
      }
    };
  }

  /**
   * Get All the players that are still alive.
   *
   * @return all active players in this game, excluding the neutral player
   */
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
   * Get the next active player after the given player
   *
   * @param player the player to start searching after for the next player
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

  /**
   * Retrieve the active player in the game
   * before the game has started there is no active player and null is returned
   * after the game is started this method guarantees to always return a player object.
   *
   * @return The active player in the game
   */
  public Player getActivePlayer() {
    return activePlayer;
  }

  /**
   * Get a player by name
   *
   * @param playerName The Name of the player to retrieve(case sensitive)
   * @return A Player within this game
   * @throws IllegalArgumentException if no player matches the given player name
   */
  public Player getPlayerByName(String playerName) {
    Player result = null;

    for (Player p : players) {
      if (p.getName().equals(playerName)) {
        result = p;
      }
    }

    if (result == null) {
      throw new IllegalArgumentException("Unknown Player name: " + playerName + " available players:" + players);
    } else {
      return result;
    }
  }

  /**
   * Get a player by ID
   *
   * @param playerID The ID of a player
   * @return A Player within this game
   * @throws IllegalArgumentException if no player matches the given player ID
   */
  public Player getPlayerByID(int playerID) {
    Player result = null;

    for (Player p : players) {
      if (p.getId() == playerID) {
        result = p;
      }
    }

    if (result == null) {
      throw new IllegalArgumentException("Unknown Player ID " + playerID + " available players:" + players);
    } else {
      return result;
    }
  }

  private void validateStartGame(Player gameStarter) {
    if (!isIdle()) {
      throw new IllegalStateException("Game is in a Illegal state " + state + " to start, a game cannot be started 2X.");
    }

    if (turn.isLimitReached()) {
      throw new IllegalStateException("Turn limit reached");
    }

    if (!players.contains(gameStarter)) {
      throw new IllegalStateException("game starter" + gameStarter + " is not in the players list");
    }

    validatePlayers();
    map.validate();
  }

  private void validatePlayers() {
    // game players must be equal to num players in map
    if (map.getNumPlayers() != players.size()) {
      throw new IllegalStateException("The amount of players in the map (" + map.getNumPlayers() + ") " +
        "does not equal the amount of players given (" + (players.size()) + ")");
    }

    // Each player must be unique
    Collection<Player> uniquePlayers = new HashSet<Player>(players);
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

        if (p.getColor().equals(player.getColor())) {
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
        "Expected=" + activePlayer.getName());
    }
  }

  void firePropertyChange(String propertyName, Serializable oldValue, Serializable newValue) {
    changeSupport.firePropertyChange(propertyName, oldValue, newValue);
  }

  public void addPropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(listener);
  }

  public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.addPropertyChangeListener(propertyName, listener);
  }

  public void addPropertyChangeListenerToEachPlayer(PropertyChangeListener listener) {
    for (Player player : getAllPlayers()) {
      player.addPropertyChangeListener(listener);
    }
  }

  public void removePropertyChangeListenerForEachPlayer(PropertyChangeListener listener) {
    for (Player player : getAllPlayers()) {
      player.removePropertyChangeListener(listener);
    }
  }

  public void removePropertyChangeListener(PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }

  public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
    changeSupport.removePropertyChangeListener(listener);
  }
}
