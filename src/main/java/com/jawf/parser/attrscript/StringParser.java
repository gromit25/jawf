package com.jawf.parser.attrscript;

import java.util.ArrayList;
import java.util.HashMap;

import com.jawf.parser.AbstractParser;
import com.jawf.parser.TransferFunction;
import com.jawf.parser.TransferFunctionBuilder;

public class StringParser extends AbstractParser<Instruction> {

	public StringParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected String[] getEndStatus() {
		return new String[] {};
	}

	@Override
	protected HashMap<String, ArrayList<TransferFunction>> getTransferMap() throws Exception {
		
		HashMap<String, ArrayList<TransferFunction>> transferMap = new HashMap<String, ArrayList<TransferFunction>>();
		
		transferMap.put("START", new TransferFunctionBuilder()
				.add("\"", "IN_STR")
				.build());
		
		transferMap.put("IN_STR", new TransferFunctionBuilder()
				.add("\\", "ESCAPE")
				.add("\"", "END")
				.add("^\"\\", "IN_STR")
				.build());
		
		transferMap.put("ESCAPE", new TransferFunctionBuilder()
				.add(".", "IN_STR")
				.build());
		
		return transferMap;
	}

}
