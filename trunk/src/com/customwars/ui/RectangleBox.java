package com.customwars.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import com.customwars.Battle;

import java.awt.image.*;
/*
 *RectangleBox.java
 *Author:Albert Lai
 *Why is the name so ridiculously redundant? There's already a Box class,
 *and there's already a Rectangle class. On second thought, AnimationBox
 *would probably make more sense.
 *RectangleBox allows for growing rectangles, and rectangles that shift colors/transparencies.
 */
public class RectangleBox extends Animation{
    int sWidth, eWidth, sHeight, eHeight; //Target dimensions
    int width, height; //Current dimensions
    int[] red, green, blue, alpha; //All colors that are to be flipped through.
    int cRed, cGreen, cBlue, cAlpha; //Current colors
    int shift = -1; //Interval over which all colors are shifted
    int index = 0; //The first color that it's shifting from. index+1 is the color it's shifting to. Index can wrap around.
    public RectangleBox(Battle b, int layer, int[] red, int[] green, int[] blue, int startx, int starty, int endx, int endy, int width, int height, int sAlpha, int eAlpha, int duration, int delay, double ease) {
    super(b,null,layer,0,0,startx,starty,endx,endy,sAlpha,eAlpha,duration,delay,ease);
    this.width = width;
    this.height = height;
    this.red = red;
    this.green = green;
    this.blue = blue;
    cRed = red[0];
    cGreen = green[0];
    cBlue = blue[0];
    cAlpha= (int)(sAlpha*2.55);
    }
    /**
     *This overrides the Animation draw method
     *@params g the Graphics2D class used for drawing
     *@params bs the BattleScreen used to get shift. Stop sniggering.
     */
    public void draw(Graphics2D g, BattleScreen bs) {
        if(lockpos) {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (cAlpha+a)/355.0f));
            Color current = new Color(cRed,cGreen,cBlue);
            g.setColor(current);
            g.fillRect((int)x, (int)y, width, height);
        } else {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (cAlpha+a)/355.0f));
            Color current = new Color(cRed,cGreen,cBlue);
            g.setColor(current);
            g.fillRect((int)x-bs.getSX(), (int)y-bs.getSY(), width, height);
        }
    }
    /**
     *This sets the time period over which colors are shifted
     *@params time the amount of frames for shifting between all colors. 
     */
    public void setShiftTimer(int time)
    {
        shift = time;
    }
    /**
     *This inputs an array of colors to shift between.
     *@params red the differing red values to shift through 
     *@params green the differing green values to shift through 
     *@params blue the differing blue values to shift through 
     *@params alpha the differing alpha values to shift through
     */
    public void setColors(int[] red, int[] green, int[] blue, int[] alpha)
    {
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.alpha = alpha;
    }
    //Inner listener to override the moving stuff for Animations
    private class Listener implements ActionListener{
        Animation animation;
        public Listener(Animation a) {
            animation = a;
            tick = 0;
            store = 0;
            if(ease != 0) //If the ease is a nonzero value, handle ease calculations
            {
                for(int i = 0; i<duration; i++) //Change to i<= duration if there are problems.
                {
                    store+= i;
                }
            }
        }
        public void actionPerformed(ActionEvent evt) {
            tick++;
            if(trigger == false) {
                if(layer == 1)
                    b.getLayerOne().add(animation);
                else if(layer == 2)
                    b.getLayerTwo().add(animation);
                else if(layer == 3)
                    b.getLayerThree().add(animation);
                else if(layer == 4)
                    b.getLayerFour().add(animation);
                
                trigger = true;
            }
            //Move it!
            
            if(ease == 0 && tick!=1) //No easeing required
            {
                a+= (ea-sa)/duration;
                y+= (ey-sy)/duration;
                x+= (ex-sx)/duration;
                width += (eWidth-sWidth)/duration;
                height += (eHeight-sHeight)/duration;
            } else if(ease > 0) {
                a += ((tick/store) * (ea-sa) * ease + (ea-sa)/duration)/(ease+1.0);
                y += ((tick/store) * (ey-sy) * ease + (ey-sy)/duration)/(ease+1.0);
                x += ((tick/store) * (ex-sx) * ease + (ex-sx)/duration)/(ease+1.0);
                width += ((tick/store) * (eWidth-sWidth) * ease + (eWidth-sWidth)/duration)/(ease+1.0);
                height += ((tick/store) * (eHeight-sHeight) * ease + (eHeight-sHeight)/duration)/(ease+1.0);
            } else if(ease < 0) {
                a += (((duration-tick)/store) * (ea-sa) * ease + (ea-sa)/duration)/(ease+1.0);
                y += (((duration-tick)/store) * (ey-sy) * ease + (ey-sy)/duration)/(ease+1.0);
                x += (((duration-tick)/store) * (ex-sx) * ease + (ex-sx)/duration)/(ease+1.0);
                width += (((duration-tick)/store) * (eWidth-sWidth) * ease + (eWidth-sWidth)/duration)/(ease+1.0);
                height += (((duration-tick)/store) * (eHeight-sHeight) * ease + (eHeight-sHeight)/duration)/(ease+1.0);
            }
            if(a>100)
                a = 100;
            if(a<0)
                a = 0;
            //Color shifting requires no easing values.
            //Because it would literally kill me to implement them.
            int nextindex = index+1;
            if(nextindex == red.length) //if index+1 is equal to the length, this means we'll array out if I try to increment it
                nextindex = 0;
            //This calculation is basically 100-%time/totaltime * origin color + %time/totaltime * new color
            cRed = (int)((shift-(tick%shift))/(shift*1.0) * red[index] + ((tick%shift))/(shift*1.0) * red[nextindex]);
            cGreen = (int)((shift-(tick%shift))/(shift*1.0) * green[index] + ((tick%shift))/(shift*1.0) * green[nextindex]);
            cBlue = (int)((shift-(tick%shift))/(shift*1.0) * blue[index] + ((tick%shift))/(shift*1.0) * blue[nextindex]);
            cAlpha = (int)((shift-(tick%shift))/(shift*1.0) * alpha[index] + ((tick%shift))/(shift*1.0) * alpha[nextindex]);

            if(tick%shift == shift-1)
            {
            index++;
            if(index+1 == red.length) //if index+1 is equal to the length, this means we'll array out if I try to increment it
                index = 0;
            }
            //Too high...delete T_T
            if(tick>duration) {
                    
                if(layer == 1)
                    b.getLayerOne().remove(animation);
                else if(layer == 2)
                    b.getLayerTwo().remove(animation);
                else if(layer == 3)
                    b.getLayerThree().remove(animation);
                else if(layer == 4)
                    b.getLayerFour().remove(animation);
                timer.setRepeats(false); //Timer, you're no longer needed.
            }
            
        }
    }
}
