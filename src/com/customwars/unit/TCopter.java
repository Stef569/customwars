package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *TCopter.java
 *Author: Xenesis
 *Contributors: Intelligent Systems
 *Creation: July 17, 2006
 *The TCopter class is used to create an instance of the TCopter Unit
 */

public class TCopter extends Transport{
    
    //constructor
    public TCopter(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "T Copter";
        unitType = 14;
        setMoveType(MOVE_AIR);
        setMove(6);
        price = 5000;
        setMaxGas(99);
        setMaxAmmo(-1);
        setVision(2);
        minRange = 0;
        setMaxRange(0);
        setDailyGas(2);
        
        starValue = 1;
        
        //Transport Statistics
        maxUnits = 1;
        transportTable[0]=true;
        transportTable[1]=true;
        
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}