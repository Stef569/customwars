package com.customwars.client.network;

/**
 * Creates NetworkManager instances
 */
public class NetworkManagerSingleton {
  private static final NetworkManager cw1NetworkManager = new Cw1NetworkManager();

  public static NetworkManager getInstance() {
    return cw1NetworkManager;
  }
}
