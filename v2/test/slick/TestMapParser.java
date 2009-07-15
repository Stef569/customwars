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
 * Test the map parser by writing the hard coded map to a file testmap.map
 * then read that map back in and start rendering it.
 *
 * This ensures that the map parser does not return an invalid map
 * eg.: if a city has a null player then the maprenderer will throw an exception.
 *
 * The test starts when a key is pressed
 *
 * @author stefan
 */
public class TestMapParser extends CWState {
  private BinaryCW2MapParser mapParser;
  private MapRenderer mapRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    mapParser = new BinaryCW2MapParser();
  }

  @Override
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    super.enter(container, game);
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (mapRenderer != null)
      mapRenderer.render(g);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    if (mapRenderer != null)
      mapRenderer.update(delta);
  }

  public void keyPressed(int col, char c) {
    Map<Tile> loadedMap;  // the resulting map read from disk

    try {
      File mapFile = new File("testmap.map");
      mapParser.writeMap(HardCodedGame.getMap(), mapFile);
      loadedMap = mapParser.readMap(mapFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    }
    mapRenderer = new MapRenderer(loadedMap);
    mapRenderer.loadResources(resources);
  }

  public int getID() {
    return 6;
  }
}
