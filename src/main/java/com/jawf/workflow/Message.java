package com.jawf.workflow;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

import lombok.Getter;
import lombok.Setter;

/**
 * 메시지 클래스
 * 워크플로우에서 데이터 전달을 위한 메시지를 표현함
 * 
 * @author jmsohn
 */
public class Message {
	
	/**
	 * 메시지 처리 중요도
	 */
	@Getter
	@Setter
	private int priority;
	
	/** 컬럼명 목록 */
	private ArrayList<String> colNames;
	/**
	 * 메시지 내 컬럼별 데이터의 타입(클래스)
	 * 컬럼의 타입은 Serializable을 구현한 클래스이어야 함
	 * 이는 네트워크 통신이 발생하게 될 경우,
	 * 데이터의 클래스가 Serializable이 아닐 경우 통신이 곤란함
	 */
	private HashMap<String, Class<? extends Serializable>> types;
	/** null을 허용하지 않는 컬럼 목록 */
	private Set<String> mandatoryCols;
	
	/** 메시지 내 컬럼별 데이터 */
	private HashMap<String, Serializable> data;
	
	/**
	 * 메시지의 컬럼 정보를 추가
	 * 
	 * @param colName 컬럼명
	 * @param isMandatory null 허용 여부(true 이면 null 허용하지 않음, false 이면 null 허용함)
	 * @param type 컬럼의 타입
	 * @return 현재 객체(fluent coding 용)
	 */
	public Message addColumn(String colName, boolean isMandatory, Class<? extends Serializable> type) throws Exception {
		
		// 컬럼명 검사
		if(colName == null) {
			throw new Exception("column name is null.");
		}
		
		// 컬럼 타입 검사
		if(type == null) {
			throw new Exception("type is null.");
		}
		
		// 컬럼 정보 추가
		this.getColumnNames().add(colName);
		this.getTypes().put(colName, type);
		
		if(isMandatory == true) {
			this.getMandatoryCols().add(colName);
		}
		
		return this;
	}
	
	/**
	 * 메시지의 컬럼 정보를 추가
	 * null 허용 여부(isMandatory)는 false(null 허용함)로 설정
	 * 
	 * @param colName 컬럼명
	 * @param type 컬럼의 타입
	 * @return 현재 객체(fluent coding 용)
	 */
	public Message addColumn(String colName, Class<? extends Serializable> type) throws Exception {
		return this.addColumn(colName, false, type);
	}
	
	/**
	 * 컬럼에 데이터 설정
	 * 
	 * @param colName 데이터를 설정할 컬럼명
	 * @param data 설정할 데이터
	 * @return 현재 객체(fluent coding 용)
	 */
	public Message set(String colName, Serializable data) throws Exception {
		
		// 컬럼명이 있는지 검사
		if(this.getTypes().containsKey(colName) == false) {
			throw new Exception("column is not found: " + colName);
		}
		
		// 컬럼이 null을 허용하지 않는 경우, 데이터가 null인지 검사
		if(this.getMandatoryCols().contains(colName) == true && data == null) {
			throw new Exception(colName + " is mandatory but data is null.");
		}

		// 데이터의 타입이 컬럼의 타입에 적합한지 검사
		if(data != null) {
			if(this.getTypes().get(colName).isAssignableFrom(data.getClass()) == false) {
				throw new Exception("column type is not matched(necessary:" + this.getTypes().get(colName).getClass() + ", requested:" + data.getClass());
			}
		}
		
		// 데이터를 설정함
		this.getData().put(colName, data);
		
		return this;
	}

	/**
	 * 컬럼의 데이터 반환
	 * 
	 * @param colName 컬럼명
	 * @return 컬럼의 데이터
	 */
	public Serializable get(String colName) throws Exception {
		
		if(this.getData().containsKey(colName) == false) {
			throw new Exception("Column is not found: " + colName);
		}
		
		return this.getData().get(colName);
	}
	
	/**
	 * 
	 * 
	 * @param colName
	 * @param type
	 * @return
	 */
	public <T> T get(String colName, Class<T> type) throws Exception {
		
		Class<?> colType = this.getTypes().get(colName);
		if(type.isAssignableFrom(colType) == false) {
			throw new Exception("Unexpected type(necessary: " + colType + ", requested" + type);
		}
		
		return type.cast(this.get(colName));
	}
	
	/**
	 * 
	 * @param colName
	 * @return
	 */
	public String getString(String colName) throws Exception {
		
		if(this.getData().containsKey(colName) == false) {
			throw new Exception("Column is not found: " + colName);
		}
		
		return this.getData().get(colName).toString();
	}
	
	/**
	 * 
	 * @return
	 */
	private ArrayList<String> getColumnNames() {
		
		if(this.colNames == null) {
			this.colNames = new ArrayList<String>();
		}
		
		return this.colNames;
	}
	
	/**
	 * 
	 * @return
	 */
	private HashMap<String, Class<? extends Serializable>> getTypes() {
		
		if(this.types == null) {
			this.types = new HashMap<String, Class<? extends Serializable>>();
		}
		
		return this.types;
	}
	
	/**
	 * 
	 * @return
	 */
	private Set<String> getMandatoryCols() {
		
		if(this.mandatoryCols == null) {
			this.mandatoryCols = new HashSet<String>();
		}
		
		return this.mandatoryCols;
	}
	
	/**
	 * 
	 * @return
	 */
	private HashMap<String, Serializable> getData() {
		
		if(this.data == null) {
			this.data = new HashMap<String, Serializable>();
		}
		
		return this.data;
	}
	
	/**
	 * 
	 */
	public String toString() {
		
		StringBuffer buffer = new StringBuffer();
		
		// 컬럼명 출력
		for(String colName: this.getColumnNames()) {
			
		}
		
		// 데이터 출력
		
		return buffer.toString();
	}

}
