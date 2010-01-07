package com.customwars.client.controller.multiplayer;

import com.customwars.client.App;
import com.customwars.client.action.network.DetermineFreeSlots;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.NetworkManager;
import com.customwars.client.network.NetworkManagerSingleton;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.state.StateChanger;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * Handle input in the join server state
 */
public class ServerGameJoinController {
  private final StateChanger stateChanger;
  private final NetworkManager networkManager;
  private Page page;

  public ServerGameJoinController(StateChanger stateChanger) {
    this.stateChanger = stateChanger;
    networkManager = NetworkManagerSingleton.getInstance();
  }

  public void init(Page page) {
    this.page = page;
  }

  public void enter() {
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

  public void fetchSides(Widget gameTxtField) {
    if (StringUtil.hasContent(gameTxtField.getText())) {
      new DetermineFreeSlots(networkManager, gameTxtField, page).run();
    }
  }

  public void joinServerGame() {
    String gameName = page.getWidget("server_game_name").getText();
    String gamePass = page.getWidget("server_game_password").getText();
    String userName = page.getWidget("user_name").getText();
    String userPassword = page.getWidget("user_password").getText();

    if (!StringUtil.hasContent(userName)) {
      GUI.showErrDialog("The user name is a required value", "User name is required");
      return;
    } else if (!StringUtil.hasContent(userPassword)) {
      GUI.showErrDialog("The user password is a required value", "User password is required");
      return;
    }

    int side;
    try {
      side = Integer.parseInt(page.getWidget("side").getText());
    } catch (NumberFormatException ex) {
      GUI.showErrDialog("No side selected", "Please choose a side");
      return;
    }

    try {
      networkManager.joinServerGame(gameName, gamePass, userName, userPassword, side);
      GUI.showdialog(userName + " joined battle " + gameName, "Success");
      stateChanger.changeToPrevious();
    } catch (NetworkException e) {
      GUI.showExceptionDialog("Could not join Game", e);
    }
  }

  public void back() {
    stateChanger.changeToPrevious();
  }
}
