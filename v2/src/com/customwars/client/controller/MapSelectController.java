package com.customwars.client.controller;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.StringUtil;
import com.customwars.client.tools.ThingleUtil;
import com.customwars.client.ui.renderer.widget.CityCountWidgetRenderer;
import com.customwars.client.ui.renderer.widget.MiniMapWidgetRenderer;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

import java.util.List;

/**
 * Handles input in the MapSelectState
 */
public class MapSelectController {
  private static final float MINIMAP_SCALE_STEP = 0.5f;
  private static final float MINIMAP_INITIAL_SCALE = 3f;
  private static final float MINIMAP_MAX_SCALE = 7f;
  private static final float MINIMAP_MIN_SCALE = 0.5f;

  private final ResourceManager resources;
  private final MiniMapWidgetRenderer miniMapRenderer;
  private final CityCountWidgetRenderer citiesRenderer;
  private final StateChanger stateChanger;
  private final StateSession stateSession;

  private float miniMapScale;
  private boolean miniMapScaleLoopDirectionZoomIn;
  private Page page;

  public MapSelectController(ResourceManager resources, MiniMapWidgetRenderer miniMapRenderer, CityCountWidgetRenderer citiesRenderer, StateChanger stateChanger, StateSession stateSession) {
    this.resources = resources;
    this.miniMapRenderer = miniMapRenderer;
    this.citiesRenderer = citiesRenderer;
    this.stateChanger = stateChanger;
    this.stateSession = stateSession;
  }

  public void init(Page page) {
    this.page = page;
  }

  public void enter() {
    miniMapScale = MINIMAP_INITIAL_SCALE;
    loadMapCategories();
  }

  public void loadMapCategories() {
    List<String> mapCategories = resources.getAllMapCategories();
    initPageContent(mapCategories);
    initFilter(mapCategories);
  }

  private void initPageContent(List<String> mapCategories) {
    if (!mapCategories.isEmpty()) {
      Widget mapCategoryCbo = page.getWidget("map_categories");
      mapCategoryCbo.removeChildren();

      for (String mapCategory : mapCategories) {
        ThingleUtil.addChoice(page, mapCategoryCbo, mapCategory);
      }

      selectDefaultMapCategory(mapCategoryCbo);
    }
  }

  private void selectDefaultMapCategory(Widget mapCategoryCbo) {
    // The previous category value is remembered
    // Only select the default first category if none is chosen
    int selectedIndex = mapCategoryCbo.getSelectedIndex();

    if (selectedIndex == -1) {
      mapCategoryCbo.setInteger("selected", 0);
    }
  }

  private void initFilter(List<String> mapCategories) {
    if (!mapCategories.isEmpty()) {
      Widget mapCategoryCbo = page.getWidget("map_categories");
      int selectedIndex = mapCategoryCbo.getSelectedIndex();
      String firstMapCategory = mapCategories.get(selectedIndex);
      filterMapsOnCategory(firstMapCategory);
    }
  }

  public void filterMapsOnCategory(String mapCategory) {
    if (resources.isValidMapCategory(mapCategory)) {
      Widget mapList = page.getWidget("map_list");
      mapList.removeChildren();

      for (Map map : resources.getAllMapsByCategory(mapCategory)) {
        Widget item = page.createWidget("item");
        item.setText(map.getMapName());
        mapList.add(item);
      }

      // Select first map in the list
      mapList.getChild(0).setBoolean("selected", true);
      mapSelected();
    }
  }

  public void mapSelected() {
    Map map = getCurrentSelectedMap();
    String mapName = map.getMapName();
    String mapAuthor = map.getAuthor();

    Widget mapNameWidget = page.getWidget("map_name");
    if (StringUtil.hasContent(map.getAuthor())) {
      mapNameWidget.setText(mapName + " by " + mapAuthor);
    } else {
      mapNameWidget.setText(mapName);
    }
    page.getWidget("map_description").setText(map.getDescription());

    miniMapRenderer.setMap(map);
    citiesRenderer.setMap(map);
    setInitialMiniMapScale(map);
    miniMapScaleLoopDirectionZoomIn = true;
  }

  /**
   * Auto zoom. Find the best zoom factor based on the map size.
   * Where the map size is the biggest row or col of the map.
   *
   * @param map The map to calculate the best initial minimap scale for
   */
  private void setInitialMiniMapScale(Map map) {
    int mapSize;

    if (map.getCols() < map.getRows()) {
      mapSize = map.getRows();
    } else {
      mapSize = map.getCols();
    }

    if (mapSize <= 10) {
      miniMapScale = 6;
    } else if (mapSize <= 15) {
      miniMapScale = 5;
    } else if (mapSize <= 20) {
      miniMapScale = 4;
    } else if (mapSize <= 25) {
      miniMapScale = 3;
    } else if (mapSize <= 35) {
      miniMapScale = 2;
    } else {
      miniMapScale = 1;
    }

    miniMapRenderer.setScale(miniMapScale);
  }

  private Map getCurrentSelectedMap() {
    Widget mapList = page.getWidget("map_list");
    Widget selectedItem = mapList.getSelectedWidget();
    return resources.getMap(selectedItem.getText());
  }

  public void continueToNextState() {
    stateSession.map = getCurrentSelectedMap();
    stateChanger.changeTo("PLAYER_OPTIONS");
  }

  public void addPressed() {
    if (miniMapScaleLoopDirectionZoomIn) {
      zoomIn();
    } else {
      zoomOut();
    }

    if (miniMapScale == MINIMAP_MAX_SCALE) {
      miniMapScaleLoopDirectionZoomIn = false;
    } else if (miniMapScale == MINIMAP_MIN_SCALE) {
      miniMapScaleLoopDirectionZoomIn = true;
    }
  }

  public void zoomIn() {
    miniMapScale = getValidScale(miniMapScale + MINIMAP_SCALE_STEP);
    miniMapRenderer.setScale(miniMapScale);
  }

  public void zoomOut() {
    miniMapScale = getValidScale(miniMapScale - MINIMAP_SCALE_STEP);
    miniMapRenderer.setScale(miniMapScale);
  }

  private float getValidScale(float newScale) {
    if (newScale > MINIMAP_MAX_SCALE) {
      return MINIMAP_MAX_SCALE;
    } else if (newScale < MINIMAP_MIN_SCALE) {
      return MINIMAP_MIN_SCALE;
    } else {
      return newScale;
    }
  }

  public void back() {
    stateChanger.changeToPrevious();
  }
}
