package com.customwars.ai;
/*
 *Battle.java
 *Author: Adam Dziuk
 *Contributors:Urusan
 *Creation: June 11, 2006
 *Battle keeps track of turns, holds the army list and map, etc.
 */

import java.io.*;

//TEMPORARY?
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.map.Map;
import com.customwars.map.Tile;
import com.customwars.map.location.Airport;
import com.customwars.map.location.Base;
import com.customwars.map.location.Bridge;
import com.customwars.map.location.City;
import com.customwars.map.location.ComTower;
import com.customwars.map.location.DestroyedPipeSeam;
import com.customwars.map.location.DestroyedSeaPipeSeam;
import com.customwars.map.location.DestroyedWall;
import com.customwars.map.location.HQ;
import com.customwars.map.location.Invention;
import com.customwars.map.location.Location;
import com.customwars.map.location.Mountain;
import com.customwars.map.location.Pipe;
import com.customwars.map.location.PipeSeam;
import com.customwars.map.location.Pipestation;
import com.customwars.map.location.Plain;
import com.customwars.map.location.Port;
import com.customwars.map.location.Property;
import com.customwars.map.location.Reef;
import com.customwars.map.location.River;
import com.customwars.map.location.Road;
import com.customwars.map.location.Sea;
import com.customwars.map.location.SeaPipe;
import com.customwars.map.location.SeaPipeSeam;
import com.customwars.map.location.Shoal;
import com.customwars.map.location.Silo;
import com.customwars.map.location.SuspensionBridge;
import com.customwars.map.location.TerrType;
import com.customwars.map.location.Terrain;
import com.customwars.map.location.Wall;
import com.customwars.map.location.Wood;
import com.customwars.officer.Andy;
import com.customwars.officer.CO;
import com.customwars.officer.COList;
import com.customwars.officer.Fighter;
import com.customwars.officer.Max;
import com.customwars.sfx.Music;
import com.customwars.state.ReplayQueue;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.Animation;
import com.customwars.ui.DialogueBox;
import com.customwars.unit.APC;
import com.customwars.unit.AntiAir;
import com.customwars.unit.Army;
import com.customwars.unit.Artillery;
import com.customwars.unit.Artillerycraft;
import com.customwars.unit.BCopter;
import com.customwars.unit.Battlecraft;
import com.customwars.unit.Battleship;
import com.customwars.unit.BlackBoat;
import com.customwars.unit.BlackBomb;
import com.customwars.unit.Bomber;
import com.customwars.unit.Carrier;
import com.customwars.unit.Cruiser;
import com.customwars.unit.Destroyer;
import com.customwars.unit.Infantry;
import com.customwars.unit.Lander;
import com.customwars.unit.MDTank;
import com.customwars.unit.Mech;
import com.customwars.unit.MegaTank;
import com.customwars.unit.Missiles;
import com.customwars.unit.Neotank;
import com.customwars.unit.Oozium;
import com.customwars.unit.Piperunner;
import com.customwars.unit.Recon;
import com.customwars.unit.Rockets;
import com.customwars.unit.Shuttlerunner;
import com.customwars.unit.Spyplane;
import com.customwars.unit.Stealth;
import com.customwars.unit.Submarine;
import com.customwars.unit.TCopter;
import com.customwars.unit.Tank;
import com.customwars.unit.Transport;
import com.customwars.unit.Unit;
import com.customwars.unit.Zeppelin;

import java.net.*;
//import javax.media.bean.playerbean.MediaPlayer;
//import java.util.Random;
import java.util.ArrayList;

public class Battle implements Serializable{
    private static final String TEMPORARYSAVE_SAVE_FILENAME = "temporarysave.save";

	private final int ARMY_LIMIT = 10;               //The maximum number of armies allowed in one battle
    
    private Map m;                                  //The battle's map
    private Army armies[] = new Army[ARMY_LIMIT];   //The battle's army list
    private Army statArmies[] = new Army[ARMY_LIMIT];//Preserves the armies in their original order, used to get stats when the game is over
    private int numArmies;                          //the number of armies in use
    private int numStatArmies;                      //the original number of armies
    private int turn;                               //the index of the army currently taking their turn
    private int day;                                //the current day of the battle
    private BattleOptions battleOptions;            //holds the battle options
    private boolean[][] fog;                        //holds the fog of war grid (false = no fog, true = fog)
    private boolean isfog;                          //is fog on this turn?
    private boolean ismist;                         //is mist on this turn?
    //fog is stronger than mist; if fog is on,
    //mist will not appear.
	final static Logger logger = LoggerFactory.getLogger(Battle.class); 
    
    private int currVisibility;                     //dictates the current visibility for
    //this turn.
    //0=Clear 1=Fog 2=Mist
    
    private int weather;                            //the current weather 0=clear 1=Rain 2=Snow 3=Sandstorm
    private int wduration;                          //the duration of the current weather in turns (-1 = infinite)
    private int wturn;                              //what turn did the weather start? (-1 = infinite)
    //private Random rw;                              //used to calculate random weather
    private ReplayQueue replayQ;                    //the replay queue, stores actions for replays
    private RNG rng;                                //the random number generator for this battle
    private ArrayList<Animation> animation1, animation2, animation3, animation4; //Holds the animations for layers 1 through 4
    public ArrayList<Animation> queue;              //hold animations while they wait to be displayed.
    public ArrayList<DialogueBox> diagQueue;        //Dialoge boxes execute sequentially.
    private boolean[] alts;
    public boolean animlock = false;                //Toggling animlock prevents player interaction (referenced from BattleScreen)
    //used in BattleScreen, stored here so it is saved and loaded
    public Location[] cursorLocation = new Location[10]; //the cursor locations (used to remember between turns)
    private double willitrain;
    private double willitsnow;
    private double willitsand;
    private int weatherget;
    private int wtimeget;
    private ArrayList<Integer> wselect = new ArrayList<Integer>();
    //creates an "empty" battle, used to start the map editor
    public Battle(Map mx){
        battleOptions = new BattleOptions();
        m = mx;
        numArmies = ARMY_LIMIT;
        animation1 = new ArrayList<Animation>();
        animation2 = new ArrayList<Animation>();
        animation3 = new ArrayList<Animation>();
        animation4 = new ArrayList<Animation>();
        queue = new ArrayList<Animation>();
        diagQueue = new ArrayList<DialogueBox>();
        //initialize the armies
        for(int i = 0; i < numArmies; i++){
            armies[i] = new Army(i,new Andy(), new Max(), i+1,i, this);
            
        }
    }
    
    //loads the battle from a .map file
    public Battle(String filename, int[] coSelect, int[] sideSelect, boolean[] altSelect, BattleOptions bopt){
        rng = new RNG();
        animation1 = new ArrayList<Animation>();
        animation2 = new ArrayList<Animation>();
        animation3 = new ArrayList<Animation>();
        animation4 = new ArrayList<Animation>();
        queue = new ArrayList<Animation>();
        diagQueue = new ArrayList<DialogueBox>();
        alts = altSelect;
        //Set the battle options
        battleOptions = bopt;
        isfog = bopt.isFog();
        ismist = bopt.isMist();
        if(battleOptions.getWeatherType()!=4){
            weather = battleOptions.getWeatherType();
            if(weather == 1)isfog = true;
        }else{
            //random weather
            weather = 0;
        }
        wduration = -1;
        wturn = -1;
        replayQ = new ReplayQueue();
        
        //clear the army list
        armies = new Army[ARMY_LIMIT];
        
        readMAPFile(filename,coSelect,sideSelect,false);
        
        //make statArmy list
        int i=0;
        while(armies[i]!=null){
            statArmies[i] = armies[i];
            i++;
            if(i==10)break;
        }
        numStatArmies = numArmies;
        
        //initialize fow map
        fog = new boolean[m.getMaxCol()][m.getMaxRow()];
        //if(isfog)calculateFoW();
        
        //Initialize the terrain style
        m.initStyle();
        
        //turns to 0 when endTurn() called at the end of this routine
        turn = -1;
        day = 1;
        
        //give each player their starting funds
        //and initiializes ai
        char c;
        for(int n = 0; n < numArmies; n++) {
            armies[n].addFunds(bopt.getStartFunds());
            armies[n].initializeTerrCost(this);
            /*c = JOptionP/ane.showInputDialog("Army: " + n + " AI CONTROLLED? Y/N?").charAt(0);
            if(c == 'Y' || c == 'y')
                armies[n].ai = new StandardAI(armies[n]);*/
        }
        
        //ends the pre-turn turn
        endTurn();
    }
    
