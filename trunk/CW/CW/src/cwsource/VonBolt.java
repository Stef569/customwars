package cwsource;
/*
 *Vonbolt.java
 *Author: Kosheh, Adam Dziuk
 *Contributors:
 *Creation:
 *The Von Bolt class is used to create an instance of the Black Hole CO Von Bolt (copyright Intelligent Systems).
 */

public class VonBolt extends CO {
    //constructor
    public VonBolt() {
        name = "Von Bolt";
        id = 27;
        
        String CObiox = "Former commander-in-chief of the Black Hole forces. A mysterious old man who has been alive a very, very long time. Masterminded the Omega war.";             //Holds the condensed CO bio'
        String titlex = "Immortal Evil";
        String hitx = "Long life"; //Holds the hit
        String missx = "Young 'uns!"; //Holds the miss
        String skillStringx = "All units have superior firepower and defense.";
        String powerStringx = ""; //Holds the Power description
        String superPowerStringx = "Fires shock waves that disables electical systems and all forces in range become paralyzed. Affected units suffer three HP of damage. Firepower and defense rises."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Von Bolt's non-infantry units enjoy " +
                "increased attack and defense. He has" +
                "no power to use, but his super truly" +
                "devastates a select area of troops  " +
                "with damage and paralysis.";//Holds CO intel on CO select menu, 6 lines max
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Andy","Max","Sami","Jake","Rachel","Nell","Hachi","Artemis","Sophie","Olaf","Grit","Sasha","Colin","Alexander","Tempest","Aira","Edge","Kanbei","Sonja","Grimm","Sensei","Rattigan","Eagle","Drake","Jess","Javier","Peter","Ozzy","Zeke","Sabaki","Amy","Jared","Walter","Koshi","Carrie"}; //Names of COs with special tags
        String[] TagNamesx = {"Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"No matter how powerful...hhh...you are, you're...hhh...still a worm on a hook...",
         "Well...hhh...let me have a taste...hhh...of your soul.",
         "Hheh heh heh... Stop panicking... it's...hhh...almost over...",
         "Hheh heh heh. The darkness rises...",
         "The sweet scent of destruction...hhh...it makes me...hhh...feel young!",
         "Hold still...hhh...I'm going to...hhhhh...suck the marrow from your bones.",};
        
        String[] Victoryx =
        {"Predator...prey...hhh... I hunt them all...",
         "Hehh hhh hhh... Pathetic.",
         "Even death...hhh...fears me..."};
        
        String[] Swapx =
        {"You're useless...",
         "Heh heh hehh... I'll chew 'em up...hhh...and spit 'em out!"};
        
        String[] defeatx =
        {"Bah! Enough...hhh... Leave me...hhh... I grow...hhh...tired. I must...hhh...rest.",
         "No...hhhh... No...! Hhhhhh... Must...hhhh...live...hhhh..."} ;
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Deus";
        SCOPName = "Ex Machina";
        COPStars = -1;
        maxStars = 10.0;
        this.army = army;
        style = BLACK_HOLE;
    }
    
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        //DS Stats
        if(army.getBattle().getBattleOptions().isBalance()) {
            if(defender instanceof Infantry | defender instanceof Mech)
                if(COP || SCOP)
                    return 110;
                else
                    return 100;
        }
        if(SCOP || COP)
            return 120;
        return 110;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        //DS Stats
        if(army.getBattle().getBattleOptions().isBalance()) {
            if(defender instanceof Infantry | defender instanceof Mech)
                if(COP || SCOP)
                    return 110;
                else
                    return 100;
        }
        if(COP || SCOP)
            return 120;
        return 110;
    }
    
    //carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
    
    //carries out Andy's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        if (army.getBattle().getBattleOptions().isBalance() == true){
            army.getBattle().getMap().doAutoExplosion(army, 2, 3, true, army.getSide());
        }else{
            //should be 50% chance of value and 50% of HP targeting FIX
            if(army.getBattle().getRNG().nextInt(2) == 1)
                army.getBattle().getMap().doAutoExplosion(army, 2, 3, true, army.getSide());
            else
                army.getBattle().getMap().doHPExplosion(army, 2, 3, true, army.getSide());
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
    }
    
}