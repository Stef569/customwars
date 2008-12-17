package com.customwars;
/*
 *Bomber.java
 *Author: veggiehunter
 *Contributors:
 *Creation:
 *The Bomber class is used to create an instance of the Bomber Unit
 */

public class Bomber extends Unit{
   
    //constructor
    public Bomber(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Bomber";
        unitType = 17;
        setMoveType(MOVE_AIR);
        setMove(7);
        price = 20000;
        setMaxGas(99);
        setMaxAmmo(9);
        setVision(2);
        minRange = 1;
        setMaxRange(1);
        setDailyGas(5);
       
        starValue = 1.8;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}