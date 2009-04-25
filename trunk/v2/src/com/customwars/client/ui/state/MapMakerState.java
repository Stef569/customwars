package com.customwars.client.ui.state;

import com.customwars.client.controller.MapEditorController;
import com.customwars.client.ui.renderer.GameRenderer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.gui.GUIContext;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Allows to place units/cities/terrain on a map
 *
 * @author stefan
 */
public class MapMakerState extends CWState {
  private GameRenderer gameRenderer;
  private MapEditorController mapEditorController;
  private static final int STARTUP_MAP_COLS = 10;
  private static final int STARTUP_MAP_ROWS = 10;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    buildGameRenderer(container);
    buildMapEditorController(container);
  }

  private void buildGameRenderer(GUIContext container) {
    gameRenderer = new GameRenderer(container);
    gameRenderer.loadResources(resources);
    gameRenderer.setRenderHUD(false);
    gameRenderer.setRenderEvents(false);
  }

  private void buildMapEditorController(GUIContext container) {
    mapEditorController = new MapEditorController(gameRenderer, container);
    mapEditorController.loadResources(resources);
    mapEditorController.init();
    mapEditorController.createEmptyMap(STARTUP_MAP_COLS, STARTUP_MAP_ROWS);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    gameRenderer.update(delta);
    mapEditorController.getActivePanel().update(delta);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    gameRenderer.render(g);
    mapEditorController.getActivePanel().render(container, g);
  }

  @Override
  public void controlPressed(Command command, CWInput cwInput) {
    gameRenderer.moveCursor(command, cwInput);

    if (cwInput.isSelect(command)) {
      if (!mapEditorController.getActivePanel().isWithinComponent(cwInput.getMouseX(), cwInput.getMouseY())) {
        mapEditorController.add();
      }
    } else if (cwInput.isFillMap(command)) {
      mapEditorController.fill();
    } else if (cwInput.isNextPage(command)) {
      mapEditorController.nextPanel();
    } else if (cwInput.isRecolor(command)) {
      mapEditorController.nextColor();
    } else if (cwInput.isDelete(command)) {
      mapEditorController.delete();
    }
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    gameRenderer.mouseMoved(newx, newy);
  }

  public int getID() {
    return 50;
  }
}