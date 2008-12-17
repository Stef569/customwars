package com.customwars;
/*
 *Rockets.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Rockets class is used to create an instance of the Rockets Unit
 */

public class Rockets extends Unit{
   
    //constructor
    public Rockets(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Rockets";
        unitType = 8;
        setMoveType(MOVE_TIRE);
        setMove(5);
        price = 14000;
        setMaxGas(50);
        setMaxAmmo(6);
        setVision(2);
        minRange = 3;
        setMaxRange(5);
       
        starValue = 1.4;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}