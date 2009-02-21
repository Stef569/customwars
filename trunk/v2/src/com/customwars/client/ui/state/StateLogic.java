package com.customwars.client.ui.state;

import org.newdawn.slick.GameContainer;
import org.newdawn.slick.state.StateBasedGame;
import org.newdawn.slick.state.transition.FadeInTransition;
import org.newdawn.slick.AppGameContainer;
import org.newdawn.slick.Color;
import java.util.Hashtable;

public class StateLogic{
    
    StateBasedGame stategame;
    AppGameContainer appcontainer;
    Hashtable<String, Integer> listStates;
    
    public StateLogic(StateBasedGame game, AppGameContainer container){
        listStates = new Hashtable<String, Integer>();
        initialize(game, container);
    }
    
    private void initialize(StateBasedGame game, AppGameContainer container){
        stategame = game;
        appcontainer = container;
    }
    
    public void changeGameState(String name){
        if(name != null){
            if(listStates.containsKey(name.toUpperCase())){
                changeGameState(listStates.get(name.toUpperCase()));
            }else{
                System.out.println("Attention: "+name+" does not exist!!!");
                listStates();
            }
        }      
    }
    
    public void addState(String theString, int theState){
        if(theString != null){
            listStates.put(theString.toUpperCase(), theState);
        }
    }
    
    public void changeGameState(int index){
        if(index > -1){
            stategame.enterState(
                index, null, new FadeInTransition(Color.black));
            appcontainer.setTitle(
                stategame.getState(index).getClass().toString());
        }
    }
    
    public void listStates(){
        while(listStates.keys().hasMoreElements())
            System.out.println("CurrentStates: "+
                    listStates.keys().nextElement());
    }
    
}
