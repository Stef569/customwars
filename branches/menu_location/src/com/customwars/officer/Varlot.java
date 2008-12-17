package com.customwars.officer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.Army;
import com.customwars.CO;
import com.customwars.Property;
import com.customwars.Unit;
/*
 *Blandie.java
 *Author: Urusan
 *Contributors:
 *Creation: December 11, 2006
 *A bland CO, perhaps a generic commanding officer?
 */

public class Varlot extends CO{
    int[] incomePenalties = {0};
    int[] capturePointLoss = {0};
    int[] numCities = {0};
    boolean sustain = false;
	final static Logger logger = LoggerFactory.getLogger(Varlot.class); 
	
    
    //constructor
    public Varlot() {
        name = "Varlot";
        id = 66;
        
        String CObiox = "This CO joined the military and rose    " +
                "through the ranks. But not quickly      " +
                "enough, and is now a sub-commander.";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Unscrupulous Mogul";
        String hitx = "Caviar"; //Holds the hit
        String missx = "Ethics"; //Holds the miss
        String skillStringx = "Varlot reduces the income from a property he captures by an amount proportional to its Capture Points.";
        String powerStringx = "Enemy properties lose 5 capture points. Varlot gains the money that would be lost this way."; //Holds the Power description
        String superPowerStringx = "All units are healed for 4 HP, gaining firepower for each point over 10HP they'd be healed. The enemy pays for these repairs, and cannot repair or resupply for a turn."; //Holds the Super description
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
        {"Move out!",
         "Attack!",
         "Forward march!",
         "Onward to victory!",
         "Never surrender!",
         "Push forward!"};
        
        String[] Victoryx =
        {"Mission complete.",
         "Another day, another battle won.",
         "Maybe I'll be up for promotion soon..."};
        
        String[] Swapx =
        {"I won't let you down!",
         "I am assuming command"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        //No special tags
        String[] TagCOsx = {"Nell"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0}; //Number of stars for each special tag.
        int[] TagPercentx = {100}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Aquisition";
        SCOPName = "Hostile Takeover";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = PARALLEL_GALAXY;
        
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP)return 110+ attacker.getCOstore()[0]*10;
        if(COP)return 110;
        return 100;
        
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)return 110;
        return 100;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Army[] armies = army.getBattle().getArmies();
        int[] income = new int[armies.length];
        capturePointLoss = new int[armies.length];
        numCities = new int[armies.length];
        for(int i = 0; i < armies.length; i++) {
            if(armies[i].getSide() != army.getSide()) {
                Property[] prop = armies[i].getProperties();
                for(int s = 0; s<prop.length; s++) {
                    if(prop[s].getCp()>5) {
                        prop[s].setCp(prop[s].getCp() - 5);
                        capturePointLoss[i] += 5;
                    } else {
                        capturePointLoss[i] += prop[s].getCp() - 1;
                        prop[s].setCp(1);
                    }
                    income[i] += (army.getBattle().getBattleOptions().getFundsLevel() * armies[i].getCO().getFundingMultiplier())/100;
                    numCities[i] += 20;
                }
            }
        }
        int index = 0; //stores the index within the array of the army with the maximum income.
        for(int i = 0; i < income.length; i++) {
            if(income[i] > income[index])
                index = i;
        }
        army.addFunds((income[index] * capturePointLoss[index])/(numCities[index]));
    }
    
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Army[] armies = army.getBattle().getArmies();
        for(int i = 0; i< armies.length; i++) {
            if(armies[i].getSide() == army.getSide()) {
                Unit[] u = armies[i].getUnits();
                for(int s = 0; s<armies.length; s++) {
                    if(u[s].isNoRepair())
                        u[s].getEnemyCOstore()[statIndex][0]+=1;
                    if(u[s].isNoResupply())
                        u[s].getEnemyCOstore()[statIndex][0]+=2;
                    if(u[s].isNoCityRepair())
                        u[s].getEnemyCOstore()[statIndex][0]+=4;
                    if(u[s].isNoCityResupply())
                        u[s].getEnemyCOstore()[statIndex][0]+=8;
                    u[s].setNoRepair(true);
                    u[s].setNoResupply(true);
                    u[s].setNoCityRepair(true);
                    u[s].setNoCityResupply(true);
                }
            }
        }
        Unit[] u = army.getUnits();
        for(int i = 0; i<u.length; i++) {
            u[i].resupply();
            for(int s = 0; s<4; s++) {
                
                if(u[i].getDisplayHP() == 10){
                    u[i].getCOstore()[0]++;
                } else {
                    u[i].heal(10);
                    for(int t = 0; t< armies.length; t++) {
                        //This one subtracts fund equal to 5% of the price. If it cannot be subtracted, the funds is instead
                        //deducted from the income.
                        if(armies[t].getSide() != army.getSide()) {
                            if(armies[t].getFunds() > u[i].getPrice()/20) {
                                armies[t].removeFunds(u[i].getPrice()/20);
                            } else{
                                incomePenalties[t] += u[i].getPrice()/20;
                                armies[t].getCO().setIncomePenalty(
										armies[t].getCO().getIncomePenalty()
												+ (u[i].getPrice()/20));
                            }
                        }
                    }
                }
            }
        }
        sustain = true;
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Army[] armies = army.getBattle().getArmies();
        
        numCities = new int[armies.length];
        for(int i = 0; i < armies.length; i++) {
            if(armies[i].getSide() != army.getSide()) {
                Property[] prop = armies[i].getProperties();
                for(int s = 0; s<prop.length; s++) {
                    //if the enemy property tile has no units on it, or has a unit and that unit is not of Varlot's', or if it does have a unit, and that unit is of Varlot's, and tat unit has infantry or mech units.
                    //What a headache. ._.
                    
                    if(!prop[s].getTile().hasUnit()) {
                        prop[s].setCp(prop[s].getTotalcp());
                    }
                    if(prop[s].getTile().hasUnit() && (prop[s].getTile().getUnit().getArmy() != army)) {
                        prop[s].setCp(prop[s].getTotalcp());
                    }
                    if(prop[s].getTile().hasUnit()) {
                        if(prop[s].getTile().getUnit().getArmy().getSide() == army.getSide()){
                            if(prop[s].getTile().getUnit().getMoveType() != prop[s].getTile().getUnit().MOVE_INFANTRY  && prop[s].getTile().getUnit().getMoveType() != prop[s].getTile().getUnit().MOVE_MECH) {
                                prop[s].setCp(prop[s].getTotalcp());
                            }
                        }
                    }
                }
            }
        }
        
        
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public void dayStart(boolean main){
        Unit[] owned = army.getUnits();
        if(owned != null)
            for(int i = 0; i< owned.length; i++) {
            if(army.getBattle().getMap().find(owned[i]).getTerrain().isUrban()) {
                logger.info("one");
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).isRepairAir() && (owned[i].getMoveType() == owned[i].MOVE_AIR)) {
                    logger.info("two");
                    if(!owned[i].isNoCityResupply() && !owned[i].isNoResupplied())
                        owned[i].resupply();
                }
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).isRepairSea() && ((owned[i].getMoveType() == owned[i].MOVE_SEA) || (owned[i].getMoveType() == owned[i].MOVE_TRANSPORT))) {
                    logger.info("two");
                    if(!owned[i].isNoCityResupply() && !owned[i].isNoResupplied())
                        owned[i].resupply();
                }
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).isRepairLand() && ((owned[i].getMoveType() == owned[i].MOVE_HOVER) || (owned[i].getMoveType() == owned[i].MOVE_MECH)|| (owned[i].getMoveType() == owned[i].MOVE_INFANTRY)|| (owned[i].getMoveType() == owned[i].MOVE_PIPE)|| (owned[i].getMoveType() == owned[i].MOVE_TREAD)|| (owned[i].getMoveType() == owned[i].MOVE_TIRE))) {
                    logger.info("two");
                    if(!owned[i].isNoCityResupply() && !owned[i].isNoResupplied())
                        owned[i].resupply();
                }
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).isRepairPipe() && (owned[i].getMoveType() == owned[i].MOVE_PIPE)) {
                    logger.info("two");
                    if(!owned[i].isNoCityResupply() && !owned[i].isNoResupplied())
                        owned[i].resupply();
                }
            }
            }
        if(army.getBattle().getDay() > 1) {
            if(main) {
                Army[] armies = army.getBattle().getArmies();
                for(int i = 0; i < armies.length; i++) {
                    if(armies[i].getSide() != army.getSide()) {
                        armies[i].getCO()
								.setIncomePenalty(
										armies[i].getCO().getIncomePenalty()
												- incomePenalties[i]);
                    }
                }
            }
        }
        if(sustain) {
            Army[] armies = army.getBattle().getArmies();
            for(int i = 0; i< armies.length; i++) {
                if(armies[i].getSide() == army.getSide()) {
                    Unit[] u = armies[i].getUnits();
                    for(int s = 0; s<armies.length; s++) {
                        //wat teh BOOLEAN ARRAY
                        if(u[s].getEnemyCOstore()[statIndex][0]%2 == 1)
                            u[s].setNoRepair(false);
                        if(u[s].getEnemyCOstore()[statIndex][0]/2%2 == 1)
                            u[s].setNoResupply(false);
                        if(u[s].getEnemyCOstore()[statIndex][0]/4%2 == 1)
                            u[s].setNoCityRepair(false);
                        if(u[s].getEnemyCOstore()[statIndex][0]/8%2 == 1)
                            u[s].setNoCityResupply(true);
                    }
                }
            }
        }
    }
    public void dayEnd(boolean main){
        if(main) {
            
            Army[] armies = army.getBattle().getArmies();
            incomePenalties = new int[armies.length];
            for(int i = 0; i < armies.length; i++) {
                if(armies[i].getSide()!=army.getSide()) {
                    Property[] prop = armies[i].getProperties();
                    for(int s = 0; s<prop.length; s++) {
                        if(prop[s].getCp() < prop[s].getTotalcp()) {
                            //if the enemy property is being captured
                            if((prop[s].getTile().hasUnit() && prop[s].getTile().getUnit().getArmy() == army) || COP) {
                                //if the capturing unit is of Varlot's army, or if the COP is activated.
                                armies[i].getCO().setIncomePenalty(
										armies[i].getCO().getIncomePenalty()
												+ (((prop[s].getTotalcp()-prop[s].getCp())*army.getBattle().getBattleOptions().getFundsLevel())/prop[s].getTotalcp()));
                                incomePenalties[i] += ((prop[s].getTotalcp()-prop[s].getCp())*army.getBattle().getBattleOptions().getFundsLevel())/prop[s].getTotalcp();
                            }
                        }
                    }
                }
            }
        }
    }
}
