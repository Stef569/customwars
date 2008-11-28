package cwsource;
/*
 *ContextMenu.java
 *Author: Urusan
 *Contributors:
 *Creation: July 16, 2006, 5:24 AM
 *This menu pops up when you click on a property that can construct units
 */

import java.awt.*;
import java.awt.image.*;
import java.awt.Color.*;
import java.awt.GradientPaint.*;

public class BuildMenu extends ScrollMenu{
    private boolean[] canBuy = new boolean[BaseDMG.NUM_UNITS];
    private boolean[] canBuyDisplay = new boolean[BaseDMG.NUM_UNITS];
    private int[] unitCost = new int[BaseDMG.NUM_UNITS];
    private int order[] = new int[BaseDMG.NUM_UNITS];
    int color;
    int style;
    
    //constructor
    public BuildMenu(boolean land, boolean sea, boolean air, boolean pipe, boolean carrier, int funds, CO co, Battle batt, ImageObserver screen){
        super((480-192)/2,32,192,screen,17);
        String[] s = new String[BaseDMG.NUM_UNITS]; //maximum possible is 32 Units
        //Image[] ic = new Image[32];
        
        color = co.getArmy().getColor();
        style = co.getStyle();
        
        int i = 0;
        
        if(land){
            if(!batt.getBattleOptions().isUnitBanned(0)){
                s[i]="Infantry";
                unitCost[i]=1000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[0]/100;
                if(funds >= unitCost[i]){
                    canBuy[0]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(0,co.getArmy().getColor());
                order[i] = 0;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(1)){
                s[i]="Mech";
                unitCost[i]=3000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[1]/100;
                if(funds >= unitCost[i]){
                    canBuy[1]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(1,co.getArmy().getColor());
                order[i] = 1;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(4)){
                s[i]="Recon";
                unitCost[i]=4000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[4]/100;
                if(funds >= unitCost[i]){
                    canBuy[4]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(4,co.getArmy().getColor());
                order[i] = 4;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(2)){
                s[i]="Tank";
                unitCost[i]=7000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[2]/100;
                if(funds >= unitCost[i]){
                    canBuy[2]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(2,co.getArmy().getColor());
                order[i] = 2;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(3)){
                s[i]="Md Tank";
                unitCost[i]=15000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[3]/100;
                if(funds >= unitCost[i]){
                    canBuy[3]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(3,co.getArmy().getColor());
                order[i] = 3;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(18)){
                s[i]="Neotank";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[18]/100;
                if(funds >= unitCost[i]){
                    canBuy[18]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(18,co.getArmy().getColor());
                order[i] = 18;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(19)){
                s[i]="Megatank";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[19]/100;
                if(funds >= unitCost[i]){
                    canBuy[19]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(19,co.getArmy().getColor());
                order[i] = 19;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(9)){
                s[i]="APC";
                unitCost[i]=5000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[9]/100;
                if(funds >= unitCost[i]){
                    canBuy[9]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(9,co.getArmy().getColor());
                order[i] = 9;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(7)){
                s[i]="Artillery";
                unitCost[i]=6000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[7]/100;
                if(funds >= unitCost[i]){
                    canBuy[7]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(7,co.getArmy().getColor());
                order[i] = 7;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(8)){
                s[i]="Rockets";
                unitCost[i]=14000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[8]/100;
                if(funds >= unitCost[i]){
                    canBuy[8]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(8,co.getArmy().getColor());
                order[i] = 8;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(5)){
                s[i]="Anti-Air";
                unitCost[i]=8000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[5]/100;
                if(funds >= unitCost[i]){
                    canBuy[5]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(5,co.getArmy().getColor());
                order[i] = 5;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(6)){
                s[i]="Missiles";
                unitCost[i]=12000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[6]/100;
                if(funds >= unitCost[i]){
                    canBuy[6]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(6,co.getArmy().getColor());
                order[i] = 6;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(20)){
                s[i]="Piperunner";
                unitCost[i]=18000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[20]/100;
                if(funds >= unitCost[i]){
                    canBuy[20]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(20,co.getArmy().getColor());
                order[i] = 20;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(27)){
                s[i]="SRunner";
                unitCost[i]=10000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[27]/100;
                if(funds >= unitCost[i]){
                    canBuy[27]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(27,co.getArmy().getColor());
                order[i] = 27;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(31)){
                s[i]="Oozium";
                unitCost[i]=10000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[31]/100;
                if(funds >= unitCost[i]){
                    canBuy[31]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(31,co.getArmy().getColor());
                order[i] = 31;
                i++;
            }
            
        }
        if(sea){
            if(!batt.getBattleOptions().isUnitBanned(13)){
                s[i]="Battleship";
                if(Options.isBalance())
                    unitCost[i]=22000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[13]/100;
                else
                unitCost[i]=25000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[13]/100;
                if(funds >= unitCost[i]){
                    canBuy[13]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(13,co.getArmy().getColor());
                order[i] = 13;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(11)){
                s[i]="Cruiser";
                if(Options.isBalance())
                    unitCost[i]=15000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[11]/100;
                else
                unitCost[i]=18000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[11]/100;
                if(funds >= unitCost[i]){
                    canBuy[11]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(11,co.getArmy().getColor());
                order[i] = 11;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(10)){
                s[i]="Lander";
                if (batt.getBattleOptions().isBalance()==true){
                    unitCost[i]=10000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[10]/100;
                } else
                    unitCost[i]=12000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[10]/100;
                if(funds >= unitCost[i]){
                    canBuy[10]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(10,co.getArmy().getColor());
                order[i] = 10;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(12)){
                s[i]="Sub";
                if (batt.getBattleOptions().isBalance()==true){
                    unitCost[i]=15000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[12]/100;
                } else
                    unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[12]/100;
                if(funds >= unitCost[i]){
                    canBuy[12]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(12,co.getArmy().getColor());
                order[i] = 12;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(21)){
                s[i]="Black Boat";
                unitCost[i]=7500*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[21]/100;
                if(funds >= unitCost[i]){
                    canBuy[21]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(21,co.getArmy().getColor());
                order[i] = 21;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(22)){
                s[i]="Carrier";
                unitCost[i]=25000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[22]/100;
                if(funds >= unitCost[i]){
                    canBuy[22]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(22,co.getArmy().getColor());
                order[i] = 22;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(30)){
                s[i]="Destroyer";
                if(Options.isBalance())
                    unitCost[i]=15000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[30]/100;
                else
                unitCost[i]=18000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[30]/100;
                if(funds >= unitCost[i]){
                    canBuy[30]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(30,co.getArmy().getColor());
                order[i] = 30;
                i++;
            }
        }
        if(air){
            if(!batt.getBattleOptions().isUnitBanned(16)){
                s[i]="Fighter";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[16]/100;
                if(funds >= unitCost[i]){
                    canBuy[16]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(16,co.getArmy().getColor());
                order[i] = 16;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(17)){
                s[i]="Bomber";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[17]/100;
                if(funds >= unitCost[i]){
                    canBuy[17]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(17,co.getArmy().getColor());
                order[i] = 17;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(15)){
                s[i]="B Copter";
                unitCost[i]=9000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[15]/100;
                if(funds >= unitCost[i]){
                    canBuy[15]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(15,co.getArmy().getColor());
                order[i] = 15;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(14)){
                s[i]="T Copter";
                unitCost[i]=5000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[14]/100;
                if(funds >= unitCost[i]){
                    canBuy[14]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(14,co.getArmy().getColor());
                order[i] = 14;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(23)){
                s[i]="Stealth";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[23]/100;
                if(funds >= unitCost[i]){
                    canBuy[23]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(23,co.getArmy().getColor());
                order[i] = 23;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(24)){
                s[i]="Black Bomb";
                if(batt.getBattleOptions().isBalance())
                    unitCost[i]=15000*co.getUnitCostMultiplier()[24]/100;
                else
                unitCost[i]=25000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[24]/100;
                if(funds >= unitCost[i]){
                    canBuy[24]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(24,co.getArmy().getColor());
                order[i] = 24;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(28)){
                s[i]="Zeppelin";
                unitCost[i]=10000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[28]/100;
                if(funds >= unitCost[i]){
                    canBuy[28]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(28,co.getArmy().getColor());
                order[i] = 28;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(29)){
                s[i]="Spyplane";
                unitCost[i]=15000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[29]/100;
                if(funds >= unitCost[i]){
                    canBuy[29]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(29,co.getArmy().getColor());
                order[i] = 29;
                i++;
            }
        }
        if(land){
            if(!batt.getBattleOptions().isUnitBanned(25)){
                s[i]="Bcraft";
                unitCost[i]=10000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[25]/100;
                if(funds >= unitCost[i]){
                    canBuy[25]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(25,co.getArmy().getColor());
                order[i] = 25;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(26)){
                s[i]="Acraft";
                unitCost[i]=10000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[26]/100;
                if(funds >= unitCost[i]){
                    canBuy[26]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(26,co.getArmy().getColor());
                order[i] = 26;
                i++;
            }
        }
        if(sea){
            if(!batt.getBattleOptions().isUnitBanned(25)){
                s[i]="Bcraft";
                unitCost[i]=15000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[25]/100;
                if(funds >= unitCost[i]){
                    canBuy[25]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(25,co.getArmy().getColor());
                order[i] = 25;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(26)){
                s[i]="Acraft";
                unitCost[i]=15000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[26]/100;
                if(funds >= unitCost[i]){
                    canBuy[26]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(26,co.getArmy().getColor());
                order[i] = 26;
                i++;
            }
        }
        if(!land && pipe){
            if(!batt.getBattleOptions().isUnitBanned(20)){
                s[i]="Piperunner";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[20]/100;
                if(funds >= unitCost[i]){
                    canBuy[20]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(20,co.getArmy().getColor());
                order[i] = 20;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(27)){
                s[i]="SRunner";
                unitCost[i]=10000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[27]/100;
                if(funds >= unitCost[i]){
                    canBuy[27]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(27,co.getArmy().getColor());
                order[i] = 27;
                i++;
            }
        }
        if(carrier){
            if(!batt.getBattleOptions().isUnitBanned(16)){
                s[i]="Fighter";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[16]/100* 80/100;
                if(funds >= unitCost[i]){
                    canBuy[16]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(16,co.getArmy().getColor());
                order[i] = 16;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(17)){
                s[i]="Bomber";
                unitCost[i]=20000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[17]/100* 80/100;
                if(funds >= unitCost[i]){
                    canBuy[17]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(17,co.getArmy().getColor());
                order[i] = 17;
                i++;
            }
            if(!batt.getBattleOptions().isUnitBanned(15)){
                s[i]="B Copter";
                unitCost[i]=9000*co.getCostMultiplier()/100*co.getUnitCostMultiplier()[15]/100 * 80/100;
                if(funds >= unitCost[i]){
                    canBuy[15]=true;
                    canBuyDisplay[i]=true;
                }
                //ic[i] = UnitGraphics.getUnitImage(15,co.getArmy().getColor());
                order[i] = 15;
                i++;
            }
        }
        String[] s2 = new String[i];
        for(int j=0;j<i;j++)s2[j]=s[j];
        //Image[] icons = new Image[i];
        //for(int j=0;j<i;j++)icons[j] = ic[j];
        
        super.loadStrings(s2);
    }
    
    public int doMenuItem(){
        if(displayItems != null && displayItems.length != 0){
            if(displayItems[item].equals("Infantry")){
                System.out.println("Infantry");
                if(canBuy[0])return 0;
            }else if(displayItems[item].equals("Mech")){
                System.out.println("Mech");
                if(canBuy[1])return 1;
            }else if(displayItems[item].equals("Tank")){
                System.out.println("Tank");
                if(canBuy[2])return 2;
            }else if(displayItems[item].equals("Md Tank")){
                System.out.println("Md Tank");
                if(canBuy[3])return 3;
            }else if(displayItems[item].equals("Recon")){
                System.out.println("Recon");
                if(canBuy[4])return 4;
            }else if(displayItems[item].equals("Anti-Air")){
                System.out.println("Anit-Air");
                if(canBuy[5])return 5;
            }else if(displayItems[item].equals("Missiles")){
                System.out.println("Missiles");
                if(canBuy[6])return 6;
            }else if(displayItems[item].equals("Artillery")){
                System.out.println("Artillery");
                if(canBuy[7])return 7;
            }else if(displayItems[item].equals("Rockets")){
                System.out.println("Rockets");
                if(canBuy[8])return 8;
            }else if(displayItems[item].equals("APC")){
                System.out.println("APC");
                if(canBuy[9])return 9;
            }else if(displayItems[item].equals("Lander")){
                System.out.println("Lander");
                if(canBuy[10])return 10;
            }else if(displayItems[item].equals("Cruiser")){
                System.out.println("Cruiser");
                if(canBuy[11])return 11;
            }else if(displayItems[item].equals("Sub")){
                System.out.println("Sub");
                if(canBuy[12])return 12;
            }else if(displayItems[item].equals("Battleship")){
                System.out.println("Battleship");
                if(canBuy[13])return 13;
            }else if(displayItems[item].equals("T Copter")){
                System.out.println("T Copter");
                if(canBuy[14])return 14;
            }else if(displayItems[item].equals("B Copter")){
                System.out.println("B Copter");
                if(canBuy[15])return 15;
            }else if(displayItems[item].equals("Fighter")){
                System.out.println("Fighter");
                if(canBuy[16])return 16;
            }else if(displayItems[item].equals("Bomber")){
                System.out.println("Bomber");
                if(canBuy[17])return 17;
            }else if(displayItems[item].equals("Neotank")){
                System.out.println("Neotank");
                if(canBuy[18])return 18;
            }else if(displayItems[item].equals("Megatank")){
                System.out.println("Megatank");
                if(canBuy[19])return 19;
            }else if(displayItems[item].equals("Piperunner")){
                System.out.println("Piperunner");
                if(canBuy[20])return 20;
            }else if(displayItems[item].equals("Black Boat")){
                System.out.println("Black Boat");
                if(canBuy[21])return 21;
            }else if(displayItems[item].equals("Carrier")){
                System.out.println("Carrier");
                if(canBuy[22])return 22;
            }else if(displayItems[item].equals("Stealth")){
                System.out.println("Stealth");
                if(canBuy[23])return 23;
            }else if(displayItems[item].equals("Black Bomb")){
                System.out.println("Black Bomb");
                if(canBuy[24])return 24;
            }else if(displayItems[item].equals("Bcraft")){
                System.out.println("Bcraft");
                if(canBuy[25])return 25;
            }else if(displayItems[item].equals("Acraft")){
                System.out.println("Acraft");
                if(canBuy[26])return 26;
            }else if(displayItems[item].equals("SRunner")){
                System.out.println("Shuttlerunner");
                if(canBuy[27])return 27;
            }else if(displayItems[item].equals("Zeppelin")){
                System.out.println("Zeppelin");
                if(canBuy[28])return 28;
            }else if(displayItems[item].equals("Spyplane")){
                System.out.println("Spyplane");
                if(canBuy[29])return 29;
            }else if(displayItems[item].equals("Destroyer")){
                System.out.println("Destroyer");
                if(canBuy[30])return 30;
            }else if(displayItems[item].equals("Oozium")){
                System.out.println("Oozium");
                if(canBuy[31])return 31;
            }else{
                System.err.println("ERROR, INVALID CONTEXT MENU ITEM");
            }
        }
        return -1;
    }
    
    public static BuildMenu generateContext(Property p, int turn, Battle batt, ImageObserver screen){
        boolean land = false, sea = false, air = false, pipe = false;
        
        if(p.getOwner()==null || p.getOwner().getID()!=turn)return null;
        
        int funds = p.getOwner().getFunds();
        CO co = p.getOwner().getCO();
        
        if(p.canCreateLand())land = true;
        if(p.canCreateSea())sea = true;
        if(p.canCreateAir())air = true;
        if(p.canCreatePipe())pipe = true;
        if(!land)
            if(!sea)
                if(!air)
                    if(!pipe)
                        return null;
        
        return new BuildMenu(land,sea,air,pipe,false,funds,co,batt,screen);
    }
    public static BuildMenu generateCarrierMenu(int funds,CO co, Battle batt, ImageObserver screen)
    {
        return new BuildMenu(false,false,false,false,true,funds,co,batt,screen);
    }
    //Draws the Menu
    public void drawMenu(Graphics2D g){
        Graphics2D g2 = (Graphics2D)g;
        if(icons == null){
    //Makes the gradient
            GradientPaint gp = SwitchGradient.getGradient(((BattleScreen)screen).getBattle().getArmy(((BattleScreen)screen).getBattle().getTurn()).getColor());
    // Fill with a gradient.
    g2.setPaint(gp);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.8f));
            g2.fillRoundRect(mx,my,width,numItems*16+2,16,16);
            g.setComposite(AlphaComposite.SrcOver);
        
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD,16));
        for(int i=currentPosition; i<maxItems+currentPosition; i++){
            int zx = mx+16;
            int zy = my+((i-currentPosition)*16);
            int ypos = UnitGraphics.findYPosition(order[i],style);
            g.drawImage(UnitGraphics.getUnitImage(order[i],color),zx,zy,zx+16,zy+16,0,ypos,16,ypos+16,screen);
            g.drawString(displayItems[i],mx+32,my+((i-currentPosition)*16)+16);
        }
        
        g.drawImage(MiscGraphics.getPointer(),mx,my+item*16,screen); 
        //g.setColor(Color.red);
        //g.fillRoundRect(mx,my+(item-currentPosition)*16,16,16,16,16);
        //extra stuff
        g.setColor(Color.black);
        g.setFont(new Font("SansSerif", Font.BOLD,16));
        for(int i=currentPosition; i<maxItems+currentPosition; i++){
            g.drawString("" + unitCost[i],mx+130,my+((i-currentPosition)*16)+16);
            if(!canBuyDisplay[i]){
                g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
                g.fillRect(mx+16,my+((i-currentPosition)*16),176,16);
                g.setComposite(AlphaComposite.SrcOver);
               }
            }
        }
    }
}