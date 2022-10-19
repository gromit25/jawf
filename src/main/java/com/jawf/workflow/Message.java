package com.jawf.workflow;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author jmsohn
 */
public class Message {
	
	@Getter
	@Setter
	private int priority;
	
	/** */
	private HashMap<String, Class<?>> types;
	/** */
	private HashMap<String, Object> data;
	
	
	public <T> T get(String colName, Class<T> type) {
		return type.cast(this.getData().get(colName));
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
	private HashMap<String, Class<?>> getTypes() {
		
		if(this.types == null) {
			this.types = new HashMap<String, Class<?>>();
		}
		
		return this.types;
	}
	
	/**
	 * 
	 * @return
	 */
	private HashMap<String, Object> getData() {
		
		if(this.data == null) {
			this.data = new HashMap<String, Object>();
		}
		
		return this.data;
	}

}