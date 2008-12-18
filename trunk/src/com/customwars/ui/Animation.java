package com.customwars.ui;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import com.customwars.ai.Battle;
import com.customwars.map.location.Location;
import com.customwars.sfx.SFX;
import com.customwars.state.ResourceLoader;
import com.customwars.unit.Unit;

import java.awt.image.*;
import java.io.*;
/*
 *Animation.java
 *Author:Albert Lai
 *In theory, this should be able to handle multiple animations at once -
 *like power animation, or, at its most basic level, explosions.
 *All animations are performed simultaneously, unless delayed
 */

public class Animation{
    protected int tick; //This ticks for frame movement
    int tick2; //This ticks for linked animations.
    protected float x; //Holds current x
    protected float y; //Holds current y
    int xoffset;
    int yoffset; //This allows proper centering
    int sx,sy,ex,ey; //Start and end locations for movement
    int sa,ea; //start and end alpha for fade in/ fade out.
    int cx1, cy1, cx2, cy2; //Start and end locations of 'cut out'd for spritesheets
    float a; //holds current alpha
    public float duration; //Time the explosion takes in frames - 30fps
    protected int delay; //Time before this activates.
    double ease; //The 'ease' of the image
    float store; //Used for ease calculations.
    Image type;
    Battle b;
    protected Timer timer; //Main timer
    Boolean trigger = false; //Used to see if the image had been added yet.
    int layer; //What layer they're one. One is top, four is bottom.
    protected boolean lockpos; //This regulates whether animations are supposed to shift with the battlefield or not
    boolean animLock; //This means this animation locks the screen while playing
    boolean moveAnimation; //set to true means a movement animation.
    Unit movedUnit; //set to mean the unit that is moved
    boolean linked = false; //is this animation linked?
    
    int spriteFrames, curx, spriteWidth, spriteHeight; //current location
    boolean spriteSheet, loop;
    public Animation() //Empty constructor for later setting.
    {
        layer = 1; //lol default.
        delay = 0;
        ease = 0;
    }
    //This constructor assumes a spritesheet is being used.
    public Animation(Battle b, Image i, int layer, int xoffset, int yoffset, int cutx1, int cuty1, int cutx2, int cuty2, int startx, int starty, int endx, int endy, int sAlpha, int eAlpha, int duration, int delay, double ease) {
        this.b = b;
        type = i;
        this.layer = layer;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
        x = startx;
        y = starty;
        sx = startx;
        sy = starty;
        ex = endx;
        ey = endy;
        sa = sAlpha;
        ea = eAlpha;
        a = sAlpha;
        this.duration = duration;
        this.delay = delay;
        tick = 0;
        tick2 = 0;
        this.ease = ease;
        cx1 = cutx1;
        cy1 = cuty1;
        cx2 = cutx2;
        cy2 = cuty2;
        
    }
    //This constructor assumes no spritesheet is being used.
    public Animation(Battle b, Image i, int layer, int xoffset, int yoffset, int startx, int starty, int endx, int endy, int sAlpha, int eAlpha, int duration, int delay, double ease) {
        this.b = b;
        type = i;
        this.layer = layer;
        this.xoffset = xoffset;
        this.yoffset = yoffset;
        x = startx;
        y = starty;
        sx = startx;
        sy = starty;
        ex = endx;
        ey = endy;
        sa = sAlpha;
        ea = eAlpha;
        a = sAlpha;
        this.duration = duration;
        this.delay = delay;
        tick = 0;
        tick2 = 0;
        this.ease = ease;
        cx1 = 0;
        cy1 = 0;
        //Graphics2D temp = new Graphics2D();
        //cx2 = i.getWidth();
        //cy2 = i.getHeight();
        
    }
    public void setSiloExplosion(Location loc ,Battle b, int delay) {
        //Type = 1 - land; 2 - sea; 3 - air.
        this.b = b;
        sx = loc.getCol() * 16;
        sy = loc.getRow() * 16;
        ex = loc.getCol() * 16;
        ey = loc.getRow() * 16;
        x = sx;
        y = sy;
        sa = 100;
        ea = 100;
        a = 100;
        this.delay = delay;
        //This block checks the type of the unit, and then fills in the details based on that.
        xoffset = 32 + 8;
        yoffset = 32 + 16;
        //MiscGraphics.getSeaExplode().flush();
        type = MiscGraphics.getSiloExplode();
        duration = MiscGraphics.getSiloDelay();
        cx1 = 0;
        cy1 = 0;
        cx2 = 96;
        cy2 = 96;
        
    }
    //This sets the Animation to an "explosion" type, centered around a unit.
    public void setExplosion(Unit u, int delay) {
        b = u.getArmy().getBattle();
        sx = u.getLocation().getCol() * 16;
        sy = u.getLocation().getRow() * 16;
        ex = u.getLocation().getCol() * 16;
        ey = u.getLocation().getRow() * 16;
        x = sx;
        y = sy;
        sa = 100;
        ea = 100;
        a = 100;
        cx1 = 0;
        cy1 = 0;
        cx2 = 32;
        cy2 = 32;
        this.delay = delay;

        //This block checks the type of the unit, and then fills in the details based on that.
        if(u.getMoveType() == 0 ||u.getMoveType() ==1||u.getMoveType() ==2||u.getMoveType() ==3||u.getMoveType() ==7||u.getMoveType() ==8) {
            xoffset = 8;
            yoffset = 16;
            setSpriteSheet(32,32,10,false);
            //MiscGraphics.getLandExplode().flush();
            type = MiscGraphics.getLandExplode();
            duration = MiscGraphics.getExplodeDelay();
            String soundLocation  = ResourceLoader.properties.getProperty("soundLocation");
            SFX.playClip(soundLocation + "/carsplod.wav");
        } else if(u.getMoveType() == 4) {
            xoffset = 8;
            yoffset = 8;
            setSpriteSheet(32,32,10,false);
            //MiscGraphics.getAirExplode().flush();
            type = MiscGraphics.getAirExplode();
            duration = MiscGraphics.getExplodeDelay();
            String soundLocation  = ResourceLoader.properties.getProperty("soundLocation");
            SFX.playClip(soundLocation + "/airsplod.wav");
        } else if(u.getMoveType() == 5 ||u.getMoveType() ==6||(u.getMoveType() == 9 && (b.getMap().find(u).getTerrain().getName().equals("Reef") || b.getMap().find(u).getTerrain().getName().equals("Sea")))) {
            xoffset = 8;
            yoffset = 16;
            setSpriteSheet(32,8,32,false);
            //MiscGraphics.getSeaExplode().flush();
            type = MiscGraphics.getSeaExplode();
            duration = MiscGraphics.getExplodeDelay();
            String soundLocation  = ResourceLoader.properties.getProperty("soundLocation");
            SFX.playClip(soundLocation + "/seasplod.wav");
        } else if(u.getMoveType() == 9) {
            xoffset = 8;
            yoffset = 16;
            //MiscGraphics.getLandExplode().flush();
            type = MiscGraphics.getLandExplode();
            duration = MiscGraphics.getExplodeDelay();
            String soundLocation  = ResourceLoader.properties.getProperty("soundLocation");
            SFX.playClip(soundLocation + "/carsplod.wav");
        }
        
    }
    //This sets power animations. The 'bling!', basically
    public void setSpriteSheet(int width, int height, int frames,boolean loop) {
        spriteWidth = width;
        spriteHeight = height;
        curx = 0;
        spriteFrames = frames;
        this.loop = loop;
        spriteSheet = true;
    }
    
