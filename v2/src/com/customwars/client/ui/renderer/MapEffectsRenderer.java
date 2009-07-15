package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.Tile;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import java.util.List;

public class MapEffectsRenderer {
  private boolean renderArrows = true;
  private MapRenderer mapRenderer;

  // Data
  private Map<Tile> map;
  private int tileSize;
  private Unit activeUnit;
  private List<Location> explosionArea;
  private List<Location> dropLocations;
  private Location transportLocation;
  private List<Location> moveZone;
  private List<Location> attackZone;

  // View
  private Animation explosionAnim;
  private Animation moveZoneAnim;
  private Animation attackZoneAnim;
  public static ImageStrip arrowImages;

  public MapEffectsRenderer(MapRenderer mapRenderer, Map<Tile> map) {
    this.mapRenderer = mapRenderer;
    this.map = map;
    this.tileSize = map.getTileSize();
  }

  public void loadResources(ResourceManager resources) {
    explosionAnim = resources.getAnim("explosion_LAND");
    moveZoneAnim = resources.getAnim("movezone");
    attackZoneAnim = resources.getAnim("attackzone");
    arrowImages = resources.getSlickImgStrip("arrows");
  }

  public void update(int elapsedTime) {
    if (moveZone != null) {
      moveZoneAnim.update(elapsedTime);
    }
    if (attackZone != null) {
      attackZoneAnim.update(elapsedTime);
    }
    if (explosionArea != null) {
      explosionAnim.update(elapsedTime);
    }
  }

  public void render(Graphics g) {
    renderzones(g);
    renderDropLocations(g);
    renderExplosionArea();
  }

  private void renderzones(Graphics g) {
    if (moveZone != null) {
      for (Location location : moveZone) {
        mapRenderer.renderImgOnTile(g, moveZoneAnim.getCurrentFrame(), location);
      }
    }

    if (attackZone != null) {
      for (Location location : attackZone) {
        mapRenderer.renderImgOnTile(g, attackZoneAnim.getCurrentFrame(), location);
      }
    }

    if (renderArrows && activeUnit != null) {
      renderArrowPath(g);
    }
  }

  private void renderExplosionArea() {
    if (explosionArea != null) {
      if (explosionAnim.isStopped()) {
        explosionArea = null;
      } else {
        for (Location t : explosionArea) {
          explosionAnim.getCurrentFrame().draw(t.getCol() * tileSize, t.getRow() * tileSize);
        }
      }
    }
  }

  private void renderDropLocations(Graphics g) {
    if (dropLocations != null) {
      for (Location tile : dropLocations) {
        if (transportLocation != null) {
          Direction dir = map.getDirectionTo(transportLocation, tile);
          renderArrowHead(g, dir, tile);
        }
      }
    }
  }

  /**
   * Shows a path of arrows from the active unit to the cursor location
   * map generates a path of Directions.
   */
  private void renderArrowPath(Graphics g) {
    Location clicked = mapRenderer.getCursorLocation();
    List<Direction> directionPath = map.getDirectionsPath(activeUnit, clicked);
    renderArrowPath(g, directionPath);
  }

  /**
   * To render an arrow we need a current and next direction
   * Read 1 Direction ahead, When the loop is at the last item in the path we render an arrowHead
   * The arrowHead only needs 1 direction(the arrow base)
   *
   * nextLocation starts at the unit location and is set to the next location in the path in each loop
   */
  public void renderArrowPath(Graphics g, List<Direction> directionPath) {
    Location nextLocation = activeUnit.getLocation();

    for (int i = 0; i < directionPath.size(); i++) {
      Direction currentDirection = directionPath.get(i);
      Direction nextDirection = i + 1 < directionPath.size() ? directionPath.get(i + 1) : null;

      // Get the next location to render the arrow on.
      nextLocation = map.getRelativeTile(nextLocation, currentDirection);

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

  public void renderArrowHead(Graphics g, Direction baseDirection, Location location) {
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

  //----------------------------------------------------------------------------
  // Zones & Arrows
  //----------------------------------------------------------------------------
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

  public void setMoveZone(List<Location> moveZone) {
    this.moveZone = moveZone;
  }

  public void setAttackZone(List<Location> attackZone) {
    this.attackZone = attackZone;
  }

  public void setDropLocations(List<Location> dropLocations, Location transportLocation) {
    this.dropLocations = dropLocations;
    this.transportLocation = transportLocation;
  }

  public void setExplosionArea(List<Location> explosionArea) {
    if (explosionAnim.isStopped()) {
      explosionAnim.restart();
    }
    this.explosionArea = explosionArea;
  }

  public void setRenderArrows(boolean renderArrows) {
    this.renderArrows = renderArrows;
  }

  public void setActiveUnit(Unit activeUnit) {
    this.activeUnit = activeUnit;
  }

  public boolean isRenderingArrows() {
    return renderArrows;
  }
}
