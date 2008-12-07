package cwsource;

/*
 *MiscGraphics.java
 *Author: Urusan
 *Contributors:
 *Creation: July 12, 2006, 12:26 PM
 *Holds graphics information for many minor things
 */

import java.awt.*;
import javax.imageio.*;
import java.awt.image.*;
import java.io.*;
import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Random;

public class BattleGraphics {
    private static int backStyle = 1;      //amount of styles possible
    private static Image[][] sky = new Image[backStyle][2];
    private static Image[][] wood = new Image[backStyle][2];
    private static Image[][] plain = new Image[backStyle][3];
    private static Image[] road = new Image[backStyle];
    private static Image[] mount = new Image[backStyle];
    private static Image[][] city = new Image[backStyle][4];
    //Transparency over efficieny. Alas
    private static Image[][] bwood = new Image[backStyle][2];
    private static Image[] bplain = new Image[backStyle];
    private static Image[] broad = new Image[backStyle];
    private static Image[] bmount = new Image[backStyle];
    private static Image[][] burban = new Image[backStyle][5];
    
    private static Image[][] HQ = new Image[backStyle][5];
    private static Image[][] bHQ = new Image[backStyle][5];
	final static Logger logger = LoggerFactory.getLogger(BattleGraphics.class); 
    final static String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
    
    private static Image MDTank;      //the images used for moving units
    private static Image Rocket;
    private static Image Smoke;    //the images used for firing units
    private static Image missile; //the missile
    private Random random = new Random();
    //constructor
    public BattleGraphics() {
    }
    
