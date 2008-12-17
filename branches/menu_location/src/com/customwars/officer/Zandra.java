package com.customwars.officer;

import com.customwars.CO;
import com.customwars.Unit;
/*
 *Zandra.java
 *Author: -
 *Contributors: -
 *Creation:
 *The Zandra class is used to create an instance of the Orange Star CO Zandra.
 */

public class Zandra extends CO
{
    private boolean sandShiftBonus;
    private boolean isAttacking;
   
    //constructor
    public Zandra()
    {
        name = "Zandra";
        id = 47;
       
        String CObiox = "A youthful Co that will protect her village at all cost. She is skilled at terrain management and always tends to have the winds blow her way.";             //Holds the condensed CO bio'
        String titlex = "Sand Queen";
        String hitx = "Coconuts"; //Holds the hit
        String missx = "Seafood"; //Holds the miss
        String skillStringx = "Zandra's skill in terrain management helps her get more out of each terrain star.";
        String powerStringx = "The winds blow, stirring up a sandstorm that may be lengthy and intensify as if drags on."; //Holds the Power description
        String superPowerStringx = "The wind seems to know when she needs help and blows just the right way to make deal damage to units that her skill can't help her with. The result is a sandstorm so intense the it stripped the skin away from units not taking cover and confuses the enemy into not being able to act as much as they normally would."; //Holds the Super description
                     //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
       intel = intelx;
        String[] COPowerx =
        {"It's high time that you felt the wrath of the desert winds.",
         "I feel the power coming in, on the far winds.",
         "You'll regret challenging me.",
         "Come, father of the four winds!",
         "Your eyes will fill with sand, as you scan this wasted land...",
         "Troops, attack now, before the winds halt their blessing!" };
       
        String[] Victoryx =
        {"Everyone, hurry back to the village. Our job here is done.",
         "These sacred lands will be protected for as long as I stand.",
         "Thank you, Uncle, for giving me the strength I needed."};
         
        String[] Swapx =
        {"The sands are shifting...",
         "Let me see if I can blow their defenses wide open..."};
       
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
       
        String[] TagCOsx = {"Hachi", "Carrie", "Varlot"}; //Names of COs with special tags
        String[] TagNamesx = {"Trade Partners", "Rust and Dust", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1, 2, 0}; //Number of stars for each special tag.
        int[] TagPercentx = {110, 115, 80}; //Percent for each special tag.
       
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
        COPName = "Sand Shift";
        SCOPName = "Desert Gale";
        COPStars = 4.0;
        maxStars = 7.0;
        this.army = army;
        style = AMBER_CORONA;
       
        sandImmunity = true;
        sandShiftBonus = false;
        isAttacking = false;
    }

    //used to get the attack bonus for damage calculation
    //Zandra doesn't get any special attack bonuses from her powers :(
    //But she DOES get her attack adjusted for terrain stars during Sand Shift! :o
    public int getAtk(Unit owned, Unit enemy)
    {
        int atk = 100;
       
        if(COP || SCOP)
        {
            atk += 10;
        }
       
        //RESET TERRAIN MODIFIERS       
        army.getCO().setEnemyTerrainPenalty(0);
        army.getCO().setTerrainDefenseMultiplier(1);
       
        if(sandShiftBonus)
        {           
            int eneTDef = army.getBattle().getMap().find(enemy).getTerrain().getDef();
            int ownTDef = army.getBattle().getMap().find(owned).getTerrain().getDef();
           
            if(eneTDef > ownTDef)
            {
                owned.getArmy().getCO().setEnemyTerrainPenalty(eneTDef - ownTDef);
                owned.getArmy().getCO().setTerrainDefenseMultiplier((double)eneTDef / ownTDef);
            }
        }
 
        return atk;
    }
   
    public void setChange(Unit u){
       
    }
   
    public void unChange(Unit u){
       
    }
   
