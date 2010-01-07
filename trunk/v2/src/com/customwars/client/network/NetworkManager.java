package com.customwars.client.network;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;

/**
 * Defines objects that can send network messages
 */
public interface NetworkManager {
  void createNewServerGame(String gameName, String gamePass, Map<Tile> map,
                           String userName, String userPassword,
                           int side, String comment) throws NetworkException;

  void joinServerGame(String gameName, String gamePass, String userName, String userPassword, int side) throws NetworkException;

  void loginToServerGame(String gameName, String userName, String userPassword) throws NetworkException;

  Game startServerGame(String serverGameName, String userName, String userPassword) throws NetworkException;

  void endTurn(Game game, String serverGameName, String userName, String userPassword) throws NetworkException;

  Game startTurn(String serverGameName, String userName, String userPassword) throws NetworkException;

  boolean isGameNameAvailable(String gameName);

  ServerGameInfo getGameInfo(String gameName) throws NetworkException;

  String[] getChatLog(String serverGameName) throws NetworkException;

  String[] getSysLog(String serverGameName) throws NetworkException;

  void destroyPlayer(Game game, Player player, String serverGameName, String userName, String userPassword) throws NetworkException;

  void endGame(String serverGameName, String userName, String userPassword) throws NetworkException;

  void sendChatMessage(String serverGameName, String userName, String chatMessage) throws NetworkException;
}
