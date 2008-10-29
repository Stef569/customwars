package cwsource;
/*
 *Peter.java
 *Author: Paul Whan
 *Contributors:
 *Creation: ChessRules
 *The Max class is used to create an instance of the Orange Star CO Max (copyright Intelligent Systems).
 */

public class Peter extends CO{
    
    //constructor
    public Peter() {
        name = "Peter";
        id = 31;
        
        String CObiox = "A veteran CO with over 30 years of experience, mostly from the plains of Cosmo Land. Cautious but decisive.";             //Holds the condensed CO bio'
        String titlex = "Careful Calculation";
        String hitx = "Electric razors"; //Holds the hit
        String missx = "Excuses"; //Holds the miss
        String skillStringx = "Peter's units plow through enemy direct-combat units with ease. However, thicker terrain weakens his attacks.";
        String powerStringx = "Firepower is greatly increases against enemy direct-combat units."; //Holds the Power description
        String superPowerStringx = "Enemy units not on a property suffer 2 HP of damage."; //Holds the Super description
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
        String[] TagCOsx = {"Sensei","Eagle","Jess","Adder"};              //Names of COs with special tags
        String[] TagNamesx  = {"Senior Strike", "Sky Tactics", "Dual Strike", "Dual Strike", "Dual Strike"};          //Names of the corresponding Tags
        int[] TagStarsx = {1 ,2, 0, 0 ,0};           //Number of stars for each special tag.
        int[] TagPercentx = {110, 110, 105, 105, 90};       //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        
        String[] COPowerx =
        {  "We're going in, boys. Wait for the signal, then strike!",
           "You won't even have time to take cover.",
           "It's time to tear through their tanks.",
           "Bogged down? Me? Ha! You seriously overestimate yourself.",
           "I've been down there with the grunts... I know what's feasible and what's not.",
           "I may not be unbeatable, but you're obviously not either."};
        
        String[] Victoryx =
        {"Experience is the key to my success.",
         "I hope you learned something from this.",
         "What did you think you were doing with those tanks?" };
        
        
        String[] Swapx =
        {"No, that won't work. Here, let me do this.",
         "Got it. We're going right down the middle." } ;
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "General Offensive";
        SCOPName = "Bombing Run";
        COPStars = 2.0;
        maxStars = 5.0;
        this.army = army;
        style = GREEN_EARTH;
    }
    int peterstars; //Used for calculation of peter's damage
    //used to get the attack bonus for damage calculation - Peter Test
    public int getAtk(Unit attacker, Unit defender){
        if(defender == null)return 100;
        int peterstars = (army.getBattle().getMap().find(defender).getTerrain().getDef())*(-5);
        
        if(defender.getMinRange() == 1 && SCOP)
            return (peterstars += 125);
        if(defender.getMinRange() == 1 && COP)
            return (peterstars += 145);
        if(COP||SCOP)
            return (peterstars += 110);
        if(defender.getMinRange() == 1)
            return (peterstars += 115);
        return (peterstars += 100);
        
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP || SCOP)
            return 110;
        return 100;
    }
    
    //changes unit for this CO
    public void setChange(Unit u){
    }
    
    //unchanges unit
    public void unChange(Unit u){
    }
    
    //carries out Max's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
    
    //carries out Max's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(army.getBattle().getMap().find(u[s]).getTerrain().isUrban() == false && u[s].moveType != u[s].MOVE_AIR)
                            if(!u[s].isInTransport())u[s].damage(20, false);
                    }else{
                        return;
                    }
                }
            }
        }
    }
    
    //used to deactivate Max's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
    //used to deactivate Max's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
}

