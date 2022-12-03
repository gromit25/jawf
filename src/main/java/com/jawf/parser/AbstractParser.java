package com.jawf.parser;

import java.io.PushbackReader;
import java.io.StringReader;
import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * 문자열 파싱하는 추상 클래스
 * -> 상태 변환 내용 및 변환 이벤트 처리는 하위 클래스에서 처리함
 *    본 클래스에서는 문자열을 읽고,
 *    상태 변환 내용 및 상태 변환시 이벤트 핸들링 메소드를 호출함 
 * 
 * @author jmsohn
 */
public abstract class AbstractParser<T> {

	/** 파싱 상태 변수 */
	private String status;
	/** 현재 파싱 트리의 루트 노드 */
	private TreeNode<T> node;
	/** 상태 변환시, 수행되는 전이함수(transfer function) 목록 */
	private HashMap<String, HashMap<String, ArrayList<Method>>> transferHandlers;

	/**
	 * 생성자
	 */
	public AbstractParser() throws Exception {
		
		// 파싱 트리 노드 생성
		this.node = new TreeNode<T>();
		
		// transfer event handler 목록 생성
		this.transferHandlers = new HashMap<String, HashMap<String, ArrayList<Method>>>();
		
		// 하위 클래스에서 구현된 transfer event handler 메소드를 목록에 등록함
		
		// 현재 클래스의 메소드 목록을 가져옴
		Method[] methods = this.getClass().getMethods();
		
		// 각 메소드 별로 TransferEventHandler Annotation이 있는지 확인하여 등록함
		// 
		for(Method method: methods) {
			
			// 메소드의 TransferEventHandler Annotation 정보를 가져옴
			// 없을 경우, 다음 메소드 검사
			TransferEventHandler handlerAnnotation = method.getAnnotation(TransferEventHandler.class);
			if(handlerAnnotation == null) {
				continue;
			}
			
			// 전이 시작 상태 목록 변수
			String[] sources = handlerAnnotation.source();
			// 전이 종료 상태 목록 변수
			String[] targets = handlerAnnotation.target();
			
			// 전이 시작 및 종료 상태에 따른 TransferEventHandler 등록
			// "시작" -> "종료" , method1 형태로 저장 
			for(String source: sources) {
			
				// 시작 상태가 등록되어 있지 않으면, 등록 수행
				if(this.transferHandlers.containsKey(source) == false) {
					this.transferHandlers.put(source, new HashMap<String, ArrayList<Method>>());
				}
				
				HashMap<String, ArrayList<Method>> sourceMap = this.transferHandlers.get(source);
				
				// 종료 상태와 TransferEventHandler 메소드를 등록함
				for(String target: targets) {
					
					if(sourceMap.containsKey(target) == false) {
						sourceMap.put(target, new ArrayList<Method>());
					}
					
					ArrayList<Method> handlers = sourceMap.get(target);
					
					// TransferEventHandler의 파라미터 개수는 1개여야 함
					if(method.getParameterCount() != 1) {
						throw new Exception("parameter count is not 1");
					}
					
					Parameter[] params = method.getParameters();
					if(params[0].getType() != Event.class) {
						throw new Exception("parameter is not Event type:" + params[0].getType());
					}
					
					handlers.add(method);
					
				} // End of for targets
				
			} // End of for sources
			
		} // End of for methods
	}
	
	/**
	 * 시작 상태 반환
	 * 
	 * @return 시작 상태
	 */
	protected abstract String getStartStatus();
	
	/**
	 * 종료 상태 목록 반환
	 * 
	 * @return 종료 상태 목록
	 */
	protected abstract String[] getEndStatus();
	
	/**
	 * 상태 변환 정보 반환
	 * -> ex) "A" 상태에서 문자 "B"가 들어오면 "C" 상태로 변한다는 정보
	 * 
	 * @return 상태 변환 정보
	 */
	protected abstract HashMap<String, ArrayList<Transfer>> getTransferMap() throws Exception;
	
	/**
	 * 파싱 시작시 콜백
	 * -> 파싱 시작과 동시에 다른 파서를 호출할 때 주로 사용됨
	 */
	protected void init() throws Exception {
		// Do Nothing
		// 하위 클래스에서 필요시 구현
	}
	
