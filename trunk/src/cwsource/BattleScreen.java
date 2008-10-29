package cwsource;
/*
 *BattleScreen.java
 *Author: Urusan
 *Contributors: Adam Dziuk
 *Creation: July 11, 2006, 7:57 AM
 *The Battle Screen, the central graphical component of CW during a battle
 */

import java.awt.*;
import javax.swing.*;
import java.io.*;
import java.util.ArrayList;

import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.color.*;
import java.awt.event.ComponentListener;
import javax.swing.event.MouseInputListener;
//import javax.swing.filechooser.FileNameExtensionFilter;

public class BattleScreen extends JComponent implements ComponentListener{
    private int DEF_TILEW = 30;   //BattleScreen width was 16 tiles
    private int DEF_TILEH = 20;   //BattleScreen height was 12 tiles
    private int MAX_TILEW = DEF_TILEW;   //Screen width in tiles
    private int MAX_TILEH = DEF_TILEH;   //Screen height in tiles
    
    private Map m;              //holds the map being displayed
    private Unit selected;      //holds the currently selected unit
    private int cx;             //holds the cursor's x position on the map (in map tiles)
    private int cy;             //holds the cursor's y position on the map (in map tiles)
    private int sx;             //holds the battle screen's x position over the map (in pixels)
    private int sy;             //holds the battle screen's y position over the map (in pixels)
    private int item;           //holds the menu's current item (both menus use this)
    private boolean menu;       //is the main menu in use?
    private boolean cmenu;      //is the context menu in use?
    private boolean move;       //is the game in move mode?
    private boolean fire;       //is the game in fire mode?
    private boolean bmenu;      //is the build menu in use?
    private boolean silo;       //is a silo in use?
    private boolean special1;
    private boolean special2;
    private boolean repair;     //is a black boat in repair mode?
    private boolean fireRange;  //is the firing range being checked?
    private boolean minimap;    //is the minimap on?
    private boolean daystart;   //is the day start screen up?
    private boolean denyContinue;//is the player allowed to bypass the day start screen?
    private boolean victory;    //is the victory screen on?
    private boolean endStats;   //is the Ending Statistics screen on?
    private boolean intel;      //is the Intel screen on?
    private boolean replay;     //is replay mode on?
    private boolean delete;
    private int statArmyIndex = 0;//which army should the stats be shown for?
    private int statType = 0;   //which kind of statistics should we display? (0=general 1=units built 2=units lost)
    private Battle b;           //holds the battle (Army List and DTD Mechanics)
    private InGameMenu currentMenu = null; //contains the active menu (null if no menus are active)
    private BufferedImage bimg; //the screen, used for double buffering and scaling
    private int scale;          //what scale multiplier is being used
    private JFrame parentFrame;  //the frame that contains the window
    private Location originalLocation;  //the pre-move position of the unit
    private int originalcp;             //the pre-move capture points
    private Location newLocation;       //the new location of the unit (used by join and load)
    private KeyControl keycontroller;   //the KeyControl, used to remove the component
    private MouseControl mousecontroller;//the MouseControl, used to remove the component
    private Unit moveTempUnit;          //used in join and load
    private int unload = 0;             //used for unloading 0=not unloading, 1=unloading slot1, 2=unloading slot2
    private boolean backAllowed = true; //is going back allowed?
    private boolean outOfMoveRange = false; //is the cursor out of the move range?
    private int unitCyclePosition = 0;  //the position in the unit cycle (see Key N)
    private int noScroll = 0;                  //don't scroll for this many more mouse commands
    //used to determine if CO info screen is on
    private boolean info, alt;
    private int infono;
    private int skip = 0; //This is used to skip lines of text.
    private int skipMax = 0;
    
    private float a;
    private boolean dialogue;
    
    private ArrayList<Location> contextTargs;
    private int currContextTarg;
    
    /** Creates a new instance of BattleScreen */
    public BattleScreen(Battle b, JFrame f){
        //makes the panel opaque, and thus visible
        this.setOpaque(true);
        
        this.b = b;
        this.m = b.getMap();
        selected = null;
        cx = 0;
        cy = 0;
        sx = 0;
        sy = 0;
        //center small maps
        if(m.getMaxCol() < 30){
            sx = -((30 - m.getMaxCol())/2)*16;
        }
        if(m.getMaxRow() < 20){
            sy = -((20 - m.getMaxRow())/2)*16;
        }        
        item = 0;
        menu = false;
        cmenu = false;
        move = false;
        fire = false;
        silo = false;
        repair = false;
        fireRange = false;
        minimap = false;
        victory = false;
        endStats = false;
        replay = false;
        info = false;
        infono = 0;
        special1 = false;
        special2 = false;
        delete = false;
        scale = 1;
        
        contextTargs = new ArrayList<Location>();
        currContextTarg = 0;
        
        int numArmies = b.getArmies().length;
        for(int i=0; i<numArmies; i++){
            b.cursorLocation[i] = new Location(0,0);
            Property props[] = b.getArmy(i).getProperties();
            if(props!=null){
                for(int k=0; k < props.length; k++){
                    if(props[k].getIndex()==9){
                        b.cursorLocation[i] = props[k].getTile().getLocation();
                    }
                }
            }
        }
        
        moveCursorTo(b.cursorLocation[0]);
        
        //KeyControl is registered with the parent frame
        keycontroller = new KeyControl(this);
        f.addKeyListener(keycontroller);
        mousecontroller = new MouseControl();
        f.addMouseListener(mousecontroller);
        f.addMouseMotionListener(mousecontroller);
        
        parentFrame = f;
        this.addComponentListener(this);
        
        //turn on turn start display for first turn
        daystart = true;
        denyContinue = false;
    }
    
    //called in response to this.repaint();
    public void paintComponent(Graphics g) {
        //clears the background
        super.paintComponent(g);
        
        //converts to Graphics2D
        Dimension d = getSize();
        Graphics2D g2 = createGraphics2D(d.width, d.height);
        g2.scale(scale,scale);
        
        drawScreen(g2);
        g2.dispose();
        g.drawImage(bimg, 0, 0, this);
    }
    
    //tells the GUI what size the window is
    public Dimension getPreferredSize(){
        return new Dimension(16*MAX_TILEW*scale,16*MAX_TILEH*scale);
    }
    