    //used to get the defense bonus for damage calculation
    //Zandra gets +2% defense per terrain star
    //Man she has a lot of defense boosting effects :[
    public int getDef(Unit enemy, Unit owned)
    {
        int def = 100;
       
        if(SCOP || COP)
        {
            def += 10;
        }
       
        //RESET TERRAIN MODIFIERS
        army.getCO().setEnemyTerrainPenalty(0);
        army.getCO().setTerrainDefenseMultiplier(1);
       
        //For handling the D2D defense bonus
        def += (2 * owned.getMap().find(owned).getTerrain().getDef());
       
        //For handling the indirect damage reduction from Desert Gale
        //Yeah I realize I'm being lazy, AND this isn't completely accurate;
        //multiplying enemy indirect damage by 80% isn't the same as a defense boost of 20% against indirects, I think >_>
        if(SCOP && (enemy.getMinRange() > 1))
        {
            def += 20;
        }
       
        //For handling the defense star bonus due to Sand Shift
        if(sandShiftBonus)
        {           
            int eneTDef = army.getBattle().getMap().find(enemy).getTerrain().getDef();
            int ownTDef = army.getBattle().getMap().find(owned).getTerrain().getDef();
           
            //NEW IF STATEMENT
            if(eneTDef > ownTDef)
            {
                owned.getArmy().getCO().setEnemyTerrainPenalty(eneTDef - ownTDef);
                owned.getArmy().getCO().setTerrainDefenseMultiplier((double)eneTDef / ownTDef);
               
                //Special fix, due to Roads not giving out any terrain stars anyway, so big terrain defense * 0 = 0 :[
                if(ownTDef == 0)
                {
                    def += (owned.getDisplayHP() * army.getBattle().getMap().find(enemy).getTerrain().getDef());
                }               
            }
            else if(eneTDef == ownTDef)
            {
                def += owned.getDisplayHP();
            }
        }
       
        //Alright so anyway, I don't intend to do touch anything in the BattleScreen or in the ContextMenu for now,
        //as that'll likely have adverse effects elsewhere, but otherwise I can't make a unit unable to attack
        //just from within a CO, and playing around with minimum ranges in 'beforeAttack' doesn't exactly prevent a unit from
        //attacking either T_T (at least, not in time).
        //So what I can do is make Zandra invincible against units that have moved, should Desert Gale be active. Likely a very
        //ugly solution, but its nearly equivalent I think.
        //
        //New Information: Holy crap. Apparently if the defense is high enough to cause damage to go into the negatives, the
        //game won't allow any attacks on the target :3 What a convenient bug!
        if(SCOP && enemy.getMoved() && !isAttacking)
        {
            //Over 9000!
            def += 9000;
        }
       
        return def;
    }
   
    //carries out Zandra's CO Power, called by CO.activateCOP()
    public void COPower()
    {
        COP = true;
        sandShiftBonus = true;
        army.getBattle().startWeather(3, 2);
    }
   
//carries out Olaf's Super CO Power, called by CO.activateSCOP()
    public void superCOPower()
    {
        SCOP = true;
        army.getBattle().startWeather(3,1);
    }
   
//used to deactivate Olaf's CO Power the next day
    public void deactivateCOP()
    {
        COP = false;       
    }
   
//used to deactivate Olaf's Super CO Power the next day
    public void deactivateSCOP()
    {
        SCOP = false;   
    }
   
    public void beforeAttack(Unit owned, Unit enemy, int damage, boolean attack)
    {
        //Zandra's Sand Shift bonus supposedly lasts as long as, uh, the sand storm brews <_<
        //The moment the sand storm dies off, her Sand Shift bonuses fade
        if(army.getBattle().getWeather() != 3)
        {
            sandShiftBonus = false;
        }
       
        isAttacking = attack;
    }
   
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack)
    {
        isAttacking = false;
    }
   
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {       
        if(sandShiftBonus)
        {   
            owned.getArmy().getCO().setEnemyTerrainPenalty(0);
            owned.getArmy().getCO().setTerrainDefenseMultiplier(1);
        }
    }
} 