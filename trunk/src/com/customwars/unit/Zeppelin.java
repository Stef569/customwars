package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Zeppelin.java
 *Author: Xaif
 *Contributors:
 *Creation: 29/7/06
 *The Zeppelin class is used to create an instance of the Zeppelin Unit
 */

public class Zeppelin extends Unit{
   
    //constructor
    public Zeppelin(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Zeppelin";
        unitType = 28;
        setMoveType(MOVE_AIR);
        setMove(5);
        price = 10000;
        setMaxGas(70);
        setMaxAmmo(9);
        setVision(2);
        minRange = 2;
        setMaxRange(4);
        setDailyGas(2);
       
        starValue = 1.2;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
       
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}