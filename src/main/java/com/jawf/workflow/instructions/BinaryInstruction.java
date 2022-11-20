package com.jawf.workflow.instructions;

import java.util.HashMap;
import java.util.Stack;

/**
 * 이항 연산자 명령어 처리를 위한 추상클래스 
 * 
 * @author jmsohn
 */
public abstract class BinaryInstruction extends Instruction {
	
	/**
	 * 숫자 연산
	 * @param p1 첫번째 파라미터
	 * @param p2 두번째 파라미터
	 * @return p1, p2의 연산 결과
	 */
	public abstract float executeBinaryOp4Float(float p1, float p2) throws Exception;
	
	/**
	 * boolean 연산
	 * @param p1 첫번째 파라미터
	 * @param p2 두번째 파라미터
	 * @return p1, p2의 연산 결과
	 */
	public abstract boolean executeBinaryOp4Boolean(boolean p1, boolean p2) throws Exception;
	
	/**
	 * 문자열 연산
	 * @param p1 첫번째 파라미터
	 * @param p2 두번째 파라미터
	 * @return p1, p2의 연산 결과
	 */
	public abstract String executeBinaryOp4String(String p1, String p2) throws Exception;

	@Override
	public void execute(Stack<Object> stack, HashMap<String, Object> values) throws Exception {
		
		// 스택에서 연산할 데이터를 가져옴
		Object p2 = stack.pop();
		Object p1 = stack.pop();
		
		if(p1 == null) {
			throw new Exception("parameter 1 is null");
		}
		
		if(p2 == null) {
			throw new Exception("parameter 2 is null");
		}
		
		// p1, p2의 타입에 따라 연산 호출
		if(p1 instanceof String || p2 instanceof String) {
			stack.push(this.executeBinaryOp4String(p1.toString(), p2.toString()));
			return;
		} else if(p1 instanceof Float && p2 instanceof Float) {
			stack.push(this.executeBinaryOp4Float((float)p1, (float)p2));
			return;
		} else if(p1 instanceof Boolean && p2 instanceof Boolean) {
			stack.push(this.executeBinaryOp4Boolean((boolean)p1, (boolean)p2));
			return;
		} else {
			throw new Exception("Unexpected data type(" + p1.getClass() + ", " + p2.getClass());
		}
		
	}

}
