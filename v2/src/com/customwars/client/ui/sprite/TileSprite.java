package com.customwars.client.ui.sprite;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.gameobject.Locatable;
import com.customwars.client.model.map.Location;
import com.customwars.client.model.map.TileMap;
import org.newdawn.slick.Animation;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;

/**
 * TiledSprite extends Sprite adding a tile Location.
 * it will handle animations that don't fit to a tile by
 * #1 translating the Image frame up or
 * #2 centering the Image frame
 * A TiledSprite moves from one Location to another. It cannot move out of the map bounds
 */
public class TileSprite extends Sprite implements Locatable {
  private TileMap map;
  private int tileSize;
  private Location location;
  private int frameHeightOffset;
  private int frameWidthOffset;
  private boolean renderInCenter;

  public TileSprite(Location location, TileMap map) {
    super(null);
    setMap(map);
    setLocation(location);
  }

  public TileSprite(int x, int y, Location location, TileMap map) {
    super(x, y);
    this.location = location;
    setMap(map);
  }

  public TileSprite(Animation anim, Location location, TileMap map) {
    super(anim);
    this.location = location;
    setMap(map);
  }

  public TileSprite(Image img, Location location, TileMap map) {
    super(img, 0, 0);
    setMap(map);
    setLocation(location);
  }

  public TileSprite(ImageStrip imageStrip, int delay, Location location, TileMap map) {
    super(null, 0, 0);
    setMap(map);
    setLocation(location);
    anim = new Animation(imageStrip.toArray(), delay);
  }

  public void render(Graphics g) {
    if (super.canRenderAnim(g)) {
      translateOffset(g, renderInCenter);
      super.render(g);
      undoTranslateOffset(g);
    }
  }

  /**
   * Calculate and store the image offsets, translate the graphics
   *
   * @param center True -> Center on the tile
   *               Default False -> Images that don't fit to the tile are drawn higher,
   *               so that the image base is equal with the tile base
   */
  void translateOffset(Graphics g, boolean center) {
    Image img = anim.getCurrentFrame();
    if (img != null) {
      if (center) {
        frameHeightOffset = (img.getHeight() - tileSize) / 2;
        frameWidthOffset = (img.getWidth() - tileSize) / 2;
      } else {
        frameHeightOffset = img.getHeight() - tileSize;
        frameWidthOffset = img.getWidth() - tileSize;
      }
      g.translate(-frameWidthOffset, -frameHeightOffset);
    }
  }

  /**
   * Undo the translation to the graphics, by using the stored offsets
   */
  void undoTranslateOffset(Graphics g) {
    g.translate(frameWidthOffset, frameHeightOffset);
    frameWidthOffset = 0;
    frameHeightOffset = 0;
  }

  //----------------------------------------------------------------------------
  // SETTERS
  // ---------------------------------------------------------------------------
  private void setMap(TileMap map) {
    this.map = map;
    this.tileSize = map.getTileSize();
  }

  /**
   * Converts newLocation to x,y coordinates and change the position of the sprite
   * if no map is set or newLocation is not valid within the map then the sprite won't move.
   *
   * @param newLocation the location the sprite should move to
   */
  public void setLocation(Location newLocation) {
    if (map != null && map.isValid(newLocation)) {
      this.location = newLocation;
      super.setLocation(newLocation.getCol() * tileSize, newLocation.getRow() * tileSize);
    }
  }

  public void setRenderInCenter(boolean renderInCenter) {
    this.renderInCenter = renderInCenter;
  }

  //----------------------------------------------------------------------------
  // GETTERS
  // ---------------------------------------------------------------------------
  public Location getLocation() {
    return location;
  }
}