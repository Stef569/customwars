package slick;

import com.customwars.client.action.ActionFactory;
import com.customwars.client.action.ClearInGameStateAction;
import com.customwars.client.action.ShowPopupMenu;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.renderer.ModelEventsRenderer;
import com.customwars.client.ui.slick.BasicComponent;
import com.customwars.client.ui.sprite.TileSprite;
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

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class TestInGameState extends CWState implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(TestInGameState.class);
  private InGameContext context;
  private GameContainer gameContainer;

  // GUI
  private Camera2D camera;
  private MapRenderer mapRenderer;
  private ModelEventsRenderer modelEventsRenderer;
  private HUD hud;

  // MODEL
  private Game game;

  public TestInGameState() {
    this.mapRenderer = new MapRenderer();
    this.modelEventsRenderer = new ModelEventsRenderer();
  }

  public void init(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    mapRenderer.setTerrainStrip(resources.getSlickImgStrip("terrains"));
    mapRenderer.loadResources(resources);
    modelEventsRenderer.loadResources(resources);
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

    this.context = new InGameContext();
    context.setMoveTraverse(moveTraverse);
    context.setGame(game);
    context.setHud(hud);
    context.setMapRenderer(mapRenderer);

    ControllerManager controllerManager = new ControllerManager(context);
    context.setControllerManager(controllerManager);

    this.game = game;
    game.init();
    controllerManager.initCityControllers();
    controllerManager.initUnitControllers();
    initGameListeners(game);
    modelEventsRenderer.setGame(game);
    modelEventsRenderer.setMoveTraverse(moveTraverse);
    hud.setGame(game);

    Map<Tile> map = game.getMap();
    setMap(map);

    initCamera(map, container);
    mapRenderer.setScroller(new Scroller(camera));

    game.startGame();
  }

  private void initGameListeners(Game game) {
    if (this.game != game) {
      this.game.removePropertyChangeListener(this);
    }

    game.addPropertyChangeListener(this);
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
    mapRenderer.setNeutralColor(game.getNeutralColor());
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
    modelEventsRenderer.update(delta);
    context.update(delta);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (camera != null) {
      g.scale(camera.getZoomLvl(), camera.getZoomLvl());
      mapRenderer.render(-camera.getX(), -camera.getY(), g);
      renderDropLocations(g);
      modelEventsRenderer.render(-camera.getX(), -camera.getY(), g);
      hud.render(g);
    }
  }

  private void renderDropLocations(Graphics g) {
    for (Tile t : context.getDropLocations()) {
      Tile transportLocation = context.getClick(2);
      if (transportLocation != null) {
        Direction dir = game.getMap().getDirectionTo(transportLocation, t);
        mapRenderer.renderArrowHead(g, dir, t);
      }
    }
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (!context.isMoving()) {
      if (cwInput.isCancelPressed(command)) {
        if (context.canUndo()) {
          context.undo();
          return;
        }
      }

      if (context.isGUIMode()) {
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
    ShowPopupMenu showContextMenu = new ShowPopupMenu("Context menu", null);
    showContextMenu.addAction(ActionFactory.buildEndTurnAction(statelogic), "End turn");
    return showContextMenu;
  }

  private void handleB(Unit activeUnit, Unit selectedUnit) {
    if (selectedUnit != null) {
      context.handleUnitBPress(selectedUnit);
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
    if (!context.isMoving()) {
      if (key == Input.KEY_R) {
        setGame(stateSession.getGame(), gameContainer);
      }
      if (key == Input.KEY_E) {
        context.doAction(ActionFactory.buildEndTurnAction(statelogic));
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
    }
  }
}
