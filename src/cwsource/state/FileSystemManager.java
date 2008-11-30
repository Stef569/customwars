package cwsource.state;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import cwsource.AllTests;
import cwsource.ResourceLoader;

public class FileSystemManager {
	
	final static Logger logger = LoggerFactory.getLogger(AllTests.class);  
	
	private static final String DIR_IGNORE_FILTER = ".";

	public static ArrayList<String> getMapCatagories() {
		File mapDir = null;
		ArrayList<String> directories = null; 
		
		FilenameFilter filter = new FilenameFilter() {
	        public boolean accept(File dir, String name) {
	            return !name.startsWith(DIR_IGNORE_FILTER);
	        }
	    };
	    
		mapDir = new File((String)ResourceLoader.properties.get("mapsLocation"));
		
		if (mapDir !=null){
			String[] list = mapDir.list(filter);
			List<String> asList = Arrays.asList(list);
			directories = new ArrayList<String>(asList);
			
			if (directories.isEmpty() && directories !=null) {
				logger.error("Problem reading from MapLocation: [" + mapDir + " ]");
				return null;
			}
			
		}
		
	    
	    return directories;
	}
	
	protected static boolean deleteDir(String dirName) {
		
		File dir = new File(dirName);
		
		if (dir.isDirectory()) {
			String[] children = dir.list();
			for (int i=0; i<children.length; i++) {
				File file = new File(dir, children[i]);
				boolean success = deleteDir(file.getAbsolutePath());
				if (!success) {
					return false;
				}
			}
		}
		
        return dir.delete();
	}
	
	protected static void createDir(String dirs) {
		boolean success = (new File(dirs)).mkdirs();
	    if (!success) {
			logger.error("Could not create a directory: [ " + dirs +" ]");
	    }
	}


}
