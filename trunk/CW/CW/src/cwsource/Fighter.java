package cwsource;
/*
 *Fighter.java
 *Author: Eskimoconvntion
 *Contributors:
 *Creation: 17/7/06
 *The Fighter class is used to create an instance of the Fighter Unit
 */

public class Fighter extends Unit{
   
    //constructor
    public Fighter(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Fighter";
        unitType = 16;
        moveType = MOVE_AIR;
        move = 9;
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