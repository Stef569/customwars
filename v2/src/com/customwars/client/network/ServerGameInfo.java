package com.customwars.client.network;

import java.util.Arrays;

public class ServerGameInfo {
  private final int day;
  private final int turn;
  private final int numPlayers;
  private final String[] userNames;

  public ServerGameInfo(int day, int turn, int numPlayers, String[] userNames) {
    this.day = day;
    this.turn = turn;
    this.numPlayers = numPlayers;
    this.userNames = userNames;
  }

  public int getDay() {
    return day;
  }

  public int getTurn() {
    return turn;
  }

  public int getNumPlayers() {
    return numPlayers;
  }

  public String[] getUserNames() {
    return userNames;
  }

  public boolean isFirstTurn() {
    return turn == 1 && day == 1;
  }

  @Override
  public String toString() {
    return "ServerGameInfo{" +
      "day=" + day +
      ", turn=" + turn +
      ", numPlayers=" + numPlayers +
      ", userNames=" + (userNames == null ? null : Arrays.asList(userNames)) +
      '}';
  }
}
