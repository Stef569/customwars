package cwsource;
/*
 *Andy.java
 *Author: Adam Dziuk
 *Contributors:  Kosheh
 *Creation: July 4, 2006, 10:17 PM
 *The Andy class is used to create an instance of the Orange Star CO Andy (copyright Intelligent Systems).
 */

public class Andy extends CO{
    
    //constructor
    public Andy() {
        name = "Andy";
        id = 0;
        String CObiox = "A whiz with a wrench, this mechanical boy wonder earned fame as the hero who defeated Sturm in the first two great wars.";             //Holds the condensed CO bio'
        String titlex = "Mr. Fix-It ";
        String hitx = "Mechanics "; //Holds the hit
        String missx = "Getting up early"; //Holds the miss
        String skillStringx = "No real weakness. Equally proficient with all types of units. Ready to fight wherever and whenever. ";
        String powerStringx = "Restores two HP to all units. Firepower increases. "; //Holds the Power description
        String superPowerStringx = "Restores five HP to all units. Firepower rises, and unit movement increases by one space."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Andy's units have no main strengths " +
                        "or weaknesses meaning he's able to  " +
                        "adapt to any situation. Both of his " +
                        "powers focus on repairing damaged   " +
                        "units.";//Holds CO intel on CO select menu
                       
        intel = intelx;
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        String[] TagCOsx = {"Max","Eagle","Hawke","Aira", "Nell","Sami","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Power Wrench","Air Lift","Shaky Alliance","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,2,1,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,115,105,105,105,105,90}; //Percent for each special tag.

        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"I haven't even cranked the engine yet!",
         "Pass me my wrench!!",
         "I'm not giving up!",
         "It's time for a tune-up!",
         "Never give up, and never lose! I'm on my way!",
         "I'm not worried! I can fix anything!"};
        
        String[] Victoryx =
        {"We won! Wooooooohooo!",
         "I can fix anything!",
         "I did it! Did you see that!?"};
        
        String[] Swapx =
        {"Time to roll up my sleeves!",
         "If it needs fixing, I'm your man!"};

        String[] defeatx =
        {"Oh, come on!",
         "Next time I see you, you're in trouble!"};
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Hyper Repair";
        SCOPName = "Hyper Upgrade";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = ORANGE_STAR;
        
       
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance() == true){
            if(COP || SCOP)
                return 130;
            return 100;
        }
        //DS Stats
        if(COP)
            return 110;
        if(SCOP)
            return 130;
        return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance() == true){
            if(COP || SCOP)
                return 110;
            return 100;
        }
        //DS Stats
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    //carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].heal(20);
            }else{
                return;
            }
        }
    }
    
    //carries out Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                u[i].heal(50);
                u[i].move++;
                u[i].changed = true;
            }else{
                return;
            }
        }
    }    
    
    //used to deactivate Andy's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    //used to deactivate Andy's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null && u[i].changed){
                u[i].move--;
                u[i].changed = false;
            }else{
                return;
            }
        }
    }
}
