package com.customwars;

/*
 *Trigger.java
 *Author: Albert Lai
 *Contributors:
 *Creation: September 15, 2006, 11:25 PM
 *This is for campaign triggers that hapen on a specific day.
 */
import java.io.*;

public class Trigger implements Serializable{
    int type; //holds type of trigger
    int day;  //holds the day the trigger goes off
    int turn; //the turn the trigger goes off.
    Battle b; //holds the battle this affects.
    //A unit trigger
    public Trigger(Battle b, int day, int turn, int type){
        this.b = b;
        this.day = day;
        this.type = type;
    }
    public void setTrigger(){};
    public void trigger(){};
}

