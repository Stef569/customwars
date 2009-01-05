package com.customwars;

import org.slf4j.Logger;  
import org.slf4j.LoggerFactory; 

import com.customwars.state.TestFileSystemManager;
import com.customwars.state.TestNetworkingManager;
import com.customwars.state.TestResourceLoader;

import junit.framework.Test;
import junit.framework.TestSuite;

/**
 * 
 * This is the wrapper for all customwars Unit tests
 * The server side tests currently need a local webserver 
 * running with the server side code deployed at address:
 * http://localhost/customwars/cw/cw1/
 *
 */
public class AllTests {
	
	final static Logger logger = LoggerFactory.getLogger(AllTests.class);  
	
	public static Test suite() {
		logger.info("Running a Full Suite of Tests");

		TestSuite suite = new TestSuite("Running Test Suite: All Custom Wars Tests");
		suite.addTestSuite(TestFileSystemManager.class);
		suite.addTestSuite(TestResourceLoader.class);
		
		//Serverside
		//suite.addTestSuite(TestNetworkingManager.class);

		
		return new TestWideSetup(suite);
	}

}
