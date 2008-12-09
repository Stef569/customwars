package com.customwars;
/*
 *Cruiser.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The Cruiser class is used to create an instance of the Cruiser Unit
 */

public class Cruiser extends Transport{
   
    //constructor
    public Cruiser(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Cruiser";
        unitType = 11;
        moveType = MOVE_SEA;
        move = 6;
        if (army.getBattle().getBattleOptions().isBalance()==true){
            price = 15000;
        }
        if (army.getBattle().getBattleOptions().isBalance()==false){
        price = 18000;}
        maxGas = 99;
        maxAmmo = 9;
        vision = 3;
        minRange = 1;
        maxRange = 1;
        dailyGas = 1;
        
        starValue = 1.6;
        
        //Transport Statistics
        maxUnits = 2;
        transportTable[14]=true;
        transportTable[15]=true;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}