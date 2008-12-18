package com.customwars;
/*
 *SwitchGradient.java
 *Author: CoconutTank
 *Contributors: Kosheh
 *Creation: November 8, 2007, 12:23 AM
 *The Battle Screen, the central graphical component of CW during a battle
 */
        
import java.awt.Color;
import java.awt.GradientPaint;

    public class SwitchGradient
{
    public static GradientPaint getGradient(int gradient)
    {
        switch (gradient)
        {
            //Orange Star
            case 0: return new GradientPaint(45, 45, new Color(247,220,203), 105, 105, new Color(248,175,165), true);
            //Blue Moon
            case 1: return new GradientPaint(45, 45, new Color(240,229,247), 105, 105, new Color(175,165,248), true);
            //Green Earth
            case 2: return new GradientPaint(45, 45, new Color(212,247,220), 105, 105, new Color(135,250,138), true);
            //Hello Comet
            case 3: return new GradientPaint(45, 45, new Color(247,229,185), 105, 105, new Color(253,248,142), true);
            //Black Hole
            case 4: return new GradientPaint(45, 45, new Color(229,229,220), 105, 105, new Color(200,200,200), true);
            //Jade Cosmos
            case 5: return new GradientPaint(45, 45, new Color(220,231,200), 105, 105, new Color(159,225,168), true);
            //Amber Corona
            case 6: return new GradientPaint(45, 45, new Color(235,234,198), 105, 105, new Color(249,219,174), true);
            //Fecal Matter
            case 7: return new GradientPaint(45, 45, new Color(219,201,231), 105, 105, new Color(217,191,249), true);
            //Copper Militia - didn't care lol
            case 8: return new GradientPaint(45, 45, new Color(120,120,120), 105, 105, new Color(160,160,160), true);
            //Arctic Militia - didn't care here either, lol
            case 9: return new GradientPaint(45, 45, new Color(120,120,120), 105, 105, new Color(160,160,160), true);
        }
    return null;
    }
}