package cwsource;
/*
 *BlackBomb.java
 *Author: Xenesis
 *Contributors: Intelligent Systems
 *Creation: July 17, 2006
 *The BlackBomb class is used to create an instance of the BlackBomb Unit
 */

public class BlackBomb extends Unit{
    
    //constructor
    public BlackBomb(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Black Bomb";
        unitType = 24;
        moveType = MOVE_AIR;
        move = 9;
        if(arm.getBattle().getBattleOptions().isBalance())
            price = 15000;
        else
            price = 25000;
        maxGas = 45;
        maxAmmo = -1;
        vision = 1;
        minRange = 0;
        maxRange = 0;
        dailyGas = 5;
        
        starValue = 0.6;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}