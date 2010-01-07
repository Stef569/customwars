package com.customwars.client.ui.state.multiplayer;

import com.customwars.client.App;
import com.customwars.client.controller.multiplayer.ServerGameLoginController;
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
 * Allows the user to login into an existing server game
 */
public class ServerGameLoginState extends CWState {
  private Page page;
  private Image backGroundImage;
  private ServerGameLoginController controller;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    controller = new ServerGameLoginController(stateChanger, stateSession);
    initPage();
    backGroundImage = resources.getSlickImg("light_menu_background");
  }

  private void initPage() {
    ThinglePageLoader thingleLoader = new ThinglePageLoader(App.get("gui.path"));
    page = thingleLoader.loadPage("ServerGameLogin.xml", "greySkin.properties", controller);
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
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      controller.back();
    }
  }

  @Override
  public int getID() {
    return 23;
  }
}
