package com.customwars.client.action.network;

import com.customwars.client.network.MessageSender;
import com.customwars.client.ui.GUI;
import org.newdawn.slick.thingle.Widget;

/**
 * This code can be used in a thread.
 * It's main purpose is to ask the server: 'Is the given server game name already used'.
 * if the server game name is already used show a dialog and focus the game name field.
 * if the server game name is available nothing happens
 *
 * sending a network message can potentially take a long time. Since slick is single threaded normally
 * the game locks up until the reply from the server is received. Using a thread prevents that.
 */
public class CheckGameNameAlreadyUsed implements Runnable {
  private final MessageSender messageSender;
  private final Widget gameTxtField;

  public CheckGameNameAlreadyUsed(MessageSender messageSender, Widget gameTxtField) {
    this.messageSender = messageSender;
    this.gameTxtField = gameTxtField;
  }

  public void run() {
    String gameName = gameTxtField.getText();
    boolean gameNameAvailable = messageSender.isGameNameAvailable(gameName);

    if (!gameNameAvailable) {
      GUI.showErrDialog("The game name " + gameName + " is already used, please choose another name", "Duplicate game name");
      gameTxtField.focus();
    }
  }
}
