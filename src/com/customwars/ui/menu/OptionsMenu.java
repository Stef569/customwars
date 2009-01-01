package com.customwars.ui.menu;
/*
 *OptionsMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: August 14, 2006, 7:29 AM
 *The options menu in a battle. Contains things like Resizing the screen and quitting
 */

import java.awt.image.ImageObserver;


public class OptionsMenu extends InGameMenu{
    
    private static final int OPTIONS_MENU_WIDTH = 250;
    private static final int OPTIONS_MENU_X = 142;
	private static final int OPTIONS_MENU_Y = 96;

	//constructor
    public OptionsMenu(ImageObserver screen){
        super(OPTIONS_MENU_X,OPTIONS_MENU_Y,OPTIONS_MENU_WIDTH,screen);
        
        String[] s = {"Delete","Yield","Music On/Off","Visual Mode","Resize","Scale","Save Replay","Test Replay DEBUG","Exit Map"};
        super.loadStrings(s);
    }
    
    public int doMenuItem(){
        switch(item){
            case 0:
                //Delete
                return 8;
                //break;
            case 1:
                //Surrender
                return 6;
            case 2:
                //Music On/Off
                return 7;
            case 3:
                //Change the Visual Mode
                break;
            case 4:
                //RESIZE SCREEN
                return 3;
            case 5:
                //re-scale the screen
                return 4;
            case 6:
                //Save the replay thus far
                return 9;
            case 7:
                //DEBUG: prints replay to screen
               return 11;
            case 8:
                //Return to the main screen
                return 5;
        }
        return 0;
    }
}