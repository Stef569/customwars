package cwsource;
/*
 *Lander.java
 *Author: Xaif
 *Contributors:
 *Creation: 17/7/06
 *The Lander class is used to create an instance of the Lander Unit
 */

public class Lander extends Transport{
   
    //constructor
    public Lander(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
       
        //Statistics
        name = "Lander";
        unitType = 10;
        moveType = MOVE_TRANSPORT;
        move = 6;
        if (army.getBattle().getBattleOptions().isBalance()==true){
            price = 10000;
        }
        if (army.getBattle().getBattleOptions().isBalance()==false){
        price = 12000;}
        maxGas = 99;
        maxAmmo = -1;
        vision = 1;
        minRange = 0;
        maxRange = 0;
        dailyGas = 1;
        
        starValue = 1.0;
        
        //Transport Statistics
        maxUnits = 2;
        for(int i=0;i<10;i++)transportTable[i]=true;
        transportTable[18]=true;
        transportTable[19]=true;
        //transportTable[20]=true;
       
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
}