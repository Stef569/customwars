package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Battleship.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The Battleship class is used to create an instance of the Battleship Unit
 */

public class Battleship extends Unit{
   
    //constructor
    public Battleship(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Battleship";
        unitType = 13;
        setMoveType(MOVE_SEA);
        setMove(5);
        price = 25000;
        setMaxGas(99);
        setMaxAmmo(9);
        setVision(2);
        minRange = 3;
        setMaxRange(7);
        setDailyGas(1);
       
        starValue = 2.2;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}