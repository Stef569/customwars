package cwsource;
/*
 *Destroyer.java
 *Author: Xaif
 *Contributors:
 *Creation: 29/7/06
 *The Destroyer class is used to create an instance of the Destroyer Unit
 */

public class Destroyer extends Unit{
   
    //constructor
    public Destroyer(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Destroyer";
        unitType = 30;
        moveType = MOVE_SEA;
        move = 6;
        if (army.getBattle().getBattleOptions().isBalance()==true){
            price = 15000;
        }
        if (army.getBattle().getBattleOptions().isBalance()==false){
        price = 18000;}
        maxGas = 99;
        maxAmmo = 9;
        vision = 2;
        minRange = 1;
        maxRange = 1;
        dailyGas = 1;
       
        starValue = 1.6;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
       
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}