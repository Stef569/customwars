package com.customwars.officer;
/*
 *Nana
 *Author: Albert Lai
 *Contributors:
 *Creation: February 1, 2007
 * kosheh: shit if i see kawaii anywhere in that script i'm flying to florida
 * kosheh: and punching lego in the face for allowing such a fallacy to take place in campaign
 * kosheh: ...
 * kosheh: i love lego
 * kosheh: this is why i would do such a thing.
 */

import java.util.ArrayList;

import com.customwars.BaseDMG;
import com.customwars.Location;
import com.customwars.unit.Unit;

public class Nana extends CO{
    
    //constructor
    public Nana() {
        name = "Nana";
        setId(43);

String CObiox = "The lovable, cute, and amazingly        " +
                "talented granddaughter of Hachi. Known  " +
                "to be incredibly scary when angry.      ";/* +
                "                                        " +
                "kosheh: shit if i see kawaii anywhere in" +
                "that script i'm flying to florida and   " +
                "punching lego in the face               " +
                "kosheh: ...                             " +
                "kosheh: i love lego. this is why i would" +
                "do such a thing.";*/
        String titlex = "Neurotically Charged";
        String hitx = "Artemis"; //Holds the hit
        String missx = "Ozzy"; //Holds the miss
        String skillStringx = "Indirect attacks deal one HP of damage to all nearby units, no matter their allegiance.";
        String powerStringx = "Direct attacks deal two HP of damage to the nearest enemy unit. Nana's units are no longer affected by collateral damage. "; //Holds the Power description
        String superPowerStringx = "Direct attacks deal two HP of damage to all units within a large blast radius. Nana's units are no longer affected by collateral damage. "; //Holds the Super description
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
        String[] COPowerx =
        {"Let's go, boys!",
        "You made me mad!",
        "Stop annoying me!",
        "I'm gonna kick your butt!",
        "I want to end this fast!",
        "Don't be such a meanie!" };
        
        String[] Victoryx =
        {"Aww, you put up a good fight though!",
        "Hehe! You're funny!",
        "You look so cute when you lose!" };
        
        
        String[] Swapx =
        {"Yay! It's my turn!",
        "This will be so easy!" };
        
        
        setCOPower(COPowerx);
        Victory = Victoryx;
        setSwap(Swapx);
        
        //No special tags
        String[] TagCOsx = {"Jake", "Hachi", "Koal","Adder","Rachel"}; //Names of COs with special tags
        String[] TagNamesx = {"Pretty Power", "Loving Granddaddy", "Dual Strike", "Dual Strike", "Dual Strike"}; //Names of the corresponding Tags
        int[] TagStarsx = {1,1,0,0,0}; //Number of stars for each special tag.
        int[] TagPercentx = {110,110,90,90,80}; //Percent for each special tag.
        
        setTagCOs(TagCOsx);
        setTagNames(TagNamesx);
        setTagStars(TagStarsx);
        setTagPercent(TagPercentx);
        
        COPName = "Chain Reaction";
        SCOPName = "Explosive Tantrum";
        COPStars = 3.0;//3/6
        maxStars = 6.0;
        this.army = army;
        style = ORANGE_STAR;
    }
    
//used to get the attack bonus for damage calculation
    public int getAtk(Unit attacker, Unit defender){
        if(COP || SCOP)return 110;
        return 100;
        
    }
    
    public void setChange(Unit u){
        
    }
    
    public void unChange(Unit u){
        
    }
    
    
//used to get the defense bonus for damage calculation
    public int getDef(Unit attacker, Unit defender){
        if(SCOP || COP)return 110;
        return 100;
    }
    
//carries out Blandie's CO Power, called by CO.activateCOP()
    public void COPower(){
        COP = true;
    }
    
//carries out Blandie's Super CO Power, called by CO.activateSCOP()
    public void superCOPower(){
        SCOP = true;
    }
    
//used to deactivate Blandie's CO Power the next day
    public void deactivateCOP(){
        COP = false;
    }
    
//used to deactivate Blandie's Super CO Power the next day
    public void deactivateSCOP(){
        SCOP = false;
    }
    