    //Creates an explosion at a location
    public void setExplosion(Location loc ,Battle b, int delay, int explodeType) {
        //Type = 1 - land; 2 - sea; 3 - air.
        this.b = b;
        sx = loc.getCol() * 16;
        sy = loc.getRow() * 16;
        ex = loc.getCol() * 16;
        ey = loc.getRow() * 16;
        x = sx;
        y = sy;
        sa = 100;
        ea = 100;
        a = 100;
        cx1 = 0;
        cy1 = 0;
        cx2 = 32;
        cy2 = 32;
        this.delay = delay;
        //This block checks the type of the unit, and then fills in the details based on that.
        if(explodeType == 1) {
            xoffset = 8;
            yoffset = 16;
            //MiscGraphics.getLandExplode().flush();
            type = MiscGraphics.getLandExplode();
            duration = MiscGraphics.getExplodeDelay();
            
        } else if(explodeType == 3) {
            xoffset = 8;
            yoffset = 8;
            //MiscGraphics.getAirExplode().flush();
            type = MiscGraphics.getAirExplode();
            duration = MiscGraphics.getExplodeDelay();
        } else if(explodeType==2) {
            xoffset = 8;
            yoffset = 16;
            //MiscGraphics.getSeaExplode().flush();
            type = MiscGraphics.getSeaExplode();
            duration = MiscGraphics.getExplodeDelay();
        }
        
    }
    /**
     *This initializes the timers.
     *@param lock whether the animation moves relative to the battlefield
     *@param flush whether the animation needs to be reloaded (for explosions, for example)
     */
    public void setup(boolean lock, boolean flush){
        lockpos = lock;
        if(type!=null && flush)
            type.flush();
        Listener animationTerminator = new Listener(this);
        timer = new Timer(20, animationTerminator);
        timer.setInitialDelay(delay*20 + 20);
        timer.setCoalesce(false);
        b.queue.add(this);
    }
    //Same as above, but does not flush.
    public void setupMove(boolean lock, Unit u, int dir){
        u.setMoving(true);
        moveAnimation = true;
        movedUnit = u;
        u.setDirection(dir);
        lockpos = lock;
        Listener animationTerminator = new Listener(this);
        timer = new Timer(20, animationTerminator);
        timer.setInitialDelay(delay*20 + 20);
        timer.setCoalesce(false);
        b.queue.add(this);
    }
    
