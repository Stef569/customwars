package com.customwars.client.model.ai;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.CWAction;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.DirectAction;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.ai.build.BuildAI;
import com.customwars.client.model.ai.build.BuildAIActions;
import com.customwars.client.model.ai.build.DefaultBuildAI;
import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.ai.unit.DefaultUnitAI;
import com.customwars.client.model.ai.unit.UnitAI;
import com.customwars.client.model.ai.unit.UnitAiActions;
import com.customwars.client.model.ai.unit.UnitOrder;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * This class is the main entry point for the AI.
 * All work is delegated to the Build and Unit AI.
 */
public class CustomWarsAI {
  private static final Logger logger = Logger.getLogger(CustomWarsAI.class);
  private final Game game;
  private final InGameContext inGameContext;
  private final ControllerManager controllers;
  private BuildAI buildAI;
  private UnitAI unitAI;
  private int previousPlayerID = -1;

  public CustomWarsAI(Game game, InGameContext inGameContext) {
    this.game = game;
    this.inGameContext = inGameContext;
    controllers = inGameContext.getObj(ControllerManager.class);
  }

  public void update() {
    if (game.getActivePlayer().isAi()) {
      int playerID = game.getActivePlayer().getId();

      if (playerID != this.previousPlayerID) {
        act();
      }
    }

    previousPlayerID = game.getActivePlayer().getId();
  }

  /**
   * The AI will act.
   *
   * 1. Find the best game actions
   * 2. Create CWActions
   * 3. Execute the actions
   */
  public void act() {
    inGameContext.setAiActing(true);

    // Create a copy of the game
    // All the AI orders are immediately performed in here.
    // A list of orders is build to be executed in the real game.
    Game gameCopy = new Game(game);
    this.buildAI = new DefaultBuildAI(gameCopy);
    this.unitAI = new DefaultUnitAI(gameCopy, controllers);
    List<CWAction> actions = createActions();
    inGameContext.setQueue(actions);
  }

  /**
   * 1. The Build AI creates a list of units to build on a factory in this turn.
   * 2. The Build actions are created
   * 3. The Unit AI thinks about what the units should do
   * 4. The unit actions are created
   * 5. All actions are returned
   *
   * @return A list of actions to be executed in this turn
   */
  private List<CWAction> createActions() {
    List<CWAction> buildActions = buildUnits();
    List<CWAction> unitActions = unitActions();

    List<CWAction> actions = new ArrayList<CWAction>();
    actions.addAll(buildActions);
    actions.addAll(unitActions);

    if (!unitAI.isGameOver()) {
      CWAction endTurn = createEndTurnAction();
      actions.add(endTurn);
    }

    return actions;
  }

  private List<CWAction> buildUnits() {
    List<CWAction> actions = new ArrayList<CWAction>();

    java.util.Map<City, Unit> unitsToBuild = buildAI.findUnitsToBuild();
    BuildAIActions buildAiActions = new BuildAIActions(unitsToBuild, game);
    logBuild(unitsToBuild);

    List<CWAction> buildActions = buildAiActions.createActions();
    actions.addAll(buildActions);
    return actions;
  }

  private void logBuild(Map<City, Unit> unitsToBuild) {
    logger.debug("Money to spend " + game.getActivePlayer().getBudget());
    logger.debug("AI BUILD:");

    for (City city : unitsToBuild.keySet()) {
      Unit unit = unitsToBuild.get(city);
      logger.info(
        String.format("Creating %s(%s) on %s @ %s", unit.getName(), unit.getPrice(), city.getName(), city.getLocation().getLocationString())
      );
    }
  }

  private List<CWAction> unitActions() {
    List<CWAction> actions = new ArrayList<CWAction>();

    List<UnitOrder> orders = unitAI.findBestUnitOrders();
    logUnitMoves(orders);

    UnitAiActions unitAiActions = new UnitAiActions(orders, game);
    List<CWAction> unitActions = unitAiActions.createActions();
    actions.addAll(unitActions);
    return actions;
  }

  private void logUnitMoves(List<UnitOrder> unitsOrders) {
    logger.info("AI ACTIONS:");

    for (UnitOrder unitOrder : unitsOrders) {
      Unit unit = game.getMap().getUnitOn(unitOrder.unitLocation);
      Fuz.UNIT_ORDER unitOrderEnum = unitOrder.order;
      String moveDestinationLocationText;

      if (unitOrder.moveDestination != null) {
        moveDestinationLocationText = unitOrder.moveDestination.getLocationString();
      } else {
        moveDestinationLocationText = "";
      }

      String targetText;
      if (unitOrder.target != null) {
        targetText = "Target @ " + unitOrder.target.getLocationString();
      } else {
        targetText = "";
      }

      if (unit != null) {
        logger.debug(
          String.format("%s @ %s %s @ %s %s",
            unit.getName(), unit.getLocationString(), unitOrderEnum, moveDestinationLocationText, targetText)
        );
      }
    }
  }

  private CWAction createEndTurnAction() {
    Player nextPlayer = game.getNextActivePlayer();

    if (nextPlayer.isAi()) {
      // Don't show the gui screen, keep playing game.
      return new AIEndTurnAction();
    } else {
      // Show the End Turn screen
      return ActionFactory.buildEndTurnAction();
    }
  }

  /**
   * This is used by the AI to end his turn without GUI end turn screen
   */
  private class AIEndTurnAction extends DirectAction {
    private InGameContext inGameContext;

    public AIEndTurnAction() {
      super("AI end turn");
    }

    @Override
    protected void init(InGameContext inGameContext) {
      this.inGameContext = inGameContext;
    }

    @Override
    protected void invokeAction() {
      if (!game.isGameOver()) {
        new ClearInGameStateAction().invoke(inGameContext);
        inGameContext.setAiActing(false);
        game.endTurn();
      }
    }
  }
}
