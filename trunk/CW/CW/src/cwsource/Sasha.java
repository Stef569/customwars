package cwsource;
/*
 *Sasha.java
 *Author: Adam Dziuk, Kosheh
 *Contributors:
 *Creation:
 *The Sasha class is used to create an instance of the Blue Moon CO Sasha (copyright Intelligent Systems).
 */

public class Sasha extends CO{
    
    
    //constructor
    public Sasha() {
        name = "Sasha";
        id = 10;
        
        String CObiox = "Colin's older sister. Normally ladylike, but becomes daring when she gets angry.";             //Holds the condensed CO bio'
        String titlex = "Dominatrix";
        String hitx = "Truffles"; //Holds the hit
        String missx = "Pork rinds"; //Holds the miss
        String skillStringx = "Being the heir to a vast fortune, she gets an additional 100 funds from allied properties.";
        String powerStringx = "The more funds she has, the more she can decrease the enemy's CO power gauge."; //Holds the Power description
        String superPowerStringx = "Earns funds when she inflicts damage on a foe. The greater the damage, the more she earns."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Sasha maximises income she receives " +
                        "from her properties and has no real " +
                        "weaknesses.  Her power focuses on   " +
                        "stopping opponent's getting theirs  " +
                        "and her super on raising cash from  " +
                        "damaging enemy units.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Colin", "Jake", "Rachel", "Grimm", "VonBolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Trust Fund", "Dual Strike", "Dual Strike", "Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {130,105,105,105,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Wealth is power.",
         "I won't go easy on you.",
         "I'll keep fighting until peace is restored.",
         "I will not back down.",
         "I'll show you my true strength.",
         "You've made me very, very...upset."};
        
        String[] Victoryx =
        {"Even kittens have claws.",
         "Money is power.",
         "Bravo!"};
        
        String[] Swapx =
        {"You won't like...what I'm about to do.",
         "I won't back down!"};
        
        String[] defeatx =
        {"This is like some horrible dream.",
         "This is such a nightmare."} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Market Crash";
        SCOPName = "War Bonds";
        COPStars = 2.0;
        maxStars = 6.0;
        this.army = army;
        style = BLUE_MOON;
        fundingMultiplier = 107.5;
        fundingBase = 107.5;
    }
    
    public int getAtk(Unit attacker, Unit defender){
        if(COP||SCOP)
            return 110;
        return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
       
        Army[] armies = army.getBattle().getArmies();
        double i = army.getFunds()/50000.0;
        for(int s = 0; s < armies.length; s++){
            if(armies[s].getSide() != getArmy().getSide()){
                armies[s].getCO().stars -= armies[s].getCO().getStars() * i;
                if(armies[s].getCO().stars < 0)
                    armies[s].getCO().stars = 0;
                if(armies[s].getAltCO() != null){
                    if (army.getBattle().getBattleOptions().isBalance() == true)
                        armies[s].getAltCO().stars -= armies[s].getAltCO().getStars() * i/2;
                    else
                        armies[s].getAltCO().stars -= armies[s].getAltCO().getStars() * i;
                    if(armies[s].getAltCO().stars < 0)
                        armies[s].getAltCO().stars = 0;
                }
            }
        } 
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        enemySalvage = 100;
    }
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
    public void propChange(Property p) {
    }
    
    public void propUnChange(Property p) {
    }
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        enemySalvage = 0;
        
    }
}