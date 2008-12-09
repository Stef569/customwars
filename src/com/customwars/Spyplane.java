package com.customwars;
/*
 *Spyplane.java
 *Author: Xaif
 *Contributors:
 *Creation: 29/7/06
 *The Spyplane class is used to create an instance of the Spyplane Unit
 */

public class Spyplane extends Unit{
   
    //constructor
    public Spyplane(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Spyplane";
        unitType = 29;
        moveType = MOVE_AIR;
        move = 8;
        price = 15000;
        maxGas = 99;
        maxAmmo = -1;
        vision = 7;
        minRange = 0;
        maxRange = 0;
        dailyGas = 5;
       
        starValue = 2.2;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
       
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}