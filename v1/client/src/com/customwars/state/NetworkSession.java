package com.customwars.state;

import com.customwars.ai.Options;
import org.apache.log4j.Logger;

import java.io.IOException;
import java.net.MalformedURLException;

/**
 * Handles chatting to the server using plain text
 * Invalid input, commands and server error messages are wrapped in NetworkException
 * <p/>
 * The results of a command from the server is stored in this object and
 * can be retrieved by using getters.
 *
 * @author stefan
 * @since 2.0
 */
public class NetworkSession {
  private static final Logger logger = Logger.getLogger(NetworkSession.class);
  private static final int MIN_USERNAME_LENGTH = 1;
  private static final int MAX_USERNAME_LENGTH = 12;

  private NetworkingManager networkManager;
  private int day, turn;
  private String[] userNames;
  private String[] sysLog;
  private String[] chatLog;
  private int sysPos;
  private int chatPos;
  private String gameName, userName;

  public NetworkSession() {
    this.networkManager = new NetworkingManager();
    this.userNames = new String[]{"Unknown"};
    this.sysLog = new String[]{"System Log"};
    this.chatLog = new String[]{"Chat Log"};
  }

  public void loginToServerGame(String gameName, String userName, String userPassword) throws NetworkException {
    logger.debug("Attempting to log in to Server Game " + gameName + " with " + userName + "/" + userPassword);
    validateConnect();
    validateLogin(gameName, userName, userPassword);
    validUp(gameName, userName, userPassword);
    refreshInfo(gameName);
  }

  private void validateLogin(String gameName, String userName, String userPass) throws NetworkException {
    if (gameName == null) throw new NetworkException("", new NullPointerException("game name is null"));
    if (userName == null) throw new NetworkException("", new NullPointerException("user Name is null"));
    if (userPass == null) throw new NetworkException("", new NullPointerException("user password is null"));

    if (userName.length() < MIN_USERNAME_LENGTH) {
      IllegalArgumentException ex = new IllegalArgumentException(
              "user Name to short provide at least " + MIN_USERNAME_LENGTH + " characters");
      throw new NetworkException("", ex);
    }

    if (userName.length() > MAX_USERNAME_LENGTH) {
      IllegalArgumentException ex = new IllegalArgumentException(
              "user Name to long provide less then " + MAX_USERNAME_LENGTH + " characters");
      throw new NetworkException("", ex);
    }
  }

  public void joinServerGame(String gameName, String masterPass, String userName, String userPassword, int slotNum) throws NetworkException {
    String msg = gameName + "\n" + masterPass + "\n" + userName + "\n" + userPassword + "\n" + slotNum + "\n" + Options.version;
    logger.debug("Attempting to join Server Game " + gameName + " with " + msg);
    validateConnect();
    validateLogin(gameName, userName, userPassword);

    joinServerGame(msg);
    refreshInfo(gameName);
    sendCommand("join", msg);
  }

  private void joinServerGame(String msg) throws NetworkException {
    String reply = sendCommand("join", msg);

    if (!reply.equals("join successful")) {
      if (reply.equals("no")) {
        throw new NetworkException("Game does not exist");
      } else if (reply.equals("wrong password")) {
        throw new NetworkException("Incorrect Password");
      } else if (reply.equals("out of range")) {
        throw new NetworkException("Army choice out of range or invalid");
      } else if (reply.equals("slot taken")) {
        throw new NetworkException("Army choice already taken");
      } else {
        throw new NetworkException("Other problem");
      }
    }
  }

  public void createServerGame(String gameName, String masterpass, String userName, String userPassword) throws NetworkException {
    logger.debug("Attempting to create a Server Game " + gameName);
    validateConnect();

    // find an unused name
    String reply = sendCommand("qname", gameName);

    if (!reply.equals("yes")) {
      logger.debug(reply);
      if (reply.equals("no")) {
        throw new NetworkException("Game name already taken");
      }
      sendCommand("qname", gameName);
    }
  }

  public void refreshInfo() throws NetworkException {
    refreshInfo(gameName);
  }

  public void refreshInfo(String gameName) throws NetworkException {
    String reply = sendCommand("getturn", gameName);
    logger.debug("Reply =" + reply);

    if (reply.equals("no")) {
      throw new NetworkException("Reply is no");
    }

    String[] nums = reply.split("\n");
    day = Integer.parseInt(nums[0]);
    turn = Integer.parseInt(nums[1]);
    int numplay = Integer.parseInt(nums[2]);
    userNames = new String[numplay];
    System.arraycopy(nums, 3, userNames, 0, numplay);

    reply = sendCommand("getsys", gameName);
    sysLog = reply.split("\n");
    sysPos = sysLog.length - 5;
    if (sysPos < 0) sysPos = 0;

    reply = sendCommand("getchat", gameName);
    chatLog = reply.split("\n");
    chatPos = chatLog.length - 5;
    if (chatPos < 0) chatPos = 0;
  }

  private String sendCommand(String command, String extra) throws NetworkException {
    try {
      return networkManager.sendCommandToMain(command, extra);
    } catch (MalformedURLException e1) {
      throw new NetworkException("Bad URL " + Options.getServerName(), e1);
    } catch (IOException e2) {
      throw new NetworkException("Connection Problem during command " + command + " with information:\n" + extra, e2);
    }
  }

  // VALIDATION
  /**
   * Confirm that there is a valid connection to the server
   */
  private void validUp(String gameName, String userName, String userPassword) throws NetworkException {
    String reply = sendCommand("validup", gameName + "\n" + userName + "\n" + userPassword + "\n" + Options.version);

    logger.debug("validup Reply=" + reply);
    if (!reply.equals("login successful")) {
      if (reply.equals("version mismatch")) {
        throw new NetworkException("Version Mismatch, Client version=" + Options.version);
      } else {
        throw new NetworkException("Problem logging in, either the username/userPassword is incorrect or the game has ended");
      }
    }
  }

  /**
   * Try to connect to the server to see that the user's URL is correct
   */
  private void validateConnect() throws NetworkException {
    try {
      networkManager.tryToConnect();
    } catch (MalformedURLException e1) {
      throw new NetworkException("Bad URL: " + Options.getServerName(), e1);

    } catch (IOException e2) {
      throw new NetworkException("user Name is null", e2);
    }
  }

  // GETTERS
  public int getDay() {
    return day;
  }

  public int getTurn() {
    return turn;
  }

  public String[] getUserNames() {
    return userNames;
  }

  public String[] getSysLog() {
    return sysLog;
  }

  public int getSysPos() {
    return sysPos;
  }

  public String[] getChatLog() {
    return chatLog;
  }

  public int getChatPos() {
    return chatPos;
  }

  public String getGameName() {
    return gameName;
  }

  public String getUserName() {
    return userName;
  }

  public void setGameName(String gameName) {
    this.gameName = gameName;
  }

  public void setUserName(String userName) {
    this.userName = userName;
  }
}
