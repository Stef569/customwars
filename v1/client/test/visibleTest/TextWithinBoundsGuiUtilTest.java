package visibleTest;

import com.customwars.util.GuiUtil;

import javax.swing.*;
import java.awt.*;

/**
 * All strings are ment to fit the black square perfectly.
 * Regardless of the length of the string and Font used
 *
 * @author stefan
 */
public class TextWithinBoundsGuiUtilTest extends JPanel {
  // Layout
  private static final int LEFT_SPACING = 0;
  private static final int MAX_SQUARE_HEIGHT = 500;

  // Test Vars
  private static final int MAX_SQUARE_WIDTH = 200;
  private static final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 25);
  private static final String TEST_STRING1 = "abcdefghijklmnopqrstraaaabbbbbbbbbaaaaaaaCCCCCCCCCaaaaaaa";
  private static final String TEST_STRING2 = "multi line1 line2 line3 line4 line5 line6 line7 line8 line9 demonstration";
    private static final String TEST_STRING3 = "                                                              hmmmm";
  private static final String TEST_STRING4 = "1 2 3 4 5 6 7 8 9";
  private static final String TEST_STING_5 = "abc ";
  private static final String TEST_STRING7 = "123456789123456789";

  protected void paintComponent(Graphics g) {
    super.paintComponent(g);
    setupTestEnvironMent(g);
    testFitLine(g);
    testMultiLine1(g);
  }

  private void setupTestEnvironMent(Graphics g) {
    g.fillRect(0, 0, MAX_SQUARE_WIDTH, MAX_SQUARE_HEIGHT);
    g.setColor(Color.RED);
    g.setFont(font);
  }

  private void testFitLine(Graphics g) {
    final int TOP_SPACING = 30;
    String fittingLineTxt = GuiUtil.fitLine(TEST_STRING7, MAX_SQUARE_WIDTH, g);
    g.drawString(fittingLineTxt, LEFT_SPACING, TOP_SPACING);
  }

  private void testMultiLine1(Graphics g) {
    final int TOP_SPACING = 50;
    FontMetrics metrics = g.getFontMetrics();
    String[] multiLine = GuiUtil.convertToMultiLineArray(TEST_STRING2, MAX_SQUARE_WIDTH, g);

    int row = 0;
    int height = metrics.getHeight();
    for (String line : multiLine) {
      row++;
      g.drawString(line, LEFT_SPACING, TOP_SPACING + (row * height));
    }
  }

  public static void main(String[] args) {
    final int SOME_SPACING = 25;
    JFrame frame = new JFrame();
    frame.add(new TextWithinBoundsGuiUtilTest());
    frame.setBounds(250, 250, MAX_SQUARE_WIDTH + SOME_SPACING, MAX_SQUARE_HEIGHT + SOME_SPACING);
    frame.setVisible(true);
  }
}