    //loads the battle from a .map file, used by the map editor
    public Battle(String filename){
        battleOptions = new BattleOptions();
        animation1 = new ArrayList<Animation>();
        animation2 = new ArrayList<Animation>();
        animation3 = new ArrayList<Animation>();
        animation4 = new ArrayList<Animation>();
        queue = new ArrayList<Animation>();
        diagQueue = new ArrayList<DialogueBox>();
        //clear the army list
        armies = new Army[ARMY_LIMIT];
        int[] coSelect = {1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0,1,0};
        int[] sideSelect = {0,1,2,3,4,5,6,7,8,9};
        
        if(filename.substring(filename.length()-4).equals(".awd")){
            readAWDFile(filename,coSelect,sideSelect,true);
        }else{
            readMAPFile(filename,coSelect,sideSelect,true);
        }
        
        //Initialize the terrain style
        m.initStyle();
        
        //Initialize the terrain costs for all players
        for(int i = 0; i < armies.length; i++) {
            armies[i].initializeTerrCost(this);
        }
    }
    
    //reads a .map file (determines which .map type a file is and calls correct reading function)
    public void readMAPFile(String filename, int[] coSelect, int[] sideSelect, boolean mapEditorMode){
        int initial = 0;      //first int in the map, determines file type
        
        try{
            //open files
            DataInputStream read = new DataInputStream(new FileInputStream(filename));
            
            //HEADER
            initial = read.readInt();
            
            //close file
            read.close();
        }catch(IOException e){
        	logger.error("Couldn't read map file [" + filename + "]", e);
        }
        
        //read correct map type
        if(initial <= -1)readNewMAPFile(filename,coSelect,sideSelect,mapEditorMode);
        else readOldMAPFile(filename,coSelect,sideSelect,mapEditorMode);
        
        if(m.getMapName().equals("")){
            //Clean up name
            String newName = filename;
            int lastslash = newName.indexOf('\\');
            while(newName.indexOf('\\',lastslash+1)!=-1){
                lastslash = newName.indexOf('\\',lastslash+1);
            }
            newName = newName.substring(lastslash+1,newName.length()-4);
            m.setMapName(newName);
        }
    }
    
    //reads a new .map file
    public void readNewMAPFile(String filename, int[] coSelect, int[] sideSelect, boolean mapEditorMode){
        int width;      //map width
        int height;     //map height
        Army armyPtr;   //pointer to an army
        Unit uni;       //the unit being worked on
        
        try{
            //open file
            DataInputStream read = new DataInputStream(new FileInputStream(filename));
            
            //HEADER
            int header = read.readInt(); //skip version number
            String name = "";
            String author = "";
            String desc = "";
            char tempbyte;
            //read name
            while(true){
                tempbyte = (char)read.readByte();
                if(tempbyte==0)break;
                name += tempbyte;
            }
            //read author
            while(true){
                tempbyte = (char)read.readByte();
                if(tempbyte==0)break;
                author += tempbyte;
            }
            //read description
            while(true){
                tempbyte = (char)read.readByte();
                if(tempbyte==0)break;
                desc += tempbyte;
            }
        	logger.info("Selecting Map name=["+ name + "] author=["+author +"] description: "+ desc);
            
            //width, height, and number of armies
            width = read.readInt();
            height = read.readInt();
            numArmies = read.readByte();
            
            //initialize the armies
            if(mapEditorMode){
                for(int i = 0; i < numArmies; i++)read.readByte();
                numArmies = ARMY_LIMIT;
                for(int i = 0; i < numArmies; i++)
                    armies[i] = new Army(i,new Andy(), new Max(), i+1,i, this);
            }else{
                for(int i = 0; i < numArmies; i++){
                    initializeCO(i,read.readByte(),coSelect,sideSelect);
                }
            }
            
            //initialize the map
            m = new Map(width, height);
            
            //assign names to the map
            m.setMapName(name);
            m.setMapAuthor(author);
            m.setMapDescription(desc);
            
            //TERRAIN
            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    int index = read.readByte();
                    armyPtr = getColorArmy(read.readByte());
                    m.find(new Location(i,j)).setTerrain(getTerrain(index,armyPtr,m.find(new Location(i,j))));
                }
            }
            
