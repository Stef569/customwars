package com.customwars.ai;
/*
 *BuildEvent.java
 *Author: Uru
 *Contributors:
 *Creation: January 8, 2007, 3:46 AM
 *Contains information about unit construction
 */

import java.io.*;

public class BuildEvent extends CWEvent implements Serializable{
    private int unitType;
    private int x;
    private int y;
    
    //constructor
    public BuildEvent(int ut, int xc, int yc, int d, int tr){
        super(1,d,tr);
        unitType = ut;
        x = xc;
        y = yc;
    }
    
    public int getUnitType(){
        return unitType;
    }
    
    public int getX(){
        return x;
    }
    
    public int getY(){
        return y;
    }
    
    public String toString(){
        return "Type: " + type + " Day: " + day + " Turn: " + turn + " Unit Type: " + unitType + " X: " + x + " Y: " + y;
    }
}
