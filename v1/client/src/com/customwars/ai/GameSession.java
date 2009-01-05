package com.customwars.ai;
/*
 *Mission.java
 *Author: Urusan
 *Contributors: Adam Dziuk
 *Creation: July 14, 2006, 5:04 AM
 *The mission. The highest class in any CW mission.
 *Since the 2 battle classes are static methods of this class,
 *all mission information is available from this class to any part of the program.
 *Use this capability sparingly, or the code will become terribly tangled.
 */

/*import com.thoughtworks.xstream.*;
import com.thoughtworks.xstream.io.xml.DomDriver;*/
import java.io.*;
import java.net.*;

import javax.swing.*;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.officer.COList;
import com.customwars.sfx.Music;
import com.customwars.state.ReplayQueue;
import com.customwars.state.ResourceLoader;
import com.customwars.ui.BattleScreen;

import java.util.zip.CRC32;
//import java.util.*;

public class GameSession {
    private static final String REPLAY_SAVE_FILENAME = "/replay.save";

	private static final String MISSION_TEMP_SAVE_FILENAME = "temporarysave.save";
    
	private static Battle battle1;   //the primary battle
    private static Battle battle2;   //a second battle for 2-screen games
    private static BattleScreen screen1; //The screen displaying the battle1
    private static BattleScreen screen2; //The screen displaying the battle2
    private static Battle initialState;   //the initial state of the battle, used for replays
   
    public static JFrame mainFrame;
   
    public  static Socket socket;
    public  static ServerSocket sServer;
    public  static String name;
    public  static int saveAttempts = 0;
	final static Logger logger = LoggerFactory.getLogger(GameSession.class);  
	
    //constructor
    public GameSession() {
    }
   
    //Starts a 1-screen mission
    public static void startMission(Battle b, BattleScreen bs){
        battle1 = b;
        battle2 = null;
        screen1 = bs;
        screen2 = null;
    }
   
    //Starts a 2-screen mission
    public static void startMission(Battle b,  BattleScreen bs, Battle b2, BattleScreen bs2){
        battle1 = b;
        battle2 = b2;
        screen1 = bs;
        screen2 = bs2;
    }
   
    //Ends the mission
    public static void endMission(){
        battle1 = null;
        battle2 = null;
        screen1 = null;
        screen2 = null;
        if(Options.isNetworkGame())Options.stopNetwork();
    }
   
