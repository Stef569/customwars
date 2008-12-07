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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ContextMenu extends InGameMenu {
    Unit u;
	final static Logger logger = LoggerFactory.getLogger(ContextMenu.class); 
    
    //[NEW]
    private static ArrayList<Location> contextTargs = new ArrayList<Location>();
    
    //constructor
    public ContextMenu(Unit temp, boolean fire, boolean capture, boolean resupply, boolean unload, boolean unload2, boolean repair, boolean launch, boolean explode, boolean dive, boolean rise, boolean hide, boolean appear, boolean join, boolean load, boolean special1, boolean special2, boolean takeoff, boolean takeoff2, boolean build, ImageObserver screen){
        super((480-96)/2,(320-80)/2,96,screen);
        String[] s = new String[6]; //max in one context window is 4 (Black Boat, Cruiser, and Carrier)
        //Expanded for time being.
        int i = 0;
        u = temp;
        if((temp.getArmy().getBattle().getMap().find(temp).getTerrain().getName().equals("Wall"))
            || (temp.getUType() == UnitID.CARRIER && ((Carrier)temp).launched) && temp.getMoved()) {
            s[0] = "No.";
            i++;
        }else{
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
                if(takeoff){s[i]="Takeoff";i++;}
                if(takeoff2){s[i]="Takeoff";i++;}
                if(build){s[i]="Build";i++;}
                if(!u.noWait && (!temp.getArmy().getBattle().getMap().find(temp).getTerrain().getName().equals("Wall")) && !(temp.getUType() == UnitID.CARRIER && ((Carrier)temp).launched)){s[i] = "Wait"; i++;}
            }else if(join == true){
                s[0] = "Join";i++;
            }else{
                s[0] = "Load";i++;
            }
            if(i == 0) { //nothing in the list? No!
                s[0] = "No.";
                i++;
            }
        }
        String[] s2 = new String[i];
        for(int j=0;j<i;j++)s2[j]=s[j];
        super.loadStrings(s2);
    }
    
    public int doMenuItem(){
       	String soundLocation = ResourceLoader.properties.getProperty("soundLocation");
        SFX.playClip(soundLocation + "/ok.wav");
        if(displayItems[item].equals("Wait")){
            logger.info("Wait");
            return 0;
        }else if(displayItems[item].equals("Fire")){
            logger.info("Fire");
            return 1;
        }else if(displayItems[item].equals("Capture")){
            logger.info("Capture");
            return 2;
        }else if(displayItems[item].equals("Resupply")){
            logger.info("Resupply");
            return 3;
        }else if(displayItems[item].equals("Unload")){
            if(item-1>=0){
                if(displayItems[item-1].equals("Unload")){
                    logger.info("Unload #2");
                    return 7;
                }
            }
            logger.info("Unload #1");
            return 6;
        }else if(displayItems[item].equals("Repair")){
            logger.info("Repair");
            return 10;
        }else if(displayItems[item].equals("Launch")){
            logger.info("Launch");
            return 8;
        }else if(displayItems[item].equals("Explode")){
            logger.info("Explode");
            return 9;
        }else if(displayItems[item].equals("Join")){
            logger.info("Join");
            return 4;
        }else if(displayItems[item].equals("Load")){
            logger.info("Load");
            return 5;
        }else if(displayItems[item].equals("Dive")){
            logger.info("Dive");
            return 11;
        }else if(displayItems[item].equals("Rise")){
            logger.info("Rise");
            return 12;
        }else if(displayItems[item].equals("Hide")){
            logger.info("Hide");
            return 13;
        }else if(displayItems[item].equals("Appear")){
            logger.info("Appear");
            return 14;
        }else if(displayItems[item].equals(u.getArmy().getCO().special1)){
            logger.info(u.getArmy().getCO().special1);
            return 22;
        }else if(displayItems[item].equals(u.getArmy().getCO().special2)){
            logger.info(""+displayItems[item].equals(u.getArmy().getCO().special2));
            return 23;
        }else if(displayItems[item].equals("Takeoff")){
            if(item-1>=0){
                if(displayItems[item-1].equals("Takeoff")){
                    logger.info("Takeoff #2");
                    return UNIT_COMMANDS.LAUNCH2;
                }
            }
            logger.info("Takeoff #1");
            return UNIT_COMMANDS.LAUNCH;
        }else if(displayItems[item].equals("Build")){
            logger.info("Build");
            return UNIT_COMMANDS.BUILD;
        }else if(displayItems[item].equals("No.")){
            logger.info("Invalid move");
        }else{
            System.err.println("ERROR, INVALID CONTEXT MENU ITEM");
        }
        return -1;
    }
    
    public static ContextMenu generateContext(Unit u, boolean join, boolean load, boolean secondunload, boolean secondTakeoff, ImageObserver screen){
        boolean fire=false, capture=false, supply=false, unload=false, unload2=false, repair=false, launch=false, explode=false, dive=false, rise=false, hide=false, appear=false, special1 = false, special2=false, takeoff = false, takeoff2 = false, build = false;
        Map m = u.getMap();
        
        if(join)return new ContextMenu(u,fire,capture,supply,unload,unload2,repair,launch,explode,dive,rise,hide,appear,true,false,false, false, false, false, false,screen);
        if(load)return new ContextMenu(u, fire,capture,supply,unload,unload2,repair,launch,explode,dive,rise,hide,appear,false,true,false, false, false, false, false,screen);
        
        contextTargs = new ArrayList<Location>();
        
        //FIRE
        boolean canFire = true;
        //prevents indirects from firing
        if(u.getMoved())
            if(u.getMinRange()>1)
                canFire = false;
        if(canFire && !u.noFire) {
            for(int i=0;i<m.getMaxCol();i++) {
                for(int j=0;j<m.getMaxRow();j++) {
                    //if enemy unit in firing range, add fire to the context menu
                    if(u.checkFireRange(new Location(i,j))&&
                            ((m.find(new Location(i,j)).hasUnit())&&
                            (m.find(new Location(i,j)).getUnit().getArmy().getSide()!=u.getArmy().getSide()))) {
                        if(u.displayDamageCalc(m.find(new Location(i,j)).getUnit())>-1 && !m.find(new Location(i,j)).getUnit().isHidden()) {
                            //[NEW]
                            //Add each target's location to the list
                            contextTargs.add(new Location(i,j));
                            fire=true;
                        }
                    }
                    //check for neutral or enemy Inventions
                    if(u.checkFireRange(new Location(i,j)) && m.find(new Location(i,j)).getTerrain() instanceof Invention) {
                        if(u.damageCalc((Invention)m.find(new Location(i,j)).getTerrain())!=-1) {
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
        if(u.getUType()==12) {
            if(((Submarine)u).isDived() && !u.noRise) {
                rise = true;
            } else if(!u.noDive) {
                dive = true;
            }
        }
        
        //HIDE/APPEAR
        //Is the unit a Stealth? And if so, are its abilities disabled?
        if(u.getUType()==23) {
            if(((Stealth)u).isDived() && !u.noAppear) {
                appear = true;
            } else if(!u.noHide) {
                hide = true;
            }
        }
        //Special 1
        if(u.getArmy().getCO().canUseSpecial1(u) && !u.noSpecial1)
            special1 = true;
        //special 2
        if(u.getArmy().getCO().canUseSpecial2(u) && !u.noSpecial2)
            special2 = true;
        
        if(u.getUType()==UnitID.CARRIER && ((Transport)u).getUnitsCarried() > 0 && !u.getMoved()) {
            if(u instanceof Transport && !u.noUnload){
                Transport trans = (Transport) u;
                int x = trans.getLocation().getCol();
                int y = trans.getLocation().getRow();
                
                if(trans.getUnitsCarried() == 2){
                    if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(2).getMType())!=-1){
                        takeoff2 = true;
                        if(!trans.checkUnloadRange(new Location(x,y+1),2)&&!trans.checkUnloadRange(new Location(x,y-1),2)&&!trans.checkUnloadRange(new Location(x+1,y),2)&&!trans.checkUnloadRange(new Location(x-1,y),2)){
                            takeoff2 = false;
                        }
                    }
                    if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(1).getMType())!=-1){
                        takeoff = true;
                        if(!trans.checkUnloadRange(new Location(x,y+1),1)&&!trans.checkUnloadRange(new Location(x,y-1),1)&&!trans.checkUnloadRange(new Location(x+1,y),1)&&!trans.checkUnloadRange(new Location(x-1,y),1)){
                            takeoff = false;
                        }
                    }
                }else if(trans.getUnitsCarried() == 1){
                    if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(1).getMType())!=-1){
                        takeoff = true;
                        if(!trans.checkUnloadRange(new Location(x,y+1),1)&&!trans.checkUnloadRange(new Location(x,y-1),1)&&!trans.checkUnloadRange(new Location(x+1,y),1)&&!trans.checkUnloadRange(new Location(x-1,y),1)){
                            takeoff = false;
                        }
                    }
                }
            }        if(u instanceof Transport && !u.noUnload){
                Transport trans = (Transport) u;
                int x = trans.getLocation().getCol();
                int y = trans.getLocation().getRow();
                
                if(trans.getUnitsCarried() == 2){
                    if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(2).getMType())!=-1){
                        takeoff2 = true;
                        if(!trans.checkUnloadRange(new Location(x,y+1),2)&&!trans.checkUnloadRange(new Location(x,y-1),2)&&!trans.checkUnloadRange(new Location(x+1,y),2)&&!trans.checkUnloadRange(new Location(x-1,y),2)){
                            takeoff2 = false;
                        }
                    }
                    if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(1).getMType())!=-1){
                        takeoff = true;
                        if(!trans.checkUnloadRange(new Location(x,y+1),1)&&!trans.checkUnloadRange(new Location(x,y-1),1)&&!trans.checkUnloadRange(new Location(x+1,y),1)&&!trans.checkUnloadRange(new Location(x-1,y),1)){
                            takeoff = false;
                        }
                    }
                }else if(trans.getUnitsCarried() == 1){
                    if(m.find(trans.getLocation()).getTerrain().moveCost(trans.getUnit(1).getMType())!=-1){
                        takeoff = true;
                        if(!trans.checkUnloadRange(new Location(x,y+1),1)&&!trans.checkUnloadRange(new Location(x,y-1),1)&&!trans.checkUnloadRange(new Location(x+1,y),1)&&!trans.checkUnloadRange(new Location(x-1,y),1)){
                            takeoff = false;
                        }
                    }
                }
            }
        }
        


        //UNLOAD
        if(u instanceof Transport && !u.noUnload && u.getUnitType() != UnitID.CARRIER){
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
        //conditions for takeoff have already been checked.
        if(u.getUType()==UnitID.CARRIER && !u.getMoved() && ((Carrier)u).getUnitsCarried() < 2)
            build = true;
        if(u.getUnitType() == UnitID.CARRIER && ((Carrier)u).usedAction())
        {
            fire = false;
            build = false;
        }
        if(u.getUnitType() == UnitID.CARRIER && ((Carrier)u).builtUnit)
        {
            //don't disable takeoff unless the carrier has built a unit
            //it has been checked for moving however.
            takeoff = false;
        }
        if(secondunload || secondTakeoff){
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
        //takeoff = false;
        //build = false;
        return new ContextMenu(u, fire,capture,supply,unload,unload2,repair,launch,explode,dive,rise,hide,appear,false,false,special1, special2, takeoff, takeoff2, build, screen);
    }
    
    //[NEW]
    public static ArrayList<Location> getContextTargs() {
        return contextTargs;
    }
}