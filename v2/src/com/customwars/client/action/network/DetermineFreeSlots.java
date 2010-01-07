package com.customwars.client.action.network;

import com.customwars.client.network.NetworkException;
import com.customwars.client.network.NetworkManager;
import com.customwars.client.network.ServerGameInfo;
import com.customwars.client.ui.GUI;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * This code can be used in a thread.
 * It's main purpose is to ask the server: 'does the given server game exists'
 *
 * if there is a side widget
 * When the server game exists the side cbo widget is updated so it contains the free slots
 * When the server game does not exists the side cbo widget is disabled and an error message is shown
 *
 * if there is a user name widget
 * When the server game exists the user name cbo widget is updated so it contains the available users
 * When the server game does not exists user name cbo widget is disabled and an error message is shown
 *
 * sending a network message can potentially take a long time. Since slick is single threaded normally
 * the game locks up until the reply from the server is received. Using a thread prevents that.
 */
public class DetermineFreeSlots implements Runnable {
  private final NetworkManager networkManager;
  private final Page page;
  private final Widget gameTxtField;
  private final Widget cboSide;
  private final Widget cboUserName;

  public DetermineFreeSlots(NetworkManager networkManager, Widget gameTxtField, Page page) {
    this.networkManager = networkManager;
    this.gameTxtField = gameTxtField;
    this.page = page;
    this.cboSide = page.getWidget("side");
    this.cboUserName = page.getWidget("user_name");
  }

  public void run() {
    String gameName = gameTxtField.getText();
    boolean hasSideComboBox = cboSide != null && cboSide.getWidgetClass().equals("combobox");
    boolean hasUserNameComboBox = cboUserName != null && cboUserName.getWidgetClass().equals("combobox");

    try {
      ServerGameInfo info = networkManager.getGameInfo(gameName);

      if (hasSideComboBox) {
        fillSideCBO(info);
        cboSide.setBoolean("enabled", true);
      }
      if (hasUserNameComboBox) {
        fillUserNameCBO(info);
        cboUserName.setBoolean("enabled", true);
      }
    } catch (NetworkException e) {
      if (hasSideComboBox) cboSide.setBoolean("enabled", false);
      if (hasUserNameComboBox) cboUserName.setBoolean("enabled", false);
      GUI.showExceptionDialog("Wrong game name", e);
    }
  }

  private void fillUserNameCBO(ServerGameInfo info) {
    cboUserName.removeChildren();

    for (String userName : info.getUserNames()) {
      if (!userName.equalsIgnoreCase("empty")) {
        Widget choice = createCboItem(userName);
        cboUserName.add(choice);
      }
    }
  }

  private void fillSideCBO(ServerGameInfo info) {
    cboSide.removeChildren();

    String[] userNames = info.getUserNames();
    for (int userNameIndex = 0; userNameIndex < userNames.length; userNameIndex++) {
      String userName = userNames[userNameIndex];
      int slot = userNameIndex + 1;
      Widget choice = createCboItem(slot + "");
      cboSide.add(choice);

      if (userName.equalsIgnoreCase("empty")) {
        choice.setBoolean("enabled", true);
      } else {
        choice.setBoolean("enabled", false);
      }
    }
  }

  private Widget createCboItem(String userName) {
    Widget cboChoice = page.createWidget("choice");
    cboChoice.setText(userName);
    return cboChoice;
  }
}
