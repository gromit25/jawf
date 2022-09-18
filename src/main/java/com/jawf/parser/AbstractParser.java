package com.jawf.parser;

import java.io.PushbackReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 
 * @author jmsohn
 */
public abstract class AbstractParser<T> {
	
	/** */
	private TreeNode<T> node;
	/** */
	private HashMap<String, HashMap<String, ArrayList<Method>>> transferHandlers;

	/**
	 * 생성자
	 */
	public AbstractParser() throws Exception {
		
		// 
		this.node = new TreeNode<T>();
		
		// transfer event handler 목록 생성
		this.transferHandlers = new HashMap<String, HashMap<String, ArrayList<Method>>>();
		
		// 하위 클래스에서 구현된 transfer event handler 메소드를 목록에 등록함
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
		// 하위 클래스에서 필요시 구현
	}
	
	/**
	 * 
	 */
	protected void processEod() {
		// Do Nothing
		// 하위 클래스에서 필요시 구현
	}
	
	/**
	 * 
	 * @param source
	 * @param target
	 * @return
	 */
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
	protected TreeNode<T> parse(PushbackReader in) throws Exception {
		
		// 최초 시작시 실행
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
		
		// End of data 발생시, 처리 수행
		this.processEod();
		
		// 종료 상태 여부 확인
		// 종료 상태가 아닌 경우, 예외 발생
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
		
		// 생성된 파싱 트리를 반환
		return this.node;
		
	} // End of parse
	
	/**
	 * 
	 * @param data
	 */
	protected void setNodeData(T data) {
		this.node.setData(data);
	}
	
	/**
	 * 
	 * @param data
	 */
	protected void addChild(T data) {
		this.node.addChild(new TreeNode<T>(data));
	}
	
	/**
	 * 상태 전환시 발생 Event 클래스
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
