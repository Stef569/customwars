package com.customwars.client.network.http.battleserver;

import com.customwars.client.model.co.CO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.network.MessageSenderAdapter;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.ServerGame;
import com.customwars.client.network.ServerGameConfig;
import com.customwars.client.network.ServerGameInfo;
import com.customwars.client.network.ServerPlayer;
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
   * 1. Create a new Server game with the given game name and game password
   * 2. Store Player Color, Team, Controller, Co in the server Config. These values are set by the host.
   * 3. Upload the initial game to the server
   */
  public void createNewServerGame(String gameName, String gamePass, Map map, String userName, String userPassword, String comment, ServerGameConfig gameConfig) throws NetworkException {
    serverGame = new ServerGame(gameName, gamePass, map.getMapName(), map.getNumPlayers(), comment);
    user = new User(userName, userPassword);

    try {
      battleServerConnection.createGame(serverGame, user);
      battleServerConnection.uploadGameConfig(serverGame, gameConfig);
      Game game = createInitialGame(map, gameConfig);
      battleServerConnection.uploadGame(serverGame, game);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  private Game createInitialGame(Map map, ServerGameConfig gameConfig) {
    List<Player> gamePlayers = new ArrayList<Player>();
    for (ServerPlayer player : gameConfig.getPlayers()) {
      int slot = player.getSlot();
      Color color = player.getColor();
      String name = "p" + slot;
      int team = player.getTeam();
      boolean ai = player.isAIController();
      CO co = COFactory.getCO(player.getCOName());
      gamePlayers.add(new Player(slot, color, name, 0, team, ai, co));
    }

    return new Game(map, gamePlayers, map.getDefaultGameRules());
  }

  /**
   * Join an existing Server game. Register the slot for the given user name.
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
   * Start a server game
   * 1. Check if the user can start this game
   * 2. Download the initial game
   * 3. Create a new Game and return it
   * The returned Game is not started yet
   */
  public Game startServerGame() throws NetworkException {
    try {
      battleServerConnection.canPlay(serverGame, user);
      Game game = battleServerConnection.downloadGame(serverGame);
      ServerGameInfo serverGameInfo = battleServerConnection.getServerGameInfo(serverGame.getGameName());
      ServerGameConfig serverGameConfig = battleServerConnection.downloadGameConfig(serverGame);
      return createGame(game, serverGameInfo, serverGameConfig);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Use the information stored on the server (server game info, ServerPlayers and the map) to create a Game
   * The map is stored in the initial game.
   */
  private static Game createGame(Game initialGame, ServerGameInfo serverGameInfo, ServerGameConfig serverGameConfig) {
    Map map = initialGame.getMap();
    String[] userNames = serverGameInfo.getUserNames();
    List<ServerPlayer> serverPlayerList = serverGameConfig.getPlayers();

    List<Player> gamePlayers = new ArrayList<Player>();
    for (int i = 0; i < serverPlayerList.size(); i++) {
      ServerPlayer player = serverPlayerList.get(i);
      Player newPlayer = createPlayer(i, player, userNames[i]);
      gamePlayers.add(newPlayer);
    }

    return new Game(map, gamePlayers, map.getDefaultGameRules());
  }

  private static Player createPlayer(int id, ServerPlayer player, String playerName) {
    Color color = player.getColor();
    CO co = COFactory.getCO(player.getCOName());
    return new Player(id, color, playerName, 0, player.getTeam(), player.isAIController(), co);
  }

  /**
   * Ends the current turn.
   * 1. The game is uploaded to the server
   * 2. The next player can now make his moves
   *
   * @param game The game with the changes from the current player
   */
  public void endTurn(Game game) throws NetworkException {
    try {
      battleServerConnection.uploadGame(serverGame, game);
      battleServerConnection.nextTurn(serverGame, user);
    } catch (IOException ex) {
      throw new NetworkException(ex);
    }
  }

  /**
   * Allows the player to start making his moves.
   *
   * @return The game downloaded from the server containing the up to date positions of all game objects.
   */
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