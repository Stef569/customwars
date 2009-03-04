package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import com.customwars.client.model.map.TileMap;
import com.customwars.client.ui.Scroller;
import com.customwars.client.ui.sprite.SpriteManager;
import com.customwars.client.ui.sprite.TileSprite;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import java.util.List;

/**
 * A map is rendered in 2 layers
 * The terrain and the sprites on the terrain
 */
public class MapRenderer extends TileMapRenderer {
  // Control
  private boolean renderTerrain = true;
  private boolean renderSprites = true;
  private boolean renderZones = true;
  private boolean renderArrows;
  private boolean cursorLocked;
  private Scroller scroller;

  // Graphics
  private SpriteManager spriteManager;
  private Animation moveZoneAnim;
  private Animation attackZoneAnim;
  private ImageStrip arrowImages;

  // Data
  private List<Location> moveZone;
  private List<Location> attackZone;
  private Unit activeUnit;

  public MapRenderer() {
    spriteManager = new SpriteManager();
  }

  public void loadResources(ResourceManager resources) {
    spriteManager.loadResources(resources);
    moveZoneAnim = new Animation(resources.getSlickImgStrip("moveZone").toArray(), 250);
    attackZoneAnim = new Animation(resources.getSlickImgStrip("attackZone").toArray(), 250);
    arrowImages = resources.getSlickImgStrip("arrows");
  }

  public void moveCursor(int x, int y) {
    moveCursor(pixelsToTile(x, y));
  }

  public void moveCursor(Direction direction) {
    Location cursorLocation = getCursorLocation();
    Location newLocation = map.getAdjacent(cursorLocation, direction);
    moveCursor(newLocation);
  }

  public void moveCursor(Location location) {
    if (!cursorLocked) {
      spriteManager.moveCursorTo(location);
      scroller.setCursorLocation(spriteManager.getCursorLocation());
    }
  }

  public void update(int elapsedTime) {
    spriteManager.update(elapsedTime);
    scroller.update(elapsedTime);
    if (moveZone != null) {
      moveZoneAnim.update(elapsedTime);
    }
    if (attackZone != null) {
      attackZoneAnim.update(elapsedTime);
    }
  }

  public void render(int x, int y, Graphics g) {
    if (renderTerrain) super.render(x, y, g);
    if (renderSprites) spriteManager.render(x, y, g);
    if (renderZones) renderzones(x, y, g);
    if (activeUnit != null) spriteManager.renderUnit(x, y, g, activeUnit);
  }

  private void renderzones(int x, int y, Graphics g) {
    if (moveZone != null) {
      for (Location location : moveZone) {
        renderImgOnTile(g, moveZoneAnim.getCurrentFrame(), location, x, y);
      }
    }

    if (attackZone != null) {
      for (Location location : attackZone) {
        renderImgOnTile(g, attackZoneAnim.getCurrentFrame(), location, x, y);
      }
    }

    if (renderArrows && activeUnit != null) {
      renderArrowPath(g);
    }
  }

  /**
   * Shows a path of arrows from the active unit to the cursor location
   * MoveSystem generates a path of Directions. to render an arrow we need a current and next direction
   * So we read 1 Direction ahead. When the loop is at the last item in the path we render an arrowHead
   * The arrowHead only needs 1 direction(the arrow base)
   *
   * nextLocation starts at the unit location and is set to the next location on each loop
   */
  private void renderArrowPath(Graphics g) {
    Map<Tile> map = (Map<Tile>) super.map;
    Location clicked = spriteManager.getCursorLocation();
    List<Direction> directionPath = map.getDirectionsPath(activeUnit, clicked);
    renderArrowPath(g, directionPath);
  }

  public void renderArrowPath(Graphics g, List<Direction> directionPath) {
    Location nextLocation = activeUnit.getLocation();

    for (int i = 0; i < directionPath.size(); i++) {
      Direction currentDirection = directionPath.get(i);
      Direction nextDirection = i + 1 < directionPath.size() ? directionPath.get(i + 1) : null;

      // Get the next location to render the arrow on.
      nextLocation = map.getAdjacent(nextLocation, currentDirection);

      if (i == directionPath.size() - 1) {
        renderArrowHead(g, currentDirection, nextLocation);
      } else {
        renderArrow(g, currentDirection, nextDirection, nextLocation);
      }
    }
  }

