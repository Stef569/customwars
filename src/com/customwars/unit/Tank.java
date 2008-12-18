package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Tank.java
 *Author: Killian Hanlon
 *Contributors:
 *Creation: 17/7/06
 *The Tank class is used to create an instance of theTank Unit
 */

public class Tank extends Unit{
   
    //constructor
    public Tank(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Tank";
        unitType = 2;
        setMoveType(MOVE_TREAD);
        setMove(6);
        price = 7000;
        setMaxGas(70);
        setMaxAmmo(9);
        setVision(3);
        minRange = 1;
        setMaxRange(1);
       
        starValue = 1.0;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}