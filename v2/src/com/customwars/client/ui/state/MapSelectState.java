package com.customwars.client.ui.state;

import com.customwars.client.App;
import com.customwars.client.controller.MapSelectController;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.tools.ThingleUtil;
import com.customwars.client.ui.renderer.widget.CityCountWidgetRenderer;
import com.customwars.client.ui.renderer.widget.MiniMapWidgetRenderer;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.slick.FontWrapper;

import java.util.Collection;
import java.util.List;

public class MapSelectState extends CWState {
  private Page page;
  private Image backGroundImage;
  private MiniMapWidgetRenderer miniMapRenderer;
  private CityCountWidgetRenderer cityCountRenderer;
  private MapSelectController controller;
  private Font guiFont;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    miniMapRenderer = new MiniMapWidgetRenderer(resources);
    cityCountRenderer = new CityCountWidgetRenderer(resources);
    controller = new MapSelectController(resources, miniMapRenderer, cityCountRenderer, stateChanger, stateSession);
    guiFont = resources.getFont("gui_text");

    initPage(gameContainer);
    initWidgetRenderers();
    loadMapCategories();
    backGroundImage = resources.getSlickImg("light_menu_background");
    page.layout();
  }

  private void initPage(GameContainer container) {
    ThinglePageLoader thingleLoader = new ThinglePageLoader(App.get("gui.path"));
    page = thingleLoader.loadPage("mapSelect.xml", "greySkin.properties", controller);
    page.setFont(new FontWrapper(guiFont));

    // Make sure that the map list occupies all vertical space.
    Widget mapCategoryCbo = page.getWidget("map_list");
    mapCategoryCbo.setInteger("height", container.getHeight()-140);
  }

  private void initWidgetRenderers() {
    miniMapRenderer.setScale(3f);
    page.getWidget("mini_map").setRenderer(miniMapRenderer);
    page.getWidget("map_city_count").setRenderer(cityCountRenderer);
  }

  private void loadMapCategories() {
    List<String> mapCategories = resources.getAllMapCategories();
    initPageContent(mapCategories);
    initFilter(mapCategories);
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    loadMapCategories();
    page.enable();
  }

  private void initPageContent(List<String> mapCategories) {
    if (!mapCategories.isEmpty()) {
      Widget mapCategoryCbo = page.getWidget("map_categories");
      mapCategoryCbo.removeChildren();
      for (String mapCategory : mapCategories) {
        ThingleUtil.addChoice(page, mapCategoryCbo, mapCategory);
      }
      mapCategoryCbo.setText(mapCategories.get(0));
    }
  }

  private void initFilter(Collection<String> mapCategories) {
    if (!mapCategories.isEmpty()) {
      String firstMapCat = mapCategories.toArray(new String[mapCategories.size()])[0];
      controller.filterMapsOnCategory(firstMapCat);
    }
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
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      controller.back();
    } else if (command == CWInput.SELECT) {
      controller.continueToNextState();
    }
  }

  @Override
  public int getID() {
    return 11;
  }
}