  private void renderArrow(Graphics g, Direction previousDirection, Direction nextDirection, Location location) {
    int x = location.getCol() * tileSize;
    int y = location.getRow() * tileSize;

    if (nextDirection == Direction.NORTH) {
      if (previousDirection == Direction.EAST)
        g.drawImage(arrowImages.getSubImage(4), x, y);
      else if (previousDirection == Direction.WEST)
        g.drawImage(arrowImages.getSubImage(5), x, y);
      else
        g.drawImage(arrowImages.getSubImage(1), x, y);
    } else if (nextDirection == Direction.EAST) {
      if (previousDirection == Direction.NORTH)
        g.drawImage(arrowImages.getSubImage(2), x, y);
      else if (previousDirection == Direction.SOUTH)
        g.drawImage(arrowImages.getSubImage(5), x, y);
      else
        g.drawImage(arrowImages.getSubImage(0), x, y);
    } else if (nextDirection == Direction.SOUTH) {
      if (previousDirection == Direction.EAST)
        g.drawImage(arrowImages.getSubImage(3), x, y);
      else if (previousDirection == Direction.WEST)
        g.drawImage(arrowImages.getSubImage(2), x, y);
      else
        g.drawImage(arrowImages.getSubImage(1), x, y);
    } else if (nextDirection == Direction.WEST) {
      if (previousDirection == Direction.NORTH)
        g.drawImage(arrowImages.getSubImage(3), x, y);
      else if (previousDirection == Direction.SOUTH)
        g.drawImage(arrowImages.getSubImage(4), x, y);
      else
        g.drawImage(arrowImages.getSubImage(0), x, y);
    }
  }

  private void renderArrowHead(Graphics g, Direction baseDirection, Location location) {
    int x = location.getCol() * tileSize;
    int y = location.getRow() * tileSize;

    if (baseDirection == Direction.NORTH)
      g.drawImage(arrowImages.getSubImage(6), x, y);
    else if (baseDirection == Direction.EAST)
      g.drawImage(arrowImages.getSubImage(7), x, y);
    else if (baseDirection == Direction.SOUTH)
      g.drawImage(arrowImages.getSubImage(8), x, y);
    else if (baseDirection == Direction.WEST)
      g.drawImage(arrowImages.getSubImage(9), x, y);
  }


  public void addCursorSprite(String cursorName, TileSprite cursorSprite) {
    spriteManager.addCursor(cursorName, cursorSprite);
  }

  public void activateCursor(String cursorName) {
    spriteManager.setActiveCursor(cursorName);
  }

  public void addCursor(String cursorName, TileSprite cursor) {
    spriteManager.addCursor(cursorName, cursor);
  }

  public void removeZones() {
    removeMoveZone();
    removeAttackZone();
  }

  public void showArrows(boolean showArrow) {
    this.renderArrows = showArrow;
  }

  public void showAttackZone() {
    if (activeUnit != null)
      this.attackZone = activeUnit.getAttackZone();
    else
      this.attackZone = null;
  }

  public void removeAttackZone() {
    this.attackZone = null;
  }

  public void showMoveZone() {
    if (activeUnit != null)
      this.moveZone = activeUnit.getMoveZone();
    else
      this.moveZone = null;
  }

  public void removeMoveZone() {
    this.moveZone = null;
  }

  public void setMap(TileMap<Tile> map) {
    super.setMap(map);
    spriteManager.setMap(map);
    spriteManager.loadSprites();
  }

  public void setScroller(Scroller scroller) {
    this.scroller = scroller;
  }

  public void setActiveUnit(Unit activeUnit) {
    this.activeUnit = activeUnit;
  }

  public void setAutoScroll(boolean scroll) {
    scroller.setAutoScroll(scroll);
  }

  public void toggleCursorLock() {
    this.cursorLocked = !cursorLocked;
  }

  public void setCursorLocked(boolean cursorLock) {
    this.cursorLocked = cursorLock;
  }

  public Location getCursorLocation() {
    if (spriteManager.isCursorSet()) {
      return spriteManager.getCursorLocation();
    } else {
      return null;
    }
  }

  public void setRenderSprites(boolean renderSprites) {
    this.renderSprites = renderSprites;
  }

  public boolean isRenderingSprites() {
    return renderSprites;
  }
}

