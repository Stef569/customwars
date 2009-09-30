package com.customwars.client.ui.mapMaker;

import com.customwars.client.io.ResourceManager;
import com.customwars.client.model.gameobject.Terrain;
import com.customwars.client.model.gameobject.TerrainFactory;
import org.newdawn.slick.Image;
import org.newdawn.slick.gui.GUIContext;

import java.awt.Color;

public class TerrainSelectPanel extends SelectPanel {
  private ResourceManager resources;
  private boolean buildComponentCompleted;

  public TerrainSelectPanel(GUIContext container) {
    super(container);
  }

  @Override
  public void loadResources(ResourceManager resources) {
    this.resources = resources;
  }

  @Override
  public void recolor(Color color) {
    buildComponent();
    buildComponentCompleted = true;
  }

  public void buildComponent() {
    if (buildComponentCompleted) return;

    clear();
    for (Terrain terrain : TerrainFactory.getBaseTerrains()) {
      Image terrainImg = resources.getSlickImgStrip("terrains").getSubImage(terrain.getID());
      add(terrainImg);
    }

    // Init makes sure that getHeight() returns the correct height.
    init();
    setLocation(0, container.getHeight() - getHeight());
  }
}