	/**
	 * 파싱 종료시 콜백
	 */
	protected void exit() throws Exception {
		// Do Nothing
		// 하위 클래스에서 필요시 구현
	}
	
	/**
	 * 종료 문자 발생시 콜백
	 * -> 후처리 및 적절한 상태에서 종료되었는지 확인
	 */
	protected void processEod() throws Exception {
		// Do Nothing
		// 하위 클래스에서 필요시 구현
	}
	
	/**
	 * 주어진 시작 상태와 종료 상태에 수행되어야 하는 TransferEventHandler 메소드 목록을 반환
	 * 
	 * @param source 시작 상태
	 * @param target 종료 상태
	 * @return TransferEventHandler 메소드 목록
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
	 * 문자열(script)를 입력받아 파싱 수행 후 루트 노드 반환 
	 * 
	 * @param script 파싱할 문자열
	 * @return 파싱 트리의 루트 노드
	 */
	public TreeNode<T> parse(String script) throws Exception {
		
		PushbackReader in = new PushbackReader(new StringReader(script));
		return this.parse(in);
		
	}
	
	/**
	 * 문장을 한 글자씩 파싱한 후 파싱 트리의 루트 노드를 반환
	 * 
	 * @param in 파싱할 문장의 Reader
	 * @return 파싱 트리의 루트 노드
	 */
	public TreeNode<T> parse(PushbackReader in) throws Exception {
		
		// 최초 시작시 실행
		this.init();
		
		// 시작 상태로 상태 초기화
		this.status = this.getStartStatus();
		
		// Reader에서 한문자씩 읽어들여 상태를 전환하고,
		// 각 상태 전환에 따른 전이함수(transfer function)을 실행시킴 
		int read = in.read();
		
		while(read != -1) {
			
			// 입력 문자 변수
			char ch = (char)read;
			
			// 시작 상태가 전이함수 목록에 없는 경우 예외 발생 
			if(this.getTransferMap().containsKey(this.status) == false) {
				throw new Exception("invalid status: " + this.status);
			}
			
			// 유효한 전이함수(현재 상태에서 입력 문자가 있는 경우)가 있는지 여부 변수
			boolean isMatched = false;
			
			// 전이 함수 목록에서 유효한 전이 함수가 있는지 확인함
			// 유효한 전이 함수에 따라 상태 변화 후 상태 변화에 따른 TransferEventHandler 메소드를 수행함
			for(Transfer transferFunction: this.getTransferMap().get(this.status)) {
				if(transferFunction.isValid(ch) == true) {
					
					// 유효한 전이함수(transfer function)이 매치되었을 경우 true로 설정함
					isMatched = true;
					
					// 이벤트 생성
					Event event = new Event(ch, in);
					
					// 이벤트 처리함수 호출
					String nextStatus = transferFunction.getNextStatus();
					ArrayList<Method> handlers = this.getHandlers(this.status, nextStatus);
					for(Method handler: handlers) {
						handler.invoke(this, event);
					}
					
					// 다음 상태로 상태를 변경
					this.status = nextStatus;
					
					// for문 종료 -> 다른 전이함수는 검사하지 않음
					break;
				}
			}
			
			// 매치되는 전이함수가 없을 경우 예외 발생
			if(isMatched == false) {
				throw new Exception("Unexpected char: " + ch + ", status:" + this.status);
			}
			
			// 다음 글자를 읽어옴
			read = in.read();
			
		} // End of while
		
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
		
		// 파싱 종료 시 호출
		this.exit();
		
		// 생성된 파싱 트리를 반환
		return this.node;
		
	} // End of parse
	
	/**
	 * 현재 노드에 데이터를 설정함
	 * 
	 * @param data 설정할 데이터
	 */
	protected void setNodeData(T data) {
		this.node.setData(data);
	}
	
	/**
	 * 현재 노드에 자식 노드를 추가함
	 * 
	 * @param data
	 */
	protected void addChild(T data) {
		this.node.addChild(new TreeNode<T>(data));
	}
	
	/**
	 * 상태 전환시 발생 Event 클래스
	 * 
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
