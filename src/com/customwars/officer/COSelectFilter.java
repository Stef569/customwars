package com.customwars.officer;
/*
 * COSelectFilter.java
 *
 * Created on May 31, 2007, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.image.*;

public class COSelectFilter extends RGBImageFilter {
    int color = 0;
    public COSelectFilter(int c) {
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
                //Red
                return rgb;
            case 1:
                switch(rgb){
                    //Blue Moon
                    //Blue
                    case 0xFFFF2929:
                        return 0xFF4242FF;
                    case 0xFFFF8484:
                        return 0xFF8484FF;
                    case 0xFFFFC6C6:
                        return 0xFFC6C6FF;
                }
                break;
            case 2:
                switch(rgb){
                    //Green Earth
                    //Green
                    case 0xFFFF2929:
                        return 0xFF2DB91F;
                    case 0xFFFF8484:
                        return 0xFF6CD46C;
                    case 0xFFFFC6C6:
                        return 0xFFAAF6BA;
                }
                break;
            case 3:
                switch(rgb){
                    //Yellow Comet
                    //Yellow
                    case 0xFFFF2929:
                        return 0xFFADAD00;
                    case 0xFFFF8484:
                        return 0xFFD6D652;
                    case 0xFFFFC6C6:
                        return 0xFFEFEFAD;
                }
                break;
            case 4:
                switch(rgb){
                    //Black Hole
                    //Gray
                    case 0xFFFF2929:
                        return 0xFF787878;
                    case 0xFFFF8484:
                        return 0xFFA5A5A5;
                    case 0xFFFFC6C6:
                        return 0xFFD6D6D6;
                }
                break;
            case 5:
                switch(rgb){
                    //Royal Cosmos
                    //Jade
                    case 0xFFFF2929:
                        return 0xFF7DA47D;
                    case 0xFFFF8484:
                        return 0xFF99CC99;
                    case 0xFFFFC6C6:
                        return 0xFFC6E4C6;
                }
                break;
            case 6:
                switch(rgb){
                    //Amber Corona
                    //Orange
                    case 0xFFFF2929:
                        return 0xFFF2A100;
                    case 0xFFFF8484:
                        return 0xFFFFC248;
                    case 0xFFFFC6C6:
                        return 0xFFFFDE9C;
                }
                break;
            case 7:
                switch(rgb){
                    //Parallel Galaxy
                    //Indigo
                    case 0xFFFF2929:
                        return 0xFF6B0AD4;
                    case 0xFFFF8484:
                        return 0xFFA489D7;
                    case 0xFFFFC6C6:
                        return 0xFFD9C4FF;
                }
                break;
        }
        
        return rgb;
    }
}