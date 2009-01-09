package com.customwars.ui.menus;

import com.customwars.ai.Battle;
import com.customwars.ai.GameSession;
import com.customwars.ui.BattleScreen;
import com.customwars.ui.state.State;

import javax.swing.*;
import java.awt.*;

/**
 * Links the battleScreen JComponent to the menus.
 *
 * @author stefan
 * @since 2.0
 */
public class InGameState implements State {
  private MenuSession menuSession;
  private JFrame frame;

  public InGameState(JFrame frame, MenuSession menuSession) {
    this.frame = frame;
    this.menuSession = menuSession;
  }

  public void init() {
    Battle battle = menuSession.getBattle();
    BattleScreen battleScreen = new BattleScreen(battle, frame);
    GameSession.startMission(battle, battleScreen);

    // save the initial state for the replay if applicable
    boolean isRecording = menuSession.getBattle().getBattleOptions().isRecording();
    if (isRecording) GameSession.saveInitialState();

    // Need to pack because the component needs to resize to the frame size.
    frame.add(battleScreen);
    frame.pack();
  }

  public void stop() {
  }

  public void paint(Graphics2D g) {
    // Don't paint as battleScreen is always painted within the Jframe.
  }
}
