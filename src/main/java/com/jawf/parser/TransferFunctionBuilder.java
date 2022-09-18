package com.jawf.parser;

import java.util.ArrayList;

public class TransferFunctionBuilder {
	
	private ArrayList<String[]> specs;
	
	public TransferFunctionBuilder() {
		this.specs = new ArrayList<String[]>();
	}
	
	public TransferFunctionBuilder add(String pattern, String nextStatus) throws Exception {
		
		String[] spec = new String[2];
		spec[0] = pattern;
		spec[1] = nextStatus;
		
		specs.add(spec);
		
		return this;
	}
	
	public ArrayList<TransferFunction> build() throws Exception {
		
		ArrayList<TransferFunction> transferFunctions = new ArrayList<TransferFunction>();
		
		for(String[] spec: this.specs) {
			transferFunctions.add(new TransferFunction(spec[0], spec[1]));
		}
		
		
		return transferFunctions;
	}
}