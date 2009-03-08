package com.customwars.client.action;

import com.customwars.client.action.game.EndTurnAction;
import com.customwars.client.action.unit.AttackAction;
import com.customwars.client.action.unit.CaptureAction;
import com.customwars.client.action.unit.ClearInGameState;
import com.customwars.client.action.unit.DropAction;
import com.customwars.client.action.unit.LoadAction;
import com.customwars.client.action.unit.MoveAnimatedAction;
import com.customwars.client.action.unit.SelectAction;
import com.customwars.client.action.unit.ShowAttackZoneAction;
import com.customwars.client.action.unit.StartAttackAction;
import com.customwars.client.action.unit.StartDropAction;
import com.customwars.client.action.unit.SupplyAndHealAction;
import com.customwars.client.action.unit.WaitAction;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.UnitFight;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.InGameSession;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a reference to all Actions by upper case name.
 * Each action added to actions is updated in each game loop
 *
 * Actions can be grouped into an ActionBag, allowing multiple actions to work together
 *
 * @author stefan
 */
public class ActionManager {
  private static final Logger logger = Logger.getLogger(ActionManager.class);
  private Map<String, CWAction> actions;
  private MapRenderer mapRenderer;
  private HUD hud;
  private InGameSession inGameSession;
  private MoveTraverse moveTraverse;
  private ResourceManager resources;
  private Game game;
  private CWAction clearInGameState;

  private ActionManager() {
    actions = new HashMap<String, CWAction>();
  }

  public ActionManager(MapRenderer mapRenderer, InGameSession inGameSession, MoveTraverse moveTraverse, ResourceManager resources, Game game, HUD hud) {
    this();
    this.mapRenderer = mapRenderer;
    this.inGameSession = inGameSession;
    this.moveTraverse = moveTraverse;
    this.resources = resources;
    this.game = game;
    this.hud = hud;
  }

  public void update(int elapsedTime) {
    for (CWAction action : actions.values()) {
      action.update(elapsedTime);
    }
  }

  public void buildActions() {
    Args.checkForNull(mapRenderer);
    Args.checkForNull(inGameSession);
    Args.checkForNull(resources);
    Args.checkForNull(game);
    Args.checkForNull(moveTraverse);

    clearInGameState = new ClearInGameState(game, mapRenderer, inGameSession);
    buildGameActions();
    buildUnitActions();
  }

  private void buildGameActions() {
    CWAction endTurn = new EndTurnAction(game);
    ActionBag endTurnActions = new ActionBag("End Turn Actions");
    endTurnActions.addAction(endTurn);
    endTurnActions.addAction(clearInGameState);
    actions.put("END_TURN", endTurnActions);
  }

  private void buildUnitActions() {
    buildSelectActions();
    buildAnimatedActions();
  }

  private void buildSelectActions() {
    CWAction unitSelect = new SelectAction(game, mapRenderer, inGameSession);
    actions.put("SELECT_UNIT", unitSelect);

    CWAction showAttackZone = new ShowAttackZoneAction(game, mapRenderer, inGameSession);
    actions.put("ATTACK_ZONE_UNIT", showAttackZone);
  }

  private void buildAnimatedActions() {
    CWAction unitMoveAnimated = new MoveAnimatedAction(game, mapRenderer, moveTraverse, inGameSession);
    CWAction unitWait = new WaitAction(game, inGameSession);
    CWAction unitCapture = new CaptureAction(game, inGameSession);
    CWAction unitSupplyAndHeal = new SupplyAndHealAction(game, inGameSession);
    CWAction unitLoadIntoTransport = new LoadAction(game, inGameSession, mapRenderer, unitWait);
    CWAction startDropMode = new StartDropAction(game.getMap(), mapRenderer, inGameSession);
    CWAction unitDrop = new DropAction(game, inGameSession);
    CWAction startAttackMode = new StartAttackAction(game, mapRenderer, inGameSession);
    CWAction unitAttack = new AttackAction(game, inGameSession, new UnitFight(game.getMap()));

    actions.put("UNIT_MOVE_ANIMATED", unitMoveAnimated);
    actions.put("UNIT_START_DROP_MODE", startDropMode);
    actions.put("UNIT_START_ATTACK_MODE", startAttackMode);

    // Animated move actions
    ActionBag waitActions = new ActionBag("Wait Actions");
    waitActions.addAction(unitMoveAnimated);
    waitActions.addAction(unitWait);
    waitActions.addAction(clearInGameState);
    actions.put("UNIT_MOVE_WAIT", waitActions);

    ActionBag captureActions = new ActionBag("Capture Actions");
    captureActions.addAction(unitMoveAnimated);
    captureActions.addAction(unitCapture);
    captureActions.addAction(unitWait);
    captureActions.addAction(clearInGameState);
    actions.put("UNIT_MOVE_CAPTURE_WAIT", captureActions);

    ActionBag supplyActions = new ActionBag("Supply Actions");
    supplyActions.addAction(unitMoveAnimated);
    supplyActions.addAction(unitSupplyAndHeal);
    supplyActions.addAction(unitWait);
    supplyActions.addAction(clearInGameState);
    actions.put("UNIT_MOVE_SUPPLY_WAIT", supplyActions);

    ActionBag loadActions = new ActionBag("Load Actions");
    loadActions.addAction(unitMoveAnimated);
    loadActions.addAction(unitLoadIntoTransport);
    loadActions.addAction(clearInGameState);
    actions.put("UNIT_MOVE_LOAD_WAIT", loadActions);

    ActionBag dropActions = new ActionBag("Drop Actions");
    dropActions.addAction(unitMoveAnimated);
    dropActions.addAction(unitWait);
    dropActions.addAction(unitDrop);
    dropActions.addAction(unitWait);
    dropActions.addAction(clearInGameState);
    actions.put("UNIT_MOVE_DROP_WAIT", dropActions);

    ActionBag attackActions = new ActionBag("Attack Actions");
    attackActions.addAction(unitMoveAnimated);
    attackActions.addAction(unitAttack);
    attackActions.addAction(unitWait);
    attackActions.addAction(clearInGameState);
    actions.put("UNIT_MOVE_ATTACK_WAIT", attackActions);
  }

  public void doAction(String actionName) {
    CWAction action = getAction(actionName);
    doAction(action);
  }

  public void doAction(CWAction action) {
    inGameSession.doAction(action);
  }

  public void addAction(String actionName, CWAction action) {
    actions.put(actionName.toUpperCase(), action);
  }

  public CWAction getAction(String actionName) {
    String key = actionName.toUpperCase();
    if (actions.containsKey(key)) {
      return actions.get(key);
    } else {
      throw new IllegalArgumentException(key + " not found actions=" + actions.keySet());
    }
  }
}
