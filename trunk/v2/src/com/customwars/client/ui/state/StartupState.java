package com.customwars.client.ui.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;

import java.io.IOException;

/**
 * State in which resources are loaded
 *
 * @author stefan
 */
public class StartupState extends CWState {
  private DeferredResource nextResource;

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    resources.loadAll();
  }

  public void update(GameContainer container, int delta) throws SlickException {
    if (nextResource != null) {
      try {
        nextResource.load();
      } catch (IOException e) {
        throw new SlickException("Failed to load: " + nextResource.getDescription(), e);
      }
      nextResource = null;
    }

    if (LoadingList.get().getRemainingResources() > 0) {
      nextResource = LoadingList.get().getNext();
    } else {
      changeGameState("IN_GAME");
    }
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    renderLoadingProgress(g, container);
  }

  private void renderLoadingProgress(Graphics g, GameContainer gameContainer) {
    if (LoadingList.isDeferredLoading()) {
      if (nextResource != null) {
        g.drawString("Loading: " + nextResource.getDescription(), 100, 100);
      }

      int total = LoadingList.get().getTotalResources();
      int loaded = LoadingList.get().getTotalResources() - LoadingList.get().getRemainingResources();
      float totalPx = gameContainer.getWidth() - 30;
      float loadedPX = loaded / (float) total;  // 0 .. 1

      g.fillRect(10, 150, loadedPX * totalPx, 20);
      g.drawRect(10, 150, totalPx, 20);
    } else {
      g.drawString("Loading...", 100, 100);
    }
  }

  public int getID() {
    return 0;
  }
}
