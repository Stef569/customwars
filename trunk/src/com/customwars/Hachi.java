package com.customwars;
    /*
     *Hachi.java
     *Author: Adam Dziuk, Kosheh
     *Contributors:
     *Creation:
     *The Hachi class is used to create an instance of the Orange Star CO Hachi (copyright Intelligent Systems).
     */

public class Hachi extends CO{
    
    //constructor
    public Hachi(Battle bat) {
        name = "Hachi";
        id = 4;
        
        String CObiox = "Owner of the Battle Maps shop. Rumored to be Orange Star's former commander-in-chief.";             //Holds the condensed CO bio'
        String titlex = "Industrialist Extraordinaire";
        String hitx = "Tea"; //Holds the hit
        String missx = "Medicine"; //Holds the miss
        String skillStringx = "Uses secret trade routes to get slightly lower deployment costs for all units.";
        String powerStringx = "Merchant pals gather from around the globe and help him deploy ground units from any allied city."; //Holds the Power description
        String superPowerStringx = "Speaks with such authority that he obtains even lower deployment costs."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "Hachi deploys units at a reduced    " +
                        "cost with no loss in firepower.  His" +
                        "power allows him to deploy land     " +
                        "forces from his cities and his super" +
                        "drops deployment costs to very low  " +
                        "levels.";//Holds CO intel on CO select menu, 6 lines max

        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        intel = intelx;
        
        String[] TagCOsx = {"Sensei", "VonBolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Grizzled Vets", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {2,0}; //Number of stars for each special tag.
        int[] TagPercentx = {100,90}; //Percent for each special tag.
        
        TagCOs = TagCOsx;
        TagNames = TagNamesx;
        TagStars = TagStarsx;
        TagPercent = TagPercentx;
        
        String[] COPowerx =
        {"This brings back memories!",
         "Runnin' away won't prove anything! Stand your ground, soldier!",
         "No need to get all worked up!",
         "Hey, I'm no retiree!",
         "I may be old, but I can still rumble!",
         "This is my best seller!"};
        
        String[] Victoryx =
        {"Battles cost too much!",
         "Rematches are free!",
         "Thank you, come again!"};
        
        String[] Swapx =
        {"I'm open for business!",
         "Switch all you want--it's free!"};
        
        String[] defeatx =
        {"I'm getting too old for this.",
         "Shop's closing for today..."} ;
        
        Swap = Swapx;       
        COPower = COPowerx;
        Victory = Victoryx;
        defeat = defeatx;
        if (bat.getBattleOptions().isBalance()== true){
            costMultiplier = 95;
            COPName = "Barter";
            SCOPName = "Merchant Union";
        }else{
            costMultiplier = 90;
            COPName = "Barter";
            SCOPName = "Merchant Union";
        }

        COPStars = 3.0;
        maxStars = 5.0;
        this.army = army;
        style = ORANGE_STAR;
    }
    
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
    
    public void setChange(Unit u){
        u.price = costMultiplier*u.price/100;
    }
    
    public void unChange(Unit u){
        u.price = 100*u.price/costMultiplier;
    }
    
    //carries out Adder's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        if (army.getBattle().getBattleOptions().isBalance()== true){
            costMultiplier -=30;
        }    
        else{
            costMultiplier = 50;}
    }
    
    
    //carries out Adder's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        costMultiplier-=30;
        SCOP = true;
        army.changeAllProp();
    }
    
    public void deactivateSCOP(){
        SCOP = false;
        if (army.getBattle().getBattleOptions().isBalance()== true){
            costMultiplier+=30;
            army.unChangeAllProp();}
        else{
            army.unChangeAllProp();
            costMultiplier = 90;}
    }
    //used to deactivate Adder's CO Power the next day
    public void deactivateCOP(){
        COP = false;
        if (army.getBattle().getBattleOptions().isBalance()== true){
            costMultiplier += 30;
        }       
        else{
            costMultiplier = 90;}
    }
    
    public void propChange(Property p) {
        if(SCOP||(COP && (army.getBattle().getBattleOptions().isBalance()== true)))
            if(p.getName().equals("City"))
                p.createLand = true;
    }
    
    public void propUnChange(Property p) {
        if(p.getName().equals("City"))
            p.createLand = false;
    }
    
    public void afterAction(Unit u, int index, Unit repaired, boolean main) {
        if(main && index == 15 && SCOP && army.getBattle().getMap().find(u).getTerrain().isUrban() && army.getBattle().getMap().find(u).getTerrain().name.equals("City"))   
        {
            //if this CO is in front, he builds a unit, the SCOP is active and the location is a city
            if(army.getFunds() < 95*u.price/costMultiplier-u.price)
            {
                army.addFunds(u.price);
                u.damage(100,true);
            }
            else
            {
                army.removeFunds(95*u.price/costMultiplier-u.price);
            }
        }
    }
}