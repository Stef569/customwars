package cwsource;
/*
 *Bomber.java
 *Author: veggiehunter
 *Contributors:
 *Creation:
 *The Bomber class is used to create an instance of the Bomber Unit
 */

public class Bomber extends Unit{
   
    //constructor
    public Bomber(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Bomber";
        unitType = 17;
        moveType = MOVE_AIR;
        move = 7;
        price = 20000;
        maxGas = 99;
        maxAmmo = 9;
        vision = 2;
        minRange = 1;
        maxRange = 1;
        dailyGas = 5;
       
        starValue = 1.8;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}