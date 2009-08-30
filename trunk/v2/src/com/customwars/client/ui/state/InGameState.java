package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.SFX;
import com.customwars.client.controller.ControllerManager;
import com.customwars.client.controller.CursorController;
import com.customwars.client.controller.GameController;
import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.Statistics;
import com.customwars.client.model.fight.Fight;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFight;
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
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;
import tools.ColorUtil;

import java.awt.Dimension;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class InGameState extends CWState implements PropertyChangeListener {
  private static final Logger logger = Logger.getLogger(InGameState.class);

  // Model
  private Game game;
  private Map<Tile> map;
  private boolean gameOver;
  private Fight fight;
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

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    this.guiContext = container;
    this.input = guiContext.getInput();
    this.fight = new UnitFight();
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
    initCursors(resources, map);

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
    GUI.addLiveObjToConsole("map", game.getMap());
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

  private void initCursors(ResourceManager resources, Map<Tile> map) {
    ImageStrip selectCursorImgs = resources.getSlickImgStrip("selectCursor");
    ImageStrip aimCursorImgs = resources.getSlickImgStrip("aimCursor");
    Image siloCursorImg = resources.getSlickImg("siloCursor");

    Tile randomTile = map.getRandomTile();
    TileSprite selectCursor = new TileSprite(selectCursorImgs, 250, randomTile, map);
    TileSprite aimCursor = new TileSprite(aimCursorImgs, randomTile, map);
    TileSprite siloCursor = new TileSprite(siloCursorImg, randomTile, map);

    selectCursor.addPropertyChangeListener(this);
    aimCursor.addPropertyChangeListener(this);
    siloCursor.addPropertyChangeListener(this);

    // Use the silo explosion cursor Image height to calculate the effect Range ie
    // If the image has a height of 160/32 is 5 tiles 5/2 rounded to int becomes 2.
    int effectRange = siloCursorImg.getHeight() / map.getTileSize() / 2;
    siloCursor.setEffectRange(effectRange);

    gameRenderer.addCursor("SELECT", selectCursor);
    gameRenderer.addCursor("ATTACK", aimCursor);
    gameRenderer.addCursor("SILO", siloCursor);
    gameRenderer.activateCursor("SELECT");
  }

  @Override
  public void leave(GameContainer container, StateBasedGame stateBasedGame) throws SlickException {
    super.leave(container, stateBasedGame);
    resetInputTransition();
  }

  private void resetInputTransition() {
    input.setOffset(0, 0);
    input.setScale(1, 1);
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
      renderAttackDamagePercentage(g);
      g.resetTransform();
    }
  }

  /**
   * Draw The damage percentage at the top right of the cursorlocation
   */
  private void renderAttackDamagePercentage(Graphics g) {
    if (inGameContext.isUnitAttackMode()) {
      Location cursorLocation = gameRenderer.getCursorLocation();
      Unit attacker = game.getActiveUnit();
      Unit defender = game.getMap().getUnitOn(cursorLocation);

      if (defender != null) {
        fight.initFight(attacker, defender);
        int tileSize = game.getMap().getTileSize();
        int cursorX = cursorLocation.getCol() * tileSize + tileSize / 2;
        int cursorY = cursorLocation.getRow() * tileSize + 5;
        int dmgPercentage = fight.getAttackDamagePercentage();

        String dmgTxt = "Damage:" + dmgPercentage + "%";
        int fontWidth = g.getFont().getWidth(dmgTxt);
        int fontHeight = g.getFont().getHeight(dmgTxt);

        final int BOX_MARGIN = 2;
        final int CURSOR_OFFSET = 64;

        int boxX = cursorX + CURSOR_OFFSET - BOX_MARGIN;
        int boxY = cursorY - CURSOR_OFFSET - BOX_MARGIN;
        int totalWidth = fontWidth + BOX_MARGIN * 2;
        int totalHeight = fontHeight + BOX_MARGIN * 2;

        // If the damage percentage does not fit to the gui make sure that it does
        // by setting the x,y away from the corner
        if (!GUI.canFitToScreen(boxX, boxY, totalWidth, totalHeight)) {
          Direction quadrant = map.getQuadrantFor(cursorLocation);
          switch (quadrant) {
            case NORTHEAST:
              boxX = cursorX - CURSOR_OFFSET - BOX_MARGIN;
              boxY = cursorY + CURSOR_OFFSET - BOX_MARGIN;
              break;
            case NORTHWEST:
              boxX = cursorX + CURSOR_OFFSET - BOX_MARGIN;
              boxY = cursorY + CURSOR_OFFSET - BOX_MARGIN;
              break;
            case SOUTHEAST:
              boxX = cursorX - CURSOR_OFFSET - BOX_MARGIN;
              boxY = cursorY - CURSOR_OFFSET - BOX_MARGIN;
              break;
            case SOUTHWEST:
              boxX = cursorX + CURSOR_OFFSET - BOX_MARGIN;
              boxY = cursorY - CURSOR_OFFSET - BOX_MARGIN;
              break;
          }
        }

        Color prevColor = g.getColor();
        g.setColor(new Color(0, 0, 0, 0.4f));
        g.fillRoundRect(boxX, boxY, totalWidth, totalHeight, 2);
        g.setColor(prevColor);
        g.drawString(dmgTxt, boxX + BOX_MARGIN, boxY + BOX_MARGIN);
      }
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
        Unit selectedUnit = game.getMap().getUnitOn(cursorLocation);
        City city = game.getMap().getCityOn(cursorLocation);

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

  public void moveCursor(Command command, CWInput cwInput) {
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
  }

  /**
   * Input is not allowed when an animation or action is still in progress
   *
   * @return If the gui is ready to process input
   */
  private boolean isInputAllowed() {
    return gameRenderer.isDyingUnitAnimationCompleted() || inGameContext.isActionCompleted();
  }

  public void mouseWheelMoved(int newValue) {
    boolean zoomEnabled = App.getBoolean("display.zoom");
    if (zoomEnabled) {
      if (entered) {
        if (newValue > 0) {
          camera.zoomIn();
        } else {
          camera.zoomOut();
        }
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
    SFX.playSound("maptick");
    Tile newCursorLocation = (Tile) cursor.getLocation();
    hud.moveOverTile(newCursorLocation);
  }

  @Override
  public int getID() {
    return 3;
  }
}