package cwsource;
/*
 *Army.java
 *Author: Adam Dziuk
 *Contributors: Urusan
 *Creation: July 1, 2006, 1:27 PM
 *The Army class holds an army, including its unit list and strategic intel
 */

//import java.util.Random;
import java.io.*;
import java.awt.Color;

public class Army implements Serializable{
    //private final int unitLimit = 50;           //The Unit Limit
    private UnitList units;                     //The Unit List
    private PropertyList properties;            //The Property List
    private int id;                             //The Army's ID number, it's position in the army list (-1 = neutral)
    private CO co;                              //The army's CO
    private Battle battle;                      //The Battle the Army is in
    private CO altCO;                           //The army's alternate CO
    private int side;                           //The army's side (0=A 1=B 2=C 3=D etc.)
    private int color;                          //the army's color (0=red, 1=blue, etc.)
    private int funds;                          //The money at this army's disposal
    private int atkPercent = 0;                 //The Attack Percent for every attack.
    //private int exLuck;                       //Extra Luck from various sources
    private boolean tag;                        //Is this Army TagBreaking?
    private boolean canTagSwap;                 //In a TagBreak, has the army swapped yet?
    private int numComTowers;                   //The number of Com Towers owned by the army
    //private Color dayStartColor[] = new Color[2];//The army's day start color
    public AI ai = null;                        //The army's AI
    
    //constructor
    public Army(int id, CO co, CO altCO, int side, int color, Battle b){
        units = new UnitList();
        properties = new PropertyList();
        this.id = id;
        this.co = co;
        this.co.statIndex = id;
        this.altCO = altCO;
        this.co.setArmy(this);
        if(altCO != null){this.altCO.setArmy(this); this.altCO.statIndex = id;}
        this.side = side;
        this.color = color;
        funds = 0;
        this.battle = b;
        //int i = isSpecTag();
        //if(i > -1 && !Options.balanceMode)
        //    exLuck = co.TagStars[i]*5;
        numComTowers = 0;
        
        //set day start colors
        /*
        if(color==0){
            //OS
            dayStartColor[0] = new Color(200, 0, 0);
            dayStartColor[1] = new Color(255, 0, 0);
        }else if(color==1){
            //BM
            dayStartColor[0] = new Color(0, 0, 200);
            dayStartColor[1] = new Color(0, 0, 255);
        }else if(color==2){
            //GE
            dayStartColor[0] = new Color(0, 200, 0);
            dayStartColor[1] = new Color(0, 255, 0);
        }else if(color==3){
            //YC
            dayStartColor[0] = new Color(200, 200, 0);
            dayStartColor[1] = new Color(255, 255, 0);
        }else if(color==4){
            //BH
            dayStartColor[0] = new Color(100, 100, 100);
            dayStartColor[1] = new Color(150, 150, 150);
        }else if(color==5){
            //RC
            dayStartColor[0] = new Color(0, 100, 0);
            dayStartColor[1] = new Color(0, 200, 0);
        }else if(color==6){
            //AC
            dayStartColor[0] = new Color(150, 150, 0);
            dayStartColor[1] = new Color(200, 200, 0);
        }else if(color==7){
            //PG
            dayStartColor[0] = new Color(0, 0, 0);
            dayStartColor[1] = new Color(150, 150, 150);
        }else if(color==8){
            //CS
            dayStartColor[0] = new Color(150, 100, 0);
            dayStartColor[1] = new Color(250, 200, 0);
        }else if(color==9){
            //CD
            dayStartColor[0] = new Color(100, 100, 200);
            dayStartColor[1] = new Color(100, 100, 255);
        }else{
            //defaults
            dayStartColor[0] = new Color(0, 0, 0);
            dayStartColor[1] = new Color(255, 255, 255);
        }
         */
    }
    
    //adds a unit to this Army
    public void addUnit(Unit u){
        units.insertLast(u);
    }
    
    //removes a unit from this Army
    public void removeUnit(Unit u){
        units.deleteItem(u);
    }
    
    //adds a property to this Army
    public void addProperty(Property p){
        properties.insertLast(p);
        co.propChange(p);
        if(p instanceof ComTower)numComTowers++;
    }
    
    //removes a property from this Army
    public void removeProperty(Property p){
        co.propUnChange(p);
        properties.deleteItem(p);
        if(p instanceof ComTower)numComTowers--;
    }
    
    public void changeAllProp(){
        if(!properties.isEmpty()){
            PropertyLink current = properties.first;
            while(current != null){
                co.propChange(current.getData());
                current = current.next;
            }
        }
    }
    
    public void unChangeAllProp(){
        if(!properties.isEmpty()){
            PropertyLink current = properties.first;
            while(current != null){
                co.propUnChange(current.getData());
                current = current.next;
            }
        }
    }
    
    //mark all the Army's units as either Active (true) or Inactive (false)
    public void setAllActive(boolean b){
        if(!units.isEmpty()){
            UnitLink current = units.first;
            while(current != null){
                current.getData().setActive(b);
                current = current.next;
            }
        }
    }
    
    public void unChangeAll(){
        if(!units.isEmpty()){
            UnitLink current = units.first;
            while(current != null){
                co.unChange(current.getData());
                current = current.next;
            }
        }
    }
    
    public void setChangeAll(){
        if(!units.isEmpty()){
            UnitLink current = units.first;
            while(current != null){
                co.setChange(current.getData());
                current = current.next;
            }
        }
    }
    
    public Battle getBattle(){
        return battle;
    }
    
