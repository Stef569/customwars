package com.customwars;
/*
 *Joey.java
 *Author: -
 *Contributors:
 *Creation: July 15, 2007
 *
 */
import java.util.ArrayList;

public class Joey extends CO {
    boolean sustainCOP;
    
    //Dang this looks sort of dumb =[
    //
    //So boosted is now a two-dimensonal array
    //
    //The outer array uses a supplying unit's COstore[0]
    //as an index, and the inner array stores the units
    //which have been boosted by the supplying unit.
    //
    //boostCounter keeps track of the amount of units
    //which are capable of boosting. It is also used to
    //assign unique IDs to each of those units.
    //
    private ArrayList<ArrayList<Unit>> boosted;
    private int boostCounter;
    
    //Important to read!
    //
    //During Joey's turn, while his COP or SCOP is active,
    //his APCs and BBoats each get their own ID. They also
    //get additional movement and don't stop from using
    //Boost. The way to interpret an affected unit's COstore[0]
    //is like this:
    //
    //  ...baa
    //
    //    -or-
    //
    //  -...baa
    //
    //  The b's keep track of the unit's used move amount
    //  and the a's represent the unit's boost ID.
    //
    //Wait so why would the COstore[0] value be negative?
    //Negative COstore[0] values are used to indicate APCs
    //that were produced via powers; they will be removed
    //on Joey's next day.
    //
    //Yar! Also remember that the boost counter is starting
    //from 1, so a decrement is required to get the accurate
    //boostID.
    //
    //So use the following formulas:
    //
    //  unit's boost array ID = (Math.abs(owned.COstore[0]) % 100) - 1
    //  unit's used move amount = Math.abs(unit.COstore[0]) / 100
    //
    
    //constructor
    public Joey() {
        name = "Joey";
        id = 65;
        
        String CObiox = "The owner of a large transportation" +
                "company at Green Earth. Joined the army" +
                "to forge more contracts for his business.";
        /*
        String CObiox = "This CO joined the military and rose    " +
                        "through the ranks. But not quickly      " +
                        "enough, and is now a sub-commander.";
         */
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Shipping and Handling";
        String hitx = "Contracts"; //Holds the hit
        String missx = "Strikes"; //Holds the miss
        String skillStringx = "Transports have reduced prices. Infantries can also capture at a slower rate while still inside an APC. ";
        String powerStringx = "APCs appear on every captured city. Transport defense increase and APC capture rate is doubled. Empty spawned APCs are removed next day. "; //Holds the Power description
        String superPowerStringx = "APCs appear on every allied city, ready to move. Supply and repair actions give firepower boosts. Spawned APCs are removed the next day."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] COPowerx =
        {"Cars, trucks, bikes, skateboards! Anything will do!" ,
         "I know these streets like the back of my hand!" ,
         "War is a tricky business, you know?" ,
         "Prepare the APCs, I got an idea!" ,
         "I know the ins and outs of transportation, can you beat that?" ,
         "Peaceful units? I beg to differ!"};
        
        String[] Victoryx =
        {"No time for celebration! we have deliveries to make!" ,
         "My strategies are dangerous, but they sure work" ,
         "Thanks for the help!" };
        
