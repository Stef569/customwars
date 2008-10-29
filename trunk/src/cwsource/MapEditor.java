package cwsource;
/*
 *MapEditor.java
 *Author: Urusan
 *Contributors:
 *Creation: July 19, 2006, 2:38 PM
 *Used to make .map files for use with CW
 */
import java.awt.*;
import javax.swing.*;
import java.io.*;
import javax.imageio.*;
import java.awt.image.*;
import java.awt.event.*;
import java.awt.color.*;
import javax.swing.event.MouseInputListener;
//import javax.swing.filechooser.FileNameExtensionFilter;
import java.util.Random;

public class MapEditor extends JComponent implements ComponentListener{
    private int DEF_TILEW = 30;   //BattleScreen width was 16 tiles
    private int DEF_TILEH = 20;   //BattleScreen height was 12 tiles
    private int MAX_TILEW = DEF_TILEW;   //Screen width in tiles
    private int MAX_TILEH = DEF_TILEH;   //Screen height in tiles
    private static final int MAX_ARMIES = 10;   //The max allowed armies is 10
    
    private Map m;              //holds the map being displayed
    private Unit selected;      //holds the currently selected unit
    private int cx;             //holds the cursor's x position on the map (in map tiles)
    private int cy;             //holds the cursor's y position on the map (in map tiles)
    private int sx;             //holds the battle screen's x position over the map (in pixels)
    private int sy;             //holds the battle screen's y position over the map (in pixels)
    private int item;           //holds the menu's current item (both menus use this)
    private boolean menu;       //is the main menu in use?
    private boolean tmenu;      //is the terrain menu in use?
    private boolean umenu;      //is the unit menu in use?
    private boolean smenu;      //is the side menu in use?
    private boolean terrain;    //is the editor in terrain mode?
    private boolean unit;       //is the editor in unit mode?
    private boolean constantMode;   //is "constant mode" on?
    private boolean minimap;    //is the minimap on?
    private Battle b;           //holds the battle (Army List and DTD Mechanics)
    private InGameMenu currentMenu = null; //contains the active menu (null if no menus are active)
    private BufferedImage bimg; //the screen, used for double buffering and scaling
    private int scale;          //what scale multiplier is being used
    private JFrame parentFrame;  //the frame that contains the window
    private Army selectedArmy;  //the selected army
    private Terrain selectedTerrain;    //the kind of terrain to be used by the editor
    private int unitType;       //The current unit type
    private String mapFilename; //The filename of the map
    private MapKeyControl keycontroller;   //the MapKeyControl, used to remove the component
    private MapMouseControl mousecontroller;   //the MouseControl, used to remove the component
    private int noScroll = 0;                  //don't scroll for this many more mouse commands
    
    /** Creates a new instance of BattleScreen */
    public MapEditor(Battle b, JFrame f){
        //makes the panel opaque, and thus visible
        this.setOpaque(true);
        
        this.b = b;
        this.m = b.getMap();
        selectedArmy = null;
        selectedTerrain = new Plain();
        selected = null;
        cx = 0;
        cy = 0;
        sx = 0;
        sy = 0;
        item = 0;
        unitType = 0;
        menu = false;
        tmenu = false;
        umenu = false;
        smenu = false;
        terrain = true;
        unit = false;
        constantMode = false;
        minimap = false;
        mapFilename = "temp.map";
        
        scale = 1;
        
        //KeyControl is registered with the parent frame
        keycontroller = new MapKeyControl(this);
        f.addKeyListener(keycontroller);
        //MouseControl is registered with the parent frame
        mousecontroller = new MapMouseControl();
        f.addMouseListener(mousecontroller);
        f.addMouseMotionListener(mousecontroller);
        
        parentFrame = f;
        this.addComponentListener(this);
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
        
        drawMap(g);
        drawCursor(g);
        
        if(menu||tmenu||umenu||smenu)currentMenu.drawMenu(g);
        
        if(minimap)drawMiniMap(g, 0, 0);
        
        //causes problems with animated gifs
        //this.repaint();
    }
    
