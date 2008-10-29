package cwsource;
/*
 *ContextMenu.java
 *Author: Urusan
 *Contributors: Adam Dziuk
 *Creation: July 14, 2006, 10:04 AM
 *This menu pops up when a unit finishes moving, and lets the user select an action
 *It has several possible combinations depending on the context
 */

import java.awt.*;
import java.awt.image.*;
import java.util.ArrayList;

public class ContextMenu extends InGameMenu
{
    Unit u;
    
    //[NEW]
    private static ArrayList<Location> contextTargs = new ArrayList<Location>();
    
    //constructor
    public ContextMenu(Unit temp, boolean fire, boolean capture, boolean resupply, boolean unload, boolean unload2, boolean repair, boolean launch, boolean explode, boolean dive, boolean rise, boolean hide, boolean appear, boolean join, boolean load, boolean special1, boolean special2, ImageObserver screen){
        super((480-96)/2,(320-80)/2,96,screen);
        String[] s = new String[6]; //max in one context window is 4 (Black Boat, Cruiser, and Carrier)
        //Expanded for time being.
        int i = 0;
        u = temp;
        if(join == false && load == false){
            if(fire){s[i]="Fire";i++;}
            if(capture){s[i]="Capture";i++;}
            if(unload){s[i]="Unload";i++;}
            if(unload2){s[i]="Unload";i++;}
            if(resupply){s[i]="Resupply";i++;}
            if(repair){s[i]="Repair";i++;}
            if(launch){s[i]="Launch";i++;}
            if(explode){s[i]="Explode";i++;}
            if(dive){s[i]="Dive";i++;}
            if(rise){s[i]="Rise";i++;}
            if(hide){s[i]="Hide";i++;}
            if(appear){s[i]="Appear";i++;}
            if(special1){s[i] = u.getArmy().getCO().special1;i++;}
            if(special2){s[i] = u.getArmy().getCO().special2;i++;}
            if(!u.noWait){s[i] = "Wait"; i++;}
        }else if(join == true){
            s[0] = "Join";i++;
        }else{
            s[0] = "Load";i++;
        }
        if(u.noWait && i == 0)
        {
        	s[0] = "!!!";
        	i++;
        }
        
        String[] s2 = new String[i];
        for(int j=0;j<i;j++)s2[j]=s[j];
        super.loadStrings(s2);
    }
    
    public int doMenuItem(){
        if(displayItems[item].equals("Wait")){
            System.out.println("Wait");
        }else if(displayItems[item].equals("Fire")){
            System.out.println("Fire");
            return 1;
        }else if(displayItems[item].equals("Capture")){
            System.out.println("Capture");
            return 2;
        }else if(displayItems[item].equals("Resupply")){
            System.out.println("Resupply");
            return 3;
        }else if(displayItems[item].equals("Unload")){
            if(item-1>=0){
                if(displayItems[item-1].equals("Unload")){
                    System.out.println("Unload #2");
                    return 7;
                }
            }
            System.out.println("Unload #1");
            return 6;
        }else if(displayItems[item].equals("Repair")){
            System.out.println("Repair");
            return 10;
        }else if(displayItems[item].equals("Launch")){
            System.out.println("Launch");
            return 8;
        }else if(displayItems[item].equals("Explode")){
            System.out.println("Explode");
            return 9;
        }else if(displayItems[item].equals("Join")){
            System.out.println("Join");
            return 4;
        }else if(displayItems[item].equals("Load")){
            System.out.println("Load");
            return 5;
        }else if(displayItems[item].equals("Dive")){
            System.out.println("Dive");
            return 11;
        }else if(displayItems[item].equals("Rise")){
            System.out.println("Rise");
            return 12;
        }else if(displayItems[item].equals("Hide")){
            System.out.println("Hide");
            return 13;
        }else if(displayItems[item].equals("Appear")){
            System.out.println("Appear");
            return 14;
        }else if(displayItems[item].equals(u.getArmy().getCO().special1)){
            System.out.println(u.getArmy().getCO().special1);
            return 22;
        }else if(displayItems[item].equals(u.getArmy().getCO().special2)){
            System.out.println(displayItems[item].equals(u.getArmy().getCO().special2));
            return 23;
        }else{
            System.err.println("ERROR, INVALID CONTEXT MENU ITEM");
        }
        return 0;
    }
    
