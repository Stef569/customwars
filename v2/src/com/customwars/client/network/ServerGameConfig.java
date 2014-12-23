package com.customwars.client.network;

import java.util.ArrayList;
import java.util.List;

public class ServerGameConfig {
  private List<ServerPlayer> players;

    public ServerGameConfig(List<ServerPlayer> players) {
      this.players = players;
  }
  
  public ServerGameConfig() {
    this(new ArrayList<ServerPlayer>());
  }

  public List<ServerPlayer> getPlayers() {
    return players;
  }

  public void addPlayer(ServerPlayer player) {
    players.add(player);
  }
}
