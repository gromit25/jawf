package com.jawf.parser;

import java.util.ArrayList;

/**
 * 
 * @author jmsohn
 */
public class TransferFunctionBuilder {
	
	private ArrayList<String[]> specs;
	
	/**
	 * 
	 */
	public TransferFunctionBuilder() {
		this.specs = new ArrayList<String[]>();
	}
	
	/**
	 * 
	 * @param pattern
	 * @param nextStatus
	 * @return
	 */
	public TransferFunctionBuilder add(String pattern, String nextStatus) throws Exception {
		
		String[] spec = new String[2];
		spec[0] = pattern;
		spec[1] = nextStatus;
		
		specs.add(spec);
		
		return this;
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<TransferFunction> build() throws Exception {
		
		ArrayList<TransferFunction> transferFunctions = new ArrayList<TransferFunction>();
		
		for(String[] spec: this.specs) {
			transferFunctions.add(new TransferFunction(spec[0], spec[1]));
		}
		
		
		return transferFunctions;
	}
}