    public static ContextMenu generateContext(Unit u, boolean join, boolean load, boolean secondunload, ImageObserver screen){
        boolean fire=false, capture=false, supply=false, unload=false, unload2=false, repair=false, launch=false, explode=false, dive=false, rise=false, hide=false, appear=false, special1 = false, special2=false;
        Map m = u.getMap();
        
        if(join)return new ContextMenu(u,fire,capture,supply,unload,unload2,repair,launch,explode,dive,rise,hide,appear,true,false,false, false, screen);
        if(load)return new ContextMenu(u, fire,capture,supply,unload,unload2,repair,launch,explode,dive,rise,hide,appear,false,true,false, false, screen);

        contextTargs = new ArrayList<Location>();
        
        //FIRE
        boolean canFire = true;
        //prevents indirects from firing
        if(u.getMoved())
            if(u.getMinRange()>1)
                canFire = false;
        if(canFire && !u.noFire)
        {
            for(int i=0;i<m.getMaxCol();i++)
            {
                for(int j=0;j<m.getMaxRow();j++)
                {
                    //if enemy unit in firing range, add fire to the context menu
                    if(u.checkFireRange(new Location(i,j))&&
                            ((m.find(new Location(i,j)).hasUnit())&&
                            (m.find(new Location(i,j)).getUnit().getArmy().getSide()!=u.getArmy().getSide())))
                    {
                        if(u.displayDamageCalc(m.find(new Location(i,j)).getUnit())>-1 && !m.find(new Location(i,j)).getUnit().isHidden())
                        {
                            //[NEW]
                        	//Add each target's location to the list             
                        	contextTargs.add(new Location(i,j));
                            fire=true;
                        }
                    }
                    //check for neutral or enemy Inventions
                    if(u.checkFireRange(new Location(i,j)) && m.find(new Location(i,j)).getTerrain() instanceof Invention)
                    {
                        if(u.damageCalc((Invention)m.find(new Location(i,j)).getTerrain())!=-1)
                        {
                            //[NEW]
                        	//Add each target's location to the list
                        	contextTargs.add(new Location(i,j));
                            fire=true;
                        }
                    }
                }
            }
        }
        
        //CAPTURE & LAUNCH
        //is the unit an infantry type?
        if(u.getUType()==0 || u.getUType()==1){
            //is the terrain a property?
            if(m.find(u.getLocation()).getTerrain() instanceof Property){
                Property p = (Property)m.find(u.getLocation()).getTerrain();
                if(p.isCapturable() && !u.noCapture){
                    if(p.getOwner()!=null){
                        if(p.getOwner().getSide()!=u.getArmy().getSide())
                            capture = true;
                    }else{
                        capture = true;
                    }
                }else{
                    if(!((Silo)p).isLaunched() && !u.noLaunch)launch = true;
                }
            }
        }
        
        //SUPPLY
        //is the unit an APC?
        if(u.getUType()==9 && !u.noResupply){
            for(int i=0;i<m.getMaxCol();i++){
                for(int j=0;j<m.getMaxRow();j++){
                    //if friendly in resupply range, add resupply to the context menu
                    if(u.checkAdjacent(new Location(i,j))&&
                            (m.find(new Location(i,j)).hasUnit()&&
                            m.find(new Location(i,j)).getUnit().getArmy()==u.getArmy()
                            && !m.find(new Location(i,j)).getUnit().noResupplied))
                        supply=true;
                }
            }
            //supply=true;
        }
        
        //REPAIR
        //Is the unit a Black Boat?
        if(u.getUType()==21 && !u.noRepair){
            for(int i=0;i<m.getMaxCol();i++){
                for(int j=0;j<m.getMaxRow();j++){
                    //if friendly in repair range, add repair to the context menu
                    if(u.checkAdjacent(new Location(i,j))&&
                            (m.find(new Location(i,j)).hasUnit()&&
                            m.find(new Location(i,j)).getUnit().getArmy()==u.getArmy()
                            && !m.find(new Location(i,j)).getUnit().noResupplied
                            && !m.find(new Location(i,j)).getUnit().noRepaired))
                        repair=true;
                }
            }
            //repair = true;
        }
        
        //EXPLODE
        //is the unit a Black Bomb?
        if(u.getUType()==24 && !u.noExplode){
            explode = true;
        }
        
        //DIVE/RISE
        //Is the unit a Submarine? And if so, are its abilities disabled?
        if(u.getUType()==12)
        {
            if(((Submarine)u).isDived() && !u.noRise)
            {
            	rise = true;
            }
            else if(!u.noDive)
            {
            	dive = true;
            }
        }
        
        //HIDE/APPEAR
        //Is the unit a Stealth? And if so, are its abilities disabled?
        if(u.getUType()==23)
        {
            if(((Stealth)u).isDived() && !u.noAppear)
            {
            	appear = true;
            }
            else if(!u.noHide)
            {
            	hide = true;
            }
        }
        //Special 1
        if(u.getArmy().getCO().canUseSpecial1(u) && !u.noSpecial1)
            special1 = true;
        //special 2
        if(u.getArmy().getCO().canUseSpecial2(u) && !u.noSpecial2)
            special2 = true;
        //UNLOAD
        if(u instanceof Transport && !u.noUnload){
            Transport trans = (Transport) u;
            int x = trans.getLocation().getCol();
            int y = trans.getLocation().getRow();
            
            if(trans.getUnitsCarried() == 2){
                if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(2).getMType())!=-1){
                    unload2 = true;
                    if(!trans.checkUnloadRange(new Location(x,y+1),2)&&!trans.checkUnloadRange(new Location(x,y-1),2)&&!trans.checkUnloadRange(new Location(x+1,y),2)&&!trans.checkUnloadRange(new Location(x-1,y),2)){
                        unload2 = false;
                    }
                }
                if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(1).getMType())!=-1){
                    unload = true;
                    if(!trans.checkUnloadRange(new Location(x,y+1),1)&&!trans.checkUnloadRange(new Location(x,y-1),1)&&!trans.checkUnloadRange(new Location(x+1,y),1)&&!trans.checkUnloadRange(new Location(x-1,y),1)){
                        unload = false;
                    }
                }
            }else if(trans.getUnitsCarried() == 1){
                if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(1).getMType())!=-1){
                    unload = true;
                    if(!trans.checkUnloadRange(new Location(x,y+1),1)&&!trans.checkUnloadRange(new Location(x,y-1),1)&&!trans.checkUnloadRange(new Location(x+1,y),1)&&!trans.checkUnloadRange(new Location(x-1,y),1)){
                        unload = false;
                    }
                }
            }
        }
        
        if(secondunload){
            fire=false;
            //capture=false;
            //supply=false;
            repair=false;
            //launch=false;
            //explode=false;
            //dive=false;
            //rise=false;
            //hide=false;
            //appear=false;
        }
        
        return new ContextMenu(u, fire,capture,supply,unload,unload2,repair,launch,explode,dive,rise,hide,appear,false,false,special1, special2, screen);
    }
    
    //[NEW]
    public static ArrayList<Location> getContextTargs()
    {
    	return contextTargs;
    }
}