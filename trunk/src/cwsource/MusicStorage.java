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
    
     public static String music[] = new String[70];
     private static String powerMusic[] = new String[6];
    
    public MusicStorage() {
        music[0] = "music/andy.ogg";       
        music[1] = "music/max.ogg";
        music[2] = "music/sami.ogg";
        music[3] = "music/nell.ogg";
        music[4] = "music/hachi.ogg";
        music[5] = "music/jake.ogg"; 
        music[6] = "music/rachel.ogg";
        music[7] = "music/olaf.ogg";
        music[8] = "music/grit.ogg";    
        music[9] = "music/colin.ogg";    
        music[10] = "music/sasha.ogg";
        music[11] = "music/eagle.ogg"; 
        music[12] = "music/drake.ogg";
        music[13] = "music/jess.ogg";  
        music[14] = "music/javier.ogg";
        music[15] = "music/kanbei.ogg";
        music[16] = "music/sonja.ogg";
        music[17] = "music/sensei.ogg";
        music[18] = "music/grimm.ogg";   
        music[19] = "music/flak.ogg";         
        music[20] = "music/lash.ogg";
        music[21] = "music/adder.ogg";     
        music[22] = "music/hawke.ogg";
        music[23] = "music/sturm.ogg";
        music[24] = "music/jugger.ogg";         
        music[25] = "music/koal.ogg";        
        music[26] = "music/kindle.ogg";
        music[27] = "music/vonbolt.ogg";
        music[28] = "music/ember.ogg";       
        music[29] = "music/mina.ogg";        
        music[30] = "music/epoch.ogg"; 
        music[31] = "music/peter.ogg";
        music[32] = "music/sabaki.ogg";
        music[33] = "music/edge.ogg";
        music[34] = "music/alexis.ogg";
        music[35] = "music/alex.ogg";
        music[36] = "music/graves.ogg";
        music[37] = "music/talyx.ogg";
        music[38] = "music/yukio.ogg";
        music[39] = "music/ozzy.ogg";
        music[40] = "music/edward.ogg";
        music[41] = "music/conrad.ogg";
        music[42] = "music/eric.ogg";
        music[43] = "music/nana.ogg";
        music[44] = "music/melanthe.ogg";
        music[45] = "music/falcone.ogg";
        music[46] = "music/julia.ogg";
        music[47] = "music/zandra.ogg";
        music[48] = "music/carmen.ogg";
        music[49] = "music/carrie.ogg";
        music[50] = "music/amy.ogg";
        music[51] = "music/sophie.ogg";
        music[52] = "music/mary.ogg";
        music[53] = "music/thanatos.ogg";
        music[54] = "music/napoleon.ogg";
        music[55] = "music/aira.ogg";
        music[56] = "music/koshi.ogg";
        music[57] = "music/artemis.ogg";
        music[58] = "music/tempest.ogg";
        music[59] = "music/rattigan.ogg";
        music[60] = "music/robo_andy.ogg";
        music[61] = "music/sanjuro.ogg";
	music[62] = "music/xavier.ogg";
        music[63] = "music/adam.ogg";
        music[64] = "music/walter.ogg";
	music[65] = "music/joey.ogg";
        music[66] = "music/varlot.ogg";
        music[67] = "music/eniac.ogg";
        music[68] = "music/minamoto.ogg";
        music[69] = "music/smitan.ogg";
        
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
