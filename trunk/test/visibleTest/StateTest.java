package visibleTest;

import com.customwars.ai.GameSession;
import com.customwars.loader.MapLoader;
import com.customwars.map.Map;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.MainMenuGraphics;
import com.customwars.ui.MiscGraphics;
import com.customwars.ui.TerrainGraphics;
import com.customwars.ui.menus.CoSelectMenu;
import com.customwars.ui.menus.GameMenu;
import com.customwars.ui.menus.GeneralOptionsMenu;
import com.customwars.ui.menus.Mainmenu;
import com.customwars.ui.menus.MapSelectMenu;
import com.customwars.ui.menus.MenuSession;
import com.customwars.ui.state.StateManager;

import javax.swing.*;
import java.util.List;

/**
 * Create states
 * when clicking on new in the mainMenu the state changes to newGame.
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
        frame = new JFrame();
        GameSession.mainFrame = frame;
        frame.setSize(480, 320);
    }

    private void initRes() {
        ResourceLoader.init();
        String SOUND_LOCATION = ResourceLoader.properties.getProperty("soundLocation");
        SFX.setSoundLocation(SOUND_LOCATION);

        MainMenuGraphics.loadImages(frame);
        MiscGraphics.loadImages(frame);
        TerrainGraphics.loadImages(frame);

        maps = new MapLoader().loadAllValidMaps();
    }

    private void initStates(MenuSession menuSession) {
        StateManager stateManager = new StateManager(frame);
        stateManager.addState("MAIN_MENU", new Mainmenu(frame, stateManager));
        stateManager.addState("NEW_GAME", new GameMenu(frame, stateManager));
        stateManager.addState("OPTIONS", new GeneralOptionsMenu(frame));
        stateManager.addState("START_SINGLEPLAYER_GAME", new MapSelectMenu(frame, stateManager, menuSession, maps));
        stateManager.addState("CO_SELECT", new CoSelectMenu(frame, stateManager, menuSession));
        stateManager.changeToState("MAIN_MENU");
    }

    public static void main(String[] args) {
        new StateTest();
    }
}