    //loads the images before use
    public static void loadImages(Component screen){
        MDTank = new ImageIcon(imagesLocation + "/ba/unit/mdtank_cannon.gif").getImage();
        Rocket = new ImageIcon(imagesLocation + "/ba/unit/rockets.gif").getImage();
        Smoke = new ImageIcon(imagesLocation + "/ba/unit/missile_smoke.gif").getImage();
        missile = new ImageIcon(imagesLocation + "/ba/unit/missile.gif").getImage();
        for(int i = 0; i<backStyle; i++) {
            sky[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/bg/sky"+i+".gif").getImage();
            sky[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/bg/forest"+i+".gif").getImage();
            
            wood[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/wood"+i+"a.gif").getImage();
            wood[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/wood"+i+"b.gif").getImage();
            
            plain[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/plain"+i+"a.gif").getImage();
            plain[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/plain"+i+"b.gif").getImage();
            plain[i][2] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/plain"+i+"c.gif").getImage();
            
            road[i] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/road"+i+".gif").getImage();
            city[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/city"+i+".gif").getImage();
            city[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/base"+i+".gif").getImage();
            city[i][2] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/airport"+i+".gif").getImage();
            city[i][3] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/port"+i+".gif").getImage();
            mount[i] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/mount"+i+".gif").getImage();
            
            bwood[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/back/wood"+i+"a.gif").getImage();
            bwood[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/back/wood"+i+"b.gif").getImage();
            bplain[i] = new ImageIcon(imagesLocation + "/ba/backdrop/back/plain"+i+".gif").getImage();
            broad[i] = new ImageIcon(imagesLocation + "/ba/backdrop/back/road"+i+".gif").getImage(); //dun have
            burban[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/back/city"+i+"a.gif").getImage(); //plain background
            burban[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/back/city"+i+"b.gif").getImage(); //city background
            burban[i][2] = new ImageIcon(imagesLocation + "/ba/backdrop/back/base"+i+".gif").getImage(); //city background
            burban[i][3] = new ImageIcon(imagesLocation + "/ba/backdrop/back/airport"+i+".gif").getImage(); //city background
            burban[i][4] = new ImageIcon(imagesLocation + "/ba/backdrop/back/port"+i+".gif").getImage(); //city background
            bmount[i] = new ImageIcon(imagesLocation + "/ba/backdrop/back/mount"+i+".gif").getImage();
            
            HQ[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/oshq" + i + ".gif").getImage();
            HQ[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/bmhq" + i + ".gif").getImage();
            HQ[i][2] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/gehq" + i + ".gif").getImage();
            HQ[i][3] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/ychq" + i + ".gif").getImage();
            HQ[i][4] = new ImageIcon(imagesLocation + "/ba/backdrop/fore/bhhq" + i + ".gif").getImage();
            
            bHQ[i][0] = new ImageIcon(imagesLocation + "/ba/backdrop/back/oshq" + i + ".gif").getImage();
            bHQ[i][1] = new ImageIcon(imagesLocation + "/ba/backdrop/back/bmhq" + i + ".gif").getImage();
            bHQ[i][2] = new ImageIcon(imagesLocation + "/ba/backdrop/back/gehq" + i + ".gif").getImage();
            bHQ[i][3] = new ImageIcon(imagesLocation + "/ba/backdrop/back/ychq" + i + ".gif").getImage();
            bHQ[i][4] = new ImageIcon(imagesLocation + "/ba/backdrop/back/bhhq" + i + ".gif").getImage();
            System.out.println("Goes through here");
        }
    }
    
    public static Image getImage(Unit u) {
        if(u.getMaxRange()>1)
            return Rocket;
        return MDTank;
    }
    public static Image getSmoke(){
        return Smoke;
    }
    
    public static Image getSky(int style, Terrain t) {
        if(t.getName().equals("Wood"))
            return sky[style][1];
        return sky[style][0];
    }
    public static Image getPlainBack(int style, Unit attacker, Unit defender){
        Terrain atkTerrain = attacker.getArmy().getBattle().getMap().find(attacker).getTerrain();
        Terrain defTerrain = defender.getArmy().getBattle().getMap().find(defender).getTerrain();
        int orient = attacker.getLocation().orient(defender.getLocation());
        switch(orient){
            case 0: //north
                if(attacker.getArmy().getBattle().getMap().onMap(new Location(attacker.getLocation().getCol(), attacker.getLocation().getRow()+1)))
                    return getBackDrop(0,attacker.getArmy().getBattle().getMap().find(new Location(attacker.getLocation().getCol(), attacker.getLocation().getRow()+1)).getTerrain());
            case 1: //west
                if(attacker.getArmy().getBattle().getMap().onMap(new Location(attacker.getLocation().getCol()+1, attacker.getLocation().getRow())))
                    return getBackDrop(0,attacker.getArmy().getBattle().getMap().find(new Location(attacker.getLocation().getCol()+1, attacker.getLocation().getRow())).getTerrain());
            case 2: //south
                if(attacker.getArmy().getBattle().getMap().onMap(new Location(attacker.getLocation().getCol(), attacker.getLocation().getRow()-1)))
                    return getBackDrop(0,attacker.getArmy().getBattle().getMap().find(new Location(attacker.getLocation().getCol(), attacker.getLocation().getRow()-1)).getTerrain());
            case 3: // east
                if(attacker.getArmy().getBattle().getMap().onMap(new Location(attacker.getLocation().getCol()-1, attacker.getLocation().getRow())))
                    return getBackDrop(0,attacker.getArmy().getBattle().getMap().find(new Location(attacker.getLocation().getCol()-1, attacker.getLocation().getRow())).getTerrain());
            default:
                return bwood[style][0]; // if the angle isn't orthogonal, use wood.'
        }
    }
    
    public static Image getBackDrop(int style, Terrain t) {
        if(t.isUrban())
            return burban[style][0];
        else switch(t.getIndex()) {
            case 0:
                return bplain[style];//*/
            case 1:
                return bwood[style][0];
            case 2:
                return bmount[style];
            case 3:
                return broad[style];//*/
            default:
            	logger.info("defaulted");
                return bwood[style][0];
        }
    }
        public static Image getAltBack(int style, Terrain t) {
            if(t.getName().equals("Wood"))
                return bwood[style][1];
            else if(t.getName().equals("City"))
                return burban[style][1];
            else if(t.getName().equals("Base"))
                return burban[style][2];
            else if(t.getName().equals("Airport"))
                return burban[style][3];
            else if(t.getName().equals("Port"))
                return burban[style][4];
            else if(t.getName().equals("HQ"))
                return bHQ[style][((Property)t).getOwner().getCO().getStyle()];
            return null;
        }
        public static Image  getFore(int style, Terrain t) {
            if(t.isUrban()) {
                if(t.getName().equals("City"))
                    return city[style][0];
                else if(t.getName().equals("Base"))
                    return city[style][1];
                else if(t.getName().equals("Airport"))
                    return city[style][2];
                else if(t.getName().equals("Port"))
                    return city[style][3];
                else if(t.getName().equals("HQ"))
                    return HQ[style][((Property)t).getOwner().getCO().getStyle()];
                return city[style][0];
            } else switch(t.getIndex()) {
                case 0:
                    return plain[style][2];
                case 1:
                    return wood[style][0];
                case 2:
                    return mount[style];
                case 3:
                    return road[style];
                default:
                    return plain[style][1];
            }
        }
        public static Image getMissile(){
            return missile;
        }
    }
