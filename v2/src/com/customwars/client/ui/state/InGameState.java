package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.GameController;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.Statistics;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;
import tools.ColorUtil;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.List;

public class InGameState extends CWState implements PropertyChangeListener {
  // Model
  private Game game;
  private Map<Tile> map;
  private boolean gameOver;
  private InGameContext inGameContext;

  // GUI
  private GUIContext guiContext;
  private HUD hud;
  private Camera2D camera;
  private GameRenderer gameRenderer;

  // Control
  private GameController gameControl;
  private CursorController cursorControl;
  private Input input;
  private boolean cursorAtZoneEdge, enableCursorZoneCheck;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    this.guiContext = container;
    this.input = guiContext.getInput();
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);
    Game game = stateSession.game;

    if (!game.isStarted() && !game.isGameOver()) {
      game.startGame();
      setGame(game, container);
      stateSession.stats = new Statistics(game);
    }
  }

  private void setGame(Game game, GameContainer container) {
    initGameListener(game);
    this.game = game;
    this.map = game.getMap();
    initCamera(map);
    initScriptObjects(game, resources);

    MoveTraverse moveTraverse = new MoveTraverse(map);
    hud = new HUD(container);

    gameRenderer = new GameRenderer(game, camera, hud, moveTraverse);
    gameRenderer.loadResources(resources);
    initCursors(map);

    inGameContext = new InGameContext();
    inGameContext.setMoveTraverse(moveTraverse);
    inGameContext.setGame(game);
    inGameContext.setResources(resources);
    inGameContext.setContainer(container);
    inGameContext.setHud(hud);
    inGameContext.setGameRenderer(gameRenderer);

    ControllerManager controllerManager = new ControllerManager(inGameContext);
    inGameContext.setControllerManager(controllerManager);

    gameControl = gameRenderer.getGameControl();
    gameControl.setInGameContext(inGameContext);
    gameControl.setStateLogic(statelogic);

    cursorControl = gameControl.getCursorController();
    inGameContext.setGameController(gameControl);

    controllerManager.initCityControllers();
    controllerManager.initUnitControllers();
  }

  /**
   * We add various objects to beanshell, accessible by their name
   */
  private void initScriptObjects(Game game, ResourceManager resources) {
    GUI.init(guiContext, camera);
    GUI.setGame(game);
    for (Player p : game.getAllPlayers()) {
      GUI.addLiveObjToConsole("p_" + ColorUtil.toString(p.getColor()), p);
    }

    GUI.addLiveObjToConsole("game", game);
    GUI.addLiveObjToConsole("map", map);
    GUI.addLiveObjToConsole("resources", resources);
  }

  private void initGameListener(Game game) {
    if (this.game != null) {
      this.game.removePropertyChangeListener(this);
    }
    game.addPropertyChangeListener(this);
  }

  private void initCamera(Map<Tile> map) {
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    this.camera = new Camera2D(screenSize, worldSize, map.getTileSize());
  }

  private void initCursors(Map<Tile> map) {
    Tile randomTile = map.getRandomTile();
    TileSprite selectCursor = resources.getCursor(App.get("user.selectcursor"));
    selectCursor.setMap(map);
    selectCursor.setLocation(randomTile);
    selectCursor.addPropertyChangeListener(this);

    TileSprite attackCursor = resources.getCursor(App.get("user.attackcursor"));
    attackCursor.setMap(map);
    attackCursor.setLocation(randomTile);
    attackCursor.addPropertyChangeListener(this);

    TileSprite siloCursor = resources.getCursor(App.get("user.silocursor"));
    siloCursor.setMap(map);
    siloCursor.setLocation(randomTile);
    siloCursor.addPropertyChangeListener(this);

    gameRenderer.addCursor("SELECT", selectCursor);
    gameRenderer.addCursor("ATTACK", attackCursor);
    gameRenderer.addCursor("SILO", siloCursor);
    gameRenderer.activateCursor("SELECT");
  }

  @Override
  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.leave(container, stateBasedGame);
    cwInput.resetInputTransition();
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    if (gameRenderer != null) {
      gameRenderer.update(delta);

      inGameContext.update(delta);
      if (gameOver && isInputAllowed()) {
        changeGameState("GAME_OVER");
        gameOver = false;
      }
      input.setOffset(camera.getX(), camera.getY());
    }
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    // gameRenderer is only assigned when init has been completed
    // render can be invoked before init by a transition see StateBasedGame
    if (gameRenderer != null) {
      gameRenderer.render(g);
      g.translate(-camera.getX(), -camera.getY());
      if (inGameContext.isUnitAttackMode()) {
        gameRenderer.renderAttackDamagePercentage(g);
      }
      g.resetTransform();
    }
  }

  public void controlPressed(Command command, CWInput cwInput) {
    if (entered && isInputAllowed()) {
      if (cwInput.isCancel(command)) {
        if (inGameContext.canUndo()) {
          gameControl.undo();
          return;
        }
      }

      moveCursor(command, cwInput);

      if (inGameContext.isGUIMode()) {
        hud.controlPressed(command, cwInput);
      } else {
        Tile cursorLocation = gameRenderer.getCursorLocation();
        Unit activeUnit = game.getActiveUnit();
        Unit selectedUnit = map.getUnitOn(cursorLocation);
        City city = map.getCityOn(cursorLocation);

        Unit unit;
        if (activeUnit != null) {
          unit = activeUnit;
        } else {
          unit = selectedUnit;
        }

        if (cwInput.isSelect(command)) {
          gameControl.handleA(unit, city, cursorLocation);
        }

        if (cwInput.isCancel(command)) {
          gameControl.handleB(activeUnit, selectedUnit);
        }

        if (cwInput.isEndTurn(command)) {
          gameControl.endTurn(statelogic);
        }

        boolean zoomEnabled = App.getBoolean("display.zoom");
        if (zoomEnabled) {
          if (cwInput.isZoomIn(command)) {
            camera.zoomIn();
          } else if (cwInput.isZoomOut(command)) {
            camera.zoomOut();
          }
        }
      }
    }
  }

  @Override
  public void controlReleased(Command command, CWInput cwInput) {
    if (cwInput.isMoveCommand(command)) {
      cursorControl.setCursorLocked(false);

      if (cursorAtZoneEdge) {
        enableCursorZoneCheck = false;
        cursorAtZoneEdge = false;
      }
    }
  }

  public void moveCursor(Command command, CWInput cwInput) {
    Tile originalLocation = gameRenderer.getCursorLocation();
    if (cwInput.isMoveCommand(command)) {
      if (cwInput.isUp(command)) {
        cursorControl.moveCursor(Direction.NORTH);
      }

      if (cwInput.isDown(command)) {
        cursorControl.moveCursor(Direction.SOUTH);
      }

      if (cwInput.isLeft(command)) {
        cursorControl.moveCursor(Direction.WEST);
      }

      if (cwInput.isRight(command)) {
        cursorControl.moveCursor(Direction.EAST);
      }

      lockCursorAtMoveZoneEdge(originalLocation);
    }
  }

  private void lockCursorAtMoveZoneEdge(Location originalCursorLocation) {
    Unit activeUnit = game.getActiveUnit();

    if (activeUnit != null) {
      List<Location> moveZone = activeUnit.getMoveZone();
      Location cursorLocation = gameRenderer.getCursorLocation();

      if (moveZone.contains(cursorLocation)) {
        cursorAtZoneEdge = false;
        enableCursorZoneCheck = true;
      } else {
        // The cursor moved outside the moveZone!
        if (enableCursorZoneCheck) {
          // Snap the cursor back, until enableCursorZoneCheck is put to false(see controlReleased)
          cursorControl.moveCursor(originalCursorLocation);
          cursorControl.setCursorLocked(true);
          cursorAtZoneEdge = true;
        }
      }
    }
  }

  /**
   * Input is allowed when all animations and actions are finished
   *
   * @return If the gui is ready to process input
   */
  private boolean isInputAllowed() {
    return gameRenderer.isDyingUnitAnimationCompleted() && inGameContext.isActionCompleted();
  }

  public void mouseWheelMoved(int newValue) {
    boolean zoomEnabled = App.getBoolean("display.zoom");
    if (entered && zoomEnabled) {
      if (newValue > 0) {
        camera.zoomIn();
      } else {
        camera.zoomOut();
      }
    }
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    if (entered) {
      cursorControl.moveCursor(newx, newy);
    }
  }

  public void propertyChange(PropertyChangeEvent evt) {
    String propertyName = evt.getPropertyName();

    if (propertyName.equals("state")) {
      this.gameOver = game.isGameOver();
    } else if (evt.getSource() instanceof TileSprite && propertyName.equals("position")) {
      cursorPositionChanged(evt);
    }
  }

  private void cursorPositionChanged(PropertyChangeEvent evt) {
    TileSprite cursor = (TileSprite) evt.getSource();
    Tile newCursorLocation = (Tile) cursor.getLocation();
    hud.moveOverTile(newCursorLocation);
    SFX.playSound("maptick");
  }

  @Override
  public int getID() {
    return 3;
  }
}