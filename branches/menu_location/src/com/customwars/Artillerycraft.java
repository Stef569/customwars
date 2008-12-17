package com.customwars;
/*
 *Artillerycraft.java
 *Author: Xaif
 *Contributors:
 *Creation: 23/7/06
 *The Artillerycraft class is used to create an instance of the Artillerycraft Unit
 */

public class Artillerycraft extends Unit{
   
    //constructor
    public Artillerycraft(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Acraft";
        unitType = 26;
        setMoveType(MOVE_HOVER);
        setMove(4);
        if(m.find(this).getTerrain().getName().equals("Port"))
            price = 15000;
        else
            price = 10000;
        setMaxGas(70);
        setMaxAmmo(6);
        setVision(4);
        minRange = 3;
        setMaxRange(4);
       
        starValue = 1.2;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}