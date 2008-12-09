package com.customwars;
/*
 *CWEvent.java
 *Author: Uru
 *Contributors:
 *Creation: January 8, 2007, 3:24 AM
 *The base event type, contains more complex types and can handle simple events on its own
 */

import java.io.*;

public class CWEvent implements Serializable{
    //data
    protected int type;   //the type of event
    protected int day;    //the day that the event occured on/will occur on
    protected int turn;   //the turn that the event occured on/will occur on
    
    //constructor
    public CWEvent(int t,int d,int tr){
        type = t;
        day = d;
        turn = tr;
    }
    
    public int getType(){
        return type;
    }
    
    public int getDay(){
        return day;
    }
    
    public int getTurn(){
        return turn;
    }
    
    public String toString(){
        return "Type: " + type + " Day: " + day + " Turn: " + turn;
    }
}
