package com.customwars.client.ui;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;

public class StateLogic{
    
    StateBasedGame stategame;
    AppGameContainer appcontainer;
    
    public void initialize(StateBasedGame game, AppGameContainer container){
        stategame = game;
        appcontainer = container;
    }
    
    public void changeGameState(int index){
        if(index > -1){
            stategame.enterState(
                index, null, new FadeInTransition(Color.black));
            appcontainer.setTitle(
                stategame.getState(index).getClass().toString());
        }
    }
    
}
