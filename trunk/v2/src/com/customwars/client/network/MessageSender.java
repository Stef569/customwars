package com.customwars.client.network;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;

/**
 * Sends game messages
 */
public interface MessageSender {

  void connect() throws NetworkException;

  void createNewServerGame(String gameName, String gamePass, Map map,
                           String userName, String userPassword,
                           String comment) throws NetworkException;

  void joinServerGame(String gameName, String gamePass,
                      String userName, String userPassword,
                      int side) throws NetworkException;

  void loginToServerGame(String gameName,
                         String userName, String userPassword) throws NetworkException;

  Game startServerGame() throws NetworkException;

  void endTurn(Game game) throws NetworkException;

  Game startTurn() throws NetworkException;

  boolean isGameNameAvailable(String gameName);

  ServerGameInfo getGameInfo(String gameName) throws NetworkException;

  String[] getChatLog() throws NetworkException;

  String[] getSysLog() throws NetworkException;

  void endGame() throws NetworkException;

  void sendChatMessage(String chatMessage) throws NetworkException;

  void drop(Unit transport, Unit unit, Location dropLocation) throws NetworkException;

  void teleport(Location from, Location to) throws NetworkException;

  void capture(Unit unit, City city) throws NetworkException;

  void load(Unit unit, Unit transport) throws NetworkException;

  void supply(Unit apc) throws NetworkException;

  void join(Unit unit, Unit target) throws NetworkException;

  void attack(Unit attacker, City city) throws NetworkException;

  void attack(Unit attacker, Unit defender) throws NetworkException;

  void launchRocket(Unit unit, City city, Location rocketDestination, int effectRange) throws NetworkException;

  void transformTerrain(Location location, Terrain transformToTerrain) throws NetworkException;

  void flare(Location flareCenter, int flareRange) throws NetworkException;

  void constructCity(Unit unit, int cityID, Location constructOn) throws NetworkException;

  void dive(Unit unit) throws NetworkException;

  void surface(Unit unit) throws NetworkException;

  void sendWait(Unit unit) throws NetworkException;

  void buildUnit(Unit unit, Location location, Player unitOwner) throws NetworkException;

  void destroyPlayer(Player player) throws NetworkException;

  void loadCO(Unit unit) throws NetworkException;

  void coPower() throws NetworkException;

  void coSuperPower() throws NetworkException;
}
