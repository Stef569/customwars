package cwsource;

import java.awt.*;

public class CWArtist 
{
	public static final void drawUnitAtXY(Graphics2D g, CWScreen obs, int utype, int color, int x, int y) 
	{
		Image uImg = UnitGraphics.getUnitImage(utype, color);
		int imgY = UnitGraphics.findYPosition(utype, color);
		g.drawImage(uImg,x,y,x+16,y+16,0, imgY, 16, imgY + 16, obs);
	}
	
	public static final void drawTerrainAtXY(Graphics2D g, CWScreen obs, Tile currTile, int x, int y) 
	{
		Image temp;
		
		if(currTile == null)
			return;
		
        Terrain ter = currTile.getTerrain();
        
        int spriteX1 = TerrType.getSearchIndex(currTile, obs.getBattle());
        int spriteX2 = spriteX1 + 16;
        
        temp = getCorrectTerrainSheet(currTile, ter, obs.getBattle());
		
		g.drawImage(temp, x, y-16, x+16, y+16, spriteX1, 0, spriteX2, 32, obs);
                
	}

	public static final Image getCorrectTerrainSheet(Tile currTile, Terrain ter, Battle b) 
	{
		if(ter instanceof Property) 
        {
        	if(ter instanceof HQ)
        		return TerrainGraphics.getHQSpriteSheet(((Property)ter).owner.getColor()+1);
        	
        	else if(((Property)ter).owner == null || (b.isFog() && ter instanceof Property && b.getFog(currTile.getLocation().getCol(),currTile.getLocation().getRow())))
                return TerrainGraphics.getUrbanSpriteSheet(0);
        	
            else
                return TerrainGraphics.getUrbanSpriteSheet(((Property)ter).owner.getColor()+1);
        } 
        else 
        {
            return TerrainGraphics.getTerrainSpriteSheet();
        }
	}
	
