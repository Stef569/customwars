package cwsource;
/*
 *Anti-Air.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The Anti-Air class is used to create an instance of the Anti-Air Unit
 */

public class AntiAir extends Unit{
   
    //constructor
    public AntiAir(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Anti-Air";
        unitType = 5;
        moveType = MOVE_TREAD;
        move = 6;
        price = 8000;
        maxGas = 60;
        maxAmmo = 9;
        vision = 2;
        minRange = 1;
        maxRange = 1;
       
        starValue = 1.0;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}