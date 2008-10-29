package cwsource;
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
        moveType = MOVE_HOVER;
        move = 4;
        price = 10000;
        maxGas = 70;
        maxAmmo = 6;
        vision = 4;
        minRange = 3;
        maxRange = 4;
       
        starValue = 1.2;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}