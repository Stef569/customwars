package cwsource;
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
        String skillStringx = "Due to lack of experience, this commander is equally proficient with all units.";
        String powerStringx = "Firepower and defense rises slightly."; //Holds the Power description
        String superPowerStringx = "Firepower and defense rises slightly. Movement is also increased by one for all units."; //Holds the Super description
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
        if(SCOP)return 110+ attacker.COstore[0]*10;
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
                    if(prop[s].cp>5) {
                        prop[s].cp -= 5;
                        capturePointLoss[i] += 5;
                    } else {
                        capturePointLoss[i] += prop[s].cp - 1;
                        prop[s].cp = 1;
                    }
                    income[i] += (army.getBattle().getBattleOptions().getFundsLevel() * armies[i].getCO().fundingMultiplier)/100;
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
                    if(u[s].noRepair)
                        u[s].enemyCOstore[statIndex]+=1;
                    if(u[s].noResupply)
                        u[s].enemyCOstore[statIndex]+=2;
                    if(u[s].noCityRepair)
                        u[s].enemyCOstore[statIndex]+=4;
                    if(u[s].noCityResupply)
                        u[s].enemyCOstore[statIndex]+=8;
                    u[s].noRepair = true;
                    u[s].noResupply = true;
                    u[s].noCityRepair = true;
                    u[s].noCityResupply = true;
                }
            }
        }
        Unit[] u = army.getUnits();
        for(int i = 0; i<u.length; i++) {
            u[i].resupply();
            for(int s = 0; s<4; s++) {
                
                if(u[i].getDisplayHP() == 10){
                    u[i].COstore[0]++;
                } else {
                    u[i].heal(10);
                    for(int t = 0; t< armies.length; t++) {
                        //This one subtracts fund equal to 5% of the price. If it cannot be subtracted, the funds is instead
                        //deducted from the income.
                        if(armies[t].getSide() != army.getSide()) {
                            if(armies[t].getFunds() > u[i].price/20)
                            {
                                armies[t].removeFunds(u[i].price/20);
                            }
                            else{
                                incomePenalties[t] += u[i].price/20;
                                armies[t].getCO().incomePenalty += u[i].price/20;
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
                    
                    if(!prop[s].tile.hasUnit()) {
                        prop[s].cp = prop[s].totalcp;
                    }
                    if(prop[s].tile.hasUnit() && (prop[s].tile.getUnit().getArmy() != army)) {
                        prop[s].cp = prop[s].totalcp;
                    }
                    if(prop[s].tile.hasUnit()) {
                        if(prop[s].tile.getUnit().getArmy().getSide() == army.getSide()){
                            if(prop[s].tile.getUnit().moveType != prop[s].tile.getUnit().MOVE_INFANTRY  && prop[s].tile.getUnit().moveType != prop[s].tile.getUnit().MOVE_MECH) {
                                prop[s].cp = prop[s].totalcp;
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
        for(int i = 0; i< owned.length; i++)
        {
            if(army.getBattle().getMap().find(owned[i]).getTerrain().urban)
            {
                System.out.println("one");
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).repairAir && (owned[i].moveType == owned[i].MOVE_AIR))
                {
                    System.out.println("two");
                   if(!owned[i].noCityResupply && !owned[i].noResupplied)
                        owned[i].resupply();
                }
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).repairSea && ((owned[i].moveType == owned[i].MOVE_SEA) || (owned[i].moveType == owned[i].MOVE_TRANSPORT)))
                {
                    System.out.println("two");
                   if(!owned[i].noCityResupply && !owned[i].noResupplied)
                        owned[i].resupply();
                }
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).repairLand && ((owned[i].moveType == owned[i].MOVE_HOVER) || (owned[i].moveType == owned[i].MOVE_MECH)|| (owned[i].moveType == owned[i].MOVE_INFANTRY)|| (owned[i].moveType == owned[i].MOVE_PIPE)|| (owned[i].moveType == owned[i].MOVE_TREAD)|| (owned[i].moveType == owned[i].MOVE_TIRE)))
                {
                    System.out.println("two");
                   if(!owned[i].noCityResupply && !owned[i].noResupplied)
                        owned[i].resupply();
                }
                if(((Property)army.getBattle().getMap().find(owned[i]).getTerrain()).repairPipe && (owned[i].moveType == owned[i].MOVE_PIPE))
                {
                    System.out.println("two");
                   if(!owned[i].noCityResupply && !owned[i].noResupplied)
                        owned[i].resupply();
                }
            }
        }
        if(army.getBattle().getDay() > 1) {
            if(main) {
                Army[] armies = army.getBattle().getArmies();
                for(int i = 0; i < armies.length; i++) {
                    if(armies[i].getSide() != army.getSide()) {
                        armies[i].getCO().incomePenalty -= incomePenalties[i];
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
                        if(u[s].enemyCOstore[statIndex]%2 == 1)
                            u[s].noRepair = false;
                        if(u[s].enemyCOstore[statIndex]/2%2 == 1)
                            u[s].noResupply = false;
                        if(u[s].enemyCOstore[statIndex]/4%2 == 1)
                            u[s].noCityRepair = false;
                        if(u[s].enemyCOstore[statIndex]/8%2 == 1)
                            u[s].noCityResupply = true;
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
                        if(prop[s].cp < prop[s].totalcp) {
                            //if the enemy property is being captured
                            if((prop[s].tile.hasUnit() && prop[s].tile.getUnit().getArmy() == army) || COP) {
                                //if the capturing unit is of Varlot's army, or if the COP is activated.
                                armies[i].getCO().incomePenalty += ((prop[s].totalcp-prop[s].cp)*army.getBattle().getBattleOptions().getFundsLevel())/prop[s].totalcp;
                                incomePenalties[i] += ((prop[s].totalcp-prop[s].cp)*army.getBattle().getBattleOptions().getFundsLevel())/prop[s].totalcp;
                            }
                        }
                    }
                }
            }
        }
    }
}
