package cwsource;
/*
 * DarkFilter.java
 *
 * Created on June 1, 2007, 7:19 PM
 *
 * To change this template, choose Tools | Template Manager
 * and open the template in the editor.
 */

import java.awt.image.*;

public class DarkFilter extends RGBImageFilter {
    
    /** Creates a new instance of DarkFilter */
    public DarkFilter() {
        // The filter's operation does not depend on the
        // pixel's location, so IndexColorModels can be
        // filtered directly.
        canFilterIndexColorModel = true;
    }
    
     public int filterRGB(int x, int y, int rgb) {
         int fac = 70;
         if((rgb & 0xFF000000)==0)return 0;
         int r = (rgb & 0x00FF0000) - fac * 0x00010000;
         int g = (rgb & 0x0000FF00) - fac * 0x00000100;
         int b = (rgb & 0x000000FF) - fac;
         if(r < 0)r=0;
         if(g < 0)g=0;
         if(b < 0)b=0;
         return 0xFF000000 + r + g + b;
    }
    
}