    //returns a copy of the unit list
    public Unit[] getUnits(){
        return units.getItems();
    }
    
    //returns a copy of the property list
    public Property[] getProperties(){
        return properties.getItems();
    }
    
    //Can this Army TagBreak?
    public boolean canTag(){
        if(altCO != null && co.canSCOP() && altCO.canSCOP())
            return true;
        return false;
    }
    
    public boolean isTag(){
        return tag;
    }
    
    public int getAtkPercent(){
        return atkPercent;
    }
    
    public boolean canTagSwap(){
        return canTagSwap;
    }
    
    //returns the number of units
    public int getNumberOfUnits(){
        return units.getSize();
    }
    
    //returns the number of units
    public int getNumberOfProperties(){
        return properties.getSize();
    }
    
    //returns the Army's CO
    public CO getCO(){
        return co;
    }
    
    //returns the Army's ID number
    public int getID(){
        return id;
    }
    
    //sets the Army's ID number
    public void setID(int newid){
        id = newid;
    }
    
    //returns the Army's color
    public int getColor(){
        return color;
    }
    
    //returns the Army's side
    public int getSide(){
        return side;
    }
    
    //returns the Alternate CO
    public CO getAltCO(){
        return altCO;
    }
    
    //returns the Army's funds
    public int getFunds(){
        return funds;
    }
    
    //adds funds
    public void addFunds(int amount){
        funds += amount;
        if(funds < 0)funds = 0;
    }
    
    public void unTag(){
        tag = false;
        int i = isSpecTag();
        if(i > -1)
            atkPercent = 0;
    }
    
    //swaps COs
    public void swap(){
        //Random r = new Random();
        CO temp = co;
        if(co.isCOP())
            co.deactivateCOP();
        if(co.isSCOP())
            co.deactivateSCOP();
            
        //swaps the unit storages of the COs during a swap
        int[] storage = new int[10];
        Unit[] u = this.getUnits();
        for(int i = 0; i < this.getNumberOfUnits(); i++)
        {
            for(int s = 0; s< u[i].altCOstore.length; s++)
            {
            storage[s] = u[i].altCOstore[s];
            u[i].altCOstore[s] = u[i].COstore[s];
            u[i].COstore[s] = storage[s];
            }
        }
        //This switches the enemyCOstore of all enemies which uses enemyCOstore.
        Army[] a = battle.getArmies();
        for(int i = 0; i<a.length; i++)
        {
            if(a[i].getSide() != this.getSide())
            {
                u = a[i].getUnits();
                storage = new int[a[i].getNumberOfUnits()];
                for(int t= 0; t<a[i].getNumberOfUnits(); t++)
                {
                storage[t] = u[t].altEnemyCOstore[this.co.statIndex];
                u[t].altEnemyCOstore[this.co.statIndex] = u[t].enemyCOstore[this.co.statIndex];
                u[t].enemyCOstore[this.co.statIndex] = storage[t];
                }
            }
        }
        unChangeAll();
        unChangeAllProp();
        co = altCO;
        altCO = temp;
        setChangeAll();
        changeAllProp();
        System.out.println(co.name + ": " + co.Swap[battle.getRNG().nextInt(2)]);
        if(tag)
            canTagSwap = false;
    }
    
    //Tag Breaks!
    public void tagBreak(){
        int i = isSpecTag();
        if(i > -1){
            System.out.println(co.TagNames[i] + "!");
            atkPercent = (co.TagPercent[i] - 100);
            if(battle.getBattleOptions().isBalance() && atkPercent > 10)atkPercent=10;
            if(battle.getBattleOptions().isBalance() && atkPercent < -10)atkPercent=-10;
        } else
            System.out.println("Dual Strike!");
        tag = true;
        canTagSwap = true;
        co.activateSCOP();
        
        if(Options.isMusicOn()){
            Music.startPowerMusic(2,co.getStyle());
        }
    }
    
    public int isSpecTag(){
        if(altCO != null && co.TagCOs != null)
            for(int i = 0; i < co.TagCOs.length; i++)
                if(co.TagCOs[i] == altCO.getName())
                    return i;
        return -1;
    }
    
    //does daily use for all units, returns true if army is routed and it causes victory/defeat
    public boolean allDailyUse(){
        if(!units.isEmpty()){
            UnitLink current = units.first;
            while(current != null){
                if(current.getData().dailyUse())
                    if(this.getBattle().removeArmy(this,null,false))
                        return true;
                current = current.next;
            }
        }
        return false;
    }
    
    public void charge(double d){
        if(co.COP || co.SCOP) return;
        co.charge(d);
        if(altCO != null)altCO.charge(d/2.0);
    }
    
    
    //subtracts funds
    public void removeFunds(int amount){
        funds -= amount;
        if(funds < 0)funds = 0;
    }
    
    //return the number of com towers the army owns
    public int getComTowers(){
        return numComTowers;
    }

    //sets the Army's CO
    public void setCO(CO c){
        co = c;
        co.setArmy(this);
    }
    
    //sets the Army's CO
    public void setAltCO(CO c){
        altCO = c;
        if(altCO != null){
            altCO.setArmy(this);
            altCO.statIndex = id;
        }
    }
    
    public boolean hasAI(){
        if(ai != null)
            return true;
                    return false;
    }
    
    public void runAI(){
        ai.start();
    }

    //returns the given day start color
    //public Color getDayStartColor(int index){
    //    return dayStartColor[index];
    //}
}