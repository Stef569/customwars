package cwsource;
/*
 *Sonja.java
 *Author: Uru
 *Contributors: Bolt Storm, Kosheh
 *Creation: October 4, 2006, 12:27 PM
 *The Sonja class is used to create an instance of the Yellow Comet CO Sonja (copyright Intelligent Systems).
 */

public class Sonja extends CO{
    
    //constructor
    public Sonja(Battle bat){
        name = "Sonja";
        id = 16;
        
        String CObiox = "Kanbei's cool and collected daughter who likes to plan before acting. She excels in gathering information.";             //Holds the condensed CO bio'
        String titlex = "Sees All, Knows All";
        String hitx = "Computers"; //Holds the hit
        String missx = "Bugs"; //Holds the miss
        String skillStringx = "Keeps HP intel hidden from foes. Increased firepower when counterattacking. However, she suffers from chronic bad luck.";
        String powerStringx = "Reduces enemy terrain defensive cover by two. Allows all units to see into woods and reefs."; //Holds the Power description
        String superPowerStringx = "Units always strike first, even during counterattacks."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Sonja hides her HP intel from the   " +
                        "enemy and has higher counterattack  " +
                        "firepower, but tends to be unlucky. " +
                        "Her power pierces terrain and fog   " +
                        "while her super reverses the first  " +
                        "strike advantage an enemy may have. ";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Kanbei","Lash","Sami","Sabaki","Conrad","Sensei","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Battle Standard","Brainstorm","Girl Power","Master Plan","Master Plan","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,1,1,1,1,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {130,105,110,110,110,105,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"I see right through your plans!",
         "I'll show you there's more to combat than power alone!",
         "Number of enemy troops...location... Got it! This is so easy!",
         "Get me some fresh intel, stat!",
         "You can't hide from me!",
         "Just like I planned..."};
        
        String[] Victoryx =
        {"My strategy was merely better... That is all.",
         "I must review my strategy.",
         "Perfect planning wins the day!"};
        
        String[] Swapx =
        {"I'll show you how this is done.",
         "Cover me!"};
        
        String[] defeatx =
        {"... I'm sorry, Father... I've broken my promise...",
         "I have learned much from this..."} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Enhanced Vision";
        SCOPName = "Counter Break";
        COPStars = 3.0;
        maxStars = 5.0;
        
        //hidden hp
        hiddenHP = true;
        
        //-5% luck
        positiveLuck = 10;
        negativeLuck = 5;
        
        //enemy terrain star reduction
        enemyTerrainPenalty = 1;
        counterAttack = 100;
        
        this.army = army;
        style = YELLOW_COMET;
        
        //readjust for balance mode
        if (bat.getBattleOptions().isBalance()== true){
            //-10% luck
            negativeLuck = 10;
            //enemy terrain star reduction
            enemyTerrainPenalty = 0;
            counterAttack = 150;
        }
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(army.getBattle().getBattleOptions().isBalance()==true){
            if(SCOP || COP)
                return 110;
            return 100;
        }
        if(SCOP || COP)
            return 110;
        return 100;
        
    }
    
    public void setChange(Unit u){
        if (army.getBattle().getBattleOptions().isBalance()==false)
            u.vision++;
        else;
    }
    
    public void unChange(Unit u){
        if (army.getBattle().getBattleOptions().isBalance()==false)
            u.vision--;
        else;
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)
            return 110;
        return 100;
    }
    
//carries out Sonja's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        piercingVision = true;
        if (army.getBattle().getBattleOptions().isBalance()==true){
            army.getBattle().setFog(true);
            enemyTerrainPenalty = 2;
        }else{
            enemyTerrainPenalty = 2;
            //increase vision
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    u[i].vision += 1;
                    //Needs Piercing Vision
                    u[i].changed = true;
                }
            }
        }
        army.getBattle().calculateFoW();
    }
    
//carries out Sonja's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        
        if (army.getBattle().getBattleOptions().isBalance() == true){
            firstStrike = true;
            enemyTerrainPenalty = 3;
            counterAttack = 100;
        }else{
            piercingVision = true;
            enemyTerrainPenalty = 3;
            firstStrike=true;
            //increase vision
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    u[i].vision += 1;
                    u[i].changed = true;
                }
            }
        }
        army.getBattle().calculateFoW();
    }
    
//used to deactivate Sonja's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        piercingVision = false;
        if (army.getBattle().getBattleOptions().isBalance()==true){
            if(!army.getBattle().getBattleOptions().isFog())
                army.getBattle().setFog(false);
            enemyTerrainPenalty = 0;
            
        }else{
            enemyTerrainPenalty = 1;
            //return vision to normal
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    if(u[i].changed){
                        u[i].vision -= 1;
                        u[i].changed = false;}
                }
            }
            return;
        }
        army.getBattle().calculateFoW();
    }
    
//used to deactivate Sonja's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        if(army.getBattle().getBattleOptions().isBalance() == true){
            firstStrike = false;
            counterAttack = 150;
            enemyTerrainPenalty = 0;
            negativeLuck = 10;
        }else{
            piercingVision = false;
            enemyTerrainPenalty = 1;
            firstStrike=false;
            //return vision to normal
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    if(u[i].changed){
                        u[i].vision -= 1;
                        u[i].changed = false;
                    }
                }
            }
        }
        army.getBattle().calculateFoW();
    }
    
}
