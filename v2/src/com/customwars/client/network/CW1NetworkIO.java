package com.customwars.client.network;

import com.customwars.client.App;
import com.customwars.client.io.loading.BinaryCW2GameParser;
import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;

/**
 * This class hides the details of sending and receiving information to/from the cw1 server.
 */
public class CW1NetworkIO {
  private final String serverURL;
  private static final String GAME_VERSION = App.get("game.name") + ' ' + App.get("game.version");

  // Perl Scripts
  private static final String MAIN_SCRIPT = "main.pl";
  private static final String UPLOAD_GAME_SCRIPT = "usave.pl";
  private static final String UPLOAD_MAP_SCRIPT = "umap.pl";
  private static final String DOWNLOAD_GAME_SCRIPT = "dsave.pl";
  private static final String DOWNLOAD_MAP_SCRIPT = "dmap.pl";

  // Commands
  private static final String TEST_CONNECTION = "test";
  private static final String CREATE_NEW_GAME = "newgame";
  private static final String JOIN_GAME = "join";
  private static final String END_GAME = "endgame";
  private static final String NEXT_TURN = "nextturn";
  private static final String REMOVE_PLAYER = "dplay";
  private static final String GET_INFO = "getturn";
  private static final String GAME_NAME_AVAILABLE = "qname";
  private static final String CAN_PLAY = "canplay";
  private static final String VALIDUP = "validup";
  private static final String GET_SYS_LOG = "getsys";
  private static final String GET_CHAT_LOG = "getchat";
  private static final String SEND_CHAT = "sendchat";

  // Replies
  private static final String SUCCESS = "success";
  private static final String GAME_CREATED = "game created";
  private static final String FILE_RECEIVED = "file recieved";
  private static final String DENIED = "no";
  private static final String PERMISSION_GRANTED = "permission granted";
  private static final String ACK = "yes";
  private static final String LOGIN_OK = "login successful";
  private static final String VERSION_MISMATCH = "version mismatch";
  private static final String AUTHENTICATION_FAILED = "username or password incorrect";
  private static final String UPDATE_OK = "update successful";
  private static final String WRONG_PASSWORD = "wrong password";
  private static final String JOIN_SUCCESSFUL = "join successful";
  private static final String OUT_OF_RANGE = "out of range";
  private static final String SLOT_TAKEN = "slot taken";
  private static final String MESSAGE_RECIEVED = "message recieved";

  private static final String TEMP_SAVE_FILE_NAME = "CW2net";
  private static final String TEMP_SAVE_FILE_EXT = ".save";

  public CW1NetworkIO(String serverURL) {
    this.serverURL = serverURL;
  }

  public void connect() throws IOException, NetworkException {
    HttpClient httpClient = new HttpClient(serverURL + MAIN_SCRIPT);
    httpClient.send(TEST_CONNECTION);
    String[] replies = httpClient.readReplies();
    String reply = replies[0];

    if (!reply.equals(SUCCESS)) {
      throw new NetworkException("Could not connect to " + serverURL + MAIN_SCRIPT, replies);
    }
  }

  public void createGame(ServerGame game, User user) throws IOException, NetworkException {
    String gameName = game.getGameName();
    String masterPass = game.getMasterPass();
    String mapName = game.getMapName();
    String numOfArmiesOnMap = game.getNumOfArmiesOnMap() + "";
    String comment = game.getComment();
    String[] replies = sendCommand(CREATE_NEW_GAME, gameName, masterPass, numOfArmiesOnMap, GAME_VERSION, comment, mapName, user.getName());
    String reply = replies[0];

    if (!reply.equalsIgnoreCase(GAME_CREATED)) {
      if (reply.equalsIgnoreCase(DENIED)) {
        throw new NetworkException("Game name taken", replies);
      }
    }
  }

