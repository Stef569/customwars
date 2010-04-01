package com.customwars.client.network;

import com.customwars.client.App;
import com.customwars.client.network.http.battleserver.BattleServerMessageSender;

/**
 * Creates MessageSender instances
 */
public class MessageSenderFactory {
  private static final MessageSenderFactory instance = new MessageSenderFactory();
  private static final MessageSender BattleServerMessageSender = new BattleServerMessageSender(App.get("user.snailserver_url"));

  public static MessageSenderFactory getInstance() {
    return instance;
  }

  public MessageSender createMessageSender() {
    switch (App.getGameMode()) {
      case NETWORK_SNAIL_GAME:
        return BattleServerMessageSender;
      default:
        return null;
    }
  }
}
