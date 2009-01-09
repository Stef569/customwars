package visibleTest;

import com.customwars.ai.BaseDMG;
import com.customwars.ai.GameSession;
import com.customwars.ai.Options;
import com.customwars.loader.MapLoader;
import com.customwars.lobbyclient.FobbahLauncher;
import com.customwars.map.Map;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.BattleGraphics;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.TerrainGraphics;
import com.customwars.ui.UnitGraphics;
import com.customwars.ui.menus.CoSelectMenu;
import com.customwars.ui.menus.GameMenu;
import com.customwars.ui.menus.GeneralOptionsMenu;
import com.customwars.ui.menus.Mainmenu;
import com.customwars.ui.menus.MapSelectMenu;
import com.customwars.ui.menus.MenuSession;
import com.customwars.ui.menus.BattleOptionsMenu;
import com.customwars.ui.menus.InGameState;
import com.customwars.ui.state.StateManager;

import javax.swing.*;
import java.util.List;

/**
 * Create states
 * when clicking on new in the mainMenu the state changes to newGame.
 * <p/>
 * The startup steps are copied from Main.java
 *
 * @author stefan
 * @since 2.0
 */
public class StateTest {
    JFrame frame;
    List<Map> maps;

    public StateTest() {
        initGui();
        initRes();
        initStates(new MenuSession());
        frame.setVisible(true);
    }

    private void initGui() {
        frame = new JFrame("Custom Wars");
        frame.setSize(480, 320);
        frame.setVisible(true);
        frame.setIconImage(new ImageIcon(ResourceLoader.properties.getProperty("imagesLocation") + "/misc/icon.gif").getImage());
        GameSession.mainFrame = frame;
        FobbahLauncher.setLaunched();
    }

    private void initRes() {
        String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
        SFX.setSoundLocation(SOUND_LOCATION);

        Options.InitializeOptions();
        BaseDMG.restoreDamageTables();
        BaseDMG.restoreBalanceDamageTables();
        UnitGraphics.loadImages(frame);
        TerrainGraphics.loadImages(frame);
        MiscGraphics.loadImages(frame);
        MainMenuGraphics.loadImages(frame);
        BattleGraphics.loadImages(frame);

        maps = new MapLoader().loadAllValidMaps();
    }

    private void initStates(MenuSession menuSession) {
        StateManager stateManager = new StateManager(frame);
        stateManager.addState("MAIN_MENU", new Mainmenu(frame, stateManager));
        stateManager.addState("NEW_GAME", new GameMenu(frame, stateManager));
        stateManager.addState("OPTIONS", new GeneralOptionsMenu(frame, stateManager));
        stateManager.addState("START_SINGLEPLAYER_GAME", new MapSelectMenu(frame, stateManager, menuSession, maps));
        stateManager.addState("CO_SELECT", new CoSelectMenu(frame, stateManager, menuSession));
        stateManager.addState("BATTLE_OPTIONS", new BattleOptionsMenu(frame, stateManager, menuSession));
        stateManager.addState("IN_GAME", new InGameState(frame, menuSession));
      stateManager.changeToState("MAIN_MENU");
    }

    public static void main(String[] args) {
        ResourceLoader.init();
        new StateTest();
    }
}
