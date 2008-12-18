package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *BCopter.java
 *Author: Xenesis
 *Contributors: Intelligent Systems
 *Creation: July 17, 2006
 *The BCopter class is used to create an instance of the BCopter Unit
 */

public class BCopter extends Unit{
    
    //constructor
    public BCopter(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "B Copter";
        unitType = 15;
        setMoveType(MOVE_AIR);
        setMove(6);
        price = 9000;
        setMaxGas(99);
        setMaxAmmo(6);
        setVision(3);
        minRange = 1;
        setMaxRange(1);
        setDailyGas(2);
        
        starValue = 1.2;
        
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}