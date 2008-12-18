package com.customwars;
/*
 *Options.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: July 31, 2006
 *Holds Options info
 */

import java.io.*;
import javax.swing.JOptionPane;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.customwars.state.ResourceLoader;

import java.awt.event.*;

/** Class which contains options and functions to modify those options.
 */
public class Options {
    /** The version of Custom Wars. */
    public static String version = "Custom Wars Beta 27";
    /** True if balance mode is on. */
    private static boolean balanceMode;
    /** True if music is on. */
    private static boolean music;
    /** The IP address to use for server games. */
    private static byte ip[] = new byte[4];
    /** The port to use for server games. */
    private static int port = 55555;
    /** ? */
    private static boolean networkGame = false;
	final static Logger logger = LoggerFactory.getLogger(Options.class); 
    /** ? */
    private static boolean send = true;
    /** The index of the cursor graphic selected by the user. */
    private static int cursorIndex;
    /** True if autosave is on. */
    private static boolean autosave = true;
    /** ? */
    private static Thread listenThread;
    /** ? */
    private static Listener listen;
    /** True if replays are recorded. */
    private static boolean recordReplay;
    /** The name of the server to use for server games. */
    private static String servername;
    /** Index of the set of default unit bans to use. */
    static int defaultBans; //Default Bans 0=CW 1=AWDS 2=AW2 3=AW1 4=None
    /** True if battle animations are on. */
    public static boolean battleBackground;
    /** Index of the CO to use on menu screens. */
    static int mainCOID;
    /** True if the default username and password should be used. */
    static boolean useDefaultLogin;
    /** The default username. */
    static String defaultUsername = "";
    /** The default user password. */
    static String defaultPassword = "";

    
    /** This is apparently an "AWFUL HACK?" */
    static public boolean killedSelf = false;       //WARNING: THIS IS AN AWFUL HACK
    
    //Server Mode Scratch Variables
    /** Name of the game being joined. */
    public static String gamename = null;
    /** Master password of the game being joined. */
    public static String masterpass = null;
    /** Username to use in the game being joined. */
    public static String username = null;
    /** User's password for the game being joined. */
    public static String password = null;
    /** ? */
    public static boolean snailGame = false;
    static boolean sound = true;
    //Keys
    public static int up = KeyEvent.VK_UP;
    public static int down = KeyEvent.VK_DOWN;
    public static int left = KeyEvent.VK_LEFT;
    public static int right = KeyEvent.VK_RIGHT;
    public static int akey = KeyEvent.VK_Z;
    public static int bkey = KeyEvent.VK_X;
    public static int pgup = KeyEvent.VK_PAGE_UP;
    public static int pgdn = KeyEvent.VK_PAGE_DOWN;
    public static int altleft = KeyEvent.VK_COMMA;
    public static int altright = KeyEvent.VK_PERIOD;
    public static int menu = KeyEvent.VK_M;
    public static int minimap = KeyEvent.VK_N;
    public static int nextunit = KeyEvent.VK_C;
    public static int constmode = KeyEvent.VK_A;
    public static int delete = KeyEvent.VK_D;
    public static int tkey = KeyEvent.VK_C;
    public static int skey = KeyEvent.VK_S;
    public static int ukey = KeyEvent.VK_V;
    public static int fogkey = KeyEvent.VK_F;
    public static int intelkey = KeyEvent.VK_I;
    static int fat_editor_menu = KeyEvent.VK_T;
    //static int altcostkey = KeyEvent.VK_A;
    //static int cointelkey = KeyEvent.VK_C;
    //0 = CW, 1 = AWDS, 2 = Custom
    static int selectedTerrain = 0;
    static int selectedUrban = 0;
    static int selectedHQ = 0;
    static String curTerrain = "";
    static String curUrban = "";
    static String curHQ = "";
    static String customTerrain = "Press Z To Set";
    static String customUrban = "Press Z To Set";
    static String customHQ = "Press Z To Set";
    public static boolean refresh = false;
    
    public Options() {
        String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
    	
        curTerrain = imagesLocation + "/terrain/terrain_CW.gif";
        curUrban = imagesLocation + "/terrain/urban_CW.gif";
        curHQ = imagesLocation + "/terrain/HQ_CW.gif";
    }
    
