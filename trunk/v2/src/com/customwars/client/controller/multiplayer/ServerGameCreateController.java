package com.customwars.client.controller.multiplayer;

import com.customwars.client.App;
import com.customwars.client.action.network.CheckGameNameAlreadyUsed;
import com.customwars.client.model.map.Map;
import com.customwars.client.network.MessageSender;
import com.customwars.client.network.MessageSenderFactory;
import com.customwars.client.network.NetworkException;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * Handle user input when creating a server game
 */
public class ServerGameCreateController {
  private final StateChanger stateChanger;
  private final StateSession stateSession;
  private MessageSender messageSender;
  private Page page;
  private Map map;

  public ServerGameCreateController(StateChanger stateChanger, StateSession stateSession) {
    this.stateChanger = stateChanger;
    this.stateSession = stateSession;
  }

  public void init(Page page) {
    this.page = page;
  }

  public void enter() {
    this.messageSender = MessageSenderFactory.getInstance().createMessageSender();
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

  public void gameNameFocusLost(Widget gameTxtField) {
    if (StringUtil.hasContent(gameTxtField.getText())) {
      App.execute(new CheckGameNameAlreadyUsed(messageSender, gameTxtField));
    }
  }

  public void gotoMapSelectState() {
    stateChanger.changeTo("MAP_SELECT");
  }

  public void mapSelected() {
    this.map = stateSession.map;
    int numPlayers = map.getNumPlayers();
    page.getWidget("selected_map").setText(map.getMapName() + "( " + numPlayers + "P)");
  }

  public void createServerGame() {
    if (stateSession.map == null) {
      GUI.showErrDialog("Please select a map before creating the server game", "No map selected");
      return;
    }

    String gameName = page.getWidget("server_game_name").getText();
    String gamePass = page.getWidget("server_game_password").getText();
    String mapName = map.getMapName();
    int playerCount = map.getNumPlayers();
    String userName = page.getWidget("user_name").getText();
    String userPassword = page.getWidget("user_password").getText();
    String comment = page.getWidget("server_game_info").getText();

    if (playerCount <= 1) {
      GUI.showErrDialog("Invalid player count " + playerCount + "  for map " + map.getMapName() + " please choose another map", "Invalid player count");
      return;
    } else if (!StringUtil.hasContent(gameName)) {
      GUI.showErrDialog("Please specify a name for your game", "Game name is required");
      return;
    } else if (!StringUtil.hasContent(mapName)) {
      GUI.showErrDialog("Please select a map for your game", "Map name is required");
      return;
    } else if (!StringUtil.hasContent(userName)) {
      GUI.showErrDialog("The user name is a required value", "User name is required");
      return;
    } else if (!StringUtil.hasContent(userPassword)) {
      GUI.showErrDialog("The user password is a required value", "User password is required");
      return;
    }

    try {
      messageSender.createNewServerGame(gameName, gamePass, map, userName, userPassword, comment);
      GUI.showdialog("Game " + gameName + " created", "Success");
      back();
    } catch (NetworkException e) {
      GUI.showExceptionDialog("Could not create new server Game", e);
    }
  }

  public void back() {
    stateSession.clear();
    clearFields();
    stateChanger.changeToPrevious();
  }

  private void clearFields() {
    page.getWidget("server_game_name").setText("");
    page.getWidget("server_game_password").setText("");
    page.getWidget("selected_map").setText("No map selected");
    page.getWidget("server_game_info").setText("");
  }
}
