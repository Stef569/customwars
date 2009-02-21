package test.slick;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.awt.AwtImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.io.loading.ImageFilterParser;
import com.customwars.client.ui.state.CWInput;
import com.customwars.client.ui.state.CWState;
import com.customwars.client.ui.state.StateLogic;
import org.newdawn.slick.Animation;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.command.Command;
import org.newdawn.slick.loading.DeferredResource;
import org.newdawn.slick.loading.LoadingList;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;
import tools.ColorUtil;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

/**
 * Tests:
 * Loading+storing of awt images
 * Recoloring awt images
 * converting to slick images
 * Deferred loading of the above+Loading bar that fits to the screen
 */
public class RecolorTest extends CWState {
  private static final String IMAGE_FILTER_FILE = "res/data/colors.xml";
  private static final String UNIT_IMG_FILTER = "unit";
  private static final String UNIT_IMG_PREFIX = "UNIT_";

  private int screenWidth;
  private Animation unitAnimRight;

  // Images
  private AwtImageLib awtImgLib;
  private ImageLib imageLib;
  private ImageStrip imageStrip;
  private SpriteSheet currentUnitImgStrip;
  private Image currentUnitImg;

  // Deferred loading
  private DeferredResource nextResource;
  private boolean loadingComplete;

  // Recoloring
  private static Color[] colors;
  private int currentColorPos = 0;

  // Test parameters
  private static final boolean DEFERRED_LOADING = true;

  public RecolorTest(CWInput cwInput, StateLogic statelogic) {
    super(cwInput, statelogic);
  }

  public void init(GameContainer container, StateBasedGame game) throws SlickException {
    LoadingList.setDeferredLoading(DEFERRED_LOADING);
    screenWidth = container.getWidth();
    awtImgLib = new AwtImageLib();
    imageLib = new ImageLib(awtImgLib);

    // Load the image filters, they provide the data needed to recolor images
    // Store the colors they support in colors[]
    loadImgFilters(awtImgLib);
  }

  /**
   * on each state enter attempt to load the images.
   * since images are cached each method returns if the image has already been loaded.
   */
  public void enter(GameContainer container, StateBasedGame game) throws SlickException {
    // Load awt images and recolor them
    loadAwtImages(awtImgLib);
    recolorAwtImages(awtImgLib);

    // Create Slick images from the awt Images
    loadSlickImages();

    // Retrieve some slick images
    imageStrip = imageLib.getSlickImgStrip(UNIT_IMG_PREFIX + "AS_STRIP");
    currentUnitImgStrip = imageLib.getSlickSpriteSheet(UNIT_IMG_PREFIX + "AS_SPRITESHEET");

    // Build unit animation
    if (!DEFERRED_LOADING) {
      Image[] unitRightImages = new Image[]{
              imageStrip.getSubImage(3), imageStrip.getSubImage(4), imageStrip.getSubImage(5)
      };
      unitAnimRight = new Animation(unitRightImages, 350);
    }
  }

  private void loadImgFilters(AwtImageLib awtImgLib) {
    ImageFilterParser imgFilterParser = new ImageFilterParser();
    InputStream in = ResourceLoader.getResourceAsStream(IMAGE_FILTER_FILE);
    try {
      imgFilterParser.loadConfigFile(in);
    } catch (IOException e) {
      Log.error("Could not load img filter", e);
    }
    awtImgLib.buildColorsFromImgFilters();
    colors = awtImgLib.getSupportedColors().toArray(new Color[0]);
  }

  private void loadAwtImages(AwtImageLib awtImgLib) {
    awtImgLib.loadImg("res/image/cliff.gif", "cliff");
    awtImgLib.loadImg("res/image/numbers.png", "numbers");
    awtImgLib.loadImg("res/image/white.png", "white");
    awtImgLib.loadImg("res/image/awTerrains.png", "terains");
    awtImgLib.loadImg("res/image/units_RED.png", UNIT_IMG_PREFIX + "RED");
  }

