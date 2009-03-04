package com.customwars.client.action;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.MapRenderer;
import org.apache.log4j.Logger;
import tools.Args;

import java.util.HashMap;
import java.util.Map;

/**
 * Holds a reference to all Actions by upper case name.
 * Each action is updated in each game loop
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

    buildGameActions();
    buildUnitActions();
  }

  private void buildGameActions() {
    CWAction endTurn = new EndTurnAction(game, inGameSession, mapRenderer, hud);
    actions.put("END_TURN", endTurn);
  }

  private void buildUnitActions() {
    buildSelectActions();
    buildAnimatedActions();
  }

  private void buildSelectActions() {
    CWAction unitSelect = new UnitSelectAction(game, mapRenderer, inGameSession);
    actions.put("SELECT_UNIT", unitSelect);
  }

  private void buildAnimatedActions() {
    CWAction unitMoveAnimated = new MoveAnimatedAction(game, mapRenderer, moveTraverse, inGameSession);
    CWAction unitWait = new UnitWaitAction(game, mapRenderer, inGameSession);
    CWAction unitCapture = new CaptureAction(game, inGameSession);
    actions.put("UNIT_MOVE_ANIMATED", unitMoveAnimated);
    actions.put("UNIT_WAIT", unitWait);
    actions.put("UNIT_CAPTURE", unitCapture);

    // Animated move actions
    ActionBag waitActions = new ActionBag("Wait Actions");
    waitActions.addAction(unitMoveAnimated);
    waitActions.addAction(unitWait);
    actions.put("UNIT_MOVE_WAIT", waitActions);

    ActionBag captureActions = new ActionBag("Capture Actions");
    captureActions.addAction(unitMoveAnimated);
    captureActions.addAction(unitCapture);
    captureActions.addAction(unitWait);
    actions.put("UNIT_MOVE_CAPTURE_WAIT", captureActions);
  }

  public void doAction(String actionName) {
    CWAction action = getAction(actionName);
    doAction(action);
  }

  public void doAction(CWAction action) {
    action.doAction();
    if (action.canUndo()) {
      inGameSession.addUndoAction(action);
    }
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
