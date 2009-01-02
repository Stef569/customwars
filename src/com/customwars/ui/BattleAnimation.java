package com.customwars.ui;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

import com.customwars.ai.BaseDMG;
import com.customwars.ai.GameSession;
import com.customwars.map.location.Terrain;
import com.customwars.unit.Unit;

import java.awt.image.*;
/*
 *Battle Animation.java
 *Author:Albert Lai
 *
 */
public class BattleAnimation extends Animation{
    Unit attacker;
    Unit defender;
    Image backa, backb;
    Image forea, foreb;
    Image frame; //holds the 'grid''
    Terrain atkTerrain, defTerrain;
    int atkDmg, countDmg;
    //I can procedur
    public BattleAnimation(Unit u, Unit d, int counterDamage, int damage) {
        super();
        atkDmg = damage;
        countDmg = counterDamage;
        
        attacker = u;
        defender = d;
        b = u.getArmy().getBattle();
        duration = 300;
        forea = BattleGraphics.getFore(0,attacker.getArmy().getBattle().getMap().find(attacker).getTerrain());
        foreb = BattleGraphics.getFore(0,defender.getArmy().getBattle().getMap().find(defender).getTerrain());
        atkTerrain = attacker.getArmy().getBattle().getMap().find(attacker).getTerrain();
        defTerrain = defender.getArmy().getBattle().getMap().find(defender).getTerrain();
        
        int orient = attacker.getLocation().orient(defender.getLocation());
        if(atkTerrain.getName().equals("Plain") || atkTerrain.getName().equals("Road")){
            backa = BattleGraphics.getPlainBack(0, attacker, defender);
        } else {
            backa = BattleGraphics.getAltBack(0,atkTerrain);
        }
        if(defTerrain.getName().equals("Plain") || defTerrain.getName().equals("Road")) {
            backb = BattleGraphics.getPlainBack(0, defender, attacker);
        } else {
            backb = BattleGraphics.getAltBack(0,defTerrain);
        }
    }
    
