package cwsource;

/*
 *Flak.java
 *Author: Adam Dziuk, Kosheh
 *Contributors: 
 *Creation:
 *The Flak class is used to create an instance of the Black Hole CO Flak (copyright Intelligent Systems).
 */

public class Flak extends CO{
   
    //constructor
    public Flak(Battle bat) {
        name = "Flak";
        id = 19;

        String CObiox = "The strongman of the Black Hole army. Promoted form private by Hawke, who was impressed by his natural ability.";             //Holds the condensed CO bio'
        String titlex = "Berzerker";
        String hitx = "Meat"; //Holds the hit
        String missx = "Vegetables"; //Holds the miss
        String skillStringx = "High firepower, but he relies solely on strength. His shoddy technique sometimes reduces the damage his units deal.";
        String powerStringx = "Firepower rises, but so does his chances of reduced firepower."; //Holds the Power description
        String superPowerStringx = "Firepower rises dramatically, but so does his chances of reduced power."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Flak's attack power is high but can " +
                        "randomly drop due to his technique. " +
                        "Powers boost his firepower to very  " +
                        "high levels, but the chances of it  " +
                        "randomly dropping also increase.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Adder","Lash"}; //Names of COs with special tags
        String[] TagNamesx = {"Totally Flaked","Bruise Cruise"}; //Names of the corresponding Tags
        int[] TagStarsx = {0,1}; //Number of stars for each special tag.
        int[] TagPercentx = {105,110}; //Percent for each special tag. 
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
       
        String[] COPowerx =
        {"Stay outta my way!",
        "Grrrrrraaaaaaa! I'm outta control!",
        "You can't hide from me! I'll hunt you down like a dog!",
        "Nnnggrr! Braaaawrr! Smaarrrrgh!",
        "Grr! Now you've really made me angry!",
        "You're gonna get pummeled!"};
       
        String[] Victoryx =
        {"What was that? A waste of my time!",
         "Graaa! Blaarrgggg! Smaaarrgggghhh!!",
         "Enough pussyfooting around!"};
        
        String[] Swapx =
        {"I'll crush you all!",
        "Let me at 'em!"}  ;
        
        String[] defeatx =
        {"I-I don't believe it... They were... stronger than me?",
         "Grrr... I wanna smash your face in!!!"} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
       
        COPName = "Brute Force";
        SCOPName = "Barbaric Blow";
        COPStars = 3.0;
        maxStars = 6.0;
        if (bat.getBattleOptions().isBalance() == true){
            positiveLuck = 25;
            negativeLuck = 10;
        }
        else{
            positiveLuck = 25;
            negativeLuck = 10;}
        this.army = army;
        style = BLACK_HOLE;
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
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        if (army.getBattle().getBattleOptions().isBalance() == true){
            positiveLuck = 55;
            negativeLuck = 20;
        }
        else{
            positiveLuck = 50;
            negativeLuck = 20;}
    }
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        if (army.getBattle().getBattleOptions().isBalance() == true){
            positiveLuck = 100;
            negativeLuck = 40;
        }
        else{
            positiveLuck = 90;
            negativeLuck = 40;}
    }    
    
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
           COP = false;
        if (army.getBattle().getBattleOptions().isBalance() == true){
            positiveLuck = 25;
            negativeLuck = 10;
        }
        else{
            positiveLuck = 25;
            negativeLuck = 10;}
    } 
    
    //used to deactivate Adder's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
        if (army.getBattle().getBattleOptions().isBalance() == true){
            positiveLuck = 25;
            negativeLuck = 10;
        }
        else{
            positiveLuck = 25;
            negativeLuck = 10;}
    }
}