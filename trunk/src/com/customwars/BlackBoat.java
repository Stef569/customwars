package com.customwars;
/*
 *BlackBoat.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The BlackBoat class is used to create an instance of the BlackBoat Unit
 */

public class BlackBoat extends Transport{
   
    //constructor
    public BlackBoat(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Black Boat";
        unitType = 21;
        moveType = MOVE_TRANSPORT;
        move = 7;
        price = 7500;
        maxGas = 60;
        maxAmmo = -1;
        vision = 1;
        minRange = 0;
        maxRange = 0;
        dailyGas = 1;
       
        starValue = 1.0;
        
        //Transport Statistics
        maxUnits = 2;
        transportTable[0]=true;
        transportTable[1]=true;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}