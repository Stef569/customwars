package com.customwars.unit;

/*
 *Stealth.java
 *Author: Xenesis
 *Contributors: Intelligent Systems
 *Creation: July 17, 2006
 *The Stealth class is used to create an instance of the Stealth Unit
 */
import com.customwars.map.Map;
import com.customwars.map.location.Location;

public class Stealth extends Unit{
    
    private int[] hiddenDamageTable = {-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,-1,85,-1,-1,-1,-1,-1,-1,55,-1,-1,-1,-1,-1,-1,-1};
    
    //constructor
    public Stealth(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Stealth";
        unitType = 23;
        setMoveType(MOVE_AIR);
        setMove(6);
        price = 20000;
        setMaxGas(60);
        setMaxAmmo(6);
        setVision(4);
        minRange = 1;
        setMaxRange(1);
        setDailyGas(5);
        
        starValue = 2;
        
        //Fills the Unit's gas and ammo
        setGas(getMaxGas());
        setAmmo(getMaxAmmo());
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
    
    public void hide(){
        setDived(true);
        setDailyGas(8);
        hidden = true;
    }
    
    public void appear(){
        setDived(false);
        setDailyGas(5);
        hidden = false;
    }
    
    public int getHiddenDamage(int i){
        return hiddenDamageTable[i];
    }
}