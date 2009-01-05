package com.customwars.map.location;
/*
 * PropFilter.java
 *
 * Created on May 31, 2007, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.image.*;

public class PropFilter extends RGBImageFilter {
    int color = 0;
    public PropFilter(int c) {
        // The filter's operation does not depend on the
        // pixel's location, so IndexColorModels can be
        // filtered directly.
        canFilterIndexColorModel = true;
        color = c;
    }
    
    public int filterRGB(int x, int y, int rgb) {
        switch(color){
            case 0:
                //Neutral
                return rgb;
            case 1:
                switch(rgb){
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                    case 0xFF9888C8:
                        return 0xFFB84078;
                    case 0xFFC0C0C8:
                        return 0xFFE05038;
                    case 0xFFF0E8D0:
                        return 0xFFF8D088;
                    case 0xFFF8F8F0 :
                        return 0xFFF8F8F8;
                }
            case 2:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Blue Moon
                //Blue
                    case 0xFF9888C8:
                        return 0xFF6848E0;
                    case 0xFFC0C0C8 :
                        return 0xFF7870F8;
                    case 0xFFF0E8D0:
                        return 0xFF88D0F8;
                    case 0xFFF8F8F0 :
                        return 0xFFB8F8F8;
                }
                break;
            case 3:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Green Earth
                //Green
                    case 0xFF9888C8:
                        return 0xFF40A068;
                    case 0xFFC0C0C8 :
                        return 0xFF50C858;
                    case 0xFFF0E8D0:
                        return 0xFF80E878;
                    case 0xFFF8F8F0 :
                        return 0xFFA8F8A8;
                }
                break;
            case 4:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Yellow Comet
                //Yellow
                    case 0xFF9888C8:
                        return 0xFFC88040;
                    case 0xFFC0C0C8 :
                        return 0xFFE0B028;
                    case 0xFFF0E8D0:
                        return 0xFFF8E030;
                    case 0xFFF8F8F0 :
                        return 0xFFF8F860;
                }
                break;
            case 5:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Black Hole
                //Gray
                    case 0xFF9888C8:
                        return 0xFF704C7F;
                    case 0xFFC0C0C8 :
                        return 0xFF7A6B99;
                    case 0xFFF0E8D0:
                        return 0xFFC8C0B8;
                    case 0xFFF8F8F0 :
                        return 0xFFE8E0D0;
                }
                break;
            case 6:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Jade Cosmos
                //Dark Green
                    case 0xFF9888C8:
                        return 0xFF567556;
                    case 0xFFC0C0C8 :
                        return 0xFF78966B;
                    case 0xFFF0E8D0:
                        return 0xFFA9CC8A;
                    case 0xFFF8F8F0 :
                        return 0xFFD1EFB4;
                }
                break;
            case 7:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Amber Corona
                //Orange
                    case 0xFF9888C8:
                        return 0xFF9D5A35;
                    case 0xFFC0C0C8 :
                        return 0xFFC87446;
                    case 0xFFF0E8D0:
                        return 0xFFF49B64;
                    case 0xFFF8F8F0 :
                        return 0xFFFFC298;
                }
                break;
            case 8:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Dark Matter
                //Dark Purple/Black
                    case 0xFF9888C8:
                        return 0xFF6A2AAA;
                    case 0xFFC0C0C8 :
                        return 0xFF9137F2;
                    case 0xFFF0E8D0:
                        return 0xFFCC99FF;
                    case 0xFFF8F8F0 :
                        return 0xFFDBB8FF;
                }
                break;
            case 9:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Copper Sun
                //Brown
                    case 0xFF9888C8:
                        return 0xFF8D5D2C;
                    case 0xFFC0C0C8 :
                        return 0xFFAD753A;
                    case 0xFFF0E8D0:
                        return 0xFFDBB48B;
                    case 0xFFF8F8F0 :
                        return 0xFFE3C9AD;
                }
                break;
            case 10:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Cobalt Drift
                //Teal/Light Blue
                    case 0xFF9888C8:
                        return 0xFF497569;
                    case 0xFFC0C0C8 :
                        return 0xFF579B89;
                    case 0xFFF0E8D0:
                        return 0xFF83D6C0;
                    case 0xFFF8F8F0 :
                        return 0xFFBCFBE9;
                }
                break;
            case 11:
                switch(rgb){                    
                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Gray Sky
                //Original Parallel Galaxy Colors
                //Dark Gray/Black
                    case 0xFF9888C8:
                        return 0xFFC9C1A2;
                    case 0xFFC0C0C8 :
                        return 0xFFA09981;
                    case 0xFFF0E8D0:
                        return 0xFF827B63;
                    case 0xFFF8F8F0 :
                        return 0xFF5D5849;
                    case 0xFFC00000:
                        return 0xFF343434;
                    case 0xFF800010:
                        return 0xFF000000;
                }
                break;
            case 12:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Rose Orchid
                //Dark Red
                    case 0xFF9888C8:
                        return 0xFFE5AB9A;
                    case 0xFFC0C0C8 :
                        return 0xFFDB8884;
                    case 0xFFF0E8D0:
                        return 0xFFD93663;
                    case 0xFFF8F8F0 :
                        return 0xFFC0305E;
                    case 0xFFC00000:
                        return 0xFF992646;
                    case 0xFF800010:
                        return 0xFF661938;
                }
                break;
            case 13:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Purple Lightning
                //Purple
                    case 0xFF9888C8:
                        return 0xFFDAA3D8;
                    case 0xFFC0C0C8 :
                        return 0xFFD37EDF;
                    case 0xFFF0E8D0:
                        return 0xFFBE4ACB;
                    case 0xFFF8F8F0 :
                        return 0xFF7A1AD5;
                    case 0xFFC00000:
                        return 0xFF6715AB;
                    case 0xFF800010:
                        return 0xFF380E72;
                }
                break;
            case 14:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Azure Asteroid
                //Dark Blue
                    case 0xFF9888C8:
                        return 0xFF8080F3;
                    case 0xFFC0C0C8 :
                        return 0xFF6767F0;
                    case 0xFFF0E8D0:
                        return 0xFF5959EC;
                    case 0xFFF8F8F0 :
                        return 0xFF4040BA;
                    case 0xFFC00000:
                        return 0xFF1212BB;
                    case 0xFF800010:
                        return 0xFF010182;
                }
                break;
            case 15:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Red Fire
                //I dunno, lol
                    case 0xFF9888C8:
                        return 0xFFCD9696;
                    case 0xFFC0C0C8 :
                        return 0xFFBF5E5E;
                    case 0xFFF0E8D0:
                        return 0xFF844545;
                    case 0xFFF8F8F0 :
                        return 0xFF753332;
                    case 0xFFC00000:
                        return 0xFF773232;
                    case 0xFF800010:
                        return 0xFF511d1c;
                }
                break;
            case 16:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Pink Planet
                //Pink
                    case 0xFF9888C8:
                        return 0xFFFCA5E3;
                    case 0xFFC0C0C8 :
                        return 0xFFFF7AD6;
                    case 0xFFF0E8D0:
                        return 0xFFF766CA;
                    case 0xFFF8F8F0 :
                        return 0xFFED36B5;
                    case 0xFFC00000:
                        return 0xFFCB2DA0;
                    case 0xFF800010:
                        return 0xFF7B3A66;
                }
                break;
            case 17:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //White Nova
                //Light Gray/White
                    case 0xFF9888C8:
                        return 0xFFEBF2F5;
                    case 0xFFC0C0C8 :
                        return 0xFFC5CCCF;
                    case 0xFFF0E8D0:
                        return 0xFFDCE3E5;
                    case 0xFFF8F8F0 :
                        return 0xFFABB1B3;
                    case 0xFFC00000:
                        return 0xFFA3A6A8;
                    case 0xFF800010:
                        return 0xFF6E7273;
                }
                break;
            case 18:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //Minty Meteor
                //Light Green
                    case 0xFF9888C8:
                        return 0xFFD8EFB2;
                    case 0xFFC0C0C8 :
                        return 0xFFC6EB8D;
                    case 0xFFF0E8D0:
                        return 0xFFB9E17B;
                    case 0xFFF8F8F0 :
                        return 0xFFA0D251;
                    case 0xFFC00000:
                        return 0xFF8CB344;
                    case 0xFF800010:
                        return 0xFF5E7143;
                }
                break;
            case 19:
                switch(rgb){                    
                    case 0xFF786878:                         
                        return 0xFFFFFF00;
                //???
                //???
                    case 0xFF9888C8:
                        return 0xFFC8B5C2;
                    case 0xFFC0C0C8 :
                        return 0xFFBF9DBA;
                    case 0xFFF0E8D0:
                        return 0xFFA1749A;
                    case 0xFFF8F8F0 :
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
