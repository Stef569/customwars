package com.customwars.state;

import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import java.io.File;

import com.customwars.ai.Options;



public class ResourceLoader {
	
        public static File theFile = new File("");
	public static Properties properties;
	final static Logger logger = LoggerFactory.getLogger(ResourceLoader.class);
        public static final String PROJECT_LOCATION = theFile.getAbsolutePath()+"/trunk/res";
        
		
	public static void init() {
		properties = new Properties(); 
		loadProperty();
		FileSystemManager.init();
	}
	
	public static void initTestProperties() {
		properties = new Properties();
		loadUnitTestProperties();
		FileSystemManager.init();
		Options.setServerName("http://localhost/customwars/cw/cw1/");
	}

        public static void loadProperty(){
		logger.info("Loading Live Game Properties");
		properties.setProperty("optionsLocation", PROJECT_LOCATION + "/options");
		properties.setProperty("BaseDMGLocation", PROJECT_LOCATION + "/BaseDMG.txt");
		properties.setProperty("AltDMGLocation", PROJECT_LOCATION + "/AltDMG.txt");
		properties.setProperty("imagesLocation", PROJECT_LOCATION + "/images");
		properties.setProperty("soundLocation", PROJECT_LOCATION + "/sound");
		properties.setProperty("tempSaveLocation", PROJECT_LOCATION + "/save");
		properties.setProperty("mapsLocation", PROJECT_LOCATION + "/maps");
		properties.setProperty("saveLocation", PROJECT_LOCATION + "/save");
	}

	public static void loadProperties(){
		logger.info("Loading Live Game Properties");
		properties.setProperty("optionsLocation", ResourceLoader.class.getResource("/core/options/options").getFile().toString());
		properties.setProperty("BaseDMGLocation", ResourceLoader.class.getResource("/core/damagetables/BaseDMG.txt").getFile().toString());
		properties.setProperty("AltDMGLocation",  ResourceLoader.class.getResource("/core/damagetables/AltDMG.txt").getFile().toString());
		properties.setProperty("imagesLocation", ResourceLoader.class.getResource("/core/images").getFile().toString());
		properties.setProperty("soundLocation", ResourceLoader.class.getResource("/core/sound").getFile().toString());
		properties.setProperty("tempSaveLocation", ResourceLoader.class.getResource("/save").getFile().toString());
		properties.setProperty("mapsLocation", ResourceLoader.class.getResource("/core/maps").getFile().toString());
		properties.setProperty("saveLocation", ResourceLoader.class.getResource("/save").getFile().toString());
	}
	
	private static void loadUnitTestProperties() {
		logger.info("Loading Unit Test Properties");
		properties.setProperty("optionsLocation", ResourceLoader.class.getResource("/testres/options/options").getFile().toString());
		properties.setProperty("BaseDMGLocation", ResourceLoader.class.getResource("/testres/damagetables/BaseDMG.txt").getFile().toString());
		properties.setProperty("AltDMGLocation", ResourceLoader.class.getResource("/testres/damagetables/AltDMG.txt").getFile().toString());
		properties.setProperty("imagesLocation", ResourceLoader.class.getResource("/testres/images").getFile().toString());
		properties.setProperty("soundLocation", ResourceLoader.class.getResource("/testres/sound").getFile().toString());
		properties.setProperty("tempSaveLocation", ResourceLoader.class.getResource("/testres/save").getFile().toString());
		properties.setProperty("mapsLocation", ResourceLoader.class.getResource("/testres/maps").getFile().toString());
		properties.setProperty("saveLocation", ResourceLoader.class.getResource("/testres/save").getFile().toString());
	}
	
    

}
