package com.customwars;
/*
 *Mech.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: July 14, 2006, 5:46 PM
 *The Mech class is used to create an instance of the Mech Unit
 */

public class Mech extends Unit{
    
    //constructor
    public Mech(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Mech";
        unitType = 1;
        setMoveType(MOVE_MECH);
        setMove(2);
        price = 3000;
        setMaxGas(70);
        setMaxAmmo(3);
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

