package com.customwars.client.controller.multiplayer;

import com.customwars.client.App;
import com.customwars.client.model.game.Game;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.NetworkManager;
import com.customwars.client.network.NetworkManagerSingleton;
import com.customwars.client.network.ServerGameInfo;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

import java.util.Arrays;

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
      snailGame = networkManager.startServerGame(serverGameName, userName, userPassword);
      snailGame.startGame();
    } else {
      snailGame = networkManager.startTurn(serverGameName, userName, userPassword);
    }

    stateSession.game = snailGame;
    stateChanger.changeTo("IN_GAME");
  }

  public void addChatMessage(final String chatMessage) {
    page.getWidget("txt_chat").setText("");

    App.execute(new Runnable() {
      public void run() {
        addChatMsg(chatMessage);
      }
    });
  }

  private void addChatMsg(String chatMessage) {
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

    page.getWidget("game").setText(serverGameName);
    page.getWidget("login").setText(userName);
    page.getWidget("days").setText("Day " + gameInfo.getDay() + "");
    page.getWidget("players").setText(Arrays.toString(gameInfo.getUserNames()));
    Widget sysLogList = page.getWidget("tab").getChild("game_log").getChild(0);
    Widget chatLogList = page.getWidget("tab").getChild("chat_log").getChild(0);

    sysLogList.removeChildren();
    for (String line : sysLog) {
      Widget listItem = page.createWidget("item");
      listItem.setText(line);
      sysLogList.add(listItem);
    }

    chatLogList.removeChildren();
    for (String line : chatLog) {
      Widget listItem = page.createWidget("item");
      listItem.setText(line);
      chatLogList.add(listItem);
    }
  }

  public void back() {
    stateChanger.changeToPrevious();
  }
}
