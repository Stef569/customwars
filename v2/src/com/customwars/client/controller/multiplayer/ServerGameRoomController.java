package com.customwars.client.controller.multiplayer;

import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.NetworkManager;
import com.customwars.client.network.NetworkManagerSingleton;
import com.customwars.client.network.ServerGameInfo;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.tools.ThingleUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.spi.ThingleColor;

/**
 * Handle input when in the server game room
 */
public class ServerGameRoomController {
  private static final Logger logger = Logger.getLogger(ServerGameRoomController.class);
  private final StateChanger stateChanger;
  private final StateSession stateSession;
  private final NetworkManager networkManager;
  private Page page;
  private String serverGameName;
  private String userName;
  private String userPassword;

  public ServerGameRoomController(StateChanger stateChanger, StateSession stateSession) {
    this.stateChanger = stateChanger;
    this.stateSession = stateSession;
    networkManager = NetworkManagerSingleton.getInstance();
  }

  public void init(Page page) {
    this.page = page;
  }

  public void enter() {
    serverGameName = stateSession.serverGameName;
    userName = stateSession.user.getName();
    userPassword = stateSession.user.getPassword();
    refreshInfo();
  }

  public void play() {
    try {
      startOrContinueGame();
    } catch (NetworkException e) {
      logger.warn("Could not start game " + serverGameName, e);
      GUI.showExceptionDialog("Could not start game " + serverGameName, e);
    }
  }

  private void startOrContinueGame() throws NetworkException {
    ServerGameInfo gameInfo = networkManager.getGameInfo(serverGameName);

    Game snailGame;
    if (gameInfo.isFirstTurn()) {
      checkForEmptySlots(gameInfo);
      snailGame = networkManager.startServerGame(serverGameName, userName, userPassword);
      snailGame.startGame();
    } else {
      snailGame = networkManager.startTurn(serverGameName, userName, userPassword);
    }

    stateSession.game = snailGame;
    stateChanger.changeTo("IN_GAME");
  }

  private void checkForEmptySlots(ServerGameInfo gameInfo) throws NetworkException {
    // todo Really the server should check for empty slots before starting, not the client
    if (gameInfo.getFreeSlots().length > 0) {
      throw new NetworkException("There are empty slots, wait for other players to join your game");
    }
  }

  public void sendChatMessage() {
    final Widget txtChat = page.getWidget("txt_chat");
    final String chatMessage = txtChat.getText();

    App.execute(new Runnable() {
      public void run() {
        if (StringUtil.hasContent(chatMessage)) {
          txtChat.setText("");
          sendChatMsg(chatMessage);
        }
      }
    });
  }

  private void sendChatMsg(String chatMessage) {
    Widget addToChatButton = page.getWidget("btn_add_chat_text");
    addToChatButton.setBoolean("enabled", false);

    try {
      networkManager.sendChatMessage(serverGameName, userName, chatMessage);
      refreshInfo();
      addToChatButton.setBoolean("enabled", true);
    } catch (NetworkException ex) {
      GUI.showExceptionDialog("Could not send chat message", ex);
      addToChatButton.setBoolean("enabled", true);
    }
  }

  public void refreshInfo() {
    App.execute(new Runnable() {
      public void run() {
        refresh();
      }
    });
  }

  private void refresh() {
    ServerGameInfo gameInfo;
    String[] chatLog, sysLog;

    try {
      gameInfo = networkManager.getGameInfo(serverGameName);
      chatLog = networkManager.getChatLog(serverGameName);
      sysLog = networkManager.getSysLog(serverGameName);
    } catch (NetworkException ex) {
      GUI.showExceptionDialog("Could not retrieve information for game " + serverGameName, ex);
      return;
    }

    // Prevents NPE by acquire the page lock, This prevents other threads
    // from rendering the page while we are editing it.
    synchronized (page) {
      page.getWidget("game").setText(serverGameName);
      page.getWidget("days").setText(gameInfo.getDay() + "");

      Widget playersList = page.getWidget("lst_players");
      Widget sysLogList = page.getWidget("tab").getChild("game_log").getChild(0);
      Widget chatLogList = page.getWidget("tab").getChild("chat_log").getChild(0);

      ThingleUtil.fillList(page, sysLogList, true, sysLog);
      ThingleUtil.fillList(page, chatLogList, true, chatLog);
      ThingleUtil.fillList(page, playersList, false, gameInfo.getUserNames());

      highLightActiveUser(gameInfo, playersList);
      highLightCurrentUser(gameInfo, playersList);
      enablePlayButton(gameInfo);
    }
  }

  private void highLightActiveUser(ServerGameInfo gameInfo, Widget playersList) {
    int currentUserIndex = gameInfo.getUserIdFor(userName);
    playersList.getChild(currentUserIndex).setBoolean("selected", true);
  }

  private void highLightCurrentUser(ServerGameInfo gameInfo, Widget playersList) {
    String activeUser = gameInfo.getActiveUser();
    ThingleColor darkerColor = Thingle.createColor(107, 107, 107);

    for (String userName : gameInfo.getUserNames()) {
      if (!userName.equals(activeUser)) {
        int userIndex = gameInfo.getUserIdFor(userName);
        playersList.getChild(userIndex).setColor("foreground", darkerColor);
      }
    }
  }

  /**
   * The play button is only enabled when the current user is
   * active(can perform his turn)
   */
  private void enablePlayButton(ServerGameInfo gameInfo) {
    String activeUser = gameInfo.getActiveUser();
    Widget playButton = page.getWidget("btn_play");

    if (userName.equals(activeUser)) {
      playButton.setBoolean("enabled", true);
    } else {
      playButton.setBoolean("enabled", false);
    }
  }

  public void back() {
    stateChanger.changeToPrevious();
  }
}
