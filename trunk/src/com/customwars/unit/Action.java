package com.customwars.unit;
/*
 *Action.java
 *Author: Uru
 *Contributors:
 *Creation: January 8, 2007, 3:35 AM
 *the most important Event type, covers actions done by units
 */

import java.io.*;

import com.customwars.ai.CWEvent;
import com.customwars.map.location.Path;

public class Action extends CWEvent implements Serializable{
    private int id; //the action's ID, see the master index list for a list
    private Path movePath;  //the move-path before the action
    private int invokersXtile;         //the x coordinate of the acting unit
    private int invokersYTile;         //the y coordinate of the acting unit
    private int targetedXTile;          //the x coordinate of the action
    private int targetedYTile;          //the y coordinate of the action
    //private int info;       //additional information about the action
    
    //constructor
    public Action(int i,int ux,int uy,Path p,int xc,int yc,int d,int tr){
        super(0,d,tr);
        id = i;
        movePath = p;
        invokersXtile = ux;
        invokersYTile = uy;
        targetedXTile = xc;
        targetedYTile = yc;
        //info = in;
    }
    
    public int getID(){
        return id;
    }
    
    public Path getPath(){
        return movePath;
    }
    
    public int getUnitX(){
        return invokersXtile;
    }
    
    public int getUnitY(){
        return invokersYTile;
    }
    
    public int getX(){
        return targetedXTile;
    }
    
    public int getY(){
        return targetedYTile;
    }
    
    //public int getInfo(){
    //    return info;
    //}
    
    public String toString(){
        return "Action Type=[" + type + "] ID=[" + id + " TileCol=[" + invokersYTile + "] TileRow=[" + invokersXtile + "] Targeted RowTile=[" + targetedXTile + "] Targeted ColTile=[" + targetedYTile + "] Movement Path: " + movePath;
    }
}
