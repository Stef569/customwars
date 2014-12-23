package com.customwars.client.network.http.battleserver;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.network.MessageSenderAdapter;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.ServerGame;
import com.customwars.client.network.ServerGameInfo;
import com.customwars.client.network.User;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Send game messages to a server using the HTTP protocol.
 * All the real work is delegated to CW1NetworkIO.
 * <p/>
 * The user and server game information is set when invoking one of the entry methods:
 * createNewServerGame, joinServerGame and loginToServerGame.
 * Other method calls will reuse the user and server game info passed to these methods.
 */
public class BattleServerMessageSender extends MessageSenderAdapter {
  private final BattleServerConnection battleServerConnection;
  private User user;
  private ServerGame serverGame;

  public BattleServerMessageSender(String serverURL) {
    battleServerConnection = new BattleServerConnection(serverURL);
  }

  @Override
  public void connect() throws NetworkException {
    try {
      battleServerConnection.connect();
    } catch (IOException e) {
      throw new NetworkException(e);
    }
  }

  /**
   * Create a new Server game and join the game as user 1
   */
  public void createNewServerGame(String gameName, String gamePass, Map map, String userName, String userPassword, String comment) throws NetworkException {
    serverGame = new ServerGame(gameName, gamePass, map.getMapName(), map.getNumPlayers(), comment);
    user = new User(userName, userPassword);

    try {
      battleServerConnection.createGame(serverGame, user);
      battleServerConnection.uploadMap(serverGame, map);
      battleServerConnection.joinGame(serverGame, user, 1);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Join an existing Server game
   */
  public void joinServerGame(String gameName, String gamePass, String userName, String userPassword, int slot) throws NetworkException {
    serverGame = new ServerGame(gameName, gamePass);
    user = new User(userName, userPassword);

    try {
      battleServerConnection.joinGame(serverGame, user, slot);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Login into an existing Server game
   */
  public void loginToServerGame(String gameName, String userName, String userPassword) throws NetworkException {
    serverGame = new ServerGame(gameName);
    user = new User(userName, userPassword);

    try {
      battleServerConnection.validateLogin(serverGame, user);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Start a servergame
   * #1 Download the chosen map for this game
   * #2 Create a Game and return it
   * The returned Game is not started yet
   */
  public Game startServerGame() throws NetworkException {
    try {
      battleServerConnection.canPlay(serverGame, user);
      Map map = battleServerConnection.downloadMap(serverGame);
      ServerGameInfo serverInfo = battleServerConnection.getServerGameInfo(serverGame.getGameName());
      return createGame(map, serverInfo);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Use the information stored on the server(server game info and the map)
   * to create a Game
   */
  private static Game createGame(Map map, ServerGameInfo serverGameInfo) {
    List<Player> mapPlayers = new ArrayList<Player>(map.getUniquePlayers());
    String[] userNames = serverGameInfo.getUserNames();
    List<Player> gamePlayers = new ArrayList<Player>();

    // The user names have the same order as the turns
    // The first game player should be named as the first user name etc...
    // The player in the map is linked to the turn position
    // todo later to be replaced by linking map players to a color
    // All players are enemies of each other
    // todo how can 2 players team up?
    for (int i = 0; i < userNames.length; i++) {
      Player mapPlayer = mapPlayers.get(i);
      Player player = createPlayer(mapPlayer, userNames[i], i, false);
      gamePlayers.add(player);
    }

    return new Game(map, gamePlayers, map.getDefaultGameRules());
  }

  private static Player createPlayer(Player mapPlayer, String playerName, int team, boolean ai) {
    int mapPlayerID = mapPlayer.getId();
    Color mapPlayerColor = mapPlayer.getColor();
    return new Player(mapPlayerID, mapPlayerColor, playerName, 0, team, ai);
  }

  public void endTurn(Game game) throws NetworkException {
    try {
      battleServerConnection.uploadGame(serverGame, game);
      battleServerConnection.nextTurn(serverGame, user);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public Game startTurn() throws NetworkException {
    try {
      battleServerConnection.canPlay(serverGame, user);
      return battleServerConnection.downloadGame(serverGame);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Remove the player from the server game
   */
  public void destroyPlayer(Player player) throws NetworkException {
    try {
      ServerGameInfo gameInfo = battleServerConnection.getServerGameInfo(serverGame.getGameName());

      int userIDToRemove = -1;
      for (String serverUserName : gameInfo.getUserNames()) {
        int userID = gameInfo.getUserIdFor(serverUserName);

        if (serverUserName.equals(player.getName())) {
          userIDToRemove = userID;
          break;
        }
      }

      if (userIDToRemove == -1) {
        throw new IllegalArgumentException(
          "can't destroy player, no user for " + player.getName() + " users " + Arrays.toString(gameInfo.getUserNames())
        );
      } else {
        battleServerConnection.removePlayer(serverGame, user, userIDToRemove);
      }
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public void endGame() throws NetworkException {
    try {
      battleServerConnection.endGame(serverGame, user);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public void sendChatMessage(String chatMessage) throws NetworkException {
    try {
      battleServerConnection.sendChatMessage(serverGame, user, chatMessage);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public boolean isGameNameAvailable(String gameName) {
    try {
      battleServerConnection.isGameNameAvailable(gameName);
      return true;
    } catch (NetworkException ex) {
      return false;
    } catch (IOException ex) {
      return false;
    }
  }

  public ServerGameInfo getGameInfo(String gameName) throws NetworkException {
    try {
      return battleServerConnection.getServerGameInfo(gameName);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public String[] getChatLog() throws NetworkException {
    try {
      return battleServerConnection.getChatLog(serverGame.getGameName());
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public String[] getSysLog() throws NetworkException {
    try {
      return battleServerConnection.getSysLog(serverGame.getGameName());
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }
}