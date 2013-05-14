/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.server.easyprinter;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author victor
 */
public class ServerEasyPrinterTest {
	
	public ServerEasyPrinterTest() {
	}
	
	@BeforeClass
	public static void setUpClass() {
	}
	
	@AfterClass
	public static void tearDownClass() {
	}
	
	@Before
	public void setUp() {
	}
	
	@After
	public void tearDown() {
	}

	/**
	 * Test of main method, of class ServerEasyPrinter.
	 */
	@Test
	public void testMain() {
		System.out.println("main");
		String[] args = null;
		ServerEasyPrinter.main(args);
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}

	/**
	 * Test of buildGUI method, of class ServerEasyPrinter.
	 */
	@Test
	public void testBuildGUI() {
		System.out.println("buildGUI");
		ServerEasyPrinter.buildGUI();
		// TODO review the generated test code and remove the default call to fail.
		fail("The test case is a prototype.");
	}
}