package slick;

import com.customwars.client.action.ActionManager;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.controller.HumanUnitController;
import com.customwars.client.controller.UnitController;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.InGameSession;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.HashMap;

public class TestInGameState extends CWState implements PropertyChangeListener, ComponentListener {
  private HashMap<Unit, UnitController> unitControllers;
  private InGameSession inGameSession;
  private GameContainer gameContainer;

  // GUI
  private Camera2D camera;
  private MapRenderer mapRenderer;
  private HUD hud;

  // MODEL
  private Game game;

  // ACTIONS
  private ActionManager actionManager;

  public TestInGameState() {
    this.unitControllers = new HashMap<Unit, UnitController>();
    this.mapRenderer = new MapRenderer();
    this.inGameSession = new InGameSession();
  }

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    mapRenderer = new MapRenderer();
    mapRenderer.setTerrainStrip(resources.getSlickImgStrip("terrains"));
    mapRenderer.loadResources(resources);
    hud = new HUD(game, container);
    hud.loadResources(resources);
    gameContainer = container;
  }

  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);

    if (game == null || game != stateSession.getGame()) {
      setGame(stateSession.getGame(), container);
    }
  }

  private void setGame(Game game, GameContainer container) {
    MoveTraverse moveTraverse = new MoveTraverse(game.getMap());
    actionManager = new ActionManager(mapRenderer, inGameSession, moveTraverse, resources, game, hud);
    actionManager.buildActions();
    this.game = stateSession.getGame();
    game.init();
    initGameListeners(game);
    initUnitControllers(moveTraverse);
    hud.setGame(game);

    Map<Tile> map = game.getMap();
    setMap(map);

    initCamera(map, container);
    mapRenderer.setScroller(new Scroller(camera));


    buildContextMenu();
    game.startGame();
  }

  private void buildContextMenu() {
    ShowPopupMenu showContextMenu = new ShowPopupMenu("Context menu", hud, inGameSession, mapRenderer);
    showContextMenu.addAction(actionManager.getAction("END_TURN"), "End turn");
    actionManager.addAction("CONTEXT_MENU", showContextMenu);
  }

  private void initGameListeners(Game game) {
    if (this.game != game) {
      this.game.removePropertyChangeListener(this);
    }

    game.addPropertyChangeListener(this);
  }

  private void initUnitControllers(MoveTraverse moveTraverse) {
    unitControllers.clear();
    for (Player player : game.getAllPlayers()) {
      if (!player.isNeutral())
        if (!player.isAi()) {
          for (Unit unit : player.getArmy()) {
            UnitController unitController = new HumanUnitController(game, unit, actionManager, moveTraverse, inGameSession, mapRenderer, hud);
            unitControllers.put(unit, unitController);
          }
        }
    }
  }

  private void setMap(Map<Tile> map) {
    // Create & add Cursors
    ImageStrip cursor1 = resources.getSlickImgStrip("selectCursor");
    ImageStrip cursor2 = resources.getSlickImgStrip("aimCursor");
    TileSprite selectCursor = new TileSprite(cursor1, 250, map.getRandomTile(), map);
    TileSprite aimCursor = new TileSprite(cursor2, map.getRandomTile(), map);

    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("ATTACK", aimCursor);
    mapRenderer.activateCursor("SELECT");
    mapRenderer.setMap(map);
    hud.moveOverTile(mapRenderer.getCursorLocation(), true);
  }

  private void initCamera(Map<Tile> map, GameContainer container) {
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    Dimension screenSize = new Dimension(container.getWidth(), container.getHeight());
    camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    BasicComponent.setCamera(camera);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
    camera.update(delta);
    actionManager.update(delta);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.scale(camera.getZoomLvl(), camera.getZoomLvl());
    mapRenderer.render(-camera.getX(), -camera.getY(), g);
    hud.render(g);
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (!inGameSession.isMoving()) {
      if (cwInput.isCancelPressed(command)) {
        if (inGameSession.canUndo()) {
          inGameSession.undo();
          return;
        }
      }

      if (inGameSession.isGUIMode()) {
        hud.controlPressed(command, cwInput);
      } else {
        Tile cursorLocation = (Tile) mapRenderer.getCursorLocation();
        Unit activeUnit = game.getActiveUnit();
        Unit selectedUnit = game.getMap().getUnitOn(cursorLocation);
        City city = game.getMap().getCityOn(cursorLocation);

        Unit unit;
        if (activeUnit != null) {
          unit = activeUnit;
        } else {
          unit = selectedUnit;
        }

        moveCursor(command, cwInput);
        if (cwInput.isSelectPressed(command)) {
          handleA(unit, city, cursorLocation);
        }

        if (cwInput.isCancelPressed(command)) {
          handleB(activeUnit, selectedUnit);
        }
      }
    }
  }

  public void handleA(Unit unit, City city, Tile cursorLocation) {
    handleUnitAPress(unit);

    if (inGameSession.isDefaultMode()) {
      inGameSession.setClick(2, cursorLocation);
      actionManager.doAction("CONTEXT_MENU");
    }
  }

  private void handleB(Unit activeUnit, Unit selectedUnit) {
    if (selectedUnit != null) {
      handleUnitBPress(selectedUnit);
    }
  }

  private void handleUnitAPress(Unit unit) {
    UnitController unitController = unitControllers.get(unit);
    if (unitController instanceof HumanUnitController) {
      HumanUnitController humanUnitController = (HumanUnitController) unitController;
      humanUnitController.handleAPress();
    }
  }

  private void handleUnitBPress(Unit unit) {
    UnitController unitController = unitControllers.get(unit);
    if (unitController instanceof HumanUnitController) {
      HumanUnitController humanUnitController = (HumanUnitController) unitController;
      humanUnitController.handleBPress();
    }
  }

  private void moveCursor(Command command, CWInput cwInput) {
    boolean traversing = mapRenderer.isTraversing();

    if (cwInput.isUpPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToNextLocation();
      else
        mapRenderer.moveCursor(Direction.NORTH);
    }

    if (cwInput.isDownPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToPreviousLocation();
      else
        mapRenderer.moveCursor(Direction.SOUTH);
    }

    if (cwInput.isLeftPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToPreviousLocation();
      else
        mapRenderer.moveCursor(Direction.WEST);
    }

    if (cwInput.isRightPressed(command)) {
      if (traversing)
        mapRenderer.moveCursorToNextLocation();
      else
        mapRenderer.moveCursor(Direction.EAST);
    }
    hud.moveOverTile(mapRenderer.getCursorLocation(), true);
  }

  public void keyReleased(int key, char c) {
    if (!inGameSession.isMoving()) {
      if (key == Input.KEY_R) {
        setGame(stateSession.getGame(), gameContainer);
      }
      if (key == Input.KEY_E) {
        actionManager.doAction("END_TURN");
      }
    }
  }

  public void mouseWheelMoved(int newValue) {
    if (newValue > 0) {
      camera.zoomIn();
    } else {
      camera.zoomOut();
    }
  }

  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    int gameX = camera.convertToGameX(newx);
    int gameY = camera.convertToGameY(newy);
    mapRenderer.moveCursor(gameX, gameY);
    hud.moveOverTile(mapRenderer.getCursorLocation(), true);
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
    } else if (propertyName.equals("turn")) {
      changeGameState("END_TURN");
    }
  }

  /**
   * A click on a menu item in the context menu
   */
  public void componentActivated(AbstractComponent abstractComponent) {
    PopupMenu popupMenu = (PopupMenu) abstractComponent;
    switch (popupMenu.getCurrentOption()) {
      case 0:
        game.endTurn();
        break;
    }
  }
}
