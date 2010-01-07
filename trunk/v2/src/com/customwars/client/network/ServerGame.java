package com.customwars.client.network;

/**
 * A Server game
 */
public class ServerGame {
  private final String gameName;
  private final String masterPass;
  private final String mapName;
  private final int numOfArmiesOnMap;
  private final String comment;

  public ServerGame(String gameName) {
    this(gameName, "");
  }

  public ServerGame(String gameName, String masterPass) {
    this(gameName, masterPass, "", 0, "");
  }

  public ServerGame(String gameName, String masterPass, String mapName, int numOfArmiesOnMap, String comment) {
    this.gameName = gameName.replaceAll("\n", " ");
    this.masterPass = masterPass.replaceAll("\n", " ");
    this.mapName = mapName.replaceAll("\n", " ");
    this.numOfArmiesOnMap = numOfArmiesOnMap;
    this.comment = comment.replaceAll("\n", " ");
  }

  public String getGameName() {
    return gameName;
  }

  public String getMasterPass() {
    return masterPass;
  }

  public int getNumOfArmiesOnMap() {
    return numOfArmiesOnMap;
  }

  public String getMapName() {
    return mapName;
  }

  public String getComment() {
    return comment;
  }
}
