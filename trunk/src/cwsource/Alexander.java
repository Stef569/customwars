package cwsource;
/*
 *Alexander.java
 *Programmer: Albert Lai
 *Technical Section: Kireato
 *Creative Section: Treedweller 
 *
 */

public class Alexander extends CO{
    int storeamount;
    //stores Alexander's money gain.
    public Alexander() {
        name = "Alexander";
        id = 35; //Placeholder
      
        String CObiox = "A diligent politician who works only for the benefit of the citizens and the promotion of peace. Olaf’s son and future ruler of Blue Moon.";             //Holds the condensed CO bio'
        String titlex = "Manipulative Politician";
        String hitx = "Peace"; //Holds the hit
        String missx = "Cruelty"; //Holds the miss
        String skillStringx = "Alexander has no strengths due to his lack of interest in war. ";
        String powerStringx = "Alexander's troops capture at a faster rate, while enemy captures are forced to restart."; //Holds the Power description
        String superPowerStringx = "Enemies receive no income, and half of their current funs are diverted to Alexander."; //Holds the Super description
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
        String[] TagCOsx = {"Olaf", "Sasha", "Hachi", "Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Iron Fist", "Perestroika", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120, 110, 80 ,90}; //Percent for each special tag.

        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"Comrades, I beg of you to see from my eyes...",
        "There is more to war than simply bullets and explosions.",
        "Express yourselves, my dear people, through actions rather than words!",
        "Now we shall see where their sympathies really belong.",
        "Perhaps this will get you to negotiate instead...", 
        "You can only win a war if your people support you, and they prefer me!"};
        
        String[] Victoryx =
        {"Ironically, your shortcomings stemmed from within your own ranks.",
        "Ah, I see now! This is why the citizens were on my side all along.",
        "Only through unrivaled determination did I acheive success." };
        
        String[] Swapx =
        {"Must I be in charge?",
        "I shall attend to the battlefield..." };
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Peace Riots";
        SCOPName = "Inevitable Revolution"; 
        COPStars = 2.0; 
        maxStars = 6.0;
        this.army = army;
        style = BLUE_MOON;
        
       
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP||COP)
            return 110;
        else return 100;
    }
    
    //used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP||COP)
            return 110;
        else return 100;
    }
    
    //carries out Andy's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits(); //Storage for army units
        Army[] armies = army.getBattle().getArmies(); //Get all armies
        //--
        for(int i = 0; i < armies.length; i++){  //Going throuhg armies
            if(armies[i].getSide() != army.getSide())
            { //If the play is an opponent
                u = armies[i].getUnits(); 
                for(int s = 0; s < u.length; s++){ 
                    if(u[s].getClass() != null){ 
                        if(u[s].map.find(u[s]).getTerrain() instanceof Property){ //Find all properties that are being captured
                            ((Property) u[s].map.find(u[s]).getTerrain()).endCapture(); //End capture
                            //This was hacked together from Hawke and the CO class
                        }
                    }
                }
            }
        }
        captureMultiplier = 150; //Eh, he captures better.
    }
    

    public void superCOPower(){       
        SCOP = true;
        
        storeamount = 0; //used to store money gained from enemies
        
        Army[] armies = army.getBattle().getArmies(); //Get all armies
        for(int i = 0; i < armies.length; i++)
        {  //Going throuhg armies
            if(armies[i].getSide() != army.getSide())
            {
                armies[i].getCO().setFunding(0); 
                //armies[i].getAltCO().setFunding(0); //This shouldn't be an issue.
                //All enemies recieve no funding
                //Now, their current gold is halved...
                storeamount += (int)(armies[i].getFunds() * 0.5);
                armies[i].removeFunds((armies[i].getFunds() * 1/2)); //Removes the funds. 
            }
        }
        army.addFunds(storeamount); //Give Alexander the money from the SCOP. 
    }    
    
    //used to deactivate Andy's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        captureMultiplier = 100;
    }

    public void deactivateSCOP()
    {
        SCOP = false;
        Army[] armies = army.getBattle().getArmies(); //Get all armies
        for(int i = 0; i < armies.length; i++){  //Going throuhg armies
            if(armies[i].getSide() != army.getSide())
            { //If an opponent... then do the following
                armies[i].getCO().restoreFunding();
                if(armies[i].getAltCO()!=null)armies[i].getAltCO().restoreFunding(); //In case they switch COs
                //restores funding to the base amount
            }
        }
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};

}

