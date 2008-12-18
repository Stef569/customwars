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
    private int type; //holds type of trigger
    private int day;  //holds the day the trigger goes off
    private int turn; //the turn the trigger goes off.
    private Battle b; //holds the battle this affects.
    //A unit trigger
    public Trigger(Battle b, int day, int turn, int type){
        this.setB(b);
        this.setDay(day);
        this.setType(type);
    }
    public void setTrigger(){};
    public void trigger(){}
	public void setB(Battle b) {
		this.b = b;
	}
	public Battle getB() {
		return b;
	}
	public void setType(int type) {
		this.type = type;
	}
	public int getType() {
		return type;
	}
	public void setDay(int day) {
		this.day = day;
	}
	public int getDay() {
		return day;
	}
	public void setTurn(int turn) {
		this.turn = turn;
	}
	public int getTurn() {
		return turn;
	};
}

