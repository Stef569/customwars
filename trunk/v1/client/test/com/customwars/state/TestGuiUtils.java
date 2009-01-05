package com.customwars.state;

import com.customwars.util.GuiUtil;
import junit.framework.Assert;
import org.junit.Before;
import org.junit.Test;

import javax.swing.*;
import java.awt.*;

/**
 * uses JUnit annotations because I needed '@Test(expected = NullPointerException.class)'
 * a random test string is chosen from TEST_STRINGS on each run stored into testString
 *
 * @author stefan
 */
public class TestGuiUtils {
  private static Graphics g;

  // Test Vars
  private static final int MAX_TXT_WIDTH = 200;
  private static final Font font = new Font(Font.SANS_SERIF, Font.BOLD, 25);

  // Test Data
  private static final String TEST_STRING1 = "abcdefghijklmnopqrstraaaabbbbbbbbbaaaaaaaCCCCCCCCCaaaaaaa";
  private static final String TEST_STRING2 = "multi line1 line2 line3 line4 line5 line6 line7 line8 line9 demonstration";
  private static final String TEST_STRING3 = "                                                              hmmmm";
  private static final String TEST_STRING4 = "";
  private static final String TEST_STRING5 = "1 2 3 4 5 6 7 8 9";
  private static final String TEST_STRING6 = "1 2 3 4 5 6 7 89";
  private static final String TEST_STRING7 = "12345678912346789";
  private static final String[] TEST_STRINGS = {TEST_STRING1, TEST_STRING2, TEST_STRING3, TEST_STRING4, TEST_STRING5, TEST_STRING6, TEST_STRING7};
  private String testString;

  @Before
  public void setUp() {
    JFrame frame = new JFrame();
    frame.setVisible(true);
    g = frame.getGraphics();
    g.setFont(font);
    testString = getTestString();
  }

  @Test
  public void testFitLine() {
    String fittingLineTxt = GuiUtil.fitLine(testString, MAX_TXT_WIDTH, g);
    Assert.assertFalse("test txt:" + testString, fittingLineTxt == null);

    int lineWidth = GuiUtil.getStringWidth(fittingLineTxt, g);
    Assert.assertTrue("test txt:" + testString, fitsLine(lineWidth, MAX_TXT_WIDTH));
  }

  @Test(expected = NullPointerException.class)
  public void testFitLineWithNull() {
    GuiUtil.fitLine(null, MAX_TXT_WIDTH, g);
  }

  @Test(expected = NullPointerException.class)
  public void testMultiLineWithNull() {
    GuiUtil.convertToMultiLine(null, MAX_TXT_WIDTH, g);
  }

  @Test
  public void testMultiLine() {
    for (String line : GuiUtil.convertToMultiLineArray(testString, MAX_TXT_WIDTH, g)) {
      Assert.assertFalse("test txt:" + testString, line == null);
      int lineWidth = GuiUtil.getStringWidth(line, g);
      Assert.assertTrue("test txt:" + testString, fitsLine(lineWidth, MAX_TXT_WIDTH));
    }
  }

  private boolean fitsLine(int lineWidth, int maxLineWidth) {
    return lineWidth <= maxLineWidth;
  }

  private String getTestString() {
    int rand = (int) (Math.random() * TEST_STRINGS.length);
    return TEST_STRINGS[rand];
  }
}
