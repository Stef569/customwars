package slick;

import com.customwars.client.io.loading.map.BinaryCW2MapParser;
import com.customwars.client.model.map.Map;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.state.CWState;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Test the map parser by writing the hard coded map to a file testmap.map
 * then read that map back in and start rendering it.
 * <p/>
 * This ensures that the map parser does not return an invalid map
 * eg.: if a city has a null player then the maprenderer will throw an exception.
 * <p/>
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

  public void render(GameContainer container, Graphics g) throws SlickException {
    if (mapRenderer != null)
      mapRenderer.render(g);
    else
      g.drawString("Startup complete, press any key to write/read and render the map", 10, 10);
  }

  public void update(GameContainer container, int delta) throws SlickException {
    if (mapRenderer != null)
      mapRenderer.update(delta);
  }

  public void keyPressed(int col, char c) {
    Map loadedMap;  // the resulting map read from disk
    File mapFile = new File("testmap.map");

    try {
      writeMapToFile(mapFile);
      loadedMap = readMapFromFile(mapFile);
    } catch (IOException e) {
      throw new RuntimeException(e);
    } finally {
      assert mapFile.delete();
    }

    mapRenderer = new MapRenderer(loadedMap);
    mapRenderer.loadResources(resources);
  }

  private void writeMapToFile(File mapFile) throws IOException {
    assert mapFile.createNewFile();

    OutputStream out = new FileOutputStream(mapFile);
    mapParser.writeMap(HardCodedGame.getMap(), out);
  }

  private Map readMapFromFile(File mapFile) throws IOException {
    InputStream in = new FileInputStream(mapFile);
    return mapParser.readMap(in);
  }

  public int getID() {
    return 102;
  }
}