    /** Assigns default values to all options. */
    public static void InitializeOptions(){
        //rng = new RNG();
        balanceMode = true;
        recordReplay = true;
        music = false;
        ip[0] = 127;
        ip[1] = 0;
        ip[2] = 0;
        ip[3] = 1;
        cursorIndex = 0;
        servername = "http://battle.customwars.com/";
        defaultBans = 0;
        battleBackground = true;
        mainCOID = 0;
        sound = true;
        useDefaultLogin = false;
        readOptions();
        if(music == true)Music.initializeMusic();
    }
    
    public static boolean isMusicOn(){
        return music;
    }
    
    public static boolean isNetworkGame(){
        return networkGame;
    }
    
    public static void turnMusicOn(){
        Music.initializeMusic();
        music = true;
        saveOptions();
    }
    
    public static void turnMusicOff(){
        Music.turnMusicOff();
        music = false;
        saveOptions();
    }
    
    public static void turnBalanceModeOn(){
        balanceMode = true;
        //BaseDMG.loadBalanceBaseDamage();
        saveOptions();
    }
    
    public static void turnBalanceModeOff(){
        balanceMode = false;
        //BaseDMG.loadBaseDamage();
        saveOptions();
    }
    
    public static boolean isBalance(){
        return balanceMode;
    }
    
    public static void startNetwork(){
        networkGame = true;
        try{
            listen = new Listener();
            listenThread = new Thread(listen);
            listenThread.start();
        }catch(Exception e){
            logger.error("error:", e); 
            System.exit(1);
        }
    }
    
    public static void stopNetwork(){
        networkGame = false;
        listen.turnOffThread();
    }
    
    public static void setBalanceMode(boolean b){
        balanceMode = b;
    }
    
    public static void setAutosave(boolean b){
        autosave = b;
        saveOptions();
    }
    
    public static boolean isAutosaveOn(){
        return autosave;
    }
    
    public static void setRecord(boolean b){
        recordReplay = b;
        saveOptions();
    }
    
    public static boolean isRecording(){
        return recordReplay;
    }
    public static boolean isDefaultLoginOn(){
        return useDefaultLogin;
    }
    
    /*public static void setRNG(int i){
        if(i==0)rng = new RNG();
        else rng = new RNG(i);
    }*/
    
    public static void setIP(){
        boolean valid = false;
        while(!valid){
            String ipAddress = JOptionPane.showInputDialog("Type in the IP Address of the person you want to send to");
            int numPeriods = 0;
            int previousPeriod = 0;
            for(int i = 0; i < ipAddress.length(); i++){
                if(ipAddress.charAt(i) == '.'){
                    int temp = -1;
                    try{
                        temp = Integer.parseInt(ipAddress.substring(previousPeriod,i));
                    }catch(NumberFormatException exc){
                        break;
                    }
                    if(temp < 0 || temp > 255)break;
                    ip[numPeriods] = (byte)temp;
                    previousPeriod = i+1;
                    numPeriods++;
                    if(numPeriods > 3)break;
                }
            }
            if(numPeriods==3){
                int temp = -1;
                try{
                    temp = Integer.parseInt(ipAddress.substring(previousPeriod));
                }catch(NumberFormatException exc){
                    continue;
                }
                if(temp < 0 || temp > 255){
                    continue;
                }else{
                    ip[numPeriods] = (byte)temp;
                    valid = true;
                }
            }
        }
        
        //get port #
        String portNumber = JOptionPane.showInputDialog("Type in the port you want to use");
        int temp = 55555;
        try{
            temp = Integer.parseInt(portNumber);
        }catch(NumberFormatException exc){
            port = 55555;
            return;
        }
        port = temp;
    }
    
    public static byte[] getIP(){
        return ip;
    }
    
    public static int getPort(){
        return port;
    }
    
    public static String getDisplayIP(){
        int ip0 = ip[0];
        int ip1 = ip[1];
        int ip2 = ip[2];
        int ip3 = ip[3];
        if(ip0 < 0)ip0 += 256;
        if(ip1 < 0)ip1 += 256;
        if(ip2 < 0)ip2 += 256;
        if(ip3 < 0)ip3 += 256;
        return "" + ip0 + "." + ip1 + "." + ip2 + "." + ip3 + ":" + port;
    }
    
    public static int getCursorIndex(){
        return cursorIndex;
    }
    
    public static void incrementCursor(){
        cursorIndex++;
        if(cursorIndex > 13)cursorIndex=0;
        saveOptions();
    }
    
