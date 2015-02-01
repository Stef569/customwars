package com.customwars.client.model.ai;

import com.customwars.client.io.loading.ModelLoader;
import com.customwars.client.model.TestData;
import com.customwars.client.model.ai.build.BuildPriority;
import com.customwars.client.model.ai.build.BuildStrategy;
import com.customwars.client.model.ai.build.DefaultBuildAdvisor;
import com.customwars.client.model.ai.fuzzy.Fuz;
import com.customwars.client.model.co.BasicCO;
import com.customwars.client.model.game.Game;
import com.customwars.client.model.game.GameRules;
import com.customwars.client.model.game.Player;
import com.customwars.client.model.game.Turn;
import com.customwars.client.model.gameobject.City;
import com.customwars.client.model.map.Map;
import com.customwars.client.tools.TextMapParser;
import junit.framework.Assert;
import org.junit.AfterClass;
import org.junit.BeforeClass;
import org.junit.Test;

import java.awt.Color;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class UnitAIBuildTest {

  @BeforeClass
  public static void beforeAllTests() throws IOException {
    // Load the data from the test directory into the Factories
    ModelLoader loader = new ModelLoader("testData/testGame/");
    loader.load();
  }

  public Game createGame(Map map) {
    Player redPlayer = new Player(0, Color.RED, "Stef", 6000, 0, true, new BasicCO("penny"));
    Player bluePlayer = new Player(1, Color.BLUE, "JSR", 8000, 1, false, new BasicCO("penny"));
    List<Player> players = Arrays.asList(redPlayer, bluePlayer);

    GameRules gameRules = new GameRules();
    gameRules.setDayLimit(Turn.UNLIMITED);
    gameRules.setCityFunds(1000);

    Game game = new Game(map, players, gameRules);
    game.startGame();
    return game;
  }

  @AfterClass
  public static void afterAllTests() {
    TestData.clearTestData();
  }

  @Test
  public void testFirstTurn() {
    final String[][] simpleMap = new String[][]{
      new String[]{"BASE-P1", "", "CITY-P*", "", "", "", "", "", "", "", ""},
      new String[]{"BASE-P1", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "HQTR-P1", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"CITY-P*", "", "", "", "", "", "", "", "", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "HQTR-P2", "", ""},
      new String[]{"", "", "", "", "", "", "", "", "MECH-P2", "", "", ""}
    };

    TextMapParser parser = new TextMapParser(simpleMap);
    Map map = parser.parseMap();
    map.setFogOfWarOn(true);
    Game game = createGame(map);

    DefaultBuildAdvisor buildAdvisor = new DefaultBuildAdvisor(game);
    BuildStrategy buildStrategy = buildAdvisor.think();

    List<BuildPriority> priorities = buildStrategy.getBuildPriority();
    List<String> top5Units = getTop5Units(priorities);
    Assert.assertTrue(top5Units.contains("BIKES"));
    Assert.assertTrue(top5Units.contains("INFANTRY"));

    City factory1 = map.getCityOn(0, 0);
    List<Fuz.UNIT_TYPE> unitTypes1 = buildStrategy.getCityBuildHints(factory1);
    Assert.assertTrue(unitTypes1.contains(Fuz.UNIT_TYPE.CAPTURE));

    City factory2 = map.getCityOn(0, 1);
    List<Fuz.UNIT_TYPE> unitTypes2 = buildStrategy.getCityBuildHints(factory2);
    Assert.assertTrue(unitTypes2.contains(Fuz.UNIT_TYPE.CAPTURE));

  }

  private List<String> getTop5Units(List<BuildPriority> priorities) {
    List<String> unitNames = new ArrayList<String>(5);

    for (int i = 0; i < 5 && i < priorities.size(); i++) {
      BuildPriority buildPriority = priorities.get(i);
      unitNames.add(buildPriority.unitName);
    }

    return unitNames;
  }
}