    public void afterAttackAction(Unit owned, Unit enemy, boolean attack) {
        if(BaseDMG.findBase(owned.getUType(), enemy.getUType(),army.getBattle().getBattleOptions().isBalance()) != -1)
        {
        if(attack && owned.getAmmo()>0 && (owned.getMType() != owned.MOVE_INFANTRY || owned.getMType() != owned.MOVE_MECH))
        {
            int offset = 0;
            int radius = 0;
            if(owned.getMinRange() > 1)
                radius = 1;
            if(SCOP)
                radius = 2;
            //This block handles Nana's D2D splash, and her SCOP direct splash.
            for(int i=-1*radius; i <= radius; i++){
                for(int j=-1*offset; j <= offset; j++){

                    if((Math.abs(j)+Math.abs(i) != 0) && army.getBattle().getMap().onMap(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)) && (army.getBattle().getMap().find(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)).hasUnit()))
                    {
                    if(owned.getMinRange()>1)
                        {   //If this is a ranged unit and the statement below eliminates friendly fire on SCOP and COP.
                        if((COP || SCOP))
                        {
                            if(army.getBattle().getMap().find(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)).getUnit().getArmy() != army)
                            {
                             if(SCOP)
                                 specialDamage(army.getBattle().getMap().find(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)).getUnit(), 2);
                             else if(COP)
                                 specialDamage(army.getBattle().getMap().find(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)).getUnit(), 2);
                            }
                        }
                        else //If neither SCOP or COP are active, just damage without checking
                        army.getBattle().getMap().find(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)).getUnit().damage(10,false);
                        }
                    if(owned.getMinRange()==1 && SCOP && (Math.abs(j)+Math.abs(i) != 0))
                        {   //If this is a direct unit, 'explode'
                        if(army.getBattle().getMap().find(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)).getUnit().getArmy() != army)
                            specialDamage(army.getBattle().getMap().find(new Location(enemy.getLocation().getCol()+j,enemy.getLocation().getRow()+i)).getUnit(), 2);
                        }
                    }
                }
                if(i<0)offset++;
                else offset--;
            }

            //This block handles her COP.
            if(COP && owned.getAmmo()>0 && (owned.getMType() != owned.MOVE_INFANTRY || owned.getMType() != owned.MOVE_MECH))
            {
                Unit[] store = enemy.getArmy().getUnits();
                int t = 0; int low = 50;
                //If the closest unit is farther than 50 spaces away... well. wtf.
                for(int i = 0; i < store.length; i++)
                {
                    t = Math.abs(enemy.getLocation().getRow() - store[i].getLocation().getRow()) + Math.abs(enemy.getLocation().getCol() - store[i].getLocation().getCol());
                    if(t<low && t != 0) //If the unit is closer than the closest unit 
                    {
                        low = t;
                    }
                }
                ArrayList closest = new ArrayList();
                //This loop scans the units once more, this time calculating the closest number of units.
                for(int i = 0; i < store.length; i++)
                {
                    t = Math.abs(enemy.getLocation().getRow() - store[i].getLocation().getRow()) + Math.abs(enemy.getLocation().getCol() - store[i].getLocation().getCol());
                    if(t == low) //If the unit is among the closest
                    {
                        closest.add(store[i]);
                    }
                }
                if(closest.get(0)!= null)
                {
                    Unit max = (Unit)closest.get(0);
                    for(int i = 0; i <(closest.size()); i++)
                    {
                        if(((Unit)closest.get(i)).getValue() > max.getValue())
                            max = (Unit)closest.get(i);
                    }
                    max.damage(20, false);
                }
            }
        }
        }
    }
    
    public void specialDamage(Unit u, int times)
    {
        for(int i = 0; u.getEnemyCOstore()[getStatIndex()][0]<5 && i<times; i++)
        {
            u.damage(10, false);
            u.getEnemyCOstore()[getStatIndex()][0]++;
        }
    }
}