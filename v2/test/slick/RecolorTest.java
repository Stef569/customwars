package slick;

import com.customwars.client.io.img.ImageLib;
import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.io.img.slick.SpriteSheet;
import com.customwars.client.io.loading.ImageFilterParser;
import com.customwars.client.tools.ColorUtil;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.BasicGame;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.util.Log;
import org.newdawn.slick.util.ResourceLoader;

import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;

/**
 * Tests:
 * Loading+storing of awt images
 * Recoloring awt images
 * converting to slick images
 */
public class RecolorTest extends BasicGame {
  private static final String IMAGE_FILTER_FILE = "res/plugin/default/data/colors.xml";
  private static final String UNIT_IMG = "res/plugin/default/images/units_RED.png";
  private static final String CITY_IMG = "res/plugin/default/images/cities_RED.png";
  private static final String UNIT_IMG_FILTER = "unit";
  private static final String UNIT_IMG_PREFIX = "UNIT_";

  // Images
  private ImageLib imageLib;
  private ImageStrip imageStrip;
  private SpriteSheet currentUnitImgStrip;
  private Image currentUnitImg;
  private Image currentCityImg;

  // Recoloring
  private static Color[] colors;
  private int currentColorPos = 0;
  private boolean darker;

  public RecolorTest() {
    super("recolor test");
  }

  public void init(GameContainer container) throws SlickException {
    imageLib = new ImageLib();

    // Load the image filters, they provide the data needed to recolor images
    // Store the colors they support in colors[]
    loadImgFilters();
    load();
  }

  private void loadImgFilters() {
    ImageFilterParser imgFilterParser = new ImageFilterParser(imageLib);
    InputStream in = ResourceLoader.getResourceAsStream(IMAGE_FILTER_FILE);
    try {
      imgFilterParser.loadConfigFile(in);
    } catch (IOException e) {
      Log.error("Could not load img filter", e);
    }
    imageLib.buildColorsFromImgFilters();
    colors = imageLib.getSupportedColors().toArray(new Color[0]);
  }

  public void load() {
    // Load unit awt image
    imageLib.loadAwtImg(UNIT_IMG_PREFIX + "RED", UNIT_IMG);
    imageLib.loadSlickSpriteSheet("UNIT_RED", 32, 40);

    // Load City awt image
    imageLib.loadAwtImg("CITY_RED", CITY_IMG);
    imageLib.loadSlickSpriteSheet("CITY_RED", 32, 40);

    recolorImages();

    // Create Slick images from the recolored awt Images
    loadSlickImages();

    // Retrieve some slick images
    imageStrip = imageLib.getSlickImgStrip(UNIT_IMG_PREFIX + "AS_STRIP");
    currentUnitImgStrip = imageLib.getSlickSpriteSheet(UNIT_IMG_PREFIX + "AS_SPRITESHEET");
  }

  private void recolorImages() {
    for (Color color : colors) {
      imageLib.recolorImg(color, "unit", "", UNIT_IMG_FILTER, 0);
      imageLib.recolorImg(color, "city", "", "city", 0);
    }
    for (Color color : colors) {
      imageLib.recolorImg(color, "unit", "darker", UNIT_IMG_FILTER, 60);
      imageLib.recolorImg(color, "city", "", "city", 60);
    }
  }

  private void loadSlickImages() {
    imageLib.loadSlickImageStrip(UNIT_IMG_PREFIX + "AS_STRIP", UNIT_IMG_PREFIX + "RED", 32, 42);
    imageLib.loadSlickSpriteSheet(UNIT_IMG_PREFIX + "AS_SPRITESHEET", UNIT_IMG_PREFIX + "RED", 32, 42);
  }

  public void update(GameContainer container, int delta) throws SlickException {
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.setColor(org.newdawn.slick.Color.white);
    g.drawString("LOADING COMPLETED   scroll to see what has been loaded", 100, 50);

    Color currentColor = colors[currentColorPos];
    g.setColor(new org.newdawn.slick.Color(currentColor.getRGB()));
    String darkTxt = darker ? "Darker " : "";
    g.drawString(darkTxt + ColorUtil.toString(currentColor), 50, 70);

    if (currentUnitImg == null) {
      currentUnitImgStrip.getSubImage(0, 1).draw(100, 100);
      currentUnitImgStrip.getSubImage(1, 1).draw(100, 140);
      currentUnitImgStrip.getSubImage(1, 2).draw(100, 180);
      imageStrip.getSubImage(5).draw(150, 100);
    } else {
      g.drawImage(currentUnitImg, 100, 100);
      if (currentCityImg != null) g.drawImage(currentCityImg, 500, 100);
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
    this.darker = !darker;
    if (darker) {
      currentUnitImg = imageLib.getSlickImg(UNIT_IMG_PREFIX + ColorUtil.toString(color) + "_darker");
      currentCityImg = imageLib.getSlickImg("city_" + ColorUtil.toString(color));
    } else {
      currentUnitImg = imageLib.getSlickImg(UNIT_IMG_PREFIX + ColorUtil.toString(color));
      currentCityImg = imageLib.getSlickImg("city_" + ColorUtil.toString(color));
    }
  }

  public static void main(String[] args) throws SlickException {
    AppGameContainer appGameContainer = new AppGameContainer(new RecolorTest());
    appGameContainer.setDisplayMode(800, 600, false);
    appGameContainer.setTargetFrameRate(60);
    appGameContainer.start();
  }
}