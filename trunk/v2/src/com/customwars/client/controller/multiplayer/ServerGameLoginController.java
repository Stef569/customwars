package com.customwars.client.controller.multiplayer;

import com.customwars.client.App;
import com.customwars.client.action.network.DetermineFreeSlots;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.MessageSenderFactory;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.User;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.apache.log4j.Logger;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * Handle input when login into a server game
 */
public class ServerGameLoginController {
  private static final Logger logger = Logger.getLogger(ServerGameLoginController.class);
  private final StateChanger stateChanger;
  private MessageSender messageSender;
  private final StateSession stateSession;
  private Page page;

  public ServerGameLoginController(StateChanger stateChanger, StateSession stateSession) {
    this.stateChanger = stateChanger;
    this.stateSession = stateSession;
  }

  public void init(Page page) {
    this.page = page;
  }

  public void enter() {
    messageSender = MessageSenderFactory.getInstance().createMessageSender();
    checkServerConnection();

    // Only set the default user name + password to the user fields
    // When they are empty, to prevent overwriting previous input
    Widget txtUserName = page.getWidget("user_name");
    if (!StringUtil.hasContent(txtUserName.getText())) {
      txtUserName.setText(App.get("user.name"));
    }

    Widget txtUserPassword = page.getWidget("user_password");
    if (!StringUtil.hasContent(txtUserPassword.getText())) {
      txtUserPassword.setText(App.get("user.password"));
    }
  }

  private void checkServerConnection() {
    try {
      messageSender.connect();
    } catch (NetworkException e) {
      GUI.showExceptionDialog("Cannot connect to server", e);
    }
  }

  public void fetchUserNames(Widget gameTxtField) {
    if (StringUtil.hasContent(gameTxtField.getText())) {
      new DetermineFreeSlots(messageSender, gameTxtField, page).run();
    }
  }

  public void loginIntoServerGame() {
    String gameName = page.getWidget("server_game_name").getText();
    String userName = page.getWidget("user_name").getText();
    String userPassword = page.getWidget("user_password").getText();

    if (!StringUtil.hasContent(userName)) {
      GUI.showErrDialog("The user name is required", "User name is required");
      return;
    } else if (!StringUtil.hasContent(userPassword)) {
      GUI.showErrDialog("The user password is required", "User password is required");
      return;
    } else if (!StringUtil.hasContent(gameName)) {
      GUI.showErrDialog("Please specify a name for your game", "Game name is required");
      return;
    }

    try {
      messageSender.loginToServerGame(gameName, userName, userPassword);
      stateSession.serverGameName = gameName;
      stateSession.user = new User(userName, userPassword);
      stateChanger.changeTo("SERVER_GAME_ROOM");
    } catch (NetworkException e) {
      logger.warn("Could not send login", e);
      if (GUI.askToResend(e) == GUI.YES_OPTION) {
        loginIntoServerGame();
      }
    }
  }

  public void back() {
    stateChanger.changeToPrevious();
  }
}
