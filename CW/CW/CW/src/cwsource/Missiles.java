package cwsource;
/*
 *Missiles.java
 *Author: Limbo the Monkey
 *Contributors:
 *Creation: 18/7/06
 *The Missiles class is used to create an instance of the Missiles Unit
 */

public class Missiles extends Unit{
   
    //constructor
    public Missiles(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Missiles";
        unitType = 6;
        moveType = MOVE_TIRE;
        move = 5;
        price = 12000;
        maxGas = 50;
        maxAmmo = 6;
        vision = 5;
        minRange = 3;
        maxRange = 6;
       
        starValue = 1.4;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}