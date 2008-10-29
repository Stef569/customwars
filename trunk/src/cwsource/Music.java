package cwsource;
/*
 *Music.java
 *Author: Adam Dziuk
 *Contributors:
 *Creation: July 12, 2006, 12:26 PM
 */

import java.io.*;
import java.net.*;
import org.newdawn.easyogg.*;


public class Music extends Thread{
    
    private static MusicStorage ms = new MusicStorage();
    private static OggClip eo;
    
    /** Creates a new instance of Music */
    public Music() {
    }
    
    public static void initializeMusic(){
        try {
            eo = new OggClip(new FileInputStream("music/intro.ogg"));
            eo.loop();
            
        } catch (Exception e) {
            e.printStackTrace();
            Options.turnMusicOff();
        }
    }
    
    public static void turnMusicOff(){
        eo.stop();
    }
    
    public static void startMusic(int id) {
        eo.stop();
        try{
        eo = new OggClip(new FileInputStream(ms.getMusic(id)));
        eo.loop();
                } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void startPowerMusic(int type, int style){
        eo.stop();
        try{
            eo = new OggClip(new FileInputStream(ms.getPowerMusic(type, style)));
            eo.loop();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static void stopMusic(){
        eo.stop();
    }
    
    public static void startMainMenuMusic() {
        eo.stop();
         try {
            eo = new OggClip(new FileInputStream("music/intro" + ".ogg"));
            eo.loop();
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}

