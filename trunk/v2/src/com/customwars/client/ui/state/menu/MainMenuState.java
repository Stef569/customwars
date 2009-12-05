package com.customwars.client.ui.state.menu;

import com.customwars.client.ui.GUI;
import com.customwars.client.ui.MenuItem;
import com.customwars.client.ui.PopupMenu;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Color;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.gui.AbstractComponent;
import org.newdawn.slick.gui.ComponentListener;
import org.newdawn.slick.state.StateBasedGame;

import java.awt.Point;

public class MainMenuState extends CWState implements ComponentListener {
  private GameContainer gameContainer;
  private PopupMenu mainMenu;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    this.gameContainer = container;
    mainMenu = new PopupMenu(container);
    mainMenu.setBackGroundColor(new Color(0, 0, 0));
    mainMenu.setHoverColor(new Color(255, 255, 255, 0.20f));
    mainMenu.addItems(
      new MenuItem(resources.getSlickImg("single"), container),
      new MenuItem(resources.getSlickImg("multi"), container),
      new MenuItem(resources.getSlickImg("options"), container),
      new MenuItem(resources.getSlickImg("exit"), container)
    );

    mainMenu.addListener(this);
    Point center = GUI.getCenteredRenderPoint(mainMenu.getSize(), container);
    mainMenu.setLocation(center.x + 20, center.y - 100);
  }

  @Override
  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(resources.getSlickImg("menu"), 0, 0);
    g.setColor(Color.white);
    mainMenu.render(container, g);

    g.setColor(Color.lightGray);
    switch (mainMenu.getCurrentItem()) {
      case 0:
        g.drawString("Single-player games and options", 210, 440);
        break;
      case 1:
        g.drawString("Play with more than one person", 210, 440);
        break;
      case 2:
        g.drawString("Choose between a list of options", 210, 440);
        break;
      case 3:
        g.drawString("Quit the game", 210, 440);
        break;
    }
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  public void controlPressed(CWCommand command, CWInput cwInput) {
    mainMenu.controlPressed(command);
  }

  public void componentActivated(AbstractComponent source) {
    PopupMenu popupMenu = (PopupMenu) source;
    switch (popupMenu.getCurrentItem()) {
      case 0:
        changeToState("SINGLE_PLAYER");
        break;
      case 2:
        changeToState("REMAP_CONTROLS");
        break;
      case 3:
        gameContainer.exit();
        break;
    }
  }

  @Override
  public int getID() {
    return 1;
  }
}

