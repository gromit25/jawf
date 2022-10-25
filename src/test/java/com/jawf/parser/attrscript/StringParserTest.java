package com.jawf.parser.attrscript;

import com.jawf.parser.script.StringParser;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class StringParserTest extends TestCase {

    /**
     * Create the test case
     *
     * @param testName name of the test case
     */
	public StringParserTest(String name) {
		super(name);
	}
	
    /**
     * @return the suite of tests being tested
     */
    public static Test suite() {
        return new TestSuite(StringParserTest.class);
    }
    
    public void test1() {
    	
    	try {
    		
	    	String test = "\"hello world\"";
	    	StringParser parser = new StringParser();
	    	
	    	parser.parse(test);
	    	assertEquals(parser.getString(), "hello world");
	    	
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}
    }
    
    public void test2() {
    	
    	try {
    		
	    	String test = "\"a\\nb\\t\\\\\"";
	    	StringParser parser = new StringParser();
	    	
	    	parser.parse(test);
	    	System.out.println(parser.getString());
	    	
    	} catch(Exception ex) {
    		ex.printStackTrace();
    		assertTrue(false);
    	}
    }

}
