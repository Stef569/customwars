package com.customwars.officer;
/*
 * UnitFilter.java
 *
 * Created on May 31, 2007, 9:39 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.image.*;

public class COPAnimFilter extends RGBImageFilter {
    int color = 0;
    public COPAnimFilter(int c) {
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
                //ie, default, you fooz.
                return rgb;
            case 1:
                switch(rgb){
                    //Blue Moon
                    //Guess That's Why They Call It The Blues!
                    case 0xFFFF0000:
                        return 0xFF0000FF;
                    case 0xFFFF5252:
                        return 0xFF5252FF;
                    case 0xFFFF7B7B:
                        return 0xFF7B7BFF;
                    case 0xFFFFA5A5:
                        return 0xFFA5A5FF;
                    case 0xFFFFE7E7:
                        return 0xFFE7E7FF;
                }
                break;
            case 2:
                switch(rgb){
                    //Green Earth
                    //Mean Green!
                    case 0xFFFF0000:
                        return 0xFF18DE18;
                    case 0xFFFF5252:
                        return 0xFF63DE63;
                    case 0xFFFF7B7B:
                        return 0xFF8CE78C;
                    case 0xFFFFA5A5:
                        return 0xFFB5EFB5;
                    case 0xFFFFE7E7:
                        return 0xFFE7FFE7;
                }
                break;
            case 3:
                switch(rgb){
                    //Yellow Comet
                    //No Comet!
                    case 0xFFFF0000:
                        return 0xFFBCBC0E;
                    case 0xFFFF5252:
                        return 0xFFD3D25B;
                    case 0xFFFF7B7B:
                        return 0xFFDEDD82;
                    case 0xFFFFA5A5:
                        return 0xFFE8E8AA;
                    case 0xFFFFE7E7:
                        return 0xFFF9F9E8;
                }
                break;
            case 4:
                switch(rgb){
                    //Black Hole
                    //Gray Hairs!
                    case 0xFFFF0000:
                        return 0xFF907070;
                    case 0xFFFF5252:
                        return 0xFFB08080;
                    case 0xFFFF7B7B:
                        return 0xFFD8A0A0;
                    case 0xFFFFA5A5:
                        return 0xFFE8C8C8;
                    case 0xFFFFE7E7:
                        return 0xFFE8E8D8;
                }
                break;
            case 5:
                switch(rgb){
                    //Jade Cosmos
                    //Made for Jade!
                    case 0xFFFF0000:
                        return 0xFF4C6E4F;
                    case 0xFFFF5252:
                        return 0xFF5E8862;
                    case 0xFFFF7B7B:
                        return 0xFF81A685;
                    case 0xFFFFA5A5:
                        return 0xFFB6CBB8;
                    case 0xFFFFE7E7:
                        return 0xFFDAE4DB;
                }
                break;
            case 6:
                switch(rgb){
                    //Amber Corona
                    //Agent Orange!
                    case 0xFFFF0000:
                        return 0xFFE78610;
                    case 0xFFFF5252:
                        return 0xFFF19A30;
                    case 0xFFFF7B7B:
                        return 0xFFFDB053;
                    case 0xFFFFA5A5:
                        return 0xFFFEC885;
                    case 0xFFFFE7E7:
                        return 0xFFFFEDD7;
                }
                break;
            case 7:
                switch(rgb){
                    //Parallel Galaxy
                    //Dark Purple Nurple!
                    case 0xFFFF0000:
                        return 0xFF827789;
                    case 0xFFFF5252:
                        return 0xFF9B8AA5;
                    case 0xFFFF7B7B:
                        return 0xFFC0ACCB;
                    case 0xFFFFA5A5:
                        return 0xFFDACFE1;
                    case 0xFFFFE7E7:
                        return 0xFFE4DBDE;
                }
                break;
            case 8:
                switch(rgb){
                    //Copper Sun
                    //Frown Brown!
                    case 0xFFFF0000:
                        return 0xFFAC7452;
                    case 0xFFFF5252:
                        return 0xFFC7A18A;
                    case 0xFFFF7B7B:
                        return 0xFFD4B8A6;
                    case 0xFFFFA5A5:
                        return 0xFFE2CEC2;
                    case 0xFFFFE7E7:
                        return 0xFFF7F2EF;
                }
                break;
            case 9:
                switch(rgb){
                    //Cobalt Drift
                    //Teal/Light Blue
                    case 0xFFFF0000:
                        return 0xFF58A5A2;
                    case 0xFFFF5252:
                        return 0xFF8EC2C0;
                    case 0xFFFF7B7B:
                        return 0xFFA9D0CF;
                    case 0xFFFFA5A5:
                        return 0xFFC4DFDE;
                    case 0xFFFFE7E7:
                        return 0xFFEFF6F6;
                }
                break;
            case 10:
                switch(rgb){
                    //Gray Sky
                    //Original Parallel Galaxy Colors
                    //Dark Black!
                    //Vimes -- I just subbed in colors of BH
                    case 0xFFFF0000:
                        return 0xFF907070;
                    case 0xFFFF5252:
                        return 0xFFB08080;
                    case 0xFFFF7B7B:
                        return 0xFFD8A0A0;
                    case 0xFFFFA5A5:
                        return 0xFFE8C8C8;
                    case 0xFFFFE7E7:
                        return 0xFFE8E8D8;
                
                }
                break;
            case 11:
                switch(rgb){
                    //Rose Orchid
                    //Roast'd Red!
                    //Made up these colors <_<
                    case 0xFFFF0000:
                        return 0xFFFF1111;
                    case 0xFFFF5252:
                        return 0xFFFF6464;
                    case 0xFFFF7B7B:
                        return 0xFFFF8C8C;
                    case 0xFFFFA5A5:
                        return 0xFFFFB6B6;
                    case 0xFFFFE7E7:
                        return 0xFFFFF8F8;
                }
                break;
            case 12:
                switch(rgb){
                    //Purple Lightning
                    //What the hell, Uru?
                    case 0xFFFF0000:
                        return 0xFF827789;
                    case 0xFFFF5252:
                        return 0xFF9B8AA5;
                    case 0xFFFF7B7B:
                        return 0xFFC0ACCB;
                    case 0xFFFFA5A5:
                        return 0xFFDACFE1;
                    case 0xFFFFE7E7:
                        return 0xFFE4DBDE;
                }
                break;
            case 13:
                switch(rgb){
                    //Azure Asteroid
                    //No seriously.
                    case 0xFFFF0000:
                        return 0xFF827789;
                    case 0xFFFF5252:
                        return 0xFF9B8AA5;
                    case 0xFFFF7B7B:
                        return 0xFFC0ACCB;
                    case 0xFFFFA5A5:
                        return 0xFFDACFE1;
                    case 0xFFFFE7E7:
                        return 0xFFE4DBDE;
                }
                break;
            case 14:
                    //Red Fire
                    //How is this distinguishable from Red Star and Rose Red?
                return rgb;
            case 15:
                switch(rgb){
                    //Pink Planet
                    //ffff I don't care anymore;
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
                    //White power!
                    case 0xFFFF0000:
                        return 0xFF000000;
                    case 0xFFFF5252:
                        return 0xFF000000;
                    case 0xFFFF7B7B:
                        return 0xFF000000;
                    case 0xFFFFA5A5:
                        return 0xFF000000;
                    case 0xFFFFE7E7:
                        return 0xFF000000;
                }
                break;
            case 17:
                switch(rgb){
                    //Minty Meteor
                    //Minty? More like...Manly!
                    case 0xFFFF0000:
                        return 0xFF4C6E4F;
                    case 0xFFFF5252:
                        return 0xFF5E8862;
                    case 0xFFFF7B7B:
                        return 0xFF81A685;
                    case 0xFFFFA5A5:
                        return 0xFFB6CBB8;
                    case 0xFFFFE7E7:
                        return 0xFFDAE4DB;
                }
                break;
            case 18:
                switch(rgb){
                    //???
                    //???
                    //??? I'll just sub in random ones for dis one.
                    //nowait TRANSPARENT
                    case 0xFFFF0000:
                        return 0xFF000000;
                    case 0xFFFF5252:
                        return 0xCC000000;
                    case 0xFFFF7B7B:
                        return 0xAA000000;
                    case 0xFFFFA5A5:
                        return 0x99000000;
                    case 0xFFFFE7E7:
                        return 0x33000000;
                    case 0xFFFFFFFF:
                        return 0x00000000;
                }
                break;
                
                
        }
        
        return rgb;
    }
}
