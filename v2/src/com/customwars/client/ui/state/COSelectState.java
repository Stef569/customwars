package com.customwars.client.ui.state;

import com.customwars.client.io.img.slick.ImageStrip;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.co.CO;
import com.customwars.client.model.co.COFactory;
import com.customwars.client.model.co.COStyle;
import com.customwars.client.tools.Args;
import com.customwars.client.tools.FontUtil;
import com.customwars.client.ui.COSheet;
import com.customwars.client.ui.renderer.LineRenderer;
import com.customwars.client.ui.state.input.CWCommand;
import com.customwars.client.ui.state.input.CWInput;
import org.newdawn.slick.Color;
import org.newdawn.slick.Font;
import org.newdawn.slick.GameContainer;
import org.newdawn.slick.Graphics;
import org.newdawn.slick.Image;
import org.newdawn.slick.Input;
import org.newdawn.slick.SlickException;
import org.newdawn.slick.state.StateBasedGame;

/**
 * Allows the user to select a co. When a CO is selected(clicked on):
 * The selected CO is put into the stateSession and the user is returned to the previous state.
 * Graphic credits goto the www.customwars.com project.
 */
public class COSelectState extends CWState {
  private static final int TAB_LEFT_MARGIN = 2;
  private static final int TAB_OFF_SCREEN_MARGIN = 12;
  private static final int LEFT_MARGIN = 2;
  private static final int TOP_MARGIN = 5;
  private static final int COLS = 3, ROWS = 5;
  private static final int CO_BOX_SIZE = 48;
  private static final int CO_BOX_MARGIN = 4;
  private static final int SQUARE_SIZE = CO_BOX_SIZE + CO_BOX_MARGIN;
  private static final int INTEL_BOX_WIDTH = 170;
  private static final CO DUMMY_CO = new BasicCO("dummy");

  private Image background;
  private Image coBanner;
  private Image layout, layoutOverlay;
  private ImageStrip coStyleTabImages;
  private LineRenderer intelRenderer;
  private Input input;

  private CO[][] coByStyle;   // COStyle ID - CO

  private COStyle currentCOStyle;
  private int currentCOStyleIndex;
  private int currentCOIndex;
  private Font intelFont;

  public void init(GameContainer gameContainer, StateBasedGame stateBasedGame) throws SlickException {
    this.input = gameContainer.getInput();
    loadImages();
    intelFont = resources.getFont("default");
    initIntelRenderer();
    fillCoStyleArray();
    selectCoStyle(0);
  }

  private void loadImages() {
    background = resources.getSlickImg("light_menu_background");
    layout = resources.getSlickImg("co_background");
    layoutOverlay = resources.getSlickImg("co_background2");
    coBanner = resources.getSlickImg("co_banner");
    coStyleTabImages = resources.getSlickImgStrip("co_style_tabs");
  }

  private void initIntelRenderer() {
    intelRenderer = new LineRenderer(intelFont);
    intelRenderer.setBackgroundColor(Color.white);
    intelRenderer.setTextColor(Color.black);
    intelRenderer.setLocation(170, 70);
    intelRenderer.setMaxSize(INTEL_BOX_WIDTH, 155);
    intelRenderer.setOverflow(LineRenderer.OVERFLOW.HIDDEN);
  }

  private void fillCoStyleArray() {
    coByStyle = new CO[COFactory.getCOStyleCount()][COLS * ROWS];

    for (COStyle coStyle : COFactory.getAllCOStyles()) {
      int col = 0;
      for (CO co : COFactory.getAllCOS()) {
        if (co.getStyle().equals(coStyle)) {
          coByStyle[co.getStyle().getID()][col++] = co;
        }
      }
    }
  }

  public void render(GameContainer container, Graphics g) throws SlickException {
    g.drawImage(background, 0, 0);
    g.drawImage(layout, 0, coStyleTabImages.getHeight());
    g.drawImage(layoutOverlay, 0, coStyleTabImages.getHeight(), getCoColor());
    g.drawImage(coBanner, 0, 1);
    g.drawImage(coStyleTabImages, TAB_LEFT_MARGIN, -TAB_OFF_SCREEN_MARGIN);

    Color origColor = g.getColor();
    renderCurrentTab(g);
    renderCoFrameWork(g);
    g.setColor(origColor);
  }

  @Override
  public void update(GameContainer container, int delta) throws SlickException {
  }

  private void renderCurrentTab(Graphics g) {
    Image coStyleTabImg = coStyleTabImages.getSubImage(currentCOStyleIndex);
    int x = currentCOStyleIndex * coStyleTabImg.getWidth();
    g.drawImage(coStyleTabImg, TAB_LEFT_MARGIN + x, 0);
  }

  private void renderCoFrameWork(Graphics g) {
    for (int row = 0; row < ROWS; row++) {
      for (int col = 0; col < COLS; col++) {
        int x = LEFT_MARGIN + col * (CO_BOX_SIZE + CO_BOX_MARGIN);
        int y = coStyleTabImages.getHeight() + TOP_MARGIN + CO_BOX_MARGIN + row * (CO_BOX_SIZE + CO_BOX_MARGIN);
        int index = col + row * COLS;
        renderCOSquare(g, x, y, index);
      }
    }
  }

  private void renderSquare(Graphics g, int x, int y) {
    Color coColor = getCoColor();
    g.setColor(coColor);
    g.drawRect(x, y, CO_BOX_SIZE, CO_BOX_SIZE);

    if (isMouseInSquare(x, y)) {
      g.drawRect(x - 1, y - 1, CO_BOX_SIZE + 2, CO_BOX_SIZE + 2);
    }
  }

