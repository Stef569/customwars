package com.customwars.unit;

import com.customwars.Location;
import com.customwars.Map;
/*
 *Submarine.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The Submarine class is used to create an instance of the Submarine Unit
 */

public class Submarine extends Unit{
    
    private int[] divedDamageTable = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,90,55,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1};
    
    //constructor
    public Submarine(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Submarine";
        unitType = 12;
        setMoveType(MOVE_SEA);
        setMove(5);
        if (army.getBattle().getBattleOptions().isBalance()==true){
            price = 15000;
        }
        if (army.getBattle().getBattleOptions().isBalance()==false){
        price = 20000;}
        setMaxGas(60);
        setMaxAmmo(6);
        setVision(5);
        minRange = 1;
        setMaxRange(1);
       
        starValue = 1.8;
       
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
    
    public void dive(){
        setDived(true);
        setDailyGas(5);
        hidden = true;
    }
    
    public void rise(){
        setDived(false);
        setDailyGas(1);
        hidden = false;
    }
    
    public int getDivedDamage(int i){
        return divedDamageTable[i];
    }
}