    //saves the initial state to memory
    public static void saveInitialState(){
        String saveLocation = ResourceLoader.properties.getProperty("saveLocation");
        logger.info("Saving initial state file [" + saveLocation + REPLAY_SAVE_FILENAME+"]");
        
        if(battle1 != null){
            try{
                ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(saveLocation + REPLAY_SAVE_FILENAME));
                write.writeObject(battle1);
                ObjectInputStream read = new ObjectInputStream(new FileInputStream(saveLocation + REPLAY_SAVE_FILENAME));
                initialState = (Battle)read.readObject();
            }catch(IOException e){
                logger.error("Error in Reading / Writing state", e);
            }catch(ClassNotFoundException e){
            	logger.error("Error in Reading / Writing state", e);
            }
        }
    }
   /*public static void saveMission(String filename){
       XStream xstream = new XStream(new DomDriver());
       xstream.alias("army", Army.class);
       xstream.alias("unit", Unit.class);
       xstream.alias("terrain", Terrain.class);
       xstream.alias("CO", CO.class);
       xstream.alias("tile", Tile.class);
       xstream.alias("map", Map.class);
       xstream.alias("battle", Battle.class);
       xstream.alias("battleoption", BattleOptions.class);
    try{
        ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(filename));
        xstream.toXML(battle1,write);
        }catch(IOException e){
        }
   }*/
    //saves the mission to file
   
    public static void saveMission(String filename){
    	String saveLocation = ResourceLoader.properties.getProperty("saveLocation");
    	logger.info("Saving file ["   + saveLocation +  "/" +  filename +"]");
        try{
        	
            ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(saveLocation + "/" +filename));
            if(battle1 != null){
                //type of replay record
                if(battle1.getBattleOptions().isRecording())write.writeInt(1);
                else write.writeInt(0);
                //save itself
                write.writeObject(battle1);
                //initial state (if type 1)
                if(battle1.getBattleOptions().isRecording())write.writeObject(initialState);
                if(!testSave(filename)){
                    saveAttempts++;
                    logger.error("Saving Error: " + saveAttempts);
                    //prevent from becoming potentially infinite
                    if(saveAttempts > 5){
                        logger.error("Unable to save!");
                        JOptionPane.showMessageDialog(mainFrame,"ERROR: Could not save after 5 attempts!");
                        return;
                    }
                    logger.info("Attempting to Resave");
                    saveMission(filename);
                    return;
                }
            }
        }catch(IOException e){
        	logger.error("Error saving Mission",e);
        }
    }
   
    public static boolean testSave(String filename){
    	
    	String saveLocation = ResourceLoader.properties.getProperty("saveLocation");
    	logger.debug("Testsaving file ["   + saveLocation +  "/" + filename+"]");

    	try{
            ObjectInputStream read = new ObjectInputStream(new FileInputStream(saveLocation + "/"+filename));
            int stype = read.readInt();
            Battle battlet = new Battle(battle1.getMap());
            battlet = (Battle) read.readObject();
            if(stype == 1){
                initialState = (Battle) read.readObject();
            }
            logger.info("Save Success!");
            return true;
        } catch (Exception e){
        	logger.error("SAVING ERROR: " + e);
            e.printStackTrace();
            return false;
        }
    }
   
    //saves a replay, which includes a save of the initial state, and a list of the actions that occured
    public static void saveReplay(String filename){
    	String saveLocation = ResourceLoader.properties.getProperty("saveLocation");
    	logger.debug("Saving Replay file ["+filename+"]");
        try{
            ObjectOutputStream write = new ObjectOutputStream(new FileOutputStream(filename));
            //version number
            write.writeInt(1);
            //save file
            if(initialState != null){
                write.writeObject(initialState);
            }
            //replay queue
            write.writeObject(battle1.getReplay());
        }catch(IOException e){
            logger.error("Error saving Replay= [" + filename + "] to location=[" + saveLocation +"]" ,e);
        }
    }
   
    //loads a mission from file
    public static void loadReplay(String filename){
    	String saveLocation = ResourceLoader.properties.getProperty("saveLocation");
    	logger.debug("Loading replay file ["+filename+"]");
        ReplayQueue rq = new ReplayQueue();
        try{
            ObjectInputStream read = new ObjectInputStream(new FileInputStream(filename));
            //determine version
            read.readInt();
            //read save
            battle1 = (Battle) read.readObject();
            //read replay queue
            rq = (ReplayQueue) read.readObject();
        }catch(IOException e){
        	logger.error("Problem with file throwing stack trace: "+ e );
            logger.error("Error loading Replay["+filename + "] from location=["+saveLocation +"]",e);
        }catch(ClassNotFoundException e){
            logger.error("error",e);
            logger.error("Error loading Replay["+filename + "] from location=["+saveLocation +"]",e);
        }
       
        battle1.setReplay(rq);
       
        screen1.resetBattle(battle1);
        if(screen2 != null)screen2.resetBattle(battle2);
       
        if(Options.isMusicOn()){
            Music.stopMusic();
            Music.startMusic(COList.getIndex(battle1.getArmy(battle1.getTurn()).getCO()));
        }
       
        screen1.startReplay();
    }
   
    //sends the mission to the IP Address
    public static void sendMission() throws IOException{

            logger.info("1");
            InetAddress lol = null;
            
			try {
				lol = InetAddress.getByAddress(Options.getIP());
			} catch (UnknownHostException e) {
				logger.error("Could not find host! ", e);
			}
			
            logger.info("2");
            
			try {           
				socket = new Socket(lol, Options.getPort());
			} catch (NullPointerException e) {
				logger.error("Host not found so couldn't connect to socket", e);
			}            
            
            logger.info("3");
            PrintWriter out = new PrintWriter(socket.getOutputStream(), true);
            logger.info("4");
            //out.print(name.length());
            logger.info("5");
            //out.print(name);
            logger.info("6");
            ObjectOutputStream write = new ObjectOutputStream(socket.getOutputStream());
            logger.info("7");
            if(battle1 != null){
                logger.info("8");
                write.writeObject(battle1);
                write.writeObject(initialState);
            }
            logger.info("9");
            

    }
   
    //gets the mission
    public static void recieveMission(InputStream in){
    
    logger.debug("recieving Mission file");	
        try{
            ObjectInputStream read = new ObjectInputStream(in);
            battle1 = (Battle) read.readObject();
            initialState = (Battle) read.readObject();
        }catch(IOException e){
            logger.error("Error recieving mission",e);
        }catch(ClassNotFoundException e){
        	logger.error("Error recieving mission",e);
        }
       
        screen1.resetBattle(battle1);
       
        if(screen2 != null)screen2.resetBattle(battle2);
       
        if(Options.isMusicOn()){
            Music.stopMusic();
            Music.startMusic(COList.getIndex(battle1.getArmy(battle1.getTurn()).getCO()));
        }
       
        //autosave after recieving correctly
        if(Options.isAutosaveOn())saveMission(MISSION_TEMP_SAVE_FILENAME);
        
        logger.debug("Just recieved Mission");
    }
   
    //loads a mission from file
    public static void loadMission(String filename){
    	//String saveLocation = ResourceLoader.properties.getProperty("saveLocation");
    	/*
    	 * TODO: Check the above line to see if we need it or if we can use just the
    	 * filename passed in.
    	 */
    	logger.debug("Loading mission file ["+filename+"]");

    	try{
            ObjectInputStream read = new ObjectInputStream(new FileInputStream(filename));
            int stype = read.readInt();
            battle1 = (Battle) read.readObject();
            if(stype == 1){
                initialState = (Battle) read.readObject();
            }
        }catch(IOException e){
            logger.error("Problem loading the mission file: ["+filename+ "]",e);
        }catch(ClassNotFoundException e){
            logger.error("Problem loading the mission file: ["+filename+ "]",e);
        }
       
        screen1.resetBattle(battle1);
        if(screen2 != null)screen2.resetBattle(battle2);
       
        if(Options.isMusicOn()){
            Music.stopMusic();
            Music.startMusic(COList.getIndex(battle1.getArmy(battle1.getTurn()).getCO()));
        }
    }
   
    //returns Battle 1
    public static Battle getBattle(){
        return battle1;
    }
   
    //returns Battle 2
    public static Battle getBattle2(){
        return battle2;
    }
    
    public static BattleScreen getBattleScreen(){
        return screen1;
    }
    public static long getCheckSum(String file){
        CRC32 checksum = new CRC32();
        FileInputStream in;
        try{
            in = new FileInputStream(file);

            ByteArrayOutputStream out = new ByteArrayOutputStream(1024);
            byte[] buffer = new byte[1024];
            int len;
            
            while((len = in.read(buffer)) >= 0)
                out.write(buffer, 0, len);
            
            in.close();
            out.close();
            
            checksum.update(out.toByteArray());
        }catch(IOException e){
            logger.error("Problem with CheckSum on file=[" +file+"]",e);
        }
        return checksum.getValue();
        
    }
}