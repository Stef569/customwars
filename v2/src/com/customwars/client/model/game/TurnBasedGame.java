package com.customwars.client.model.game;

import com.customwars.client.model.gameobject.GameObject;
import com.customwars.client.model.gameobject.GameObjectState;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.rules.GameRules;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.Iterator;
import java.util.List;

/**
 * A basic game that has
 * a state, map, players and the current turn.
 * It can be in 3 states:
 * IDLE, IN_GAME(active), GAME_OVER(destroyed)
 *
 * @author Stefan
 */
public abstract class TurnBasedGame extends GameObject {
  private static final Logger logger = Logger.getLogger(TurnBasedGame.class);
  Map<Tile> map;                // The map containing all the units/cities
  Turn turn;                    // The current turn
  private List<Player> players; // The players that are in this game
  Player activePlayer;          // There can only be one active player in a game at any time
  private GameRules rules;

  /**
   * Start a game with no turn specified
   * The subclass is responsible for setting the turn
   */
  public TurnBasedGame(Map<Tile> map, List<Player> players, GameRules rules) {
    this(map, players, null, rules);
  }

  public TurnBasedGame(Map<Tile> map, List<Player> players, Turn turn, GameRules rules) {
    this.map = map;
    this.players = players;
    this.turn = turn;
    this.rules = rules;
  }

  // ---------------------------------------------------------------------------
  // Actions
  // ---------------------------------------------------------------------------
  /**
   * @param gameStarter The player that starts this game
   */
  public void startGame(Player gameStarter) {
    canStartGame(gameStarter);
    setActivePlayer(gameStarter);
    setState(GameObjectState.ACTIVE);
    endTurn(gameStarter);
  }

  public void startTurn(Player currentPlayer) {
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
  private void endTurn(Player invoker) throws NotYourTurnException {
    canEndTurn(invoker);
    Player nextActivePlayer = getNextActivePlayer(activePlayer);

    if (rules.isGameOver(this)) {
      setState(GameObjectState.DESTROYED);
      return;
    }

    turn.increaseTurn();
    if (turn.isTurnLimitReached()) {
      setState(GameObjectState.DESTROYED);
      return;
    }

    // End the turn for the invoker
    invoker.endTurn();
    map.endTurn(invoker);

    // Start the next
    nextActivePlayer.startTurn();
    map.startTurn(nextActivePlayer);
    setActivePlayer(nextActivePlayer);

    firePropertyChange("turn", turn, turn);
  }

  /**
   * We can end the turn when
   * the game in the IN_GAME(Active) state
   * there is an active player
   * there is a nextPlayer available
   * the invoker is the current activePlayer
   *
   * @param invoker The player trying to end his turn
   * @throws NotYourTurnException when the invoking player cannot end his turn
   */
  private void canEndTurn(Player invoker) throws NotYourTurnException {
    Args.checkForNull(invoker, "requesting EndOfTurn Player cannot be null");

    // Game check:
    if (!isActive()) {
      throw new IllegalStateException("Trying to endTurn on a not started Game");
    }

    // Player check:
    Player currentActivePlayer = getActivePlayer();           // current player
    Player nextActivePlayer = getNextPlayer(activePlayer);    // Next player to be active

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

  public void changePlayerName(String from, String to) {
    Player p = getPlayer(from);
    p.setName(to);
  }

  public void setRules(GameRules rules) {
    this.rules = rules;
  }

  private void setActivePlayer(Player p) {
    Player oldVal = this.activePlayer;
    activePlayer = p;
    firePropertyChange("activePlayer", oldVal, this.activePlayer);
  }

  public int getTurn() {
    return turn.getTurnCount();
  }

  public int getDay() {
    int day, turnCount, playerCount;

    turnCount = turn.getTurnCount();
    playerCount = players.size();

    if (playerCount > 0) {
      day = turnCount / playerCount;
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
    Player nextPlayer = getNextPlayer(player);
    while (nextPlayer.isDestroyed()) {
      nextPlayer = getNextPlayer(nextPlayer);
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

  // ---------------------------------------------------------------------------
  // Validation
  // --------------------------------------------------------------------------
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
      throw new IllegalStateException("Game is in a Illegal state " + getState() + " to start");
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
}
