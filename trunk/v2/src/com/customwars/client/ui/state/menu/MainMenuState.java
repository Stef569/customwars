package com.customwars.client.ui.state.menu;

import com.customwars.client.App;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.thingle.MenuListener;
import com.customwars.client.ui.thingle.ThingleMenu;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

public class MainMenuState extends CWState {
  private Image backGroundImage;
  private int windowHeight;
  private ThingleMenu mainMenu;

  public void init(final GameContainer container, StateBasedGame game) throws SlickException {
    windowHeight = container.getHeight();
    backGroundImage = resources.getSlickImg("dark_menu_background");
    Font font = resources.getFont("menu");
    String guiPath = App.get("gui.path");

    mainMenu = new ThingleMenu(guiPath, "mainMenu.xml", new MenuListener() {
      public void selected(int selectedIndex) {
        switch (selectedIndex) {
          case 0:
            changeToState("SINGLE_PLAYER");
            break;
          case 1:
            changeToState("MULTI_PLAYER");
            break;
          case 2:
            changeToState("MAP_EDITOR");
            break;
          case 3:
            changeToState("OPTIONS_MENU");
            break;
          case 4:
            changeToState("CREDITS");
            break;
          case 5:
            container.exit();
            break;
        }
      }
    });
    mainMenu.setFont(font);
    mainMenu.translateItems();
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
    mainMenu.enable();
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(backGroundImage, 0, 0);
    mainMenu.render();

    g.setColor(Color.lightGray);
    switch (mainMenu.getSelectedIndex()) {
      case 0:
        g.drawString("Single-player game", 210, windowHeight - 40);
        break;
      case 1:
        g.drawString("Play with more than one person", 210, windowHeight - 40);
        break;
      case 2:
        g.drawString("Create save and load maps with this editor", 210, windowHeight - 40);
        break;
      case 3:
        g.drawString("Fine tune the options", 210, windowHeight - 40);
        break;
      case 4:
        g.drawString("Shows the credits", 210, windowHeight - 40);
        break;
      case 5:
        g.drawString("Quit the game", 210, windowHeight - 40);
        break;
    }
  }

  @Override
  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    super.leave(container, game);
    mainMenu.disable();
  }

  @Override
  public void keyPressed(int key, char c) {
    mainMenu.keyPressed(key);
  }

  @Override
  public int getID() {
    return 1;
  }
}

