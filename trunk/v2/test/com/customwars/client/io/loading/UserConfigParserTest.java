package com.customwars.client.io.loading;

import com.customwars.client.ui.state.CWInput;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.lwjgl.LWJGLException;
import org.newdawn.slick.Input;

import java.util.List;
import java.util.Properties;

/**
 * Test reading user properties
 * starting with keys,
 * mouse btn and mouse clicks can't be tested since they require a display...
 *
 * @author stefan
 */
public class UserConfigParserTest {
  private UserConfigParser userConfigParser;
  private CWInput inputProvider;

  @Before
  public void beforeEachTest() throws LWJGLException {
    inputProvider = new CWInput(new Input(0));
    userConfigParser = new UserConfigParser(inputProvider);
  }

  @Test
  public void testReadingKeyInput() {
    Properties properties = new Properties();
    properties.put(UserConfigParser.INPUT_PREFIX + ".CanCeL", "x,W,a,9,f5");
    properties.put(UserConfigParser.INPUT_PREFIX + ".SELECT", "A,B,C,D,E,F,ESCAPE");
    properties.put(UserConfigParser.INPUT_PREFIX + ".Toggle_Music", "1");

    userConfigParser.readInputConfig(properties);
    List commands = inputProvider.getUniqueCommands();
    Assert.assertFalse(commands.contains(null));
    Assert.assertEquals(3, commands.size());
    Assert.assertEquals(5, inputProvider.getControlsFor(CWInput.cancel).size());
    Assert.assertEquals(6, inputProvider.getControlsFor(CWInput.select).size());
    Assert.assertEquals(1, inputProvider.getControlsFor(CWInput.toggleMusic).size());
    Assert.assertEquals(0, inputProvider.getControlsFor(CWInput.zoomIn).size());
  }

  @Test
  public void testReadingDuplicateKeyInput() {
    Properties properties = new Properties();
    // Dilema 2 keys binded to 2 different commands
    properties.put(UserConfigParser.INPUT_PREFIX + ".CANCEL", "A");
    properties.put(UserConfigParser.INPUT_PREFIX + ".SELECT", "A");            // Ignore duplicate keys
    userConfigParser.readInputConfig(properties);

    Assert.assertEquals(1, inputProvider.getControlsFor(CWInput.cancel).size());
    Assert.assertEquals(0, inputProvider.getControlsFor(CWInput.select).size());
  }
}
