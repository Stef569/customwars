package cwsource;
/*
 *Andy.java
 *Author: Adam Dziuk
 *Contributors: Kosheh
 *Creation: July 4, 2006, 10:17 PM
 *The COP causes her units to suffer no penalty when counterattacking, and to gain +20 defense.
 */

import java.util.ArrayList;

public class Julia extends CO{
    ArrayList location;
    int[] fuel;
    boolean defending = false;
    boolean turn; //Is it Julia's turn?
    ArrayList deadUnits = new ArrayList();
    ArrayList deadUnitsLocation = new ArrayList();
//Constructor
    public Julia() {
        name = "Julia";
        id = 46; //placeholder for Sturm
        
        String CObiox =
                "A devoted CO who embraces Parallel      " +
                "Galaxy's ideals and wishes to unite the " +
                "world. She excels at utilizing          " +
                "propaganda to demoralize her enemies.   ";
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Minister of Propoganda";
        String hitx = "The morning paper"; //Holds the hit
        String missx = "The comics section"; //Holds the miss
        String skillStringx =
                "Units have reduced offensive power and  " +
                "weak counterattacks. However, firepower " +
                "is unaffected by loss of HP.";
        
        String powerStringx ="Units that have been destroyed last turn are temporarily revived with one HP."; //Holds the Power description
        String superPowerStringx = "Enemy units within range of Julia’s units must immediately attack. These units skip their next turn."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Julia is able to manipulate media.  " +
                        "Her units fight as if they had more " +
                        "HP than displayed. Her COP revives  " +
                        "units, while her SCOP allows her to " +
                        "control enemy units." +
                        "";//Holds CO intel on CO select menu, 6 lines max
                        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        String[] TagCOsx = {"Artemis","Talyx","Mina","Thanatos", "Hawke", "Kindle","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Double Think","Dystopia","Carrot and Stick","Thought Police", "Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        
        int[] TagStarsx = {2,1,1,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,105,105,90,90,80}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Your defeat is tomorrow's headline.",
         "Start the presses!",
         "I don’t receive the news. I make the news.",
         "I am the only voice of the people.",
         "Let us see how devoted your people are to your cause.",
         "You will come to embrace our ideals." };
        
        String[] Victoryx =
        {"The proof of our might will forever be etched in your minds.",
         "How foolish it was of you to try to defy us.",
         "This victory will be pleasing news to our citizens."};
        
        
        String[] Swapx =
        {"Our cause is a just one.",
         "I advise you to yield." };
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Rallying Cry";
        SCOPName = "Media Mayhem";
        COPStars = 3.0;//3/6
        maxStars = 7.0;
        this.army = army;
        style = PARALLEL_GALAXY;
        location = new ArrayList();
        fuel = new int[500]; //Julia's SCOP will scan no more than 500 units.
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(!defending) //If the unit is attacking
        {
            if (SCOP||COP)
                return (int)(95/(attacker.getDisplayHP()/10.0-(attacker.getDisplayHP()/1100.0)));
            else
            {
                logger.info(""+ (int)(85/(attacker.getDisplayHP()/10.0-(attacker.getDisplayHP()/1100.0))));
                return (int)(85/(attacker.getDisplayHP()/10.0-(attacker.getDisplayHP()/1100.0)));
            }
        } else //if the unit is defending.
        {
            if (SCOP||COP)
                return (int)(75/(attacker.getDisplayHP()/10.0-(attacker.getDisplayHP()/1100.0)));
            else
                return (int)(65/(attacker.getDisplayHP()/10.0-(attacker.getDisplayHP()/1100.0)));
        }
    } //Attack damage is always 80%
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP && turn)
            return 130;
        if (COP||SCOP)
            return 110;
        else
            return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    public void dayStart(boolean main ){
        turn = true;
        location.clear();
        int i = 0;
        int t = 0;
        Army[] enemyArmy = army.getBattle().getArmies();
        
        for(int s = 0; s<enemyArmy.length; s++) {
            Unit[] u = enemyArmy[s].getUnits();
            if(u != null)
                if(enemyArmy[s].getSide() != army.getSide())
                    for(t = 0; t<u.length; t++) {
                u[t].enemyCOstore[statIndex][0] = i + t;
                location.add(u[i + t].getLocation());
                fuel[i+t] = u[i+t].getGas();
                    }
            i+=t;
        }
    }
    
    public void dayEnd(boolean main ) {
        turn = false;
        deadUnits.clear();
        deadUnitsLocation.clear();
    }
    
    public void COPower() {
        COP = true;
        for(int i = 0; i < deadUnits.size(); i ++) {
            if(!army.getBattle().getMap().find((Location)deadUnitsLocation.get(i)).hasUnit()) {
                army.getBattle().getMap().addUnit((Location)deadUnitsLocation.get(i), (Unit)deadUnits.get(i));
                army.addUnit((Unit)deadUnits.get(i));
                army.getBattle().getMap().find((Location)deadUnitsLocation.get(i)).getUnit().setActive(true);
            } else {
                boolean placed = false;
                int radius = 1;
                int offset = 0;
                while(!placed) {
                    for(int s=-1*radius; s <= radius; s++){
                        for(int j=-1*offset; j <= offset; j++){
                            Location temp = new Location(((Location)deadUnitsLocation.get(i)).getCol()+j,((Location)deadUnitsLocation.get(i)).getRow()+s);
                            if(army.getBattle().getMap().onMap(temp) && !army.getBattle().getMap().find(temp).hasUnit() && army.getBattle().getMap().find(temp).getTerrain().moveCost(((Unit)deadUnits.get(i)).moveType) != -1) {
                                army.getBattle().placeUnit(army.getBattle().getMap(),army.getBattle().getMap().find(temp),((Unit)deadUnits.get(i)).unitType, army);
                                army.getBattle().getMap().find(temp).getUnit().damage(100,false);
                                army.getBattle().getMap().find(temp).getUnit().setActive(true);
                                placed = true;
                            }
                            if(placed)
                                break;
                        }
                        if(placed)
                            break;
                        if(i<0)offset++;
                        else offset--;
                    }
                    radius++;
                    offset = 0;
                }
            }
        }
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        mayhem = true;
        
        Army[] armies = army.getBattle().getArmies();
        Unit[] u, alliedUnit;
        alliedUnit = army.getUnits();
        for(int t = 0; t < armies.length; t++) { //Goes through the armies
            if(armies[t].getSide() != army.getSide() && armies[t].getUnits() != null) { //If hostile
                u = armies[t].getUnits(); //gets the units of army being targetted
                for(int s = 0; s < u.length; s++) { //Cycles through units
                    for(int i = 0; i < alliedUnit.length; i++) { //Cycles through allied units
                        u[s].calcMoveTraverse();
                        if(u[s].checkDisplayFireRange(alliedUnit[i].getLocation()) && !army.getBattle().getFog(u[s].getLocation().getCol(), u[s].getLocation().getRow())) {
                            u[s].setActive(true);
                            u[s].noUnload = true;
                            u[s].noJoin = true;
                            u[s].noLoad = true;
                            u[s].noExplode = true;
                            u[s].noWait = true;
                            break;
                        }
                    }
                }//This ends the unit for loop
                //This ends the if statement checking cities
            }
        }
    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        mayhem = false;
        
        Army[] armies = army.getBattle().getArmies();
        Unit[] u, alliedUnit;
        alliedUnit = army.getUnits();
        for(int t = 0; t < armies.length; t++) { //Goes through the armies
            if(armies[t].getSide() != army.getSide() && armies[t].getUnits() != null) { //If hostile
                u = armies[t].getUnits(); //gets the units of army being targetted
                for(int s = 0; s < u.length; s++) { //Cycles through units
                    u[s].noUnload = false;
                    u[s].noJoin = false;
                    u[s].noLoad = false;
                    u[s].noExplode = false;
                    u[s].noWait = false;
                }//This ends the unit for loop
                //This ends the if statement checking cities
            }
        }
    }
    
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if (!attack) //If Julia's unit is defending from the first attack'
        {
            defending = true;
            if(turn) //If Julia is counterattacking on Julia's turn this means an enemy is attacking!
            {
                enemy.setActive(false);
            }
            if(destroy) //If the defending unit (Julia's unit) will be destroyed
            {
                deadUnits.add(owned);
                deadUnitsLocation.add(owned.getLocation());
            }
        }
        
    }
    
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) {
        if(!attack)//If Julia's unit is defending from the counter attack
        {
            if(destroy) //If the attacking unit (Julia's unit) will be destroyed
            {
                deadUnits.add(owned);
                deadUnitsLocation.add(owned.getLocation());
            }
        }
    }
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        defending = false;
    }
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        if(SCOP && u.getArmy().getSide() == army.getSide()) {
            Army[] armies = army.getBattle().getArmies();
        }
    }
    public void afterEnemyAction(Unit u, int index, Unit repaired, boolean main) 
    {
    	//[CHANGED]
        if(turn && index != 1 && index != 20) {
            army.getBattle().getMap().move(u, (Location)(location.get(u.enemyCOstore[statIndex][0])));
            u.setLocation((Location)(location.get(u.enemyCOstore[statIndex][0])));
            u.gas = (fuel[u.enemyCOstore[statIndex][0]]);
            u.setActive(true);
            logger.info("Tried to move!");
        }
    }
}