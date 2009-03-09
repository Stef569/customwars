package com.customwars.client.controller;

import com.customwars.client.action.ActionManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;

/**
 * Handles any input for 1 city
 * This can be surrounding tile information, clicks in a menu, Ai
 *
 * @author stefan
 */
public abstract class CityController {
  Game game;
  City city;
  ActionManager actionManager;

  public CityController(City city, Game game, ActionManager actionManager) {
    this.city = city;
    this.game = game;
    this.actionManager = actionManager;
  }

  boolean canBuyUnit(Unit unit) {
    Player activePlayer = game.getActivePlayer();

    return (activePlayer.canPurchase(unit)) &&
            activePlayer == city.getOwner() &&
            city.canBuild(unit);
  }
}
