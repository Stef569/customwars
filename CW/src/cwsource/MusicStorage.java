package cwsource;
/*
 * MusicStorage.java
 *
 * Created on August 4, 2006, 7:10 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

/**
 *
 * @author Adam Dziuk
 */
import java.io.*;
//import javax.media.*;
//import javax.media.bean.playerbean.*;
import java.net.*;

public class MusicStorage{
    
     public static String music[] = new String[COList.getListing().length+1];
     private static String powerMusic[] = new String[6];
    
    public MusicStorage() {
        for(int i = 0; i< COList.getListing().length; i++)
        {
            music[i] = "music/" + COList.getLowerCaseName(i) + ".ogg";
        }
        
        powerMusic[0] = "music/cop.ogg";
        powerMusic[1] = "music/scop.ogg";
        powerMusic[2] = "music/tagpower.ogg";
        powerMusic[3] = "music/bhcop.ogg";
        powerMusic[4] = "music/bhscop.ogg";
        powerMusic[5] = "music/bhtag.ogg";
    }
    
    public String getMusic(int id){
        System.out.println(music[id]);
        return music[id];
    }
    
    public String getPowerMusic(int type, int style){
        //type (0 = COP, 1 = SCOP, 2 = TAG)
        if(style == 4 || style == 7)
            type += 3;
        return powerMusic[type];
    }
}