    //makes a Graphics2D object of the given size
    public Graphics2D createGraphics2D(int w, int h) {
        Graphics2D g2 = null;
        if (bimg == null || bimg.getWidth() != w || bimg.getHeight() != h) {
            bimg = (BufferedImage) createImage(w, h);
        }
        g2 = bimg.createGraphics();
        g2.setBackground(getBackground());
        g2.clearRect(0, 0, w, h);
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING,
                RenderingHints.VALUE_RENDER_QUALITY);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        return g2;
    }
    
    //Draws the screen
    public void drawScreen(Graphics2D g){
        //draws an animated gif in the background
        //this triggers repaint automatically
        //using repaint normally ruins animations
        g.drawImage(MiscGraphics.getMoveTile(),0,0,this);
        
        //draw a black background (if the map is smaller than the screen)
        g.setColor(Color.DARK_GRAY);
        g.fillRect(0,0,480,320);
        if(Options.battleBackground){
            g.drawImage(MiscGraphics.getBattleBackground(),0,0,this);
        }
        
        drawMap(g);
        if(b.isFog() || b.isMist())drawFog(g);
        if(move){
            drawMoveRange(g);
            drawPath(g);
        }
        if(fire)drawFireRange(g);
        if(fireRange)drawDisplayFireRange(g);
        if(unload > 0)drawUnloadRange(g);
        if(repair)drawRepairRange(g);
        if(special1)drawSpecial1Range(g);
        if(special2)drawSpecial2Range(g);
        if(!silo)drawCursor(g);
        else drawSiloCursor(g);
        
        drawWeather(g);
        
        drawCOBar(g);
        drawInfoBox(g);
        drawAnimations(g);
        
        if((menu||cmenu||bmenu) && !b.animlock)currentMenu.drawMenu(g);
        
        if(minimap)drawMiniMap(g, 0, 0);
        if(daystart)drawDayStartScreen(g);
        if(intel)drawIntelScreen(g);
        if(info)drawInfoScreen(g); //Info
        
        if(victory)drawVictoryScreen(g);
        if(endStats)drawEndStats(g);
        
        //causes problems with animated gifs
        //this.repaint();
    }
    
    public void drawAnimations(Graphics2D g){
        //hey, draw that in reverse order!
        if(!b.diagQueue.isEmpty()) {
            if(!b.diagQueue.get(0).timer.isRunning()) {
                b.diagQueue.get(0).start();
            } else {
                b.diagQueue.get(0).draw(g,this);
            }
        }
        
        for(int i = 0; i<b.getLayerFour().size(); i++) {
            ((Animation)b.getLayerFour().get(i)).draw(g,this);
        }
        for(int i = 0; i<b.getLayerThree().size(); i++) {
            ((Animation)b.getLayerThree().get(i)).draw(g,this);
        }
        for(int i = 0; i<b.getLayerTwo().size(); i++) {
            ((Animation)b.getLayerTwo().get(i)).draw(g,this);
        }
        for(int i = 0; i<b.getLayerOne().size(); i++) {
            ((Animation)b.getLayerOne().get(i)).draw(g,this);
        }
        
        g.setComposite(AlphaComposite.SrcOver);
    }
    //Draws the map and the units
    public void drawMap(Graphics2D g){
        Image temp;
        Terrain ter;
        //System.out.println(sx+","+sy);
        //only print onscreen tiles
        for(int x=sx/16;x<m.getMaxCol();x++){
            if(x>=sx/16+MAX_TILEW)break;
            for(int y=sy/16;y<m.getMaxRow();y++){
                if(y>=sy/16+MAX_TILEH)break;
                //draw terrain
                if(x < 0 || y < 0)continue;
                ter = m.find(new Location(x,y)).getTerrain();
                
                if(ter instanceof Property ) {
                    if(((Property)ter).owner == null || (b.isFog() && ter instanceof Property && b.getFog(x,y)))
                        temp = TerrainGraphics.getUrbanSpriteSheet(0);
                    else
                        temp = TerrainGraphics.getUrbanSpriteSheet(((Property)ter).owner.getColor()+1);
                    if(b.isFog() && ter instanceof Property && b.getFog(x,y) && ter.getIndex()!=9 && ter.getIndex() != 16 && !(ter instanceof HQ)){
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getIndex()-7)*16, 0, (ter.getIndex()-6)*16, 32, this);
                    } else if(ter instanceof Pipestation) {
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, 8*16, 0, 9*16, 32, this);
                    } else if(ter instanceof HQ){
                        temp = TerrainGraphics.getHQSpriteSheet(((Property)ter).owner.getColor()+1);
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (((Property)ter).owner.getCO().getStyle())*16, 0, (((Property)ter).owner.getCO().getStyle()+1)*16, 32, this);
                    } else if (ter instanceof Silo){
                        if(((Silo)ter).launched)
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, 1*16, 0, 2*16, 32, this);
                        else
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, 0*16, 0, 1*16, 32, this);
                    }else{
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getIndex()-7)*16, 0, (ter.getIndex()-6)*16, 32, this);
                    }
                } else {
                    temp = TerrainGraphics.getTerrainSpriteSheet();
                    if(ter.index<3) { //Plain, Wood, Mountain
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getIndex())*16, 0, (ter.getIndex()+1)*16, 32, this);
                    } else if(ter.index == 3){ //Road
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+3)*16, 0, (ter.getStyle()+4)*16, 32, this);
                    } else if(ter.index == 4){ //Bridge
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+14)*16, 0, (ter.getStyle()+15)*16, 32, this);
                    }
                    //Put suspension code here
                    //g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+16)*16, 0, (ter.getStyle()+17)*16, 32, this);
                    else if(ter.index == 5){ //River
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+18)*16, 0, (ter.getStyle()+19)*16, 32, this);
                    }
                    else if (ter.index == 8){ //Shoal
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+33)*16, 0, (ter.getStyle()+34)*16, 32, this);
                    }
                    else if (ter.index == 6){ //Sea
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+72)*16, 0, (ter.getStyle()+73)*16, 32, this);
                    }
                    else if (ter.index == 7){ //Reef
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (71)*16, 0, (72)*16, 32, this);
                    }
                    else if (ter.index == 15){ //Pipe
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+103)*16, 0, (ter.getStyle()+104)*16, 32, this);
                    }
                    else if (ter.index == 18){ //PipeSeam
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+118)*16, 0, (ter.getStyle()+119)*16, 32, this);
                    }
                    else if (ter.index == 19){ //Destroyed Seam
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+120)*16, 0, (ter.getStyle()+121)*16, 32, this);
                    }
                }
                
                //if a unit is there, draw it
                //Uh sorry about the big mess guys =[
                if((m.find(new Location(x,y)).getUnit()!=null && 
                	(!m.find(new Location(x,y)).getUnit().isHidden() || 
                	 m.find(new Location(x,y)).getUnit().getArmy().getSide() == b.getArmy(b.getTurn()).getSide()) && 
                	 !m.find(new Location(x,y)).getUnit().moving) ||
                	 (b.isMist() && 
                	  m.find(new Location(x,y)).getUnit() != null && 
                	  !m.find(new Location(x,y)).getUnit().isDived()) &&
                	  !m.find(new Location(x,y)).getUnit().moving)
                {
                	 
                          //Spacing issues I can't bother to fix at the moment. <_<
                    /*if(m.find(new Location(x,y)).getUnit() == selected && selected.direction != -1 && selected.getMType() >1)
                    {
                        switch(selected.direction) {
                            case 0: //north
                                g.drawImage(UnitGraphics.getNorthImage(m.find(new Location(x,y)).getUnit()),x*16-sx-1,y*16-sy,x*16-sx+16-1+8,y*16-sy+16+8,0,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24,30,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24+24,this);
                                
                                break;
                            case 1: //east
                                g.drawImage(UnitGraphics.getEastImage(m.find(new Location(x,y)).getUnit()),x*16-sx,y*16-sy-3,x*16-sx+16+8,y*16-sy+16-3+8,0,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24,30,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24+24,this);
                                
                                break;
                            case 2: //south
                                g.drawImage(UnitGraphics.getSouthImage(m.find(new Location(x,y)).getUnit()),x*16-sx,y*16-sy,x*16-sx+16+8,y*16-sy+16+8,0,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24,30,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24+24,this);
                                
                                break;
                            case 3: //west
                                g.drawImage(UnitGraphics.getWestImage(m.find(new Location(x,y)).getUnit()),x*16-sx,y*16-sy-3,x*16-sx+16+8,y*16-sy+16-3+8,0,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24,30,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())/16*24+24,this);
                                
                                break;
                        }
                    }*/
                    g.drawImage(UnitGraphics.getUnitImage(m.find(new Location(x,y)).getUnit()),x*16-sx,y*16-sy,x*16-sx+16,y*16-sy+16,0,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit()),16,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())+16,this);
                    //darken the unit if inactive
                    if(!m.find(new Location(x,y)).getUnit().isActive()){
                        if(m.find(new Location(x,y)).getUnit().getArmy() == b.getArmy(b.getTurn())){
                            g.setColor(Color.black);
                            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                            //g.drawImage(UnitGraphics.getDarkUnit(m.find(new Location(x,y)).getUnit()),x*16-sx,y*16-sy,this);
                            g.fillRect(x*16-sx,y*16-sy,16,16);
                            g.setComposite(AlphaComposite.SrcOver);
                        }
                    }
                    
                    //draw the low ammo icon if ammo is low
                    if(m.find(new Location(x,y)).getUnit().isLowOnAmmo()){
                        g.drawImage(MiscGraphics.getLowAmmoIcon(),x*16-sx,y*16-sy+8,this);
                    }
                    
                    //draw the low fuel icon if fuel is low
                    if(m.find(new Location(x,y)).getUnit().isLowOnFuel()){
                        g.drawImage(MiscGraphics.getLowFuelIcon(),x*16-sx,y*16-sy+8,this);
                    }
                    
                    //draw the capture icon if capturing
                    if(m.find(new Location(x,y)).getTerrain() instanceof Property){
                        Property prop = (Property)m.find(new Location(x,y)).getTerrain();
                        if(prop.getCapturePoints() < prop.getMaxCapturePoints())
                            g.drawImage(MiscGraphics.getCaptureIcon(),x*16-sx,y*16-sy+8,this);
                    }
                    
                    //draw the dived icon if diving
                    if(m.find(new Location(x,y)).getUnit().isDived()){
                        g.drawImage(MiscGraphics.getDiveIcon(),x*16-sx,y*16-sy+8,this);
                    }
                    
                    //draw the the load icon if a transport with units
                    if(m.find(new Location(x,y)).getUnit() instanceof Transport){
                        Transport trans = (Transport)m.find(new Location(x,y)).getUnit();
                        if(trans.getUnitsCarried() > 0)
                            g.drawImage(MiscGraphics.getLoadIcon(),x*16-sx,y*16-sy+8,this);
                    }
                    
                    //draw HP if health is not perfect
                    //draw ? HP if
                    //
                    // (a) it is an enemy's turn and the Unit's CO has hidden HP
                    //
                    //  -or-
                    //
                    // (b) any one enemy has hideAllHP on and it is not his turn
                    //
                    
                    boolean hideTheHP = false;
                    
                    hideTheHP = assessHideTheHP();
                    
                    if(!hideTheHP && m.find(new Location(x,y)).getUnit().getDisplayHP()!=10 && (!m.find(new Location(x,y)).getUnit().getArmy().getCO().hiddenHP || b.getArmy(b.getTurn()) == m.find(new Location(x,y)).getUnit().getArmy()))
                    {
                    	/*
                    	//Check if exact HP should be drawn
                    	if(b.getArmy(b.getTurn()).getCO().seeFullHP)
                    	{
                    		int tensDigit = m.find(new Location(x,y)).getUnit().getHP() / 10;
                    		int onesDigit = m.find(new Location(x,y)).getUnit().getHP() % 10;

                    		g.drawImage(MiscGraphics.getHpDisplay(tensDigit),x*16-sx+8,y*16-sy+3,this);
                    		g.drawImage(MiscGraphics.getHpDisplay(onesDigit),x*16-sx+8,y*16-sy+9,this);
                    	}
                    	else
                    	{
                    		g.drawImage(MiscGraphics.getHpDisplay(m.find(new Location(x,y)).getUnit().getDisplayHP()),x*16-sx+8,y*16-sy+9,this);
                    	}
                    	*/
                    	
                    	g.drawImage(MiscGraphics.getHpDisplay(m.find(new Location(x,y)).getUnit().getDisplayHP()),x*16-sx+8,y*16-sy+9,this);
                    } 
                    else if(hideTheHP || m.find(new Location(x,y)).getUnit().getArmy().getCO().hiddenHP && (m.find(new Location(x,y)).getUnit().getDisplayHP()!=10 || b.getArmy(b.getTurn()).getSide() != m.find(new Location(x,y)).getUnit().getArmy().getSide()))
                    {
                        g.drawImage(MiscGraphics.getHiddenHP(),x*16-sx+8,y*16-sy+9,this);
                    }
                    /*if(m.find(new Location(x,y)).getUnit().getDisplayHP()!=10){
                        g.drawImage(MiscGraphics.getHpDisplay(m.find(new Location(x,y)).getUnit().getDisplayHP()),x*16-sx+8,y*16-sy+9,this);
                    }*/
                    
                    //draw HP if health is not perfect
                    /*if(m.find(new Location(x,y)).getUnit().getDisplayHP()!=10 && (!m.find(new Location(x,y)).getUnit().getArmy().getCO().hiddenHP || b.getArmy(b.getTurn()) == m.find(new Location(x,y)).getUnit().getArmy())){
                        g.drawImage(MiscGraphics.getHpDisplay(m.find(new Location(x,y)).getUnit().getDisplayHP()),x*16-sx+8,y*16-sy+9,this);
                    } else if (m.find(new Location(x,y)).getUnit().getArmy().getCO().hiddenHP && (m.find(new Location(x,y)).getUnit().getDisplayHP()!=10 || b.getArmy(b.getTurn()).getSide() != m.find(new Location(x,y)).getUnit().getArmy().getSide())){
                        g.drawImage(MiscGraphics.getHiddenHP(),x*16-sx+8,y*16-sy+9,this);
                    }*/
                    /*if(m.find(new Location(x,y)).getUnit().getDisplayHP()!=10){
                        g.drawImage(MiscGraphics.getHpDisplay(m.find(new Location(x,y)).getUnit().getDisplayHP()),x*16-sx+8,y*16-sy+9,this);
                    }*/
                    
                }
            }
        }
    }

	public boolean assessHideTheHP() 
	{
		Army[] allArmies = b.getArmies();                    
		if(allArmies != null)
		{
			for(int i = 0; i < allArmies.length; i++)
			{
				Army pickArmy = allArmies[i];
				
				if(pickArmy != null && pickArmy.getSide() != b.getArmy(b.getTurn()).getSide())
				{
					CO currCO = pickArmy.getCO();
					
					if(currCO != null)
					{
						if(currCO.hideAllHP)
						{
							/*
							hideTheHP = true;
							i = allArmies.length + 1;
							*/
							
							return true;
						}
					}
				}
			}
		}
		//return hideTheHP;
		
		return false;
	}
    
    //draws the cursor
    public void drawCursor(Graphics2D g){
        //g.setColor(Color.red);
        //g.drawRect(cx*16-sx,cy*16-sy,16,16);
        
        //Check to see if the aiming cursor should be drawn
        if(b.getArmy(b.getTurn()).getCO().isSelecting()) {
            g.drawImage(MiscGraphics.getAimCursor(),cx*16-sx-7,cy*16-sy-7,this);
        } else {
            //Makes sure that the aiming cursor is set to its normal cursor
            MiscGraphics.restoreAimCursor();
            g.drawImage(MiscGraphics.getCursor(),cx*16-sx-7,cy*16-sy-7,this);
        }
        //g.drawImage(MiscGraphics.getCursor(),cx*16-sx-16,cy*16-sy-16,this);
    }
    
    //draws the cursor
    public void drawSiloCursor(Graphics2D g){
        g.drawImage(MiscGraphics.getSiloCursor(),cx*16-sx-32,cy*16-sy-32,this);
    }
    
    public void drawPath(Graphics2D g){
        int[] path = selected.getPath().getItems();
        if(path != null){
            //g.setColor(Color.red);
            int x = selected.getLocation().getCol();
            int y = selected.getLocation().getRow();
            
            if(path.length != 0){
                //start
                //if(path[0]==1 || path[0]==3)g.drawImage(MiscGraphics.getArrow(0),x*16-sx,y*16-sy,this);
                //else g.drawImage(MiscGraphics.getArrow(1),x*16-sx,y*16-sy,this);
                if(path[0] == 0)y--;
                else if(path[0] == 1)x++;
                else if(path[0] == 2)y++;
                else if(path[0] == 3)x--;
                int olddir = path[0];
                
                //middle
                for(int i=1; i<path.length; i++){
                    int index = 0;
                    if(olddir==0){
                        if(path[i]==0)index = 1;
                        if(path[i]==1)index = 3;
                        if(path[i]==3)index = 4;
                    }else if(olddir==1){
                        if(path[i]==0)index = 5;
                        if(path[i]==1)index = 0;
                        if(path[i]==2)index = 4;
                    }else if(olddir==2){
                        if(path[i]==1)index = 2;
                        if(path[i]==2)index = 1;
                        if(path[i]==3)index = 5;
                    }else if(olddir==3){
                        if(path[i]==0)index = 2;
                        if(path[i]==2)index = 3;
                        if(path[i]==3)index = 0;
                    }
                    
                    g.drawImage(MiscGraphics.getArrow(index),x*16-sx,y*16-sy,this);
                    if(path[i] == 0)y--;
                    else if(path[i] == 1)x++;
                    else if(path[i] == 2)y++;
                    else if(path[i] == 3)x--;
                    olddir = path[i];
                }
                
                //end
                if(olddir == 0)g.drawImage(MiscGraphics.getArrow(6),x*16-sx,y*16-sy,this);
                else if(olddir == 1)g.drawImage(MiscGraphics.getArrow(7),x*16-sx,y*16-sy,this);
                else if(olddir == 2)g.drawImage(MiscGraphics.getArrow(8),x*16-sx,y*16-sy,this);
                else if(olddir == 3)g.drawImage(MiscGraphics.getArrow(9),x*16-sx,y*16-sy,this);
                
            }
        }
    }
    
    public void drawInfoBox(Graphics2D g) {
        Tile currTile = m.find(new Location(cx,cy));
        
        boolean hideTheHP = assessHideTheHP();
        
        //Check for drawing on right side
        if(cx < (MAX_TILEW/2)+(sx/16)) {
            //Terrain box
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            g.setColor(new Color(5,46,68));
            g.fillRect(16*MAX_TILEW-32,16*MAX_TILEH-56,32,56);
            g.setComposite(AlphaComposite.SrcOver);
            
            Image temp = TerrainGraphics.getTerrainImage(currTile.getTerrain());
            Terrain ter = currTile.getTerrain();
            if(ter instanceof Property ) {
                if(((Property)ter).owner == null || (b.isFog() && ter instanceof Property && b.getFog(currTile.getLocation().getCol(),currTile.getLocation().getRow())))
                    temp = TerrainGraphics.getUrbanSpriteSheet(0);
                else
                    temp = TerrainGraphics.getUrbanSpriteSheet(((Property)ter).owner.getColor()+1);
                if(b.isFog() && ter instanceof Property && b.getFog(currTile.getLocation().getCol(),currTile.getLocation().getRow()) && ter.getIndex()!=9 && ter.getIndex() != 16 && !(ter instanceof HQ)){
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getIndex()-7)*16, 0, (ter.getIndex()-6)*16, 32, this);
                } else if(ter instanceof Pipestation) {
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, 8*16, 0, 9*16, 32, this);
                } else if(ter instanceof HQ){
                    temp = TerrainGraphics.getHQSpriteSheet(((Property)ter).owner.getColor()+1);
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (((Property)ter).owner.getCO().getStyle())*16, 0, (((Property)ter).owner.getCO().getStyle()+1)*16, 32, this);
                } else if (ter instanceof Silo){
                    if(((Silo)ter).launched)
                        g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, 1*16, 0, 2*16, 32, this);
                    else
                        g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, 0*16, 0, 1*16, 32, this);
                }else{
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getIndex()-7)*16, 0, (ter.getIndex()-6)*16, 32, this);
                }
            } else {
                temp = TerrainGraphics.getTerrainSpriteSheet();
                if(ter.index<3) { //Plain, Wood, Mountain
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getIndex())*16, 0, (ter.getIndex()+1)*16, 32, this);
                } else if(ter.index == 3){ //Road
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+3)*16, 0, (ter.getStyle()+4)*16, 32, this);
                } else if(ter.index == 4){ //Bridge
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+14)*16, 0, (ter.getStyle()+15)*16, 32, this);
                }
                //Put suspension code here
                //g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+16)*16, 0, (ter.getStyle()+17)*16, 32, this);
                else if(ter.index == 5){ //River
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+18)*16, 0, (ter.getStyle()+19)*16, 32, this);
                } else if (ter.index == 8){ //Shoal
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+33)*16, 0, (ter.getStyle()+34)*16, 32, this);
                } else if (ter.index == 6){ //Sea
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+72)*16, 0, (ter.getStyle()+73)*16, 32, this);
                } else if (ter.index == 7){ //Reef
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (71)*16, 0, (72)*16, 32, this);
                } else if (ter.index == 15){ //Pipe
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+103)*16, 0, (ter.getStyle()+104)*16, 32, this);
                } else if (ter.index == 18){ //PipeSeam
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+118)*16, 0, (ter.getStyle()+119)*16, 32, this);
                } else if (ter.index == 19){ //Destroyed Seam
                    g.drawImage(temp, 16*MAX_TILEW-24, 16*MAX_TILEH-46-16, 16*MAX_TILEW-24+16, 16*MAX_TILEH-30, (ter.getStyle()+120)*16, 0, (ter.getStyle()+121)*16, 32, this);
                }
            }


            //g.drawImage(temp,16*MAX_TILEW-24,16*MAX_TILEH-46-temp.getHeight(this)+16,this);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.drawString(currTile.getTerrain().getName(),16*MAX_TILEW-32,16*MAX_TILEH-46);
            
            if(currTile.getTerrain() instanceof Invention) {
                Invention inv = (Invention)currTile.getTerrain();
                g.drawString("" + inv.getHP(),16*MAX_TILEW-32,16*MAX_TILEH-20);
                
                if(fire && /*(u.getArmy().getSide() != selected.getArmy().getSide()) &&*/ selected.damageCalc(inv) > -1) {
                    g.setColor(Color.red);
                    g.fillRect(16*MAX_TILEW-32,16*MAX_TILEH-72,32,16);
                    
                    g.setColor(Color.white);
                    g.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g.drawString("" + selected.damageCalc(inv),16*MAX_TILEW-32,16*MAX_TILEH-56);
                }
            } else {
                //defense stars
                g.drawImage(MiscGraphics.getSmallStar(5),16*MAX_TILEW-32,16*MAX_TILEH-28,this);
                g.drawString("" + currTile.getTerrain().getDef(),16*MAX_TILEW-22,16*MAX_TILEH-20);
                
                //capture points
                if(currTile.getTerrain() instanceof Property) {
                    g.drawImage(MiscGraphics.getCaptureIcon(),16*MAX_TILEW-32,16*MAX_TILEH-18,this);
                    g.drawString("" + ((Property)currTile.getTerrain()).getCapturePoints(),16*MAX_TILEW-22,16*MAX_TILEH-10);
                }
            }
            
            
            //Unit box
            if(currTile.getUnit()!=null && 
               (!m.find(new Location(cx,cy)).getUnit().isHidden() || 
               m.find(new Location(cx,cy)).getUnit().getArmy().getSide() == b.getArmy(b.getTurn()).getSide())  ||
          	   (b.isMist() && 
               m.find(new Location(cx,cy)).getUnit() != null && 
               !m.find(new Location(cx,cy)).getUnit().isDived()))
            {
                Unit tileUnit = currTile.getUnit();
                g.setColor(Color.black);
                g.fillRect(16*MAX_TILEW-64,16*MAX_TILEH-56,32,56);
                
                g.setColor(Color.white);
                
                if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                    g.drawString("?????",16*MAX_TILEW-64,16*MAX_TILEH-46);
                } else {
                    g.drawString(tileUnit.getName(),16*MAX_TILEW-64,16*MAX_TILEH-46);
                }
                
                //g.drawImage(UnitGraphics.getUnitImage(u),16*MAX_TILEW-56,16*MAX_TILEH-46,this);
                
                //This line is used for drawing normal units
                g.drawImage(UnitGraphics.getUnitImage(tileUnit),16*MAX_TILEW-56,16*MAX_TILEH-46,16*MAX_TILEW-56+16,16*MAX_TILEH-46+16,0,UnitGraphics.findYPosition(tileUnit),16,UnitGraphics.findYPosition(tileUnit)+16,this);
                
                //This is when the unit's HP is drawn
                g.drawImage(MiscGraphics.getSmallHeart(),16*MAX_TILEW-63,16*MAX_TILEH-26, this);
                if(hideTheHP || tileUnit.getArmy().getCO().hiddenHP && b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide()) 
                {
                    g.drawString("?",16*MAX_TILEW-54,16*MAX_TILEH-20);
                } 
                else 
                {
                	if(b.getArmy(b.getTurn()).getCO().seeFullHP)
                	{
                		String onesDigit = "" + (tileUnit.getHP() % 10);
                		String tensDigit = "" + (tileUnit.getHP() / 10);
                		
                		g.drawString(tensDigit + "." + onesDigit,16*MAX_TILEW-54,16*MAX_TILEH-20);
                	}
                	else
                	{
                		g.drawString("" + tileUnit.getDisplayHP(),16*MAX_TILEW-54,16*MAX_TILEH-20);
                	}
                }
                
                //This is when the unit's fuel is drawn
                g.drawImage(MiscGraphics.getLowFuelIcon(),16*MAX_TILEW-64,16*MAX_TILEH-18,this);
                if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                    g.drawString("?",16*MAX_TILEW-54,16*MAX_TILEH-10);
                } else {
                    g.drawString("" + tileUnit.getGas(),16*MAX_TILEW-54,16*MAX_TILEH-10);
                }
                
                //This is when the unit's ammo is drawn
                if(tileUnit.getAmmo()!=-1 || (tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide()))) {
                    g.drawImage(MiscGraphics.getLowAmmoIcon(),16*MAX_TILEW-64,16*MAX_TILEH-8,this);
                    if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                        g.drawString("?",16*MAX_TILEW-54,16*MAX_TILEH);
                    } else {
                        g.drawString("" + tileUnit.getAmmo(),16*MAX_TILEW-54,16*MAX_TILEH);
                    }
                }
                
                if(fire && (tileUnit.getArmy().getSide() != selected.getArmy().getSide()) && selected.displayDamageCalc(tileUnit) > -1 && selected.checkFireRange(new Location(cx,cy))){
                    g.setColor(Color.red);
                    g.fillRect(16*MAX_TILEW-64,16*MAX_TILEH-72,32,16);
                    
                    g.setColor(Color.white);
                    g.setFont(new Font("SansSerif", Font.BOLD, 16));
                    //g.drawString(selected.displayDamageCalc(u)+"%",16*MAX_TILEW-64,16*MAX_TILEH-56);
                    
                    //Koshi can get LOL readouts!
                    if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                        g.drawString("LOL",16*MAX_TILEW-64,16*MAX_TILEH-56);
                    } else {
                        g.drawString(selected.displayDamageCalc(tileUnit)+"",16*MAX_TILEW-64,16*MAX_TILEH-56);
                    }
                }
                
                //Transport box
                if(tileUnit instanceof Transport && (!b.isFog() || b.getArmy(b.getTurn()).getSide() == tileUnit.getArmy().getSide())) {
                    Transport trans = (Transport) tileUnit;
                	
                    if((trans.getUnitsCarried()>0) && !trans.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() == tileUnit.getArmy().getSide())) 
                    {
                        g.setColor(Color.black);
                        g.fillRect(16*MAX_TILEW-96,16*MAX_TILEH-56,32,56);
                        
                        if((trans.getUnitsCarried() == 2)) {
                            //This line is used for drawing normal units
                            g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(1)),16*MAX_TILEW-88,16*MAX_TILEH-50,16*MAX_TILEW-88+16,16*MAX_TILEH-50+16,0,UnitGraphics.findYPosition(trans.getUnit(1)),16,UnitGraphics.findYPosition(trans.getUnit(1))+16,this);
                            
                            //This line is used for drawing normal units
                            g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(2)),16*MAX_TILEW-88,16*MAX_TILEH-32,16*MAX_TILEW-88+16,16*MAX_TILEH-32+16,0,UnitGraphics.findYPosition(trans.getUnit(2)),16,UnitGraphics.findYPosition(trans.getUnit(2))+16,this);
                        } else {
                            //This line is used for drawing normal units
                            g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(1)),16*MAX_TILEW-88,16*MAX_TILEH-36,16*MAX_TILEW-88+16,16*MAX_TILEH-36+16,0,UnitGraphics.findYPosition(trans.getUnit(1)),16,UnitGraphics.findYPosition(trans.getUnit(1))+16,this);
                        }
                    }
                }
            }
        }
        //Draw on left side
        else {
            //Terrain box
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.85f));
            g.setColor(new Color(5,46,68));
            g.fillRect(0,16*MAX_TILEH-56,32,56);
            g.setComposite(AlphaComposite.SrcOver);
            Image temp = TerrainGraphics.getTerrainImage(currTile.getTerrain());
            Terrain ter = currTile.getTerrain();
            
            if(ter instanceof Property ) {
                if(((Property)ter).owner == null || (b.isFog() && ter instanceof Property && b.getFog(currTile.getLocation().getCol(),currTile.getLocation().getRow())))
                    temp = TerrainGraphics.getUrbanSpriteSheet(0);
                else
                    temp = TerrainGraphics.getUrbanSpriteSheet(((Property)ter).owner.getColor()+1);
                if(b.isFog() && ter instanceof Property && b.getFog(currTile.getLocation().getCol(),currTile.getLocation().getRow()) && ter.getIndex()!=9 && ter.getIndex() != 16 && !(ter instanceof HQ)){
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getIndex()-7)*16, 0, (ter.getIndex()-6)*16, 32, this);
                } else if(ter instanceof Pipestation) {
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, 8*16, 0, 9*16, 32, this);
                } else if(ter instanceof HQ){
                    temp = TerrainGraphics.getHQSpriteSheet(((Property)ter).owner.getColor()+1);
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (((Property)ter).owner.getCO().getStyle())*16, 0, (((Property)ter).owner.getCO().getStyle()+1)*16, 32, this);
                } else if (ter instanceof Silo){
                    if(((Silo)ter).launched)
                        g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, 1*16, 0, 2*16, 32, this);
                    else
                        g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, 0*16, 0, 1*16, 32, this);
                }else{
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getIndex()-7)*16, 0, (ter.getIndex()-6)*16, 32, this);
                }
            } else {
                temp = TerrainGraphics.getTerrainSpriteSheet();
                if(ter.index<3) { //Plain, Wood, Mountain
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getIndex())*16, 0, (ter.getIndex()+1)*16, 32, this);
                } else if(ter.index == 3){ //Road
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+3)*16, 0, (ter.getStyle()+4)*16, 32, this);
                } else if(ter.index == 4){ //Bridge
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+14)*16, 0, (ter.getStyle()+15)*16, 32, this);
                }
                //Put suspension code here
                //g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+16)*16, 0, (ter.getStyle()+17)*16, 32, this);
                else if(ter.index == 5){ //River
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+18)*16, 0, (ter.getStyle()+19)*16, 32, this);
                } else if (ter.index == 8){ //Shoal
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+33)*16, 0, (ter.getStyle()+34)*16, 32, this);
                } else if (ter.index == 6){ //Sea
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+72)*16, 0, (ter.getStyle()+73)*16, 32, this);
                } else if (ter.index == 7){ //Reef
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (71)*16, 0, (72)*16, 32, this);
                } else if (ter.index == 15){ //Pipe
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+103)*16, 0, (ter.getStyle()+104)*16, 32, this);
                } else if (ter.index == 18){ //PipeSeam
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+118)*16, 0, (ter.getStyle()+119)*16, 32, this);
                } else if (ter.index == 19){ //Destroyed Seam
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 8+16, 16*MAX_TILEH-30, (ter.getStyle()+120)*16, 0, (ter.getStyle()+121)*16, 32, this);
                }
            }
            /*if(ter instanceof Property && ((Property)ter).owner != null) {
                if(b.isFog() && ter instanceof Property && b.getFog(currTile.getLocation().getCol(),currTile.getLocation().getRow()) && ter.getIndex()!=9 && ter.getIndex() != 16 && !(ter instanceof HQ)){
                    temp = TerrainGraphics.getTerrainImage(ter.getIndex(),0);
                    g.drawImage(temp,8,16*MAX_TILEH-46-temp.getHeight(this)+16,this);
                }
                else if(ter instanceof Pipestation) {
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 24, 16*MAX_TILEH-30, 13*16, 0, 14*16, 32, this);
                } else if(ter instanceof HQ){
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 24, 16*MAX_TILEH-30, ((Property)ter).owner.getCO().getStyle()*16, 0, (((Property)ter).owner.getCO().getStyle()+1)*16, 32, this);
                } else{
                    g.drawImage(temp, 8, 16*MAX_TILEH-46-16, 24, 16*MAX_TILEH-30, (ter.getIndex()-2)*16, 0, (ter.getIndex()-1)*16, 32, this);
                }
            } else {
                g.drawImage(temp,8,16*MAX_TILEH-46-temp.getHeight(this)+16,this);
            }*/
            
            //g.drawImage(temp,8,16*MAX_TILEH-46-temp.getHeight(this)+16,this);
            g.setColor(Color.white);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.drawString(currTile.getTerrain().getName(),0,16*MAX_TILEH-46);
            
            if(currTile.getTerrain() instanceof Invention){
                Invention inv = (Invention)currTile.getTerrain();
                g.drawString("" + inv.getHP(),0,16*MAX_TILEH-20);
                
                if(fire && /*(u.getArmy().getSide() != selected.getArmy().getSide()) &&*/ selected.damageCalc(inv) != -1){
                    g.setColor(Color.red);
                    g.fillRect(0,16*MAX_TILEH-72,32,16);
                    
                    g.setColor(Color.white);
                    g.setFont(new Font("SansSerif", Font.BOLD, 16));
                    g.drawString("" + selected.damageCalc(inv),0,16*MAX_TILEH-56);
                }
            } else {
                //defense stars
                g.drawImage(MiscGraphics.getSmallStar(5),0,16*MAX_TILEH-28,this);
                g.drawString("" + currTile.getTerrain().getDef(),10,16*MAX_TILEH-20);
                
                //capture points
                if(currTile.getTerrain() instanceof Property) {
                    g.drawImage(MiscGraphics.getCaptureIcon(),0,16*MAX_TILEH-18,this);
                    g.drawString("" + ((Property)currTile.getTerrain()).getCapturePoints(),10,16*MAX_TILEH-10);
                }
            }
            
            
            //Unit box
            if(currTile.getUnit()!=null && 
               (!m.find(new Location(cx,cy)).getUnit().isHidden() || 
               m.find(new Location(cx,cy)).getUnit().getArmy().getSide() == b.getArmy(b.getTurn()).getSide()) ||
          	   (b.isMist() && 
               m.find(new Location(cx,cy)).getUnit() != null && 
               !m.find(new Location(cx,cy)).getUnit().isDived())) 
            {
                Unit tileUnit = currTile.getUnit();
                g.setColor(Color.black);
                g.fillRect(32,16*MAX_TILEH-56,32,56);
                
                g.setColor(Color.white);
                
                if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                    g.drawString("?????",32,16*MAX_TILEH-46);
                } else {
                    g.drawString(tileUnit.getName(),32,16*MAX_TILEH-46);
                }
                //g.drawImage(UnitGraphics.getUnitImage(u),40,16*MAX_TILEH-46,this);
                
                //This is when for drawing normal units
                g.drawImage(UnitGraphics.getUnitImage(tileUnit),40,16*MAX_TILEH-46,40+16,16*MAX_TILEH-46+16,0,UnitGraphics.findYPosition(tileUnit),16,UnitGraphics.findYPosition(tileUnit)+16,this);

                //This is where the unit's HP is drawn
                g.drawImage(MiscGraphics.getSmallHeart(),33,16*MAX_TILEH-26, this);
                if(hideTheHP || tileUnit.getArmy().getCO().hiddenHP && b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide()) 
                {
                    g.drawString("?",42,16*MAX_TILEH-20);
                } 
                else 
                {
                	if(b.getArmy(b.getTurn()).getCO().seeFullHP)
                	{
                		String onesDigit = "" + (tileUnit.getHP() % 10);
                		String tensDigit = "" + (tileUnit.getHP() / 10);
                		
                		g.drawString(tensDigit + "." + onesDigit,42,16*MAX_TILEH-20);
                	}
                	else
                	{
                		g.drawString("" + tileUnit.getDisplayHP(),42,16*MAX_TILEH-20);
                	}
                	
                    //g.drawString("" + tileUnit.getDisplayHP(),42,16*MAX_TILEH-20);
                }
                
                //This is where the unit's fuel information is drawn
                g.drawImage(MiscGraphics.getLowFuelIcon(),32,16*MAX_TILEH-18,this);
                if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                    g.drawString("?",42,16*MAX_TILEH-10);
                } else {
                    g.drawString("" + tileUnit.getGas(),42,16*MAX_TILEH-10);
                }
                
                //This is where the unit's ammo information is drawn
                if(tileUnit.getAmmo()!=-1 || (tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide()))) {
                    g.drawImage(MiscGraphics.getLowAmmoIcon(),32,16*MAX_TILEH-8,this);
                    if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                        g.drawString("?",42,16*MAX_TILEH);
                    } else {
                        g.drawString("" + tileUnit.getAmmo(),42,16*MAX_TILEH);
                    }
                }
                
                if(fire && (tileUnit.getArmy().getSide() != selected.getArmy().getSide()) && selected.displayDamageCalc(tileUnit) != -1 && selected.checkFireRange(new Location(cx,cy))){
                    g.setColor(Color.red);
                    g.fillRect(32,16*MAX_TILEH-72,32,16);
                    
                    g.setColor(Color.white);
                    g.setFont(new Font("SansSerif", Font.BOLD, 16));
                    //g.drawString(selected.displayDamageCalc(u)+"%",32,16*MAX_TILEH-56);
                    
                    //Koshi can get LOL readouts!
                    if(tileUnit.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() != tileUnit.getArmy().getSide())) {
                        g.drawString("LOL",32,16*MAX_TILEH-56);
                        //g.drawString("LOL",16*MAX_TILEW-64,16*MAX_TILEH-56);
                    } else {
                        g.drawString(selected.displayDamageCalc(tileUnit)+"",32,16*MAX_TILEH-56);
                        //g.drawString(selected.displayDamageCalc(tileUnit)+"",16*MAX_TILEW-64,16*MAX_TILEH-56);
                    }
                }
                
                //Transport box
                if(tileUnit instanceof Transport && (!b.isFog() || b.getArmy(b.getTurn()).getSide() == tileUnit.getArmy().getSide())) {
                    Transport trans = (Transport) tileUnit;
                    
                    if((trans.getUnitsCarried()>0) && !trans.getArmy().getCO().hiddenUnitInfo && (b.getArmy(b.getTurn()).getSide() == tileUnit.getArmy().getSide())) {
                        g.setColor(Color.black);
                        g.fillRect(64,16*MAX_TILEH-56,32,56);
                        
                        if(trans.getUnitsCarried()==2) {
                            //This line is used for drawing normal units
                            g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(1)),72,16*MAX_TILEH-50,72+16,16*MAX_TILEH-50+16,0,UnitGraphics.findYPosition(trans.getUnit(1)),16,UnitGraphics.findYPosition(trans.getUnit(1))+16,this);
                            
                            //This line is used for drawing normal units
                            g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(2)),72,16*MAX_TILEH-32,72+16,16*MAX_TILEH-32+16,0,UnitGraphics.findYPosition(trans.getUnit(2)),16,UnitGraphics.findYPosition(trans.getUnit(2))+16,this);
                        } else {
                            //This line is used for drawing normal units
                            g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(1)),72,16*MAX_TILEH-36,72+16,16*MAX_TILEH-36+16,0,UnitGraphics.findYPosition(trans.getUnit(1)),16,UnitGraphics.findYPosition(trans.getUnit(1))+16,this);
                        }
                    }
                }
            }
        }
    }
    
    //draws the CO Bar
    public void drawCOBar(Graphics2D g){
        if(cx < (MAX_TILEW/2)+(sx/16)){
            g.drawImage(MiscGraphics.getReverseCOBar(b.getArmy(b.getTurn()).getColor()),16*MAX_TILEW-71,0,this);
            if(!b.getArmy(b.getTurn()).getCO().altCostume){
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getCO().getID()),16*MAX_TILEW-1,0,16*MAX_TILEW-33,12,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getCO().getID()),16*MAX_TILEW-1,0,16*MAX_TILEW-33,12,369,350,401,362,this);
            }
            if(b.getArmy(b.getTurn()).getAltCO() != null)drawAltCOBar(g);
            
            g.drawImage(MiscGraphics.getGold(),16*MAX_TILEW-59,9,this);
            String funds = "" + b.getArmy(b.getTurn()).getFunds();
            for(int i=funds.length()-1,j=0;i>=0;i--,j++){
                g.drawImage(MiscGraphics.getMoneyDigit(funds.charAt(i)),16*MAX_TILEW-5-j*6,9,this);
            }
            
            if(b.getArmy(b.getTurn()).isTag()){
                g.drawImage(MiscGraphics.getTagBreak(),16*MAX_TILEW-73,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isCOP()){
                g.drawImage(MiscGraphics.getPower(),16*MAX_TILEW-73,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isSCOP()){
                g.drawImage(MiscGraphics.getSuperPower(),16*MAX_TILEW-73,0,this);
            }else{
                int maxStars = b.getArmy(b.getTurn()).getCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),16*MAX_TILEW-33-6*i-6,0,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),16*MAX_TILEW-33-COPStars*6-7-7*(i-COPStars),0,this);
                    }
                }
            }
        }else{
            g.drawImage(MiscGraphics.getCOBar(b.getArmy(b.getTurn()).getColor()),0,0,this);
            if(!b.getArmy(b.getTurn()).getCO().altCostume){
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getCO().getID()),0,0,32,12,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getCO().getID()),0,0,32,12,369,350,401,362,this);
            }
            
            if(b.getArmy(b.getTurn()).getAltCO() != null)drawAltCOBar(g);
            
            g.drawImage(MiscGraphics.getGold(),0,9,this);
            String funds = "" + b.getArmy(b.getTurn()).getFunds();
            for(int i=funds.length()-1,j=0;i>=0;i--,j++){
                g.drawImage(MiscGraphics.getMoneyDigit(funds.charAt(i)),54-j*6,9,this);
            }
            
            if(b.getArmy(b.getTurn()).isTag()){
                g.drawImage(MiscGraphics.getTagBreak(),39,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isSCOP()){
                g.drawImage(MiscGraphics.getSuperPower(),39,0,this);
            }else if(b.getArmy(b.getTurn()).getCO().isCOP()){
                g.drawImage(MiscGraphics.getPower(),39,0,this);
            }else{
                int maxStars = b.getArmy(b.getTurn()).getCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),33+6*i,0,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),33+6*i,0,this);
                    }
                }
            }
        }
    }
    
    //draws the Alt CO Bar
    public void drawAltCOBar(Graphics2D g){
        if(cx < (MAX_TILEW/2)+(sx/16)){
            g.drawImage(MiscGraphics.getAltReverseCOBar(b.getArmy(b.getTurn()).getColor()),16*MAX_TILEW-71,0,this);
            if(!b.getArmy(b.getTurn()).getAltCO().altCostume){
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getAltCO().getID()),16*MAX_TILEW-1,21,16*MAX_TILEW-33,33,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getAltCO().getID()),16*MAX_TILEW-1,21,16*MAX_TILEW-33,33,369,350,401,362,this);
            }
            if(!b.getArmy(b.getTurn()).isTag()){
                int maxStars = b.getArmy(b.getTurn()).getAltCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getAltCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getAltCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),16*MAX_TILEW-33-6*i-6,21,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),16*MAX_TILEW-33-COPStars*6-7-7*(i-COPStars),21,this);
                    }
                }
            }
        }else{
            g.drawImage(MiscGraphics.getAltCOBar(b.getArmy(b.getTurn()).getColor()),0,0,this);
            if(!b.getArmy(b.getTurn()).getAltCO().altCostume){
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getAltCO().getID()),0,21,32,33,144,350,176,362,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getAltCO().getID()),0,21,32,33,369,350,401,362,this);
            }
            if(!b.getArmy(b.getTurn()).isTag()){
                int maxStars = b.getArmy(b.getTurn()).getAltCO().getMaxStars();
                int COPStars = b.getArmy(b.getTurn()).getAltCO().getCOPStars();
                double currentStars = b.getArmy(b.getTurn()).getAltCO().getStars();
                double totalStars = currentStars;
                int starType = 0;
                for(int i=0;i<maxStars;i++){
                    if(currentStars == 0.0){
                        starType = 0;
                    }else if(currentStars >= 1.0){
                        if(i<COPStars){
                            if(totalStars >= COPStars)starType = 6;
                            else starType = 5;
                        }else{
                            if(totalStars >= maxStars)starType = 6;
                            else starType = 5;
                        }
                        currentStars-=1.0;
                    }else{
                        if(currentStars <= .4)starType=1;
                        else if(currentStars <= .6)starType=2;
                        else if(currentStars <= .8)starType=3;
                        else if(currentStars <= 1.0)starType=4;
                        currentStars = 0.0;
                    }
                    
                    if(i<COPStars){
                        g.drawImage(MiscGraphics.getSmallStar(starType),33+6*i,21,this);
                    }else{
                        g.drawImage(MiscGraphics.getBigStar(starType),33+6*i,21,this);
                    }
                }
            }
        }
    }
    
    //draws the Movement range
    public void drawMoveRange(Graphics2D g){
        if(selected.getMoveRange() == null ) selected.calcMoveTraverse();
        
        MoveTraverse mt = selected.getMoveRange();
        
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        //g.setColor(Color.blue);
        for(int i=sx/16;i<m.getMaxCol();i++){
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<m.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                //if(mt.checkMove(i,j))g.fillRect(i*16-sx,j*16-sy,16,16);
                if(mt.checkMove(i,j))g.drawImage(MiscGraphics.getMoveTile(),i*16-sx,j*16-sy,this);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the Firing Range
    public void drawFireRange(Graphics2D g){
        //makes the tiles translucent
        
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        for(int i=sx/16;i<m.getMaxCol();i++) {
            if(i>=sx/16+MAX_TILEW) {
                break;
            }
            
            for(int j=sy/16;j<m.getMaxRow();j++) {
                if(j>=sy/16+MAX_TILEH) {
                    break;
                }
                if(i < 0 || j < 0) {
                    continue;
                }
                
                /*
                if(selected.checkFireRange(new Location(i,j))) 
                {
                    g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                }
                */
                
                //[CHANGE]
                for(int k = 0; k < contextTargs.size(); k++)
                {
                	if((new Location(i, j).equals(contextTargs.get(k))))
                	{
                        g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                	}
                }
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws Fog of War
    public void drawFog(Graphics2D g){
        //makes the tiles translucent
        //g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        
        //If there is any fog present, use black squares
        //But if there is mist present and no fog, use white squares
        if(b.isFog())
        {
        	g.setColor(Color.black);
        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        }
        else if(b.isMist())
        {
        	g.setColor(Color.white);
        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        }
        
        for(int i=sx/16;i<m.getMaxCol();i++)
        {
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<m.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                //draw fog tile
                if(b.getFog(i,j))g.fillRect(i*16-sx,j*16-sy,16,16);
                
                //draw detected units
                if(m.find(new Location(i,j)).hasUnit()){
                    Unit tempu = m.find(new Location(i,j)).getUnit();
                    if(tempu.getArmy().getSide() != b.getArmy(b.getTurn()).getSide() && tempu.isHidden() && tempu.isDetected()){
                        //sets alpha back to normal
                        g.setComposite(AlphaComposite.SrcOver);
                        g.drawImage(MiscGraphics.getDetectIcon(),i*16-sx,j*16-sy,this);
                        //makes the tiles translucent
                        if(b.isFog())
                        {
                        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                        }
                        else if(b.isMist())
                        {
                        	g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
                        }
                    }
                }
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the weather
    public void drawWeather(Graphics2D g){
        if(b.getWeather()!=0){
            if(b.getWeather()==1){
                //rain
                for(int i=sx/16;i<m.getMaxCol();i++){
                    if(i>=sx/16+MAX_TILEW)break;
                    for(int j=sy/16;j<m.getMaxRow();j++){
                        if(j>=sy/16+MAX_TILEH)break;
                        if(i < 0 || j < 0)continue;
                        g.drawImage(MiscGraphics.getRain(0),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getRain(1),i*16-sx,j*16-sy,this);
                    }
                }
            }else if(b.getWeather()==2){
                //rain
                for(int i=sx/16;i<m.getMaxCol();i++){
                    if(i>=sx/16+MAX_TILEW)break;
                    for(int j=sy/16;j<m.getMaxRow();j++){
                        if(j>=sy/16+MAX_TILEH)break;
                        if(i < 0 || j < 0)continue;
                        g.drawImage(MiscGraphics.getSnow(0),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getSnow(1),i*16-sx,j*16-sy,this);
                        //g.drawImage(MiscGraphics.getSnow(2),i*16-sx,j*16-sy,this);
                    }
                }
            }else if(b.getWeather()==3){
                //rain
                for(int i=sx/16;i<m.getMaxCol();i++){
                    if(i>=sx/16+MAX_TILEW)break;
                    for(int j=sy/16;j<m.getMaxRow();j++){
                        if(j>=sy/16+MAX_TILEH)break;
                        if(i < 0 || j < 0)continue;
                        g.drawImage(MiscGraphics.getSand(0),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getSand(1),i*16-sx,j*16-sy,this);
                        g.drawImage(MiscGraphics.getSand(2),i*16-sx,j*16-sy,this);
                    }
                }
            }
        }
    }
    
    //draws the Repair Range
    public void drawRepairRange(Graphics2D g){
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        for(int i=sx/16;i<m.getMaxCol();i++){
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<m.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                if(selected.checkAdjacent(new Location(i,j)))g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    public void drawSpecial1Range(Graphics2D g){
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        for(int i=sx/16;i<m.getMaxCol();i++){
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<m.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                //if(selected.checkAdjacent(new Location(i,j)))g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                
                if(selected.getArmy().getCO().canTargetSpecial1(selected, new Location(i,j)))
                    g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    public void drawSpecial2Range(Graphics2D g){
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        for(int i=sx/16;i<m.getMaxCol();i++){
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<m.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                if(selected.getArmy().getCO().canTargetSpecial2(selected, new Location(i,j)))g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the Display Firing Range
    public void drawDisplayFireRange(Graphics2D g){
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        
        for(int i=sx/16;i<m.getMaxCol();i++) {
            if(i>=sx/16+MAX_TILEW)
                break;
            
            for(int j=sy/16;j<m.getMaxRow();j++) {
                if(j>=sy/16+MAX_TILEH) {
                    break;
                }
                if(i < 0 || j < 0) {
                    continue;
                }
                
                int colOffset = Math.abs(i - selected.getLocation().getCol());
                int rowOffset = Math.abs(j - selected.getLocation().getRow());
                int distance = colOffset + rowOffset;
                
                if(selected.checkDisplayFireRange(new Location(i,j)) && (!selected.getArmy().getCO().disruptFireDisplay || b.getArmy(b.getTurn()).getSide() == selected.getArmy().getSide())) {
                    g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                } else if(selected.getArmy().getCO().disruptFireDisplay && (distance <= 1) && b.getArmy(b.getTurn()).getSide() != selected.getArmy().getSide()) {
                    g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
                }
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the Unloading range
    public void drawUnloadRange(Graphics2D g){
        Transport trans = (Transport) selected;
        //makes the tiles translucent
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        for(int i=sx/16;i<m.getMaxCol();i++){
            if(i>=sx/16+MAX_TILEW)break;
            for(int j=sy/16;j<m.getMaxRow();j++){
                if(j>=sy/16+MAX_TILEH)break;
                if(i < 0 || j < 0)continue;
                if(trans.checkUnloadRange(new Location(i,j),unload))g.drawImage(MiscGraphics.getAttackTile(),i*16-sx,j*16-sy,this);
            }
        }
        //sets alpha back to normal
        g.setComposite(AlphaComposite.SrcOver);
    }
    
    //draws the Day Start screen
    public void drawDayStartScreen(Graphics2D g){
        //fill in background
        g.setColor(Color.black);
        g.fillRect(0,0,MAX_TILEW*16,MAX_TILEH*16);
        g.drawImage(MiscGraphics.getDayStart(b.getArmy(b.getTurn()).getColor()),0,0,MAX_TILEW*16,MAX_TILEH*16,0,0,480,320,this);
        
        //draw COs
        int coh = (MAX_TILEH*16-350)/2;
        if(coh < 0)coh = 0;
        if(b.getArmy(b.getTurn()).getAltCO()!=null){
            if(!b.getArmy(b.getTurn()).getAltCO().altCostume){
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getAltCO().getID()),MAX_TILEW*16*2/3+20,coh,(MAX_TILEW*16*2/3+20)+225,coh+350,0,0,225,350,this);
            } else {
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getAltCO().getID()),MAX_TILEW*16*2/3+20,coh,(MAX_TILEW*16*2/3+20)+225,coh+350,225,0,450,350,this);
            }       }
        if(!b.getArmy(b.getTurn()).getCO().altCostume){
            g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getCO().getID()),MAX_TILEW*16*2/3,coh,(MAX_TILEW*16*2/3)+225,coh+350,0,0,225,350,this);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(b.getArmy(b.getTurn()).getCO().getID()),MAX_TILEW*16*2/3,coh,(MAX_TILEW*16*2/3)+225,coh+350,225,0,450,350,this);
        }
        //draw day text
        g.setColor(Color.black);
        g.setFont(new Font("Impact", Font.BOLD, MAX_TILEH*16/3));
        g.drawString("Day "+b.getDay(),10,MAX_TILEH*16*15/24);
    }
    
    //draws the CO Bar
    public void drawArmyCOBar(Army a, int x, int y, boolean displayMoney, Graphics2D g){
        g.drawImage(MiscGraphics.getCOBar(a.getColor()),x,y,this);
        if(!a.getCO().altCostume){
            g.drawImage(MiscGraphics.getCOSheet(a.getCO().getID()),x,y,x+32,y+12,144,350,176,362,this);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(a.getCO().getID()),x,y,x+32,y+12,369,350,401,362,this);
        }
        if(a.getAltCO() != null)drawArmyAltCOBar(a,x,y,g);
        
        
        g.drawImage(MiscGraphics.getGold(),x+0,y+9,this);
        
        //New stuff here
        boolean hidePower  = determineIfPowerHidden(a.getCO());
        boolean hideGold = determineIfGoldHidden(a.getCO());
        if(displayMoney && !hideGold) {
            String funds = "" + a.getFunds();
            for(int i=funds.length()-1,j=0;i>=0;i--,j++){
                g.drawImage(MiscGraphics.getMoneyDigit(funds.charAt(i)),x+54-j*6,y+9,this);
            }
        }else{
            g.drawImage(MiscGraphics.getQuestionMark(),x+54,y+9,this);
        }
        
        if(a.isTag()){
            g.drawImage(MiscGraphics.getTagBreak(),x+39,y,this);
        }else if(a.getCO().isSCOP()){
            g.drawImage(MiscGraphics.getSuperPower(),x+39,y,this);
        }else if(a.getCO().isCOP()){
            g.drawImage(MiscGraphics.getPower(),x+39,y,this);
        } else if(hidePower) {
            g.drawImage(MiscGraphics.getQuestionMark(),x+33,y,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+40,y,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+47,y,this);
        } else {
            int maxStars = a.getCO().getMaxStars();
            int COPStars = a.getCO().getCOPStars();
            double currentStars = a.getCO().getStars();
            double totalStars = currentStars;
            int starType = 0;
            for(int i=0;i<maxStars;i++){
                if(currentStars == 0.0){
                    starType = 0;
                }else if(currentStars >= 1.0){
                    if(i<COPStars){
                        if(totalStars >= COPStars)starType = 6;
                        else starType = 5;
                    }else{
                        if(totalStars >= maxStars)starType = 6;
                        else starType = 5;
                    }
                    currentStars-=1.0;
                }else{
                    if(currentStars <= .4)starType=1;
                    else if(currentStars <= .6)starType=2;
                    else if(currentStars <= .8)starType=3;
                    else if(currentStars <= 1.0)starType=4;
                    currentStars = 0.0;
                }
                
                if(i<COPStars){
                    g.drawImage(MiscGraphics.getSmallStar(starType),x+33+6*i,y,this);
                }else{
                    g.drawImage(MiscGraphics.getBigStar(starType),x+33+6*i,y,this);
                }
            }
        }
    }
    
    public boolean determineIfPowerHidden(CO thisCO) {
        boolean hideInfo = false;
        
        int currSide = b.getArmy(b.getTurn()).getSide();
        int aSide = thisCO.getArmy().getSide();
        
        if(thisCO.hiddenPower && currSide != aSide) {
            hideInfo = true;
        }
        
        return hideInfo;
    }
    public boolean determineIfGoldHidden(CO thisCO) {
        boolean hideInfo = false;
        
        int currSide = b.getArmy(b.getTurn()).getSide();
        int aSide = thisCO.getArmy().getSide();
        
        if(thisCO.hiddenGold && currSide != aSide) {
            hideInfo = true;
        }
        
        return hideInfo;
    }
    public boolean determineIfIntelHidden(CO thisCO) {
        boolean hideInfo = false;
        
        int currSide = b.getArmy(b.getTurn()).getSide();
        int aSide = thisCO.getArmy().getSide();
        
        if(thisCO.hiddenIntel && currSide != aSide) {
            hideInfo = true;
        }
        
        return hideInfo;
    }
    
    //draws the Alt CO Bar
    public void drawArmyAltCOBar(Army a, int x, int y, Graphics2D g){
        //if(a.getAltCO() != null){
        g.drawImage(MiscGraphics.getAltCOBar(a.getColor()),x,y,this);
        if(!a.getAltCO().altCostume){
            g.drawImage(MiscGraphics.getCOSheet(a.getAltCO().getID()),x,y+21,x+32,y+33,144,350,176,362,this);
        } else {
            g.drawImage(MiscGraphics.getCOSheet(a.getAltCO().getID()),x,y+21,x+32,y+33,369,350,401,362,this);
        }
        
        //New stuff here
        boolean hideInfo = determineIfPowerHidden(a.getAltCO());
        
        if(hideInfo) {
            g.drawImage(MiscGraphics.getQuestionMark(),x+33,y+21,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+40,y+21,this);
            g.drawImage(MiscGraphics.getQuestionMark(),x+47,y+21,this);
        } else if(!a.isTag()) {
            int maxStars = a.getAltCO().getMaxStars();
            int COPStars = a.getAltCO().getCOPStars();
            double currentStars = a.getAltCO().getStars();
            double totalStars = currentStars;
            int starType = 0;
            for(int i=0;i<maxStars;i++){
                if(currentStars == 0.0){
                    starType = 0;
                }else if(currentStars >= 1.0){
                    if(i<COPStars){
                        if(totalStars >= COPStars)starType = 6;
                        else starType = 5;
                    }else{
                        if(totalStars >= maxStars)starType = 6;
                        else starType = 5;
                    }
                    currentStars-=1.0;
                }else{
                    if(currentStars <= .4)starType=1;
                    else if(currentStars <= .6)starType=2;
                    else if(currentStars <= .8)starType=3;
                    else if(currentStars <= 1.0)starType=4;
                    currentStars = 0.0;
                }
                
                if(i<COPStars){
                    g.drawImage(MiscGraphics.getSmallStar(starType),x+33+6*i,y+21,this);
                }else{
                    g.drawImage(MiscGraphics.getBigStar(starType),x+33+6*i,y+21,this);
                }
            }
        }
    }
    //draws the info screen
    public void drawInfoScreen(Graphics2D g) {
        String[] bio;
        int i, store, adjust;
        int starStore = 0;
        g.drawImage(MainMenuGraphics.getBackground(),0,0, this);
        //Draws the COs
        String sidestring, costring;
        //These two are used for adjusting the text so it doesn't interfere with the top info bar
        store = 0;
        adjust = 0;
        int charShift = 0;
        Character c = new Character(' ');
        int s;
        if(!alt) {
            //Draw the main CO
            if(!b.getArmy(infono).getCO().altCostume)
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(infono).getCO().getID()),175,40,175 + 225, 40 + 350, 0, 0, 225, 350, this);
            else
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(infono).getCO().getID()),175,40,175 + 225, 40 + 350, 225, 0, 450, 350, this);
            //
            //Draw the 'basic information'
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            g.drawString("Side: " + (infono+1), 8,20);
            g.drawString("Main CO: " + b.getArmy(infono).getCO().getName(), 45,20);
            g.drawString(b.getArmy(infono).getCO().title, (b.getArmy(infono).getCO().getName().length() + 9)*6 + 45,20);
            
            //CO Bio
            //This is a 'word wrap' thing, used multiple times. Listen up, I'm only going to document this once. >_>
            for(i = 0; i<((b.getArmy(infono).getCO().getBio().length()/40)+1); i++) {//As long as i is shorter than the length, in characters, of the bio divided by 40, incremented by one.
                if(b.getArmy(infono).getCO().getBio().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left in the bio?
                    //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
                    if((40 + i*15- skip*MAX_TILEH*8+ store*15) + adjust <25 && 40 + i*15- skip*MAX_TILEH*8+ + store*15 +adjust >0)
                        adjust += 25; //If so, move this, and everything after this, 25 pixels down.
                    //Draw the substring - 40 characters from the last area.
                    if(c.isWhitespace(b.getArmy(infono).getCO().getBio().charAt((i+1)*40-charShift))) { //If the string ends with a space/ next line starts with one, 'sfine, continue
                        g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift, (i+1)*40-charShift), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    } else {//If not - TIME TO PARSE THIS MOTHER
                        for(s = 1; s<41; s++) {
                            if(c.isWhitespace(b.getArmy(infono).getCO().getBio().charAt((i+1)*40-charShift-s))) {
                                break;
                            }
                        }
                        if(s!=40) {
                            g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift, (i+1)*40-charShift-s), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                            charShift+=s; //Shift the substring over
                        } else {
                            g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift, (i+1)*40-charShift-s), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                            charShift+=s; //Shift the substring over
                            s = 0;
                        }
                    }
                    
                } else //If there is less than 40 characters left...
                {
                    if((40 + i*15- skip*MAX_TILEH*8 + adjust+ store*15)<25 && store*15+ 40 + i*15- skip*MAX_TILEH*8 + adjust>0)
                        adjust += 25;
                    //Avoiding info space.
                    //Drawing the rest of the substring.
                    //First we see if
                    g.drawString(b.getArmy(infono).getCO().getBio().substring(i*40-charShift), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 ==((b.getArmy(infono).getCO().getBio().length()/40)+1))
                    store = i; //Store is used to get placement.
            }
            store += 2;
            
            //This draws the Hit and Miss
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getHitIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getCO().getHit(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getMissIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getCO().getMiss(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            
            //This draws the power bar
            if(((store+1)*15- skip*MAX_TILEH*8 + adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8 + adjust)>0)
                adjust += 25;
            if(b.getArmy(infono).getCO().getCOPStars() != -1)
                for(i = 0; i<b.getArmy(infono).getCO().getCOPStars()+1; i++) {
                g.drawImage(MiscGraphics.getSmallStar(5),10+i*6, (store+1)*15- skip*MAX_TILEH*8 + adjust, this);
                starStore = i;
                }
            //Aaand...the SCOP
            for(i = 0; i<(b.getArmy(infono).getCO().getMaxStars()-b.getArmy(infono).getCO().getCOPStars()); i++) {
                g.drawImage(MiscGraphics.getBigStar(5),10+i*8 + starStore*6, (store+1)*15- skip*MAX_TILEH*8 + adjust, this);
            }
            store++;
            //This draws the Day to Day.
            
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            
            g.drawImage(MiscGraphics.getSkillIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            
            //Word wrap for the skill
            for(i = 0; i<((b.getArmy(infono).getCO().getD2D().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getCO().getD2D().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    
                    g.drawString(b.getArmy(infono).getCO().getD2D().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().getD2D().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 ==((b.getArmy(infono).getCO().getD2D().length()/40)+1))
                    store +=i;
            }
            store += 2;
            //This draws the Power
            if(b.getArmy(infono).getCO().COPStars != -1) {
                if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                    adjust += 25;
                
                g.drawImage(MiscGraphics.getPowerIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
                g.drawString(b.getArmy(infono).getCO().COPName, 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                
                for(i = 0; i<((b.getArmy(infono).getCO().getCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
                {
                    if(b.getArmy(infono).getCO().getCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        g.drawString(b.getArmy(infono).getCO().getCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    } else {
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        
                        g.drawString(b.getArmy(infono).getCO().getCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    }
                    if(i+1 == ((b.getArmy(infono).getCO().getCOPString().length()/40)+1))
                        store +=i;
                }
                
                store += 2;
            }
            //This draws the SCOP
            if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getSuperIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
            g.drawString(b.getArmy(infono).getCO().SCOPName, 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
            for(i = 0; i<((b.getArmy(infono).getCO().getSCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getCO().getSCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().getSCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().getSCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 == ((b.getArmy(infono).getCO().getSCOPString().length()/40)+1))
                    store +=i;
            }
            //Draws tags
            store += 2;
            for(i = 0; i< b.getArmy(infono).getCO().TagStars.length; i++) {
                if(b.getArmy(infono).getCO().TagStars[i] > 0) {
                    if(i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust<25 && i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getCO().TagCOs[i], 10, i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                    for(int t =0; t<b.getArmy(infono).getCO().TagStars[i]; t++) {
                        g.drawImage(MiscGraphics.getBigStar(5), b.getArmy(infono).getCO().TagCOs[i].length()*25 + 15 + t*8,(store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust+ i*15 -10, this);
                    }
                    if(i+1 == b.getArmy(infono).getCO().TagStars.length)
                        store +=i;
                }
            }
            skipMax = ((store * 15)/(MAX_TILEH*8));
        } else {
            //Draw the main CO
            if(!b.getArmy(infono).getAltCO().altCostume)
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(infono).getAltCO().getID()),175,40,175 + 225, 40 + 350, 0, 0, 225, 350, this);
            else
                g.drawImage(MiscGraphics.getCOSheet(b.getArmy(infono).getAltCO().getID()),175,40,175 + 225, 40 + 350, 225, 0, 450, 350, this);
            //Draw the 'basic information'
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            g.drawString("Side: " + (infono+1), 8,20);
            g.drawString("Main CO: " + b.getArmy(infono).getAltCO().getName(), 45,20);
            g.drawString(b.getArmy(infono).getAltCO().title, (b.getArmy(infono).getAltCO().getName().length() + 9)*6 + 45,20);
            
            //CO Bio
            //This is a 'word wrap' thing, used multiple times. Listen up, I'm only going to document this once. >_>
            for(i = 0; i<((b.getArmy(infono).getAltCO().getBio().length()/40)+1); i++) {//As long as i is shorter than the length, in characters, of the bio divided by 40, incremented by one.
                if(b.getArmy(infono).getAltCO().getBio().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left in the bio?
                    //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
                    if((40 + i*15- skip*MAX_TILEH*8) + adjust <25 && 40 + i*15- skip*MAX_TILEH*8+ adjust >0)
                        adjust += 25; //If so, move this, and everything after this, 25 pixels down.
                    //Draw the substring - 40 characters from the last area.
                    g.drawString(b.getArmy(infono).getAltCO().getBio().substring(i*40, (i+1)*40), 10, 40 + i*15- skip*MAX_TILEH*8 + adjust);
                    
                } else //If there is less than 40 characters left...
                {
                    if((40 + i*15- skip*MAX_TILEH*8 + adjust)<25 && 40 + i*15- skip*MAX_TILEH*8 + adjust>0)
                        adjust += 25;
                    //Avoiding info space.
                    //Drawing the rest of the substring.
                    g.drawString(b.getArmy(infono).getAltCO().getBio().substring(i*40), 10,40 + i*15- skip*MAX_TILEH*8);
                }
                if(i+1 ==((b.getArmy(infono).getAltCO().getBio().length()/40)+1))
                    store = i; //Store is used to get placement.
            }
            store+=2;
            //This draws the Hit and Miss
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getHitIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getAltCO().getHit(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getMissIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            g.drawString(b.getArmy(infono).getAltCO().getMiss(), 45,40 + (store-1)*15- skip*MAX_TILEH*8 + adjust);
            store++;
            
            //This draws the power bar
            //If the CO has a COP
            if((22 + (store)*15- skip*96*scale + adjust)<25 && (22 + (store)*15- skip*96*scale + adjust)>0)
                adjust += 25;
            if(b.getArmy(infono).getAltCO().getCOPStars() != -1)
                for(i = 0; i<b.getArmy(infono).getAltCO().getCOPStars()+1; i++) {
                g.drawImage(MiscGraphics.getSmallStar(5),10+i*6, 22 + (store)*15- skip*96*scale + adjust, this);
                starStore = i;
                }
            //Aaand...the SCOP
            for(i = 0; i<(b.getArmy(infono).getAltCO().getMaxStars()-b.getArmy(infono).getAltCO().getCOPStars()); i++) {
                g.drawImage(MiscGraphics.getBigStar(5),10+i*8 + starStore*6, 20 + (store)*15- skip*96*scale + adjust, this);
            }
            
            store++;
            
            //This draws the Day to Day.
            
            if(((store+1)*15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1)*15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            
            g.drawImage(MiscGraphics.getSkillIcon(),10 ,(store+1)*15- skip*MAX_TILEH*8 + adjust,this);
            
            //Word wrap for the skill
            for(i = 0; i<((b.getArmy(infono).getAltCO().getD2D().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getAltCO().getD2D().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    
                    g.drawString(b.getArmy(infono).getAltCO().getD2D().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().getD2D().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 ==((b.getArmy(infono).getAltCO().getD2D().length()/40)+1))
                    store +=i;
            }
            store += 2;
            //This draws the Power
            if(b.getArmy(infono).getAltCO().COPStars != -1) {
                if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                    adjust += 25;
                
                g.drawImage(MiscGraphics.getPowerIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
                g.drawString(b.getArmy(infono).getAltCO().COPName, 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                
                for(i = 0; i<((b.getArmy(infono).getAltCO().getCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
                {
                    if(b.getArmy(infono).getAltCO().getCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        g.drawString(b.getArmy(infono).getAltCO().getCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    } else {
                        if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                            adjust += 25;
                        
                        g.drawString(b.getArmy(infono).getAltCO().getCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                    }
                    if(i+1 == ((b.getArmy(infono).getAltCO().getCOPString().length()/40)+1))
                        store +=i;
                }
                
                store += 2;
            }
            //This draws the SCOP
            if(((store+1) * 15- skip*MAX_TILEH*8+ adjust)<25 && ((store+1) * 15- skip*MAX_TILEH*8+ adjust)>0)
                adjust += 25;
            g.drawImage(MiscGraphics.getSuperIcon(), 10, (store+1) * 15- skip*MAX_TILEH*8 + adjust, this);
            g.drawString(b.getArmy(infono).getAltCO().SCOPName, 26, (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
            for(i = 0; i<((b.getArmy(infono).getAltCO().getSCOPString().length()/40)+1); i++) //As long as i is shorter than the length, in characters, of this divided by 40 +1
            {
                if(b.getArmy(infono).getAltCO().getSCOPString().length() - (i+1)*40 >= 0) { //Is there more than 40 characters left?
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().getSCOPString().substring(i*40, (i+1)*40), 10, 40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                } else {
                    if((40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)<25 && (40 + i*15 + store*15- skip*MAX_TILEH*8+ adjust)>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().getSCOPString().substring(i*40), 10,40 + i*15 + store*15- skip*MAX_TILEH*8 + adjust);
                }
                if(i+1 == ((b.getArmy(infono).getAltCO().getSCOPString().length()/40)+1))
                    store +=i;
            }
            //Draws tags
            store += 2;
            for(i = 0; i< b.getArmy(infono).getAltCO().TagStars.length; i++) {
                if(b.getArmy(infono).getAltCO().TagStars[i] > 0) {
                    if(i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust<25 && i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8+ adjust>0)
                        adjust += 25;
                    g.drawString(b.getArmy(infono).getAltCO().TagCOs[i], 10, i*15 + (store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust);
                    for(int t =0; t<b.getArmy(infono).getAltCO().TagStars[i]; t++) {
                        g.drawImage(MiscGraphics.getBigStar(5), b.getArmy(infono).getAltCO().TagCOs[i].length()*5 + 15 + t*8,(store+2) * 15 - 2- skip*MAX_TILEH*8 + adjust+ i*15 -10, this);
                    }
                    if(i+1 == b.getArmy(infono).getAltCO().TagStars.length)
                        store +=i;
                }
            }
            skipMax = ((store * 15)/(MAX_TILEH*8));
        }
    }
    //draws the Intel screen
    public void drawIntelScreen(Graphics2D g){
        boolean displayMoney = true;        //is it ok to display the enemy money and properties?
        boolean displayArmyMoney = true;    //is it ok to display this army's money? (self and allies)
        Army ctarmy = b.getArmy(b.getTurn());
        
        if(b.getWeather()==1 || b.isFog()) {
            displayMoney = false;
        }
        
        //fill in background
        g.drawImage(MainMenuGraphics.getBackground(),0,0,this);
        //g.drawImage(MiscGraphics.getDayStart(b.getArmy(b.getTurn()).getColor()),0,0,MAX_TILEW*16,MAX_TILEH*16,0,0,256,192,this);
        
        //Day and Turn
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD, 15));
        g.drawString("Day "+b.getDay(),0,15);
        g.setColor(Color.yellow);
        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
        if(b.getTurn()<5)g.fillRect(0,20+32*b.getTurn(),130,32);
        else g.fillRect(130,20+32*(b.getTurn()-5),130,32);
        g.setComposite(AlphaComposite.SrcOver);
        
        Army[] arms = b.getArmies();
        
        for(int i=0; i < arms.length; i++) {
            if(!displayMoney && arms[i].getSide() != ctarmy.getSide()) {
                displayArmyMoney = false;
            } else {
                displayArmyMoney = true;
            }
            
            //draw units
            if(i < 5) {
                //This line is used for drawing normal units
                g.drawImage(UnitGraphics.getUnitImage(0,arms[i].getColor()),90,20+i*32,90+16,20+i*32+16,0,UnitGraphics.findYPosition(0,arms[i].getCO().getStyle()),16,UnitGraphics.findYPosition(0,arms[i].getCO().getStyle())+16,this);
            }
            
            else g.drawImage(UnitGraphics.getUnitImage(0, arms[i].getColor()),220,20+(i-5)*32,220+16,20+(i-5)*32+16, 0, UnitGraphics.findYPosition(0,arms[i].getCO().getStyle()), 16, UnitGraphics.findYPosition(0,arms[i].getCO().getStyle())+16 ,this);
            
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            //This is new!
            boolean hideInfo = determineIfIntelHidden(arms[i].getCO());
            
            String uCounter = "";
            
            if(hideInfo) {
                uCounter = "x?";
            } else {
                uCounter = "x"+arms[i].getNumberOfUnits();
            }
            
            if(i < 5) {
                g.drawString(uCounter,106,20+16+i*32);
            } else {
                g.drawString(uCounter,236,20+16+(i-5)*32);
            }
            
            //draw properties
            Image temp;
            temp = TerrainGraphics.getUrbanSpriteSheet(arms[i].getColor()+1);
            if(i<5)g.drawImage(temp,90,20+10+i*32-10,90+16,20+10+i*32+22,3*16,0,4*16,32,this);
            else g.drawImage(temp,220,20+10+(i-5)*32-10,220+16,20+10+(i-5)*32+22,3*16,0,4*16,32,this);
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            
            //There is new stuff here
            if(displayArmyMoney && !hideInfo) {
                if(i<5) {
                    g.drawString("x"+arms[i].getNumberOfProperties(),106,20+32+i*32);
                } else {
                    g.drawString("x"+arms[i].getNumberOfProperties(),236,20+32+(i-5)*32);
                }
            } else {
                if(i<5) {
                    g.drawString("x?",106,20+32+i*32);
                } else {
                    g.drawString("x?",236,20+32+(i-5)*32);
                }
            }
            
            //draw D2D bars
            if(i < 5)drawArmyCOBar(arms[i],0,20+32*i,displayArmyMoney,g);
            else drawArmyCOBar(arms[i],130,20+32*(i-5),displayArmyMoney,g);
        }
    }
    //draws a dialogue box
    //uh, untested.
    /*public void drawDialogueBox(Graphics2D g, String string){
        g.setColor(Color.WHITE);
        g.fillRect(0,MAX_TILEH*16-35,MAX_TILEW*16, MAX_TILEH*16);
     
        for(int i = 0; i<((string.length()/50)+1); i++)
        {//As long as i is shorter than the length, in characters, of the bio divided by 40, incremented by one.
            if(string.length() - (i+1)*50 >= 0)
            { //Is there more than 40 characters left in the bio?
                //Does this intrude upon the 'sacred space' that is the "Side: Main" info?"
                //Draw the substring - 40 characters from the last area.
                g.drawString(string.substring(i*50, (i+1)*50), 10, 50 + i*15*MAX_TILEH*8);
     
            }
            else //If there is less than 40 characters left...
            {
                //Avoiding info space.
                //Drawing the rest of the substring.
                g.drawString(string.substring(i*40), 10,50 + i*15*MAX_TILEH*8);
            }
        }
    }*/
    //draws the Day Start screen
    public void drawVictoryScreen(Graphics2D g){
        //fill in background
        g.setColor(Color.black);
        g.fillRect(0,0,MAX_TILEW*16,MAX_TILEH*16);
        
        //draw the basic format
        g.drawImage(MiscGraphics.getSky(),0,0,this);
        g.drawImage(MiscGraphics.getWin(),0,0,this);
        g.drawImage(MiscGraphics.getLose(),0,0,this);
        g.drawImage(MiscGraphics.getDays(),0,0,this);
        
        //write name and day
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(m.getMapName(),14,100);
        g.drawString(b.getDay()+" Days",109,100);
        
        //draw CO heads
        //draw victorious COs
        int numArmies = b.getNumArmies();
        Army varmies[] = new Army[numArmies];
        for(int i=numArmies-1; i >= 0; i--){
            varmies[i] = b.getArmy(i);
            if(varmies[i].getAltCO()!=null){
                if(!varmies[i].getAltCO().altCostume)
                    g.drawImage(MiscGraphics.getCOSheet(varmies[i].getAltCO().getID()),24+i*48,0,(24+i*48)+48,48,48,350,96,398,this);
                else
                    g.drawImage(MiscGraphics.getCOSheet(varmies[i].getAltCO().getID()),24+i*48,0,(24+i*48)+48,48,273,350,320,398,this);
            }
            if(!varmies[i].getCO().altCostume)
                g.drawImage(MiscGraphics.getCOSheet(varmies[i].getCO().getID()),0+i*48,0,(0+i*48)+48,48,48,350,96,398,this);
            else
                g.drawImage(MiscGraphics.getCOSheet(varmies[i].getCO().getID()),0+i*48,0,(0+i*48)+48,48,273,350,320,398,this);
        }
        //draw defeated COs
        int numStatArmies = b.getNumStatArmies();
        int lpos = numStatArmies-numArmies-1;
        for(int i=numStatArmies-1; i >=0; i--){
            boolean isLoser = true;
            Army statArmy = b.getStatArmy(i);
            //check if a victorious army
            for(int j=0; j < varmies.length; j++){
                if(statArmy == varmies[j])isLoser = false;
            }
            if(isLoser){
                if(statArmy.getAltCO()!=null){
                    if(!statArmy.getAltCO().altCostume)
                        g.drawImage(MiscGraphics.getCOSheet(statArmy.getAltCO().getID()),94+lpos*48,144,(94+lpos*48)+48,192,96,350,143,398,this);
                    else
                        g.drawImage(MiscGraphics.getCOSheet(statArmy.getAltCO().getID()),94+lpos*48,144,(94+lpos*48)+48,192,321,350,368,398,this);
                }
                if(!statArmy.getCO().altCostume)
                    g.drawImage(MiscGraphics.getCOSheet(statArmy.getCO().getID()),70+lpos*48,144,(70+lpos*48)+48,192,96,350,143,398,this);
                else
                    g.drawImage(MiscGraphics.getCOSheet(statArmy.getCO().getID()),70+lpos*48,144,(70+lpos*48)+48,192,321,350,368,398,this);
                lpos--;
            }
        }
    }
    
    //draws the Day Start screen
    public void drawEndStats(Graphics2D g){
        Army statArmy = b.getStatArmy(statArmyIndex);
        
        //fill in background
        g.setColor(Color.black);
        g.fillRect(0,0,MAX_TILEW*16,MAX_TILEH*16);
        
        //draw basic format
        g.drawImage(MainMenuGraphics.getBackground(),0,0,this);
        g.drawImage(MiscGraphics.getStatsBox(),0,0,this);
        
        //write name and day
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(m.getMapName(),24,34);
        g.drawString(b.getDay()+" Days",194,34);
        
        //draw CO faces and names
        if(statArmy!=null){
            if(!statArmy.getCO().altCostume)
                g.drawImage(MiscGraphics.getCOSheet(statArmy.getCO().getID()),51,41,51 + 32, 41 + 12, 144, 350, 175, 362, this);
            else
                g.drawImage(MiscGraphics.getCOSheet(statArmy.getCO().getID()),51,41,51 + 32, 41 + 12, 369, 350, 400, 362, this);
            g.setColor(Color.black);
            g.setFont(new Font("SansSerif", Font.PLAIN, 10));
            g.drawString(statArmy.getCO().getName(),90,49);
            if(statArmy.getAltCO()!=null){
                if(!statArmy.getAltCO().altCostume)
                    g.drawImage(MiscGraphics.getCOSheet(statArmy.getAltCO().getID()),148,41,148 + 32, 41 + 12, 144, 350, 175, 362, this);
                else
                    g.drawImage(MiscGraphics.getCOSheet(statArmy.getAltCO().getID()),148,41,148 + 32, 41 + 12, 369, 350, 400, 362, this);
                g.setColor(Color.black);
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.drawString(statArmy.getAltCO().getName(),186,49);
            }
            
            if(statType == 0){
                //draw remaining buildings
                Image temp;
                temp = TerrainGraphics.getTerrainImage(10,statArmy.getColor()+1);
                g.drawImage(temp,34,118-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(11,statArmy.getColor()+1);
                g.drawImage(temp,89,118-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(12,statArmy.getColor()+1);
                g.drawImage(temp,144,118-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(13,statArmy.getColor()+1);
                g.drawImage(temp,62,150-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(14,statArmy.getColor()+1);
                g.drawImage(temp,117,150-(temp.getHeight(this)-16),this);
                temp = TerrainGraphics.getTerrainImage(17,statArmy.getColor()+1);
                g.drawImage(temp,172,150-(temp.getHeight(this)-16),this);
                //calculate buildings
                int proptypes[] = new int[6];
                Property props[] = statArmy.getProperties();
                for(int i = 0; i < props.length; i++){
                    if(props[i].getIndex() == 10)proptypes[0]++;
                    else if(props[i].getIndex() == 11)proptypes[1]++;
                    else if(props[i].getIndex() == 12)proptypes[2]++;
                    else if(props[i].getIndex() == 13)proptypes[3]++;
                    else if(props[i].getIndex() == 14)proptypes[4]++;
                    else if(props[i].getIndex() == 17)proptypes[5]++;
                }
                //draw strings
                g.setColor(Color.black);
                g.setFont(new Font("SansSerif", Font.PLAIN, 10));
                g.drawString("x"+proptypes[0],57,134);
                g.drawString("x"+proptypes[1],112,134);
                g.drawString("x"+proptypes[2],167,134);
                g.drawString("x"+proptypes[3],86,165);
                g.drawString("x"+proptypes[4],141,165);
                g.drawString("x"+proptypes[5],196,165);
            }else if(statType == 1){
                g.drawString("Units Built",57,134);
            }else if(statType == 2){
                g.drawString("Units Lost",57,134);
            }
        }
    }
    
    public void drawMiniMap(Graphics2D g, int x, int y){
        Image minimap = MiscGraphics.getMinimap();
        Map map = m;
        for(int i=0; i < map.getMaxCol(); i++){
            for(int j=0; j < map.getMaxRow();j++){
                //draw terrain
                int terraintype = map.find(new Location(i,j)).getTerrain().getIndex();
                if(terraintype < 9){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,0+(terraintype*4),0,4+(terraintype*4),4,this);
                }else if(terraintype == 9){
                    int armycolor = ((Property)map.find(new Location(i,j)).getTerrain()).getOwner().getColor();
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,36+(armycolor*4),0,40+(armycolor*4),4,this);
                }else if(terraintype < 15 || terraintype == 17){
                    int armycolor = ((Property)map.find(new Location(i,j)).getTerrain()).getColor();
                    if(b.getFog(i,j))armycolor = 0;
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,76+(armycolor*4),0,80+(armycolor*4),4,this);
                }else if(terraintype == 15){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,120,0,124,4,this);
                }else if(terraintype == 16){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,128,0,132,4,this);
                }else if(terraintype == 18){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,124,0,128,4,this);
                }else if(terraintype == 19){
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,0,0,4,4,this);
                }
                
                
                //draw units
                if(map.find(new Location(i,j)).hasUnit()){
                    Unit theunit = map.find(new Location(i,j)).getUnit();
                    int armycolor = theunit.getArmy().getColor();
                    //only draw if not hidden
                    if(theunit.getArmy().getSide() != b.getArmy(b.getTurn()).getSide()){
                        //don't draw hidden enemy units
                        if(!theunit.isHidden())
                            g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,132+(armycolor*4),0,136+(armycolor*4),4,this);
                    }else{
                        //always draw friendly units
                        g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,132+(armycolor*4),0,136+(armycolor*4),4,this);
                    }
                }
            }
        }
    }
    
    //moves the cursor by a certain amount (untested)
    /*private void moveCursorRelative(int xadj, int yadj){
        if(m.onMap(cx+xadj,cy+yadj)){
            cx += xadj;
            cy += yadj;
     
            //this part needs work
            if(cy < sy/16)sy -= 16;
        }
    }*/
    
    //moves the cursor to a given position
    private void moveCursorTo(Location l){
        int x = l.getCol();
        int y = l.getRow();
        
        if(m.onMap(x,y)){
            cx = x;
            cy = y;
            
            if(cx < sx/16 || cx >= sx/16+MAX_TILEW || cy < sy/16 || cy >= sy/16+MAX_TILEH){
                sx = (cx - 8) * 16;
                sy = (cy - 6) * 16;
                if(sx < 0) sx = 0;
                if(sy < 0) sy = 0;
                if(sx+MAX_TILEW*16 > m.getMaxCol()*16) sx = m.getMaxCol()*16-MAX_TILEW*16;
                if(sy+MAX_TILEH*16 > m.getMaxRow()*16) sy = m.getMaxRow()*16-MAX_TILEH*16;
                if(sx < 0) sx = 0;
                if(sy < 0) sy = 0;
            }
            
            //center small maps
            if(m.getMaxCol() < 30){
                sx = -((30 - m.getMaxCol())/2)*16;
            }
            if(m.getMaxRow() < 20){
                sy = -((20 - m.getMaxRow())/2)*16;
            }
        }
    }
    
    //
    public void removeFromFrame(){
        parentFrame.getContentPane().remove(this);
        parentFrame.removeKeyListener(keycontroller);
        parentFrame.removeMouseListener(mousecontroller);
        parentFrame.removeMouseMotionListener(mousecontroller);
    }
    
    public void resetBattle(Battle newBattle){
        b = newBattle;
        m = b.getMap();
        selected = null;
        cx = 0;
        cy = 0;
        sx = 0;
        sy = 0;
        //center small maps
        if(m.getMaxCol() < 30){
            sx = -((30 - m.getMaxCol())/2)*16;
        }
        if(m.getMaxRow() < 20){
            sy = -((20 - m.getMaxRow())/2)*16;
        }
        item = 0;
        menu = false;
        cmenu = false;
        move = false;
        fire = false;
        silo = false;
        repair = false;
        fireRange = false;
        minimap = false;
        victory = false;
        endStats = false;
        replay = false;
        
        //scale = 1;
        
        //for(int i=0; i<10; i++){
        //    b.cursorLocation[i] = new Location(0,0);
        //}
        moveCursorTo(b.cursorLocation[b.getTurn()]);
        
        if(Options.isNetworkGame())daystart = true;
        denyContinue = false;
    }
    
    public void startReplay(){
        replay = true;
    }
    
    public void endBattle(){
        victory = true;
        //end the game asap if a snail game
        if(Options.snailGame){
            b.sendCommandToMain("endgame",Options.gamename+"\n"+Options.username+"\n"+Options.password);
        }
        //save the replay if applicable
        if(b.getBattleOptions().isRecording() && !replay){
            JFileChooser fc = new JFileChooser();
            fc.setDialogTitle("Save replay file...");
            fc.setCurrentDirectory(new File("./"));
            fc.setApproveButtonText("Save");
            /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "CW Replay Files", "replay");
            fc.setFileFilter(filter);*/
            int returnVal = fc.showOpenDialog(this);
            
            if(returnVal != 1){
                String filename = fc.getSelectedFile().getPath();
                if(filename.length()<8 || !filename.substring(filename.length()-7).equals(".replay"))filename += ".replay";
                Mission.saveReplay(filename);
            }
        }
    }
    
    private void returnToMain(){
        //end the mission
        Mission.endMission();
        
        //put a Main Menu inside the frame
        parentFrame.setSize(400,400);
        removeFromFrame();
        MainMenu mm = new MainMenu(parentFrame);
        parentFrame.getContentPane().add(mm);
        parentFrame.validate();
        parentFrame.pack();
        //hopefully, this should make the intro music work right
        if(Options.isMusicOn())Music.startMainMenuMusic();
        Options.snailGame = false;
    }
    
    private void returnToServerScreen(){
        //end the mission
        Mission.endMission();
        
        //put a Main Menu inside the frame
        parentFrame.setSize(400,400);
        removeFromFrame();
        MainMenu mm = new MainMenu(parentFrame);
        parentFrame.getContentPane().add(mm);
        parentFrame.validate();
        parentFrame.pack();
        //hopefully, this should make the intro music work right
        if(Options.isMusicOn())Music.startMainMenuMusic();
        mm.returnToServerInfo();
    }
    
    //Builds a new unit
    private void buildUnit(Map m, Tile t, int type){
        int x = t.getLocation().getCol();
        int y = t.getLocation().getRow();
        Army a = ((Property)t.getTerrain()).getOwner();
        
        switch(type){
            case 0:
                new Infantry(x,y,a,m);
                break;
            case 1:
                new Mech(x,y,a,m);
                break;
            case 2:
                new Tank(x,y,a,m);
                break;
            case 3:
                new MDTank(x,y,a,m);
                break;
            case 4:
                new Recon(x,y,a,m);
                break;
            case 5:
                new AntiAir(x,y,a,m);
                break;
            case 6:
                new Missiles(x,y,a,m);
                break;
            case 7:
                new Artillery(x,y,a,m);
                break;
            case 8:
                new Rockets(x,y,a,m);
                break;
            case 9:
                new APC(x,y,a,m);
                break;
            case 10:
                new Lander(x,y,a,m);
                break;
            case 11:
                new Cruiser(x,y,a,m);
                break;
            case 12:
                new Submarine(x,y,a,m);
                break;
            case 13:
                new Battleship(x,y,a,m);
                break;
            case 14:
                new TCopter(x,y,a,m);
                break;
            case 15:
                new BCopter(x,y,a,m);
                break;
            case 16:
                new Fighter(x,y,a,m);
                break;
            case 17:
                new Bomber(x,y,a,m);
                break;
            case 18:
                new Neotank(x,y,a,m);
                break;
            case 19:
                new MegaTank(x,y,a,m);
                break;
            case 20:
                new Piperunner(x,y,a,m);
                break;
            case 21:
                new BlackBoat(x,y,a,m);
                break;
            case 22:
                new Carrier(x,y,a,m);
                break;
            case 23:
                new Stealth(x,y,a,m);
                break;
            case 24:
                new BlackBomb(x,y,a,m);
                break;
            case 25:
                new Battlecraft(x,y,a,m);
                break;
            case 26:
                new Artillerycraft(x,y,a,m);
                break;
            case 27:
                new Shuttlerunner(x,y,a,m);
                break;
            case 28:
                new Zeppelin(x,y,a,m);
                break;
            case 29:
                new Spyplane(x,y,a,m);
                break;
            case 30:
                new Destroyer(x,y,a,m);
                break;
            case 31:
                new Oozium(x,y,a,m);
                break;
        }
    }
    
    public void pressedA(){
        //what is selecting Vimes? (--Urusan)
        //It's used for actions that require selection. <_<
        if(!b.diagQueue.isEmpty())
            b.diagQueue.get(0).pressedA();
        if(b.animlock)
            return;
        if(b.getArmy(b.getTurn()).getCO().isSelecting()) {
            if(b.getArmy(b.getTurn()).getCO().validSelection(b.getMap().find(new Location(cx, cy)))) {
                System.out.println("Selection cycle activated!");
                b.getArmy(b.getTurn()).getCO().selectAction(b.getMap().find(new Location(cx, cy)));
                if(b.getBattleOptions().isRecording())b.getReplay().push(new SelectionEvent(cx,cy,b.getDay(),b.getTurn()));
                return;
            } else {
                b.getArmy(b.getTurn()).getCO().invalidSelection();
                return;
            }
        }
        if(delete){
            if(m.find(new Location(cx,cy)).hasUnit()){
                Unit dunit = m.find(new Location(cx,cy)).getUnit();
                if(dunit.getArmy()==b.getArmy(b.getTurn())){
                    if(dunit.isActive() || dunit.getArmy().getCO().canAlwaysDelete()){
                        System.out.println("Delete Unit");
                        dunit.eliminateUnit();
                        if(b.getBattleOptions().isRecording())b.getReplay().push(new DeleteEvent(cx,cy,b.getDay(),b.getTurn()));
                        boolean defeat = false;
                        if(dunit.isRout()){
                            defeat = b.removeArmy(dunit.getArmy(),dunit.getArmy(),false);
                            if(defeat)endBattle();
                            if(!defeat && Options.snailGame)returnToServerScreen();
                            
                            
                        }
                        delete = false;
                        return;
                    }
                }
                
            }
            delete = false;
        }
        if(!daystart && !intel && !victory && !endStats && !replay){
            if(menu){
                menu=false;
                b.cursorLocation[b.getTurn()] = new Location(cx,cy);
                
                int menuselection = currentMenu.doMenuItem();
                currentMenu = null;
                if(menuselection == 1){
                    intel = true;
                }else if(menuselection == 2){
                    menu = true;
                    currentMenu = new OptionsMenu(this);
                }else if(menuselection == 3){
                    MAX_TILEW = getPositiveInteger("Enter a width in tiles for the screen",16);
                    MAX_TILEH = getPositiveInteger("Enter a height in tiles for the screen",12);
                    parentFrame.pack();
                }else if(menuselection == 4){
                    if(scale < 4)
                        scale++;
                    else
                        scale = 1;
                    parentFrame.pack();
                }else if(menuselection == 5){
                    //endBattle();
                    returnToMain();
                }else if(menuselection == 6){
                    if(b.getBattleOptions().isRecording())b.getReplay().push(new CWEvent(7,b.getDay(),b.getTurn()));
                    boolean defeat = b.removeArmy(b.getArmy(b.getTurn()),null,false);
                    if(defeat){
                        endBattle();
                    }
                    if(!defeat && Options.snailGame){
                        returnToServerScreen();
                    }
                }else if(menuselection == 7){
                    if(Options.isMusicOn())Options.turnMusicOff();
                    else Options.turnMusicOn();
                    if(Options.isMusicOn())Music.startMusic(b.getArmy(b.getTurn()).getCO().getID());
                }else if(menuselection == 8){
                    delete = true;
                }else if(menuselection == 9){
                    if(b.getBattleOptions().isRecording()){
                        if(!Options.isNetworkGame() && !Options.snailGame){
                            System.out.println("Replay");
                            System.out.println(b.getReplay());
                            JFileChooser fc = new JFileChooser();
                            fc.setDialogTitle("Save replay file...");
                            fc.setCurrentDirectory(new File("./"));
                            fc.setApproveButtonText("Save");
                            /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                    "CW Replay Files", "replay");
                            fc.setFileFilter(filter);*/
                            int returnVal = fc.showOpenDialog(this);
                            
                            if(returnVal != 1){
                                String filename = fc.getSelectedFile().getPath();
                                if(filename.length()<8 || !filename.substring(filename.length()-7).equals(".replay"))filename += ".replay";
                                Mission.saveReplay(filename);
                            }
                        }
                    }
                }else if(menuselection == 11){
                    if(!Options.isNetworkGame() && !Options.snailGame){
                        System.out.println("Replay");
                        System.out.println(b.getReplay());
                    }
                }else if(menuselection == 10){
                    System.out.println("Turn Start");
                    daystart = true;
                    if(Options.isNetworkGame() && Options.getSend())denyContinue = true;
                    if(Options.snailGame){
                        returnToServerScreen();
                    }
                }else if(menuselection == 12){
                    info = true;
                }else if(menuselection == 13){
                    if(!Options.snailGame){
                        System.out.println("Save");
                        JFileChooser fc = new JFileChooser();
                        fc.setDialogTitle("Save game...");
                        fc.setCurrentDirectory(new File("./"));
                        fc.setApproveButtonText("Save");
                        /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                                "CW Save Files", "save");
                        fc.setFileFilter(filter);*/
                        int returnVal = fc.showOpenDialog(this);
                        
                        if(returnVal != 1){
                            String x = fc.getSelectedFile().getPath();
                            if(x.length()<6 || !x.substring(x.length()-5).equals(".save"))x += ".save";
                            Mission.saveMission(x);
                        }
                    }else{
                        Mission.saveMission("./temporarysave.save");
                        b.sendFile("usave.pl", Options.gamename,"./temporarysave.save");
                    }
                }else if(menuselection == 14){
                    System.out.println("Load");
                    boolean validSave = false;
                    
                    JFileChooser fc = new JFileChooser();
                    fc.setDialogTitle("Load game...");
                    fc.setCurrentDirectory(new File("./"));
                    fc.setApproveButtonText("Load");
                    /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                            "CW Save Files", "save");
                    fc.setFileFilter(filter);*/
                    int returnVal = fc.showOpenDialog(this);
                    
                    if(returnVal != 1){
                        String filename = fc.getSelectedFile().getPath();
                        File saveFile = new File(filename);
                        if(saveFile.exists()){
                            validSave = true;
                        }
                        
                        if(validSave)Mission.loadMission(filename);
                    }
                }else if(menuselection == -10){
                    endBattle();
                }
                
                moveCursorTo(b.cursorLocation[b.getTurn()]);
            }else if(cmenu){
                cmenu=false;
                int action = currentMenu.doMenuItem();
                System.out.println(action);
                if(action == 1)
                {
                    fire = true;
                    
                    //[NEW]
                    if(contextTargs.size() > 0)
                    {
                    	currContextTarg = 0;
                    	cx = contextTargs.get(currContextTarg).getCol();
                    	cy = contextTargs.get(currContextTarg).getRow();
                    }
                }
                else if(action == 2)
                {
                    boolean endGame = ((Property)m.find(selected.getLocation()).getTerrain()).capture(selected);
                    endUnitTurn(2,null);
                    if(endGame)endBattle();
                }else if(action == 3){
                    ((APC)selected).resupplyAdjacent();
                    endUnitTurn(16,null);
                    if(backAllowed == false)backAllowed = true;
                }else if(action == 4){
                    //Add Money if over 10HP
                    int temphp = selected.getDisplayHP() + moveTempUnit.getDisplayHP() - 10;
                    if(temphp > 0)b.getArmy(b.getTurn()).addFunds((temphp*selected.getPrice())/10);
                    //add HP to unit being moved onto
                    moveTempUnit.heal(selected.getDisplayHP()*10);
                    //add supplies to unit being moved onto
                    moveTempUnit.addGas(selected.getGas());
                    moveTempUnit.addAmmo(selected.getAmmo());
                    //make the dived state of the unit being moved onto the same as the one moving
                    moveTempUnit.dived = selected.isDived();
                    //Remove Unit being moved
                    m.remove(selected);
                    selected.getArmy().removeUnit(selected);
                    //end Unit's turn
                    selected = moveTempUnit;
                    endUnitTurn(4,null);
                    //moveTempUnit.setActive(false);
                    selected = null;
                }else if(action == 5){
                    Transport trans = (Transport) moveTempUnit;
                    trans.load(selected);
                    //Remove Unit being moved from map
                    m.remove(selected);
                    //end Unit's turn
                    endUnitTurn(5,(Unit)trans);
                }else if(action == 6){
                    unload = 1;
                }else if(action == 7){
                    unload = 2;
                }else if(action == 8){
                    silo = true;
                }else if(action == 9){
                    String x = JOptionPane.showInputDialog("About to Explode - Are you sure? y/n");
                    if(x != null && !x.equals("") && (x.charAt(0) == 'y' || x.charAt(0) == 'Y')){
                        m.doExplosion(3,5,selected.getLocation().getCol(),selected.getLocation().getRow(), false);
                        selected.eliminateUnit();
                        endUnitTurn(9,null);
                    }else{
                        cmenu = true;
                    }
                }else if(action == 10){
                    repair = true;
                }else if(action == 11){
                    ((Submarine)selected).dive();
                    endUnitTurn(11,null);
                }else if(action == 12){
                    ((Submarine)selected).rise();
                    endUnitTurn(12,null);
                }else if(action == 13){
                    ((Stealth)selected).hide();
                    endUnitTurn(13,null);
                }else if(action == 14){
                    ((Stealth)selected).appear();
                    endUnitTurn(14,null);
                }else if(action == 22){
                    special1 = true;
                }else if(action == 23){
                    special2 = true;
                }else{
                    endUnitTurn(0,null);
                    if(backAllowed == false)backAllowed = true;
                }
                if(!cmenu)currentMenu = null;
            }else if(bmenu){
                int newUnit = currentMenu.doMenuItem();
                if(newUnit != -1){
                    bmenu = false;
                    buildUnit(m,m.find(new Location(cx,cy)),newUnit);
                    //if(newUnit != -1){
                    b.getArmy(b.getTurn()).removeFunds(m.find(new Location(cx,cy)).getUnit().getPrice());
                    //}
                    currentMenu = null;
                    Army[] armies = b.getArmies();
                    for(int i = 0; i < armies.length; i++) {
                        if(m.find(new Location(cx,cy)).getUnit().getArmy().getSide()!= armies[i].getSide()) {
                            armies[i].getCO().afterEnemyAction(m.find(new Location(cx,cy)).getUnit(), 15, null, true);
                            if(armies[i].getAltCO() != null)
                                armies[i].getAltCO().afterEnemyAction(m.find(new Location(cx,cy)).getUnit(), 15, null, false);
                        }
                    }
                    m.find(new Location(cx,cy)).getUnit().getArmy().getCO().afterAction(m.find(new Location(cx,cy)).getUnit(), 15, null, true);
                    if(m.find(new Location(cx,cy)).getUnit().getArmy().getAltCO() != null)
                        m.find(new Location(cx,cy)).getUnit().getArmy().getAltCO().afterAction(m.find(new Location(cx,cy)).getUnit(), 15, null, false);
                    
                    b.calculateFoW();
                    if(b.getBattleOptions().isRecording())b.getReplay().push(new BuildEvent(newUnit,cx,cy,b.getDay(),b.getTurn()));
                }
            }else if(fireRange){
                //Don't do anything
            }else if(unload > 0){
                Transport trans = (Transport) selected;
                if(trans.checkUnloadRange(new Location(cx,cy),unload)){
                    if(m.find(new Location(cx,cy)).hasUnit()){
                        //ambush
                        endUnitTurn(17,null);
                        unload = 0;
                        if(backAllowed == false)backAllowed = true;
                    }else{
                        Unit unloading = trans.unload(unload);
                        unloading.forceMove(new Location(cx,cy));
                        unloading.setActive(false);
                        if(trans.getUnitsCarried()>0){
                            currentMenu = ContextMenu.generateContext(selected,false,false,true,this);
                            cmenu = true;
                            backAllowed = false;
                            //Activates once for unloading the first time.
                            trans.getArmy().getCO().afterAction(trans, 6, unloading, true);
                            if(trans.getArmy().getAltCO() != null)
                                trans.getArmy().getAltCO().afterAction(trans, 6, unloading, false);
                            //adds action to the queue
                            int replayAction = 6;
                            if(unload == 2)replayAction = 7;
                            CWEvent tempev= new Action(replayAction,originalLocation.getCol(),originalLocation.getRow(),selected.getPath(),cx,cy,b.getDay(),b.getTurn());
                            System.out.println(tempev);
                            b.getReplay().push(tempev);
                        }else{
                            endUnitTurn(6,unloading);
                            if(backAllowed == false)backAllowed = true;
                        }
                        unload = 0;
                    }
                }
            }
            //else if(move)
            else if(move && selected.active && (selected.getArmy().getID() == b.getArmy(b.getTurn()).getID() || b.getArmy(b.getTurn()).getCO().mayhem))
            {
                if(selected.getMoveRange().checkMove(cx,cy) || (m.find(new Location(cx,cy)).hasUnit() && m.find(new Location(cx,cy)).getUnit() == selected))
                {
                    //OOZIUM MOVE OVERIDES EVERYTHING ELSE ^_^
                    if(selected.getName().equals("Oozium")){
                        if(!m.find(new Location(cx,cy)).hasUnit() || m.find(new Location(cx,cy)).getUnit().getArmy().getSide() != selected.getArmy().getSide()){
                            originalLocation = selected.getLocation();
                            move = false;
                            boolean gameEnd = selected.move(new Location(cx,cy));
                            endUnitTurn(19,null);
                            if(gameEnd)endBattle();
                        }else if(m.find(new Location(cx,cy)).hasUnit() && m.find(new Location(cx,cy)).getUnit() == selected){
                            move = false;
                            endUnitTurn(19,null);
                        }
                    }else{
                        originalLocation = selected.getLocation();
                        
                        if(m.find(originalLocation).getTerrain() instanceof Property) {
                            originalcp = ((Property)m.find(originalLocation).getTerrain()).getCapturePoints();
                        }
                        
                        if(!m.find(new Location(cx,cy)).hasUnit() || (m.find(new Location(cx,cy)).getUnit().isHidden()&&(m.find(new Location(cx,cy)).getUnit().getArmy().getSide() != selected.getArmy().getSide()))) {
                            boolean ambush = selected.move(new Location(cx,cy));
                            if(ambush){
                                move = false;
                                
                                Army[] armies = b.getArmies();
                                
                                for(int i = 0; i < armies.length; i++) {
                                    if(selected.getTrapper().getArmy().getSide() != armies[i].getSide()) {
                                        armies[i].getCO().afterEnemyAction(selected, 18, null, true);
                                        
                                        if(armies[i].getAltCO() != null)
                                            armies[i].getAltCO().afterEnemyAction(selected, 18, null, false);
                                    }
                                }
                                
                                endUnitTurn(18,null);
                                
                                if(backAllowed == false) {
                                    backAllowed = true;
                                }
                            } else {
                                move = false;
                                currentMenu = ContextMenu.generateContext(selected,false,false,false,this);
                                contextTargs = ContextMenu.getContextTargs();
                                cmenu = true;
                            }
                        }else{
                            newLocation = new Location(cx,cy);
                            moveTempUnit = m.find(new Location(cx,cy)).getUnit();
                            
                            if(moveTempUnit != selected){
                                //JOIN
                                if(moveTempUnit.getUnitType() == selected.getUnitType()){
                                    if(moveTempUnit.getArmy() == selected.getArmy()){
                                        if(moveTempUnit.getDisplayHP()!=10 && !moveTempUnit.noJoin && !selected.noJoin){
                                            if(moveTempUnit instanceof Transport){
                                                if(((Transport)moveTempUnit).getUnitsCarried()!=0 || ((Transport)selected).getUnitsCarried()!=0)return;
                                            }
                                            move = false;
                                            currentMenu = ContextMenu.generateContext(selected,true,false,false,this);
                                            cmenu = true;
                                        }
                                    }
                                }
                                
                                //LOAD
                                if(moveTempUnit instanceof Transport){
                                    Transport trans = (Transport)moveTempUnit;
                                    if(trans.canCarry(selected.getUnitType()) && trans.roomAvailable() && !trans.noLoad){
                                        move = false;
                                        currentMenu = ContextMenu.generateContext(selected,false,true,false,this);
                                        cmenu = true;
                                    }
                                }
                            }else{
                                move = false;
                                currentMenu = ContextMenu.generateContext(selected,false,false,false,this);
                                contextTargs = ContextMenu.getContextTargs();
                                cmenu = true;
                            }
                        }
                    }
                }
            }else if(silo){
                silo = false;
                
                ((Silo)m.find(selected.getLocation()).getTerrain()).launch();
                Animation launchup = new Animation(b, MiscGraphics.getMissileUp(), 1, 0, 0, selected.getLocation().getCol()*16, selected.getLocation().getRow()*16, selected.getLocation().getCol()*16, -40, 100, 100, 25, 0, 0);
                Animation launchdown = new Animation(b, MiscGraphics.getMissileUp(), 1, 0, 0, cx*16, 0, cx*16, cy*16, 100, 100, 25, 10 , 0);
                Animation temp = new Animation();
                temp.setSiloExplosion(new Location(cx,cy),b,0);
                
                launchup.setup(false,false);
                launchdown.setup(false,false);
                temp.setup(false,true);
                
                launchdown.linkTo(temp);
                launchup.linkTo(launchdown);
                
                launchup.start();
                
                endUnitTurn(8,null);
                //LAUNCH
                m.doExplosion(2,3,cx,cy, false);
            }else if(special1){
                if(selected.getArmy().getCO().canTargetSpecial1(selected, (new Location(cx,cy)))) {
                    special1 = false;
                    selected.getArmy().getCO().useSpecial1(selected, (new Location(cx,cy)));
                    
                    endUnitTurn(22,null);
                }
            }else if(special2){
                if(selected.getArmy().getCO().canTargetSpecial2(selected, (new Location(cx,cy)))) {
                    special2 = false;
                    selected.getArmy().getCO().useSpecial2(selected, (new Location(cx,cy)));
                    
                    endUnitTurn(23,null);
                }
            }else if(m.find(new Location(cx,cy)).hasUnit()){
                if(fire){
                    if(selected != m.find(new Location(cx,cy)).getUnit() && BaseDMG.find(selected, m.find(new Location(cx,cy)).getUnit(),b.getBattleOptions().isBalance()) > -1){
                        if(selected.getArmy().getSide() != m.find(new Location(cx,cy)).getUnit().getArmy().getSide()){
                            if(selected.checkFireRange(new Location(cx,cy))){
                                if(!m.find(new Location(cx,cy)).getUnit().isHidden()){
                                    boolean gameEnd = selected.fire(m.find(new Location(cx,cy)).getUnit());
                                    fire = false;
                                    endUnitTurn(1,null);
                                    if(gameEnd)endBattle();
                                    if(Options.killedSelf){
                                        Options.killedSelf = false;
                                        if(Options.snailGame)returnToServerScreen();
                                    }
                                }
                            }
                        }
                    }
                }else if(repair){
                    if(selected != m.find(new Location(cx,cy)).getUnit()){
                        if(selected.checkAdjacent(new Location(cx,cy))){
                            Unit temp = m.find(new Location(cx,cy)).getUnit();
                            if(selected.getArmy() == temp.getArmy()){
                                if(temp.getDisplayHP() != 10 && (temp.getPrice()/10 * temp.repairMod) <= b.getArmy(b.getTurn()).getFunds()&& !temp.noRepaired){
                                    temp.heal(10);
                                    b.getArmy(b.getTurn()).removeFunds((int)(temp.getPrice()/10* temp.repairMod));
                                }
                                if(!temp.noResupplied)
                                    temp.resupply();
                                repair = false;
                                endUnitTurn(10,temp);
                            }
                        }
                    }
                }
                else
                {
                    //if(m.find(new Location(cx,cy)).getUnit().isActive())
                	//if(m.find(new Location(cx,cy)).hasUnit())
                	//[CHANGE]
                	if(visibleAtXY(cx, cy))
                    {
                        //select
                        selected = m.find(new Location(cx,cy)).getUnit();
                        move = true;
                        outOfMoveRange = false;
                        selected.calcMoveTraverse();
                        selected.getPath().resetPath();
                    }else{
                        //Same as hitting m
                        getBattleMenu();
                        return;
                    }
                }
            }else if(m.find(new Location(cx,cy)).getTerrain() instanceof Invention){
                if(fire){
                    if(selected.checkFireRange(new Location(cx,cy))){
                        Invention inv = (Invention) m.find(new Location(cx,cy)).getTerrain();
                        if(inv.find(selected.getAmmo(),selected.getUnitType()) > -1){
                            //if(selected.getArmy().getSide() != m.find(new Location(cx,cy)).getUnit().getArmy().getSide()){
                            selected.fire(inv);
                            fire = false;
                            endUnitTurn(20,null);
                            //}
                        }
                    }
                }else{
                    //Same as hitting m
                    getBattleMenu();
                    return;
                }
            }else if(m.find(new Location(cx,cy)).getTerrain() instanceof Property && !fire && !repair){
                currentMenu = BuildMenu.generateContext((Property)m.find(new Location(cx,cy)).getTerrain(),b.getTurn(),b,this);
                if(currentMenu != null){
                    bmenu = true;
                }else{
                    //Same as hitting m
                    getBattleMenu();
                    return;
                }
            }else{
                getBattleMenu();
                return;
            }
        }else if(daystart){
            System.out.println("ending day start screen");
            if(!denyContinue)daystart = false;
            /*
           if(b.getArmies()[b.getTurn()].hasAI()){
               AI temp = new Alpha1AI(b.getArmies()[b.getTurn()]);
               b.getArmies()[b.getTurn()].ai = temp;
                b.getArmies()[b.getTurn()].runAI();
                Thread.yield();
                System.out.println("post AI");
                 System.out.println(Thread.currentThread());
        }*/}else if(victory){
            victory = false;
            endStats = true;
        }else if(endStats){
            returnToMain();
        }else if(replay){
            executeNextAction();
        }
    }
    
    public void pressedB(){
        if(!b.diagQueue.isEmpty())
            b.diagQueue.get(0).pressedA();
        if(b.animlock)
        {
            for(int i = 0; i<b.getLayerOne().size(); i++) {
                b.getLayerOne().get(i).removeTimers();
                b.getLayerOne().remove(i);
            }
            for(int i = 0; i<b.getLayerTwo().size(); i++) {
                b.getLayerTwo().get(i).removeTimers();
                b.getLayerTwo().remove(i);
            }
            for(int i = 0; i<b.getLayerThree().size(); i++) {
                b.getLayerThree().get(i).removeTimers();
                b.getLayerThree().remove(i);
            }
            for(int i = 0; i<b.getLayerFour().size(); i++) {
                b.getLayerFour().get(i).removeTimers();
                b.getLayerFour().remove(i);
            }
            for(int i = 0; i<b.queue.size(); i++) {
                b.queue.get(i).removeTimers();
                b.queue.remove(i);
            }
            
            b.animlock = false;
            return;
        }
        
        if(b.getArmy(b.getTurn()).getCO().isSelecting()) {
            b.getArmy(b.getTurn()).getCO().cancelSelection();
        }
        if(delete) {
            delete = false;
        }
        if(!daystart && !intel && !victory && !endStats && !replay && !info){
            if(backAllowed){
                //if a normal menu is active, turn it off without taking any action
                if(menu || bmenu){
                    currentMenu = null;
                    menu = false;
                    bmenu = false;
                }else if(move){
                    //if in move mode, end it and deselect the unit
                    move = false;
                    selected.direction = -1;
                    selected = null;
                }else if(cmenu || fire || (unload > 0) || silo || repair || special1 || special2){
                    //if move already done: reverse the move, end the mode, and deselect
                    selected.undoMove(originalLocation,originalcp);
                    if(cmenu){
                        cmenu = false;
                        currentMenu = null;
                    }else if(fire){
                        fire = false;
                    }else if(unload > 0){
                        unload = 0;
                    }else if(silo){
                        silo = false;
                    }else if(repair){
                        repair = false;
                    }else if (special1){
                        special1 = false;
                    }else if (special2){
                        special2 = false;
                    }
                    selected = null;
                    
                }else if(fireRange){
                    //Firing Range mode is active, turn it off
                    fireRange = false;
                    selected = null;
                }else{
                    //Nothing is on, turn on firing range mode if possible
                    if(m.find(new Location(cx,cy)).hasUnit())
                    {
                        selected=m.find(new Location(cx,cy)).getUnit();
                        
                        // [CHANGE]
                        if(visibleAtXY(cx, cy))
                        {
                            selected.calcMoveTraverse();
                            fireRange = true;
                        }
                        else
                        {
                            selected = null;
                        }
                    }
                }
            }
        }else if(intel){
            intel = false;
        }else if(info){
            info = false;
        }else if(endStats){
            victory = true;
            endStats = false;
        }else if(replay){
            replay = false;
            b.getReplay().clear();
        }
    }
    
    private void executeNextAction(){
        executeNextAction(b.getNextReplayEvent());
    }
    
    public void executeNextAction(CWEvent n){
        System.out.println(n);
        if(n == null){
            //end of replay, resume
            replay = false;
        }else{
            switch(n.getType()){
                case 0:
                    //action
                    Action a = (Action)n;
                    selected = m.find(new Location(a.getUnitX(),a.getUnitY())).getUnit();
                    originalLocation = new Location(a.getUnitX(),a.getUnitY());
                    if(a.getID()==4){
                        //join
                        moveTempUnit = m.find(new Location(a.getX(),a.getY())).getUnit();
                        //Add Money if over 10HP
                        int temphp = selected.getDisplayHP() + moveTempUnit.getDisplayHP() - 10;
                        if(temphp > 0)b.getArmy(b.getTurn()).addFunds((temphp*selected.getPrice())/10);
                        //add HP to unit being moved onto
                        moveTempUnit.heal(selected.getDisplayHP()*10);
                        //add supplies to unit being moved onto
                        moveTempUnit.addGas(selected.getGas());
                        moveTempUnit.addAmmo(selected.getAmmo());
                        //make the dived state of the unit being moved onto the same as the one moving
                        moveTempUnit.dived = selected.isDived();
                        //Remove Unit being moved
                        m.remove(selected);
                        selected.getArmy().removeUnit(selected);
                        //end Unit's turn
                        //NEEDS TESTING
                        selected = moveTempUnit;
                        endUnitTurn(4,null);
                        //moveTempUnit.setActive(false);
                        selected = null;
                        break;
                    }else if(a.getID()==5){
                        //load
                        moveTempUnit = m.find(new Location(a.getX(),a.getY())).getUnit();
                        Transport trans = (Transport) moveTempUnit;
                        trans.load(selected);
                        //Remove Unit being moved from map
                        m.remove(selected);
                        //end Unit's turn
                        endUnitTurn(5,(Unit)trans);
                        break;
                    }else{
                        if(selected instanceof Oozium){
                            //Ooziums act differently
                            selected.calcMoveTraverse();
                            selected.setPath(a.getPath());
                            if(m.find(new Location(a.getX(),a.getY())).hasUnit() && m.find(new Location(a.getX(),a.getY())).getUnit() == selected){
                                endUnitTurn(19,null);
                            }else{
                                boolean gameEnd = selected.move(new Location(a.getX(),a.getY()));
                                endUnitTurn(19,null);
                                if(gameEnd)endBattle();
                            }
                        }else{
                            //move
                            selected.calcMoveTraverse();
                            selected.setPath(a.getPath());
                            selected.move(a.getPath().findEndCoordinates());
                            //action
                            switch(a.getID()){
                                case 0:
                                    //wait
                                    endUnitTurn(0,null);
                                    break;
                                case 1:
                                    //fire
                                    if(m.find(new Location(a.getX(),a.getY())).hasUnit()){
                                        //unit
                                        boolean gameEnd = selected.fire(m.find(new Location(a.getX(),a.getY())).getUnit());
                                        fire = false;
                                        endUnitTurn(1,null);
                                        if(gameEnd)endBattle();
                                    }else{
                                        //invention
                                        Invention inv = (Invention) m.find(new Location(a.getX(),a.getY())).getTerrain();
                                        selected.fire(inv);
                                        fire = false;
                                        endUnitTurn(20,null);
                                    }
                                    break;
                                case 2:
                                    //capture
                                    boolean endGame = ((Property)m.find(selected.getLocation()).getTerrain()).capture(selected);
                                    endUnitTurn(2,null);
                                    if(endGame)endBattle();
                                    break;
                                case 3:
                                    //resupply
                                    ((APC)selected).resupplyAdjacent();
                                    endUnitTurn(16,null);
                                    break;
                                case 6:
                                    //unload slot 1
                                case 7:
                                    //unload slot 2
                                    Transport trans = (Transport) selected;
                                    if(m.find(new Location(a.getX(),a.getY())).hasUnit()){
                                        //ambush
                                        endUnitTurn(17,null);
                                    }else{
                                        int tempUnload = 1;
                                        if(a.getID()==7)tempUnload = 2;
                                        Unit unloading = trans.unload(tempUnload);
                                        unloading.forceMove(new Location(a.getX(),a.getY()));
                                        unloading.setActive(false);
                                        if(trans.getUnitsCarried()>0){
                                            //Activates once for unloading the first time.
                                            trans.getArmy().getCO().afterAction(trans, 6, unloading, true);
                                            if(trans.getArmy().getAltCO() != null)
                                                trans.getArmy().getAltCO().afterAction(trans, 6, unloading, false);
                                            //do next action ASAP
                                            Action a2 = (Action) b.getNextReplayEvent();
                                            System.out.println(a2);
                                            int nextAction = a2.getID();
                                            if(nextAction==0){
                                                endUnitTurn(0,null);
                                            }else if(nextAction == 6){
                                                if(m.find(new Location(a2.getX(),a2.getY())).hasUnit()){
                                                    //ambush
                                                    endUnitTurn(17,null);
                                                }else{
                                                    unloading = trans.unload(1);
                                                    unloading.forceMove(new Location(a2.getX(),a2.getY()));
                                                    unloading.setActive(false);
                                                }
                                            }else{
                                                endUnitTurn(6,unloading);
                                            }
                                        }
                                    }
                                    break;
                                case 8:
                                    //launch
                                    ((Silo)m.find(selected.getLocation()).getTerrain()).launch();
                                    endUnitTurn(8,null);
                                    m.doExplosion(2,3,a.getX(),a.getY(), false);
                                    break;
                                case 9:
                                    //explode
                                    m.doExplosion(3,5,selected.getLocation().getCol(),selected.getLocation().getRow(), false);
                                    selected.eliminateUnit();
                                    endUnitTurn(9,null);
                                    break;
                                case 10:
                                    //repair
                                    Unit temp = m.find(new Location(a.getX(),a.getY())).getUnit();
                                    if(temp.getDisplayHP() != 10 && temp.getPrice()/10 <= b.getArmy(b.getTurn()).getFunds()){
                                        temp.heal(10);
                                        b.getArmy(b.getTurn()).removeFunds(temp.getPrice()/10);
                                    }
                                    temp.resupply();
                                    endUnitTurn(10,temp);
                                    break;
                                case 11:
                                    //dive
                                    ((Submarine)selected).dive();
                                    endUnitTurn(11,null);
                                    break;
                                case 12:
                                    //rise
                                    ((Submarine)selected).rise();
                                    endUnitTurn(12,null);
                                    break;
                                case 13:
                                    //hide
                                    ((Stealth)selected).hide();
                                    endUnitTurn(13,null);
                                    break;
                                case 14:
                                    //appear
                                    ((Stealth)selected).appear();
                                    endUnitTurn(14,null);
                                    break;
                                case 22:
                                    //Special1
                                    selected.getArmy().getCO().useSpecial1(selected, (new Location(cx,cy)));
                                    endUnitTurn(22,null);
                                case 23:
                                    selected.getArmy().getCO().useSpecial2(selected, (new Location(cx,cy)));
                                    endUnitTurn(23,null);
                            }
                        }
                    }
                    moveCursorTo(new Location(a.getX(),a.getY()));
                    break;
                case 1:
                    //build
                    BuildEvent be = (BuildEvent)n;
                    moveCursorTo(new Location(be.getX(),be.getY()));
                    buildUnit(m,m.find(new Location(be.getX(),be.getY())),be.getUnitType());
                    b.getArmy(b.getTurn()).removeFunds(m.find(new Location(cx,cy)).getUnit().getPrice());
                    Army[] armies = b.getArmies();
                    for(int i = 0; i < armies.length; i++) {
                        if(m.find(new Location(cx,cy)).getUnit().getArmy().getSide()!= armies[i].getSide()) {
                            armies[i].getCO().afterEnemyAction(m.find(new Location(cx,cy)).getUnit(), 15, null, true);
                            if(armies[i].getAltCO()!= null)
                                armies[i].getAltCO().afterEnemyAction(m.find(new Location(cx,cy)).getUnit(), 15, null, false);
                        }
                    }
                    m.find(new Location(cx,cy)).getUnit().getArmy().getCO().afterAction(m.find(new Location(cx,cy)).getUnit(), 15, null, true);
                    if(m.find(new Location(cx,cy)).getUnit().getArmy().getAltCO() != null)
                        m.find(new Location(cx,cy)).getUnit().getArmy().getAltCO().afterAction(m.find(new Location(cx,cy)).getUnit(), 15, null, false);
                    
                    b.calculateFoW();
                    break;
                case 2:
                    //COP
                    b.getArmy(b.getTurn()).getCO().activateCOP();
                    break;
                case 3:
                    //SCOP
                    b.getArmy(b.getTurn()).getCO().activateSCOP();
                    break;
                case 4:
                    //Tag
                    b.getArmy(b.getTurn()).tagBreak();
                    break;
                case 5:
                    //Swap
                    if(b.getArmy(b.getTurn()).canTagSwap()){
                        b.getArmy(b.getTurn()).swap();
                        b.getArmy(b.getTurn()).setAllActive(true);
                        b.getArmy(b.getTurn()).getCO().activateSCOP();
                    }else{
                        b.getArmy(b.getTurn()).swap();
                        boolean endGame = b.endTurn();
                        if(endGame)endBattle();
                    }
                    break;
                case 6:
                    //End Turn
                    boolean endGame = b.endTurn();
                    if(endGame)endBattle();
                    break;
                case 7:
                    //Yield
                    boolean defeat = b.removeArmy(b.getArmy(b.getTurn()),null,false);
                    if(defeat){
                        endBattle();
                    }
                    break;
                case 8:
                    //Delete
                    DeleteEvent de = (DeleteEvent) n;
                    Unit dunit = m.find(new Location(de.getX(),de.getY())).getUnit();
                    moveCursorTo(new Location(de.getX(),de.getY()));
                    dunit.eliminateUnit();
                    boolean defeat2 = false;
                    if(dunit.isRout())defeat2 = b.removeArmy(dunit.getArmy(),dunit.getArmy(),false);
                    if(defeat2)endBattle();
                    break;
                case 9:
                    //Selection
                    SelectionEvent se = (SelectionEvent) n;
                    moveCursorTo(new Location(se.getX(),se.getY()));
                    if(b.getArmy(b.getTurn()).getCO().validSelection(b.getMap().find(new Location(se.getX(),se.getY())))) {
                        b.getArmy(b.getTurn()).getCO().selectAction(m.find(new Location(se.getX(),se.getY())));
                    } else
                        b.getArmy(b.getTurn()).getCO().cancelSelection();
                    break;
            }
        }
    }
    
    private void endUnitTurn(int action, Unit repaired){
        selected.setActive(false);
        b.calculateFoW();
        selected.setIfHidden();
        selected.checkForStealth(null);
        selected.checkForStealth(originalLocation);
        
        //Vimes' AfterAction() code (improved by urusan)
        Army[] armies = b.getArmies();
        for(int i = 0; i < armies.length; i++) {
            if(selected.getArmy().getSide()!= armies[i].getSide()) {
                armies[i].getCO().afterEnemyAction(selected, action, repaired, true);
                if(armies[i].getAltCO() != null)
                    armies[i].getAltCO().afterEnemyAction(selected, action, repaired, false);
            }
        }
        selected.getArmy().getCO().afterAction(selected, action, repaired, true);
        if(selected.getArmy().getAltCO() != null)
            selected.getArmy().getAltCO().afterAction(selected, action, repaired, false);
        //if recording is on, add the action to the replay queue
        if(b.getBattleOptions().isRecording() && !replay){
            int replayAction = -1;
            if(action == 16){
                replayAction = 3;
            }else if(action == 6 || action == 17){
                if(unload == 2)replayAction = 7;
                else replayAction = 6;
            }else if(action == 18){
                replayAction = 0;
            }else if(action == 19){
                replayAction = 0;
            }else if(action == 20){
                replayAction = 1;
            }else{
                replayAction = action;
            }
            
            if(replayAction >= 0){
                CWEvent tempev= new Action(replayAction,originalLocation.getCol(),originalLocation.getRow(),selected.getPath(),cx,cy,b.getDay(),b.getTurn());
                System.out.println(tempev);
                b.getReplay().push(tempev);
            }
        }
        
        selected = null;
    }
    
    private void getBattleMenu(){
        //Same as hitting m
        if(selected == null){
            if(!bmenu && !cmenu){
                if(!menu){
                    menu=true;
                    currentMenu = BattleMenu.generateContext(b,this);
                }else menu=false;
            }
        }
    }
    
    public Battle getBattle(){
        return b;
    }
    
    public int getPositiveInteger(String message, int lowerLimit){
        int num = 0;
        boolean valid = false;
        while(!valid){
            String x = JOptionPane.showInputDialog(message);
            try{
                num = Integer.parseInt(x);
                if(num >= lowerLimit)valid = true;
                else{
                    valid = false;
                    continue;
                }
            }catch(NumberFormatException exc){
                valid = false;
                continue;
            }
        }
        return num;
    }
    
    //This class deals with keypresses
    class KeyControl implements KeyListener{
        BattleScreen parentScreen;
        
        public KeyControl(BattleScreen b){
            super();
            parentScreen=b;
        }
        
        public void keyTyped(KeyEvent e) {}
        
        public void keyPressed(KeyEvent e) {
            int keypress = e.getKeyCode();
            
            if(!daystart && !intel && !victory && !endStats && !replay){
                if(keypress == Options.up){
                    if(info){
                        skip--;
                        if(skip<0)
                            skip = 0;
                    }else if(menu || cmenu || bmenu){
                        currentMenu.goUp();
                    }
                    else if(m.onMap(cx,cy-1))
                    {
                    	//[NEW]
                    	if(fire)
                    	{
                    		currContextTarg++;
                    		
                    		if(currContextTarg >= contextTargs.size())
                    			currContextTarg = 0;
                    		
                    		cx = contextTargs.get(currContextTarg).getCol();
                    		cy = contextTargs.get(currContextTarg).getRow();
                    	}
                    	else
                    	{
	                        cy--;
	                        if(cy < sy/16+2 && sy > 0)sy -= 16;
	                        if(move){
	                            if(selected.getMoveRange().checkMove(cx,cy)){
	                                if(outOfMoveRange){
	                                    selected.getPath().reCalculatePath(cx,cy,selected);
	                                    outOfMoveRange = false;
	                                }else{
	                                    selected.goDirection(0);
	                                    selected.getPath().truncatePath(cx,cy);
	                                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cx,cy,selected);
	                                }
	                            }else{
	                                if(cx == selected.getLocation().getCol() && cy == selected.getLocation().getRow()){
	                                    selected.getPath().resetPath();
	                                }else{
	                                    outOfMoveRange = true;
	                                }
	                            }
	                        }
                    	}
                    }
                }else if(keypress == Options.down){
                    if(info){
                        skip ++;
                        if(skip>skipMax)
                            skip = skipMax;
                    }else if(menu || cmenu || bmenu){
                        currentMenu.goDown();
                    }
                    else if(m.onMap(cx,cy+1))
                    {
                    	//[NEW]
                    	if(fire)
                    	{
                    		currContextTarg--;
                    		
                    		if(currContextTarg < 0)
                    			currContextTarg = contextTargs.size() - 1;
                    		
                    		cx = contextTargs.get(currContextTarg).getCol();
                    		cy = contextTargs.get(currContextTarg).getRow();
                    	}
                    	else
                    	{
	                        cy++;
	                        if(cy >= sy/16+MAX_TILEH-2 && cy < m.getMaxRow()-2)sy += 16;
	                        if(move){
	                            if(selected.getMoveRange().checkMove(cx,cy)){
	                                if(outOfMoveRange){
	                                    selected.getPath().reCalculatePath(cx,cy,selected);
	                                    outOfMoveRange = false;
	                                }else{
	                                    selected.goDirection(2);
	                                    selected.getPath().truncatePath(cx,cy);
	                                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cx,cy,selected);
	                                }
	                            }else{
	                                if(cx == selected.getLocation().getCol() && cy == selected.getLocation().getRow()){
	                                    selected.getPath().resetPath();
	                                }else{
	                                    outOfMoveRange = true;
	                                }
	                            }
	                        }
                    	}
                    }
                }else if(keypress == Options.left){
                    if(info){
                        if(alt) //If we are on an alternate CO, move to the main CO
                            alt = false;
                        else { //If we are on a main CO
                            infono--;
                            if(infono < 0)
                                infono = b.getNumArmies() - 1 ;
                            
                            if(b.getArmy(infono).getAltCO() != null) { //If the alt CO exists
                                alt = true;
                            }
                            skip = 0;
                        }
                        
                    }else if(menu || cmenu || bmenu){
                        
                    }
                    else if(m.onMap(cx-1,cy))
                    {

                    	//[NEW]
                    	if(fire)
                    	{
                    		currContextTarg--;
                    		
                    		if(currContextTarg < 0)
                    			currContextTarg = contextTargs.size() - 1;
                    		
                    		cx = contextTargs.get(currContextTarg).getCol();
                    		cy = contextTargs.get(currContextTarg).getRow();
                    	}
                    	else
                    	{
	                        cx--;
	                        if(cx < sx/16+2 && sx > 0)sx -= 16;
	                        if(move){
	                            if(selected.getMoveRange().checkMove(cx,cy)){
	                                if(outOfMoveRange){
	                                    selected.getPath().reCalculatePath(cx,cy,selected);
	                                    outOfMoveRange = false;
	                                }else{
	                                    selected.goDirection(3);
	                                    selected.getPath().truncatePath(cx,cy);
	                                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cx,cy,selected);
	                                }
	                            }else{
	                                if(cx == selected.getLocation().getCol() && cy == selected.getLocation().getRow()){
	                                    selected.getPath().resetPath();
	                                }else{
	                                    outOfMoveRange = true;
	                                }
	                            }
	                        }
                    	}
                    }
                }else if(keypress == Options.right){
                    if(info){
                        if(alt) //If we are on an alternate CO, move to the main CO
                        {
                            alt = false;
                            infono++;
                        } else { //If we are on a main CO
                            if(b.getArmy(infono).getAltCO() != null) { //If the alt CO exists
                                alt = true;
                            } else //if there is no alt CO for the previous CO
                                infono++;
                            skip = 0;
                        }
                        if(infono > b.getNumArmies() -1 )
                            infono = 0;
                    }else if(menu || cmenu || bmenu){
                        
                    }
                    else if(m.onMap(cx+1,cy))
                    {
                    	//[NEW]
                    	if(fire)
                    	{
                    		currContextTarg++;
                    		
                    		if(currContextTarg >= contextTargs.size())
                    			currContextTarg = 0;
                    		
                    		cx = contextTargs.get(currContextTarg).getCol();
                    		cy = contextTargs.get(currContextTarg).getRow();
                    	}
                    	else
                    	{
	                        cx++;
	                        if(cx >= sx/16+MAX_TILEW-2 && cx < m.getMaxCol()-2)sx += 16;
	                        if(move){
	                            if(selected.getMoveRange().checkMove(cx,cy)){
	                                if(outOfMoveRange){
	                                    selected.getPath().reCalculatePath(cx,cy,selected);
	                                    outOfMoveRange = false;
	                                }else{
	                                    selected.goDirection(1);
	                                    selected.getPath().truncatePath(cx,cy);
	                                    if(!selected.getPath().isLegal(selected))selected.getPath().reCalculatePath(cx,cy,selected);
	                                }
	                            }else{
	                                if(cx == selected.getLocation().getCol() && cy == selected.getLocation().getRow()){
	                                    selected.getPath().resetPath();
	                                }else{
	                                    outOfMoveRange = true;
	                                }
	                            }
	                        }
                    	}
                    }
                }else if(keypress == Options.akey){
                    pressedA();
                }else if(keypress == Options.bkey){
                    pressedB();
                }else if(keypress == Options.menu){
                    if(selected == null){
                        if(!bmenu && !cmenu){
                            if(!menu){
                                menu=true;
                                //currentMenu = new ContextMenu(true,false,false,true,true,false,false,false);
                                //currentMenu = new ContextMenu(false,false,true,false,false,false,true,true);
                                //currentMenu = ContextMenu.generateContext(selected);
                                currentMenu = BattleMenu.generateContext(b,parentScreen);
                            }else menu=false;
                        }
                    }
                }else if(keypress == Options.nextunit){
                    if(selected == null){
                        if(!bmenu && !cmenu && !menu){
                            Unit[] unitList = b.getArmy(b.getTurn()).getUnits();
                            if(unitList != null){
                                if(unitCyclePosition>=unitList.length)unitCyclePosition=0;
                                boolean found = false;
                                for(int i=unitCyclePosition; i<unitList.length; i++){
                                    if(unitList[i].isActive() && !unitList[i].isInTransport()){
                                        moveCursorTo(unitList[i].getLocation());
                                        unitCyclePosition=i+1;
                                        found = true;
                                        break;
                                    }
                                }
                                if(!found){
                                    for(int i=0; i<unitList.length; i++){
                                        if(unitList[i].isActive() && !unitList[i].isInTransport()){
                                            moveCursorTo(unitList[i].getLocation());
                                            unitCyclePosition=i+1;
                                            break;
                                        }
                                    }
                                }
                            }
                        }
                    }
                }else if(keypress == Options.minimap){
                    if(!minimap){
                        minimap = true;
                    }else minimap=false;
                }
            }else if(daystart){
                if(keypress == Options.akey){
                    pressedA();
                }
            }else if(intel){
                if(keypress == Options.bkey){
                    pressedB();
                }
            }else if(victory){
                if(keypress == Options.akey){
                    pressedA();
                }
            }else if(endStats){
                if(keypress == Options.akey){
                    pressedA();
                }else if(keypress == Options.bkey){
                    pressedB();
                }else if(keypress == Options.left){
                    statArmyIndex--;
                    if(statArmyIndex < 0)statArmyIndex = 0;
                }else if(keypress == Options.right){
                    statArmyIndex++;
                    if(b.getStatArmy(statArmyIndex) == null)statArmyIndex--;
                }else if(keypress == Options.up){
                    statType--;
                    if(statType < 0)statType = 0;
                }else if(keypress == Options.down){
                    statType++;
                    if(statType > 2)statType = 2;
                }
            }else if(replay){
                if(keypress == Options.akey){
                    pressedA();
                }else if(keypress == Options.bkey){
                    pressedB();
                }else if(keypress == Options.left){
                    if(m.onMap(cx-1,cy)){
                        cx--;
                        if(cx < sx/16+2 && sx > 0)sx -= 16;
                    }
                }else if(keypress == Options.right){
                    if(m.onMap(cx+1,cy)){
                        cx++;
                        if(cx >= sx/16+MAX_TILEW-2 && cx < m.getMaxCol()-2)sx += 16;
                    }
                }else if(keypress == Options.up){
                    if(m.onMap(cx,cy-1)){
                        cy--;
                        if(cy < sy/16+2 && sy > 0)sy -= 16;
                    }
                }else if(keypress == Options.down){
                    if(m.onMap(cx,cy+1)){
                        cy++;
                        if(cy >= sy/16+MAX_TILEH-2 && cy < m.getMaxRow()-2)sy += 16;
                    }
                }else if(keypress == Options.fogkey){
                    if(b.isFog()){
                        b.setFog(false);
                    }else{
                        b.setFog(true);
                    }
                    b.calculateFoW();
                }else if(keypress == Options.intelkey){
                    intel = true;
                }
            }
        }
        
        public void keyReleased(KeyEvent e) {}
        
        
    }
    
    //Used to automatically resize as the window changes
    public void componentHidden(ComponentEvent e){}
    public void componentMoved(ComponentEvent e){}
    public void componentResized(ComponentEvent e) // Called whenever the container frame is resized
    {
        Dimension PanelSize = getSize();
        //Takes the window size, divides by tile size, rounds up
        MAX_TILEW = (int)(Math.ceil(PanelSize.getWidth() /(16 * scale)));
        MAX_TILEH = (int)(Math.ceil(PanelSize.getHeight() / (16 * scale)));
        if(MAX_TILEW > m.getMaxCol()) //Frame cant be wider than map
        {
            MAX_TILEW = m.getMaxCol();
        }
        if(MAX_TILEH > m.getMaxRow()) //Frame cant be shorter than map
        {
            MAX_TILEH = m.getMaxRow();
        }
        if(MAX_TILEW < 30) //Maintain minimum width
        {
            MAX_TILEW = 30;
        }
        if(MAX_TILEH < 20) //Maintain minimum height
        {
            MAX_TILEH = 20;
        }
        //Rounds up the screen size itself to whole tiles
        int sizex = MAX_TILEW * (16 * scale);
        int sizey = MAX_TILEH * (16 * scale);
        if(sx/16+MAX_TILEW > m.getMaxCol() || sy/16+MAX_TILEH > m.getMaxRow()){
            sx = 0;
            sy = 0;
            cx = 0;
            cy = 0;
            //center small maps
            if(m.getMaxCol() < 30){
                sx = -((30 - m.getMaxCol())/2)*16;
            }
            if(m.getMaxRow() < 20){
                sy = -((20 - m.getMaxRow())/2)*16;
            }
        }
        setPreferredSize(new Dimension(sizex, sizey));
        parentFrame.pack();
    }
    public void componentShown(ComponentEvent e){}
    
    class MouseControl implements MouseInputListener{
        public void mouseClicked(MouseEvent e){
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            System.out.println(x + "," + y + ":" + e.getButton());
            
            if(e.getButton() == e.BUTTON1){
                if(menu || cmenu){
                    int mitem = currentMenu.getMenuItemAt(x,y,scale);
                    if(mitem != -1){
                        currentMenu.setMenuItem(mitem);
                        pressedA();
                    }
                }else if(bmenu){
                    int mitem = currentMenu.getMenuItemAt(x,y,scale);
                    if(mitem != -1){
                        System.out.print(mitem);
                        if(mitem == -2)currentMenu.goUp();
                        else if(mitem == -3)currentMenu.goDown();
                        else{
                            currentMenu.setMenuItem(mitem);
                            pressedA();
                        }
                    }
                }else{
                    if(noScroll == 0){
                        if(x < 32*scale){
                            if(m.getMaxCol() > DEF_TILEW){
                                sx -= 16;
                                if(sx < 0)sx=0;
                            }
                        }else if(x > MAX_TILEW*16*scale-32*scale){
                            if(m.getMaxCol() > DEF_TILEW){
                                sx += 16;
                                if(sx > (m.getMaxCol()-MAX_TILEW)*16)sx-=16;
                            }
                        }
                        
                        if(y < 32*scale){
                            if(m.getMaxRow() > DEF_TILEH){
                                sy -= 16;
                                if(sy < 0)sy=0;
                            }
                        }else if(y > MAX_TILEH*16*scale-32*scale){
                            if(m.getMaxRow() > DEF_TILEH){
                                sy += 16;
                                if(sy > (m.getMaxRow()-MAX_TILEH)*16)sy-=16;
                            }
                        }
                        noScroll = 3;
                    }else noScroll--;
                    
                    cx = sx/16 + x/(16*scale);
                    if(cx < 0)cx=0;
                    else if(cx >= m.getMaxCol())cx=m.getMaxCol()-1;
                    cy = sy/16 + y/(16*scale);
                    if(cy < 0)cy=0;
                    else if(cy >= m.getMaxRow())cy=m.getMaxRow()-1;
                    
                    if(move)selected.getPath().reCalculatePath(cx,cy,selected);
                    
                    pressedA();
                }
            }else{
                //any other button
                pressedB();
            }
        }
        
        public void mouseEntered(MouseEvent e) {}
        
        public void mouseExited(MouseEvent e) {}
        
        public void mousePressed(MouseEvent e) {}
        
        public void mouseReleased(MouseEvent e) {}
        
        public void mouseDragged(MouseEvent e) {}
        
        public void mouseMoved(MouseEvent e){
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            
            if(!menu && !cmenu && !bmenu){
                if(noScroll == 0){
                    if(x < 32*scale){
                        if(m.getMaxCol() > DEF_TILEW){
                            sx -= 16;
                            if(sx < 0)sx=0;
                        }
                    }else if(x > MAX_TILEW*16*scale-32*scale){
                        if(m.getMaxCol() > DEF_TILEW){
                            sx += 16;
                            if(sx > (m.getMaxCol()-MAX_TILEW)*16)sx-=16;
                        }
                    }
                    
                    if(y < 32*scale){
                        if(m.getMaxRow() > DEF_TILEH){
                            sy -= 16;
                            if(sy < 0)sy=0;
                        }
                    }else if(y > MAX_TILEH*16*scale-32*scale){
                        if(m.getMaxRow() > DEF_TILEH){
                            sy += 16;
                            if(sy > (m.getMaxRow()-MAX_TILEH)*16)sy-=16;
                        }
                    }
                    noScroll = 3;
                }else noScroll--;
                
                cx = sx/16 + x/(16*scale);
                if(cx < 0)cx=0;
                else if(cx >= m.getMaxCol())cx=m.getMaxCol()-1;
                cy = sy/16 + y/(16*scale);
                if(cy < 0)cy=0;
                else if(cy >= m.getMaxRow())cy=m.getMaxRow()-1;
                
                if(move)selected.getPath().reCalculatePath(cx,cy,selected);
            }
        }
    }
    
    public void setDayStart(boolean b){
        daystart = b;
    }
    
    public int getSX() {
        return sx;
    }
    public int getSY() {
        return sy;
    }
    
    //[NEW]
    public boolean visibleAtXY(int x, int y)
    {                   	  
    	//If there is no unit at the given location, then nothing should be displayed
    	if(m.find(new Location(x,y)).getUnit() != null)
    	{
    		Unit thisUnit = m.find(new Location(x,y)).getUnit();
    		
    		//Do not display the unit if it is moving
    		if(!thisUnit.moving)
    		{
	    		//If the current analyzed unit is allied to the current player, it should be
	    		//visible no matter what the conditions are like
	    		if(thisUnit.getArmy().getSide() == b.getArmy(b.getTurn()).getSide())
	    		{
	    			return true;
	    		}
	    		//Otherwise, more conditions need to be checked
	    		else
	    		{
	    			//Check FoW based conditions
    				//Check if the current unit is 'hidden'
    				//If it is not hidden, it should be displayed
	    			if(b.isFog() && !thisUnit.isHidden())
	    			{
	    					return true;
	    			}
	    			//Check for MoW based conditions
    				//Check if the current unit is 'dived' or if the unit has been 
	    			//detected. If it is not dived, or it has been detected, it should 
	    			//be displayed
	    			else if(b.isMist() && (!thisUnit.isDived() || thisUnit.detected))
	    			{
	    					return true;
	    			}
	    			//Check for clear conditions
    				//Check if the current unit is 'hidden'
    				//If it is not hidden, it should be displayed
	    			else if(!thisUnit.isHidden())
	    			{
	    				return true;
	    			}
	    		}
    		}
    	}
    	
    	return false;
    }
    
    /* Dunn't work
    public void addScreenGlide(Location end, int delay, int duration) {
        Timer timer = new Timer(16, null);//30 frames per second
        EndListener endPerformer = new EndListener(end, duration, timer);
        timer.addActionListener(endPerformer); //End performer kills the timer thread after a set amount of time
        timer.setInitialDelay(delay*16+16);
        timer.setRepeats(true);
        timer.start();
    }
     
     
    private class EndListener implements ActionListener{
        int duration;
        int ticker;
        Location loc;
        Timer timer;
        public EndListener(Location end, int duration, Timer t) {
            timer = t;
            ticker = 0;
            this.duration = duration;
            loc = end;
        }
        public void actionPerformed(ActionEvent evt) {
            ticker++;
            if(ticker > duration)
            {
                timer.setRepeats(false);
                System.out.println("End");
            }
            else{
            sx+=(loc.getCol() - (sx+MAX_TILEW)/2)/duration; //increments distance equally over the duration
            sy+=(loc.getRow() - (sy+MAX_TILEH)/2)/duration;
            System.out.println("Gliding");
            }
        }
    }
     
     */
}