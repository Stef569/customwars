package cwsource;
/*
 *Battleship.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The Battleship class is used to create an instance of the Battleship Unit
 */

public class Battleship extends Unit{
   
    //constructor
    public Battleship(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Battleship";
        unitType = 13;
        moveType = MOVE_SEA;
        move = 5;
        price = 25000;
        maxGas = 99;
        maxAmmo = 9;
        vision = 2;
        minRange = 3;
        maxRange = 7;
        dailyGas = 1;
       
        starValue = 2.2;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}