	public static void drawUnitStatus(Graphics2D g, CWScreen obs, Unit thisUnit, int x, int y) 
	{
		//darken the unit if inactive
		if(!thisUnit.isActive())
		{
		    if(thisUnit.getArmy() == obs.getBattle().getArmy(obs.getBattle().getTurn()))
		    {
		        g.setColor(Color.black);
		        g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
		        //g.drawImage(UnitGraphics.getDarkUnit(thisUnit),x, y,this);
		        g.fillRect(x, y,16,16);
		        g.setComposite(AlphaComposite.SrcOver);
		    }
		}
		
		//draw the low ammo icon if ammo is low
		if(thisUnit.isLowOnAmmo())
		{
		    g.drawImage(MiscGraphics.getLowAmmoIcon(), x, y+8,obs);
		}
		
		//draw the low fuel icon if fuel is low
		if(thisUnit.isLowOnFuel()){
		    g.drawImage(MiscGraphics.getLowFuelIcon(), x, y+8,obs);
		}
		
		//draw the capture icon if capturing
		if(obs.getBattle().getMap().find(thisUnit).getTerrain() instanceof Property)
		{
		    Property prop = (Property)obs.getBattle().getMap().find(thisUnit).getTerrain();
		    if(prop.getCapturePoints() < prop.getMaxCapturePoints())
		        g.drawImage(MiscGraphics.getCaptureIcon(), x, y+8,obs);
		}
		
		//draw the dived icon if diving
		if(thisUnit.isDived())
		{
		    g.drawImage(MiscGraphics.getDiveIcon(), x, y+8,obs);
		}
		
		//draw the the load icon if a transport with units
		if(thisUnit instanceof Transport)
		{
		    Transport trans = (Transport)thisUnit;
		    if(trans.getUnitsCarried() > 0)
		        g.drawImage(MiscGraphics.getLoadIcon(), x, y+8,obs);
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
		
		if(obs instanceof BattleScreen)
			hideTheHP = ((BattleScreen)obs).assessHideTheHP();
		
		if(!hideTheHP && thisUnit.getDisplayHP()!=10 && (!thisUnit.getArmy().getCO().hiddenHP || obs.getBattle().getArmy(obs.getBattle().getTurn()) == thisUnit.getArmy()))
		{
			g.drawImage(MiscGraphics.getHpDisplay(thisUnit.getDisplayHP()),x+8,y+9,obs);
		} 
		else if(hideTheHP || thisUnit.getArmy().getCO().hiddenHP && (thisUnit.getDisplayHP()!=10 || obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() != thisUnit.getArmy().getSide()))
		{
		    g.drawImage(MiscGraphics.getHiddenHP(),x+8,y+9,obs);
		}
	}

	public static void drawTerrainInfo(Graphics2D g, CWScreen obs, Tile currTile, int x, int y) 
	{
        g.setColor(Color.white);
        g.setFont(new Font("SansSerif", Font.PLAIN, 10));
        g.drawString(currTile.getTerrain().getName(), x, y + 10);
        
		if(currTile.getTerrain() instanceof Invention) 
        {
            Invention inv = (Invention)currTile.getTerrain();

            //Draw invention HP
            g.drawImage(MiscGraphics.getSmallHeart(), x + 2, y + 30, obs);
            g.drawString("" + inv.getHP(), x + 10, y + 36);
            
            if(obs instanceof BattleScreen)
            {
            	BattleScreen bs = (BattleScreen)obs;
            	if(bs.isFiring() && bs.getSelectedUnit().damageCalc(inv) > -1) 
            	{
	                g.setColor(Color.red);
	                g.fillRect(x, y - 16, 32, 16);
	                
	                g.setColor(Color.white);
	                g.setFont(new Font("SansSerif", Font.BOLD, 16));
	                g.drawString("" + obs.getSelectedUnit().damageCalc(inv), x, y);
        		}
            }
        } 
        else 
        {
            //defense stars
            g.drawImage(MiscGraphics.getSmallStar(5), x, y + 28, obs);
            g.drawString("" + currTile.getTerrain().getDef(), x + 10, y + 36);
            
            //capture points
            if(currTile.getTerrain() instanceof Property) 
            {
                g.drawImage(MiscGraphics.getCaptureIcon(), x, y + 38,obs);
                g.drawString("" + ((Property)currTile.getTerrain()).getCapturePoints(), x + 10, y + 46);
            }
        }
	}

	public static void drawUnitInfo(Graphics2D g, CWScreen obs, Tile currTile, int x, int y) 
	{
		Unit tileUnit = currTile.getUnit();
		g.setColor(Color.black);
		g.fillRect(x, y, 32, 56);
		
		boolean hideTheHP = false;
		
		if(obs instanceof BattleScreen)
			hideTheHP = ((BattleScreen)obs).assessHideTheHP();
		
		g.setColor(Color.white);
		
		if(tileUnit.getArmy().getCO().hiddenUnitInfo && (obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() != tileUnit.getArmy().getSide())) 
		{
			g.drawString("?????", x, y + 10); 
		} 
		else 
		{
			g.drawString(tileUnit.getName(), x, y + 10);
		}
		
		//This line is used for drawing normal units
		//g.drawImage(UnitGraphics.getUnitImage(tileUnit), x + 8, y + 10, x + 8 + 16, y + 10 + 16, 0, UnitGraphics.findYPosition(tileUnit),16,UnitGraphics.findYPosition(tileUnit)+16,obs);
		drawUnitAtXY(g, obs, tileUnit.getUType(), tileUnit.getArmy().getColor(), x + 8, y + 10);
		
		//16*MAX_TILEW-63
		//This is when the unit's HP is drawn
		g.drawImage(MiscGraphics.getSmallHeart(), x + 2, y + 30, obs);
		
		if(hideTheHP || tileUnit.getArmy().getCO().hiddenHP && obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() != tileUnit.getArmy().getSide()) 
		{
			g.drawString("?", x + 10, y + 36);
		} 
		else 
		{
			if(obs.getBattle().getArmy(obs.getBattle().getTurn()).getCO().seeFullHP)
			{
				String onesDigit = "" + (tileUnit.getHP() % 10);
				String tensDigit = "" + (tileUnit.getHP() / 10);
				
				g.drawString(tensDigit + "." + onesDigit, x + 10, y + 36);
			}
			else
			{
				g.drawString("" + tileUnit.getDisplayHP(), x + 10, y + 36);
			}
		}
		
		//This is when the unit's fuel is drawn
		g.drawImage(MiscGraphics.getLowFuelIcon(), x, y + 38,obs);
		
		if(tileUnit.getArmy().getCO().hiddenUnitInfo && (obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() != tileUnit.getArmy().getSide())) 
		{
			g.drawString("?", x + 10, y + 46);
		} 
		else 
		{
			g.drawString("" + tileUnit.getGas(), x + 10, y + 46);
		}
		
		//This is when the unit's ammo is drawn
		if(tileUnit.getAmmo() != -1 || (tileUnit.getArmy().getCO().hiddenUnitInfo && (obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() != tileUnit.getArmy().getSide()))) 
		{
			g.drawImage(MiscGraphics.getLowAmmoIcon(), x, y + 48, obs);
		    
		    if(tileUnit.getArmy().getCO().hiddenUnitInfo && (obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() != tileUnit.getArmy().getSide())) 
		    {
		    	g.drawString("?", x + 10, y + 56);
		    } 
		    else 
		    {
		    	g.drawString("" + tileUnit.getAmmo(), x + 10, y + 56);
		    }
		}
		
		//This is where the damage display is drawn for units
		
		if(obs instanceof BattleScreen)
		{
			if(((BattleScreen)obs).isFiring() && (tileUnit.getArmy().getSide() != obs.getSelectedUnit().getArmy().getSide()) && obs.getSelectedUnit().displayDamageCalc(tileUnit) > -1 /*&& obs.getSelectedUnit().checkFireRange(obs.getCursorLoc())*/)
			{
			    g.setColor(Color.red);
			    g.fillRect(x, y - 16, 32, 16);
			    
			    g.setColor(Color.white);
			    g.setFont(new Font("SansSerif", Font.BOLD, 16));
			    
			    //Koshi can get LOL readouts!
			    if(tileUnit.getArmy().getCO().hiddenUnitInfo && (obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() != tileUnit.getArmy().getSide())) 
			    {
			    	g.drawString("LOL", x, y);
			    } 
			    else 
			    {
			    	g.drawString(obs.getSelectedUnit().displayDamageCalc(tileUnit)+"", x, y);
			    }
			}
		}
		
		//drawTransInfoBox(g, obs, tileUnit);
	}

	public static void drawTransInfoBox(Graphics2D g, CWScreen obs, Unit tileUnit, int x, int y) 
	{
		//Transport box
		if(tileUnit != null &&
		   tileUnit instanceof Transport && 
		   (!obs.getBattle().isFog() || 
		    obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() == tileUnit.getArmy().getSide())) 
		{
		    Transport trans = (Transport) tileUnit;
			
		    if((trans.getUnitsCarried()>0) && !trans.getArmy().getCO().hiddenUnitInfo && (obs.getBattle().getArmy(obs.getBattle().getTurn()).getSide() == tileUnit.getArmy().getSide())) 
		    {
		        g.setColor(Color.black);
		        g.fillRect(x, y, 32, 56);

		        //This line is used for drawing the first loaded unit
		        //g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(1)), trnsBox_x + 8, trnsBox_y + 20, trnsBox_x + 8 + 16, trnsBox_y + 20 + 16, 0, UnitGraphics.findYPosition(trans.getUnit(1)),16,UnitGraphics.findYPosition(trans.getUnit(1))+16,this);
		        CWArtist.drawUnitAtXY(g, obs, trans.getUnit(1).getUType(), trans.getUnit(1).getArmy().getColor(),  x + 8, y + 20);
		        
		        if((trans.getUnitsCarried() == 2)) 
		        {
		            //This line is used for drawing the second loaded unit
		        	//g.drawImage(UnitGraphics.getUnitImage(trans.getUnit(2)), trnsBox_x + 8, trnsBox_y + 34, trnsBox_x + 8 + 16, trnsBox_y + 20 + 34, 0, UnitGraphics.findYPosition(trans.getUnit(1)),16,UnitGraphics.findYPosition(trans.getUnit(1))+16,this);
		        	CWArtist.drawUnitAtXY(g, obs, trans.getUnit(2).getUType(), trans.getUnit(2).getArmy().getColor(),  x + 8, y + 20);
		        } 
		    }
		}
	}
}
