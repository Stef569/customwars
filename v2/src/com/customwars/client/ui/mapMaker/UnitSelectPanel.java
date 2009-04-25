package com.customwars.client.ui.mapMaker;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.model.gameobject.Unit;
import com.customwars.client.model.gameobject.UnitFactory;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Color;

/**
 * Allows a unit to be selected from all the units available in the game
 * Renders a list of available units looking right
 */
public class UnitSelectPanel extends SelectPanel {
  private SpriteSheet unitSpriteSheet;
  private ResourceManager resources;

  public UnitSelectPanel(GUIContext container) {
    super(container);
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  public void recolor(Color c) {
    unitSpriteSheet = resources.getUnitSpriteSheet(c);
    buildComponent();
  }

  private void buildComponent() {
    clear();
    for (Unit unit : UnitFactory.getAllUnits()) {
      Image unitImg = unitSpriteSheet.getSubImage(4, unit.getID());
      add(unitImg);
    }

    // Init makes sure that getHeight() returns the correct height.
    init();
    setLocation(0, container.getHeight() - getHeight());
  }
}