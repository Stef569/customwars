package slick;

import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.io.IOException;

/**
 * @author stefan
 */
public class TestMapParser extends CWState {
  private BinaryCW2MapParser mapParser;
  private MapRenderer mapRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    mapParser = new BinaryCW2MapParser();
    mapRenderer = new MapRenderer();
    mapRenderer.loadResources(resources);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    mapRenderer.render(0, 0, g);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
  }

  public void keyPressed(int col, char c) {
    Map<Tile> loadedMap;  // the resulting map read from disk

    try {
      mapParser.writeMap(HardCodedGame.getMap(), new File("testmap.map"));
      loadedMap = mapParser.readMap(new File("testmap.map"));
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    mapRenderer.setMap(loadedMap);
  }

  public int getID() {
    return 6;
  }
}