  private void recolorAwtImages(AwtImageLib awtImgLib) {
    Color baseColor = awtImgLib.getBaseColor(UNIT_IMG_FILTER);
    for (Color color : colors) {
      String baseImgName = UNIT_IMG_PREFIX + ColorUtil.toString(baseColor);
      String storeImgName = UNIT_IMG_PREFIX + ColorUtil.toString(color);
      awtImgLib.recolorImg(color, UNIT_IMG_FILTER, baseImgName, storeImgName, 10);
    }
  }

  private void loadSlickImages() {
    for (Color color : colors) {
      imageLib.loadSlickImage(UNIT_IMG_PREFIX + ColorUtil.toString(color));
    }
    imageLib.loadSlickImageStrip(UNIT_IMG_PREFIX + "AS_STRIP", UNIT_IMG_PREFIX + "RED", 32, 42);
    imageLib.loadSlickSpriteSheet(UNIT_IMG_PREFIX + "AS_SPRITESHEET", UNIT_IMG_PREFIX + "RED", 32, 42);
  }

  public void update(GameContainer container, StateBasedGame game, int delta) throws SlickException {
    updateLoadingProgress();
  }

  private void updateLoadingProgress() throws SlickException {
    if (nextResource != null) {
      try {
        nextResource.load();
        // slow down loading for example purposes
//        try {
//          Thread.sleep(500);
//        } catch (Exception e) {
//        }
      } catch (IOException e) {
        throw new SlickException("Failed to load: " + nextResource.getDescription(), e);
      }

      nextResource = null;
    }

    if (LoadingList.get().getRemainingResources() > 0) {
      nextResource = LoadingList.get().getNext();
    } else {
      if (!loadingComplete) {
        loadingComplete = true;
      }
    }
  }

  public void render(GameContainer container, StateBasedGame game, Graphics g) throws SlickException {
    if (loadingComplete) {
      g.setColor(org.newdawn.slick.Color.white);
      g.drawString("LOADING COMPLETED   scroll to see what has been loaded", 100, 50);

      Color currentColor = colors[currentColorPos];
      g.setColor(new org.newdawn.slick.Color(currentColor.getRGB()));
      g.drawString(ColorUtil.toString(currentColor), 50, 70);

      if (currentUnitImg == null) {
        currentUnitImgStrip.getSubImage(0, 1).draw(100, 100);
        currentUnitImgStrip.getSubImage(1, 1).draw(100, 140);
        currentUnitImgStrip.getSubImage(1, 2).draw(100, 180);
        imageStrip.getSubImage(5).draw(150, 100);
        if (unitAnimRight != null) unitAnimRight.draw(140, 160);
      } else {
        g.drawImage(currentUnitImg, 100, 100);
      }
    } else {
      renderLoadingProgress(g);
    }
  }

  private void renderLoadingProgress(Graphics g) {
    if (LoadingList.isDeferredLoading()) {
      if (nextResource != null) {
        g.drawString("Loading: " + nextResource.getDescription(), 100, 100);
      }

      int total = LoadingList.get().getTotalResources();
      int loaded = LoadingList.get().getTotalResources() - LoadingList.get().getRemainingResources();
      float totalPx = screenWidth - 30;
      float loadedPX = loaded / (float) total;  // 0 .. 1

      g.fillRect(10, 150, loadedPX * totalPx, 20);
      g.drawRect(10, 150, totalPx, 20);
    } else {
      g.drawString("Loading...", 100, 100);
    }
  }

  public void mouseWheelMoved(int newValue) {
    recolor(currentColorPos -= (newValue / 120));
  }

  private void recolor(int i) {
    if (i >= colors.length) {
      i = 0;
    }

    if (i < 0) {
      i = colors.length - 1;
    }

    currentColorPos = i;
    recolor();
  }

  private void recolor() {
    Color color = colors[currentColorPos];
    currentUnitImg = imageLib.getSlickImg(UNIT_IMG_PREFIX + ColorUtil.toString(color));
  }

  public void controlPressed(Command command, CWInput cwInput) {
  }

  public int getID() {
    return 3;
  }
}