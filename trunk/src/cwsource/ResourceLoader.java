package cwsource;

import java.util.Properties;

public class ResourceLoader {
	
	public static Properties properties;
		
	public static void init() {
		properties = new Properties(); 
		loadKevDevProperties();
	}

	public static void loadKevDevProperties(){
		properties.setProperty("optionsLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/options");
		properties.setProperty("BaseDMGLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/BaseDMG.txt");
		properties.setProperty("AltDMGLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/AltDMG.txt");
		properties.setProperty("imagesLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/images");
		properties.setProperty("soundLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/sound");
		properties.setProperty("tempSaveLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/temp.save");
		properties.setProperty("mapsLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/maps");
		properties.setProperty("saveLocation", "/Users/kevin/Documents/_Dev/customwars/devRelease/res/save");
	}
	
    

}
