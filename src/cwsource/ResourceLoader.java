package cwsource;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cwsource.state.FileSystemManager;

public class ResourceLoader {
	
	public static Properties properties;
	final static Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
    //note, temporarily changed to work locally. /Users/kevin/Documents/_Dev/customwars/trunk
	public static final String PROJECT_LOCATION = "/Users/Albert/Documents/NetBeansProjects/folder";
		
	public static void init() {
		properties = new Properties(); 
		loadProperties();
		FileSystemManager.init();
	}
	
	public static void initTestProperties() {
		properties = new Properties();
		loadUnitTestProperties();
		FileSystemManager.init();
	}


	public static void loadProperties(){
		logger.info("Loading Live Game Properties");
		properties.setProperty("optionsLocation", PROJECT_LOCATION + "/res/options");
		properties.setProperty("BaseDMGLocation", PROJECT_LOCATION + "/res/BaseDMG.txt");
		properties.setProperty("AltDMGLocation", PROJECT_LOCATION + "/res/AltDMG.txt");
		properties.setProperty("imagesLocation", PROJECT_LOCATION + "/res/images");
		properties.setProperty("soundLocation", PROJECT_LOCATION + "/res/sound");
		properties.setProperty("tempSaveLocation", PROJECT_LOCATION + "/res/temp.save");
		properties.setProperty("mapsLocation", PROJECT_LOCATION + "/res/maps");
		properties.setProperty("saveLocation", PROJECT_LOCATION + "/res/save");
	}
	
	private static void loadUnitTestProperties() {
		logger.info("Loading Unit Test Properties");
		properties.setProperty("optionsLocation", PROJECT_LOCATION + "/test-res/options");
		properties.setProperty("BaseDMGLocation", PROJECT_LOCATION + "/test-res/BaseDMG.txt");
		properties.setProperty("AltDMGLocation", PROJECT_LOCATION + "/test-res/AltDMG.txt");
		properties.setProperty("imagesLocation", PROJECT_LOCATION + "/test-res/images");
		properties.setProperty("soundLocation", PROJECT_LOCATION + "/test-res/sound");
		properties.setProperty("tempSaveLocation", PROJECT_LOCATION + "/test-res/temp.save");
		properties.setProperty("mapsLocation", PROJECT_LOCATION + "/test-res/maps");
		properties.setProperty("saveLocation", PROJECT_LOCATION + "/test-res/save");
	}
    

}
