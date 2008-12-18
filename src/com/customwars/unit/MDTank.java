package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *MD Tank.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The MDTank class is used to create an instance of the MDTank Unit
 */

public class MDTank extends Unit{
   
    //constructor
    public MDTank(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Md Tank";
        unitType = 3;
        setMoveType(MOVE_TREAD);
        setMove(5);
        price = 15000;
        setMaxGas(50);
        setMaxAmmo(8);
        setVision(1);
        minRange = 1;
        setMaxRange(1);
       
        starValue = 1.6;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}