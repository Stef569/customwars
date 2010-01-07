package com.customwars.client.ui.state.multiplayer;

import com.customwars.client.App;
import com.customwars.client.controller.multiplayer.ServerGameCreateController;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;

/**
 * Allows the user to create a new server game
 */
public class ServerGameCreateState extends CWState {
  private Page page;
  private Image backGroundImage;
  private ServerGameCreateController controller;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    controller = new ServerGameCreateController(stateChanger, stateSession);
    initPage();
    backGroundImage = resources.getSlickImg("light_menu_background");
  }

  private void initPage() {
    ThinglePageLoader thingleLoader = new ThinglePageLoader(App.get("gui.path"));
    page = thingleLoader.loadPage("ServerGameCreate.xml", "greySkin.properties", controller);
  }


  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    controller.enter();

    // if a previous state has set a map in the statesession
    // make sure the controller knows about it
    if (stateSession.map != null) {
      controller.mapSelected();
    }
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
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      controller.back();
    }
  }

  @Override
  public int getID() {
    return 21;
  }
}
