package com.customwars.client.action.network;

import com.customwars.client.network.MessageSender;
import com.customwars.client.network.NetworkException;
import com.customwars.client.network.ServerGameInfo;
import com.customwars.client.tools.ThingleUtil;
import com.customwars.client.ui.GUI;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * This code can be used in a thread.
 * It's main purpose is to ask the server: 'does the given server game exists'
 * <p/>
 * if there is a slot widget
 * When the server game exists the slot cbo widget is updated so it contains the free slots
 * When the server game does not exists the slot cbo widget is disabled and an error message is shown
 * <p/>
 * if there is a user name widget
 * When the server game exists the user name cbo widget is updated so it contains the available users
 * When the server game does not exists user name cbo widget is disabled and an error message is shown
 * <p/>
 * sending a network message can potentially take a long time. Since slick is single threaded normally
 * the game locks up until the reply from the server is received. Using a thread prevents that.
 */
public class DetermineFreeSlots implements Runnable {
  private final MessageSender messageSender;
  private final Page page;
  private final Widget gameTxtField;
  private final Widget cboSlot;
  private final Widget cboUserName;

  public DetermineFreeSlots(MessageSender messageSender, Widget gameTxtField, Page page) {
    this.messageSender = messageSender;
    this.gameTxtField = gameTxtField;
    this.page = page;
    this.cboSlot = page.getWidget("slot");
    this.cboUserName = page.getWidget("user_name");
  }

  public void run() {
    String gameName = gameTxtField.getText();
    boolean hasSlotComboBox = cboSlot != null && cboSlot.getWidgetClass().equals("combobox");
    boolean hasUserNameComboBox = cboUserName != null && cboUserName.getWidgetClass().equals("combobox");

    try {
      ServerGameInfo serverGameInfo = messageSender.getGameInfo(gameName);

      if (hasSlotComboBox) {
        fillSlotCBO(serverGameInfo);
        selectFirstAvailableSlot(serverGameInfo);
        cboSlot.setBoolean("enabled", true);
      }
      if (hasUserNameComboBox) {
        fillUserNameCBO(serverGameInfo);
        selectFirstNonEmptyUserName(serverGameInfo);
        cboUserName.setBoolean("enabled", true);
      }
    } catch (NetworkException e) {
      if (hasSlotComboBox) cboSlot.setBoolean("enabled", false);
      if (hasUserNameComboBox) cboUserName.setBoolean("enabled", false);
      GUI.showExceptionDialog("Wrong game name", e);
    }
  }

  /**
   * Add each user that has joined the game to the username combo box widget
   *
   * @param serverGameInfo The Game info object containing the user names
   */
  private void fillUserNameCBO(ServerGameInfo serverGameInfo) {
    cboUserName.removeChildren();

    for (String userName : serverGameInfo.getUserNames()) {
      int slotNr = serverGameInfo.getSlotNrForUser(userName);

      if (!serverGameInfo.isFreeSlot(slotNr)) {
        ThingleUtil.addChoice(page, cboUserName, userName);
      }
    }
  }

  private void selectFirstNonEmptyUserName(ServerGameInfo serverGameInfo) {
    for (String userName : serverGameInfo.getUserNames()) {
      // If the current username is present in the list select it
      if (cboUserName.getText().equals(userName)) {
        ThingleUtil.selectChild(cboUserName, userName);
        return;
      }
    }

    for (String userName : serverGameInfo.getUserNames()) {
      int slotNr = serverGameInfo.getSlotNrForUser(userName);

      if (!serverGameInfo.isFreeSlot(slotNr)) {
        ThingleUtil.selectChild(cboUserName, userName);
        return;
      }
    }
  }

  /**
   * Fill the slot combobox with slot numbers. When the slot is free enable the choice.
   * If the slot is already taken by a user the choice is disabled.
   *
   * @param serverGameInfo The game info object containing the free slots
   */
  private void fillSlotCBO(ServerGameInfo serverGameInfo) {
    cboSlot.removeChildren();

    for (int freeSlotNr : serverGameInfo.getFreeSlots()) {
      Widget choice = ThingleUtil.addChoice(page, cboSlot, freeSlotNr + "");

      if (serverGameInfo.isFreeSlot(freeSlotNr)) {
        choice.setBoolean("enabled", true);
      } else {
        choice.setBoolean("enabled", false);
      }
    }
  }

  private void selectFirstAvailableSlot(ServerGameInfo serverGameInfo) {
    for (String userName : serverGameInfo.getUserNames()) {
      String slotNr = serverGameInfo.getSlotNrForUser(userName) + "";

      // If the current slot number is present in the list select it
      if (cboSlot.getText().equals(slotNr)) {
        ThingleUtil.selectChild(cboSlot, slotNr);
        return;
      }
    }

    for (String userName : serverGameInfo.getUserNames()) {
      int slotNr = serverGameInfo.getSlotNrForUser(userName);

      // Select the first empty slot
      if (serverGameInfo.isFreeSlot(slotNr)) {
        ThingleUtil.selectChild(cboSlot, slotNr + "");
        return;
      }
    }
  }
}