    public static void decrementCursor(){
        cursorIndex--;
        if(cursorIndex < 0)cursorIndex=13;
        saveOptions();
    }
    public static int getMainCOID(){
        return mainCOID;
    }
    public static void incrementCO(){
        if(mainCOID<69)
            mainCOID++;
        else
            mainCOID = 0;
        saveOptions();
    }
    public static void decrementCO(){
        if(mainCOID>0)
            mainCOID--;
        else
            mainCOID = 69;
        saveOptions();
    }
    public static void toggleRefresh(){
        refresh = !refresh;
        saveOptions();
    }
    
    public static void saveOptions(){
        try{
        	
            DataOutputStream write = new DataOutputStream(new FileOutputStream(ResourceLoader.properties.getProperty("optionsLocation")));
            write.writeBoolean(music);
            write.writeBoolean(balanceMode);
            write.writeInt(cursorIndex);
            write.writeBoolean(autosave);
            write.writeBoolean(recordReplay);
            write.writeInt(defaultBans);
            write.writeBoolean(battleBackground);
            write.writeInt(up);
            write.writeInt(down);
            write.writeInt(left);
            write.writeInt(right);
            write.writeInt(akey);
            write.writeInt(bkey);
            write.writeInt(pgup);
            write.writeInt(pgdn);
            write.writeInt(altleft);
            write.writeInt(altright);
            write.writeInt(menu);
            write.writeInt(minimap);
            write.writeInt(nextunit);
            write.writeInt(constmode);
            write.writeInt(delete);
            write.writeInt(tkey);
            write.writeInt(skey);
            write.writeInt(ukey);
            write.writeInt(fogkey);
            write.writeInt(intelkey);
            //write.writeInt(fat_editor_menu);
            write.writeUTF(servername);
            write.writeBoolean(useDefaultLogin);
            write.writeUTF(defaultUsername);
            write.writeUTF(defaultPassword);
            write.writeInt(mainCOID);
            write.writeBoolean(SFX.getMute());
            write.writeInt(selectedTerrain);
            write.writeInt(selectedUrban);
            write.writeInt(selectedHQ);
            write.writeUTF(customTerrain);
            write.writeUTF(customUrban);
            write.writeUTF(customHQ);
            write.writeBoolean(refresh);
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    public static void readOptions(){
        try{
            DataInputStream read = new DataInputStream(new FileInputStream(ResourceLoader.properties.getProperty("optionsLocation")));
            music = read.readBoolean();
            balanceMode = read.readBoolean();
            cursorIndex = read.readInt();
            autosave = read.readBoolean();
            recordReplay = read.readBoolean();
            defaultBans = read.readInt();
            battleBackground = read.readBoolean();
            up = read.readInt();
            down = read.readInt();
            left = read.readInt();
            right = read.readInt();
            akey = read.readInt();
            bkey = read.readInt();
            pgup = read.readInt();
            pgdn = read.readInt();
            altleft = read.readInt();
            altright = read.readInt();
            menu = read.readInt();
            minimap = read.readInt();
            nextunit = read.readInt();
            constmode = read.readInt();
            delete = read.readInt();
            tkey = read.readInt();
            skey = read.readInt();
            ukey = read.readInt();
            fogkey = read.readInt();
            intelkey = read.readInt();
            servername = read.readUTF();
            useDefaultLogin = read.readBoolean();
            defaultUsername = read.readUTF();
            defaultPassword = read.readUTF();
            mainCOID = read.readInt();
            SFX.setMute(read.readBoolean());
            selectedTerrain = read.readInt();
            selectedUrban = read.readInt();
            selectedHQ = read.readInt();
            customTerrain = read.readUTF();
            customUrban = read.readUTF();
            customHQ = read.readUTF();
            refresh = read.readBoolean();
            resetSpriteStrings();
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    public static boolean getSend(){
        return send;
    }
    
    public static void toggleSend(){
        if(send)send = false;
        else send = true;
    }
    
    public static void setServer(){
        String temp = JOptionPane.showInputDialog("Type in the URL of the server you want to manage your snail games\nShould be in the format http://www.domain.com/subdirs/",servername);
        if(temp != null){
            if(temp.charAt(temp.length()-1)!='/')temp += "/";
            servername = temp;
        }
        saveOptions();
    }
    
    public static String getServerName(){
        return servername;
    }
    
    public static int getDefaultBans(){
        return defaultBans;
    }
    
    public static void incrementDefaultBans(){
        defaultBans++;
        if(defaultBans > 5)defaultBans=0;
        saveOptions();
    }
    
    public static void toggleBattleBackground(){
        if(battleBackground)battleBackground = false;
        else battleBackground = true;
        saveOptions();
    }
    public static void toggleDefaultLogin(){
        if(useDefaultLogin)useDefaultLogin = false;
        else useDefaultLogin = true;
        saveOptions();
    }

    public static void setDefaultLogin(){
        String temp = JOptionPane.showInputDialog("Type in the default user name you wish to use in server games (12 characters max).", defaultUsername);
        if(temp != null && temp.length() > 0 && temp.length() < 13){
            defaultUsername = temp;
        }
        
        temp = JOptionPane.showInputDialog("Type in the default password you wish to use in server games.", defaultPassword);
        if(temp != null){
            defaultPassword = temp;
        }
        
        saveOptions();
    }
   
    public static String getDefaultUsername(){
        return defaultUsername;
    }
    public static String getDefaultPassword(){
        return defaultPassword;
    }        
    
    public static int getSelectedTerrain(){
        return selectedTerrain;
    }
    public static String getCustomTerrainString(){
        return customTerrain;
    }
    public static int getSelectedUrban(){
        return selectedUrban;
    }
    public static String getCustomUrbanString(){
        return customUrban;
    }
    public static int getSelectedHQ(){
        return selectedHQ;
    }
    public static String getCustomHQString(){
        return customHQ;
    }
    public static void incrementTerrain(){
        if(selectedTerrain+1 <3)
            selectedTerrain++;
        else
            selectedTerrain = 0;
        saveOptions();
        resetSpriteStrings();
    }
    public static void decrementTerrain(){
        if(selectedTerrain > 0)
            selectedTerrain--;
        else
            selectedTerrain = 2;
        saveOptions();
        resetSpriteStrings();
    }
    public static void incrementUrban(){
        if(selectedUrban+1 <3)
            selectedUrban++;
        else
            selectedUrban = 0;
        saveOptions();
        resetSpriteStrings();
    }
    public static void decrementUrban(){
        if(selectedUrban > 0)
            selectedUrban--;
        else
            selectedUrban = 2;
        saveOptions();
        resetSpriteStrings();
    }
    public static void incrementHQ(){
        if(selectedHQ+1 <3)
            selectedHQ++;
        else
            selectedHQ = 0;
        saveOptions();
        resetSpriteStrings();
    }
    public static void decrementHQ(){
        if(selectedHQ > 0)
            selectedHQ--;
        else
            selectedHQ = 2;
        saveOptions();
        resetSpriteStrings();
    }
    public static void setCustomTerrain(){
        customTerrain = JOptionPane.showInputDialog("Type in the name of the custom terrain file you are using (omit the .gif at the end):","Press Z to Set");
        if(customTerrain.equals(null))
            customTerrain = "Press Z to Set";
        saveOptions();
        resetSpriteStrings();
    }
    public static void setCustomUrban(){
        customUrban = JOptionPane.showInputDialog("Type in the name of the custom urban file you are using (omit the .gif at the end):","Press Z to Set");
        if(customUrban.equals(null))
            customUrban = "Press Z to Set";
        saveOptions();
        resetSpriteStrings();
    }
    public static void setCustomHQ(){
        customHQ = JOptionPane.showInputDialog("Type in the name of the custom HQ file you are using (omit the .gif at the end):","Press Z to Set");
        if(customHQ.equals(null))
            customHQ = "Press Z to Set";
        saveOptions();
        resetSpriteStrings();
    }
    public static boolean getRefresh(){
        return refresh;
    }
    public static void resetSpriteStrings(){
        String imagesLocation = ResourceLoader.properties.getProperty("imagesLocation");
        
        
            switch(selectedTerrain) {
                case 0:
                    curTerrain = imagesLocation + "/terrain/terrain_CW.gif";
                    break;
                case 1:
                    curTerrain = imagesLocation + "/terrain/terrain_AWDS.gif";
                    break;
                case 2:
                    curTerrain = imagesLocation + "/terrain/" + customTerrain + ".gif";
            }
            switch(selectedUrban) {
                case 0:
                    curUrban = imagesLocation + "/terrain/urban_CW.gif";
                    break;
                case 1:
                    curUrban = imagesLocation + "/terrain/urban_AWDS.gif";
                    break;
                case 2:
                    curUrban = imagesLocation + "/terrain/" + customUrban + ".gif";
            }
            switch(selectedHQ) {
                case 0:
                    curHQ = imagesLocation + "/terrain/HQ_CW.gif";
                    break;
                case 1:
                    curHQ = imagesLocation + "/terrain/HQ_AWDS.gif";
                    break;
                case 2:
                    curHQ = imagesLocation + "/terrain/" + customHQ + ".gif";
            }
    }
}
