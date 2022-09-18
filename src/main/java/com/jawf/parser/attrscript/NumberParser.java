package com.jawf.parser.attrscript;

import java.util.ArrayList;
import java.util.HashMap;

import com.jawf.parser.AbstractParser;
import com.jawf.parser.TransferEventHandler;
import com.jawf.parser.TransferFunction;
import com.jawf.parser.TransferFunctionBuilder;

import lombok.Getter;

/**
 * 
 * @author jmsohn
 */
public class NumberParser extends AbstractParser<Instruction> {
	
	/** */
	@Getter
	private float number;
	
	/** */
	private StringBuffer buffer;

	/**
	 * 
	 */
	public NumberParser() throws Exception {
		super();
		this.buffer = new StringBuffer();
	}

	@Override
	protected String getStartStatus() {
		return "START";
	}

	@Override
	protected String[] getEndStatus() {
		return new String[] {
				"NUMBER",
				"FLOATING_NUMBER",
				"END"
		};
	}

	@Override
	protected HashMap<String, ArrayList<TransferFunction>> getTransferMap() throws Exception {
		
		HashMap<String, ArrayList<TransferFunction>> transferMap = new HashMap<String, ArrayList<TransferFunction>>();
		
		transferMap.put("START", new TransferFunctionBuilder()
				.add("0-9", "NUMBER")
				.build());
		
		transferMap.put("NUMBER", new TransferFunctionBuilder()
				.add("0-9", "NUMBER")
				.add(".", "DOT")
				.add("^0-9.", "END")
				.build());
		
		transferMap.put("DOT", new TransferFunctionBuilder()
				.add("0-9", "FLOATING_NUMBER")
				.build());
		
		transferMap.put("FLOATING_NUMBER", new TransferFunctionBuilder()
				.add("0-9", "FLOATING_NUMBER")
				.add("^0-9", "END")
				.build());
		
		return transferMap;
	}
	
	@TransferEventHandler(source={"START", "NUMBER"}, target="NUMBER")
	private void handleNumber(Event event) {
		this.buffer.append(event.getChar());
	}
	
	@TransferEventHandler(source="NUMBER", target="DOT")
	private void handleDot(Event event) {
		this.buffer.append(event.getChar());
	}
	
	@TransferEventHandler(source={"DOT", "FLOATING_NUMBER"}, target="FLOATING_NUMBER")
	private void handleFloatingNumber(Event event) {
		this.buffer.append(event.getChar());
	}
	
	protected void processEod() throws Exception {
		this.number = Float.parseFloat(this.buffer.toString());
	}

}
