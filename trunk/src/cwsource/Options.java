package cwsource;
/*
 *Options.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: July 31, 2006
 *Holds Options info
 */

import java.io.*;
import javax.swing.JOptionPane;
import java.awt.event.*;

public class Options {
    //The Version
    public static String version = "Custom Wars Beta 24a";
    
    private static boolean balanceMode;
    private static boolean music;
    private static byte ip[] = new byte[4];
    private static int port = 55555;
    private static boolean networkGame = false;
    private static boolean send = true;
    private static int cursorIndex;
    private static boolean autosave = true;
    private static Thread listenThread;
    private static Listener listen;
    private static boolean recordReplay;
    private static String servername;
    static int defaultBans; //Default Bans 0=CW 1=AWDS 2=AW2 3=AW1 4=None
    static boolean battleBackground;
    static int mainCOID;
    
    static public boolean killedSelf = false;       //WARNING: THIS IS AN AWFUL HACK
    
    //Server Mode Scratch Variables
    static String gamename = null;
    static String masterpass = null;
    static String username = null;
    static String password = null;
    static boolean snailGame = false;
    
    //Keys
    static int up = KeyEvent.VK_UP;
    static int down = KeyEvent.VK_DOWN;
    static int left = KeyEvent.VK_LEFT;
    static int right = KeyEvent.VK_RIGHT;
    static int akey = KeyEvent.VK_Z;
    static int bkey = KeyEvent.VK_X;
    static int pgup = KeyEvent.VK_PAGE_UP;
    static int pgdn = KeyEvent.VK_PAGE_DOWN;
    static int altleft = KeyEvent.VK_COMMA;
    static int altright = KeyEvent.VK_PERIOD;
    static int menu = KeyEvent.VK_M;
    static int minimap = KeyEvent.VK_N;
    static int nextunit = KeyEvent.VK_C;
    static int constmode = KeyEvent.VK_A;
    static int delete = KeyEvent.VK_D;
    static int tkey = KeyEvent.VK_C;
    static int skey = KeyEvent.VK_S;
    static int ukey = KeyEvent.VK_V;
    static int fogkey = KeyEvent.VK_F;
    static int intelkey = KeyEvent.VK_I;
    //static int altcostkey = KeyEvent.VK_A;
    //static int cointelkey = KeyEvent.VK_C;
    
    public Options() {}
    
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
            System.out.println(e); System.exit(1);
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
    public static void saveOptions(){
        try{
            DataOutputStream write = new DataOutputStream(new FileOutputStream("options"));
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
            write.writeUTF(servername);
            write.writeInt(mainCOID);
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    public static void readOptions(){
        try{
            DataInputStream read = new DataInputStream(new FileInputStream("options"));
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
            mainCOID = read.readInt();
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
}
