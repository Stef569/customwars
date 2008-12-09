package com.customwars;
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
        moveType = MOVE_AIR;
        move = 6;
        price = 9000;
        maxGas = 99;
        maxAmmo = 6;
        vision = 3;
        minRange = 1;
        maxRange = 1;
        dailyGas = 2;
        
        starValue = 1.2;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}