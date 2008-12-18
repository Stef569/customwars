package com.customwars.officer;

import com.customwars.unit.Army;
import com.customwars.unit.Unit;
/*
 *Tempest.java
 *Author: Fugue
 *Contributors: Kosheh, Urusan
 *Creation: 6/19/07
 *The Tempest class is used to create an instance of the Blue Moon CO Tempest (created by ThrawnFett).
 */

public class Tempest extends CO{
    //constructor
    public Tempest() {
        name = "Tempest";
        setId(58);
        
        String CObiox = "An old friend of Olaf's who rejoined the military in their time of need. A survivalist and has mastered fighting in even the most extreme weather conditions.";             //Holds the condensed CO bio'
        String titlex = "Fearsome Forecast";
        String hitx = "Clouds"; //Holds the hit
        String missx = "Sunshine"; //Holds the miss
        String skillStringx = "Trained to fight in extreme weather, Tempest's troops are immune to weather effects. ";
        String powerStringx = "Causes a sandstorm for two days. Indirect range is increased by one. "; //Holds the Power description
        String superPowerStringx = "Causes a snow to fall for two days. Enemy movement on rough terrain is reduced. All units replenish their fuel. Defense boosts apply. "; //Holds the Super description
        //"                                    " sizing markers
        String intelx = "Tempest is immune to all weathers.  " +
                "During his COP, he summons a two day" +
                "sand-storm, as well as increasing   " +
                "the range of his indirects. His SCOP" +
                "increases his defense, summons a two" +
                "day snowstorm, and reduces movement.";//Holds CO intel on CO select menu, 6 lines max
        
        intel = intelx;
        CObio = CObiox;
        title = titlex;
        hit = hitx;
        miss = missx;
        skillString = skillStringx;
        powerString = powerStringx;
        superPowerString = superPowerStringx;
        
        
        String[] COPowerx =
        {"Forecast? Today: Death. Tomorrow's not looking good either.",
         "Even nature is against you!" ,
         "This squall will stop you in your tracks!",
         "There's no warning for this severe weather." ,
         "We're gonna blow you away!" ,
         "You should have taken shelter.",};
        
        String[] Victoryx =
        {"The wind seperates the wheat from the chaff.",
         "Nature just helped to precipitate our victory.",
         "I'll bet you didn't predict this!"};
        
        String[] Swapx =
        {"Time to press forward.",
         "I think I see a storm brewing."};
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        String[] TagCOsx = {"Olaf","Drake","Lash","Von Bolt"}; //Names of COs with special tags
        String[] TagNamesx = {"Snowball Effect","Hailstorm","Dual Strike","Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,105,80,90}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Harmattan";
        SCOPName = "Katabatic Storm";
        COPStars = 3.0;
        maxStars = 7.0;
        this.army = army;
        style = BLUE_MOON;
        
        snowImmunity = true;
        rainImmunity = true;
        sandImmunity = true;
        setCleanEnemyStoreBegin(false);
        setCleanEnemyStoreEnd(false);
    }
    
    //used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP || SCOP) return 120;
        return 100;
    }
    
    public void setChange(Unit u){}
    
    public void unChange(Unit u){}
    
    
    //   used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP) return 140;
        if(COP) return 110;
        return 100;
    }
    
    public void COPower(){
        COP = true;
        // Sandstorm
        army.getBattle().startWeather(2, 1);
        
        // -1 Range
        Army[] a = army.getBattle().getArmies();
        for(int s = 0; s< a.length; s++) {
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    if(u[i].getMinRange() > 1 && !a[s].getCO().isSandImmune()){
                        u[i].setMaxRange(u[i]
								.getMaxRange() - 1);
                        u[i].getEnemyCOstore()[getStatIndex()][1] = 1;
                    }
                } else
                    return;
            }
        }
        
    }
    
    public void superCOPower(){
        SCOP = true;
        // Snow
        army.getBattle().startWeather(2,2);
        
        // Refuel
        Unit[] u = army.getUnits();
        for(int i = 0; i < u.length; i++){
            if(u[i].getClass() != null) u[i].setGas(u[i].getMaxGas());
            else return;
        }
        
        // Movement penalty
        Army[] armies = army.getBattle().getArmies();
        Unit[] e;
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                e = armies[i].getUnits();
                for(int s = 0; s < e.length; s++){
                    if(e[s].getClass() != null){
                        if((e[s].getMType() != e[s].MOVE_AIR) || army.getBattle().getMap().find(e[s].getLocation()).getTerrain().getName() == "Airport"){
                            e[s].getEnemyCOstore()[getStatIndex()][0] = army.getBattle().getMap().find(e[s].getLocation()).getTerrain().getDef();
                            e[s].setMove(e[s].getMove()
									- e[s].getEnemyCOstore()[getStatIndex()][0]);
                        }
                    } else
                        return;
                }
            }
        }
    }
    
    public void deactivateCOP(){
        COP = false;
        Army[] a = army.getBattle().getArmies();
        for(int s = 0; s< a.length; s++) {
            Unit[] u = army.getUnits();
            for(int i = 0; i < u.length; i++){
                if(u[i].getClass() != null){
                    if(u[i].getMinRange() > 1 && u[i].getEnemyCOstore()[getStatIndex()][1]==1){
                        u[i].setMaxRange(u[i]
								.getMaxRange() + 1);
                    }
                } else
                    return;
            }
        }
    }
    
    public void deactivateSCOP(){
        SCOP = false;
        boolean isFront = true;
        if(getArmy().getCO() != this) isFront = false;
        
        Army[] armies = army.getBattle().getArmies();
        Unit[] u;
        for(int i = 0; i < armies.length; i++){
            if(armies[i].getSide() != army.getSide() && armies[i].getUnits() != null){
                u = armies[i].getUnits();
                for(int s = 0; s < u.length; s++){
                    if(u[s].getClass() != null){
                        if(isFront){
                            u[s].setMove(u[s].getMove()
									+ u[s].getEnemyCOstore()[getStatIndex()][0]);
                            u[s].getEnemyCOstore()[getStatIndex()][0] = 0;
                        } else{
                            u[s].setMove(u[s].getMove()
									+ u[s].getAltEnemyCOstore()[getStatIndex()][0]);
                            u[s].getAltEnemyCOstore()[getStatIndex()][0] = 0;
                        }
                    } else
                        return;
                }
            }
        }
    }
}