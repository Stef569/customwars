package com.customwars;
/*
 *Zeppelin.java
 *Author: Xaif
 *Contributors:
 *Creation: 29/7/06
 *The Zeppelin class is used to create an instance of the Zeppelin Unit
 */

public class Zeppelin extends Unit{
   
    //constructor
    public Zeppelin(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Zeppelin";
        unitType = 28;
        moveType = MOVE_AIR;
        move = 5;
        price = 10000;
        maxGas = 70;
        maxAmmo = 9;
        vision = 2;
        minRange = 2;
        maxRange = 4;
        dailyGas = 2;
       
        starValue = 1.2;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
       
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}