  private void renderCOSquare(Graphics g, int x, int y, int index) {
    renderSquare(g, x, y);
    renderCO(g, x, y, index);
  }

  private Color getCoColor() {
    return new Color(currentCOStyle.getColor().getRGB());
  }

  private boolean isMouseInSquare(int x, int y) {
    int mouseX = input.getMouseX();
    int mouseY = input.getMouseY();

    return
      mouseX > x && mouseX < x + CO_BOX_SIZE + CO_BOX_MARGIN / 2 &&
        mouseY > y && mouseY < y + CO_BOX_SIZE + CO_BOX_MARGIN / 2;
  }

  private void renderCO(Graphics g, int x, int y, int index) {
    CO co = coByStyle[currentCOStyleIndex][index];
    if (co != null) {
      COSheet coSheet = resources.getCOSheet(co);
      g.drawImage(coSheet.getLeftHead(0), x, y);
      intelRenderer.render(g);
    }
  }

  @Override
  public void controlPressed(CWCommand command, CWInput cwInput) {
    switch (command.getEnum()) {
      case SELECT:
        selectCurrentCO();
        break;
      case CANCEL:
      case EXIT:
        stateChanger.changeToPrevious();
        break;
      case DOWN:
        moveDown();
        break;
      case UP:
        moveUp();
        break;
      case LEFT:
        moveLeft();
        break;
      case RIGHT:
        moveRight();
        break;
      case NEXT_PAGE:
        selectNextCoStyle();
        break;
      case PREV_PAGE:
        selectNextCoStyle();
        break;
    }
  }

  private void selectNextCoStyle() {
    int nextCoStyleIndex = Args.getBetweenMinMax(currentCOStyleIndex + 1, 0, COFactory.getCOStyleCount(), 0);
    selectCoStyle(nextCoStyleIndex);
  }

  private void moveUp() {
    this.currentCOIndex -= ROWS;
  }

  private void moveLeft() {
    this.currentCOIndex--;
  }

  private void moveRight() {
    this.currentCOIndex++;
  }

  private void moveDown() {
    this.currentCOIndex += ROWS;
  }

  @Override
  public void mouseClicked(int button, int x, int y, int clickCount) {
    if (isMouseWithinCOFramework(x, y)) {
      selectCurrentCO();
    }
  }

  private boolean isMouseWithinCOFramework(int mouseX, int mouseY) {
    int CO_BOXES_TOP_MARGIN = coStyleTabImages.getHeight() + TOP_MARGIN;
    return mouseX > LEFT_MARGIN && mouseX < LEFT_MARGIN + SQUARE_SIZE * COLS &&
      mouseY > CO_BOXES_TOP_MARGIN && mouseY < CO_BOXES_TOP_MARGIN + SQUARE_SIZE * ROWS;
  }

  private void selectCurrentCO() {
    stateSession.selectedCO = coByStyle[currentCOStyleIndex][currentCOIndex];
    stateChanger.changeToPrevious();
  }

  @Override
  public void mouseMoved(int oldx, int oldy, int newx, int newy) {
    for (int tabImgIndex = 0; tabImgIndex < coStyleTabImages.getCols(); tabImgIndex++) {
      if (isMouseWithinTabImage(newx, newy, tabImgIndex)) {
        selectCoStyle(tabImgIndex);
      }
    }

    int currentSquareIndex = getCOIndex(newx, newy);
    if (currentSquareIndex != -1) {
      this.currentCOIndex = currentSquareIndex;
      createIntel(coByStyle[currentCOStyleIndex][currentCOIndex]);
    } else {
      createIntel(DUMMY_CO);
    }
  }

  private void createIntel(CO co) {
    intelRenderer.clearText();

    if (co != null) {
      String coInfo = co.getIntel().length() == 0 ? co.getBio() : co.getIntel();
      String[] lines = FontUtil.wrapToArray(coInfo, INTEL_BOX_WIDTH, intelFont);
      for (String intelLine : lines) {
        intelRenderer.addText(intelLine);
      }
    }
  }

  private boolean isMouseWithinTabImage(int mouseX, int mouseY, int imgIndex) {
    Image img = coStyleTabImages.getSubImage(imgIndex);
    int xOffset = imgIndex * img.getWidth();
    int x = TAB_LEFT_MARGIN + mouseX;
    int y = TAB_OFF_SCREEN_MARGIN + mouseY;
    return x >= xOffset && x <= img.getWidth() + xOffset && y >= 0 && y <= img.getHeight();
  }

  private void selectCoStyle(int index) {
    if (COFactory.hasCOStyleFor(index)) {
      this.currentCOStyleIndex = index;
      this.currentCOStyle = COFactory.getCOStyle(index);
      // todo CO background image
    }
  }

  /**
   * Get the co index in the table based on the given x y position.
   * The returned index starts at 0.
   *
   * @return The co index in the table, -1 if the x,y position is outside of the table.
   */
  private int getCOIndex(int x, int y) {
    boolean withinSquares =
      x > LEFT_MARGIN && x < SQUARE_SIZE * COLS &&
        y > coStyleTabImages.getHeight() && y < SQUARE_SIZE * ROWS;

    if (withinSquares) {
      int col = (x - LEFT_MARGIN) / SQUARE_SIZE;
      int row = (y - coStyleTabImages.getHeight()) / SQUARE_SIZE;
      return col + row * COLS;
    } else {
      return -1;
    }
  }

  public int getID() {
    return 12;
  }
}
