package com.customwars.officer;

import com.customwars.unit.Unit;

public class Conrad extends CO{
    int luckPool, tempPool = 0;
    int t = 0;
    int pseudoLuck = 0;
    boolean tested = false;
    boolean counter = false;
    //constructor
    public Conrad() {
        name = "Conrad";
        setId(41);
       
        String CObiox = "A damage analyst from Green Earth who favors precise tactics. Very intelligent, but inexperienced.";             //Holds the condensed CO bio'
        String titlex = "Target Locked";
        String hitx = "Techno"; //Holds the hit
        String missx = "Blackouts"; //Holds the miss
        String skillStringx = "Pre-battle damage estimations are completely accurate. Firepower increases as more units are within vision range. Weak counters.";
        String powerStringx = "Unit vision is extended. Firepower increases even more as more units are within vision range."; //Holds the Power description
        String superPowerStringx = "Conrad's first attack deals +50% luck damage. If Conrad would deal over 100%, the damage is rolled over to the next engagement."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Conrad's damage estimations are     " +
                        "always correct. His firepower rises " +
                        "as more units are able to see the   " +
                        "defending unit. His COP increases   " +
                        "the potency of his D2D, while his   " +
                        "SCOP grants him damage rollover.";//Holds CO intel on CO select menu, 6 lines max
        intel = intelx;
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        
        String[] COPowerx =
        {"You might be more experienced, but that doesn't mean I can't teach you a thing or two!" ,
        "I won't go down that easily! This battle isn't even close to finished!" ,
        "The chances of you pulling through are pretty slim. Believe me, I would know!" ,
        "My attack plan is perfect! Even I can't mess it up!" ,
        "Your strategy doesn't add up. Mine practically multiplies!" ,
        "Don't make me angry! ...It makes me screw up my numbers." };
       
        String[] Victoryx =
        {"Solid tactics get solid results!" ,
        "That was easier than I expected. Were my numbers off?" ,
        "Either I'm getting better, or you guys are getting worse!" };
       
        String[] Swapx =
        {"Hey, hold on! I still need to make some calculations!" ,
        "The real test starts now!"};
       
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
       
        //No special tags
        String[] TagCOsx = {"Sonja", "Peter", "Hawke", "Falcone"}; //Names of COs with special tags
        String[] TagNamesx = {"Master Plan", "Show of Skill", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,90,90}; //Percent for each special tag.
       
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
       
        COPName = "Calculated Strike";
        SCOPName = "Precision Assault";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = GREEN_EARTH;
       
        setPositiveLuck(0); //Conrad, my pal, you shall have all the damage figured into your damage.
        setCounterAttack(80); //less than 100 for counterAttack nullifies all counters, for some reason.
    }
   
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        setBonusDamage(0);
        setBonusDamage(getBonusDamage() + (attacker.getDisplayHP()/2));
        setBonusDamage(getBonusDamage() + luckPool);
        
        Unit[] u = army.getUnits();
        Unit current;
        
        if(defender!=null)
        {
            if(attacker.getMaxRange() == 1 && (attacker.getMoveType() != attacker.MOVE_INFANTRY || attacker.getMoveType() != attacker.MOVE_MECH))
            { //Direct unit?
                for (int i = 0; i< u.length; i++)
                {
                    t = Math.abs(u[i].getLocation().getRow() - defender.getLocation().getRow()) + Math.abs(u[i].getLocation().getCol() - defender.getLocation().getCol());
                    //How far away is defender?
                    if(u[i].getVision() >= t)
                    {//Is the defender within the sight range of this unit?
                        if(COP)
                            setBonusDamage(getBonusDamage() + 4);
                        else
                            setBonusDamage(getBonusDamage() + 1);
                    }
                }
            }
        }
       
        

        
        if(SCOP || COP)
            return 110;
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
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].setVision(u[i].getVision() + 1);
                u[i].setChanged(true);
            }
        }
        //+1 vision
        army.getBattle().calculateFoW();
    }
   
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        luckPool = 50;
    }
   
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].isChanged()){
                    u[i]
							.setVision(u[i].getVision() - 1);
                    u[i].setChanged(false);}
            }
        }
        army.getBattle().calculateFoW();
    }
   
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        luckPool = 0;
    }
   
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean attack)
    {  
        if(attack)
        {
            luckPool = 0;
            if(damage> 100)
            {   
                luckPool += (damage)-100;
            }
        }
    }
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean attack)
    {  
        if(attack)
        {
            luckPool = 0;
            if(damage> 100)
            {   
                luckPool += (damage)-100;
            }
        }
    }
    
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {
        counter = false;
    }
   
}
