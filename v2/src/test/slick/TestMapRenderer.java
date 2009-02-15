package test.slick;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.CWInput;
import com.customwars.client.ui.renderer.MapRenderer;
import com.customwars.client.ui.renderer.MiniMapRenderer;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.state.StateBasedGame;
import test.testData.HardCodedGame;

/**
 * renders a hardcoded map
 *
 * @author stefan
 */
public class TestMapRenderer extends com.customwars.client.ui.CWState {
  private MapRenderer mapRenderer;
  private MiniMapRenderer miniMapRenderer;
  private CWInput input;
  
  //Minor adjustment for mouseCursor...
  private int mouseX;
  private int mouseY;

  public TestMapRenderer(CWInput input) {
    this.input = input;
  }

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    TileMap<Tile> map = HardCodedGame.getMap();
    ImageStrip terrainStrip = new ImageStrip("res/image/awTerrains.png", map.getTileSize(), 42);

    ImageStrip cursor1 = new ImageStrip("res/image/selectCursor.png", 48, 48);
    TileSprite selectCursor = new TileSprite(cursor1, 250, map.getRandomTile(), map);
    ImageStrip cursor2 = new ImageStrip("res/image/aimcursor0.png", 54, 54);
    TileSprite aimCursor = new TileSprite(cursor2, map.getRandomTile(), map);

    mapRenderer = new MapRenderer(map);
    mapRenderer.setTerrainStrip(terrainStrip);
    mapRenderer.addCursor("SELECT", selectCursor);
    mapRenderer.addCursor("AIM", aimCursor);
    mapRenderer.activeCursor("SELECT");

    ImageStrip miniMapTerrainStrip = new ImageStrip("res/image/miniMap.png", 4, 4);
    miniMapRenderer = new MiniMapRenderer(map);
    miniMapRenderer.setTerrainStrip(miniMapTerrainStrip);
    miniMapRenderer.setLocation(510, 25);
    
    mouseX = 0;
    mouseY = 0;
  }

  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    mapRenderer.render(g);
    g.drawString(mapRenderer.getCursorLocation().toString(), 10, container.getHeight() - 20);

    g.drawString("MiniMap", 500, 2);
    miniMapRenderer.render(g);
  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    mapRenderer.update(delta);
  }

  public void controlPressed(Command command) {
    if (input.isSelectPressed(command)) {
      System.out.println("Clicked on " + mapRenderer.getCursorLocation());
    }
  }

  public void controlReleased(Command command) {
  }

  public void keyReleased(int key, char c) {
    if (key == Input.KEY_0) {
      mapRenderer.activeCursor("DOES_NOT_EXISTS");
    }
    if (key == Input.KEY_1) {
      mapRenderer.activeCursor("AIM");
    }
    if (key == Input.KEY_2) {
      mapRenderer.activeCursor("SELECT");
    }
    
    mapRenderer.moveCursor(mouseX, mouseY);
  }

  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
      mouseX = newx;
      mouseY = newy;
      mapRenderer.moveCursor(newx, newy);
  }

  public int getID() {
    return 1;
  }
}