            //UNITS
            while(true){
                int type = read.readByte();
                if(type == -10)break;
                int side = read.readByte();
                int x = read.readInt();
                int y = read.readInt();
                placeUnit(m, m.find(new Location(x,y)), type, getColorArmy(side));
            }
            //CAMPAIGN TRIGGERS
            /*
            while(header == -2){
                int type = read.readByte();
                if(type == -11)break;
                switch(type) {
                    case 0:
                        int day = read.readInt();
                        int turn = read.readInt();
                        int uType = read.readInt();
                        int x = read.readInt();
                        int y = read.readInt();
                        int army = read.readInt();
                        int HP = read.readInt();
                        int fuel = read.readInt();
                        int ammo = read.readInt();
                        getMap().getTriggers().add(new UnitTrigger(this, day, turn, x, y, uType,army, HP, fuel, ammo));
                        break;
                    case 1:
                        day = read.readInt();
                        turn = read.readInt();
                         x = read.readInt();
                         y = read.readInt();
                        int damage = read.readInt();
                        boolean deadly = read.readBoolean();
                        getMap().getTriggers().add(new DamageTrigger(this, day, turn, x, y, damage, deadly));
                        break;
                }
            }*/
        }catch(IOException e){
        	logger.error("Couldn't read NEW map file [" +filename +"]" , e);
        }
    }
    
    //reads an old .map file
    public void readOldMAPFile(String filename, int[] coSelect, int[] sideSelect, boolean mapEditorMode){
        int width;      //map width
        int height;     //map height
        Army armyPtr;   //pointer to an army
        Unit uni;       //the unit being worked on
        
        try{
            //open file
            DataInputStream read = new DataInputStream(new FileInputStream(filename));
            
            //HEADER
            width = read.readInt();
            height = read.readInt();
            numArmies = read.readInt();
            
            //initialize the armies
            if(mapEditorMode){
                for(int i = 0; i < numArmies; i++)read.readInt();
                numArmies = ARMY_LIMIT;
                for(int i = 0; i < numArmies; i++)
                    armies[i] = new Army(i,new Andy(), new Max(), i+1,i, this);
            }else{
                for(int i = 0; i < numArmies; i++){
                    initializeCO(i,read.readInt(),coSelect,sideSelect);
                }
            }
            
            //initialize the map
            m = new Map(width, height);
            
            //TERRAIN
            for(int i=0; i<width; i++){
                for(int j=0; j<height; j++){
                    int index = read.readInt();
                    armyPtr = getColorArmy(read.readInt());
                    m.find(new Location(i,j)).setTerrain(getTerrain(index,armyPtr,m.find(new Location(i,j))));
                }
            }
            
            //UNITS
            while(true){
                int type = read.readInt();
                if(type == -10)break;
                int side = read.readInt();
                int x = read.readInt();
                int y = read.readInt();
                placeUnit(m, m.find(new Location(x,y)), type, getColorArmy(side));
            }
        }catch(IOException e){
        	logger.error("Couldn't read old map file [" + filename +"]", e);
        }
    }
    
    public void readAWDFile(String filename, int[] coSelect, int[] sideSelect, boolean mapEditorMode){
        Army armyPtr;   //pointer to an army
        Unit uni;       //the unit being worked on
        
        try{
            //open file
            DataInputStream read = new DataInputStream(new FileInputStream(filename));
            
            //skip header
            read.skipBytes(11);
            
            //initialize the armies
            numArmies = ARMY_LIMIT;
            for(int i = 0; i < numArmies; i++)
                armies[i] = new Army(i,new Andy(), new Max(), i+1,i, this);
            
            //initialize the map
            m = new Map(30, 20);
            
            //TERRAIN
            for(int i=0; i<30; i++){
                for(int j=0; j<20; j++){
                    int byte1 = read.readUnsignedByte();
                    int byte2 = read.readUnsignedByte();
                    int index = byte2 * 256 + byte1;
                    m.find(new Location(i,j)).setTerrain(getAWDTerrain(index,m.find(new Location(i,j))));
                }
            }
            
            //UNITS
            for(int i=0; i<30; i++){
                for(int j=0; j<20; j++){
                    int byte1 = read.readUnsignedByte();
                    int byte2 = read.readUnsignedByte();
                    int index = byte2 * 256 + byte1;
                    if(byte2 != 255)
                        placeAWDUnit(m, m.find(new Location(i,j)), index);
                }
            }
        }catch(IOException e){
        	logger.error("Couldn't read AWD file [" + filename +"]", e);
        }
    }
    
    //calculate Fog of War coverage
    //can also now calculate Mist of War coverage
    public void calculateFoW() {
        //Calculate coverage if fog or mist is on
        if(isfog || ismist) {
            //cover entire map
            for(int i = 0; i < m.getMaxCol(); i++){
                for(int j = 0; j < m.getMaxRow(); j++){
                    fog[i][j] = true;
                    //inventions are always visible
                    if(m.find(new Location(i,j)).getTerrain() instanceof Invention)fog[i][j] = false;
                }
            }
            
            //set all enemy units to undetected
            for(int i = 0; i < numArmies; i++){
                if(armies[i].getSide()!=armies[turn].getSide()){
                    Unit[] u = armies[i].getUnits();
                    if(u!=null){
                        for(int j=0; j < u.length; j++){
                            u[j].setDetected(false);
                        }
                    }
                }
            }
            
            //fill in vision ranges
            for(int i = 0; i < numArmies; i++){
                //if the army, or allied, add vision ranges
                if(armies[i].getSide()==armies[turn].getSide()){
                    //get all the army's units
                    Unit[] u = armies[i].getUnits();
                    if(u!=null){
                        for(int j=0; j < u.length; j++){
                            if(!u[j].isInTransport()){
                                //generate each unit's vision range
                                Location loc = u[j].getLocation();
                                int x = loc.getCol();
                                int y = loc.getRow();
                                int radius = u[j].getVision();
                                //Infantry on Mountains
                                if((u[j].getUType()==0 || u[j].getUType()==1) && m.find(u[j].getLocation()).getTerrain().getIndex()==2)radius+=3;
                                int offset = 0;
                                for(int k=-1*radius; k <= radius; k++){
                                    for(int l=-1*offset; l <= offset; l++){
                                        if(m.onMap(x+l,y+k)){
                                            
                                            //spyplanes always detect units
                                            if(u[j].getUnitType()==29 && m.find(new Location(x+l,y+k)).hasUnit()){
                                                m.find(new Location(x+l,y+k)).getUnit().setDetected(true);
                                            }
                                            
                                            //wood/reef?
                                            int tind = m.find(new Location(x+l,y+k)).getTerrain().getIndex();
                                            if((tind == 1 || tind == 7) && Math.abs(l)+Math.abs(k) > 1){
                                                //fog[x+l][y+k] = true;
                                                //COs with piercing vision can see through woods/reefs
                                                if(armies[i].getCO().isPiercingVision()){
                                                    fog[x+l][y+k] = false;
                                                }
                                                //air units recieve no cover from woods/reefs
                                                if(m.find(new Location(x+l,y+k)).hasUnit() && m.find(new Location(x+l,y+k)).getUnit().getMType()==m.find(new Location(x+l,y+k)).getUnit().MOVE_AIR){
                                                    fog[x+l][y+k] = false;
                                                }
                                            }else{
                                                fog[x+l][y+k] = false;
                                            }
                                        }
                                    }
                                    if(k<0)offset++;
                                    else offset--;
                                }
                            }
                        }
                    }
                    
                    //properties
                    Property[] p = armies[i].getProperties();
                    
                    if(p!=null) {
                        for(int j=0; j < p.length; j++) {
                            //generate each property's vision range
                            Location loc = p[j].getTile().getLocation();
                            
                            Property prop = (Property)m.find(loc).getTerrain();
                            
                            int x = loc.getCol();
                            int y = loc.getRow();
                            int radius = prop.getVision();
                            int offset = 0;
                            
                            for(int k=-1*radius; k <= radius; k++) {
                                for(int l=-1*offset; l <= offset; l++) {
                                    if(m.onMap(x+l,y+k)) {
                                        fog[x+l][y+k] = false;
                                    }
                                }
                                if(k<0) {
                                    offset++;
                                } else {
                                    offset--;
                                }
                            }
                        }
                    }
                }
            }
        }else{
            //fog is off
            for(int i = 0; i < m.getMaxCol(); i++){
                for(int j = 0; j < m.getMaxRow(); j++){
                    fog[i][j] = false;
                }
            }
        }
        
        //hide any enemy units
        for(int i = 0; i < numArmies; i++){
            //if enemy, check if hidden
            if(armies[i].getSide()!=armies[turn].getSide()){
                //units
                Unit[] u = armies[i].getUnits();
                if(u!=null){
                    for(int j=0; j < u.length; j++){
                        u[j].setIfHidden();
                    }
                }
            }
        }
    }
    //A version of calculateFoW that does NOT clear the earlier paths.
