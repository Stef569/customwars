package com.customwars.client.network;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
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
 */
public class Cw1NetworkManager implements NetworkManager {
  private final CW1NetworkIO CW1NetworkIO;

  public Cw1NetworkManager() {
    CW1NetworkIO = new CW1NetworkIO();
  }

  /**
   * Create a new ServerGame
   */
  public void createNewServerGame(String gameName, String gamePass, Map<Tile> map, String userName, String userPassword, int side, String comment) throws NetworkException {
    ServerGame serverGame = new ServerGame(gameName, gamePass, map.getMapName(), map.getNumPlayers(), comment);
    User user = new User(userName, userPassword);

    try {
      CW1NetworkIO.createGame(serverGame, user);
      CW1NetworkIO.uploadMap(serverGame, map);
      CW1NetworkIO.joinGame(serverGame, user, side);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Join an existing ServerGame
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
   * #2
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

  private static Game createGame(Map<Tile> map, ServerGameInfo serverInfo) {
    List<Player> players = new ArrayList<Player>(map.getUniquePlayers());
    String[] userNames = serverInfo.getUserNames();
    List<Player> gamePlayers = new ArrayList<Player>();

    for (int i = 0; i < players.size(); i++) {
      Player mapPlayer = players.get(i);
      int mapPlayerID = mapPlayer.getId();
      Color mapPlayerColor = mapPlayer.getColor();
      Player gamePlayer = new Player(mapPlayerID, mapPlayerColor, userNames[i], 0, i, false);
      gamePlayers.add(gamePlayer);
    }

    // todo default game config?
    return new Game(map, gamePlayers, new GameRules());
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
      String[] userNames = gameInfo.getUserNames();
      for (int userID = 0; userID < userNames.length; userID++) {
        String serverUserName = userNames[userID];
        if (serverUserName.equals(player.getName())) {
          userIDToRemove = userID;
          break;
        }
      }

      if (userIDToRemove == -1) {
        throw new IllegalArgumentException(
          "can't destroy player, no user for " + player.getName() + " users " + Arrays.toString(userNames)
        );
      } else {
        CW1NetworkIO.removePlayer(serverGame, user, userIDToRemove);
        CW1NetworkIO.uploadGame(serverGame, game);
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