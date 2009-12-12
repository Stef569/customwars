package com.customwars.client.controller;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.widget.CityCountWidgetRenderer;
import com.customwars.client.ui.renderer.widget.MiniMapWidgetRenderer;
import com.customwars.client.ui.state.StateChanger;
import com.customwars.client.ui.state.StateSession;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;

/**
 * Handles input in the MapSelectState
 */
public class MapSelectController {
  private Page page;
  private final ResourceManager resources;
  private final MiniMapWidgetRenderer miniMapRenderer;
  private final CityCountWidgetRenderer citiesRenderer;
  private final StateChanger stateChanger;
  private final StateSession stateSession;

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

  public void mapSelected() {
    Map<Tile> map = getCurrentSelectedMap();
    page.getWidget("map_name").setText(map.getMapName());

    miniMapRenderer.setMap(map);
    citiesRenderer.setMap(map);
  }

  public void filterMapsOnCategory(String mapCategory) {
    Widget mapList = page.getWidget("map_list");
    mapList.removeChildren();

    for (Map<Tile> map : resources.getAllMapsByCategory(mapCategory)) {
      Widget item = page.createWidget("item");
      item.setText(map.getMapName());
      mapList.add(item);
    }

    // Select first map in the list
    mapList.getChild(0).setBoolean("selected", true);
    mapSelected();
  }

  private Map<Tile> getCurrentSelectedMap() {
    Widget mapList = page.getWidget("map_list");
    Widget selectedItem = mapList.getSelectedWidget();
    return resources.getMap(selectedItem.getText());
  }

  public void continueToNextState() {
    stateSession.map = getCurrentSelectedMap();
    stateChanger.changeTo("GAME_RULES");
  }

  public void back() {
    stateChanger.changeToPrevious();
  }
}