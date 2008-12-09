package com.customwars;
/*
 *SideMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 20, 2006, 10:54 AM
 *A menu in the map editor. Used to select the Side of the units and properties placed
 */

import java.awt.*;
import java.awt.image.*;

public class SideMenu extends InGameMenu{
    
    public SideMenu(ImageObserver screen){
        super((480-96)/2,(320-150)/2,96,screen);
        
        String[] s = {"Neutral","Orange Star","Blue Moon","Green Earth","Yellow Comet","Black Hole","Jade Cosmos","Amber Corona","Parallel Galaxy","Copper Sun","Cobalt Drift"};
        super.loadStrings(s);
    }
    
    public int doMenuItem(){
        return item;
    }
}