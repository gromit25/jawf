package com.jawf.parser;

import java.util.ArrayList;

/**
 * 전이함수 생성 클래스
 * 
 * @author jmsohn
 */
public class TransferBuilder {
	
	/** */
	private ArrayList<String[]> specs;
	
	/**
	 * 생성자
	 */
	public TransferBuilder() {
		this.specs = new ArrayList<String[]>();
	}
	
	/**
	 * 
	 * @param pattern
	 * @param nextStatus
	 * @return
	 */
	public TransferBuilder add(String pattern, String nextStatus) throws Exception {
		
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
	public ArrayList<Transfer> build() throws Exception {
		
		ArrayList<Transfer> transferFunctions = new ArrayList<Transfer>();
		
		for(String[] spec: this.specs) {
			transferFunctions.add(new Transfer(spec[0], spec[1]));
		}
		
		return transferFunctions;
	}
}