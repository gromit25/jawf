package com.jawf.workflow.instructions;

/**
 * DIV 연산자 처리 클래스
 * 
 * @author jmsohn
 */
public class DIV extends BinaryInstruction {

	@Override
	public float executeBinaryOp4Float(float p1, float p2) throws Exception {
		
		if(p2 == 0) {
			throw new Exception("divide by zero.");
		}
		
		return p1/p2;
	}

	@Override
	public boolean executeBinaryOp4Boolean(boolean p1, boolean p2) throws Exception {
		throw new Exception("Unexpected type");
	}

	@Override
	public String executeBinaryOp4String(String p1, String p2) throws Exception {
		throw new Exception("Unexpected type");
	}

}
