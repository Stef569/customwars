package com.customwars.client.network;

import com.customwars.client.App;

/**
 * Creates NetworkManager instances
 */
public class NetworkManagerSingleton {
  private static final NetworkManager cw1NetworkManager = new Cw1NetworkManager(App.get("user.snailserver_url"));

  public static NetworkManager getInstance() {
    return cw1NetworkManager;
  }
}