  public void joinGame(ServerGame game, User user, int slot) throws IOException, NetworkException {
    String[] replies = sendCommand(JOIN_GAME, game.getGameName(), game.getMasterPass(), user.getName(), user.getPassword(), slot + "", GAME_VERSION);
    String reply = replies[0];

    if (!reply.equalsIgnoreCase(JOIN_SUCCESSFUL)) {
      String reason = "";
      if (reply.equalsIgnoreCase(WRONG_PASSWORD)) {
        reason = "Wrong password";
      } else if (reply.equalsIgnoreCase(OUT_OF_RANGE)) {
        reason = "Army choice out of range or invalid";
      } else if (reply.equalsIgnoreCase(SLOT_TAKEN)) {
        reason = "Army choice already taken";
      } else {
        reason = "unknown";
      }
      throw new NetworkException("Could not join game " + game.getGameName() + " " + reason, replies);
    }
  }

  public void validateLogin(ServerGame serverGame, User user) throws IOException, NetworkException {
    String[] replies = sendCommand(VALIDUP, serverGame.getGameName(), user.getName(), user.getPassword(), GAME_VERSION);
    String reply = replies[0];

    if (!reply.equalsIgnoreCase(LOGIN_OK)) {
      String err = "Problem logging in ";
      if (reply.equalsIgnoreCase(VERSION_MISMATCH)) {
        throw new NetworkException(err + " The server reported a version mismatch", replies);
      } else if (reply.equalsIgnoreCase(AUTHENTICATION_FAILED)) {
        throw new NetworkException(err + "'Wrong user name or password'", replies);
      } else {
        throw new NetworkException(err +
          "either the username/password is incorrect or the game " + serverGame.getGameName() + " has ended.", replies
        );
      }
    }
  }

  public void canPlay(ServerGame serverGame, User user) throws IOException, NetworkException {
    String[] replies = sendCommand(CAN_PLAY, serverGame.getGameName(), user.getName(), user.getPassword());
    String reply = replies[0];

    if (!reply.equals(PERMISSION_GRANTED)) {
      if (reply.equals("not your turn")) {
        throw new NetworkException("It's not your turn", replies);
      } else {
        throw new NetworkException("Can not play game", replies);
      }
    }
  }

  public void isGameNameAvailable(String gameName) throws IOException, NetworkException {
    String[] replies = sendCommand(GAME_NAME_AVAILABLE, gameName);
    String reply = replies[0];

    if (!reply.equalsIgnoreCase(ACK)) {
      if (reply.equalsIgnoreCase(DENIED)) {
        throw new NetworkException("Game name already taken", replies);
      } else {
        throw new NetworkException("Unknown", replies);
      }
    }
  }

  public ServerGameInfo getServerGameInfo(String gameServerName) throws IOException, NetworkException {
    String[] replies = sendCommand(GET_INFO, gameServerName);
    String reply = replies[0];

    if (reply.equalsIgnoreCase(DENIED)) {
      throw new NetworkException("Can't get game info for server game " + gameServerName, replies);
    }

    int day = Integer.parseInt(replies[0]);
    int turn = Integer.parseInt(replies[1]);
    int numPlayers = Integer.parseInt(replies[2]);
    String[] userNames = new String[numPlayers];
    System.arraycopy(replies, 3, userNames, 0, numPlayers);

    return new ServerGameInfo(day, turn, numPlayers, userNames);
  }

  public void endGame(ServerGame serverGame, User user) throws IOException, NetworkException {
    String[] replies = sendCommand(END_GAME, serverGame.getGameName(), user.getName(), user.getPassword());
    String reply = replies[0];

    if (!reply.equals(UPDATE_OK)) {
      throw new NetworkException("Could not end the game", replies);
    }
  }

  public void nextTurn(ServerGame serverGame, User user) throws IOException, NetworkException {
    String[] replies = sendCommand(NEXT_TURN, serverGame.getGameName(), user.getName(), user.getPassword());
    String reply = replies[0];

    if (!reply.equals(UPDATE_OK)) {
      throw new NetworkException("Could not go to the next turn", replies);
    }
  }

