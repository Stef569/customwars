package cwsource;
/*
 *Grimm.java
 *Author: Adam Dziuk, Kosheh, Paul Whan
 *Contributors:
 *Creation:
 *Falcone PUNCH.
 *Attacking oozium permanently raises bad luck.
 */

import java.util.Random;

public class Falcone extends CO{
    int storepos = 0;
    int storeneg = 0;
    boolean sustainSCOP = false;
//constructor
    public Falcone() {
        name = "Falcone";
        id = 45; //placeholder

        String CObiox = "A lone rebel on Wars World whom suffers from bipolar disorder and is subject to \"visions\". Rose in the shadow of his older brother, Hawke.";          //Holds the condensed CO bio'
        String titlex = "Dark Seer";
        String hitx = "Visions"; //Holds the hit
        String missx = "Failure"; //Holds the miss
        String skillStringx = "Those around Falcone become cursed with bad luck. Enemy units may suddenly deal less damage than expected.";
        String powerStringx = "Units deal more damage than expected. Enemy units may suddenly deal even less damage than expected."; //Holds the Power description
        String superPowerStringx = "All enemy units suffer two HP of damage. Enemy repair costs are tripled."; //Holds the Super description
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
 
        String[] TagCOsx = {"Hawke", "Melanthe", "Graves", "Ember", "Drake", "Eagle"}; //Names of COs with special tags
        String[] TagNamesx = {"Birds of Prey","Omnivore","Debilitate","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,2,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120, 120, 110,105,90,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        
        String[] COPowerx =
        {"Your fate has been decided." ,
        "Peace is a lie, there is only passion.", 
        "...the visions... are coming." ,
        "I'm having a vision... it foresees death." ,
        "You should watch your back..." ,
        "To live... is to die.",
        };
        //To inhale...is to exhale...
        
        String[] Victoryx =
        {"Victory was assured." ,
        "How unlucky for you..." ,
        "Had an accident did we?" };
        
        String[] Swapx =
        {"It's our turn..." ,
        "The visions are never gone..." };
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Visionary Calamity";
        SCOPName = "Disturbing Tumult";
        COPStars = 3.0;//3/7
        maxStars = 7.0;
        this.army = army;
        style = BLACK_HOLE;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        else 
            return 100;        
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    public void dayStart(boolean main )
    {
        if(sustainSCOP)
        {
        sustainSCOP = false;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u = army.getUnits();
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        u[s].repairMod = 1;
                    } else
                        return;
                }
            }
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
    }
    
//carries out Grimm's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        sustainSCOP = true;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u = army.getUnits();
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(!u[s].isInTransport())u[s].damage(20, false);
                        u[s].repairMod = 2f;
                    } else
                        return;
                }
            }
        }
        
    }
    
//used to deactivate Grimm's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Grimm's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack) {
        if(enemy.name != "Oozium")
        {
            if(SCOP)
                enemy.getArmy().getCO().negativeLuck += 15;
            else if(COP)
                enemy.getArmy().getCO().negativeLuck += 35;
            else enemy.getArmy().getCO().negativeLuck += 5;
        }
        
        if(attack && COP)
        {
        int bonus = (int)((positiveLuck-negativeLuck)/10.0 * owned.getDisplayHP());
        storepos = positiveLuck;
        positiveLuck = 0;
        storeneg = negativeLuck;
        negativeLuck = 0; //Turns off luck, as he is supposed to have 'max luck'
        
        if(bonus > enemy.getHP())
            bonus = enemy.getHP();
       
        enemy.damage(bonus, false);
        army.charge(bonus*enemy.getStarValue()/200.0);
        enemy.getArmy().charge(bonus*enemy.getStarValue()/100.0);
        army.addFunds(army.getCO().enemySalvage * bonus * enemy.getPrice() /10000);
        enemy.getArmy().addFunds(enemy.getArmy().getCO().friendlySalvage * bonus * enemy.getPrice() /10000);
        }
    }
    
    public void beforeCounter(Unit owned, Unit enemy, int damage, boolean attack) {
        
            if(attack && COP && damage!=owned.getHP())
            {
            int bonus = (int)((positiveLuck-negativeLuck)/10.0 * owned.getDisplayHP());
            storepos = positiveLuck;
            positiveLuck = 0;
            storeneg = negativeLuck;
            negativeLuck = 0; //Turns off luck, as he is supposed to have 'max luck'
            if(bonus > enemy.getHP())
                bonus = enemy.getHP();

            enemy.damage(bonus, false);
            army.charge(bonus*enemy.getStarValue()/200.0);
            enemy.getArmy().charge(bonus*enemy.getStarValue()/100.0);
            army.addFunds(army.getCO().enemySalvage * bonus * enemy.getPrice() /10000);
            enemy.getArmy().addFunds(enemy.getArmy().getCO().friendlySalvage * bonus * enemy.getPrice() /10000);
            }
        }
    
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        if(enemy.name != "Oozium")
        {
            if(SCOP)        
                enemy.getArmy().getCO().negativeLuck -= 15;
            else if(COP)
                enemy.getArmy().getCO().negativeLuck -= 35;
            else enemy.getArmy().getCO().negativeLuck -= 5;
        }
    }
    public void afterAction(Unit u, int index, Unit repaired, boolean main)
    {
        if(main && index ==1)
        {
            if(COP)
            {
                positiveLuck = storepos;
                negativeLuck = storeneg;
            }
        }
    }
    
}
