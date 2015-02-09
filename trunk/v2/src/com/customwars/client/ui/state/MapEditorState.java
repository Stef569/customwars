package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.controller.mapeditor.MapEditorController;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.GUI;
import com.customwars.client.ui.renderer.MapEditorRenderer;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import com.customwars.client.ui.thingle.DialogResult;
import com.customwars.client.ui.thingle.InputDialogListener;
import com.customwars.client.ui.thingle.ThingleInputDialog;
import org.apache.log4j.Logger;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.util.FileChooserListener;

import java.io.File;

/**
 * In this state the user can create open and save maps
 * Input is handled by the mapEditorController
 * rendering is handled by the MapEditorRenderer
 */
public class MapEditorState extends CWState {
  private static final Logger logger = Logger.getLogger(MapEditorState.class);
  private MapEditorController mapEditorController;
  private MapEditorRenderer mapEditorRenderer;
  private Map lastLoadedMap;

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
    mapEditorRenderer = null;
    mapEditorController = null;
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (GUI.isRenderingDialog()) return;

    Tile cursorLocation = mapEditorRenderer.getCursorLocation();
    int selectedIndex = mapEditorRenderer.getSelectedIndex();

    switch (command.getEnum()) {
      case SELECT:
        mapEditorController.addToMap();
        break;
      case CANCEL:
        mapEditorController.toggleCursorLock();
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
      case MAP_EDITOR_CONSTANT_MODE:
        mapEditorController.toggleConstantMode();
        break;
      case EXIT:
        changeToPreviousState();
        break;
      default:
        if (command.isMoveCommand()) {
          moveCursor(command);
        }
    }
  }

  @Override
  public void mousePressed(int button, int x, int y) {
    if (GUI.isRenderingDialog()) return;

    if (button == Input.MOUSE_LEFT_BUTTON) {
      mapEditorController.addToMap();
    } else if (button == Input.MOUSE_RIGHT_BUTTON) {
      mapEditorController.storeGameObject();
    }
  }

  private void saveMap() {
    ThingleInputDialog saveMapDialog = new ThingleInputDialog("Save Map", new InputDialogListener() {
      public void buttonClicked(ThingleInputDialog dialog, DialogResult button) {
        if (button == DialogResult.OK) {
          handleSaveMapDialogInput(dialog);
        }
      }
    });

    String defaultMapName = "";
    String defaultMapDescription = "";
    String defaultAuthor = App.get("user.name");

    if (lastLoadedMap != null) {
      defaultMapName = lastLoadedMap.getMapName();
      defaultMapDescription = lastLoadedMap.getDescription();
      defaultAuthor = lastLoadedMap.getAuthor();
    }

    saveMapDialog.addTextField("Map Name", defaultMapName);
    saveMapDialog.addTextField("Map Description", defaultMapDescription);
    saveMapDialog.addTextField("Author", defaultAuthor);
    GUI.showDialog(saveMapDialog);
  }

  public void handleSaveMapDialogInput(ThingleInputDialog dialog) {
    String mapName = dialog.getFieldValue("map name");
    String mapDescription = dialog.getFieldValue("map description");
    String author = dialog.getFieldValue("author");
    mapEditorController.saveMap(mapName, mapDescription, author);
  }

  private void openMap() {
    File homeDir = new File(App.get("home.maps.dir"));
    GUI.browseForFile("Open Map", null, "Open", homeDir, new FileChooserListener() {
      public void fileSelected(File file) {
        handleOpenMapDialogInput(file);
      }

      public void chooserCanceled() {
      }
    });
  }

  private void handleOpenMapDialogInput(File file) {
    if (file != null) {
      try {
        lastLoadedMap = mapEditorController.loadMap(file);
      } catch (Exception e) {
        logger.error(e);
        GUI.showExceptionDialog(
          String.format("Could not open the map '%s'", file.getPath()), e,
          "Error while Opening map"
        );
      }
    }
  }

  private void createNewMap() {
    final ThingleInputDialog dialog = new ThingleInputDialog("New Map", new InputDialogListener() {
      public void buttonClicked(ThingleInputDialog dialog, DialogResult button) {
        if (button == DialogResult.OK) {
          handleNewMapDialogInput(dialog);
        }
      }
    });
    dialog.addTextField("Cols", "25");
    dialog.addTextField("Rows", "25");
    GUI.showDialog(dialog);
  }

  private void handleNewMapDialogInput(ThingleInputDialog dialog) {
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

  public void moveCursor(CWCommand command) {
    if (GUI.isRenderingDialog()) return;

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
    if (GUI.isRenderingDialog()) return;
    mapEditorController.moveCursor(newx, newy);
  }

  public int getID() {
    return 50;
  }
}
