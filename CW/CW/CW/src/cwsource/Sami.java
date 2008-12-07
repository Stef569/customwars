package cwsource;
/*
 *Sami.java
 *Author: Urusan
 *Contributors: Kosheh
 *Creation: August 12, 2006, 8:52 AM
 *The Sami class is used to create an instance of the Orange Star CO Sami (copyright Intelligent Systems).
 */

public class Sami extends CO{
    
    //constructor
    public Sami() {
        name = "Sami";
        id = 2;

        String CObiox = "A strong-willed Orange Star special forces captain who loves long hair. Despite having short hair. Whatever, IS.";             //Holds the condensed CO bio'
        String titlex = "Darling of the Soldiers";
        String hitx = "Chocolate"; //Holds the hit
        String missx = "Cowards"; //Holds the miss
        String skillStringx = "As an infantry specialist, her foot soldiers do more damage and capture faster. Non-infantry direct-combat units have weaker firepower ";
        String powerStringx = "Infantry units receive a movement bonus of one space. Their attack also increases. "; //Holds the Power description
        String superPowerStringx = "All foot soldiers's capturing rate is doubled. Their movement is increased by two spaces and their attack increases greatly. "; //Holds the Super description
                      //"                                    " sizing markers        
        String intelx = "Sami's infantry is able to capture  " +
                        "and attack more effectively at the  " +
                        "expense of her direct units. Sami's " +
                        "powers increase the movement and    " +
                        "effectiveness of her infantry troops"; //el on CO select menu, 6 lines max
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] COPowerx =
        {"You're not bad!  Now it's my turn!",
         "All right!  Time to end this!",
         "Infantry... Assault!",
         "Ready or not, here I come!",
         "All right, it's make-or-break time!",
         "Move out, grunts!"};
        
        String[] Victoryx =
        {"Mission accomplished! Awaiting orders!",
         "Commandos always complete their mission.",
         "Score one for the grunts!"};
        
        String[] Swapx =
        {"Roger! Advance to the front lines!",
         "Ready for duty!"};

        String[] defeatx =
        {"Things would be easier if we had more infantry units...",
         "Next time's for real. I won't lose focus."} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        String[] TagCOsx = {"Eagle","Sonja","Nell","Andy","Max","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Earth and Sky","Girl Power","Dual Strike","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {3,1,0,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {120,110,105,105,105,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        COPName = "Double Time";
        SCOPName = "Victory March";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = ORANGE_STAR;
        
        captureMultiplier = 150;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(attacker instanceof Infantry || attacker instanceof Mech){
            if(SCOP)
                return 180;
            if(COP)
                return 160;
            return 120;
        }else if(attacker.getMinRange() > 1){
            if(SCOP || COP)
                return 110;
            return 100;
        }
        if(SCOP || COP)
            return 100;
        return 90;
        
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)
            return 110;
        return 100;
    }
    
//carries out Sami's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i] instanceof Infantry || u[i] instanceof Mech){
                    u[i].move += 1;
                    u[i].changed = true;
                }
            } else
                return;
        }
    }
    
//carries out Sami's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        if(army.getBattle().getBattleOptions().isBalance())
            captureMultiplier = 300;
        else
            captureMultiplier = 2000;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i] instanceof Infantry || u[i] instanceof Mech){
                    u[i].move += 2;
                    u[i].changed = true;
                }
            } else
                return;
        }
    }
    
//used to deactivate Sami's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].changed){
                    if(u[i] instanceof Infantry || u[i] instanceof Mech){
                        u[i].move -= 1;
                        u[i].changed = false;
                    }
                }
            } else
                return;
        }
    }
    
//used to deactivate Sami's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        captureMultiplier = 150;
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null){
                if(u[i].changed){
                    if(u[i] instanceof Infantry || u[i] instanceof Mech){
                        u[i].move -= 2;
                        u[i].changed = false;
                    }
                }
            } else
                return;
        }
    }
}
