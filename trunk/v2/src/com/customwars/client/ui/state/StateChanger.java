package com.customwars.client.ui.state;

import org.newdawn.slick.Color;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;

import java.util.Deque;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.Map;

/**
 * Handles transitions between states, each state has an ID starting from 0
 * It maps state names to their stateID, state names are always stored in upper case.
 * multiple stateNames can point to the same stateID.
 *
 * Each time we change to a state, we store that stateID into previousStates which uses a LIFO Queue
 * This allows to go back 1 state, clearPreviousStates removes all stored stateID's from the previousStates queue.
 */
public class StateChanger {
  private static final int PREVIOUS_STATE_LIMIT = 20;
  private StateBasedGame stategame;
  private Deque<Integer> previousStates;
  private Map<String, Integer> states;

  public StateChanger(StateBasedGame game) {
    this.stategame = game;
    this.states = new HashMap<String, Integer>();
    this.previousStates = new LinkedList<Integer>();
  }

  public void addState(String stateName, int stateID) {
    if (stateName != null) {
      String key = stateName.toUpperCase();
      states.put(key, stateID);
    }
  }

  public void clearPreviousStates() {
    previousStates.clear();
  }

  /**
   * Change to another state
   *
   * @param stateName case insensitive state name
   * @throws IllegalArgumentException thrown when a stateName is not mapped to a stateID
   * or when the stateID is not within state bounds(>0 <states.size)
   */
  public void changeTo(String stateName) {
    if (stateName != null) {
      gotoState(stateName.toUpperCase());
    }
  }

  public void changeToPrevious() {
    if (!previousStates.isEmpty()) {
      int lastStateID = previousStates.removeLast();
      gotoState(lastStateID);
    }
  }

  /**
   * Try to move to the next state, only works if ID's start from 0
   * and go up by 1.
   */
  public void changeToNext() {
    int nextID = stategame.getCurrentStateID() + 1;
    if (stategame.getState(nextID) != null) {
      gotoState(nextID);
    } else {
      gotoState(0);
    }
  }

  private void gotoState(String stateName) {
    if (states.containsKey(stateName)) {
      int stateID = states.get(stateName);
      gotoState(stateID);
    } else {
      throw new IllegalArgumentException(
              "Attention: " + stateName + " does not exist!!! states:" + states.keySet());
    }
  }

  public void gotoState(int stateID) throws IllegalArgumentException {
    if (stateID >= 0) {
      stategame.enterState(stateID, null, new FadeInTransition(Color.black, 250));
      if (previousStates.size() < PREVIOUS_STATE_LIMIT)
        previousStates.add(stateID);
    } else {
      throw new IllegalArgumentException(
              "StateID " + stateID + " is not within state bounds >=0");
    }
  }
}
