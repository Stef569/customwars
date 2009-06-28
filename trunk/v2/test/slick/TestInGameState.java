package slick;

import com.customwars.client.SFX;
import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.Statistics;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.InGameContext;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.state.StateBasedGame;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestInGameState extends CWState implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(TestInGameState.class);
  private InGameContext context;
  private GameContainer gameContainer;
  private GameRenderer gameRenderer;
  private Game game;
  private Fight fight = new UnitFight();
  private boolean gameOver;

  public TestInGameState() {
  }

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    gameContainer = container;
    gameRenderer = new GameRenderer(container);
    gameRenderer.loadResources(resources);
  }

  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);

    if (game == null || game != stateSession.game) {
      setGame(stateSession.game, container);
      stateSession.stats = new Statistics(game);
    }
  }

  private void setGame(Game game, GameContainer container) {
    this.game = game;
    MoveTraverse moveTraverse = new MoveTraverse(game.getMap());

    context = new InGameContext();
    context.setMoveTraverse(moveTraverse);
    context.setGame(game);
    context.setResources(resources);
    context.setContainer(container);

    gameRenderer.setInGameContext(context);
    context.setHud(gameRenderer.getHud());
    context.setGameRenderer(gameRenderer);

    ControllerManager controllerManager = new ControllerManager(context);
    context.setControllerManager(controllerManager);

    initGameListeners(game);
    controllerManager.initCityControllers();
    controllerManager.initUnitControllers();

    game.startGame();
  }

  private void initGameListeners(Game game) {
    if (this.game != game) {
      this.game.removePropertyChangeListener(this);
    }

    game.addPropertyChangeListener(this);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    Camera2D camera = gameRenderer.getCamera();
    container.getInput().setOffset(camera.getX(), camera.getY());

    gameRenderer.update(delta);
    context.update(delta);

    if (gameOver && context.isActionCompleted()) {
      changeGameState("GAME_OVER");
    }
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    gameRenderer.render(g);
    if (context != null && context.isUnitAttackMode()) {
      Location cursorLocation = gameRenderer.getCursorLocation();
      Unit attacker = game.getActiveUnit();
      Unit defender = game.getMap().getUnitOn(cursorLocation);
      if (defender != null) {
        fight.initFight(attacker, defender);
        int tileSize = game.getMap().getTileSize();
        int x = cursorLocation.getCol() * tileSize;
        int y = cursorLocation.getRow() * tileSize;

        // Draw The damage percentage at the top right of the cursorlocation
        g.drawString("Damage:" + fight.getAttackDamagePercentage() + "", x + 50, y - 50);
      }
    }
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (cwInput.isCancel(command)) {
      if (context.canUndo()) {
        SFX.playSound("cancel");
        context.undo();
        return;
      }
    }

    if (context.isGUIMode()) {
      gameRenderer.controlPressed(command, cwInput);
    } else {
      Tile cursorLocation = gameRenderer.getCursorLocation();
      Unit activeUnit = game.getActiveUnit();
      Unit selectedUnit = game.getMap().getUnitOn(cursorLocation);
      City city = game.getMap().getCityOn(cursorLocation);

      Unit unit;
      if (activeUnit != null) {
        unit = activeUnit;
      } else {
        unit = selectedUnit;
      }

      gameRenderer.moveCursor(command, cwInput);
      if (cwInput.isSelect(command)) {
        handleA(unit, city, cursorLocation);
      }

      if (cwInput.isCancel(command)) {
        handleB(activeUnit, selectedUnit);
      }
    }
  }

  public void handleA(Unit unit, City city, Tile cursorLocation) {
    if (unit != null && unit.isActive() || context.isInUnitMode()) {
      context.handleUnitAPress(unit);
    } else if (!cursorLocation.isFogged() && cursorLocation.getLocatableCount() == 0 &&
      city != null && city.getOwner() == game.getActivePlayer() && city.canBuild()) {
      context.handleCityAPress(city);
    } else if (context.isDefaultMode()) {
      new ClearInGameStateAction().invoke(context);
      ShowPopupMenu showContextMenu = buildContextMenu();
      showContextMenu.setLocation(cursorLocation);
      context.doAction(showContextMenu);
    } else {
      logger.warn("could not handle A press");
    }
  }

  private ShowPopupMenu buildContextMenu() {
    ShowPopupMenu showContextMenu = new ShowPopupMenu("Context menu");
    MenuItem endTurnMenuItem = new MenuItem("End turn", gameContainer);
    showContextMenu.addAction(ActionFactory.buildEndTurnAction(statelogic), endTurnMenuItem);
    return showContextMenu;
  }

  private void handleB(Unit activeUnit, Unit selectedUnit) {
    if (selectedUnit != null) {
      context.handleUnitBPress(selectedUnit);
    }
  }

  public void keyReleased(int key, char c) {
    if (!context.isMoving()) {
      if (key == Input.KEY_E) {
        context.doAction(ActionFactory.buildEndTurnAction(statelogic));
      }
    }
  }

  public void mouseWheelMoved(int newValue) {
    if (newValue > 0) {
      gameRenderer.zoomIn();
    } else {
      gameRenderer.zoomOut();
    }
  }

  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    gameRenderer.mouseMoved(newx, newy);
  }

  public int getID() {
    return 3;
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (propertyName.equals("state")) {
      if (game.isGameOver()) {
        gameOver = true;
      }
    }
  }
}
