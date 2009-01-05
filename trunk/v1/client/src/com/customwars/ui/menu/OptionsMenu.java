package com.customwars.ui.menu;


import java.awt.image.ImageObserver;

/*
 *OptionsMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: August 14, 2006, 7:29 AM
 *The options menu in a battle. Contains things like Resizing the screen and quitting
 */
public class OptionsMenu extends InGameMenu{
  // Menu Items
  private static final int MNU_DELETE = 0;
  private static final int MNU_SURRENDER = 1;
  private static final int MNU_TOGGLE_MUSIC = 2;
  private static final int MNU_VISUAL_MODE = 3;
  private static final int MNU_RESIZE = 4;
  private static final int MNU_RESCALE = 5;
  private static final int MNU_SAVE_REPLAY = 6;
  private static final int MNU_DEBUG_PRINT_REPLAY = 7;
  private static final int MNU_MAIN = 8;

  // Menu Names
  private static final String[] s = {"Delete","Yield","Music On/Off","Visual Mode","Resize","Scale","Save Replay",
          "Test Replay DEBUG","Exit Map"};

  // Gui Layout
  private static final int OPTIONS_MENU_WIDTH = 250;
  private static final int OPTIONS_MENU_X = 142;
	private static final int OPTIONS_MENU_Y = 96;

    public OptionsMenu(ImageObserver screen){
        super(OPTIONS_MENU_X,OPTIONS_MENU_Y,OPTIONS_MENU_WIDTH,screen);
        super.loadStrings(s);
    }
    
    public int doMenuItem(){
        switch(item){
            case MNU_DELETE:
                return MENU_SEL.DELETE;
            case MNU_SURRENDER:
                return MENU_SEL.YIELD;
            case MNU_TOGGLE_MUSIC:
                return MENU_SEL.TOGGLE_MUSIC;
            case MNU_VISUAL_MODE:
                break;
            case MNU_RESIZE:
                return MENU_SEL.RESIZE_SCREEN;
            case MNU_RESCALE:
                return MENU_SEL.RESCALE;
            case MNU_SAVE_REPLAY:
                return MENU_SEL.SAVE_REPLAY;
            case MNU_DEBUG_PRINT_REPLAY:
               return MENU_SEL.SAVE_REPLAY_DEBUG;
            case MNU_MAIN:
                return MENU_SEL.EXIT;
            default: throw new AssertionError("Don't know about MenuItem " + item);
        }
        return 0;
    }
}