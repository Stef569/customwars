package cwsource;
/*
/*
 *Sabaki.java
 *Author: Albert Lai
 *Contributors: 
 *Creation: ThrawnFett
 *The Sabaki class summons Commander Sabaki for 5GB, requires player to discard a card.
 */

public class Sabaki extends CO{
//------------------
int SabakiStore; //Stores damage for use in afterCounter
boolean destroyedDefending;
//--------------------
//constructor
    public Sabaki() {
        name = "Sabaki";
        id = 32;

        String CObiox = "A former Yellow Comet CO that started a rebellion when she saw the state of Amber Corona. An expert at salvaging parts and field repairs. ";             //Holds the condensed CO bio'
        String titlex = "Waste Not, Want Not";
        String hitx = "Spare parts"; //Holds the hit
        String missx = "Poverty"; //Holds the miss
        String skillStringx = "Highly skilled at salvaging parts, Sabaki's units can drain HP from enemies.";
        String powerStringx = "Units can drain even more HP. In addition, their defense as they inflict more damage. "; //Holds the Power description
        String superPowerStringx = "Units heal HP equal to the amount of damage they inflict."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Sabaki is able to restore HP to her " +
                        "units when dealing damage to the " +
                        "enemy.  Her powers both follow the  " +
                        "same theme but with more potency.   ";//Holds CO intel on CO select menu, 6 lines max
                        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] TagCOsx = {"Sonja","Amber","Kanbei"}; //Names of COs with special tags
        String[] TagNamesx = {"Perception Path","Eureka","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,90}; //Percent for each special tag.

        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Grab those parts! There's a tank that needs fixing!",
        "Get those machines up and running! We need to push forward!",
        "Every machine has a weakness, you just have to know where to look.",
        "You may think I'm nuts, but you're about to get screwed...",
        "Time to throw a wrench in their plans.",
        "Let's see how you react when the hammer falls..."};
        
        String[] Victoryx =
        {"My army acts like a well-oiled machine.",
        "Nothing's more satisfying than killing an enemy with their own gun.",
        "Your army just fell apart out there."};
        
        String[] Swapx =
        {"Looks like we need a quick fix.",
         "Is our plan breaking down?"};
        
        COPower = COPowerx;
        Victory = Victoryx;
        Swap = Swapx;
        
        COPName = "Speed Salvage";
        SCOPName = "Rejuvination Raid";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = AMBER_CORONA;
        
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
       if (SCOP)
            return 140;
       else if (COP)
            return 110;
       else return 100;

    }
    
    public int getInventionAtk(Unit attacker, Unit inv){
        if (SCOP)
            return 140;
        else if (COP)
            return 110;
        else return 100;
    }
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if (SCOP)
           return 110; //Defense is 110 if a SCOP is activated
        if (COP)
           return 110 + defender.COstore[0]; 
        //Defense Increase. I'm hoping that 'defender' is always under Sabaki's control. 
        //Otherwise, well.
        return 100;
    }
    
//carries out Sabaki's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
    
    
//carries out Sabaki's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
    }
    
    public void setChange(Unit u) { ;}
    
    public void unChange(Unit u) { ;}
    
//used to deactivate Sabaki's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        //unit cycling taken care of  day code.in
    }
    
//used to deactivate Sabaki's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public void afterAttack(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack) 
    {
        if (attack) //If, first strike
        {
            SabakiStore = damage; //SabakiStore stores the damage Sabaki deals on first strike
            if (COP)
            {
                owned.COstore[0] += (5*damage/10); //Every hitpoint of damage dealt by Sabaki increases her defense by 5. This will probably be dumb, though.
            }
        //If Sabaki initiated the attack, then SabakiStore is the amount that would be healed w/regards to the COP/SCOP
            if (!SCOP&&!COP) //If neither SCOP or COP are activated, use her D2D healing
            {
                SabakiStore = SabakiStore/5;
                if(owned.getDisplayHP() < 6)
                    SabakiStore *= 2;
                if(destroy) {
                    SabakiStore+=5;} //If Sabaki KOs a unit
                owned.heal(SabakiStore); //And all is well with the world
            }
            if(destroy)
                destroyedDefending = true;
        }
        
    }
    public void afterCounter(Unit owned, Unit enemy, int damage, boolean destroy, boolean attack)
    {
        
        if (attack) //If Sabaki is counterattacking, then use the damage that it deals now
        {
            if (SCOP) {
                owned.heal(damage);
            } else if (COP) {
                owned.COstore[0] += (5*damage/10); //Increases Sabaki's defense, including counterattacks
                //Actually, was this CO designed to have defense increase for counterattacks?
                SabakiStore = damage/5;
                if(owned.getDisplayHP() < 6)
                    SabakiStore *= 2;
                if(destroy) {
                    SabakiStore+=5;} //If Sabaki KOs a unit
                owned.heal(damage);
            }
            
        }
    }

    public void afterAttackAction(Unit owned, Unit enemy, boolean attack)
    {
        if (attack) //If Sabaki had first strike
        {
            //If Sabaki's unit isn't destroyed by the counterattack.
            //The unit code checks for this, so I'm fairly certain an if statement is unnessecary
            if (SCOP) {
                owned.heal(SabakiStore); //Use the damage value from before
            } else if (COP) {
                SabakiStore = SabakiStore/10;
                if(destroyedDefending) {
                    SabakiStore+=0.2;} //If Sabaki KOs a unit
                owned.heal(SabakiStore);
            }
        }
        destroyedDefending = false;
    }
}