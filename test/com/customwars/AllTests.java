package com.customwars;

import org.slf4j.Logger;  
import org.slf4j.LoggerFactory; 

import com.customwars.state.TestFileSystemManager;

import junit.framework.Test;
import junit.framework.TestSuite;

public class AllTests {
	
	final static Logger logger = LoggerFactory.getLogger(AllTests.class);  
	
	public static Test suite() {
		logger.info("Running a Full Suite of Tests");

		TestSuite suite = new TestSuite("Test for cwsource");
		suite.addTestSuite(TestFileSystemManager.class);
		
		return new TestWideSetup(suite);
	}

}
