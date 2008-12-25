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
    
    //constructor
    public OptionsMenu(ImageObserver screen){
        //super((256-96)/2,(192-80)/2);
        //super((256-96)/2,(162-96)/2);
        //super((256-96)/2,(162-112)/2);
        super((480-96)/2,(320-128)/2,130,screen);
        
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