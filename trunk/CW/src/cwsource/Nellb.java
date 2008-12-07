package cwsource;


/*
 *Nellb.java
 *Author: Kosheh, Paul Whan, Adam Dzuik
 *Contributors:
 *Creation:
 *The Balance Nell class is used to create an instance of the Orange Star CO Nell (copyright Intelligent Systems) with balanced stats (By Paul Whan).
 */

public class Nellb extends CO{
    
//constructor
    public Nellb() {
        name = "Nell";
        id = 3;
        
        String[] TagCOsx = {"Rachel","Andy","Max","Sami","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Windfall","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,105,105,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"This will bring me luck!",
         "Don't hate me just because I'm lucky!",
         "It's payback time!",
         "Everything will work out!",
         "I'm feelin' lucky!",
         "Pull together, everyone!"};
        
        String[] Victoryx =
        {"Lady luck was with me!",
         "Ha! Too bad for you!",
         "...And that's how it's done."};
        
        String[] Swapx =
        {"I hope I get lucky...",
         "Let's get down to business!"};
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Lucky Star";
        SCOPName = "Lady Luck";
        COPStars = 3.0;
        maxStars = 6.0;
        positiveLuck = 15;
        negativeLuck = 0;
        this.army = army;
        style = ORANGE_STAR;
    }
    
//used to get the attack bonus for damage calculation
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
    
//carries out Nell's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        positiveLuck = 35;
    }
    
//carries out Nell's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        positiveLuck = 70;
        
    }
    
//used to deactivate Nell's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        positiveLuck = 15;
    }
    
//used to deactivate Nell's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        positiveLuck = 15;
    }
}