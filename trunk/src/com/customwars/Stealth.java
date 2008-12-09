package com.customwars;
/*
 *Stealth.java
 *Author: Xenesis
 *Contributors: Intelligent Systems
 *Creation: July 17, 2006
 *The Stealth class is used to create an instance of the Stealth Unit
 */

public class Stealth extends Unit{
    
    private int[] hiddenDamageTable = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,85,-1,-1,-1,-1,-1,-1,55,-1,-1,-1,-1,-1,-1,-1};
    
    //constructor
    public Stealth(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Stealth";
        unitType = 23;
        moveType = MOVE_AIR;
        move = 6;
        price = 20000;
        maxGas = 60;
        maxAmmo = 6;
        vision = 4;
        minRange = 1;
        maxRange = 1;
        dailyGas = 5;
        
        starValue = 2;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
    
    public void hide(){
        dived = true;
        dailyGas = 8;
        hidden = true;
    }
    
    public void appear(){
        dived = false;
        dailyGas = 5;
        hidden = false;
    }
    
    public int getHiddenDamage(int i){
        return hiddenDamageTable[i];
    }
}