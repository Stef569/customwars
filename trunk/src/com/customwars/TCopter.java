package com.customwars;
/*
 *TCopter.java
 *Author: Xenesis
 *Contributors: Intelligent Systems
 *Creation: July 17, 2006
 *The TCopter class is used to create an instance of the TCopter Unit
 */

public class TCopter extends Transport{
    
    //constructor
    public TCopter(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "T Copter";
        unitType = 14;
        moveType = MOVE_AIR;
        move = 6;
        price = 5000;
        maxGas = 99;
        maxAmmo = -1;
        vision = 2;
        minRange = 0;
        maxRange = 0;
        dailyGas = 2;
        
        starValue = 1;
        
        //Transport Statistics
        maxUnits = 1;
        transportTable[0]=true;
        transportTable[1]=true;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}