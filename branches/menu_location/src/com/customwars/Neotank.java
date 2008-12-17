package com.customwars;
/*
 *Neotank.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Neotank class is used to create an instance of the Neotank Unit
 */

public class Neotank extends Unit{
   
    //constructor
    public Neotank(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Neotank";
        unitType = 18;
        setMoveType(MOVE_TREAD);
        setMove(6);
        price = 20000;
        setMaxGas(99);
        setMaxAmmo(9);
        setVision(2);
        minRange = 1;
        setMaxRange(1);
       
        starValue = 1.8;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}