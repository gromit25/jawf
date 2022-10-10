package com.jawf.parser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * 
 * @author jmsohn
 */
public class TransferFunctionTest extends TestCase {
	
    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
    public TransferFunctionTest(String testName) {
        super(testName);
    }
    
    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(TransferFunctionTest.class);
    }
    
    /**
     * 
     */
    public void testOne() {
    	
    	try {
    		
    		TransferFunction tfunc = new TransferFunction("a", "");
    		assertTrue(tfunc.isValid('a'));
    		assertFalse(tfunc.isValid('b'));
    		
    	} catch(Exception ex) {
    		
    		ex.printStackTrace();
    		assertTrue(false);
    		
    	}
    	
    }
    
    public void testTwo() {
    	
    	try {
    		
    		TransferFunction tfunc = new TransferFunction(".", "");
    		assertTrue(tfunc.isValid('a'));
    		assertTrue(tfunc.isValid('b'));
    		assertTrue(tfunc.isValid('#'));
    		assertTrue(tfunc.isValid('/'));
    		
    	} catch(Exception ex) {
    		
    		ex.printStackTrace();
    		assertTrue(false);
    		
    	}
    	
    }
    
    public void testThree() {
    	
    	try {
    		
    		TransferFunction tfunc = new TransferFunction("#a-z", "");
    		assertTrue(tfunc.isValid('a'));
    		assertFalse(tfunc.isValid('A'));
    		
    	} catch(Exception ex) {
    		
    		ex.printStackTrace();
    		assertTrue(false);
    		
    	}
    	
    }
}
