package com.jawf.parser.attrscript;

import java.util.ArrayList;
import java.util.HashMap;

import com.jawf.parser.AbstractParser;
import com.jawf.parser.TransferFunction;
import com.jawf.parser.TransferFunctionBuilder;

public class NumberParser extends AbstractParser<Instruction> {
	
	enum Status {
		START
		, NUMBER
		, DOT
		, FLOATING_NUMBER
		, END
	}

	public NumberParser() throws Exception {
		super();
	}

	@Override
	protected String getStartStatus() {
		return Status.START.name();
	}

	@Override
	protected String[] getEndStatus() {
		return new String[] {
				Status.NUMBER.name(),
				Status.FLOATING_NUMBER.name(),
				Status.END.name()
		};
	}

	@Override
	protected HashMap<String, ArrayList<TransferFunction>> getTransferMap() throws Exception {
		
		HashMap<String, ArrayList<TransferFunction>> transferMap = new HashMap<String, ArrayList<TransferFunction>>();
		
		transferMap.put(Status.START.name(), new TransferFunctionBuilder()
				.add("0-9", Status.NUMBER.name())
				.build());
		
		transferMap.put(Status.NUMBER.name(), new TransferFunctionBuilder()
				.add("0-9", Status.NUMBER.name())
				.add(".", Status.DOT.name())
				.add("^0-9.", Status.END.name())
				.build());
		
		transferMap.put(Status.DOT.name(), new TransferFunctionBuilder()
				.add("0-9", Status.FLOATING_NUMBER.name())
				.build());
		
		transferMap.put(Status.FLOATING_NUMBER.name(), new TransferFunctionBuilder()
				.add("0-9", Status.FLOATING_NUMBER.name())
				.add("^0-9", Status.END.name())
				.build());
		
		return transferMap;
	}

}
