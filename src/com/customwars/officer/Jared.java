package com.customwars.officer;
/*
 *Grimm.java
 *Author: Adam Dziuk, Kosheh, Paul Whan
 *Contributors:
 *Creation:
 *The Grimm class is used to create an instance of the Yellow Comet CO Grimm (copyright Intelligent Systems).
 */
import java.util.ArrayList;

import com.customwars.Army;
import com.customwars.BaseDMG;
import com.customwars.CO;
import com.customwars.Location;
import com.customwars.MoveID;
import com.customwars.Unit;
public class Jared extends CO{
    boolean sustain;
//constructor
    public Jared() {
        name = "Jared";
        id = 70;
        
        String CObiox = "A young hacker from Blue Moon who is loyal to people rather than ideas or nations.";             //Holds the condensed CO bio'
        String titlex = "Pathfinder";
        String hitx = "Blue Screens"; //Holds the hit
        String missx = "Black Bombs"; //Holds the miss
        String skillStringx = "Movement cost through woods and reefs are reduced, but firepower on that terrain is reduced as well.";
        String powerStringx = "Enemy movement costs are increased by one."; //Holds the Power description
        String superPowerStringx = "Enemies within attack range of one of Jared's units suffer an extra attack using a fraction of their power."; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Jared is able t move through rough  " +
                        "terrain faster at the cost of fire  " +
                        "power. Jared's power increases      " +
                        "movement, while his super allows    " +
                        "all of his units to launch weakened " +
                        "attacks against all enemies in range"; 
        
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
        {"I never fail. It's time you learned that!",
         "Your army is at the mercy of my keyboard.",
         "I'm on fire... but your plans are burning up.",
         "Psh. Some security. That only took 5 minutes.",
         "Each time you patch a hole, you open another...",
         "The mouse is mightier than the sword." };
        
        String[] Victoryx =
        {"I never quit, and I never lose.",
         "What's wrong? Couldn't take the heat?",
         "You can't stop me. It's that simple." };
        
        String[] Swapx =
        {"Things are starting to heat up now.",
         "Wait a second; the system's still booting." };
        
        Swap = Swapx;
        COPower = COPowerx;
        Victory = Victoryx;
        
        COPName = "Firewall";
        SCOPName = "Firestorm";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = JADE_COSMOS;
        sustain = false;
    }
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(army.getBattle().getMap().find(attacker).getTerrain().baseMoveCost(attacker.getMoveType()) > 1) {
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
                    if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().getName().equals("Wood"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,-1,-1,0,0,0,0,0,-1});
                    else if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().getName().equals("Reef"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,0,0,0,-1,-1,0,0,0});
                }
            }
        }
        if(sustain)
            sustain = false;
    }
    public void dayEnd(boolean main){
        if(main&& army.getBattle().getWeather() == 0) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().getName().equals("Wood"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,1,1,0,0,0,0,0,1});
                    else if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().getName().equals("Reef"))
                        army.getBattle().getMap().find(new Location(i,t)).getTerrain().addMoveSet(new double[] {0,0,0,0,0,1,1,0,0,0});
                    
                }
            }
        }
    }
    
    /*
    public void enemyDayStart(boolean main) {
        
        if(sustain) {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) {
                    //move[] 0=infantry, 1=mech, 2=tread, 3=tires, 4=air, 5=sea, 6=transport, 7=oozium, 8=pipe, 9=hover
                    for(int s = 0; s<10; s++) {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1)
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, 1 );
                    }
                }
            }
        }
    }
    
    public void enemyDayEnd(boolean main )
    {
        double[] temp = (new Wood()).move;
        
        if(sustain) 
        {
            for(int i = 0; i< army.getBattle().getMap().getMaxCol(); i++) 
            {
                for(int t = 0; t< army.getBattle().getMap().getMaxRow(); t++) 
                {
                    //Used to be just restore cost
                    for(int s = 0; s<10; s++) 
                    {
                        if(army.getBattle().getMap().find(new Location(i,t)).getTerrain().moveCost(s) != -1 && temp[s] != -1)
                            army.getBattle().getMap().find(new Location(i,t)).getTerrain().addCost(s, -1);
                    }
                }
            }
            
        }
    }
    */
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        //Balance Stats
        if (SCOP||COP)
            return 110;
        else
            return 100;
    }
    
//carries out Grimm's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
                Army[] a = army.getBattle().getArmies();
        for(int r = 0; r < a.length; r++)
        {
            if(a[r].getSide() != army.getSide()) 
            {
            	//NEW
                double[] temp = new double[MoveID.MAX_MOVE_TYPES];
                
                for(int q = 0; q < temp.length; q++)
                {
                	temp[q] = 1;
                }
                
                a[r].addTerrCosts_global(temp);
            }
        }
    }
    
//carries out Grimm's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
        Unit[] u = army.getUnits();
        Army[] a = army.getBattle().getArmies();
        ArrayList<Unit> storage = new ArrayList<Unit>();
        

        
        int targets = 0;
        for(int i = 0; i<u.length; i++) 
        {
            targets = 0;
            storage.clear();
            
            for(int s = 0; s<a.length;s++) 
            {
                if(a[s].getSide() != army.getSide()) 
                {
                	
                
                	/*//NEW
                    double[] temp = new double[MoveID.MAX_MOVE_TYPES];
                    
                    for(int q = 0; q < temp.length; q++)
                    {
                    	temp[q] = 1;
                    }
                    
                    a[s].addTerrCosts_global(temp);
                    */
                    
                    Unit[] enemy = a[s].getUnits();
                    
                    for(int t = 0; t<enemy.length; t++) 
                    {
                        if(u[i].checkDisplayFireRange(enemy[t].getLoc()) && BaseDMG.find(u[i], enemy[t], army.getBattle().getBattleOptions().isBalance()) != -1) {
                            targets++;
                            storage.add(enemy[t]);
                        }
                    }
                }
            }
            
            for(int q = 0; q<storage.size(); q++) {
                int dmg = u[i].displayDamageCalc(storage.get(q));
                storage.get(q).damage((int)(dmg/(targets*1.0)), false);
            }
            
        }
    }
    
//used to deactivate Grimm's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    	//NEW
        double[] temp = new double[MoveID.MAX_MOVE_TYPES];
        
        for(int q = 0; q < temp.length; q++)
        {
        	temp[q] = -1;
        }

        Army[] a = army.getBattle().getArmies();

        for(int s = 0; s<a.length;s++) 
        {
            if(a[s].getSide() != army.getSide()) 
            {                
                a[s].addTerrCosts_global(temp);                
            }
        }
    }
    
//used to deactivate Grimm's Super CO Power the next day
    public void deactivateSCOP()
    {
        SCOP = false;
        
    }
}