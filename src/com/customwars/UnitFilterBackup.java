package com.customwars;
/*
 * UnitFilter.java
 *
 * Created on May 31, 2007, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.image.*;

public class UnitFilterBackup extends RGBImageFilter {
    int color = 0;
    public UnitFilterBackup(int c) {
        // The filter's operation does not depend on the
        // pixel's location, so IndexColorModels can be
        // filtered directly.
        canFilterIndexColorModel = true;
        color = c;
    }
    
    public int filterRGB(int x, int y, int rgb) {
        switch(color){
            case 0:
                //Orange Star
                /*
                 *
                 */
                //Red
                return rgb;
            case 1:
                switch(rgb){
                    //Blue Moon
                    //Blue
                    case 0xFFF8B878:
                        //0xFF381818:
                        return 0xFFB8F0F8;
                    case 0xFFF89868:
                        //0xFF980038
                        return 0xFF68E0F0;
                    case 0xFFF85800:
                        //0xFFE00008
                        return 0xFF48C8F8;
                    case 0xFFF00008:
                        //0xFFF82800
                        return 0xFF0098F0;
                    case 0xFFC00000:
                        //0xFF85800
                        return 0xFF0068E8;
                    case 0xFF800010:
                        //0xFFF89870
                        return 0xFF0018A8;
                    case 0xFFC8B8A8:
                        //0xF8C880
                        return 0xFFB0B8C8;
                    case 0xFF908880:
                        return 0xFF889098;
                    case 0xFF685860:
                        return 0xFF586070;
                }
                break;
            case 2:
                switch(rgb){
                    //Green Earth
                    //Green
                    case 0xFFF8B878:
                        return 0xFFD8F8C8;
                    case 0xFFF89868:
                        return 0xFF60F848;
                    case 0xFFF85800:
                        return 0xFF30F830;
                    case 0xFFF00008:
                        return 0xFF00C010;
                    case 0xFFC00000:
                        return 0xFF009000;
                    case 0xFF800010:
                        return 0xFF003800;
                    case 0xFFC8B8A8:
                        return 0xFFB8C8B0;
                    case 0xFF908880:
                        return 0xFF98A088;
                    case 0xFF685860:
                        return 0xFF586068;
                }
                break;
            case 3:
                switch(rgb){
                    //Yellow Comet
                    //Yellow
                    case 0xFFF8B878:
                        return 0xFFF8F898;
                    case 0xFFF89868:
                        return 0xFFF8F840;
                    case 0xFFF85800:
                        return 0xFFF8C000;
                    case 0xFFF00008:
                        return 0xFFD08000;
                    case 0xFFC00000:
                        return 0xFFB88000;
                    case 0xFF800010:
                        return 0xFF504000;
                    case 0xFFC8B8A8:
                        return 0xFFC8B8A8;
                    case 0xFF908880:
                        return 0xFF908880;
                    case 0xFF685860:
                        return 0xFF686058;
                }
                break;
            case 4:
                switch(rgb){
                    //Black Hole
                    //Gray
                    case 0xFFF8B878:
                        return 0xFFD8E0D8;
                    case 0xFFF89868:
                        return 0xFFC0C0A8;
                    case 0xFFF85800:
                        return 0xFF989888;
                    case 0xFFF00008:
                        return 0xFF6038A0;
                    case 0xFFC00000:
                        return 0xFF482878;
                    case 0xFF800010:
                        return 0xFF180828;
                    case 0xFFC8B8A8:
                        return 0xFFB0B8C8;
                    case 0xFF908880:
                        return 0xFF908880;
                    case 0xFF685860:
                        return 0xFF686058;
                }
                break;
            case 5:
                switch(rgb){
                    //Jade Cosmos
                    //Dark Green
                    case 0xFFF8B878:
                        return 0xFFBBD9BD;
                    case 0xFFF89868:
                        return 0xFF89BB8C;
                    case 0xFFF85800:
                        return 0xFF5FA764;
                    case 0xFFF00008:
                        return 0xFF48824C;
                    case 0xFFC00000:
                        return 0xFF335E36;
                    case 0xFF800010:
                        return 0xFF2F4631;
                }
                break;
            case 6:
                switch(rgb){
                    //Amber Corona
                    //Orange
                    case 0xFFF8B878:
                        return 0xFFFFDEB6;
                    case 0xFFF89868:
                        return 0xFFFEC078;
                    case 0xFFF85800:
                        return 0xFFFCA339;
                    case 0xFFF00008:
                        return 0xFFE78716;
                    case 0xFFC00000:
                        return 0xFFAD5E00;
                    case 0xFF800010:
                        return 0xFF673801;
                }
                break;
            case 7:
                switch(rgb){
                    //Parallel Galaxy
                    //Dark Purple/Black
                    
                    case 0xFFF8B878:
                        return 0xFFEAA8FF;
                    case 0xFFF89868:
                        return 0xFFDD77FF;
                    case 0xFFF85800:
                        return 0xFF9438FF;
                    case 0xFFF00008:
                        return 0xFF6F00EF;
                    case 0xFFC00000:
                        return 0xFF6B0AD4;
                    case 0xFF800010:
                        return 0xFF3E067F;
      
                }
                break;
            case 8:
                switch(rgb){
                    //Copper Sun
                    //Brown
                    case 0xFFF8B878:
                        return 0xFFFFD1A8;
                    case 0xFFF89868:
                        return 0xFFDF6A00;
                    case 0xFFF85800:
                        return 0xFFBB5900;
                    case 0xFFF00008:
                        return 0xFFBD6B22;
                    case 0xFFC00000:
                        return 0xFF994800;
                    case 0xFF800010:
                        return 0xFF663100;
                }
                break;
            case 9:
                switch(rgb){
                    //Cobalt Drift
                    //Teal/Light Blue
                    case 0xFFF8B878:
                        return 0xFFC9F7EB;
                    case 0xFFF89868:
                        return 0xFF9ED4C5;
                    case 0xFFF85800:
                        return 0xFF7CC7B3;
                    case 0xFFF00008:
                        return 0xFF3EA998;
                    case 0xFFC00000:
                        return 0xFF349496;
                    case 0xFF800010:
                        return 0xFF123C52;
                }
                break;
            case 10:
                switch(rgb){
                    //Gray Sky
                    //Original Parallel Galaxy Colors
                    //Dark Gray/Black
                    case 0xFFF8B878:
                        return 0xFFC9C1A2;
                    case 0xFFF89868:
                        return 0xFFA09981;
                    case 0xFFF85800:
                        return 0xFF827B63;
                    case 0xFFF00008:
                        return 0xFF5D5849;
                    case 0xFFC00000:
                        return 0xFF343434;
                    case 0xFF800010:
                        return 0xFF000000;
                }
                break;
            case 11:
                switch(rgb){
                    //Rose Orchid
                    //Dark Red
                    case 0xFFF8B878:
                        return 0xFFE5AB9A;
                    case 0xFFF89868:
                        return 0xFFDB8884;
                    case 0xFFF85800:
                        return 0xFFD93663;
                    case 0xFFF00008:
                        return 0xFFC0305E;
                    case 0xFFC00000:
                        return 0xFF992646;
                    case 0xFF800010:
                        return 0xFF661938;
                }
                break;
            case 12:
                switch(rgb){
                    //Purple Lightning
                    //Purple
                    case 0xFFF8B878:
                        return 0xFFDAA3D8;
                    case 0xFFF89868:
                        return 0xFFD37EDF;
                    case 0xFFF85800:
                        return 0xFFBE4ACB;
                    case 0xFFF00008:
                        return 0xFF7A1AD5;
                    case 0xFFC00000:
                        return 0xFF6715AB;
                    case 0xFF800010:
                        return 0xFF380E72;
                }
                break;
            case 13:
                switch(rgb){
                    //Azure Asteroid
                    //Dark Blue
                    case 0xFFF8B878:
                        return 0xFF8080F3;
                    case 0xFFF89868:
                        return 0xFF6767F0;
                    case 0xFFF85800:
                        return 0xFF5959EC;
                    case 0xFFF00008:
                        return 0xFF4040BA;
                    case 0xFFC00000:
                        return 0xFF1212BB;
                    case 0xFF800010:
                        return 0xFF010182;
                }
                break;
            case 14:
                switch(rgb){
                    //Red Fire
                    //I dunno, lol
                    case 0xFFF8B878:
                        return 0xFFCD9696;
                    case 0xFFF89868:
                        return 0xFFBF5E5E;
                    case 0xFFF85800:
                        return 0xFF844545;
                    case 0xFFF00008:
                        return 0xFF753332;
                    case 0xFFC00000:
                        return 0xFF773232;
                    case 0xFF800010:
                        return 0xFF511d1c;
                }
                break;
            case 15:
                switch(rgb){
                    //Pink Planet
                    //Pink
                    case 0xFFF8B878:
                        return 0xFFFCA5E3;
                    case 0xFFF89868:
                        return 0xFFFF7AD6;
                    case 0xFFF85800:
                        return 0xFFF766CA;
                    case 0xFFF00008:
                        return 0xFFED36B5;
                    case 0xFFC00000:
                        return 0xFFCB2DA0;
                    case 0xFF800010:
                        return 0xFF7B3A66;
                }
                break;
            case 16:
                switch(rgb){
                    //White Nova
                    //Light Gray/White
                    case 0xFFF8B878:
                        return 0xFFEBF2F5;
                    case 0xFFF89868:
                        return 0xFFC5CCCF;
                    case 0xFFF85800:
                        return 0xFFDCE3E5;
                    case 0xFFF00008:
                        return 0xFFABB1B3;
                    case 0xFFC00000:
                        return 0xFFA3A6A8;
                    case 0xFF800010:
                        return 0xFF6E7273;
                }
                break;
            case 17:
                switch(rgb){
                    //Minty Meteor
                    //Light Green
                    case 0xFFF8B878:
                        return 0xFFD8EFB2;
                    case 0xFFF89868:
                        return 0xFFC6EB8D;
                    case 0xFFF85800:
                        return 0xFFB9E17B;
                    case 0xFFF00008:
                        return 0xFFA0D251;
                    case 0xFFC00000:
                        return 0xFF8CB344;
                    case 0xFF800010:
                        return 0xFF5E7143;
                }
                break;
            case 18:
                switch(rgb){
                    //???
                    //???
                    case 0xFFF8B878:
                        return 0xFFC8B5C2;
                    case 0xFFF89868:
                        return 0xFFBF9DBA;
                    case 0xFFF85800:
                        return 0xFFA1749A;
                    case 0xFFF00008:
                        return 0xFF885698;
                    case 0xFFC00000:
                        return 0xFF6F457A;
                    case 0xFF800010:
                        return 0xFF462E51;
                }
                break;
                
                
        }
        
        return rgb;
    }
}