  public void removePlayer(ServerGame serverGame, User user, int armyID) throws IOException, NetworkException {
    String[] replies = sendCommand(REMOVE_PLAYER, serverGame.getGameName(), user.getName(), user.getPassword(), armyID + "");
    String reply = replies[0];

    if (!reply.equals(UPDATE_OK)) {
      throw new NetworkException("Could not remove player " + armyID);
    }
  }

  public void uploadMap(ServerGame serverGame, Map<Tile> map) throws IOException, NetworkException {
    HttpClient client = new HttpClient(serverURL + UPLOAD_MAP_SCRIPT);
    File tempSaveFile = File.createTempFile(TEMP_SAVE_FILE_NAME, TEMP_SAVE_FILE_EXT);
    BinaryCW2MapParser mapParser = new BinaryCW2MapParser();
    mapParser.writeMap(map, new GZIPOutputStream(new FileOutputStream(tempSaveFile)));

    client.upload(serverGame.getGameName(), tempSaveFile);
    tempSaveFile.delete();
    String[] replies = client.readReplies();
    String reply = replies[0];

    if (!reply.equalsIgnoreCase(FILE_RECEIVED)) {
      throw new NetworkException("Could not send file", replies);
    }
  }

  public Map<Tile> downloadMap(ServerGame serverGame) throws IOException {
    HttpClient client = new HttpClient(serverURL + DOWNLOAD_MAP_SCRIPT);
    File tempSaveFile = File.createTempFile(TEMP_SAVE_FILE_NAME, TEMP_SAVE_FILE_EXT);
    client.download(serverGame.getGameName(), tempSaveFile);

    BinaryCW2MapParser mapParser = new BinaryCW2MapParser();
    Map<Tile> map = mapParser.readMap(new GZIPInputStream(new FileInputStream(tempSaveFile)));
    tempSaveFile.delete();
    return map;
  }

  public String uploadGame(ServerGame serverGame, Game game) throws IOException {
    HttpClient client = new HttpClient(serverURL + UPLOAD_GAME_SCRIPT);
    File tempSaveFile = File.createTempFile(TEMP_SAVE_FILE_NAME, TEMP_SAVE_FILE_EXT);
    BinaryCW2GameParser gameParser = new BinaryCW2GameParser();
    gameParser.writeGame(game, new GZIPOutputStream(new FileOutputStream(tempSaveFile)));

    client.upload(serverGame.getGameName(), tempSaveFile);
    tempSaveFile.delete();
    String[] replies = client.readReplies();
    String checkSum = replies[0];

    return checkSum;
  }

  public Game downloadGame(ServerGame serverGame) throws IOException {
    HttpClient client = new HttpClient(serverURL + DOWNLOAD_GAME_SCRIPT);
    File tempSaveFile = File.createTempFile(TEMP_SAVE_FILE_NAME, TEMP_SAVE_FILE_EXT);
    client.download(serverGame.getGameName(), tempSaveFile);

    BinaryCW2GameParser gameParser = new BinaryCW2GameParser();
    Game game = gameParser.readGame(new GZIPInputStream(new FileInputStream(tempSaveFile)));
    tempSaveFile.delete();
    return game;
  }

  public String[] getChatLog(String serverGameName) throws IOException {
    return sendCommand(GET_CHAT_LOG, serverGameName);
  }

  public String[] getSysLog(String serverGameName) throws IOException {
    return sendCommand(GET_SYS_LOG, serverGameName);
  }

  public void sendChatMessage(ServerGame serverGame, User user, String chatMessage) throws IOException, NetworkException {
    String[] replies = sendCommand(SEND_CHAT, serverGame.getGameName(), user.getName(), chatMessage);
    String reply = replies[0];

    if (!reply.equals(MESSAGE_RECIEVED)) {
      throw new NetworkException("Could not send chat message");
    }
  }

  protected String[] sendCommand(String command, String... parameters) throws IOException {
    HttpClient client = new HttpClient(serverURL + MAIN_SCRIPT);
    client.send(command, parameters);
    return client.readReplies();
  }
}
