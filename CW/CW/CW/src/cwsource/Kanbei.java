package cwsource;
/*
 *Kanbei.java
 *Author: Adam Dziuk, Xaif
 *Contributors:
 *Creation:
 *The Kanbei class is used to create an instance of the Yellow Comet CO Kanbei (copyright Intelligent Systems).
 */

public class Kanbei extends CO{
   
//constructor
    public Kanbei() {
        name = "Kanbei";
        id = 15;
       
        String CObiox = "The emperor of Yellow Comet. A skilled commander who has a soft spot for his daughter.";             //Holds the condensed CO bio'
        String titlex = "Vigilant Samurai";
        String hitx = "Sonja"; //Holds the hit
        String missx = "Computers"; //Holds the miss
        String skillStringx = "All units have high offensive and defensive capabilities, but are expensive to deploy.";
        String powerStringx = "Increases firepower of all units."; //Holds the Power description
        String superPowerStringx = "Greatly strengthens offensive and defensive abilities of all units. Firepower doubles when inflicting damage in counterattacks."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Kanbei's Imperial forces have the   " +
                        "best offensive and defensive ratings" +
                        "but they come at a high price.  His " +
                        "power raises offensive ratings even " +
                        "higher, while his super also boosts " +
                        "defense and counter damage dealt.   ";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
       
        
        String[] TagCOsx = {"Sonja","Javier","Sensei","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Battle Standard","Code of Honour","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {130,110,105,90}; //Percent for each special tag.
       
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
       
        String[] COPowerx =
        {"Who shall taste my wrath?",
         "Observe the heart of a true Samurai.",
         "Your numbers mean nothing!  Nothing can stop me!",
         "My mind is honed for battle.  That is the essence of a samurai!",
         "True Samurai don't know the meaning of retreat.",
         "I hope you thoroughly enjoy the taste of my sword!"};
       
        String[] Victoryx =
        {"Kanbei is victorious! We shall meet again!",
         "Raise your voices in victory!",
         "No enemy can stand before Kanbei!"};
       
        String[] Swapx =
        {"Prepare yourself! Kanbei is here!",
         "Let us fight with honor!"};
       
        String[] defeatx =
        {"Preposterous! So many of Kanbei's forces defeated in such a short time? Withdraw!",
         "Regardless of the odds, Kanbei should never lose this many units! Withdraw!"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        costMultiplier = 120;
       
        COPName = "Morale Boost";
        SCOPName = "Samurai Spirit";
        COPStars = 4.0;
        maxStars = 7.0;
        this.army = army;
        style = YELLOW_COMET;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
                //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if (COP||SCOP)
                return 160;
            else
                return 120;
        }
        //DS Stats
        if(COP || SCOP)
            return 160;
        return 120;
    }
   
    public void setChange(Unit u){
        u.price = costMultiplier*u.price/100;
    }
   
    public void unChange(Unit u){
        u.price = 100*u.price/costMultiplier;
    }
   
   
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if (SCOP)
                return 155;
            if (COP)
                return 125;
            else
                return 115;
        }
        //DS Stats
        if(SCOP)
            return 160;
        if(COP)
            return 130;
        return 120;
    }
   
//carries out Kanbei's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
   
//carries out Kanbei's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        counterAttack = 200;
    }
   
//used to deactivate Kanbei's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
   
//used to deactivate Kanbei's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        counterAttack = 100;
    }
}