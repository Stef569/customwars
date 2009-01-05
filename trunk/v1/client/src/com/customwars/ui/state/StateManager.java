package com.customwars.ui.state;

import org.apache.log4j.Logger;

import javax.swing.*;
import java.awt.*;
import java.util.HashMap;
import java.util.Map;

/**
 * Holds all the states
 * It will paint the current state on frame.repaint
 * The current state can be changed to a new state by invoking changeToState(String newState)
 * Each State can be initialized, painted and be stopped.
 * Each State is painted within 1 JPanel
 * <p/>
 * States should be added before invoking changeToState by invoking addState(String stateName, State state)
 * A debug message is logged when this class could not change to the stateName.
 * @author stefan
 * @since 2.0
 */
public class StateManager {
    private static final Logger logger = Logger.getLogger(StateManager.class);
    private Map<String, State> states = new HashMap<String, State>();
    private JFrame frame;
    private State currentState;

    public StateManager(JFrame frame) {
        this.frame = frame;
        StatePanel statePanel = new StatePanel();
        frame.add(statePanel);
    }

    public void changeToState(String stateName) {
        if (states.containsKey(stateName)) {
            State newState = states.get(stateName);

            stopCurrentState();
            initNewState(newState);
        } else {
            logger.warn("State " + stateName + " is not available, All States:" + states.keySet());
        }
    }

    private void stopCurrentState() {
        if (currentState != null) {
            currentState.stop();
        }
    }

    private void initNewState(State newState) {
        currentState = newState;
        newState.init();
    }

    public void addState(String stateName, State state) {
        states.put(stateName, state);
    }

    public void removeState(String stateName) {
        states.remove(stateName);
    }

    /**
     * Panel used to paint the currentState
     */
    private class StatePanel extends JPanel {
        protected void paintComponent(Graphics g) {
            currentState.paint((Graphics2D) g);
        }
    }
}
