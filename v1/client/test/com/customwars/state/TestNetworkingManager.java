package com.customwars.state;

import java.io.IOException;
import java.net.MalformedURLException;

import junit.framework.TestCase;


public class TestNetworkingManager extends TestCase{
	
	String command;
	String expectedResult;
	String extras;
	NetworkingManager networkingManager;
	
	@Override
	protected void setUp() throws Exception {
		networkingManager = new NetworkingManager();
	}
	
	public void testTryToConnect() throws MalformedURLException, IOException {
		assertTrue(networkingManager.tryToConnect());
	}
	
	public void testConnectionTestThroughSendCommandToMain() throws MalformedURLException, IOException {
		command = "test";
		expectedResult = "success";
		extras = "";
		assertEquals(expectedResult, networkingManager.sendCommandToMain(command, extras));
	}
	
//	public void testIfGameExistsThroughSendCommandToMain() throws MalformedURLException, IOException {
//		command = "qname";
//		expectedResult = "no";
//		extras = "anyStringReturnsYes";
//		assertEquals(expectedResult, networkingManager.sendCommandToMain(command, extras));
//	}
	
//	public void testGetChatLogThroughSendCommandToMain() throws MalformedURLException, IOException  {
//		command = "getchat";
//		expectedResult = "no";
//		extras = "anyStringReturnsYes";
//		assertEquals(expectedResult, networkingManager.sendCommandToMain(command, extras));
//	}
//	
//	public void testGetSysLogThroughSendCommandToMain() throws MalformedURLException, IOException  {
//		command = "getsys";
//		expectedResult = "no";
//		extras = "anyStringReturnsYes";
//		assertEquals(expectedResult, networkingManager.sendCommandToMain(command, extras));
//	}
	
	
	
	
}
