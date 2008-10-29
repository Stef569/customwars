package cwsource;
/*
 *Grimm.java
 *Author: Adam Dziuk, Kosheh, Paul Whan
 *Contributors:
 *Creation:
 *The Grimm class is used to create an instance of the Yellow Comet CO Grimm (copyright Intelligent Systems).
 */
import java.util.ArrayList;

public class Kinnegard extends CO{
    ArrayList suspension;
    ArrayList<Integer> storage; //stores turns in suspension
    int last; //holds how many suspended units there are
//constructor
    public Kinnegard() {
        name = "Kinnegard";
        id = 18;
        
        String CObiox = "A Yellow Comet commander with a dynamic personality. Could care less about the details. Nicknamed \"Lightning Grimm.\"";             //Holds the condensed CO bio'
        String titlex = "Lightning Grimm";
        String hitx = "Donuts"; //Holds the hit
        String missx = "Planning"; //Holds the miss
        String skillStringx = "Firepower of all units is increased, thanks to his daredevil nature, but thier defenses are reduced.";
        String powerStringx = "Increases the attack of all units."; //Holds the Power description
        String superPowerStringx = "Greatly increases the attack of all units."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Grimm's whole army boasts unrivalled" +
                "offensive ratings but his defense   " +
                "falters.  His powers simply boost   " +
                "his already high attack even higher.";//Holds CO intel on CO select menu, 6 lines max
        
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Sensei","Sasha","Javier","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Rolling Thunder","Dual Strike","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,105,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        
        String[] COPowerx =
        {"Things are lookin' Grimm for you! Harrrrr!",
         "You're about to enter a world of pain!!",
         "Outta the way! I got crushin' to do!",
         "Oooh, yeah!!",
         "Gwar har har!! Go cry like a little girl!!",
         "What a pencil neck!!",};
        
        String[] Victoryx =
        {"Wanna throw down again? Oooh yeah!",
         "Gwar har har! Hit the road, slick!",
         "Fear the lightning!"};
        
        String[] Swapx =
        {"Oooh yeah!! Now, I mean business!!",
         "I'll deal with these losers!!"};
        
        String[] defeatx =
        {"I'm tellin' you, this is awful!",
         "I'll get you next time! Oooh yeah!"} ;
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        
        COPName = "Knuckleduster";
        SCOPName = "Haymaker";
        COPStars = 2.0;
        maxStars = 5.0;
        this.army = army;
        style = YELLOW_COMET;
        special1 = "Suspend";
        special2 = "Recall";
        
        cleanStore = false;
        suspension = new ArrayList();
        storage = new ArrayList<Integer>();
        last = 0;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(SCOP || COP)
            return 110;
        else
            return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(COP | SCOP)
            return 110;
        else
            return 100;
    }
    public void dayStart(boolean main) {
        for(int i = 0; i<storage.size()/2; i++)
        {
            if(storage.get(i*2).intValue() < 2)
            {
                storage.set(i*2, Integer.valueOf(storage.get(i).intValue() + 1));
            }
            else
            {
                
            }
        }
        
    }
//carries out Grimm's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
    
//carries out Grimm's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
    }
    
//used to deactivate Grimm's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Grimm's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    public boolean canUseSpecial1(Unit owned) {
        if(owned.COstore[0]%2 == 0)
            return true;
        return false;
    }
    //Call to see if a unit is in range of a special ability (used for drawing)
    public boolean canTargetSpecial1(Unit owned, Location target) {
        if(!owned.getLocation().equals(target) && owned.getMoveRange().pathableAtXY(target.getCol(), target.getRow(),army.getBattle().getMap(),owned)) {
            return true;
        }
        return false;
    }
    public void useSpecial1(Unit owned, Location target) {
        owned.COstore[0] = last * 2;
        suspension.add(owned);
        storage.add(Integer.valueOf(0));
        
        owned.setLocation(target);
        owned.COstore[0] = last * 2 + 1;
        suspension.add(owned);
        storage.add(Integer.valueOf(0));
        
        owned.damage(150, true);
    }
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        
    }
}