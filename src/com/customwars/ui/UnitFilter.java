package com.customwars.ui;
/*
 * UnitFilter.java
 *
 * Created on May 31, 2007, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.image.*;

public class UnitFilter extends RGBImageFilter {
    int color = 0;
    public UnitFilter(int c) {
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
                    case 0xFF381818:
                        //0xFF381818:
                        return 0xFF181840 ;
                    case 0xFF980038:
                        //0xFF980038
                        return 0xFF2820C0;
                    case 0xFFE00008:
                        //0xFFE00008
                        return 0xFF0068E8;
                    case 0xFFF82800:
                        //0xFFF82800
                        return 0xFF0098F0;
                    case 0xFFF85800:
                        //0xFFF85800
                        return 0xFF40B8F0;
                    case 0xFFF89870:
                        //0xFFF89870
                        return 0xFF68E0F8;
                    case 0xFFF8C880:
                        //0xFFF8C880
                        return 0xFFB8F0F8;
                }
                break;
            case 2:
                switch(rgb){
                    //Green Earth
                    //Green
                    
                    case 0xFF381818:
                        return 0xFF182818;
                    case 0xFF980038:
                        return 0xFF088048;
                    case 0xFFE00008:
                        return 0xFF08A830;
                    case 0xFFF82800:
                        return 0xFF10D028;
                    case 0xFFF85800:
                        return 0xFF28F028;
                    case 0xFFF89870:
                        return 0xFF88F880;
                    case 0xFFF8C880:
                        return 0xFFC8F8C0;
                }
                break;
            case 3:
                switch(rgb){
                    //Yellow Comet
                    //Yellow
                    case 0xFF381818:
                        return 0xFF302018;
                    case 0xFF980038:
                        return 0xFFB85060;
                    case 0xFFE00008:
                        return 0xFFD08000;
                    case 0xFFF82800:
                        return 0xFFE0A810;
                    case 0xFFF85800:
                        return 0xFFF0D028;
                    case 0xFFF89870:
                        return 0xFFF8F040;
                    case 0xFFF8C880:
                        return 0xFFF8F8A8;
                }
                break;
            case 4:
                switch(rgb){
                    //Black Hole
                    //Gray
                    case 0xFF381818:
                        return 0xFF281828;
                    case 0xFF980038:
                        return 0xFF502860;
                    case 0xFFE00008:
                        return 0xFF605080;
                    case 0xFFF82800:
                        return 0xFF707098 ;
                    case 0xFFF85800:
                        return 0xFF989888 ;
                    case 0xFFF89870:
                        return 0xFFC0C0A8;
                    case 0xFFF8C880:
                        return 0xFFD8E0D8 ;
                }
                break;
            case 5:
                switch(rgb){
                    //Jade Cosmos
                    //Dark Green
                    case 0xFF381818:
                        return 0xFF132813;
                    case 0xFF980038:
                        return 0xFF2E442E;
                    case 0xFFE00008:
                        return 0xFF6F8261;
                    case 0xFFF82800:
                        return 0xFF80966F;
                    case 0xFFF85800:
                        return 0xFFA3BF8D;
                    case 0xFFF89870:
                        return 0xFFBDDDA1;
                    case 0xFFF8C880:
                        return 0xFFD7F4BA;
                }
                break;
            case 6:
                switch(rgb){
                    //Amber Corona
                    //Orange
                    case 0xFF381818:
                        return 0xFF2B2117 ;
                    case 0xFF980038:
                        return 0xFF702B00 ;
                    case 0xFFE00008:
                        return 0xFF702B00 ;
                    case 0xFFF82800:
                        return 0xFFCC6C00 ;
                    case 0xFFF85800:
                        return 0xFFFF7F00 ;
                    case 0xFFF89870:
                        return 0xFFFFBA60 ;
                    case 0xFFF8C880:
                        return 0xFFFFD4AD;
                }
                break;
            case 7:
                switch(rgb){
                    //Dark Matter (formerly Parallel Galaxy)
                    //Dark Purple/Black
                    
                    case 0xFF381818:
                        return 0xFF2F2838 ;
                    case 0xFF980038:
                        return 0xFF3412A5 ;
                    case 0xFFE00008:
                        return 0xFF6720D9;
                    case 0xFFF82800:
                        return 0xFF8642F4 ;
                    case 0xFFF85800:
                        return 0xFFA95FFE ;
                    case 0xFFF89870:
                        return 0xFFE58AFC ;
                    case 0xFFF8C880:
                        return 0xFFEEABFE ;
                }
                break;
            case 8:
                switch(rgb){
                    //Copper Sun
                    //Brown
                    case 0xFF381818:
                        return 0xFF663100;
                    case 0xFF980038:
                        return 0xFF994800;
                    case 0xFFE00008:
                        return 0xFFBD6B22;
                    case 0xFFF82800:
                        return 0xFFBB5900;
                    case 0xFFF85800:
                        return 0xFFDF6A00;
                    case 0xFFF89870:
                        return 0xFFFFD1A8;
                }
                break;
            case 9:
                switch(rgb){
                    //Cobalt Drift
                    //Teal/Light Blue
                    case 0xFF381818:
                        return 0xFF123C52;
                    case 0xFF980038:
                        return 0xFF349496;
                    case 0xFFE00008:
                        return 0xFF3EA998;
                    case 0xFFF82800:
                        return 0xFF7CC7B3;
                    case 0xFFF85800:
                        return 0xFF9ED4C5;
                    case 0xFFF89870:
                        return 0xFFC9F7EB;
                }
                break;
            case 10:
                switch(rgb){
                    //Gray Sky
                    //Original Parallel Galaxy Colors
                    //Dark Gray/Black
                    case 0xFF381818:
                        return 0xFFC9C1A2;
                    case 0xFF980038:
                        return 0xFFA09981;
                    case 0xFFE00008:
                        return 0xFF827B63;
                    case 0xFFF82800:
                        return 0xFF5D5849;
                    case 0xFFF85800:
                        return 0xFF343434;
                    case 0xFFF89870:
                        return 0xFF381818;
                }
                break;
            case 11:
                switch(rgb){
                    //Rose Orchid
                    //Dark Red
                    case 0xFF381818:
                        return 0xFFE5AB9A;
                    case 0xFF980038:
                        return 0xFFDB8884;
                    case 0xFFE00008:
                        return 0xFFD93663;
                    case 0xFFF82800:
                        return 0xFFC0305E;
                    case 0xFFF85800:
                        return 0xFF992646;
                    case 0xFFF89870:
                        return 0xFF661938;
                }
                break;
            case 12:
                switch(rgb){
                    //Purple Lightning
                    //Purple
                    case 0xFF381818:
                        return 0xFFDAA3D8;
                    case 0xFF980038:
                        return 0xFFD37EDF;
                    case 0xFFE00008:
                        return 0xFFBE4ACB;
                    case 0xFFF82800:
                        return 0xFF7A1AD5;
                    case 0xFFF85800:
                        return 0xFF6715AB;
                    case 0xFFF89870:
                        return 0xFF380E72;
                }
                break;
            case 13:
                switch(rgb){
                    //Azure Asteroid
                    //Dark Blue
                    case 0xFF381818:
                        return 0xFF8080F3;
                    case 0xFF980038:
                        return 0xFF6767F0;
                    case 0xFFE00008:
                        return 0xFF5959EC;
                    case 0xFFF82800:
                        return 0xFF4040BA;
                    case 0xFFF85800:
                        return 0xFF1212BB;
                    case 0xFFF89870:
                        return 0xFF010182;
                }
                break;
            case 14:
                switch(rgb){
                    //Red Fire
                    //I dunno, lol
                    case 0xFF381818:
                        return 0xFFCD9696;
                    case 0xFF980038:
                        return 0xFFBF5E5E;
                    case 0xFFE00008:
                        return 0xFF844545;
                    case 0xFFF82800:
                        return 0xFF753332;
                    case 0xFFF85800:
                        return 0xFF773232;
                    case 0xFFF89870:
                        return 0xFF511d1c;
                }
                break;
            case 15:
                switch(rgb){
                    //Pink Planet
                    //Pink
                    case 0xFF381818:
                        return 0xFFFCA5E3;
                    case 0xFF980038:
                        return 0xFFFF7AD6;
                    case 0xFFE00008:
                        return 0xFFF766CA;
                    case 0xFFF82800:
                        return 0xFFED36B5;
                    case 0xFFF85800:
                        return 0xFFCB2DA0;
                    case 0xFFF89870:
                        return 0xFF7B3A66;
                }
                break;
            case 16:
                switch(rgb){
                    //White Nova
                    //Light Gray/White
                    case 0xFF381818:
                        return 0xFFEBF2F5;
                    case 0xFF980038:
                        return 0xFFC5CCCF;
                    case 0xFFE00008:
                        return 0xFFDCE3E5;
                    case 0xFFF82800:
                        return 0xFFABB1B3;
                    case 0xFFF85800:
                        return 0xFFA3A6A8;
                    case 0xFFF89870:
                        return 0xFF6E7273;
                }
                break;
            case 17:
                switch(rgb){
                    //Minty Meteor
                    //Light Green
                    case 0xFF381818:
                        return 0xFFD8EFB2;
                    case 0xFF980038:
                        return 0xFFC6EB8D;
                    case 0xFFE00008:
                        return 0xFFB9E17B;
                    case 0xFFF82800:
                        return 0xFFA0D251;
                    case 0xFFF85800:
                        return 0xFF8CB344;
                    case 0xFFF89870:
                        return 0xFF5E7143;
                }
                break;
            case 18:
                switch(rgb){
                    //???
                    //???
                    case 0xFF381818:
                        return 0xFFC8B5C2;
                    case 0xFF980038:
                        return 0xFFBF9DBA;
                    case 0xFFE00008:
                        return 0xFFA1749A;
                    case 0xFFF82800:
                        return 0xFF885698;
                    case 0xFFF85800:
                        return 0xFF6F457A;
                    case 0xFFF89870:
                        return 0xFF462E51;
                }
                break;
                
                
        }
        
        return rgb;
    }
}
