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
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;
import org.newdawn.slick.thingle.Widget;
import org.newdawn.slick.thingle.internal.slick.FontWrapper;

import java.util.List;

/**
 * Allows the player to select a map.
 * The maps are divided into map categories [2P, 3P, 4P, Classic, ...]
 * <p/>
 * A scaled minimap is shown on the right side.
 * Below the minimap is a panel that shows the count of each city type in the chosen map.
 * <p/>
 * Category: ___   Map name
 * map1            -------------
 * map2            |           |
 * map3            |           |
 * map4            |           |
 * map5            -------------
 * map6            Map description
 */
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
    backGroundImage = resources.getSlickImg("light_menu_background");

    initPage(gameContainer);
    initWidgetRenderers();
    controller.loadMapCategories();
    page.layout();
  }

  private void initPage(GameContainer container) {
    ThinglePageLoader thingleLoader = new ThinglePageLoader(App.get("gui.path"));
    page = thingleLoader.loadPage("mapSelect.xml", "greySkin.properties", controller);
    page.setFont(new FontWrapper(guiFont));

    // Make sure that the map list occupies all vertical space.
    Widget mapCategoryCbo = page.getWidget("map_list");
    mapCategoryCbo.setInteger("height", container.getHeight() - 140);
  }

  private void initWidgetRenderers() {
    page.getWidget("mini_map").setRenderer(miniMapRenderer);
    page.getWidget("map_city_count").setRenderer(cityCountRenderer);
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    controller.enter();
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
    if (container.getInput().isKeyPressed(Input.KEY_ADD)) {
      controller.addPressed();
    }
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
