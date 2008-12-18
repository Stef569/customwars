package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Shuttlerunner.java
 *Author: Xaif
 *Contributors:
 *Creation: 29/7/06
 *The Shuttlerunner class is used to create an instance of the Shuttlerunner Unit
 */

public class Shuttlerunner extends Transport{
   
    //constructor
    public Shuttlerunner(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Shuttlerunner";
        unitType = 27;
        setMoveType(MOVE_PIPE);
        setMove(11);
        price = 10000;
        setMaxGas(99);
        setMaxAmmo(-1);
        setVision(3);
        minRange = 0;
        setMaxRange(0);
       
        starValue = 1.0;
       
        //Transport Statistics
        maxUnits = 2;
        for(int i=0;i<10;i++)transportTable[i]=true;
        transportTable[18]=true;
        transportTable[19]=true;
        transportTable[20]=true;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
       
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}