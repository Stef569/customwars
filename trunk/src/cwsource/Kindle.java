package cwsource;
/*
 *Kindle.java
 *Author: Paul Whan
 *Contributors: Adam Dzuik
 *Creation:
 *The Max class is used to create an instance of the Orange Star CO Max (copyright Intelligent Systems).
 */

public class Kindle extends CO{
    
    //Constructor
    public Kindle() {
        name = "Kindle";
        id = 26;
        
        String CObiox = "Jugger and Koal's commanding officer. Has a blunt, queen-like personality.";             //Holds the condensed CO bio'
        String titlex = "Urban Terror";
        String hitx = "Anything chic"; //Holds the hit
        String missx = "Anything passe"; //Holds the miss
        String skillStringx = "An upper-crust CO who excels at urban warfare. Firepower of all units increased on properties.";
        String powerStringx = "Inflicts three HP of damage to enemy units on properties. Also increases firepower of all units on a property."; //Holds the Power description
        String superPowerStringx = "The more properties she controls, the more firepower she gains. Also greatly increases firepower of all units on a property."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Kindle has increased firepower while" +
                        "attacking from properties. Her power" +
                        "deals large damage to enemy units   " +
                        "on properties and her super grants a" +
                        "firepower boost dependant on her    " +
                        "own property count.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Koal","Jugger","Jake","Hawke"}; //Names of COs with special tags
        String[] TagNamesx = {"Flash Point","Fireworks","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {105,105,90,80}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"So, you want to see me get serious? Very well! You asked for it!",
         "Even the prettiest rose has thorns!",
         "I am Kindle, Gorgeous AND Clever!",
         "Let us play a more dangerous game, shall we? I grow weary of this.",
         "Aha ha ha! Beg for mercy, rat!",
         "Still putting up a fight? Unbelievable!"};
        
        String[] Victoryx =
        {"Winning is almost as wonderful as I am. Aha ha ha!",
         "I won again? This is getting so boring.",
         "Aha ha ha! Pathetic."};
        
        
        String[] Swapx =
        {"Aha ha ha! It's showtime!",
         "I suppose we should switch."};
        
        String[] defeatx =
        {"Losing to you corn-fed country folk is enough to give me frown lines.",
         "That was a bit of a disappointment."} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Urban Blight";
        SCOPName = "High Society";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = BLACK_HOLE;
    }
    
    int numProperties; //Used to gather how many properties Kindle has for SCOP.
    int numProp2;
    public int getAtk(Unit attacker, Unit defender){
        int numProperties = army.getProperties().length;
        int numProp2 = numProperties*3;
        //Balance Stats
        if(army.getBattle().getBattleOptions().isBalance()== true){
            if(army.getBattle().getMap().find(attacker).getTerrain().isUrban() == true && SCOP)
                return (numProp2 += 190);
            if(SCOP)
                return (numProp2 += 110);
            if(army.getBattle().getMap().find(attacker).getTerrain().isUrban() == true && COP)
                return 160;
            if(COP)
                return 110;
            if(army.getBattle().getMap().find(attacker).getTerrain().isUrban() == true)
                return 120;
            else
                return 100;
        }
        //DS Stats
        if(army.getBattle().getMap().find(attacker).getTerrain().isUrban() == true && SCOP)
            return (numProp2 += 230);
        if(SCOP)
            return (numProp2 += 110);
        if(army.getBattle().getMap().find(attacker).getTerrain().isUrban() == true && COP)
            return 190;
        if(COP)
            return 110;
        if(army.getBattle().getMap().find(attacker).getTerrain().isUrban() == true)
            return 140;
        return 100;
        
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
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(army.getBattle().getMap().find(u[s]).getTerrain().isUrban() == true)
                            if(u[s].getArmy().getSide()!=army.getSide())
                                if(army.getBattle().getBattleOptions().isBalance())
                                {if(!u[s].isInTransport())u[s].damage(20, false);}
                                else
                                {
                                if(!u[s].isInTransport())u[s].damage(30, false);
                                }
                    }else{
                        return;
                    }
                }
            }
        }
    }
    //carries out Max's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
    };
    
    public void deactivateCOP(){
        COP = false;
    }
    
    //used to deactivate Max's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
}
