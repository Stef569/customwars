package com.customwars.unit;
/*
 *Action.java
 *Author: Uru
 *Contributors:
 *Creation: January 8, 2007, 3:35 AM
 *the most important Event type, covers actions done by units
 */

import java.io.*;

import com.customwars.CWEvent;
import com.customwars.map.location.Path;

public class Action extends CWEvent implements Serializable{
    private int id; //the action's ID, see the master index list for a list
    private Path movePath;  //the move-path before the action
    private int cx;         //the x coordinate of the acting unit
    private int cy;         //the y coordinate of the acting unit
    private int x;          //the x coordinate of the action
    private int y;          //the y coordinate of the action
    //private int info;       //additional information about the action
    
    //constructor
    public Action(int i,int ux,int uy,Path p,int xc,int yc,int d,int tr){
        super(0,d,tr);
        id = i;
        movePath = p;
        cx = ux;
        cy = uy;
        x = xc;
        y = yc;
        //info = in;
    }
    
    public int getID(){
        return id;
    }
    
    public Path getPath(){
        return movePath;
    }
    
    public int getUnitX(){
        return cx;
    }
    
    public int getUnitY(){
        return cy;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    //public int getInfo(){
    //    return info;
    //}
    
    public String toString(){
        return "Type: " + type + " Day: " + day + " Turn: " + turn + " ID: " + id + " UX: " + cx + " UY: " + cy + "\nX: " + x + " Y: " + y + " Path: " + movePath;
    }
}
