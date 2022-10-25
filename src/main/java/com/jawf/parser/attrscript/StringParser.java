package com.jawf.parser.attrscript;

import java.util.ArrayList;
import java.util.HashMap;

import com.jawf.parser.AbstractParser;
import com.jawf.parser.TransferEventHandler;
import com.jawf.parser.Transfer;
import com.jawf.parser.TransferFunctionBuilder;

public class StringParser extends AbstractParser<Instruction> {
	
	private StringBuffer buffer;

	/**
	 * 생성자
	 */
	public StringParser() throws Exception {
		super();
		this.buffer = new StringBuffer("");
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected String[] getEndStatus() {
		return new String[] {"END"};
	}

	@Override
	protected HashMap<String, ArrayList<Transfer>> getTransferMap() throws Exception {
		
		HashMap<String, ArrayList<Transfer>> transferMap = new HashMap<String, ArrayList<Transfer>>();
		
		transferMap.put("START", new TransferFunctionBuilder()
				.add("\"", "IN_STR")
				.add("^\"", "FAIL")
				.build());
		
		transferMap.put("IN_STR", new TransferFunctionBuilder()
				.add("\\\\", "ESCAPE")
				.add("\"", "END")
				.add("^\\\\\"", "IN_STR")
				.build());
		
		transferMap.put("ESCAPE", new TransferFunctionBuilder()
				.add(".", "IN_STR")
				.build());
		
		return transferMap;
	}
	
	@TransferEventHandler(source="IN_STR", target="IN_STR")
	public void handleString(Event event) {
		this.buffer.append(event.getChar());
	}
	
	@TransferEventHandler(source="ESCAPE", target="IN_STR")
	public void handleEscape(Event event) {
		
		switch(event.getChar()) {
		case 'n':
			this.buffer.append('\n');
			break;
		case 't':
			this.buffer.append('\t');
			break;
		default:
			this.buffer.append(event.getChar());
			break;
		}
	}
	
	public String getString() {
		return this.buffer.toString();
	}

}
