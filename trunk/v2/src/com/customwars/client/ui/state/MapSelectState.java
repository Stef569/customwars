package com.customwars.client.ui.state;

import com.customwars.client.controller.MapSelectController;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.ui.renderer.widget.MapCitiesWidgetRenderer;
import com.customwars.client.ui.renderer.widget.MiniMapWidgetRenderer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Thingle;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.slick.SlickThinletFactory;

import java.util.Collection;
import java.util.List;

public class MapSelectState extends CWState {
  private Page page;
  private Image backGroundImage;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    Thingle.init(new SlickThinletFactory(gameContainer));

    MiniMapWidgetRenderer miniMapRenderer = new MiniMapWidgetRenderer(resources);
    MapCitiesWidgetRenderer mapCitiesRenderer = new MapCitiesWidgetRenderer(resources);
    MapSelectController controller = new MapSelectController(resources, miniMapRenderer, mapCitiesRenderer, stateChanger, stateSession);

    initPage(controller);

    Widget miniMapPanel = page.getWidget("mini_map");
    miniMapPanel.setRenderer(miniMapRenderer);
    Widget mapCityCountPanel = page.getWidget("map_properties");
    mapCityCountPanel.setRenderer(mapCitiesRenderer);

    List<String> mapCategories = resources.getAllMapCategories();
    initPageContent(mapCategories);
    initFilter(mapCategories, controller);
    backGroundImage = resources.getSlickImg("light_menu_background");
  }

  private void initPage(MapSelectController controller) {
    ThinglePageLoader thingleLoader = new ThinglePageLoader("res/data/gui/");
    page = thingleLoader.loadPage("mapSelect.xml", "greySkin.properties", controller);
    page.setDrawDesktop(false);
  }

  private void initPageContent(List<String> mapCategories) {
    if (!mapCategories.isEmpty()) {
      Widget mapCategoryCbo = page.getWidget("mapCategories");
      for (String mapCategory : mapCategories) {
        Widget cboItem = page.createWidget("choice");
        cboItem.setText(mapCategory);
        mapCategoryCbo.add(cboItem);
      }
      mapCategoryCbo.setText(mapCategories.get(0));
    }
  }

  private void initFilter(Collection<String> mapCategories, MapSelectController controller) {
    if (!mapCategories.isEmpty()) {
      String firstMapCat = mapCategories.toArray(new String[mapCategories.size()])[0];
      controller.filterMapsOnCategory(firstMapCat);
    }
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    page.enable();
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    page.disable();
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(backGroundImage, 0, 0);
    page.render();
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public int getID() {
    return 11;
  }
}
