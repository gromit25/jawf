package com.jawf.workflow.instructions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

/**
 * 명령어 추상 클래스
 * 
 * @author jmsohn
 */
public abstract class Instruction {
	
	/** 명령어의 파라미터 목록 */
	private ArrayList<String> params = new ArrayList<String>(); 
	
	/**
	 * 명령어 수행 메소드
	 * @param stack 스택
	 * @param values 메모리
	 */
	public abstract void execute(Stack<Object> stack, HashMap<String, Object> values) throws Exception;
	
	/**
	 * 파라미터의 개수 반환
	 * @return 파라미터의 개수
	 */
	public int getParamCount() {
		return this.params.size();
	}
	
	/**
	 * 특정 인덱스의 파라미터를 반환
	 * @param index 반환할 특정 인덱스
	 * @return 특정 인덱스의 파라미터
	 */
	public String getParam(int index) throws Exception {
		return this.params.get(index);
	}
	
	/**
	 * 연산자 파라미터 추가
	 * @param param 추가할 파라미터 
	 */
	public void addParam(String param) {
		this.params.add(param);
	}

}
