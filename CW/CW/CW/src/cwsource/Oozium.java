package cwsource;
 /*
  *Oozium.java
  *Author: Adam Dziuk
  *Contributors:
  *Creation: December 10, 2006, 12:28 PM
  *The Mech class is used to create an instance of the Oozium238 Unit
  */

public class Oozium extends Unit{
    
    //constructor
    public Oozium(int row, int col, Army arm, Map m) {
        super(new Location(row, col),arm,m);
        
        //Statistics
        name = "Oozium";
        unitType = 31;
        moveType = MOVE_OOZIUM;
        move = 1;
        //price = 0;
        price = 10000;
        maxGas = 99;
        maxAmmo = -1;
        vision = 1;
        minRange = 0;
        maxRange = 0;
        
        starValue = 4.0;
        
        //Fills the Unit's gas and ammo
        gas = maxGas;
        ammo = maxAmmo;
        
        //make CO adjustments
        arm.getCO().setChange(this);
    }
    
    public boolean damage(int dmg, boolean destroy){
        if(destroy){
            if(dmg < 0) return false;
            hP -= dmg;
            if (hP <= 0){
                hP = 1;
                
                if(destroy){
                    if(map.find(this).getTerrain() instanceof Property){
                        ((Property) map.find(this).getTerrain()).endCapture();
                    }
                    map.remove(this);
                    army.removeUnit(this);
                    return true;
                }
            }
            return false;
        }
        return false;
    }
    
    //moves the unit to the given Location, checks if the move is valid, returns ambush status
//moves the unit to the given Location, checks if the move is valid, returns ambush status
    //NOTE: MOST MOVE ROUTINES RETURN AMBUSH STATUS, BUT OOZIUM CAN'T BE AMBUSHED...INSTEAD THEY CAN CAUSE DEFEAT
    //SO IT RETURNS THAT INSTEAD
    public Unit move(Location endLoc)
    {
        //boolean gameEnd = false;
        int i = unitPath.getLength();
        Unit lastUnit = null;
        
        //ensures that a proper moveTraverse has been generated...in the final version, this will happen when the player selects the unit to move
        if(moveRange == null)
        {
            System.out.println("Cannot move yet, move range unchecked");
            return lastUnit;
        }
        
        //check and execute the move
        if(moveRange.checkMove(endLoc))
        {
            if(!map.find(endLoc).hasUnit())
            {
                map.move(this, endLoc);
                this.setLocation(endLoc);
                //reset moveRange
                //moveRange = null;
                moved = true;
                //return gameEnd;
                return lastUnit;
            }
            if(map.find(endLoc).hasUnit() || map.find(endLoc).getUnit().getArmy().getSide() != getArmy().getSide())
            {
                Unit enemyu = map.find(endLoc).getUnit();
                boolean destroyed = enemyu.damage(1000, true);
                if(destroyed && enemyu.isRout())
                {
                    if(enemyu.getArmy().getBattle().removeArmy(enemyu.getArmy(),army,false))
                    {
                    	//gameEnd = true;
                    	lastUnit = enemyu;
                    }
                }
                map.move(this, endLoc);
                this.setLocation(endLoc);
                //reset moveRange
                //moveRange = null;
                moved = true;
            }else{
                System.out.println("Cannot move, a friendly unit is already occupying that tile");
            }
        }else{
            System.out.println("Invalid Move");
        }
        
        return lastUnit;
    }
}


