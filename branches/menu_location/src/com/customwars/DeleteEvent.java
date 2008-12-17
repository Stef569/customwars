package com.customwars;
/*
 *DeleteEvent.java
 *Author: Uru
 *Contributors:
 *Creation: January 10, 2007, 2:58 AM
 *A special class for recording unit deletions
 */

import java.io.*;

public class DeleteEvent extends CWEvent implements Serializable{
    private int x;          //the x coordinate of the action
    private int y;          //the y coordinate of the action
    
    //constructor
    public DeleteEvent(int xc,int yc,int d,int tr){
        super(8,d,tr);
        x = xc;
        y = yc;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public String toString(){
        return "Type: " + type + " Day: " + day + " Turn: " + turn + " X: " + x + " Y: " + y;
    }
}