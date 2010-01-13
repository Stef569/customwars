package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.GameController;
import com.customwars.client.controller.InGameCursorController;
import com.customwars.client.model.Statistics;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.model.map.path.MoveTraverse;
import com.customwars.client.tools.ColorUtil;
import com.customwars.client.ui.Camera2D;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.HUD;
import com.customwars.client.ui.renderer.GameRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Dimension;
import java.awt.Point;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * In this state the user can play and win a game against opponents
 * Rendering is handled by GameRenderer and hud
 * Input is handled by GameController
 *
 * The Game to display is read from the session when entering this state.
 */
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
  private Point center;

  // Control
  private GameController gameControl;
  private InGameCursorController cursorControl;
  private Input input;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    this.guiContext = container;
    this.input = guiContext.getInput();
  }

  @Override
  public void enter(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.enter(container, stateBasedGame);

    switch (App.getGameMode()) {
      case SINGLE_PLAYER:
        enterSinglePlayerMode(stateSession.game);
        break;
      case NETWORK_SNAIL_GAME:
        enterMultiPlayerMode(stateSession.game);
        break;
      case LOAD_SAVED_GAME:
        initSubSystems(stateSession.game);
        App.changeGameMode(App.GAME_MODE.SINGLE_PLAYER);
        break;
    }

    hud.moveOverTile(gameRenderer.getCursorLocation());
    stateChanger.clearPreviousStatesHistory();
    stateChanger.stopRecordingStateHistory();
  }

  /**
   * In SP mode the game is started once and
   * game sub systems(gui, ingamecontext) are inited once.
   */
  private void enterSinglePlayerMode(Game game) {
    if (!game.isStarted() && !game.isGameOver()) {
      game.startGame();
      initSubSystems(game);
    }
  }

  /**
   * In MP mode the game is never started and
   * game sub systems(gui, ingamecontext) are inited each time when the user enters this state
   */
  private void enterMultiPlayerMode(Game game) {
    initSubSystems(game);
  }

  private void initSubSystems(Game game) {
    initGame(game);
    initGameContext(game, guiContext);
    stateSession.stats = new Statistics(game);
  }

  private void initGame(Game game) {
    initGameListener(game);
    this.game = game;
    this.map = game.getMap();
    initCamera(map);
    initScriptObjects();
    center = GUI.getCenteredRenderPoint(map.getSize(), guiContext);
  }

  private void initGameContext(Game game, GUIContext container) {
    MoveTraverse moveTraverse = new MoveTraverse(map);
    hud = new HUD(container);

    gameRenderer = new GameRenderer(game, camera, hud, moveTraverse);
    gameRenderer.loadResources(resources);

    inGameContext = new InGameContext();
    inGameContext.setMoveTraverse(moveTraverse);
    inGameContext.setGame(game);
    inGameContext.setResources(resources);
    inGameContext.setContainer((GameContainer) container);
    inGameContext.setHud(hud);
    inGameContext.setGameRenderer(gameRenderer);
    inGameContext.setStateChanger(stateChanger);
    inGameContext.setStateSession(stateSession);

    ControllerManager controllerManager = new ControllerManager(inGameContext);
    inGameContext.setControllerManager(controllerManager);

    gameControl = gameRenderer.getGameControl();
    gameControl.setInGameContext(inGameContext);

    cursorControl = (InGameCursorController) gameControl.getCursorController();
    inGameContext.setGameController(gameControl);

    controllerManager.initCityControllers();
    controllerManager.initUnitControllers();
    initCursors(map);
  }

  /**
   * Add objects to beanshell, accessible by their name
   */
  private void initScriptObjects() {
    for (Player p : game.getAllPlayers()) {
      String colorName = ColorUtil.toString(p.getColor());
      GUI.addLiveObjToConsole("p_" + colorName, p);
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

  private void initCamera(TileMap<Tile> map) {
    Dimension screenSize = new Dimension(guiContext.getWidth(), guiContext.getHeight());
    Dimension worldSize = new Dimension(map.getWidth(), map.getHeight());
    this.camera = new Camera2D(screenSize, worldSize, map.getTileSize());
    boolean zoomEnabled = App.getBoolean("display.zoom");
    camera.setZoomingEnabled(zoomEnabled);
    GUI.setCamera(camera);
  }

  private void initCursors(Map<Tile> map) {
    TileSprite selectCursor = resources.createCursor(map, App.get("user.selectcursor"));
    TileSprite attackCursor = resources.createCursor(map, App.get("user.attackcursor"));
    TileSprite siloCursor = resources.createCursor(map, App.get("user.silocursor"));

    cursorControl.addCursor("SELECT", selectCursor);
    cursorControl.addCursor("ATTACK", attackCursor);
    cursorControl.addCursor("SILO", siloCursor);
    cursorControl.activateCursor("SELECT");
    cursorControl.addListener(this);
  }

  @Override
  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.leave(container, stateBasedGame);
    cwInput.resetInputTransform();
    cursorControl.removeListener(this);
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    if (entered) {
      gameRenderer.update(delta);
      gameRenderer.setRenderAttackDamage(inGameContext.isUnitAttackMode());

      inGameContext.update(delta);
      if (gameOver && isInputAllowed()) {
        changeToState("GAME_OVER");
        inGameContext = null;
        gameOver = false;
      }

      if (hud.isRenderingAbsolutePopup()) {
        input.setOffset(0, 0);
      } else {
        input.setOffset(camera.getX() - center.x, camera.getY() - center.y);
      }
    }
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    if (entered) {
      g.translate(center.x, center.y);
      gameRenderer.render(g);
      g.resetTransform();
      hud.renderAbsolute(g);
    }
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (entered && isInputAllowed()) {
      if (inGameContext.canUndo() && command == CWInput.CANCEL) {
        gameControl.undo();
        return;
      }

      if (inGameContext.isGUIMode()) {
        hud.controlPressed(command);
      } else {
        if (command.isMoveCommand()) {
          moveCursor(command);
        }

        Tile cursorLocation = gameRenderer.getCursorLocation();
        switch (command.getEnum()) {
          case SELECT:
            gameControl.handleA(cursorLocation);
            break;
          case CANCEL:
            gameControl.handleB(cursorLocation);
            break;
          case END_TURN:
            gameControl.endTurn();
            break;
          case ZOOM_IN:
            camera.zoomIn();
            break;
          case ZOOM_OUT:
            camera.zoomOut();
            break;
          case UNIT_CYCLE:
            gameControl.startUnitCycle();
            break;
        }
      }
    }
  }

  @Override
  public void controlReleased(CWCommand command, CWInput cwInput) {
    if (command.isMoveCommand()) {
      cursorControl.moveControlReleased();
    }
  }

  public void moveCursor(CWCommand command) {
    switch (command.getEnum()) {
      case UP:
        cursorControl.moveCursor(Direction.NORTH);
        break;
      case DOWN:
        cursorControl.moveCursor(Direction.SOUTH);
        break;
      case LEFT:
        cursorControl.moveCursor(Direction.WEST);
        break;
      case RIGHT:
        cursorControl.moveCursor(Direction.EAST);
        break;
    }
  }

  /**
   * Input is allowed when all animations and actions are finished
   *
   * @return If the gui is ready to process input
   */
  private boolean isInputAllowed() {
    return gameRenderer != null && gameRenderer.isDyingUnitAnimationCompleted() &&
      inGameContext != null && inGameContext.isActionCompleted();
  }

  public void mouseWheelMoved(int newValue) {
    if (entered) {
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

    if (evt.getSource() instanceof Game && propertyName.equals("state")) {
      gameStateChanged(evt);
    } else if (evt.getSource() instanceof TileSprite && propertyName.equals("position")) {
      cursorPositionChanged(evt);
    }
  }

  private void gameStateChanged(PropertyChangeEvent evt) {
    this.gameOver = game.isGameOver();
  }

  private void cursorPositionChanged(PropertyChangeEvent evt) {
    TileSprite cursor = (TileSprite) evt.getSource();
    Tile newCursorLocation = (Tile) cursor.getLocation();
    hud.moveOverTile(newCursorLocation);
    SFX.playSound("maptick");
  }

  @Override
  public int getID() {
    return 14;
  }
}