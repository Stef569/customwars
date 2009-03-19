package slick;

import com.customwars.client.io.loading.MapParser;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author stefan
 */
public class TestMapParser extends CWState {
  private MapParser mapParser;
  private MapRenderer mapRenderer;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    mapParser = new MapParser();
    mapRenderer = new MapRenderer();
    mapRenderer.loadResources(resources);
    mapRenderer.setTerrainStrip(resources.getSlickImgStrip("terrains"));
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    mapRenderer.render(0, 0, g);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    mapRenderer.update(delta);
  }

  public void keyPressed(int col, char c) {
    Map<Tile> hardCodedMap = HardCodedGame.getMap();
    List<String> mapProperties = new ArrayList<String>();
    Map<Tile> loadedMap;  // the resulting map read from disk

    for (String key : hardCodedMap.getPropertyKeys()) {
      mapProperties.add("[" + key + " " + hardCodedMap.getProperty(key) + "]");
    }

    try {
      mapParser.writeMap("test.map", mapProperties.toArray(new String[]{}), hardCodedMap);
      loadedMap = mapParser.loadMapAsResource("test.hardCodedMap");
    } catch (IOException e) {
      throw new RuntimeException(e);
    }

    // Change to loadedMap causes null pointer exception due all tiles being null.
    mapRenderer.setMap(hardCodedMap);
  }

  public int getID() {
    return 6;
  }
}
