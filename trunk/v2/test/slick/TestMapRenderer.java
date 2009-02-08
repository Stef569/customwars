package test.slick;

import client.ui.renderer.MapRenderer;
import client.ui.renderer.TileMapRenderer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import test.HardCodedGame;

/**
 * Shows a hardcoded map to the screen
 * @author stefan
 */
public class TestMapRenderer extends BasicGameState {
  private TileMapRenderer tileMapRenderer;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    tileMapRenderer = new MapRenderer(HardCodedGame.getMap());
    tileMapRenderer.loadResources();
  }

  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    tileMapRenderer.render(g);
  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
  }

  public int getID() {
    return 1;
  }
}
