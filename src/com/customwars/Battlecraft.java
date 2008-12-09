package com.customwars;
/*
 *Battlecraft.java
 *Author: Xaif
 *Contributors:
 *Creation: 23/7/06
 *The Battlecraft class is used to create an instance of the Battlecraft Unit
 */

public class Battlecraft extends Unit{
   
    //constructor
    public Battlecraft(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Bcraft";
        unitType = 25;
        moveType = MOVE_HOVER;
        move = 5;
        if(m.find(this).getTerrain().getName().equals("Port"))
            price = 15000;
        else
            price = 10000;
        maxGas = 70;
        maxAmmo = 9;
        vision = 3;
        minRange = 1;
        maxRange = 1;
       
        starValue = 1.2;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}