        String[] Swapx =
        {"I said to deliver it 2 hours ago!!...oh, its my turn?" ,
         "Sorry, I can't assure the safety of your units"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Sensei", "Jess", "Sasha", "Drake"}; //Names of COs with special tags
        String[] TagNamesx = {"Deliver Squad", "Endless Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1, 1, 0, 0}; //Number of stars for each special tag.
        int[] TagPercentx = {110, 110, 105, 90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Risky Investments";
        SCOPName = "Hermes' Spear";
        COPStars = 3.0;
        maxStars = 5.0;
        this.army = army;
        style = GREEN_EARTH;
        
        unitCostMultiplier[9] = 90;
        unitCostMultiplier[21] = 90;
        unitCostMultiplier[14] = 90;
        unitCostMultiplier[27] = 90;
        special1 = "Subjugate";
        special2 = "Boost";
        
        cleanStore = false;
        
        boosted = new ArrayList<ArrayList<Unit>>();
        boostCounter = 1;
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender) {
        int firepower = 100;
        
        if(SCOP) {
            firepower += attacker.COstore[0]*20;
        }
        if(COP) {
            firepower += 10;
        }
        
        return firepower;
    }
    
    public void setChange(Unit u){
        u.price = (costMultiplier*unitCostMultiplier[u.unitType])*u.price/10000;
    }
    
    public void unChange(Unit u){
        u.price = 10000*u.price/(costMultiplier*unitCostMultiplier[u.unitType]);
    }
    
    public void dayStart(boolean main) {
        //Mostly taken from Xavier code. Thanks CoconutTank!
        //No problem Vimes, glad to have helped.
        Unit tempUnit = null;
        boolean destroyed = false;
        if(sustainCOP) {
            sustainCOP = false;
            Unit[] u = army.getUnits();
            
            if(main) {
                for(int i = 0; i<u.length; i++) {
                    //Only APCs with negative COstore[0]s get removed
                    //Mostly everything else is left alone
                    if(u[i].COstore[0] <= -1 && u[i].unitType == 9) {
                        tempUnit = u[i];
                        destroyed = u[i].damage(150, true);
                    }
                    u[i].COstore[0] = 0;
                }
            } else {
                for(int i = 0; i<u.length; i++) {
                    //Only APCs with negative altCOstore[0]s get removed
                    //Mostly everything else is left alone
                    if(u[i].altCOstore[0] <= -1 && u[i].unitType == 9) {
                        tempUnit = u[i];
                        destroyed = u[i].damage(150, true);
                    }
                    u[i].COstore[0] = 0;
                }
            }
            if(tempUnit != null) {
                if(destroyed && tempUnit.isRout()) {
                    boolean gameEnd = tempUnit.getArmy().getBattle().removeArmy(tempUnit.getArmy(),null,false);
                }
            }
        }
        
        //Reset the boosted information
        //boostCounter should always start at 1 because 0 == -0 and that's bad
        boostCounter = 1;
        boosted.clear();
    }
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender) {
        int defense = 100;
        
        if(COP && defender.unitType == 9 ||  defender.unitType == 21 || defender.unitType ==14 || defender.unitType == 27) {
            defense += 20;
        }
        if(SCOP || COP) {
            defense += 10;
        }
        
        return defense;
    }
    
    //carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower() {
        COP = true;
        sustainCOP = true;
        
        for(int c = 0; c < army.getBattle().getMap().getMaxCol(); c++) {
            for(int r = 0; r < army.getBattle().getMap().getMaxRow(); r++) {
                Tile targTile = army.getBattle().getMap().find(new Location(c, r));
                
                if(targTile.getTerrain().getName().equals("City") && !(targTile.hasUnit()) && ((Property)targTile.getTerrain()).owner == army) {
                    army.getBattle().placeUnit(army.getBattle().getMap(), targTile, 9, army);
                    targTile.getUnit().COstore[0] = -1;//lole, marked for DEATH
                    targTile.getUnit().setActive(true);
                }
            }
        }
        unitCostMultiplier[9] = 60;
        unitCostMultiplier[21] = 60;
        unitCostMultiplier[14] = 60;
        unitCostMultiplier[27] = 60;
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower() {
        SCOP = true;
        sustainCOP = true; //Destroys APCs if they
        Property[] prop = army.getProperties();
        for(int i = 0; i < prop.length; i++) {
            if(prop[i].getName().equals("City") && !(prop[i].tile.hasUnit())) {
                army.getBattle().placeUnit(army.getBattle().getMap(), prop[i].getTile(), 9, army);
                prop[i].tile.getUnit().damage(30,false);
                prop[i].tile.getUnit().setActive(true);
                //There was stuff here but I think I moved it out into the bottom for loop
                
                //Yar this be important, negatively ID'd APCs will die
                prop[i].tile.getUnit().COstore[0] = -boostCounter;//lole, marked for DEATH
                boostCounter++;
                
                ArrayList<Unit> boostArray = new ArrayList<Unit>();
                boosted.add(boostArray);
            }
        }
        Unit[] u = army.getUnits();
        for(int i = 0 ; i<u.length; i++) {
            if(u[i].unitType == 9 ||  u[i].unitType == 21 || u[i].unitType == 14 || u[i].unitType == 27) {
                //All transports get +1 movement and that change is recorded
                u[i].move++;
                u[i].changed = true;
                
                //APC and BBoat specific effects
                if(u[i].unitType == 9 || u[i].unitType == 21) {
                    u[i].noRepair = true;
                    u[i].noResupply = true;
                    
                    //Yar this be important, positively ID'd APCs and BBoats will
                    //not die. Of course APCs with negative IDs don't count =[
                    if(u[i].COstore[0] >= 0) {
                        u[i].COstore[0] = boostCounter;//lole, NOT marked for DEATH?
                        boostCounter++;
                        
                        ArrayList<Unit> boostArray = new ArrayList<Unit>();
                        boosted.add(boostArray);
                    }
                }
            }
        }
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP() {
        COP = false;
        unitCostMultiplier[9] = 90;
        unitCostMultiplier[21] = 90;
        unitCostMultiplier[14] = 90;
        unitCostMultiplier[27] = 90;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP() {
        SCOP = false;
        //-1 move
        Unit[] u = army.getUnits();
        
        for(int i = 0 ; i<u.length; i++) {
            u[i].noRepair = false;
            u[i].noResupply = false;
            
            if(u[i].changed)
                u[i].move--;
            
            //Give the APCs and BBoats back their movement
            //
            //During the SCOP, if say an APC uses up all of
            //its move points, then it should have 7aa as its
            //COstore[0] value, and 7aa / 100 = 7. Earlier the
            //extra movement is already compensated for, so
            //the unit's move should be -1. Add 7 to that for
            //great justice!
            //
            //This is also important for tags because
            //refreshing a unit with 0 movement is roflmao.
            //
            if(u[i].unitType == 9 || u[i].unitType == 21) {
                int moveVal = Math.abs(u[i].COstore[0]) / 100;
                
                u[i].move += moveVal;
            }
        }
    }
    public boolean canUseSpecial1(Unit owned) {
        Terrain tempT = army.getBattle().getMap().find(owned).getTerrain();
        
        if(tempT instanceof Property) {
            Property targProp = (Property)tempT;
            
            if(!owned.noCapture && owned.unitType == 9 && ((APC)owned).getUnitsCarried() > 0 && targProp.isCapturable()) {
                if(targProp.getOwner() == null || targProp.getOwner().getSide() != army.getSide()) {
                    return true;
                }
            }
        }
        return false;
    }
    //Call to see if a unit is in range of a special ability (used for drawing)
    public boolean canTargetSpecial1(Unit owned, Location target) {
        if(owned.getLocation().equals(target)) {
            return true;
        }
        return false;
    }
    public void useSpecial1(Unit owned, Location target) {
        Property targProp = (Property)army.getBattle().getMap().find(target).getTerrain();
        int captVal = (int)(((Transport)owned).getUnit(1).getDisplayHP() * 0.7 * captureMultiplier / 100);
        
        if(COP) {
            captVal *= 2;
        }
        
        if(targProp.getCapturePoints() <= captVal) 
        {
            //targProp.setOwner(army);
            if(targProp.setOwner(owned.getArmy()))
            {
            	Mission.getBattleScreen().endBattle();
            }
            targProp.setCapturePoints(targProp.getMaxCapturePoints());
        } 
        else 
        {
            int captSet = targProp.getCapturePoints() - captVal;
            targProp.setCapturePoints(captSet);
        }
    }
    
    public boolean canUseSpecial2(Unit owned) {
        if(SCOP && (owned.unitType == 9 ||  owned.unitType == 21)) {
            for(int i=0;i<army.getBattle().getMap().getMaxCol();i++) {
                for(int j=0;j<army.getBattle().getMap().getMaxRow();j++) {
                    //if friendly in resupply range, add resupply to the context menu
                    if(owned.checkAdjacent(new Location(i,j))&&
                            (army.getBattle().getMap().find(new Location(i,j)).hasUnit()&&
                            army.getBattle().getMap().find(new Location(i,j)).getUnit().getArmy()==owned.getArmy()))
                        return true;
                }
            }
            //supply=true;
        }
        return false;
    }
    //Call to see if a unit is in range of a special ability (used for drawing)
    //The unit also needs to be under your control, or is it possible to repair/refuel
    //allied units?
    //Also units which are already in the owned unit's boost array are no longer valid
    //targets. That is specific for BBoats I believe?
    public boolean canTargetSpecial2(Unit owned, Location target) {
        int boostID = (Math.abs(owned.COstore[0]) % 100) - 1;
        
        Unit targUnit = army.getBattle().getMap().find(target).getUnit();
        
        if(targUnit != null && targUnit.unitType != 9 && targUnit.unitType != 21 && !isUnitInBoostedAtID(boostID, targUnit)) {
            if(owned.unitType == 21 && owned.checkAdjacent(target) && targUnit.getArmy() == army) {
                return true;
            }
        }
        if(owned.unitType == 9 && owned.getLocation().equals(target) && army.getBattle().getMap().find(target).getUnit().getArmy() == army)
            return true;
        return false;
    }
    
    public void useSpecial2(Unit owned, Location target) {
        int boostID = (Math.abs(owned.COstore[0]) % 100) - 1;
        
        if(owned.unitType == 21) {
            if(army.getBattle().getMap().find(target).hasUnit() && army.getBattle().getMap().find(target).getUnit().getArmy().getSide() == army.getSide()) {
                Unit u = army.getBattle().getMap().find(target).getUnit();
                //Cannot boost APCs or BBoats
                if(u.unitType != 9 && u.unitType != 21) {
                    u.COstore[0] += 1; //increases COstore[0] by 1
                }
                //Hopefully sets the movement to the movement left after moving.
                
                //Adding the unit to the boosted array
                boosted.get(boostID).add(u);
            }
        }
        //Do NOT boost units which are already in the owned unit's boost array!
        else if (owned.unitType == 9) {
            Tile north = army.getBattle().getMap().find(new Location(target.getCol(), target.getRow()-1));
            Tile south = army.getBattle().getMap().find(new Location(target.getCol(), target.getRow()+1));
            Tile east = army.getBattle().getMap().find(new Location(target.getCol()+1, target.getRow()));
            Tile west = army.getBattle().getMap().find(new Location(target.getCol()-1, target.getRow()));
            
            //if(north.hasUnit() && north.getUnit().getArmy().getSide() == army.getSide())
            if(north.hasUnit() && north.getUnit().getArmy() == army) {
                if(north.getUnit().unitType != 9 && north.getUnit().unitType != 21 && !isUnitInBoostedAtID(boostID, north.getUnit())) {
                    north.getUnit().COstore[0] += 1;
                    //Adding the unit to the boosted array
                    boosted.get(boostID).add(north.getUnit());
                }
            }
            //if(south.hasUnit() && south.getUnit().getArmy().getSide() == army.getSide())
            if(south.hasUnit() && south.getUnit().getArmy() == army) {
                if(south.getUnit().unitType != 9 && south.getUnit().unitType != 21 && !isUnitInBoostedAtID(boostID, south.getUnit())) {
                    south.getUnit().COstore[0] += 1;
                    //Adding the unit to the boosted array
                    boosted.get(boostID).add(south.getUnit());
                }
            }
            //if(east.hasUnit() && east.getUnit().getArmy().getSide() == army.getSide())
            if(east.hasUnit() && east.getUnit().getArmy() == army) {
                if(east.getUnit().unitType != 9 && east.getUnit().unitType != 21 && !isUnitInBoostedAtID(boostID, east.getUnit())) {
                    east.getUnit().COstore[0] += 1;
                    //Adding the unit to the boosted array
                    boosted.get(boostID).add(east.getUnit());
                }
            }
            //if(west.hasUnit() && west.getUnit().getArmy().getSide() == army.getSide())
            if(west.hasUnit() && west.getUnit().getArmy() == army) {
                if(west.getUnit().unitType != 9 && west.getUnit().unitType != 21 && !isUnitInBoostedAtID(boostID, west.getUnit())) {
                    west.getUnit().COstore[0] += 1;
                    //Adding the unit to the boosted array
                    boosted.get(boostID).add(west.getUnit());
                }
            }
        }
    }
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack)
    {
    if(!attack && owned.getUType() == 9)
    {
        army.charge(-damage*owned.price/7000/200.0);
        enemy.getArmy().charge(damage*owned.price/7000/200.0);
    }
    }
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        /*if(index == 5 && repaired.unitType == 9 && COP) //If the unit is an APC, and a unit is loaded into one
        {
            repaired.COstore[0] = 1; //Do not die on me!
        }*/
        
        //Hmm? Oh I see, how interesting. Anyway, need to take the sign of the unit's
        //COstore[0] value into account when manipulating movement and COstore[0].
        if(index == 15 && u.unitType == 9 && COP)
        {
            Infantry temp = new Infantry(0,0,army,army.getBattle().getMap());
            army.getBattle().getMap().remove(temp);
            ((APC)u).load(temp);
        }
        if(SCOP && (u.unitType == 9 || u.unitType == 21) && index == 23) {
            //Grrr, need to abuse mods again...
            //Well I have at least one thing I can depend on:
            //
            //  The boostCounter won't go past 99 because each player can't have more
            //  than 50 units to begin with. This will probably have to be changed in
            //  the future, if the unit cap is lifted. What's nice about that information
            //  is that I can separate the data for movement modification from the
            //  unit's boost ID.
            //
            
            int remainingMP = u.getMoveRange().checkMPLeft(u.getLocation().getCol(), u.getLocation().getRow());
            
            //If the unit used Boost without moving
            if(remainingMP == -1) {
                remainingMP = u.move;
            }
            
            int usedMP = u.move - remainingMP;
            
            if(u.COstore[0] > 0) {
                u.COstore[0] += (usedMP * 100);
            } else {
                u.COstore[0] += (usedMP * -100);
            }
            
            u.move = remainingMP;
            
            u.setActive(true);
        }
    }
    
    //Gets the boosting unit's ID and uses that to find its supplying array.
    //Then the function looks through the array for the target unit. If a
    //match is found, the method returns true. Otherwise, it returns false.
    public boolean isUnitInBoostedAtID(int boostID, Unit targUnit) {
        ArrayList<Unit> boostArray = boosted.get(boostID);
        
        if(boostArray != null && boostArray.contains(targUnit)) {
            return true;
        }
        
        return false;
    }
}