public void updateFoW() {
        //Calculate coverage if fog or mist is on
        if(isfog || ismist) {
            //cover entire map
            
            //set all enemy units to undetected
            for(int i = 0; i < numArmies; i++){
                if(armies[i].getSide()!=armies[turn].getSide()){
                    Unit[] u = armies[i].getUnits();
                    if(u!=null){
                        for(int j=0; j < u.length; j++){
                            u[j].setDetected(false);
                        }
                    }
                }
            }
            
            //fill in vision ranges
            for(int i = 0; i < numArmies; i++){
                //if the army, or allied, add vision ranges
                if(armies[i].getSide()==armies[turn].getSide()){
                    //get all the army's units
                    Unit[] u = armies[i].getUnits();
                    if(u!=null){
                        for(int j=0; j < u.length; j++){
                            if(!u[j].isInTransport()){
                                //generate each unit's vision range
                                Location loc = u[j].getLocation();
                                int x = loc.getCol();
                                int y = loc.getRow();
                                int radius = u[j].getVision();
                                //Infantry on Mountains
                                if((u[j].getUType()==0 || u[j].getUType()==1) && m.find(u[j].getLocation()).getTerrain().getIndex()==2)radius+=3;
                                int offset = 0;
                                for(int k=-1*radius; k <= radius; k++){
                                    for(int l=-1*offset; l <= offset; l++){
                                        if(m.onMap(x+l,y+k)){
                                            
                                            //spyplanes always detect units
                                            if(u[j].getUnitType()==29 && m.find(new Location(x+l,y+k)).hasUnit()){
                                                m.find(new Location(x+l,y+k)).getUnit().setDetected(true);
                                            }
                                            
                                            //wood/reef?
                                            int tind = m.find(new Location(x+l,y+k)).getTerrain().getIndex();
                                            if((tind == 1 || tind == 7) && Math.abs(l)+Math.abs(k) > 1){
                                                //fog[x+l][y+k] = true;
                                                //COs with piercing vision can see through woods/reefs
                                                if(armies[i].getCO().isPiercingVision()){
                                                    fog[x+l][y+k] = false;
                                                }
                                                //air units recieve no cover from woods/reefs
                                                if(m.find(new Location(x+l,y+k)).hasUnit() && m.find(new Location(x+l,y+k)).getUnit().getMType()==m.find(new Location(x+l,y+k)).getUnit().MOVE_AIR){
                                                    fog[x+l][y+k] = false;
                                                }
                                            }else{
                                                fog[x+l][y+k] = false;
                                            }
                                        }
                                    }
                                    if(k<0)offset++;
                                    else offset--;
                                }
                            }
                        }
                    }
                    
                    //properties
                    Property[] p = armies[i].getProperties();
                    
                    if(p!=null) {
                        for(int j=0; j < p.length; j++) {
                            //generate each property's vision range
                            Location loc = p[j].getTile().getLocation();
                            
                            Property prop = (Property)m.find(loc).getTerrain();
                            
                            int x = loc.getCol();
                            int y = loc.getRow();
                            int radius = prop.getVision();
                            int offset = 0;
                            
                            for(int k=-1*radius; k <= radius; k++) {
                                for(int l=-1*offset; l <= offset; l++) {
                                    if(m.onMap(x+l,y+k)) {
                                        fog[x+l][y+k] = false;
                                    }
                                }
                                if(k<0) {
                                    offset++;
                                } else {
                                    offset--;
                                }
                            }
                        }
                    }
                }
            }
        }else{
            //fog is off
            for(int i = 0; i < m.getMaxCol(); i++){
                for(int j = 0; j < m.getMaxRow(); j++){
                    fog[i][j] = false;
                }
            }
        }
        
        //hide any enemy units
        for(int i = 0; i < numArmies; i++){
            //if enemy, check if hidden
            if(armies[i].getSide()!=armies[turn].getSide()){
                //units
                Unit[] u = armies[i].getUnits();
                if(u!=null){
                    for(int j=0; j < u.length; j++){
                        u[j].setIfHidden();
                    }
                }
            }
        }
    }
    //Clears a diamond of fog.
    public void clearFog(int radius, int x, int y){
        int offset = 0;
        for(int i=-1*radius; i <= radius; i++){
            for(int j=-1*offset; j <= offset; j++){
                if(m.onMap(x+j,y+i)){
                    if((m.find(new Location (x+j, y+i)).getTerrain().getName().equals("Wood") || m.find(new Location (x+j, y+i)).getTerrain().getName().equals("Reef"))
                            && Math.abs(i)+Math.abs(j) <= 1)
                    {
                        //If there's a wood here, only clear it if the distance is less than or equal to 1
                        fog[x+j][y+i] = false;
                    }
                    else if(!m.find(new Location (x+j, y+i)).getTerrain().getName().equals("Wood") &&
                            !m.find(new Location (x+j, y+i)).getTerrain().getName().equals("Reef"))
                    {
                        //If it's not woods or reefs, clear away!
                    fog[x+j][y+i] = false;
                    }
                }
            }
            if(i<0)offset++;
            else offset--;
        }
    }
    //clears a diamond of fog (piercing vision)
        public void clearPiercingFog(int radius, int x, int y){
        int offset = 0;
        for(int i=-1*radius; i <= radius; i++){
            for(int j=-1*offset; j <= offset; j++){
                if(m.onMap(x+j,y+i)){
                    fog[x+j][y+i] = false;
                }
            }
            if(i<0)offset++;
            else offset--;
        }
    }
    //starts a given weather
    public void startWeather(int type, int time){
        isfog = battleOptions.isFog();
        if(type!=4)weather = type;
        wduration = time;
        wturn = turn;
        if(weather == 1)isfog = true;
        calculateFoW();
    }
    
    //ends the current turn and starts the next turn, returns true if victory/defeat occurs during this phase
    public boolean endTurn(){
        boolean battleEnd = false;
        if(turn >= 0) {
            
            
            armies[turn].getCO().dayEnd(true);
            if(armies[turn].getAltCO() != null)
                armies[turn].getAltCO().dayEnd(false);
            
            for(int i = 0; i < numArmies; i++) {
                if(armies[i].getSide() != armies[turn].getSide()) {
                    armies[i].getCO().enemyDayEnd(true);
                    if(armies[i].getAltCO() != null)
                        armies[i].getAltCO().enemyDayEnd(false);
                }
            }
            //deal with CO store check
            Unit[] COstorecheck = armies[turn].getUnits();
            if(turn >= 0 && armies[turn].getCO().isCleanEnemyStoreEnd()) {
                for(int i = 0; i<numArmies; i++) {
                    if(armies[i].getSide() != armies[turn].getSide()) {
                        COstorecheck = armies[i].getUnits();
                        if(COstorecheck != null)
                            for(int t = 0; t<COstorecheck.length; t++)
                                for(int s = 0; s < 10; s++)
                                    COstorecheck[t].getEnemyCOstore()[armies[i].getCO().getStatIndex()][s] = 0;
                    }
                }
            }
        }
        
        if(++turn >= numArmies){
            turn = 0;
            day++;
            
            //check for turn limit
            if(battleOptions.getTurnLimit()>0){
                if(day > battleOptions.getTurnLimit()){
                    JOptionPane.showMessageDialog(null, "Turn Limit Exceeded!");
                    return true;
                }
            }
        }
        
        //check weather duration
        if(turn == wturn){
            if(wduration > -1){
                wduration--;
                if(wduration == 0){
                    //return to original weather
                    isfog = battleOptions.isFog();
                    ismist = battleOptions.isMist();
                    if(battleOptions.getWeatherType()!=4){
                        weather = battleOptions.getWeatherType();
                        if(weather == 1)isfog = true;
                    }else{
                        //random weather
                        weather = 0;
                    }
                    wduration = -1;
                    wturn = -1;
                }
            }
        }else if(battleOptions.getWeatherType()==4 && weather==0 && getDay() > (battleOptions.getMinWDay()) -1){
            
            //Chance of weather defined in options. Scales to army numbers - converts %/Day to %/turn
            willitrain = 100*Math.pow(  (100 - battleOptions.getRainChance())/100.0  ,  1/(double)getNumArmies()   );
            willitsnow = 100*Math.pow(  (100 - battleOptions.getSnowChance())/100.0  ,  1/(double)getNumArmies()   );
            willitsand = 100*Math.pow(  (100 - battleOptions.getSandChance())/100.0  ,  1/(double)getNumArmies()   );
            
            wselect.clear();
            if(rng.nextInt(101)>willitsnow){
                wselect.add(2);
            }
            if(rng.nextInt(101)>willitrain){
                wselect.add(1);
            }
            if(rng.nextInt(101)>willitsand){
                wselect.add(3);
            }
            if(wselect.size()>0){
                weatherget = rng.nextInt(wselect.size());
                wtimeget = battleOptions.getMinWTime() + rng.nextInt(battleOptions.getMaxWTime() - battleOptions.getMinWTime() +1);
                startWeather(wselect.get(weatherget), wtimeget);
            }
        }
        
        for(int i = 0; i < numArmies; i++)
            armies[i].setAllActive(false);
        armies[turn].setAllActive(true);
        if(day != 1)battleEnd = armies[turn].allDailyUse();
        
        Unit[] COstorecheck = armies[turn].getUnits();
        if(COstorecheck != null)
            if(armies[turn].getCO().isCleanStore())
                for(int i = 0; i < COstorecheck.length; i++)
                    for(int s = 0; s<COstorecheck[i].getCOstore().length; s++)
                        COstorecheck[i].getCOstore()[s] = 0;
        //This sets all COstore counters of the current CO to zero at the beginning of their day.
        if(turn >= 0 && armies[turn].getCO().isCleanEnemyStoreBegin()) {
            for(int i = 0; i<numArmies; i++) {
                if(armies[i].getSide() != armies[turn].getSide()) {
                    COstorecheck = armies[i].getUnits();
                    if(COstorecheck != null)
                        for(int t = 0; t<COstorecheck.length; t++)
                            for(int s = 0; s < 10; s++)
                                COstorecheck[t].getEnemyCOstore()[armies[i].getCO().getStatIndex()][s] = 0;
                }
            }
        }
        
        if(armies[turn].getCO().isCOP())
            armies[turn].getCO().deactivateCOP();
        if(armies[turn].getCO().isSCOP())
            armies[turn].getCO().deactivateSCOP();
        if(armies[turn].getTag() == 1) {
            armies[turn].getCO().activateSCOP();
            armies[turn].incrementTag();
        }else if(armies[turn].getTag() == 2) {
            armies[turn].unTag();
        }
        
        //armies[turn].charge(10); //USED BY MOOGLEGUNNER TO TEST CO POWERS DO NOT REMOVE ==========================
        //lol, uh, moved here. It works better.
        if(Options.isMusicOn()){
            Music.startMusic(COList.getIndex(armies[turn].getCO()));
        }
        //I'm just going to try this thang out
        System.gc();
        
        armies[turn].getCO().dayStart(true);
        if(armies[turn].getAltCO() != null)
            armies[turn].getAltCO().dayStart(false);
        for(int i = 0; i < numArmies; i++) {
            if(armies[i].getSide() != armies[turn].getSide()) {
                armies[i].getCO().enemyDayStart(true);
                if(armies[i].getAltCO() != null)
                    armies[i].getAltCO().enemyDayStart(false);
            }
        }
        
        //traverse property list and deal with funds
        Property[] prop = armies[turn].getProperties();
        if(prop!=null){
            for(int i = 0; i < prop.length; i++){
                armies[turn].addFunds((int)(prop[i].getIncome()*battleOptions.getFundsLevel()/1000*armies[turn].getCO().getFunding()/100));
            }
        }
        //reduces income by CO associated penalty
        armies[turn].removeFunds(armies[turn].getCO().getIncomePenalty());
        //now, again, but for repairs and supplies
        if(prop!=null){
            for(int i = 0; i < prop.length; i++){
                if(prop[i].getTile().getUnit()!=null){
                    Unit temp = prop[i].getTile().getUnit();
                    if(temp.getArmy() == prop[i].getOwner()){
                        if((temp.getMType() == temp.MOVE_AIR && prop[i].canRepairAir())||
                                ((temp.getMType() == temp.MOVE_SEA || temp.getMType() == temp.MOVE_TRANSPORT || temp.getMType() == temp.MOVE_HOVER) && prop[i].canRepairSea())||
                                (temp.getMType() != temp.MOVE_AIR && prop[i].canRepairLand())||
                                (temp.getMType() == temp.MOVE_PIPE && prop[i].canRepairPipe())){
                            for(int numHeals = armies[turn].getCO().getRepairHp(); numHeals > 0; numHeals--){
                                if(temp.getDisplayHP() != 10 && ((int)(temp.getPrice()/10*temp.getRepairMod())) <= armies[turn].getFunds() && !temp.isNoCityRepair()){
                                    temp.heal(10);
                                    armies[turn].removeFunds((int)(temp.getPrice()/10 * temp.getRepairMod()));
                                    //Only activate if they are actually repairing, and only on the first time.
                                    if(numHeals == armies[turn].getCO().getRepairHp()) {
                                        armies[turn].getCO().afterAction(temp, 21, null, true);
                                        if(armies[turn].getAltCO() != null)
                                            armies[turn].getAltCO().afterAction(temp, 21, null, false);
                                        for(int t = 0; t < numArmies; t++) {
                                            if(armies[t].getSide() != armies[turn].getSide()) {
                                                armies[t].getCO().afterEnemyAction(temp, 21, null, true);
                                                if(armies[t].getAltCO() != null)
                                                    armies[t].getAltCO().afterEnemyAction(temp, 21, null, false);
                                            }
                                        }
                                    }
                                    //
                                }
                            }
                            if(!temp.isNoCityResupply())
                                temp.resupply();
                            
                        }
                    }
                }
            }
        }
        
        
        //traverse unit list and deal with repairs and supplies
        Unit[] u = armies[turn].getUnits();
        if(u!=null){
            for(int i = 0; i < u.length; i++){
                //clear animations.
                u[i].setDirection(-1);
                //resupply adjacent if an APC
                if(u[i].getUnitType() == 9)((APC)u[i]).resupplyAdjacent();
                //Repair transported units if a carrier
                if(u[i].getUnitType() == 22){
                    Transport trans = (Transport)u[i];
                    
                    if(trans.getUnitsCarried()>0){
                        if(trans.getUnitsCarried()==2){
                            trans.getUnit(2).resupply();
                            for(int numHeals = armies[turn].getCO().getRepairHp(); numHeals > 0; numHeals--){
                                if(trans.getUnit(2).getDisplayHP() != 10 && ((int)(trans.getUnit(2).getPrice()/10*trans.getUnit(2).getRepairMod())) <= armies[turn].getFunds() && !trans.getUnit(2).isNoCityRepair()){
                                    trans.getUnit(2).heal(10);
                                    armies[turn].removeFunds((int)(trans.getUnit(2).getPrice()/10 * trans.getUnit(2).getRepairMod()));
                                    //Only activate if they are actually repairing, and only on the first time.
                                    if(numHeals == armies[turn].getCO().getRepairHp()) {
                                        armies[turn].getCO().afterAction(trans.getUnit(2), 21, null, true);
                                        if(armies[turn].getAltCO() != null)
                                            armies[turn].getAltCO().afterAction(trans.getUnit(2), 21, null, false);
                                        for(int t = 0; t < numArmies; t++) {
                                            if(armies[t].getSide() != armies[turn].getSide()) {
                                                armies[t].getCO().afterEnemyAction(trans.getUnit(2), 21, null, true);
                                                if(armies[t].getAltCO() != null)
                                                    armies[t].getAltCO().afterEnemyAction(trans.getUnit(2), 21, null, false);
                                            }
                                        }
                                    }
                                }
                            }
                        }
                        for(int numHeals = armies[turn].getCO().getRepairHp(); numHeals > 0; numHeals--){
                            if(trans.getUnit(1).getDisplayHP() != 10 && ((int)(trans.getUnit(1).getPrice()/10*trans.getUnit(1).getRepairMod())) <= armies[turn].getFunds() && !trans.getUnit(1).isNoCityRepair()){
                                trans.getUnit(1).heal(10);
                                armies[turn].removeFunds((int)(trans.getUnit(1).getPrice()/10 * trans.getUnit(1).getRepairMod()));
                                //Only activate if they are actually repairing, and only on the first time.
                                if(numHeals == armies[turn].getCO().getRepairHp()) {
                                    armies[turn].getCO().afterAction(trans.getUnit(1), 21, null, true);
                                    if(armies[turn].getAltCO() != null)
                                        armies[turn].getAltCO().afterAction(trans.getUnit(1), 21, null, false);
                                    for(int t = 0; t < numArmies; t++) {
                                        if(armies[t].getSide() != armies[turn].getSide()) {
                                            armies[t].getCO().afterEnemyAction(trans.getUnit(1), 21, null, true);
                                            if(armies[t].getAltCO() != null)
                                                armies[t].getAltCO().afterEnemyAction(trans.getUnit(1), 21, null, false);
                                        }
                                    }
                                }
                            }
                        }
                        trans.getUnit(1).resupply();
                    }
                }
            }
        }
        
        //Beginning of day, reset and recalculate
        //calculate fog of war
        calculateFoW();
        
        //check which units are visible
        /*for(int i=0; i<numArmies; i++){
            Unit[] thisArmysUnits = armies[i].getUnits();
            if(thisArmysUnits != null){
                for(int j = 0; j < thisArmysUnits.length; j++){
                    thisArmysUnits[j].setIfHidden();
                }
            }
        }*/
        
        //clears animations
        animation1.clear();
        animation2.clear();
        animation3.clear();
        animation4.clear();
        queue.clear();
        
        //deal with network games
        if(Options.isNetworkGame() && Options.getSend()){
            if(!(day == 1 && turn == 0)){
                try {
					Mission.sendMission();
				} catch (IOException e) {
					logger.error("IO Error sending mission", e);
				}
            }
        }
        
        //deal with snail games
        if(Options.snailGame && !(turn == 0 && day == 1)){
            //upload save
            Mission.saveMission(TEMPORARYSAVE_SAVE_FILENAME);
            sendFile("usave.pl", Options.gamename,TEMPORARYSAVE_SAVE_FILENAME);
            //TODO: retry?
            
            //update server information
            String reply = sendCommandToMain("nextturn", Options.gamename+"\n"+Options.username+"\n"+Options.password);
            logger.info("info = ["+ reply +"]");
            //TODO: retry?
        }
        
        //autosave if applicable
        else if(Options.isAutosaveOn())Mission.saveMission(TEMPORARYSAVE_SAVE_FILENAME);
        
        return battleEnd;
    }
    
    //try to connect to the server to see that the user's URL is correct
    public String sendCommandToMain(String command, String extra){
        String reply = "";
        try{
            URL url = new URL(Options.getServerName() + "main.pl");
            URLConnection con = url.openConnection();
            con.setDoOutput(true);
            con.setDoInput(true);
            con.setUseCaches(false);
            con.setRequestProperty("Content-type", "text/plain");
            if(extra.equals("")){
                con.setRequestProperty("Content-length", command.length()+"");
            }else{
                con.setRequestProperty("Content-length", (command.length()+1+extra.length())+"");
            }
            PrintStream out = new PrintStream(con.getOutputStream());
            out.print(command);
            if(!extra.equals("")){
                out.print("\n");
                out.print(extra);
            }
            out.flush();
            out.close();
            BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
            String s = in.readLine();
            if(s != null){
                reply += s;
                while ((s = in.readLine()) != null) {
                    reply += "\n";
                    reply += s;
                }
            }
            in.close();
        }catch(MalformedURLException e1){
        	logger.error("Bad URL "+Options.getServerName());
            JOptionPane.showMessageDialog(Mission.mainFrame,"Bad URL: "+Options.getServerName());
            return null;
        }catch(IOException e2){
        	logger.error("Connection Problem during command "+command+" with information:\n"+extra);
            JOptionPane.showMessageDialog(Mission.mainFrame,"Connection Problem during command "+command+" with the following information:\n"+extra);
            return null;
        }
        
        return reply;
    }
    
    public String sendFile(String script, String input, String file){
    	
    	
    	
        String reply = "";
        do{
            try{
                URL url = new URL(Options.getServerName() + script);
                URLConnection con = url.openConnection();
                con.setDoOutput(true);
                con.setDoInput(true);
                con.setUseCaches(false);
                con.setRequestProperty("Content-type", "text/plain");
                byte buffer[] = new byte[1];
                logger.info("opening file");
                File source = new File(ResourceLoader.properties.getProperty("saveLocation") + "/" +file);
                con.setRequestProperty("Content-length", (input.length()+1+source.length())+"");
                
                PrintStream out1 = new PrintStream(con.getOutputStream());
                out1.print(input);
                out1.print("\n");
                
                FileInputStream src = new FileInputStream(file);
                OutputStream out = con.getOutputStream();
                while(true){
                    int count = src.read(buffer);
                    if(count == -1)break;
                    out.write(buffer);
                }
                out.flush();
                out.close();
                out1.flush();
                out1.close();
                
                BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
                String s = in.readLine();
                if(s != null){
                    reply += s;
                    while ((s = in.readLine()) != null) {
                        reply += "\n";
                        reply += s;
                    }
                }
                in.close();
                
            }catch(MalformedURLException e1){
            	logger.error("Bad URL "+Options.getServerName());
                JOptionPane.showMessageDialog(Mission.mainFrame,"Bad URL: "+Options.getServerName());
                return null;
            }catch(IOException e2){
            	logger.error("Connection problem, unable to send file");
                JOptionPane.showMessageDialog(Mission.mainFrame,"Connection problem, unable to send file");
                return null;
            }
        } while(!reply.equals(String.valueOf(Mission.getCheckSum(file))));
        
        logger.info("Reply " + reply);
        
        return reply;
    }
    
    public boolean removeArmy(Army a, Army capturer, boolean capture){
        for(int i = 0; i < numArmies; i++){
            if(armies[i] == a){
                //remove units
                Unit[] u = a.getUnits();
                if(u!=null){
                    for(int j = 0; j < u.length; j++){
                        u[j].eliminateUnit();
                    }
                }
                //make properties neutral
                Property[] prop = a.getProperties();
                if(prop!=null){
                    for(int j = 0; j < prop.length; j++){
                        if(prop[j] instanceof HQ){
                            if(!capture)prop[j].getTile().setTerrain(new City(prop[j].getTile()));
                            else prop[j].getTile().setTerrain(new City(capturer,prop[j].getTile()));
                        }else{
                            if(capture)prop[j].getTile().setTerrain(getTerrain(prop[j].getIndex(),capturer,prop[j].getTile()));
                            else prop[j].getTile().setTerrain(getTerrain(prop[j].getIndex(),null,prop[j].getTile()));
                        }
                    }
                }
                //reduce number of armies
                numArmies--;
                //remove the army from list and compact the list
                for(int j = i; j < numArmies; j++){
                    armies[j] = armies[j+1];
                    armies[j].setID(j);
                }
                //end search and end turn if army is current army
                if(a.getID() == turn){
                    turn--;
                    //remove self from a snail game
                    if(Options.snailGame){
                        //find original postition if a snail game
                        int opos = 0;
                        for(int ii = 0; ii < statArmies.length; ii++){
                            if(statArmies[ii] == a){
                                opos = ii;
                            }
                        }
                        sendCommandToMain("dplay",Options.gamename + "\n" + Options.username + "\n" + Options.password + "\n" + opos);
                    }
                    endTurn();
                }else{
                    //set turn correctly
                    turn = capturer.getID();
                    //remove the player who was eliminated from a snail game
                    if(Options.snailGame){
                        int opos = 0;
                        for(int ii = 0; ii < statArmies.length; ii++){
                            if(statArmies[ii] == a){
                                opos = ii;
                            }
                        }
                        sendCommandToMain("dplay",Options.gamename + "\n" + Options.username + "\n" + Options.password + "\n" + opos);
                        //upload save
                        Mission.saveMission(TEMPORARYSAVE_SAVE_FILENAME);
                        
                        
                        sendFile("usave.pl", Options.gamename,TEMPORARYSAVE_SAVE_FILENAME);
                        //TODO: retry?
                    }
                }
            }
        }
        
        //check for game end
        if(numArmies <= 1)return true;
        //check for allied victory
        boolean sameSide = true;
        int a1side = armies[0].getSide();
        for(int j = 1; j < numArmies; j++){
            if(armies[j].getSide()!=a1side)sameSide = false;
        }
        if(sameSide)return true;
        //game not over
        return false;
    }
    
    //get a terrain of a given type and army
    private Terrain getTerrain(int type, Army selectedArmy, Tile t){
        Terrain selectedTerrain = new Plain();
        switch(type){
            case 0:
                selectedTerrain = new Plain();
                break;
            case 1:
                selectedTerrain = new Wood();
                break;
            case 2:
                selectedTerrain = new Mountain();
                break;
            case 3:
                selectedTerrain = new Road();
                break;
            case 4:
                selectedTerrain = new Bridge();
                break;
            case 5:
                selectedTerrain = new River();
                break;
            case 6:
                selectedTerrain = new Sea();
                break;
            case 7:
                selectedTerrain = new Reef();
                break;
            case 8:
                selectedTerrain = new Shoal();
                break;
            case 9:
                if(selectedArmy != null)
                    selectedTerrain = new HQ(selectedArmy,t);
                break;
            case 10:
                if(selectedArmy == null)
                    selectedTerrain = new City(t);
                else
                    selectedTerrain = new City(selectedArmy,t);
                break;
            case 11:
                if(selectedArmy == null)
                    selectedTerrain = new Base(t);
                else
                    selectedTerrain = new Base(selectedArmy,t);
                break;
            case 12:
                if(selectedArmy == null)
                    selectedTerrain = new Airport(t);
                else
                    selectedTerrain = new Airport(selectedArmy,t);
                break;
            case 13:
                if(selectedArmy == null)
                    selectedTerrain = new Port(t);
                else
                    selectedTerrain = new Port(selectedArmy,t);
                break;
            case 14:
                if(selectedArmy == null)
                    selectedTerrain = new ComTower(t);
                else
                    selectedTerrain = new ComTower(selectedArmy,t);
                break;
            case 15:
                selectedTerrain = new Pipe();
                break;
            case 16:
                selectedTerrain = new Silo();
                break;
            case 17:
                if(selectedArmy == null)
                    selectedTerrain = new Pipestation(t);
                else
                    selectedTerrain = new Pipestation(selectedArmy,t);
                break;
            case 18:
                selectedTerrain = new PipeSeam(m,t);
                break;
            case 19:
                selectedTerrain = new DestroyedPipeSeam();
                break;
            case 20:
                selectedTerrain = new SuspensionBridge();
                break;
            case TerrType.WALL:
                selectedTerrain = new Wall(m,t);
                break;
            case TerrType.DEST_WALL:
                selectedTerrain = new DestroyedWall();
                break;
            case TerrType.SEA_PIPE:
                selectedTerrain = new SeaPipe();
                break;
            case TerrType.SP_SEAM:
                selectedTerrain = new SeaPipeSeam(m, t);
                break;
            case TerrType.DEST_SPS:
                selectedTerrain = new DestroyedSeaPipeSeam();
                break;
        }
        return selectedTerrain;
    }
    
    //get a terrain of a given type and army
    private Terrain getAWDTerrain(int index, Tile t){
        Terrain selectedTerrain = new Pipe();
        switch(index){
            case 0:
                selectedTerrain = new Plain();
                break;
            case 90:
                selectedTerrain = new Wood();
                break;
            case 150:
                selectedTerrain = new Mountain();
                break;
            case 1:
                selectedTerrain = new Road();
                break;
            case 2:
            case 32:
                selectedTerrain = new Bridge();
                break;
            case 3:
                selectedTerrain = new River();
                break;
            case 60:
                selectedTerrain = new Sea();
                break;
            case 30:
                selectedTerrain = new Reef();
                break;
            case 39:
                selectedTerrain = new Shoal();
                break;
            case 300:
                selectedTerrain = new HQ(armies[0],t);
                break;
            case 310:
                selectedTerrain = new HQ(armies[1],t);
                break;
            case 320:
                selectedTerrain = new HQ(armies[2],t);
                break;
            case 330:
                selectedTerrain = new HQ(armies[3],t);
                break;
            case 340:
                selectedTerrain = new HQ(armies[4],t);
                break;
            case 301:
                selectedTerrain = new City(armies[0],t);
                break;
            case 311:
                selectedTerrain = new City(armies[1],t);
                break;
            case 321:
                selectedTerrain = new City(armies[2],t);
                break;
            case 331:
                selectedTerrain = new City(armies[3],t);
                break;
            case 341:
                selectedTerrain = new City(armies[4],t);
                break;
            case 351:
                selectedTerrain = new City(t);
                break;
            case 302:
                selectedTerrain = new Base(armies[0],t);
                break;
            case 312:
                selectedTerrain = new Base(armies[1],t);
                break;
            case 322:
                selectedTerrain = new Base(armies[2],t);
                break;
            case 332:
                selectedTerrain = new Base(armies[3],t);
                break;
            case 342:
                selectedTerrain = new Base(armies[4],t);
                break;
            case 352:
                selectedTerrain = new Base(t);
                break;
            case 303:
                selectedTerrain = new Airport(armies[0],t);
                break;
            case 313:
                selectedTerrain = new Airport(armies[1],t);
                break;
            case 323:
                selectedTerrain = new Airport(armies[2],t);
                break;
            case 333:
                selectedTerrain = new Airport(armies[3],t);
                break;
            case 343:
                selectedTerrain = new Airport(armies[4],t);
                break;
            case 353:
                selectedTerrain = new Airport(t);
                break;
            case 304:
                selectedTerrain = new Port(armies[0],t);
                break;
            case 314:
                selectedTerrain = new Port(armies[1],t);
                break;
            case 324:
                selectedTerrain = new Port(armies[2],t);
                break;
            case 334:
                selectedTerrain = new Port(armies[3],t);
                break;
            case 344:
                selectedTerrain = new Port(armies[4],t);
                break;
            case 354:
                selectedTerrain = new Port(t);
                break;
            case 305:
                selectedTerrain = new ComTower(armies[0],t);
                break;
            case 315:
                selectedTerrain = new ComTower(armies[1],t);
                break;
            case 325:
                selectedTerrain = new ComTower(armies[2],t);
                break;
            case 335:
                selectedTerrain = new ComTower(armies[3],t);
                break;
            case 345:
                selectedTerrain = new ComTower(armies[4],t);
                break;
            case 355:
                selectedTerrain = new ComTower(t);
                break;
            case 16:
                selectedTerrain = new Pipe();
                break;
            case 350:
                selectedTerrain = new Silo();
                break;
            case 226:
                selectedTerrain = new PipeSeam(m,t);
                break;
                //unsupported terrain
            case 167:
                selectedTerrain = new Plain();
                break;
            case 306:
                selectedTerrain = new HQ(armies[0],t);
                break;
            case 316:
                selectedTerrain = new HQ(armies[1],t);
                break;
            case 326:
                selectedTerrain = new HQ(armies[2],t);
                break;
            case 336:
                selectedTerrain = new HQ(armies[3],t);
                break;
            case 346:
                selectedTerrain = new HQ(armies[4],t);
                break;
        }
        return selectedTerrain;
    }
    
    //Places a new unit
    public void placeUnit(Map m, Tile t, int type, Army a){
        int x = t.getLocation().getCol();
        int y = t.getLocation().getRow();
        
        if(a != null){
            switch(type){
                case 0:
                    new Infantry(x,y,a,m);
                    break;
                case 1:
                    new Mech(x,y,a,m);
                    break;
                case 2:
                    new Tank(x,y,a,m);
                    break;
                case 3:
                    new MDTank(x,y,a,m);
                    break;
                case 4:
                    new Recon(x,y,a,m);
                    break;
                case 5:
                    new AntiAir(x,y,a,m);
                    break;
                case 6:
                    new Missiles(x,y,a,m);
                    break;
                case 7:
                    new Artillery(x,y,a,m);
                    break;
                case 8:
                    new Rockets(x,y,a,m);
                    break;
                case 9:
                    new APC(x,y,a,m);
                    break;
                case 10:
                    new Lander(x,y,a,m);
                    break;
                case 11:
                    new Cruiser(x,y,a,m);
                    break;
                case 12:
                    new Submarine(x,y,a,m);
                    break;
                case 13:
                    new Battleship(x,y,a,m);
                    break;
                case 14:
                    new TCopter(x,y,a,m);
                    break;
                case 15:
                    new BCopter(x,y,a,m);
                    break;
                case 16:
                    new Fighter(x,y,a,m);
                    break;
                case 17:
                    new Bomber(x,y,a,m);
                    break;
                case 18:
                    new Neotank(x,y,a,m);
                    break;
                case 19:
                    new MegaTank(x,y,a,m);
                    break;
                case 20:
                    new Piperunner(x,y,a,m);
                    break;
                case 21:
                    new BlackBoat(x,y,a,m);
                    break;
                case 22:
                    new Carrier(x,y,a,m);
                    break;
                case 23:
                    new Stealth(x,y,a,m);
                    break;
                case 24:
                    new BlackBomb(x,y,a,m);
                    break;
                case 25:
                    new Battlecraft(x,y,a,m);
                    break;
                case 26:
                    new Artillerycraft(x,y,a,m);
                    break;
                case 27:
                    new Shuttlerunner(x,y,a,m);
                    break;
                case 28:
                    new Zeppelin(x,y,a,m);
                    break;
                case 29:
                    new Spyplane(x,y,a,m);
                    break;
                case 30:
                    new Destroyer(x,y,a,m);
                    break;
                case 31:
                    new Oozium(x,y,a,m);
                    break;
            }
        }
    }
    
    //Places a new awd unit
    private void placeAWDUnit(Map m, Tile t, int index){
        index -= 500;
        Army a = armies[index/40];
        int type = index%40;
        
        int x = t.getLocation().getCol();
        int y = t.getLocation().getRow();
        
        if(a != null){
            switch(type){
                case 0:
                    new Infantry(x,y,a,m);
                    break;
                case 20:
                    new Mech(x,y,a,m);
                    break;
                case 21:
                    new Tank(x,y,a,m);
                    break;
                case 1:
                    new MDTank(x,y,a,m);
                    break;
                case 2:
                    new Recon(x,y,a,m);
                    break;
                case 4:
                    new AntiAir(x,y,a,m);
                    break;
                case 24:
                    new Missiles(x,y,a,m);
                    break;
                case 3:
                    new Artillery(x,y,a,m);
                    break;
                case 23:
                    new Rockets(x,y,a,m);
                    break;
                case 22:
                    new APC(x,y,a,m);
                    break;
                case 8:
                    new Lander(x,y,a,m);
                    break;
                case 27:
                    new Cruiser(x,y,a,m);
                    break;
                case 28:
                    new Submarine(x,y,a,m);
                    break;
                case 7:
                    new Battleship(x,y,a,m);
                    break;
                case 26:
                    new TCopter(x,y,a,m);
                    break;
                case 6:
                    new BCopter(x,y,a,m);
                    break;
                case 5:
                    new Fighter(x,y,a,m);
                    break;
                case 25:
                    new Bomber(x,y,a,m);
                    break;
                case 9:
                    new Neotank(x,y,a,m);
                    break;
                case 10:
                    new MegaTank(x,y,a,m);
                    break;
                case 11:
                    new Piperunner(x,y,a,m);
                    break;
                case 29:
                    new BlackBoat(x,y,a,m);
                    break;
                case 30:
                    new Carrier(x,y,a,m);
                    break;
                case 31:
                    new Stealth(x,y,a,m);
                    break;
                case 32:
                    new BlackBomb(x,y,a,m);
                    break;
                case 12:
                    new Oozium(x,y,a,m);
                    break;
            }
        }
    }
    
    //A simple CO selection routine
    public void initializeCO(int i, int color, int[] coSelect, int[] sideSelect){
        CO a,b;
        int x = coSelect[2*i];
        a = getCO(x);
        a.setAltCostume(alts[2*i]);
        x=coSelect[2*i+1];
        b = getCO(x);
        if(b!=null)
            b.setAltCostume(alts[2*i+1]);
        armies[i] = new Army(i,a, b, sideSelect[i], color, this);
    }
    
    public CO getCO(int index){
        COList.buildCOList(this);
        
        if(index == -1)
            return null;
        
        return COList.getListing()[index];
    }
    
    //returns the map
    public Map getMap(){
        return m;
    }
    
    public int getTurn(){
        return turn;
    }
    
    public int getDay(){
        return day;
    }
    
    public Army[] getArmies(){
        Army[] arm = new Army[numArmies];
        for(int i = 0; i < numArmies; i++)
            arm[i] = armies[i];
        return arm;
    }
    
    //returns a given army by color
    public Army getColorArmy(int color){
        //if(num >= ARMY_LIMIT || num < 0)return null;
        for(int i=0; i<numArmies; i++){
            if(armies[i].getColor()==color)return armies[i];
        }
        return null;
    }
    
    //returns a given army
    public Army getArmy(int num){
        if(num >= ARMY_LIMIT || num < 0)return null;
        return armies[num];
    }
    
    //returns a given stat army
    public Army getStatArmy(int num){
        if(num >= ARMY_LIMIT || num < 0)return null;
        return statArmies[num];
    }
    
    //returns the battle options
    public BattleOptions getBattleOptions(){
        return battleOptions;
    }
    
    //returns FoW at a given position
    public boolean getFog(int x, int y){
        return fog[x][y];
    }
    
    //returns the current weather
    public int getWeather(){
        return weather;
    }
    
    //returns the current number of armies
    public int getNumArmies(){
        return numArmies;
    }
    
    //returns the original number of armies
    public int getNumStatArmies(){
        return numStatArmies;
    }
    
    //is fog of war on right now?
    public boolean isFog() {
        return isfog;
    }
    
    //set fog of war on or off
    //toggling fog on will also turn mist off
    public void setFog(boolean f) {
        isfog = f;
        if(isfog)
            ismist = false;
    }
    
    public boolean isMist() {
        return ismist;
    }
    
    public void setMist(boolean m) {
        ismist = m;
        if(ismist)
            isfog = false;
    }
    
    public void resetVisibility() {
        isfog = battleOptions.isFog();
        ismist = battleOptions.isMist();
    }
    
    public void addReplayEvent(CWEvent ev){
        replayQ.push(ev);
    }
    
    public CWEvent getNextReplayEvent(){
        return replayQ.pop();
    }
    
    public ReplayQueue getReplay(){
        return replayQ;
    }
    
    public void setReplay(ReplayQueue rq){
        replayQ = rq;
    }
    
    public RNG getRNG(){
        return rng;
    }
    
    public ArrayList<Animation> getLayerOne(){
        return animation1;
    }
    public ArrayList<Animation> getLayerTwo(){
        return animation2;
    }
    public ArrayList<Animation> getLayerThree(){
        return animation3;
    }
    public ArrayList<Animation> getLayerFour(){
        return animation4;
    }
    
    public ArrayList<DialogueBox> getDialogueQueue(){
        return diagQueue;
    }
    public void emptyAnimations(){
        animation1.clear();
        animation2.clear();
        animation3.clear();
        animation4.clear();
        queue.clear();
    }
}
