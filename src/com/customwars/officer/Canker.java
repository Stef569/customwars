package com.customwars.officer;

import com.customwars.DialogueBox;
import com.customwars.unit.Army;
import com.customwars.unit.Unit;

public class Canker extends CO{
    boolean activeSuper;
    int goldRequirement;
    int damage;
//constructor
    public Canker() {
        name = "Grimm";
        setId(18);
        
        String CObiox = "A Yellow Comet commander with a dynamic personality. Could care less about the details. Nicknamed \"Lightning Grimm.\"";             //Holds the condensed CO bio'
        String titlex = "Kamikaze!";
        String hitx = "Donuts"; //Holds the hit
        String missx = "Planning"; //Holds the miss
        String skillStringx = "Firepower of all units is increased, thanks to his daredevil nature, but thier defenses are reduced.";
        String powerStringx = "Increases the attack of all units."; //Holds the Power description
        String superPowerStringx = "Greatly increases the attack of all units."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Grimm's whole army boasts unrivalled" +
                        "offensive ratings but his defense   " +
                        "falters.  His powers simply boost   " +
                        "his already high attack even higher.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Sensei","Sasha","Javier","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Rolling Thunder","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,105,90}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        
        String[] COPowerx =
        {"Things are lookin' Grimm for you! Harrrrr!",
         "You're about to enter a world of pain!!",
         "Outta the way! I got crushin' to do!",
         "Oooh, yeah!!",
         "Gwar har har!! Go cry like a little girl!!",
         "What a pencil neck!!",};
        
        String[] Victoryx =
        {"Wanna throw down again? Oooh yeah!",
         "Gwar har har! Hit the road, slick!",
         "Fear the lightning!"};
        
        String[] Swapx =
        {"Oooh yeah!! Now, I mean business!!",
         "I'll deal with these losers!!"};
        
        setSwap(Swapx);
        setCOPower(COPowerx);
        Victory = Victoryx;
        
        COPName = "Knuckleduster";
        SCOPName = "Haymaker";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = YELLOW_COMET;
        setCleanStore(false);
        repairHp = 0;
        
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
            if (SCOP)
                return 190;
            if (COP)
                return 155;
            else    
                return 115;    
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    public void dayStart(boolean main)
    {
    if(main){
        Unit[] u = army.getUnits();
        for(int i = 0; i<u.length; i++)
        {
            u[i].getCOstore()[0]++;
            u[i].heal(5);
            u[i].heal((u[i].getCOstore()[0]-1)*3);
        }
        if(activeSuper)
        {
            DialogueBox info = new DialogueBox(army.getBattle(), "Gold requirement: " + goldRequirement + "Damage: " + damage);
            info.setup();
            info.start();
        }
    }
    }
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){

        if(COP || SCOP)
            return 110;
        else
            return 100;
    }
    
//carries out Grimm's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i<u.length; i++)
        {
            if(u[i].getClass() != null)
            {
            if(!u[i].isInTransport() && u[i].getUnitType() > 1)u[i].damage(10,false);
            }
        }
        Army[] armies = army.getBattle().getArmies();
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport() && u[i].getUnitType() > 1)u[s].damage(10, false);
                    } else
                        return;
                }
            }
        }
    }
    
//carries out Grimm's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        activeSuper = true;
        goldRequirement = 4000;
        damage = 20;
    }
    
//used to deactivate Grimm's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Grimm's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {
    owned.getCOstore()[0] = 0;
    }
    public void afterEnemyAction(Unit u, int index, Unit repaired, boolean main) {
    }
    
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        if(index == 15)
        {
            u.damage(10,false);
        }
    }
}