package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.controller.MapEditorController;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.hud.Dialog;
import com.customwars.client.ui.renderer.MapEditorRenderer;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import java.io.File;
import java.io.IOException;

/**
 * In this state the user can create open and save maps
 * Input is handled by the mapEditorController
 * rendering is handled by the MapEditorRenderer
 */
public class MapEditorState extends CWState {
  private static final Logger logger = Logger.getLogger(MapEditorState.class);
  private MapEditorController mapEditorController;
  private MapEditorRenderer mapEditorRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    mapEditorRenderer = new MapEditorRenderer(container, cwInput);
    mapEditorRenderer.loadResources(resources);
    mapEditorController = new MapEditorController(mapEditorRenderer, resources);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapEditorRenderer.update(delta);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (entered) {
      mapEditorRenderer.render(g);
    }
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    cwInput.resetInputTransform();
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    Tile cursorLocation = mapEditorRenderer.getCursorLocation();
    int selectedIndex = mapEditorRenderer.getSelectedIndex();

    switch (command.getEnum()) {
      case SELECT:
        select();
        break;
      case FILL_MAP:
        mapEditorController.fill(selectedIndex);
        break;
      case NEXT_PAGE:
        mapEditorController.nextPanel();
        break;
      case RECOLOR:
        mapEditorController.nextColor();
        break;
      case DELETE:
        mapEditorController.delete(cursorLocation);
        break;
      case SAVE:
        saveMap();
        break;
      case OPEN:
        openMap();
        break;
      case NEW:
        createNewMap();
        break;
      default:
        if (command.isMoveCommand()) {
          moveCursor(command);
        }
    }
  }

  @Override
  public void mousePressed(int button, int x, int y) {
    if (button == Input.MOUSE_LEFT_BUTTON) {
      select();
    }
  }

  private void select() {
    boolean clickedOnMap = mapEditorRenderer.isMouseInMap();

    if (clickedOnMap) {
      Tile cursorLocation = mapEditorRenderer.getCursorLocation();
      int selectedIndex = mapEditorRenderer.getSelectedIndex();
      mapEditorController.add(cursorLocation, selectedIndex);
    }
  }

  private void saveMap() {
    cwInput.setActive(false);
    Dialog dialog = new Dialog("Save Map");
    dialog.addTextField("Map name");
    dialog.addTextField("Map description");
    dialog.addTextField("Author");

    int eventType = dialog.show();
    handleSaveMapDialogInput(eventType, dialog);
    cwInput.setActive(true);
  }

  public void handleSaveMapDialogInput(int eventType, Dialog dialog) {
    if (eventType == JOptionPane.OK_OPTION) {
      String mapName = dialog.getFieldValue("map name");
      String mapDescription = dialog.getFieldValue("map description");
      String author = dialog.getFieldValue("author");

      try {
        mapEditorController.saveMap(mapName, mapDescription, author);
        GUI.showdialog(
          String.format("%s your map '%s'\nhas been saved to %s", author, mapName, App.get("home.maps.dir")),
          "Saved"
        );
      } catch (IOException e) {
        logger.error(e);
        GUI.showExceptionDialog(
          String.format("Could not save the map '%s'", mapName), e,
          "Error while saving"
        );
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }

  private void openMap() {
    cwInput.setActive(false);
    JFileChooser fileChooser = new JFileChooser(App.get("home.maps.dir"));
    fileChooser.showOpenDialog(null);

    File file = fileChooser.getSelectedFile();
    handleOpenMapDialogInput(file);
    cwInput.setActive(true);
  }

  private void handleOpenMapDialogInput(File file) {
    if (file != null) {
      try {
        mapEditorController.loadMap(file);
      } catch (IOException e) {
        logger.error(e);
        GUI.showExceptionDialog(
          String.format("Could not open the map '%s'", file.getPath()), e,
          "Error while Opening map"
        );
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }

  private void createNewMap() {
    cwInput.setActive(false);
    Dialog dialog = new Dialog("New Map");
    dialog.addTextField("Cols");
    dialog.addTextField("Rows");

    int eventType = dialog.show();
    handleNewMapDialogInput(eventType, dialog);
    cwInput.setActive(true);
  }

  private void handleNewMapDialogInput(int eventType, Dialog dialog) {
    if (eventType == JOptionPane.OK_OPTION) {
      String colsVal = dialog.getFieldValue("cols");
      String rowsVal = dialog.getFieldValue("rows");
      String errTitle = "Error while creating new map";

      try {
        int cols = Integer.parseInt(colsVal);
        int rows = Integer.parseInt(rowsVal);

        if (cols <= 1) {
          GUI.showErrDialog(
            cols + " is too small, please increase the cols value(>1)",
            errTitle
          );
          return;
        }

        if (rows <= 1) {
          GUI.showErrDialog(
            rows + " is too small, please increase the rows value(>1)",
            errTitle
          );
          return;
        }

        mapEditorController.createEmptyMap(cols, rows);
      } catch (NumberFormatException e) {
        logger.error(e);
        GUI.showExceptionDialog(
          "Please enter a numeric value", e,
          errTitle
        );
      } catch (Exception e) {
        logger.error(e);
      }
    }
  }

  public void moveCursor(CWCommand command) {
    switch (command.getEnum()) {
      case UP:
        mapEditorController.moveCursor(Direction.NORTH);
        break;
      case DOWN:
        mapEditorController.moveCursor(Direction.SOUTH);
        break;
      case LEFT:
        mapEditorController.moveCursor(Direction.WEST);
        break;
      case RIGHT:
        mapEditorController.moveCursor(Direction.EAST);
        break;
    }
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    mapEditorController.moveCursor(newx, newy);
  }

  public int getID() {
    return 50;
  }
}
