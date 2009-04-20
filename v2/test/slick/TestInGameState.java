package slick;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
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

  public TestInGameState() {
  }

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    gameContainer = container;
    gameRenderer = new GameRenderer(container);
    gameRenderer.loadResources(resources);
  }

  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);

    if (game == null || game != stateSession.getGame()) {
      setGame(stateSession.getGame(), container);
    }
  }

  private void setGame(Game game, GameContainer container) {
    this.game = game;
    game.init();
    MoveTraverse moveTraverse = new MoveTraverse(game.getMap());

    context = new InGameContext();
    context.setMoveTraverse(moveTraverse);
    context.setGame(game);
    context.setResources(resources);
    context.setContainer(container);

    gameRenderer.setInGameContext(context);
    context.setHud(gameRenderer.getHud());
    context.setMapRenderer(gameRenderer.getMapRenderer());

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
    gameRenderer.update(delta);
    context.update(delta);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    gameRenderer.render(g);
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (!context.isMoving()) {
      if (cwInput.isCancel(command)) {
        if (context.canUndo()) {
          context.playSound("cancel");
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
  }

  public void handleA(Unit unit, City city, Tile cursorLocation) {
    if (unit != null && unit.isActive() || context.isInUnitMode()) {
      context.handleUnitAPress(unit);
    } else if (!cursorLocation.isFogged() && cursorLocation.getLocatableCount() == 0 &&
            city != null && city.getOwner() == game.getActivePlayer() && city.canBuild()) {
      context.handleCityAPress(city);
    } else if (context.isDefaultMode()) {
      context.doAction(new ClearInGameStateAction());
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
        changeGameState("GAME_OVER");
      }
    }
  }
}