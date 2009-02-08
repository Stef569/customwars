package test.slick;

import client.model.map.Tile;
import client.model.map.TileMap;
import client.ui.CWInput;
import client.ui.renderer.MapRenderer;
import client.ui.renderer.MiniMapRenderer;
import client.ui.renderer.TileMapRenderer;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.command.InputProviderListener;
import org.newdawn.slick.state.BasicGameState;
import org.newdawn.slick.state.StateBasedGame;
import test.HardCodedGame;

/**
 * renders a hardcoded map
 *
 * @author stefan
 */
public class TestMapRenderer extends BasicGameState implements InputProviderListener {
  private TileMapRenderer mapRenderer;
  private TileMapRenderer miniMapRenderer;
  private CWInput cwInput;

  public TestMapRenderer(CWInput cwInput) {
    this.cwInput = cwInput;
    cwInput.addCommandListener(this);
  }

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    TileMap<Tile> map = HardCodedGame.getMap();
    mapRenderer = new MapRenderer(map);
    mapRenderer.loadResources();

    miniMapRenderer = new MiniMapRenderer(map);
    miniMapRenderer.loadResources();
    miniMapRenderer.setLocation(510, 25);
  }

  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    mapRenderer.render(g);

    g.drawString("MiniMap", 500, 2);
    miniMapRenderer.render(g);
  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
  }

  public void controlPressed(Command command) {
  }

  public void controlReleased(Command command) {
  }

  public int getID() {
    return 1;
  }
}