    //Draws the map and the units
    public void drawMap(Graphics2D g){
        Image temp;
        
        //only print onscreen tiles
        for(int x=sx/16;x<m.getMaxCol();x++){
            if(x>=sx/16+MAX_TILEW)break;
            for(int y=sy/16;y<m.getMaxRow();y++){
                if(y>=sy/16+MAX_TILEH)break;
                //draw terrain
                if(x < 0 || y < 0)continue;
                
                Terrain ter = m.find(new Location(x,y)).getTerrain();
                if(ter instanceof Property ) {
                    if(((Property)ter).owner == null)
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
                } else{
                    temp = TerrainGraphics.getTerrainSpriteSheet();
                    if(ter.index<3) { //Plain, Wood, Mountain
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getIndex())*16, 0, (ter.getIndex()+1)*16, 32, this);
                    } else if(ter.index == 3){ //Road
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+3)*16, 0, (ter.getStyle()+4)*16, 32, this);
                    } else if(ter.index == 4){ //Bridge
                        g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+14)*16, 0, (ter.getStyle()+15)*16, 32, this);
                    }else if(ter.index == 20){
                    //Put suspension code here
                    g.drawImage(temp, x*16-sx, y*16-sy-16, (x+1)*16-sx, y*16-sy+16, (ter.getStyle()+16)*16, 0, (ter.getStyle()+17)*16, 32, this);
                    }else if(ter.index == 5){ //River
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
                if(m.find(new Location(x,y)).getUnit()!=null){
                    //g.drawImage(UnitGraphics.getUnitImage(m.find(new Location(x,y)).getUnit()),x*16-sx,y*16-sy,this);
                    g.drawImage(UnitGraphics.getUnitImage(m.find(new Location(x,y)).getUnit()),x*16-sx,y*16-sy,x*16-sx+16,y*16-sy+16,0,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit()),16,UnitGraphics.findYPosition(m.find(new Location(x,y)).getUnit())+16,this);
                    //draw HP if health is not perfect
                    if(m.find(new Location(x,y)).getUnit().getDisplayHP()!=10){
                        g.setColor(Color.black);
                        g.fillRect(x*16+8-sx,y*16+8-sy,8,8);
                        g.setColor(Color.white);
                        g.setFont(new Font("SansSerif", Font.PLAIN,10));
                        g.drawString("" + m.find(new Location(x,y)).getUnit().getDisplayHP(),x*16+10-sx,y*16+16-sy);
                    }
                }
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
                    int armycolor = map.find(new Location(i,j)).getUnit().getArmy().getColor();
                    g.drawImage(minimap,x+(i*4),y+(j*4),x+(i*4)+4,y+(j*4)+4,132+(armycolor*4),0,136+(armycolor*4),4,this);
                }
            }
        }
    }
    
    //draws the cursor
    public void drawCursor(Graphics2D g){
        //g.setColor(Color.red);
        //g.drawRect(cx*16-sx,cy*16-sy,16,16);
        g.drawImage(MiscGraphics.getCursor(),cx*16-sx-7,cy*16-sy-7,this);
    }
    
    //Fills the map with the selected terrain
    public void fillMap(){
        if(selectedTerrain.getIndex()!=8 && selectedTerrain.getIndex()!=9 && selectedTerrain.getIndex()!=18){
            Unit temp;
            for(int i=0; i < m.getMaxCol(); i++){
                for(int j=0; j < m.getMaxRow(); j++){
                    m.find(new Location(i,j)).setTerrain(selectedTerrain);
                    selectTerrain(selectedTerrain.getIndex());
                    temp = m.find(new Location(i,j)).getUnit();
                    if(temp != null){
                        m.remove(temp);
                        temp.getArmy().removeUnit(temp);
                    }
                }
            }
            m.initStyle();
        }
    }
    
    public void randomMap(){
        //set constants
        int PRI = 0;
        
        //clear map
        selectTerrain(PRI);
        fillMap();
        
        Random r = new Random();
        
        
        for(int i=0; i < 1000; i++){
            int num = r.nextInt(100);
            if(num < 30)num = 0;
            else if(num < 60)num = 1;
            else if(num < 70)num = 2;
            else if(num < 90)num = 6;
            else num = 10;
            
            int lim = 100;
            if(num==3 || num==10){
                //lim = r.nextInt(2)+1;
                lim = 1;
            }else if(num==0){
                lim = r.nextInt(40)+1;
            }else if(num==1){
                //lim = r.nextInt(3)+1;
                lim = 1;
            }else if(num==2){
                //lim = r.nextInt(3)+1;
                lim = 1;
            }else{
                lim = r.nextInt(70)+1;
            }
            
            if(num==6)createSeededArea(num, lim, true);
            else createSeededArea(num, lim, true);
        }
        
        //remember to apply the correct visual style
        m.initStyle();
    }
    
    public void createSeededArea(int SEC, int LIM, boolean cont){
        //seed ocean
        Random r = new Random();
        int numfill = 0;
        int r1 = r.nextInt(m.getMaxCol());
        int r2 = r.nextInt(m.getMaxRow());
        selectTerrain(SEC);
        m.find(new Location(r1,r2)).setTerrain(selectedTerrain);
        selectTerrain(SEC);
        numfill++;
        
        //fill out ocean
        boolean grid[][] = new boolean[m.getMaxCol()][m.getMaxRow()];
        for(int i=0; i<m.getMaxCol(); i++){
            for(int j=0; j<m.getMaxRow(); j++){
                if(m.find(new Location(i,j)).getTerrain().getIndex()==SEC){
                    grid[i][j]=true;
                }
            }
        }
        Tile node = m.find(new Location(r1,r2));
        int x = r1;
        int y = r2;
        while(node != null){
            if(m.onMap(x+1,y)){
                if(r.nextInt(2)==1){
                    m.find(new Location(x+1,y)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            if(m.onMap(x-1,y)){
                if(r.nextInt(2)==1){
                    m.find(new Location(x-1,y)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            if(m.onMap(x,y+1)){
                if(r.nextInt(2)==1){
                    m.find(new Location(x,y+1)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            if(m.onMap(x,y-1)){
                if(r.nextInt(2)==1){
                    m.find(new Location(x,y-1)).setTerrain(selectedTerrain);
                    selectTerrain(SEC);
                    numfill++;
                    if(numfill > LIM)break;
                }
            }
            grid[x][y] = true;
            if(numfill > LIM)break;
            
            boolean found = false;
            for(int i=0; i < m.getMaxCol(); i++){
                for(int j=0; j < m.getMaxRow(); j++){
                    if(!grid[i][j]){
                        if(m.find(new Location(i,j)).getTerrain().getIndex()==SEC){
                            node = m.find(new Location(i,j));
                            x = i;
                            y = j;
                            found = true;
                            break;
                        }
                    }
                }
                if(found)break;
            }
            
            if(!found)node = null;
        }
        
        //remove totally surrounded single tiles
        if(cont){
            for(int i=0; i < m.getMaxCol(); i++){
                for(int j=0; j < m.getMaxRow(); j++){
                    if(m.find(new Location(i,j)).getTerrain().getIndex()!=SEC){
                        if(!m.onMap(i+1,j) || m.find(new Location(i+1,j)).getTerrain().getIndex() == SEC){
                            if(!m.onMap(i-1,j) || m.find(new Location(i-1,j)).getTerrain().getIndex() == SEC){
                                if(!m.onMap(i,j+1) || m.find(new Location(i,j+1)).getTerrain().getIndex() == SEC){
                                    if(!m.onMap(i,j-1) || m.find(new Location(i,j-1)).getTerrain().getIndex() == SEC){
                                        m.find(new Location(i,j)).setTerrain(selectedTerrain);
                                        selectTerrain(SEC);
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
    
    public void mirrorHorizontal(){
        //create double width map
        int w = m.getMaxCol()*2;
        int h = m.getMaxRow();
        int oldw = m.getMaxCol();
        int oldh = m.getMaxRow();
        
        Map temp = new Map(w,h);
        temp.copyMap(m);
        
        m = temp;
        b = new Battle(m);
        
        //finalize copy
        selectedArmy = null;
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
        unitType = 0;
        menu = false;
        tmenu = false;
        umenu = false;
        smenu = false;
        terrain = true;
        unit = false;
        constantMode = false;
        minimap = false;
        
        //mirror
        for(int j = 0; j < oldh; j++){
            for(int i = 0; i < oldw; i++){
                selectTerrain(m.find(new Location(i,j)).getTerrain().getIndex());
                m.find(new Location(w-i-1,j)).setTerrain(selectedTerrain);
            }
        }
        
        selectedTerrain = new Plain();
        m.initStyle();
    }
    
    public void mirrorVertical(){
        //create double height map
        int w = m.getMaxCol();
        int h = m.getMaxRow()*2;
        int oldw = m.getMaxCol();
        int oldh = m.getMaxRow();
        
        Map temp = new Map(w,h);
        temp.copyMap(m);
        
        m = temp;
        b = new Battle(m);
        
        //finalize copy
        selectedArmy = null;
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
        unitType = 0;
        menu = false;
        tmenu = false;
        umenu = false;
        smenu = false;
        terrain = true;
        unit = false;
        constantMode = false;
        minimap = false;
        
        //mirror
        //mirror
        for(int j = 0; j < oldh; j++){
            for(int i = 0; i < oldw; i++){
                selectTerrain(m.find(new Location(i,j)).getTerrain().getIndex());
                m.find(new Location(i,h-j-1)).setTerrain(selectedTerrain);
            }
        }
        
        selectedTerrain = new Plain();
        m.initStyle();
    }
    
    //removes the component from the frame
    public void removeFromFrame(){
        parentFrame.getContentPane().remove(this);
        parentFrame.removeKeyListener(keycontroller);
        parentFrame.removeMouseListener(mousecontroller);
        parentFrame.removeMouseMotionListener(mousecontroller);
    }
    
    private void endBattle(){
        //end the mission
        Mission.endMission();
        
        //put a Main Menu inside the frame
        parentFrame.setSize(400,400);
        removeFromFrame();
        MainMenu mm = new MainMenu(parentFrame);
        parentFrame.getContentPane().add(mm);
        parentFrame.validate();
        parentFrame.pack();
    }
    
    //helper routine: places a terrain when enter is hit (or in constant mode)
    private void placeTile(){
        //erase any previous HQs of the same type
        if(selectedTerrain.getIndex() == 9){
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    if(m.find(new Location(i,j)).getTerrain().getIndex()==9){
                        if(((Property)m.find(new Location(i,j)).getTerrain()).getColor() == ((Property)selectedTerrain).getColor()){
                            m.find(new Location(i,j)).setTerrain(new Plain());
                        }
                    }
                }
            }
        }
        m.find(new Location(cx,cy)).setTerrain(selectedTerrain);
        selectTerrain(selectedTerrain.getIndex());
        m.initStyle();
    }
    
    //Select a Terrain
    private void selectTerrain(int type){
        switch(type){
            case 0:
                selectedTerrain = new Plain();
                break;
            case 1:
                selectedTerrain = new Wood();
                break;
            case 2:
                selectedTerrain = new Mountain();
                break;
            case 3:
                selectedTerrain = new Road();
                break;
            case 4:
                selectedTerrain = new Bridge();
                break;
            case 5:
                selectedTerrain = new River();
                break;
            case 6:
                selectedTerrain = new Sea();
                break;
            case 7:
                selectedTerrain = new Reef();
                break;
            case 8:
                selectedTerrain = new Shoal();
                break;
            case 9:
                if(selectedArmy != null)
                    selectedTerrain = new HQ(selectedArmy);
                break;
            case 10:
                if(selectedArmy == null)
                    selectedTerrain = new City();
                else
                    selectedTerrain = new City(selectedArmy);
                break;
            case 11:
                if(selectedArmy == null)
                    selectedTerrain = new Base();
                else
                    selectedTerrain = new Base(selectedArmy);
                break;
            case 12:
                if(selectedArmy == null)
                    selectedTerrain = new Airport();
                else
                    selectedTerrain = new Airport(selectedArmy);
                break;
            case 13:
                if(selectedArmy == null)
                    selectedTerrain = new Port();
                else
                    selectedTerrain = new Port(selectedArmy);
                break;
            case 14:
                if(selectedArmy == null)
                    selectedTerrain = new ComTower();
                else
                    selectedTerrain = new ComTower(selectedArmy);
                break;
            case 15:
                selectedTerrain = new Pipe();
                break;
            case 16:
                selectedTerrain = new Silo();
                break;
            case 17:
                if(selectedArmy == null)
                    selectedTerrain = new Pipestation();
                else
                    selectedTerrain = new Pipestation(selectedArmy);
                break;
            case 18:
                selectedTerrain = new PipeSeam();
                break;
            case 19:
                selectedTerrain = new DestroyedPipeSeam();
                break;
            case 20:
                selectedTerrain = new SuspensionBridge();
                break;
        }
    }
    
    //Places a new unit
    private void placeUnit(Map m, Tile t, int type){
        int x = t.getLocation().getCol();
        int y = t.getLocation().getRow();
        Army a = selectedArmy;
        
        if(a != null){
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
    }
    
    //writes new .map files
    public void saveMap(){
        Terrain ter;    //temporarily holds the terrain being worked on
        Unit uni;       //temporarily holds the unit being worked on
        
        int colors[] = new int[MAX_ARMIES];
        int cindex = 0;
        int numArmies = 0;
        boolean unique = true;
        
        //find the armies on the map
        for(int i=0; i<m.getMaxCol(); i++){
            for(int j=0; j<m.getMaxRow(); j++){
                //check for owned properties
                if(m.find(new Location(i,j)).getTerrain() instanceof Property){
                    int color = ((Property)m.find(new Location(i,j)).getTerrain()).getColor();
                    if(m.find(new Location(i,j)).getTerrain().getIndex() == 9)color++;
                    if(color != 0){
                        for(int k=0; k<cindex; k++){
                            if(colors[k]==color){
                                unique = false;
                                break;
                            }
                        }
                        if(unique){
                            colors[cindex] = color;
                            cindex++;
                            numArmies++;
                        }else{
                            unique = true;
                        }
                    }
                }
                
                //check for units
                if(m.find(new Location(i,j)).getUnit()!=null){
                    int color = m.find(new Location(i,j)).getUnit().getArmy().getColor() + 1;
                    for(int k=0; k<cindex; k++){
                        if(colors[k]==color){
                            unique = false;
                            break;
                        }
                    }
                    if(unique){
                        colors[cindex] = color;
                        cindex++;
                        numArmies++;
                    }else{
                        unique = true;
                    }
                }
            }
        }
        
        //sort the list (lowest numbers always first)
        for(int i=0; i < numArmies-1; i++){
            int min = i;
            for(int j=i+1; j < numArmies; j++){
                if(colors[j] < colors[min]){
                    min = j;
                }
            }
            int temp = colors[i];
            colors[i] = colors[min];
            colors[min] = temp;
        }
        
        for(int i = 0; i < colors.length; i++)colors[i]--;
        
        try{
            DataOutputStream write = new DataOutputStream(new FileOutputStream(mapFilename));
            
            //HEADER
            //version number
            write.writeInt(-1);
            //name
            write.writeBytes(m.getMapName());
            write.writeByte(0);
            //author
            write.writeBytes(m.getMapAuthor());
            write.writeByte(0);
            //description
            write.writeBytes(m.getMapDescription());
            write.writeByte(0);
            //Width
            write.writeInt(m.getMaxCol());
            //Height
            write.writeInt(m.getMaxRow());
            //Number of Armies
            write.writeByte(numArmies);
            //write colors
            for(int i=0;i<numArmies;i++)write.writeByte(colors[i]);
            
            //TERRAIN
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    ter = m.find(new Location(i,j)).getTerrain();
                    //Terrain Type
                    write.writeByte(ter.getIndex());
                    
                    //Side
                    if(ter instanceof Property){
                        Property prop = (Property)ter;
                        if(prop.getOwner()==null){
                            //neutral
                            write.writeByte(-1);
                        }else{
                            //The property belongs to the nth army in the list (color given in header)
                            write.writeByte(prop.getOwner().getColor());
                        }
                    }else{
                        //neutral
                        write.writeByte(-1);
                    }
                }
            }
            
            //UNITS
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    uni = m.find(new Location(i,j)).getUnit();
                    if(uni != null){
                        //type
                        write.writeByte(uni.getUnitType());
                        //side
                        write.writeByte(uni.getArmy().getColor());
                        //x position
                        write.writeInt(uni.getLocation().getCol());
                        //y position
                        write.writeInt(uni.getLocation().getRow());
                    }
                }
            }
            
            //Signals end of Unit List (so we can add extra info later if we wish)
            write.writeByte(-10);
            for(int i = 0; i<m.getTriggers().size(); i++){
                write.writeInt(m.getTriggers().get(i).type);
                write.writeInt(m.getTriggers().get(i).day);
                write.writeInt(m.getTriggers().get(i).turn);
                switch(m.getTriggers().get(i).type)
                {
                    case 0:
                        //unit trigger
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).unitType);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).x);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).y);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).army);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitHP);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitFuel);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitAmmo);
                        break;
                    case 1:
                        //damage trigger
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).x);
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).y);
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).damage);
                        write.writeBoolean(((DamageTrigger)m.getTriggers().get(i)).destroy);
                }
            }
            //Signals end of Command List
            write.writeByte(-11);
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    //writes new .map files
    public static void saveMap(Map m, String mapFilename){
        Terrain ter;    //temporarily holds the terrain being worked on
        Unit uni;       //temporarily holds the unit being worked on
        
        int colors[] = new int[MAX_ARMIES];
        int cindex = 0;
        int numArmies = 0;
        boolean unique = true;
        
        //find the armies on the map
        for(int i=0; i<m.getMaxCol(); i++){
            for(int j=0; j<m.getMaxRow(); j++){
                //check for owned properties
                if(m.find(new Location(i,j)).getTerrain() instanceof Property){
                    int color = ((Property)m.find(new Location(i,j)).getTerrain()).getColor();
                    if(m.find(new Location(i,j)).getTerrain().getIndex() == 9)color++;
                    if(color != 0){
                        for(int k=0; k<cindex; k++){
                            if(colors[k]==color){
                                unique = false;
                                break;
                            }
                        }
                        if(unique){
                            colors[cindex] = color;
                            cindex++;
                            numArmies++;
                        }else{
                            unique = true;
                        }
                    }
                }
                
                //check for units
                if(m.find(new Location(i,j)).getUnit()!=null){
                    int color = m.find(new Location(i,j)).getUnit().getArmy().getColor() + 1;
                    for(int k=0; k<cindex; k++){
                        if(colors[k]==color){
                            unique = false;
                            break;
                        }
                    }
                    if(unique){
                        colors[cindex] = color;
                        cindex++;
                        numArmies++;
                    }else{
                        unique = true;
                    }
                }
            }
        }
        
        //sort the list (lowest numbers always first)
        for(int i=0; i < numArmies-1; i++){
            int min = i;
            for(int j=i+1; j < numArmies; j++){
                if(colors[j] < colors[min]){
                    min = j;
                }
            }
            int temp = colors[i];
            colors[i] = colors[min];
            colors[min] = temp;
        }
        
        for(int i = 0; i < colors.length; i++)colors[i]--;
        
        try{
            DataOutputStream write = new DataOutputStream(new FileOutputStream(mapFilename));
            
            //HEADER
            //version number
            write.writeInt(-1);
            //name
            write.writeBytes(m.getMapName());
            write.writeByte(0);
            //author
            write.writeBytes(m.getMapAuthor());
            write.writeByte(0);
            //description
            write.writeBytes(m.getMapDescription());
            write.writeByte(0);
            //Width
            write.writeInt(m.getMaxCol());
            //Height
            write.writeInt(m.getMaxRow());
            //Number of Armies
            write.writeByte(numArmies);
            //write colors
            for(int i=0;i<numArmies;i++)write.writeByte(colors[i]);
            
            //TERRAIN
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    ter = m.find(new Location(i,j)).getTerrain();
                    //Terrain Type
                    write.writeByte(ter.getIndex());
                    
                    //Side
                    if(ter instanceof Property){
                        Property prop = (Property)ter;
                        if(prop.getOwner()==null){
                            //neutral
                            write.writeByte(-1);
                        }else{
                            //The property belongs to the nth army in the list (color given in header)
                            write.writeByte(prop.getOwner().getColor());
                        }
                    }else{
                        //neutral
                        write.writeByte(-1);
                    }
                }
            }
            
            //UNITS
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    uni = m.find(new Location(i,j)).getUnit();
                    if(uni != null){
                        //type
                        write.writeByte(uni.getUnitType());
                        //side
                        write.writeByte(uni.getArmy().getColor());
                        //x position
                        write.writeInt(uni.getLocation().getCol());
                        //y position
                        write.writeInt(uni.getLocation().getRow());
                    }
                }
            }
            
            //Signals end of Unit List (so we can add extra info later if we wish)
            write.writeByte(-10);
            for(int i = 0; i<m.getTriggers().size(); i++){
                write.writeInt(m.getTriggers().get(i).type);
                write.writeInt(m.getTriggers().get(i).day);
                write.writeInt(m.getTriggers().get(i).turn);
                switch(m.getTriggers().get(i).type)
                {
                    case 0:
                        //unit trigger
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).unitType);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).x);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).y);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).army);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitHP);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitFuel);
                        write.writeInt(((UnitTrigger)m.getTriggers().get(i)).UnitAmmo);
                        break;
                    case 1:
                        //damage trigger
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).x);
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).y);
                        write.writeInt(((DamageTrigger)m.getTriggers().get(i)).damage);
                        write.writeBoolean(((DamageTrigger)m.getTriggers().get(i)).destroy);
                }
            }
            //Signals end of Command List
            write.writeByte(-11);
        }catch(IOException e){
            System.err.println(e);
        }
    }
    
    //writes old .map files
    /*public void saveMap(){
        Terrain ter;    //temporarily holds the terrain being worked on
        Unit uni;       //temporarily holds the unit being worked on
     
        int colors[] = new int[MAX_ARMIES];
        int cindex = 0;
        int numArmies = 0;
        boolean unique = true;
     
        //find the armies on the map
        for(int i=0; i<m.getMaxCol(); i++){
            for(int j=0; j<m.getMaxRow(); j++){
                //check for owned properties
                if(m.find(new Location(i,j)).getTerrain() instanceof Property){
                    int color = ((Property)m.find(new Location(i,j)).getTerrain()).getColor();
                    if(m.find(new Location(i,j)).getTerrain().getIndex() == 9)color++;
                    if(color != 0){
                        for(int k=0; k<cindex; k++){
                            if(colors[k]==color){
                                unique = false;
                                break;
                            }
                        }
                        if(unique){
                            colors[cindex] = color;
                            cindex++;
                            numArmies++;
                        }else{
                            unique = true;
                        }
                    }
                }
     
                //check for units
                if(m.find(new Location(i,j)).getUnit()!=null){
                    int color = m.find(new Location(i,j)).getUnit().getArmy().getColor() + 1;
                    for(int k=0; k<cindex; k++){
                        if(colors[k]==color){
                            unique = false;
                            break;
                        }
                    }
                    if(unique){
                        colors[cindex] = color;
                        cindex++;
                        numArmies++;
                    }else{
                        unique = true;
                    }
                }
            }
        }
     
        //sort the list (lowest numbers always first)
        for(int i=0; i < numArmies-1; i++){
            int min = i;
            for(int j=i+1; j < numArmies; j++){
                if(colors[j] < colors[min]){
                    min = j;
                }
            }
            int temp = colors[i];
            colors[i] = colors[min];
            colors[min] = temp;
        }
     
        for(int i = 0; i < colors.length; i++)colors[i]--;
     
        //DEBUG
        /*System.out.println("Number of Armies: " + numArmies);
        for(int i = 0; i < colors.length; i++)System.out.print(colors[i]);
        System.out.println();
     
        try{
            DataOutputStream write = new DataOutputStream(new FileOutputStream(mapFilename));
     
            //HEADER
            //Width
            write.writeInt(m.getMaxCol());
            //Height
            write.writeInt(m.getMaxRow());
            //Number of Armies
            write.writeInt(numArmies);
            //write colors
            for(int i=0;i<numArmies;i++)write.writeInt(colors[i]);
     
            //TERRAIN
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    ter = m.find(new Location(i,j)).getTerrain();
                    //Terrain Type
                    write.writeInt(ter.getIndex());
     
                    //Side
                    if(ter instanceof Property){
                        Property prop = (Property)ter;
                        if(prop.getOwner()==null){
                            //neutral
                            write.writeInt(-1);
                        }else{
                            //The property belongs to the nth army in the list (color given in header)
                            write.writeInt(prop.getOwner().getColor());
                        }
                    }else{
                        //neutral
                        write.writeInt(-1);
                    }
                }
            }
     
            //UNITS
            for(int i=0; i<m.getMaxCol(); i++){
                for(int j=0; j<m.getMaxRow(); j++){
                    uni = m.find(new Location(i,j)).getUnit();
                    if(uni != null){
                        //type
                        write.writeInt(uni.getUnitType());
                        //side
                        write.writeInt(uni.getArmy().getColor());
                        //x position
                        write.writeInt(uni.getLocation().getCol());
                        //y position
                        write.writeInt(uni.getLocation().getRow());
                    }
                }
            }
     
            //Signals end of Unit List (so we can add extra info later if we wish)
            write.writeInt(-10);
        }catch(IOException e){
            System.err.println(e);
        }
    }*/
    
    public void pressedA(){
        if(menu){
            menu=false;
            int menuselection = currentMenu.doMenuItem();
            if(menuselection == 1){
                if(scale < 4)
                    scale++;
                else
                    scale = 1;
                parentFrame.pack();
            }else if(menuselection == 2){
                fillMap();
            }else if(menuselection == 3){
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Save Map");
                if(new File("./maps/User Maps/").exists() && new File("./maps/User Maps/").isDirectory()){
                    fc.setCurrentDirectory(new File("./maps/User Maps/"));
                }else{
                    fc.setCurrentDirectory(new File("./maps/"));
                }
                fc.setApproveButtonText("Save");
                /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "CW and AWD Maps", "map", "awd");
                FileNameExtensionFilter filter2 = new FileNameExtensionFilter(
                        "AWD Maps", "awd");
                FileNameExtensionFilter filter3 = new FileNameExtensionFilter(
                        "CW Maps", "map");
                fc.setFileFilter(filter);
                fc.setFileFilter(filter2);
                fc.setFileFilter(filter3);*/
                int returnVal = fc.showOpenDialog(this);
                if(returnVal != 1){
                    String x = fc.getSelectedFile().getPath();
                    if(x.length()<5 || !x.substring(x.length()-4).equals(".map"))x += ".map";
                    mapFilename = x;
                    System.out.println(mapFilename);
                    saveMap();
                }
            }else if(menuselection == 4){
                //load a map
                JFileChooser fc = new JFileChooser();
                fc.setDialogTitle("Load Map");
                if(new File("./maps/User Maps/").exists() && new File("./maps/User Maps/").isDirectory()){
                    fc.setCurrentDirectory(new File("./maps/User Maps/"));
                }else{
                    fc.setCurrentDirectory(new File("./maps/"));
                }
                fc.setApproveButtonText("Load");
                /*FileNameExtensionFilter filter = new FileNameExtensionFilter(
                        "CW and AWD Maps", "map", "awd");
                FileNameExtensionFilter filter2 = new FileNameExtensionFilter(
                        "AWD Maps", "awd");
                FileNameExtensionFilter filter3 = new FileNameExtensionFilter(
                        "CW Maps", "map");
                fc.setFileFilter(filter3);
                fc.setFileFilter(filter2);
                fc.setFileFilter(filter);*/
                int returnVal = fc.showOpenDialog(this);
                
                if(returnVal != 1){
                    String x = fc.getSelectedFile().getPath();
                    if(mapFilename.length()<5 || (!mapFilename.substring(mapFilename.length()-4).equals(".map") && !mapFilename.substring(mapFilename.length()-4).equals(".awd")))mapFilename += ".map";
                    System.out.println(x);
                    File saveFile = new File(x);
                    if(saveFile.exists()){
                        mapFilename = x;
                        
                        b = new Battle(mapFilename);
                        m = b.getMap();
                        
                        selectedArmy = null;
                        selectedTerrain = new Plain();
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
                        unitType = 0;
                        menu = false;
                        tmenu = false;
                        umenu = false;
                        smenu = false;
                        terrain = true;
                        unit = false;
                        constantMode = false;
                        minimap = false;
                    }
                }
            }else if(menuselection == 5){
                int w = getPositiveInteger("Input the new map's width",1);
                int h = getPositiveInteger("Input the new map's height",1);
                
                m = new Map(w,h);
                b = new Battle(m);
                
                selectedArmy = null;
                selectedTerrain = new Plain();
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
                unitType = 0;
                menu = false;
                tmenu = false;
                umenu = false;
                smenu = false;
                terrain = true;
                unit = false;
                constantMode = false;
                minimap = false;
                //mapFilename = "temp.map";
            }else if(menuselection == 6){
                MAX_TILEW = getPositiveInteger("Enter a width in tiles for the screen",16);
                MAX_TILEH = getPositiveInteger("Enter a height in tiles for the screen",12);
                parentFrame.pack();
            }else if(menuselection == 7){
                int w = getPositiveInteger("Input the new map's width",1);
                int h = getPositiveInteger("Input the new map's height",1);
                
                Map temp = new Map(w,h);
                temp.copyMap(m);
                
                m = temp;
                b = new Battle(m);
                
                selectedArmy = null;
                selectedTerrain = new Plain();
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
                unitType = 0;
                menu = false;
                tmenu = false;
                umenu = false;
                smenu = false;
                terrain = true;
                unit = false;
                constantMode = false;
                minimap = false;
                //mapFilename = "temp.map";
            }else if(menuselection == 8){
                endBattle();
            }else if(menuselection == 9){
                m.setMapName(JOptionPane.showInputDialog("Enter a name for the map",m.getMapName()));
                m.setMapAuthor(JOptionPane.showInputDialog("Enter the author's name",m.getMapAuthor()));
                m.setMapDescription(JOptionPane.showInputDialog("Enter a description for this map",m.getMapDescription()));
            }else if(menuselection == 10){
                randomMap();
            }else if(menuselection == 11){
                String sel = JOptionPane.showInputDialog("Horizontal Mirror or Vertical Mirror?\nType h or v.");
                if(sel != null){
                    if(sel.equals("h")){
                        mirrorHorizontal();
                    }else if(sel.equals("v")){
                        mirrorVertical();
                    }
                }
            }
            
            currentMenu = null;
        }else if(tmenu){
            tmenu=false;
            int menuselection = currentMenu.doMenuItem();
            selectTerrain(menuselection);
            currentMenu = null;
            terrain = true;
            unit = false;
        }else if(smenu){
            smenu=false;
            int menuselection = currentMenu.doMenuItem();
            if(menuselection != 0)
                selectedArmy = b.getArmy(menuselection-1);
            else
                selectedArmy = null;
            selectTerrain(selectedTerrain.getIndex());
            currentMenu = null;
        }else if(umenu){
            umenu=false;
            unitType = currentMenu.doMenuItem();
            currentMenu = null;
            terrain = false;
            unit = true;
        }else if(terrain){
            placeTile();
        }else if(unit){
            placeUnit(m,m.find(new Location(cx,cy)),unitType);
        }
    }
    
    public void pressedB(){
        if(menu || tmenu || umenu || smenu){
            menu = false;
            tmenu = false;
            umenu = false;
            smenu = false;
            currentMenu = null;
        }else{
            if(m.find(new Location(cx,cy)).getTerrain() instanceof Property){
                if(((Property)m.find(new Location(cx,cy)).getTerrain()).getOwner()!=null)
                    selectedArmy = b.getArmy(((Property)m.find(new Location(cx,cy)).getTerrain()).getOwner().getID());
                else
                    selectedArmy = null;
            }
            selectTerrain(m.find(new Location(cx,cy)).getTerrain().getIndex());
            terrain = true;
            unit = false;
        }
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
    class MapKeyControl implements KeyListener{
        MapEditor parentScreen;
        
        public MapKeyControl(MapEditor m){
            super();
            parentScreen=m;
        }
        
        public void keyTyped(KeyEvent e) {}
        
        public void keyPressed(KeyEvent e){
            int keypress = e.getKeyCode();
            
            if(keypress == Options.up){
                if(menu || tmenu || umenu || smenu){
                    currentMenu.goUp();
                }else if(m.onMap(cx,cy-1)){
                    cy--;
                    if(cy < sy/16+2 && sy > 0)sy -= 16;
                }
                if(constantMode == true){
                    if(terrain){
                        placeTile();
                    }
                }
            }else if(keypress == Options.down){
                if(menu || tmenu || umenu || smenu){
                    currentMenu.goDown();
                }else if(m.onMap(cx,cy+1)){
                    cy++;
                    if(cy >= sy/16+MAX_TILEH-2 && cy < m.getMaxRow()-2)sy += 16;
                }
                if(constantMode == true){
                    if(terrain){
                        placeTile();
                    }
                }
            }else if(keypress == Options.left){
                if(menu){
                    
                }else if(tmenu){
                    tmenu = false;
                    currentMenu = null;
                    smenu = true;
                    currentMenu = new SideMenu(parentScreen);
                }else if(umenu){
                    umenu = false;
                    currentMenu = null;
                    tmenu = true;
                    if(selectedArmy!=null)
                        currentMenu = new TerrainMenu(selectedArmy.getColor()+1,parentScreen);
                    else
                        currentMenu = new TerrainMenu(0,parentScreen);
                }else if(smenu){
                    smenu = false;
                    currentMenu = null;
                    umenu = true;
                    if(selectedArmy!=null)
                        currentMenu = new UnitMenu(selectedArmy.getColor(),parentScreen);
                    else
                        currentMenu = new UnitMenu(0,parentScreen);
                }else if(m.onMap(cx-1,cy)){
                    cx--;
                    if(cx < sx/16+2 && sx > 0)sx -= 16;
                }
                if(constantMode == true){
                    if(terrain){
                        placeTile();
                    }
                }
            }else if(keypress == Options.right){
                if(menu){
                    
                }else if(tmenu){
                    tmenu = false;
                    currentMenu = null;
                    umenu = true;
                    if(selectedArmy!=null)
                        currentMenu = new UnitMenu(selectedArmy.getColor(),parentScreen);
                    else
                        currentMenu = new UnitMenu(0,parentScreen);
                }else if(umenu){
                    umenu = false;
                    currentMenu = null;
                    smenu = true;
                    currentMenu = new SideMenu(parentScreen);
                }else if(smenu){
                    smenu = false;
                    currentMenu = null;
                    tmenu = true;
                    if(selectedArmy!=null)
                        currentMenu = new TerrainMenu(selectedArmy.getColor()+1,parentScreen);
                    else
                        currentMenu = new TerrainMenu(0,parentScreen);
                }else if(m.onMap(cx+1,cy)){
                    cx++;
                    if(cx >= sx/16+MAX_TILEW-2 && cx < m.getMaxCol()-2)sx += 16;
                }
                if(constantMode == true){
                    if(terrain){
                        placeTile();
                    }
                }
            }else if(keypress == Options.akey){
                pressedA();
            }else if(keypress == Options.constmode){
                if(constantMode)constantMode = false;
                else constantMode = true;
            }else if(keypress == Options.delete){
                Unit temp = m.find(new Location(cx,cy)).getUnit();
                if(temp != null){
                    m.remove(temp);
                    temp.getArmy().removeUnit(temp);
                }
            }else if(keypress == Options.tkey){
                if(!menu && !umenu && !smenu){
                    if(!tmenu){
                        tmenu=true;
                        if(selectedArmy!=null)
                            currentMenu = new TerrainMenu(selectedArmy.getColor()+1,parentScreen);
                        else
                            currentMenu = new TerrainMenu(0,parentScreen);
                    }else tmenu=false;
                }
            }else if(keypress == Options.skey){
                if(!menu && !umenu && !tmenu){
                    if(!smenu){
                        smenu=true;
                        currentMenu = new SideMenu(parentScreen);
                    }else smenu=false;
                }
            }else if(keypress == Options.ukey){
                if(!menu && !smenu && !tmenu){
                    if(!umenu){
                        umenu=true;
                        if(selectedArmy!=null)
                            currentMenu = new UnitMenu(selectedArmy.getColor(),parentScreen);
                        else
                            currentMenu = new UnitMenu(0,parentScreen);
                    }else umenu=false;
                }
            }else if(keypress == Options.bkey){
                pressedB();
            }else if(keypress == Options.menu){
                if(selected == null){
                    if(!tmenu && !umenu && !smenu){
                        if(!menu){
                            menu=true;
                            currentMenu = new MapMenu(parentScreen);
                        }else menu=false;
                    }
                }
            }else if(keypress == Options.minimap){
                if(!minimap){
                    minimap = true;
                }else minimap=false;
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
    
    class MapMouseControl implements MouseInputListener{
        public void mouseClicked(MouseEvent e){
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            System.out.println(x + "," + y + ":" + e.getButton());
            
            if(e.getButton() == e.BUTTON1){
                if(menu || smenu){
                    int mitem = currentMenu.getMenuItemAt(x,y,scale);
                    if(mitem != -1){
                        currentMenu.setMenuItem(mitem);
                        pressedA();
                    }
                }else if(tmenu || umenu){
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
                }
            }else{
                //any other button
                if(!menu && !umenu && !tmenu && !smenu){
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
                    
                    cx = sx/16 + x/(16*scale);
                    if(cx < 0)cx=0;
                    else if(cx >= m.getMaxCol())cx=m.getMaxCol()-1;
                    cy = sy/16 + y/(16*scale);
                    if(cy < 0)cy=0;
                    else if(cy >= m.getMaxRow())cy=m.getMaxRow()-1;
                    pressedB();
                }
            }
        }
        
        public void mouseEntered(MouseEvent e) {}
        
        public void mouseExited(MouseEvent e) {}
        
        public void mousePressed(MouseEvent e) {
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            System.out.println(x + "," + y + ":" + e.getButton());
            
            if(e.getButton() == e.BUTTON1){
                if(!menu && !umenu && !tmenu && !smenu){
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
                    
                    cx = sx/16 + x/(16*scale);
                    if(cx < 0)cx=0;
                    else if(cx >= m.getMaxCol())cx=m.getMaxCol()-1;
                    cy = sy/16 + y/(16*scale);
                    if(cy < 0)cy=0;
                    else if(cy >= m.getMaxRow())cy=m.getMaxRow()-1;
                    pressedA();
                }
            }
        }
        public void mouseReleased(MouseEvent e) {}
        
        public void mouseDragged(MouseEvent e) {
            if(!menu && !umenu && !tmenu && !smenu){
                int x = e.getX() - parentFrame.getInsets().left;
                int y = e.getY() - parentFrame.getInsets().top;
                
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
                pressedA();
            }
        }
        
        public void mouseMoved(MouseEvent e){
            int x = e.getX() - parentFrame.getInsets().left;
            int y = e.getY() - parentFrame.getInsets().top;
            
            if(!menu && !umenu && !tmenu && !smenu){
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
            }
        }
    }
}