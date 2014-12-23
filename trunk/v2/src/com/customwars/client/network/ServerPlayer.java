package com.customwars.client.network;

import java.awt.Color;

public class ServerPlayer {
  private final int slot;
  private final String COName;
  private final Color color;
  private final int team;
  private final String controller;

  public ServerPlayer(int slot, String COName, Color color, int team, String controller) {
    this.slot = slot;
    this.COName = COName;
    this.color = color;
    this.team = team;
    this.controller = controller;
  }

  public int getSlot() {
    return slot;
  }

  public String getCOName() {
    return COName;
  }

  public Color getColor() {
    return color;
  }

  public int getTeam() {
    return team;
  }

  public String getController() {
    return controller;
  }

  public boolean isAIController() {
    return controller != null && controller.equals("AI");
  }
}