    public void draw(Graphics2D g, BattleScreen bs) {
        if(spriteSheet){
            //duration/frames is the current frame
            //curx = (int)(tick/duration * spriteFrames * spriteWidth); This causes an interesting wheee! effect
            curx = (int)(tick/duration * spriteFrames) * spriteWidth;
            if(lockpos) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a/100.0f));
                g.drawImage(type, (int)x-xoffset, (int)y-yoffset, (int)x-xoffset+(cx2-cx1), (int)y-yoffset+(cy2-cy1), curx , cy1,curx + spriteWidth,cy2, bs);
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a/100.0f));
                g.drawImage(type, (int)x-xoffset-bs.getSX(), (int)y-yoffset-bs.getSY(), (int)x-xoffset-bs.getSX()+(cx2-cx1), (int)y-yoffset-bs.getSY()+(cy2-cy1), curx,cy1,curx + spriteWidth,cy2, bs);
            }    
        }else{
            if(lockpos) {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a/100.0f));
                g.drawImage(type, (int)x-xoffset, (int)y-yoffset, (int)x-xoffset+(cx2-cx1), (int)y-yoffset+(cy2-cy1), cx1,cy1,cx2,cy2, bs);
            } else {
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, a/100.0f));
                g.drawImage(type, (int)x-xoffset-bs.getSX(), (int)y-yoffset-bs.getSY(), (int)x-xoffset-bs.getSX()+(cx2-cx1), (int)y-yoffset-bs.getSY()+(cy2-cy1), cx1,cy1,cx2,cy2, bs);
            }
        }
    }
    /**
     *This causes animation to start playing when the current animation ends
     *@param animation the animation to link to.
     */
    public void linkTo(Animation animation) {
        linkedListener linker = new linkedListener(animation);
        timer.addActionListener(linker);
        linked = true;
    }
    /**
     * This effectively stops the timer from firing again by stripping all action listeners and halting the timer.
     */
    public void removeTimers() {
        ActionListener[] listeners = timer.getActionListeners();
        for(int i = 0; i< listeners.length; i++) {
            if(listeners[i] instanceof linkedListener)
            timer.removeActionListener(listeners[i]);
        }
        
        tick = (int)duration;
        timer.setRepeats(false);
    }
    
    public void addLock(){
        b.animlock = true;
        animLock = true;
    }
    public void start() {
        if(animLock) {
            b.animlock = true;
            timer.addActionListener(new unlockListener());
        }
        timer.start();
    }
    //This timer handles the addition and removal of animations
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
                b.queue.remove(this);
                trigger = true;
            }
            //Move it!
            if(ease == 0 && tick!=1) //No easeing required
            {
                a+= (ea-sa)/duration;
                y+= (ey-sy)/duration;
                x+= (ex-sx)/duration;
            } else if(ease > 0) {
                a += ((tick/store) * (ea-sa) * ease + (ea-sa)/duration)/(ease+1.0);
                y += ((tick/store) * (ey-sy) * ease + (ey-sy)/duration)/(ease+1.0);
                x += ((tick/store) * (ex-sx) * ease + (ex-sx)/duration)/(ease+1.0);
            } else if(ease < 0) {
                a += (((duration-tick)/store) * (ea-sa) * ease + (ea-sa)/duration)/(ease+1.0);
                y += (((duration-tick)/store) * (ey-sy) * ease + (ey-sy)/duration)/(ease+1.0);
                x += (((duration-tick)/store) * (ex-sx) * ease + (ex-sx)/duration)/(ease+1.0);
            }
            if(a>100)
                a = 100;
            if(a<0)
                a = 0;
            
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
                if(moveAnimation && !linked)
                    movedUnit.setMoving(false);
            }
            
        }
    }
    
    
    
    private class linkedListener implements ActionListener{
        Animation linkedAnimation; //Linked animation to be executed when this one ends
        int totaltime;
        boolean trig2;
        public linkedListener(Animation a) {
            tick2 = 0;
            linkedAnimation =a;
            totaltime = (int)duration; //How long does it take for this animation to end?
            trig2 = false;
        }
        public void actionPerformed(ActionEvent evt){
            tick2++;
            if(tick2+1>=totaltime && !trig2) {
                if(animLock)
                    linkedAnimation.addLock();
                linkedAnimation.start();
                trig2 = true;
            }
            
        }
    }
    
    private class unlockListener implements ActionListener{
        int time;
        public unlockListener() {
            time = (int)duration; //How long does it take for this animation to end?
        }
        public void actionPerformed(ActionEvent evt){
            //This piggybacks off the normal listener
            if(tick>=duration && !linked) {
                b.animlock = false;
            }
        }
    }
}




