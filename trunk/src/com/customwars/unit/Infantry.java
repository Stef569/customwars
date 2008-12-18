package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Infantry.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: June 24, 2006, 11:51 PM
 *The Infantry class is used to create an instance of the Infantry Unit
 */

public class Infantry extends Unit{
    
    //constructor
    public Infantry(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Infantry";
        unitType = 0;
        setMoveType(MOVE_INFANTRY);
        setMove(3);
        price = 1000;
        setMaxGas(99);
        setMaxAmmo(-1);
        setVision(2);
        minRange = 1;
        setMaxRange(1);
        
        starValue = 0.4;
        
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}