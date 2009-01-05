package com.customwars.officer;

import com.customwars.ai.Battle;
import com.customwars.map.Map;

public class COList {
    static Battle tempbat = new Battle(new Map(30,20));
    private static CO[] listing = {new Andy(), new Max(),new Sami(),new Nell(),new Hachi(tempbat),new Jake(),new Rachel(),new Olaf(),new Grit(),new Colin(tempbat), new Sasha(), new Eagle(), new Drake(),new Jess(),new Javier(tempbat),new Kanbei(),new Sonja(tempbat),new Sensei(),new Grimm(),new Flak(tempbat), new Lash(tempbat),new Adder(tempbat), new Hawke(), new Sturm(tempbat), new Jugger(tempbat), new Koal(), new Kindle(), new VonBolt(), new Ember(),  new Epoch(), new Peter(), new Sabaki(), new Alexander(), new Graves(), new Yukio(), new Ozzy(), new Edward(), new Conrad(), new Eric(),new Melanthe(), new Falcone(), new Carmen(), new Amy(), new Sophie(), new Mary(), new Napoleon(), new Koshi(), new Tempest(), new Rattigan(),  new Adam(), new Walter(), new Varlot(),new Minamoto(), new Smitan(), new Jared()};    
    public COList(){}
    
    public static CO[] getListing() {
        return listing;
    }

    public static void buildCOList(Battle b){
        for(int i = 1; i<listing.length; i++) {
            switch(i){
                case 1:
                    listing[i-1] = new Andy();break;
                case 2:
                    listing[i-1] = new Max();break;
                case 3:
                    listing[i-1] = new Sami();break;
                case 4:
                    listing[i-1] = new Nell();break;
                case 5:
                    listing[i-1] = new Hachi(b);break;
                case 6:
                    listing[i-1] = new Jake();break;
                case 7:
                    listing[i-1] = new Rachel();break;
                case 8:
                    listing[i-1] = new Olaf();break;
                case 9:
                    listing[i-1] = new Grit();break;
                case 10:
                    listing[i-1] = new Colin(b);break;
                case 11:
                    listing[i-1] = new Sasha();break;
                case 12:
                    listing[i-1] = new Eagle();break;
                case 13:
                    listing[i-1] = new Drake();break;
                case 14:
                    listing[i-1] = new Jess();break;
                case 15:
                    listing[i-1] = new Javier(b);break;
                case 16:
                    listing[i-1] = new Kanbei();break;
                case 17:
                    listing[i-1] = new Sonja(b);break;
                case 18:
                    listing[i-1] = new Sensei();break;
                case 19:
                    listing[i-1] = new Grimm();break;
                case 20:
                    listing[i-1] = new Flak(b);break;
                case 21:
                    listing[i-1] = new Lash(b);break;
                case 22:
                    listing[i-1] = new Adder(b);break;
                case 23:
                    listing[i-1] = new Hawke();break;
                case 24:
                    listing[i-1] = new Sturm(b);break;
                case 25:
                    listing[i-1] = new Jugger(b);break;
                case 26:
                    listing[i-1] = new Koal();break;
                case 27:
                    listing[i-1] = new Kindle();break;
                case 28:
                    listing[i-1] = new VonBolt();break;
                case 29:
                    listing[i-1] = new Ember();break;
                case 30:
                    listing[i-1] = new Epoch();break;
                case 31:
                    listing[i-1] = new Peter();break;
                case 32:
                    listing[i-1] = new Sabaki();break;
                case 33:
                    listing[i-1] = new Alexander();break;
                case 34:
                    listing[i-1] = new Graves();break;
                case 35:
                    listing[i-1] = new Yukio();break;
                case 36:
                    listing[i-1] = new Ozzy();break;
                case 37:
                    listing[i-1] = new Edward();break;
                case 38:
                    listing[i-1] = new Conrad();break;
                case 39:
                    listing[i-1] = new Eric();break;
                case 40:
                    listing[i-1] = new Melanthe();break;
                case 41:
                    listing[i-1] = new Falcone();break;
                case 42:
                    listing[i-1] = new Carmen();break;
                case 43:
                    listing[i-1] = new Amy();break;
                case 44:
                    listing[i-1] = new Sophie();break;
                case 45:
                    listing[i-1] = new Mary();break;
                case 46:
                    listing[i-1] = new Napoleon();break;
                case 47:
                    listing[i-1] = new Koshi();break;
                case 48:
                    listing[i-1] = new Tempest();break;
                case 49:
                    listing[i-1] = new Rattigan();break;
                case 50:
                    listing[i-1] = new Adam();break;
                case 51:
                    listing[i-1] = new Walter();break;
                case 52:
                    listing[i-1] = new Varlot();break;
                case 53:
                    listing[i-1] = new Minamoto();break;
                case 54:
                    listing[i-1] = new Smitan();break;
                case 71:
                    listing[i-1] = new Jared();break;
            }
        }
    }
    
    public static CO getCO(int index) {
        return listing[index];
    }
    public static int getIndex(CO c){
        for(int i = 0; i < listing.length; i++)
        {
            if(c.getId() == listing[i].getId())
                return i;
        }
        return -1;
    }
    public static String getLowerCaseName(int index) {
        return listing[index].getName().toLowerCase();
    }
}
