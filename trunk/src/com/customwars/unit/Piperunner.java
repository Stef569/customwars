package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Piperunner.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The Piperunner class is used to create an instance of the Piperunner Unit
 */

public class Piperunner extends Unit{
   
    //constructor
    public Piperunner(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Piperunner";
        unitType = 20;
        setMoveType(MOVE_PIPE);
        setMove(9);
        price = 18000;
        setMaxGas(99);
        setMaxAmmo(9);
        setVision(4);
        minRange = 2;
        setMaxRange(5);
       
        starValue = 2.0;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}