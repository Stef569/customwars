package cwsource;
/*
 *Grimm.java
 *Author: Adam Dziuk, Kosheh, Paul Whan
 *Contributors:
 *Creation:
 *The Grimm class is used to create an instance of the Yellow Comet CO Grimm (copyright Intelligent Systems).
 */

public class Jared extends CO{
    boolean sustain;
//constructor
    public Jared() {
        name = "Grimm";
        id = 18;
        
        String CObiox = "A Yellow Comet commander with a dynamic personality. Could care less about the details. Nicknamed \"Lightning Grimm.\"";             //Holds the condensed CO bio'
        String titlex = "Kamikaze!";
        String hitx = "Donuts"; //Holds the hit
        String missx = "Planning"; //Holds the miss
        String skillStringx = "Firepower of all units is increased, thanks to his daredevil nature, but thier defenses are reduced.";
        String powerStringx = "Increases the attack of all units."; //Holds the Power description
        String superPowerStringx = "Greatly increases the attack of all units."; //Holds the Super description
                      //"                                    " sizing markers
        String intelx = "" +
                        "" +
                        "" +
                        "" +
                        "" +
                        "";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
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
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Knuckleduster";
        SCOPName = "Haymaker";
        COPStars = 3.0;
        maxStars = 6.0;
        this.army = army;
        style = YELLOW_COMET;
        sustain = false;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        //Balance Stats
        if(army.getBattle().getMap().find(attacker).getTerrain().baseMoveCost(attacker.moveType) > 1)
        {
        if (SCOP||COP)
            return 100;
        return 90;
        }
        if (SCOP||COP)
            return 110;
        return 100;
    }
    
    public void setChange(Unit u){};
    
    public void unChange(Unit u){};
    
    public void dayStart(boolean main){
        if(main) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Wood"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,-1,-1,0,0,0,0,0,-1});
                    else if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Reef"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,0,0,0,-1,-1,0,0,0});
                }
            }
        }
    }
    public void dayEnd(boolean main){
        if(main&& army.getBattle().getWeather() == 0) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Wood"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,1,1,0,0,0,0,0,1});
                    else if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Reef"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,0,0,0,1,1,0,0,0});
                    
                }
            }
        }
        if(sustain)
            sustain = false;
    }
    
    public void enemyDayStart(boolean main) {
        double[] temp = (new Wood()).move;
        if(sustain) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    //move[] 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
                    for(int s = 0; s<10; s++) {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1 && temp[s] != -1)
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, temp[s]);
                    }
                    /*
                if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Wood"))
                    army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new int[] {2,2,4,6,1,-1,-1,1,-1,8});
                if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Plain"))
                    army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new int[] {2,2,3,5,1,-1,-1,1,-1,5});
                if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Road") || army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Bridge"))
                    army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new int[] {2,2,3,4,1,-1,-1,1,-1,5}); //infantry changed to zero from two
                if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Shoal"))
                    army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new int[] {2,2,3,4,1,-1,1,1,-1,5});
                if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().isUrban())
                    army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new int[] {2,2,3,4,1,-1,-1,1,-1,6});
                if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Port"))
                    army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new int[] {2,2,3,4,1,1,1,1,-1,5});
                if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("Mountain") || army.getBattle().getMap().find(new Location(i,t)).getTerrain().name.equals("River"))
                    army.getBattle().getMap().find(new Location(i,t)).getTerrain().newMoveSet(new int[] {3,2,-1,-1,1,-1,-1,1,-1,-1});
                     */
                }
            }
        }
    }
    
    public void enemyDayEnd(boolean main ){
        double[] temp = (new Wood()).move;
        if(sustain) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    //Used to be just restore cost
                    for(int s = 0; s<10; s++) {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1 && temp[s] != -1)
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, -temp[s]);
                    }
                }
            }
            
        }
    }
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if (army.getBattle().getBattleOptions().isBalance()== true){
            if (SCOP||COP)
                return 100;
            else    
                return 90;}
        //DS Mode Stats
        else{
        if(COP || SCOP)
            return 90;
        else
            return 80;}
    }
    
//carries out Grimm's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
        sustain = true;
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
}