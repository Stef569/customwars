package com.customwars.client.network;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Translate use cases into network messages
 * By using the CW1 battle server, All the real work is delegated to CW1NetworkIO
 */
public class Cw1NetworkManager implements NetworkManager {
  private final CW1NetworkIO CW1NetworkIO;

  public Cw1NetworkManager(String serverURL) {
    CW1NetworkIO = new CW1NetworkIO(serverURL);
  }

  /**
   * Create a new Server game and join the game as user 1
   */
  public void createNewServerGame(String gameName, String gamePass, Map<Tile> map, String userName, String userPassword, String comment) throws NetworkException {
    ServerGame serverGame = new ServerGame(gameName, gamePass, map.getMapName(), map.getNumPlayers(), comment);
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.createGame(serverGame, user);
      CW1NetworkIO.uploadMap(serverGame, map);
      CW1NetworkIO.joinGame(serverGame, user, 1);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Join an existing Server game
   */
  public void joinServerGame(String gameName, String gamePass, String userName, String userPassword, int side) throws NetworkException {
    ServerGame serverGame = new ServerGame(gameName, gamePass);
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.joinGame(serverGame, user, side);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Login into an existing Server game
   */
  public void loginToServerGame(String gameName, String userName, String userPassword) throws NetworkException {
    ServerGame serverGame = new ServerGame(gameName, "");
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.validateLogin(serverGame, user);
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
  public Game startServerGame(String serverGameName, String userName, String userPassword) throws NetworkException {
    ServerGame serverGame = new ServerGame(serverGameName);
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.canPlay(serverGame, user);
      Map<Tile> map = CW1NetworkIO.downloadMap(serverGame);
      ServerGameInfo serverInfo = CW1NetworkIO.getServerGameInfo(serverGameName);
      return createGame(map, serverInfo);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Use the information stored on the server(server game info and the map)
   * to create a Game
   */
  private static Game createGame(Map<Tile> map, ServerGameInfo serverGameInfo) {
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

  public void endTurn(Game game, String serverGameName, String userName, String userPassword) throws NetworkException {
    ServerGame serverGame = new ServerGame(serverGameName);
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.uploadGame(serverGame, game);
      CW1NetworkIO.nextTurn(serverGame, user);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public Game startTurn(String serverGameName, String userName, String userPassword) throws NetworkException {
    ServerGame serverGame = new ServerGame(serverGameName);
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.canPlay(serverGame, user);
      return CW1NetworkIO.downloadGame(serverGame);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Remove the player from the server game
   */
  public void destroyPlayer(Game game, Player player, String serverGameName, String userName, String userPassword) throws NetworkException {
    ServerGame serverGame = new ServerGame(serverGameName);
    User user = new User(userName, userPassword);

    try {
      ServerGameInfo gameInfo = CW1NetworkIO.getServerGameInfo(serverGameName);

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
        CW1NetworkIO.removePlayer(serverGame, user, userIDToRemove);
      }
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public void endGame(String serverGameName, String userName, String userPassword) throws NetworkException {
    ServerGame serverGame = new ServerGame(serverGameName);
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.endGame(serverGame, user);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public void sendChatMessage(String serverGameName, String userName, String chatMessage) throws NetworkException {
    ServerGame serverGame = new ServerGame(serverGameName);
    User user = new User(userName, "");

    try {
      CW1NetworkIO.sendChatMessage(serverGame, user, chatMessage);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public boolean isGameNameAvailable(String gameName) {
    try {
      CW1NetworkIO.isGameNameAvailable(gameName);
      return true;
    } catch (NetworkException ex) {
      return false;
    } catch (IOException ex) {
      return false;
    }
  }

  public ServerGameInfo getGameInfo(String gameName) throws NetworkException {
    try {
      return CW1NetworkIO.getServerGameInfo(gameName);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public String[] getChatLog(String serverGameName) throws NetworkException {
    try {
      return CW1NetworkIO.getChatLog(serverGameName);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  public String[] getSysLog(String serverGameName) throws NetworkException {
    try {
      return CW1NetworkIO.getSysLog(serverGameName);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }
}
