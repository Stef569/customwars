package com.customwars.client.action.network;

import com.customwars.client.network.NetworkException;
import com.customwars.client.network.NetworkManager;
import com.customwars.client.network.ServerGameInfo;
import com.customwars.client.tools.ThingleUtil;
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
        selectFirstAvailableSide(info);
        cboSide.setBoolean("enabled", true);
      }
      if (hasUserNameComboBox) {
        fillUserNameCBO(info);
        selectFirstNonEmptyUserName(info);
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

  private void selectFirstNonEmptyUserName(ServerGameInfo info) {
    for (String userName : info.getUserNames()) {
      // If the current username is present in the list select it
      if (cboUserName.getText().equals(userName)) {
        ThingleUtil.selectChild(cboSide, userName);
        return;
      }
    }

    for (String userName : info.getUserNames()) {
      if (!userName.equalsIgnoreCase("empty")) {
        ThingleUtil.selectChild(cboUserName, userName);
        break;
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
        choice.setBoolean("enabled", false);
      } else {
        choice.setBoolean("enabled", true);
      }
    }
  }

  private void selectFirstAvailableSide(ServerGameInfo info) {
    String[] userNames = info.getUserNames();
    for (int userNameIndex = 0; userNameIndex < userNames.length; userNameIndex++) {
      int slot = userNameIndex + 1;

      // If the current slot number is present in the list select it
      if (cboSide.getText().equals(slot + "")) {
        ThingleUtil.selectChild(cboSide, slot + "");
        return;
      }
    }

    for (int userNameIndex = 0; userNameIndex < userNames.length; userNameIndex++) {
      String userName = userNames[userNameIndex];
      int slot = userNameIndex + 1;

      // Select the first non empty slot
      if (!userName.equalsIgnoreCase("empty")) {
        ThingleUtil.selectChild(cboSide, slot + "");
        break;
      }
    }
  }

  private Widget createCboItem(String userName) {
    Widget cboChoice = page.createWidget("choice");
    cboChoice.setText(userName);
    return cboChoice;
  }
}
