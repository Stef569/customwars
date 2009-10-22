package com.customwars.client.ui.state;

import com.customwars.client.io.ResourceManager;
import com.thoughtworks.xstream.core.util.Fields;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Music;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;

import java.lang.reflect.Field;

/**
 * A state that CW can be in, examples MainMenu, EndTurn, InGame
 * each state can listen for input commands(Select, Cancel, menuUp,menuDown etc)
 * by overwriting controlPressed(Command command)
 *
 * We extend BasicGameState to add cw specific functionality.
 *
 * @author stefan
 */
public abstract class CWState extends BasicGameState implements InputProviderListener {
  protected static StateChanger stateChanger;       // Allows to change to another state
  protected static ResourceManager resources;   // All the resources
  protected static CWInput cwInput;             // Handles input
  protected static StateSession stateSession;   // Handles Data between states
  protected static Font defaultFont;
  protected static Color defaultColor = Color.white;
  protected boolean entered;

  public final void controlPressed(Command command) {
    if (entered) controlPressed(command, cwInput);
  }

  public void controlPressed(Command command, CWInput cwInput) {
  }

  public final void controlReleased(Command command) {
    if (entered) controlReleased(command, cwInput);
  }

  public void controlReleased(Command command, CWInput cwInput) {
  }

  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    entered = true;
  }

  public void leave(GameContainer container, StateBasedGame game) throws SlickException {
    entered = false;
  }

  public final void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    if (defaultFont != null) g.setFont(defaultFont);
    if (defaultColor != null) g.setColor(defaultColor);
    render(container, g);
  }

  public abstract void render(GameContainer container, Graphics g) throws SlickException;

  public final void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    update(container, delta);
  }

  public abstract void update(GameContainer container, int delta) throws SlickException;

  public void changeGameState(String newStateName) {
    stateChanger.changeTo(newStateName);
  }

  public void toggleMusic(Music music) {
    if (music.playing()) {
      music.pause();
    } else {
      music.resume();
    }
  }

  public static void setStateChanger(StateChanger stateChanger) {
    CWState.stateChanger = stateChanger;
  }

  public static void setResources(ResourceManager resources) {
    CWState.resources = resources;
  }

  public static void setCwInput(CWInput cwInput) {
    CWState.cwInput = cwInput;
  }

  public static void setStateSession(StateSession stateSession) {
    CWState.stateSession = stateSession;
  }

  public static void setDefaultFont(GameContainer gameContainer) {
    Font font = resources.getFont("DEFAULT");
    CWState.defaultFont = font;

    try {
      Field f = GameContainer.class.getDeclaredField("defaultFont");
      f.setAccessible(true);
      Fields.write(f, gameContainer, font);
      f.setAccessible(false);
    } catch (NoSuchFieldException e) {
      e.printStackTrace();
    }
  }
}