    public void draw(Graphics2D g, BattleScreen bs) {
        //155 - defender hit, 190 - attacker hit.
        //fades out the background
        if(tick < 20) {
            //First! Fade out the background
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, (float)(tick * 0.02)));
            g.setColor(Color.BLACK);
            g.fill(new Rectangle(0,0,GameSession.getBattleScreen().getWidth(), GameSession.getBattleScreen().getHeight()));
            
        } else {//stay faded out plz
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.4f));
            g.setColor(Color.BLACK);
            g.fill(new Rectangle(0,0,GameSession.getBattleScreen().getWidth(), GameSession.getBattleScreen().getHeight()));
        }
        //draws the main instance of the animation: ends intro at 60;
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER));
        //clips window - full width is 258, beginning at 110
        //full height is, uh, begins at 30, height of 249
        g.setClip(null);
        if(tick<60) {
            Polygon poly = new Polygon();
            poly.addPoint(239-(tick*129/60),30);
            poly.addPoint(239+(tick*129/60),30);
            poly.addPoint(239+(tick*129/60),279);
            poly.addPoint(239-(tick*129/60),279);
            
            g.setClip(poly);
        }
        if(tick < 300) {
            //The width of the whole thing is 127 for the image, while 110 is the spacer including the divider bar in the middle
            //draws the sky
            g.drawImage(BattleGraphics.getSky(0,atkTerrain),110, 87, bs);
            g.drawImage(BattleGraphics.getSky(0,defTerrain),110+127+4, 87, bs);
            //draws the divider
            //WIDTH OF DIVIDER = 4
            g.fillRect(110+127,87,4,192);
            //draws the background
            g.drawImage(backa,110, 87, bs);
            g.drawImage(backb,110+127+4, 87, bs);
            //draws the foreground
            g.drawImage(forea,110, 87, bs);
            g.drawImage(foreb,110+127+4, 87, bs);
        }
        //draws allied HP bar
        //Damage is dealt over 40 ticks.
        
        //draws the health bars
        if(tick > 180) {
            if(tick <= 200) {
                g.setColor(Color.RED);
                g.fillRect(110,78,(127*((attacker.getHP() + countDmg)*(30-(tick-180)) + (attacker.getHP())*(tick-180))/30)/(100),9); //draws the appropriate percent of the life bar
            } else {
                //draws the appropriate percent of the life bar after being hit
                g.setColor(Color.RED);
                g.fillRect(110,78,(127*(attacker.getHP()))/(100),9);
                
            }
        } else {
            g.setColor(Color.RED);
            g.fillRect(110,78,(127*(attacker.getHP() + countDmg))/(100),9); //draws the appropriate percent of the life bar
        }
        //draws defending HP Bar
        //Delay of 50 ticks, same duration
        if(tick > 145) {
            if(tick <= 165) {
                g.setColor(Color.RED);
                g.fillRect(110+127+4,78,(127*((defender.getHP() + atkDmg)*(20-(tick-145)) + (defender.getHP())*(tick-145))/20)/(100),9); //draws the appropriate percent of the life bar
            } else {
                g.setColor(Color.RED);
                g.fillRect(110+127+4,78,(127*(defender.getHP()))/(100),9); //draws the appropriate percent of the life bar
            }
        } else {
            g.setColor(Color.RED);
            g.fillRect(110+127+4,78,(127*(defender.getHP() + atkDmg))/(100),9); //draws the appropriate percent of the life bar
        }
        
        //Draws the CO portraits
        int emotiona = 0;
        int emotionb = 0;
        if(attacker.getArmy().getCO().isAltCostume()) {
            g.drawImage(MiscGraphics.getCOSheet(attacker.getArmy().getCO().getId()),110,30,110+48,78,273,302+emotiona*48,320,302+(emotiona+1)*48,bs);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(attacker.getArmy().getCO().getId()),110,30,110+48,78,emotiona*48,350,(emotiona+1)*48,398,bs);
        }
        if(defender.getArmy().getCO().isAltCostume()) {
            g.drawImage(MiscGraphics.getCOSheet(defender.getArmy().getCO().getId()),110+127*2+4,30,110+127*2-48+4,78,273,302+emotionb*48,320,302+(emotionb+1)*48,bs);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(defender.getArmy().getCO().getId()),110+127*2+4,30,110+127*2-48+4,78,emotionb*48,350,(emotionb+1)*48,398,bs);
        }
        
        //Draws the terrain stars for the attacker
        if(attacker.getMoveType() != attacker.MOVE_AIR)
            for(int i = 0; i< (int)(attacker.getArmy().getCO().getTerrainDefenseMultiplier()*attacker.getArmy().getBattle().getMap().find(attacker).getTerrain().getDef()); i++) {
            g.drawImage(MiscGraphics.getSmallStar(5),115+i*7,90,bs);
            }
        //Draws the terrain stars for the defender
        if(defender.getMoveType() != attacker.MOVE_AIR)
            for(int i = 0; i< (int)(defender.getArmy().getCO().getTerrainDefenseMultiplier()*defender.getArmy().getBattle().getMap().find(defender).getTerrain().getDef()); i++) {
            g.drawImage(MiscGraphics.getSmallStar(5),115+127+4+i*7,90,bs);
            }
        
        
        //clips the attacker to the specific window, but only if the intro isn't done.
        if(tick >=60)
            g.setClip(110,84, 127, 192);
        int whatnot;
        if(tick<190) {
            whatnot = (attacker.getHP() +19 + countDmg)/20;
        } else {
            whatnot = (attacker.getHP()+19)/20;
        }
        while(whatnot > 0) {
            tick-=whatnot;
            if(attacker.getMaxRange() > 1) {
                //if the missile is 'fired', draw the misile UNDER the attacker
                if(tick >= 128) {
                    g.drawImage(BattleGraphics.getMissile(),-40+120+(whatnot*70)%100+28+(tick-128)*12, 220-whatnot*17+14-(tick-128)*6,bs);
                }
                
                if (tick < 128 ) {
                    //static
                    g.drawImage(BattleGraphics.getImage(attacker),-40+120+(whatnot*70)%100,220-whatnot*17,-40+100+120+(whatnot*70)%100,220+70-whatnot*17,0,0,100,70,bs);
                } else if (tick >= 128 & tick <132) {
                    //firing frame 1
                    g.drawImage(BattleGraphics.getImage(attacker),-40+120+(whatnot*70)%100,220-whatnot*17,-40+100+120+(whatnot*70)%100,220+70-whatnot*17,100,0,200,70,bs);
                    g.drawImage(BattleGraphics.getSmoke(),-40+120+(whatnot*70)%100,220-whatnot*17+3,-40+100+120+(whatnot*70)%100,220+70-whatnot*17+3,100*((tick-128)/2),0,100*((tick-126)/2),70,bs);
                } else if (tick >= 132 & tick <136) {
                    //firing frame 2
                    g.drawImage(BattleGraphics.getImage(attacker),-40+120+(whatnot*70)%100,220-whatnot*17,-40+100+120+(whatnot*70)%100,220+70-whatnot*17,200,0,300,70,bs);
                    g.drawImage(BattleGraphics.getSmoke(),-40+120+(whatnot*70)%100,220-whatnot*17+3,-40+100+120+(whatnot*70)%100,220+70-whatnot*17+3,100*((tick-128)/2),0,100*((tick-126)/2),70,bs);
                } else {
                    g.drawImage(BattleGraphics.getImage(attacker),-40+120+(whatnot*70)%100,220-whatnot*17,-40+100+120+(whatnot*70)%100,220+70-whatnot*17,200,0,300,70,bs);
                }
                
            } else {
                if (tick < 120 ) {
                    // 100 x 70
                    // frame 1 is static
                    // 2-5 is moving in
                    // 6-10 is stopping, but 5 must link to 6
                    // 11-16 are firing frames
                    //Breakdown! -40 = starting location, which is then adjusted by "tick". Whatnot is the unit being drawn (draws backwards for layerin) and the modulus is for a staggered effect.
                    g.drawImage(BattleGraphics.getImage(attacker),-40+tick+(whatnot*70)%100,220-whatnot*17,-40+100+tick+(whatnot*70)%100,220+70-whatnot*17,(tick%4 +1)*100,0,(tick%4+2)*100,70,bs);
                } else if (tick >= 120 && tick <128) { //move in
                    g.drawImage(BattleGraphics.getImage(attacker),-40+120+(whatnot*70)%100,220-whatnot*17,-40+100+120+(whatnot*70)%100,220+70-whatnot*17,(int)((tick/8.0*5)%5 +5)*100,0,(int)((tick/8.0*5)%5 +6)*100,70,bs);
                } else if (tick >= 128 & tick <139) {//stop
                    g.drawImage(BattleGraphics.getImage(attacker),-40+120+(whatnot*70)%100,220-whatnot*17,-40+100+120+(whatnot*70)%100,220+70-whatnot*17,(int)((tick/11.0*7+4)%7+8)*100,0,(int)((tick/11.0*7+4)%7+9)*100,70,bs);
                } else if (tick >= 139) { //fire
                    g.drawImage(BattleGraphics.getImage(attacker),-40+120+(whatnot*70)%100,220-whatnot*17,-40+100+120+(whatnot*70)%100,220+70-whatnot*17,0,0,100,70,bs);
                }
            }
            tick+=whatnot;
            whatnot--;
        }
        //If the tick is more than 60, use the correct clipping mask
        if(tick >=60) {
            g.setClip(null);
            g.setClip(127+5+110,84, 127, 192);
            //The next block draws hte impacting missiles
            if(attacker.getMaxRange() > 1 && tick > 146 && tick <=155) {
                whatnot = (attacker.getHP()+19)/20;
                while(whatnot > 0) {
                    tick-=whatnot;
                    g.drawImage(BattleGraphics.getMissile(),127-40+120+(whatnot*70)%100+28+(tick-155)*12, 220-whatnot*17+14+(tick-155)*6,127-40+120+(whatnot*70)%100+28+(tick-155)*12+BattleGraphics.getMissile().getWidth(bs), 220-whatnot*17+14+(tick-155)*6+BattleGraphics.getMissile().getHeight(bs),0,BattleGraphics.getMissile().getHeight(bs),BattleGraphics.getMissile().getWidth(bs),0,bs);
                    tick+=whatnot;
                    whatnot--;
                }
            }
        }
        //new window, first draws the static animation
        if((tick < 155)) {
            whatnot = (defender.getHP()+19 + atkDmg)/20;
            while(whatnot > 0) {
                //static
                g.drawImage(BattleGraphics.getImage(defender),127-40+100+120+(whatnot*70)%100,220-whatnot*17,127-40+120+(whatnot*70)%100,220+70-whatnot*17,0,0,100,70,bs);
                whatnot--;
            }
        } else{
            //draws the amount of units remaining
            whatnot = (defender.getHP()+19)/20;
            while(whatnot > 0) {
                tick-=whatnot;
                if (tick >= 155 && tick <166 && BaseDMG.find(defender,attacker, b.getBattleOptions().isBalance()) != -1 && defender.getMaxRange() == 1 && attacker.getMaxRange() == 1) {
                    //shooting
                    g.drawImage(BattleGraphics.getImage(defender),127-40+100+120+(whatnot*70)%100,220-whatnot*17,127-40+120+(whatnot*70)%100,220+70-whatnot*17,(int)(((tick-1)/11.0*7)%7+9)*100,0,(int)(((tick-1)/11.0*7)%7+10)*100,70,bs);
                    tick+=whatnot;
                    whatnot--;
                }else{
                    // static
                    g.drawImage(BattleGraphics.getImage(defender),127-40+100+120+(whatnot*70)%100,220-whatnot*17,127-40+120+(whatnot*70)%100,220+70-whatnot*17,0,0,100,70,bs);
                    tick+=whatnot;
                    whatnot--;
                }
                
            }
        }
        
        
        g.setClip(null);
        //draws the defender
    }
    public void setup(){
        b.animlock = true;
        
        Listener animationTerminator = new Listener(this);
        timer = new Timer(20, animationTerminator);
        timer.addActionListener(new unlockListener());
        timer.setInitialDelay(delay*20 + 20);
        timer.setCoalesce(false);
        b.queue.add(this);
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
            timer.removeActionListener(listeners[i]);
        }
        timer.setRepeats(false);
    }
    
    
    public void start() {
        
        timer.start();
    }
    //This timer handles the addition and removal of animations
    private class Listener implements ActionListener{
        Animation animation;
        public Listener(Animation a) {
            animation = a;
            tick = 0;
            store = 0;
        }
        public void actionPerformed(ActionEvent evt) {
            tick++;
            if(trigger == false) {
                b.getLayerOne().add(animation);
                b.queue.remove(this);
                trigger = true;
            }
            if(tick>duration) {
                b.getLayerOne().remove(animation);
                BattleGraphics.getImage(attacker).flush();
                BattleGraphics.getImage(defender).flush();
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
