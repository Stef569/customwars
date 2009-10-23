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
 * mouse controls can't be tested since they require a display.
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
    properties.put(UserConfigParser.INPUT_PREFIX + ".SELECT", "B,C,D,E,F,ESCAPE");
    properties.put(UserConfigParser.INPUT_PREFIX + ".Toggle_Music", "1");

    userConfigParser.readInputConfig(properties);
    List commands = inputProvider.getUniqueCommands();
    Assert.assertFalse(commands.contains(null));
    Assert.assertEquals(3, commands.size());
    Assert.assertEquals(5, inputProvider.getControlsFor(inputProvider.CANCEL).size());
    Assert.assertEquals(6, inputProvider.getControlsFor(inputProvider.SELECT).size());
    Assert.assertEquals(1, inputProvider.getControlsFor(inputProvider.TOGGLE_MUSIC).size());
    Assert.assertEquals(0, inputProvider.getControlsFor(inputProvider.ZOOM_IN).size());
  }

  @Test(expected = IllegalArgumentException.class)
  public void testReadingDuplicateKeyInput() {
    Properties properties = new Properties();
    // Dilema 2 keys binded to 2 different commands
    // Throws an exception
    properties.put(UserConfigParser.INPUT_PREFIX + ".CANCEL", "A");
    properties.put(UserConfigParser.INPUT_PREFIX + ".SELECT", "A");
    userConfigParser.readInputConfig(properties);

    Assert.assertEquals(1, inputProvider.getControlsFor(inputProvider.CANCEL).size());
    Assert.assertEquals(0, inputProvider.getControlsFor(inputProvider.SELECT).size());
  }

  @Test()
  public void testReadingDuplicateCommands() {
    Properties properties = new Properties();
    // 2 commands, the 2nd command overwrites the first command
    // Only B is used as control for CANCEL
    properties.put(UserConfigParser.INPUT_PREFIX + ".CANCEL", "A");
    properties.put(UserConfigParser.INPUT_PREFIX + ".CANCEL", "B");
    userConfigParser.readInputConfig(properties);
  }
}
