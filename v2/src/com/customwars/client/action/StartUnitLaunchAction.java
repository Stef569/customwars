package com.customwars.client.action;

import com.customwars.client.controller.CursorController;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Location;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

/**
 * Allows a plane on a carrier to be launched.
 * This action does not actually perform the launch but instead allows the user to choose
 * a launch destination tile within the move zone. The plane is put on to the carrier location.
 * <p/>
 * There are 2 locations mentioned:
 * transport.getLocation() -> The location of the transport before it moved.
 * move destination -> The destination of the move, where the plane is launched from.
 */
public class StartUnitLaunchAction extends DirectAction {
  private static final Logger logger = Logger.getLogger(StartUnitLaunchAction.class);
  private InGameContext inGameContext;
  private CursorController cursorControl;
  private MapRenderer mapRenderer;
  private final Unit carrier;
  private final Unit unitToBeLaunched;
  private Game game;

  public StartUnitLaunchAction(Unit carrier, Unit unitToBeLaunched) {
    super("Start Launch");
    this.carrier = carrier;
    this.unitToBeLaunched = unitToBeLaunched;
  }

  protected void init(InGameContext inGameContext) {
    this.inGameContext = inGameContext;
    this.game = inGameContext.getObj(Game.class);
    this.cursorControl = inGameContext.getObj(CursorController.class);
    this.mapRenderer = inGameContext.getObj(MapRenderer.class);
  }

  protected void invokeAction() {
    logger.debug("Preparing to launch from: " + carrier.getLocationString());
    mapRenderer.removeZones();

    // Put the plane on the carrier location in the map.
    Location carrierLocation = carrier.getLocation();
    carrier.remove(unitToBeLaunched);
    carrierLocation.add(unitToBeLaunched);

    unitToBeLaunched.setActive(true);
    game.setActiveUnit(unitToBeLaunched);
    game.getMap().buildMovementZone(unitToBeLaunched);
    mapRenderer.showMoveZone();

    inGameContext.setLaunchingUnit(true);
  }

  public void undo() {
    Location carrierLocation = carrier.getLocation();
    carrierLocation.remove(unitToBeLaunched);
    carrier.add(unitToBeLaunched);
    carrierLocation.add(carrier);
    game.setActiveUnit(carrier);
    cursorControl.moveCursor(carrier.getLocation());
    new ClearInGameStateAction().invoke(inGameContext);
  }
}
