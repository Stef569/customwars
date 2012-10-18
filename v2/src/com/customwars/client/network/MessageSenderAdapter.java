package com.customwars.client.network;

import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;

/**
 * An abstract adapter class for sending game message.
 * The methods in this class are empty. This class exists as
 * convenience for creating objects that don't need to implement all methods.
 */
public class MessageSenderAdapter implements MessageSender {
  private static final String[] EMPTY = new String[0];

  @Override
  public void connect() throws NetworkException {
  }

  @Override
  public void createNewServerGame(String gameName, String gamePass, Map map, String userName, String userPassword, String comment) throws NetworkException {
  }

  @Override
  public void joinServerGame(String gameName, String gamePass, String userName, String userPassword, int side) throws NetworkException {
  }

  @Override
  public void loginToServerGame(String gameName, String userName, String userPassword) throws NetworkException {
  }

  @Override
  public Game startServerGame() throws NetworkException {
    return null;
  }

  @Override
  public void endTurn(Game game) throws NetworkException {
  }

  @Override
  public Game startTurn() throws NetworkException {
    return null;
  }

  @Override
  public boolean isGameNameAvailable(String gameName) {
    return false;
  }

  @Override
  public ServerGameInfo getGameInfo(String gameName) throws NetworkException {
    return null;
  }

  @Override
  public String[] getChatLog() throws NetworkException {
    return EMPTY;
  }

  @Override
  public String[] getSysLog() throws NetworkException {
    return EMPTY;
  }

  @Override
  public void endGame() throws NetworkException {
  }

  @Override
  public void sendChatMessage(String chatMessage) throws NetworkException {
  }

  @Override
  public void drop(Unit transport, Unit unit, Location dropLocation) throws NetworkException {
  }

  @Override
  public void teleport(Location from, Location to) throws NetworkException {
  }

  @Override
  public void capture(Unit unit, City city) throws NetworkException {
  }

  @Override
  public void load(Unit unit, Unit transport) throws NetworkException {
  }

  @Override
  public void supply(Unit apc) throws NetworkException {
  }

  @Override
  public void join(Unit unit, Unit target) throws NetworkException {
  }

  @Override
  public void attack(Unit attacker, City city) throws NetworkException {
  }

  @Override
  public void attack(Unit attacker, Unit defender) throws NetworkException {
  }

  @Override
  public void launchRocket(Unit unit, City city, Location rocketDestination, int effectRange) throws NetworkException {
  }

  @Override
  public void transformTerrain(Location location, Terrain transformToTerrain) throws NetworkException {
  }

  @Override
  public void flare(Location flareCenter, int flareRange) throws NetworkException {
  }

  @Override
  public void constructCity(Unit unit, String cityID, Location constructOn) throws NetworkException {
  }

  @Override
  public void dive(Unit unit) throws NetworkException {
  }

  @Override
  public void surface(Unit unit) throws NetworkException {
  }

  @Override
  public void sendWait(Unit unit) throws NetworkException {
  }

  @Override
  public void buildUnit(Unit unit, Location location, Player unitOwner) throws NetworkException {
  }

  @Override
  public void destroyPlayer(Player player) throws NetworkException {
  }

  @Override
  public void loadCO(Unit unit) throws NetworkException {
  }

  @Override
  public void coPower() throws NetworkException {
  }

  @Override
  public void coSuperPower() throws NetworkException {
  }

  @Override
  public void produceUnit(Unit producer, String unitToProduce, Player owner) throws NetworkException {
  }

  @Override
  public void deleteUnit(Location location) throws NetworkException {
  }
}
