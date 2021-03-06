package com.customwars.client.ui.state.multiplayer;

import com.customwars.client.App;
import com.customwars.client.controller.multiplayer.ServerGameRoomController;
import com.customwars.client.io.loading.ThinglePageLoader;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.thingle.Page;

/**
 * Allows the user to view server the game stats and to chat with other users participating in this game.
 * The user can start the game or start his turn, depending on the current server game state.
 * A user can only login into server games he is participating in.
 */
public class ServerGameRoomState extends CWState {
  private static final int SERVER_GAME_ROOM_REFRESH_RATE = 10 * 1000; // 10 sec
  private Page page;
  private Image backGroundImage;
  private ServerGameRoomController controller;
  private int timeTaken;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    controller = new ServerGameRoomController(stateChanger, stateSession);
    initPage();
    backGroundImage = resources.getSlickImg("light_menu_background");
  }

  private void initPage() {
    ThinglePageLoader thingleLoader = new ThinglePageLoader(App.get("gui.path"));
    page = thingleLoader.loadPage("serverGameRoom.xml", "greySkin.properties", controller);
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
    synchronized (page) {
      page.render();
    }
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
    timeTaken += delta;

    if (timeTaken > SERVER_GAME_ROOM_REFRESH_RATE) {
      controller.refreshInfo();
      timeTaken = 0;
    }
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    if (command == CWInput.CANCEL) {
      controller.back();
    }
  }

  @Override
  public void keyPressed(int key, char c) {
    if (key == Input.KEY_ENTER) {
      controller.sendChatMessage();
    }
  }

  @Override
  public int getID() {
    return 24;
  }
}
