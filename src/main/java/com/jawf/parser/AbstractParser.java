package com.jawf.parser;

import java.io.PushbackReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;

public abstract class AbstractParser {
	
	private HashMap<String, HashMap<String, ArrayList<Method>>> transferHandlers;

	/**
	 * 생성자
	 * 
	 */
	public AbstractParser() throws Exception {
		
		this.transferHandlers = new HashMap<String, HashMap<String, ArrayList<Method>>>();
		
		for(Method method: this.getClass().getMethods()) {
			
			TransferEventHandler handlerAnnotation = method.getAnnotation(TransferEventHandler.class);
			if(handlerAnnotation == null) {
				continue;
			}
			
			String[] sources = handlerAnnotation.source();
			String[] targets = handlerAnnotation.target();
			
			for(String source: sources) {
			
				//
				if(this.transferHandlers.containsKey(source) == false) {
					this.transferHandlers.put(source, new HashMap<String, ArrayList<Method>>());
				}
				
				HashMap<String, ArrayList<Method>> sourceMap = this.transferHandlers.get(source);
				
				//
				for(String target: targets) {
					
					if(sourceMap.containsKey(target) == false) {
						sourceMap.put(target, new ArrayList<Method>());
					}
					
					ArrayList<Method> handlers = sourceMap.get(target);
					
					//
					if(method.getParameterCount() != 1) {
						throw new Exception("parameter count is not 1");
					}
					
					Parameter[] params = method.getParameters();
					if(params[0].getType() != Event.class) {
						throw new Exception("parameter is not Event type:" + params[0].getType());
					}
					
					//
					handlers.add(method);
				}
			}
		}
	}
	
	/**
	 * 시작 상태 반환
	 * @return
	 */
	protected abstract String getStartStatus();
	
	/**
	 * 종료 상태 목록 반환
	 * @return
	 */
	protected abstract String[] getEndStatus();
	
	/**
	 * 
	 * @return
	 */
	protected abstract HashMap<String, ArrayList<TransferFunction>> getTransferMap() throws Exception;
	
	/**
	 * 
	 */
	protected void init() {
		// Do Nothing
	}
	
	/**
	 * 
	 */
	protected void processEod() {
		// Do Nothing
	}
	
	private ArrayList<Method> getHandlers(String source, String target) throws Exception {
		
		// 핸들러 목록에 소스가 없는 경우, 빈 array 반환
		if(this.transferHandlers.containsKey(source) == false) {
			return new ArrayList<Method>();
		}
		
		HashMap<String, ArrayList<Method>> sourceMap = this.transferHandlers.get(source);
		
		// 소스 핸들러 목록에 타깃이 없는 경우, 빈 array 반환
		if(sourceMap.containsKey(target) == false) {
			return new ArrayList<Method>();
		}
		
		return sourceMap.get(target);
	}
	
	/**
	 * 
	 * @param in
	 */
	protected void _parse(PushbackReader in) throws Exception {
		
		//
		this.init();
		
		//
		int read = in.read();
		String status = this.getStartStatus();
		
		//
		//
		while(read != -1) {
			
			char ch = (char)read;
			
			//
			if(this.getTransferMap().containsKey(status) == false) {
				throw new Exception("invalid status: " + status);
			}
			
			//
			for(TransferFunction transferFunction: this.getTransferMap().get(status)) {
				if(transferFunction.isValid(ch) == true) {
					
					// 이벤트 생성
					Event event = new Event(ch, in); 
					
					// 이벤트 처리함수 호출
					String nextStatus = transferFunction.getNextStatus();
					ArrayList<Method> handlers = this.getHandlers(status, nextStatus);
					for(Method handler: handlers) {
						handler.invoke(this, event);
					}
					
					status = nextStatus;
					break;
				}
			}
			
			//
			read = in.read();
		} // End of while
		
		this.processEod();
		
		//
		boolean isEndStatus = false;
		for(String endStatus: this.getEndStatus()) {
			if(status.equals(endStatus) == true) {
				isEndStatus = true;
				break;
			}
		}
		
		if(isEndStatus == false) {
			throw new Exception("Unexpected end status:" + status);
		}
		
	} // End of parser
	
	/**
	 * 상태 전환시 
	 * @author jmsohn
	 */
	protected static class Event {
		
		private char ch;
		private PushbackReader in;
		
		public Event(char ch, PushbackReader in) {
			this.ch = ch;
			this.in = in;
		}
		
		public char getChar() {
			return this.ch;
		}
		
		public PushbackReader getReader() {
			return this.in;
		}
	}
}
