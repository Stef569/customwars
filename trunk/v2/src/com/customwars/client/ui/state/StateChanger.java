package com.customwars.client.ui.state;

import com.customwars.client.tools.UCaseMap;
import org.apache.log4j.Logger;
import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;

import java.util.Deque;
import java.util.LinkedList;
import java.util.Map;

/**
 * Handles transitions between states, each state has an unique ID starting from 0
 * It maps state names to their stateID, state names are always stored in upper case.
 * multiple stateNames can point to the same stateID.
 *
 * Each time we change to a state, we store that stateID into previousStates which uses a LIFO Queue
 * This allows to go back 1 state, clearPreviousStatesHistory removes all stored stateID's from the previousStates queue.
 */
public class StateChanger {
  private static final Logger logger = Logger.getLogger(StateChanger.class);
  private static final int PREVIOUS_STATE_LIMIT = 10;
  private final StateBasedGame stateBasedgame;
  private final Deque<Integer> previousStates;
  private final Map<String, Integer> states;
  private boolean recording = true;

  public StateChanger(StateBasedGame game) {
    this.stateBasedgame = game;
    this.states = new UCaseMap<Integer>();
    this.previousStates = new LinkedList<Integer>();
  }

  public void addState(String stateName, int stateID) {
    if (stateName != null) {
      states.put(stateName, stateID);
    }
  }

  public void clearPreviousStatesHistory() {
    previousStates.clear();
  }

  /**
   * Change to another state
   *
   * @param stateName case insensitive state name
   * @throws IllegalArgumentException thrown when a stateName is not mapped to a stateID
   *                                  or when the stateID is not within state bounds(>0 <states.size)
   */
  public void changeTo(String stateName) {
    if (states.containsKey(stateName)) {
      int stateID = states.get(stateName);
      gotoState(stateID);
    } else {
      throw new IllegalArgumentException(
        "Attention: " + stateName + " does not exist!!! states:" + states.keySet());
    }
  }

  /**
   * Try to change to the previous state. This only works
   * if changeTo(String) or changeToNext() are invoked at least 2x.
   */
  public void changeToPrevious() {
    if (previousStates.size() >= 2) {
      int currentStateID = previousStates.removeLast();
      int previousStateID = previousStates.removeLast();
      gotoState(previousStateID);
    } else {
      logger.warn("Could not go to the previous state, as there is not enough state history(min 2) " + previousStates);
    }
  }

  /**
   * Try to move to the next state, only works if ID's start from 0
   * and go up by 1.
   */
  public void changeToNext() {
    int nextID = stateBasedgame.getCurrentStateID() + 1;
    if (stateBasedgame.getState(nextID) != null) {
      gotoState(nextID);
    } else {
      gotoState(0);
    }
  }

  private void gotoState(int stateID) {
    if (stateID >= 0) {
      stateBasedgame.enterState(stateID, null, new FadeInTransition(Color.black, 250));
      storeStateID(stateID);
      String stateName = getStateName(stateID);
      logger.debug("Entering state [" + stateName + "] History=" + previousStates + " Recording=" + recording);
    } else {
      throw new IllegalArgumentException(
        "StateID " + stateID + " is not within state bounds >=0");
    }
  }

  private String getStateName(int stateID) {
    for (Map.Entry<String, Integer> stateEntry : states.entrySet()) {
      if (stateEntry.getValue() == stateID) {
        return stateEntry.getKey();
      }
    }
    throw new IllegalArgumentException("No state found for " + stateID);
  }

  private void storeStateID(int stateID) {
    if (recording) {
      if (previousStates.size() > PREVIOUS_STATE_LIMIT) {
        previousStates.removeFirst();
      }
      previousStates.add(stateID);
    }
  }

  /**
   * Temporally stop recording states to the previousStates history
   *
   * @see #resumeRecordingStateHistory()
   */
  public void stopRecordingStateHistory() {
    recording = false;
  }

  /**
   * Record each change to another state so that
   * the changeToPrevious method changes to the previous state
   *
   * @see #stopRecordingStateHistory()
   */
  public void resumeRecordingStateHistory() {
    recording = true;
  }
}
