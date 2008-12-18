package com.customwars.officer;

import com.customwars.map.location.Property;
import com.customwars.unit.Unit;

public class Sanjuro extends CO{
    int atk; //attack bonus or penalty
    //constructor
    public Sanjuro() {
        name = "Sanjuro";
        setId(61);
        
        String CObiox = "A respected mercenary from Yellow Comet. Once he accepts a job, he sees it through to the end no matter how difficult or dangerous it becomes. ";
        //This is seperated into blocks 40 characters long!
        //Use this as a guide for a better look proper word-wrapping.
        String titlex = "Asian Thrift";
        String hitx = "Honest work"; //Holds the hit
        String missx = "Shady business"; //Holds the miss
        String skillStringx = "A keen mind for his finances, he spends more for better equipment when there's a surplus and focuses on getting the best deal when he's pressed for money.";
        String powerStringx = "When his units take combat damage, he recieves a portion of the value of the damage in funds."; //Holds the Power description
        String superPowerStringx = "When a unit is built, units of the same type recieve a firepower and defense boost, unhindered by terrain, and production costs reduced by half."; //Holds the Super description
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
        {"My services are expensive, and worth every penny.",
"Consider this a free sample!",
"You're finished! Surrender now!",
"War is my livelihood. Can you handle my level of experience?",
"I was born for battle. I have no limits!",
"If you think this is strictly business, think again. The battlefield calls to me!" };
        
        String[] Victoryx =
        {"I have a reputation to keep, you know.",
"Another job well done...where's the next fight?",
"Did you expect anything less from a hardened mercenary?" };
        
        String[] Swapx =
        {"Time to earn my pay...",
"Let's get down to business." };
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        //No special tags
        String[] TagCOsx = {"Hachi", "Javier", "Kanbei", "Sasha", "Varlot", "Graves", "Yukio", "Eric", "Edward", "Carmen", "Mary"}; //Names of COs with special tags
        String[] TagNamesx = {"Risky Business", "Knights Errant", "Samurai Steel", "Break the Bank", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {02,2,1,1,0,0,0,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120,115, 110,110,80,85,90,90,90,90,90}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Heart of Gold";
        SCOPName = "Buying Power";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = YELLOW_COMET;
        atk = 0;
    }
    public void setChange(Unit u){
        u.setPrice((costMultiplier*unitCostMultiplier[u.getUnitType()])*u.getPrice()/10000);
    }
   
    public void unChange(Unit u){
        u.setPrice(10000*u.getPrice()/(costMultiplier*unitCostMultiplier[u.getUnitType()]));
    }
    
    public void dayStart(boolean main ) {
        Property[] prop = army.getProperties();
        int store = 0; //stores the income
        atk = 0;
        int initialstore;
        int funds = army.getFunds();
        int initialfunds = army.getFunds();
        for(int i = 0; i<prop.length; i++) {
            store+=prop[i].getIncome()*army.getBattle().getBattleOptions().getFundsLevel()/1000*getFunding()/100;
        }
        funds += store;

        //deals with repairs.
        if(prop!=null){
            for(int i = 0; i < prop.length; i++){
                if(prop[i].getTile().getUnit()!=null){
                    Unit temp = prop[i].getTile().getUnit();
                    if(temp.getArmy() == prop[i].getOwner()){
                        if((temp.getMType() == temp.MOVE_AIR && prop[i].canRepairAir())||
                                ((temp.getMType() == temp.MOVE_SEA || temp.getMType() == temp.MOVE_TRANSPORT || temp.getMType() == temp.MOVE_HOVER) && prop[i].canRepairSea())||
                                (temp.getMType() != temp.MOVE_AIR && prop[i].canRepairLand())||
                                (temp.getMType() == temp.MOVE_PIPE && prop[i].canRepairPipe())){
                            for(int numHeals = army.getCO().getRepairHp(); numHeals > 0; numHeals--){
                                if(temp.getDisplayHP() != 10 && temp.getPrice()/10 <= funds && !temp.isNoCityRepair()){
                                    funds -= (temp.getPrice()/10);
                                }
                            }
                            }
                        }
                    }
                }
            }
            initialfunds = funds;
            initialstore = store;
        if(initialstore<funds) { //If more funds than store
            int exceed = funds-initialstore; //How much does funds exceed initialstore?
            
            exceed= exceed - (int)(initialstore*0.1);
            for(int i = 0; exceed>=0 && i<10; i++) {
                atk+=2;
                costMultiplier+=1;
                exceed= exceed - (int)(initialstore*0.1);
            }
        }
        else{
            int exceed = initialstore-funds; //How much does income exceed funds??
            exceed-=(int)(initialstore*0.1);
            for(int i = 0; exceed>=0 && i<10; i++) {
                atk-=1;
                costMultiplier-=2;
                exceed-=(int)(initialstore*0.1);
            }
        }
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        
        if(SCOP && attacker.getCOstore()[0] ==1)
            return 150 + atk;
        if(COP||SCOP)return 110 + atk;
        return 100 + atk;
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP && defender.getCOstore()[0] ==1)
            return 130;
        if(SCOP || COP)return 110;
        return 100;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        setFriendlySalvage(40);
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;

    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        setFriendlySalvage(0);
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i<u.length; i++)
        {
            u[i].setPerfectMovement(false);
        }
        for(int i =0; i<unitCostMultiplier.length; i++)
            unitCostMultiplier[i]=100;
    }
    public void enemyDayEnd(boolean main ) 
    {
    costMultiplier = 100;
    }
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        if(index == 15 && SCOP)
        {
            u.getCOstore()[0] = 1;
            Unit[] temp = army.getUnits();
            for(int i = 0; i< temp.length; i++)
            {
                if(temp[i].getUnitType() == u.getUnitType())
                    temp[i].setPerfectMovement(true);
            }
            unitCostMultiplier[u.getUnitType()]=50;
        }
    }
}
