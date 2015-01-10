package com.customwars.client.ui.renderer;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.map.Direction;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.Map;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class MapEffectsRenderer {
  // Control
  private boolean renderArrowPath = true;
  private boolean renderArrowHead = true;

  // Data
  private final Map map;
  private final int tileSize;
  private Unit activeUnit;
  private Collection<Location> explosionArea;
  private Collection<Location> dropLocations;
  private Location transportLocation;
  private Collection<Location> moveZone;
  private Collection<Location> attackZone;
  private UnitMovePath unitMovePath;
  private boolean mustRebuildPath;

  // View
  private final MapRenderer mapRenderer;
  private Animation explosionAnim;
  private Animation moveZoneAnim;
  private Animation attackZoneAnim;
  private ImageStrip arrowImages;

  public MapEffectsRenderer(MapRenderer mapRenderer, Map map) {
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

    if (renderArrowPath && activeUnit != null && unitMovePath != null) {
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
   * The user selects the locations to be included in the path.
   * If the path exceeds the maximum length the map generates the shortest path to the destination.
   *
   * @see UnitMovePath
   */
  private void renderArrowPath(Graphics g) {
    List<Direction> directionPath = unitMovePath.getDirectionsPath();
    renderArrowPath(g, directionPath);
  }

  /**
   * To render an arrow we need a current and next direction
   * Read 1 Direction ahead, When the loop is at the last item in the path we render an arrowHead
   * The arrowHead only needs 1 direction(the arrow base)
   * <p/>
   * nextLocation starts at the unit location and is set to the next location in the path in each loop
   */
  private void renderArrowPath(Graphics g, List<Direction> directionPath) {
    Location nextLocation = activeUnit.getLocation();

    for (int i = 0; i < directionPath.size(); i++) {
      Direction currentDirection = directionPath.get(i);
      Direction nextDirection = i + 1 < directionPath.size() ? directionPath.get(i + 1) : null;

      // Get the next location to render the arrow on.
      nextLocation = map.getRelativeTile(nextLocation, currentDirection);

      if (renderArrowHead && i == directionPath.size() - 1) {
        renderArrowHead(g, currentDirection, nextLocation);
      } else {
        renderArrow(g, currentDirection, nextDirection, nextLocation);
      }
    }
  }

  private void renderArrow(Graphics g, Direction previousDirection, Direction nextDirection, Location location) {
    if (location != null) {
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
        else if (previousDirection == Direction.SOUTH) {
          g.drawImage(arrowImages.getSubImage(4), x, y);
        } else {
          g.drawImage(arrowImages.getSubImage(0), x, y);
        }
      }
    }
  }

  private void renderArrowHead(Graphics g, Direction baseDirection, Location location) {
    if (location != null) {
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
  }

  //----------------------------------------------------------------------------
  // Zones & Arrows
  //----------------------------------------------------------------------------

  public void createMovePath() {
    int maxMovement = activeUnit.getStats().getMovement();
    unitMovePath = new UnitMovePath(map, maxMovement);
  }

  public void cursorMoved(Location oldLocation, Location newLocation, Direction moveDirection) {
    if (unitMovePath != null) {
      boolean adjacent = TileMap.isAdjacent(oldLocation, newLocation);
      boolean inMoveZone = activeUnit.getMoveZone().contains(newLocation);

      if (!adjacent || !inMoveZone) {
        mustRebuildPath = true;
      } else {
        if (unitMovePath.canAddDirection(activeUnit, moveDirection, newLocation)) {
          if (mustRebuildPath) {
            unitMovePath.createShortestPath(activeUnit, newLocation);
            mustRebuildPath = false;
          } else {
            unitMovePath.addDirection(activeUnit, moveDirection, newLocation);
          }
        }
      }
    }
  }

  public void removeZones() {
    removeMoveZone();
    removeAttackZone();
  }

  /**
   * Show the attack zone for the active unit
   */
  public void showAttackZone() {
    this.attackZone = activeUnit != null ? activeUnit.getAttackZone() : null;
  }

  public void removeAttackZone() {
    this.attackZone = null;
  }

  /**
   * Show the move zone for the active unit
   */
  public void showMoveZone() {
    this.moveZone = activeUnit != null ? activeUnit.getMoveZone() : null;
  }

  public void removeMoveZone() {
    this.moveZone = null;
  }

  public void setMoveZone(Collection<Location> moveZone) {
    this.moveZone = moveZone;
  }

  public void setAttackZone(Collection<Location> attackZone) {
    this.attackZone = attackZone;
  }

  public void setDropLocations(List<Location> dropLocations, Location transportLocation) {
    this.dropLocations = dropLocations;
    this.transportLocation = transportLocation;
  }

  public void setExplosionArea(Collection<Location> explosionArea) {
    if (explosionAnim.isStopped()) {
      explosionAnim.restart();
    }
    this.explosionArea = explosionArea;
  }

  public void setRenderArrowPath(boolean renderArrowPath) {
    this.renderArrowPath = renderArrowPath;

    if (!renderArrowPath) {
      unitMovePath = null;
    }
  }

  public void setRenderArrowHead(boolean renderArrowHead) {
    this.renderArrowHead = renderArrowHead;
  }

  public void setActiveUnit(Unit activeUnit) {
    this.activeUnit = activeUnit;
  }

  /**
   * @return The user chosen move path or an empty path
   */
  public List<Location> getUnitMovePath() {
    if (unitMovePath != null) {
      List<Direction> directionPath = unitMovePath.getDirectionsPath();
      List<Location> movePath = convertDirectionsToTiles(directionPath);
      return movePath;
    } else {
      return Collections.emptyList();
    }
  }

  private List<Location> convertDirectionsToTiles(List<Direction> directionPath) {
    List<Location> locations = new ArrayList<Location>();

    Location base = activeUnit.getLocation();
    locations.add(base);

    for (Direction direction : directionPath) {
      Location nextLocation = map.getRelativeTile(base, direction);
      locations.add(nextLocation);
      base = nextLocation;
    }

    return locations;
  }
}
