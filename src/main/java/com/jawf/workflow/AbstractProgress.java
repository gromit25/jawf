package com.jawf.workflow;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import lombok.Getter;
import lombok.Setter;

/**
 * 메시지 처리 추상 컴포넌트
 * -> 입력 큐는 필수적으로 설정되어야 하며,
 *    출력 큐는 설정되지 않을 경우 출력하지 않음
 * 
 * @author jmsohn
 */
public abstract class AbstractProgress extends AbstractComponent {
	
	/** 입력 큐 */
	@Getter
	@Setter
	private PriorityBlockingQueue<Message> inqueue;
	
	/** 입력 큐 메시지 입력 대기 시간(초) */
	@Getter
	@Setter
	private long inqueueTimeout;
	
	/** 출력 큐 */
	@Getter
	@Setter
	private PriorityBlockingQueue<Message> outqueue;
	
	/** 출력 큐 내에서 메시지 유지 시간(초) */
	@Getter
	@Setter
	private long outqueueTimeout;
	
	/**
	 * 메시지(데이터) 처리 후 반환
	 * -> 수집을 더할 것이 없으면 null을 반환하여야 함
	 * 
	 * @return 수집된 메시지 목록
	 */
	protected abstract ArrayList<Message> processMessages(Message message);

	/**
	 * 메시지 처리 컴포넌트 쓰레드 실행 메소드
	 */
	public void run() {
		
		// 시작시 호출
		this.init();
		
		// 수신된 메시지 변수
		Message in = null;
		
		// EOD 메시지 수집 여부 변수
		boolean hasEOD = false;
		
		do {
			
			// 입력큐로 부터 메시지 수신
			while(in == null) {
				try {
					in = this.getInqueue().poll(this.getInqueueTimeout(), TimeUnit.SECONDS);
				} catch(Exception ex) {
					//TODO 로깅
				}
			}
			
			try {
				
				// 수신된 메시지 처리 후 결과 수집
				ArrayList<Message> messages = this.processMessages(in);
				
				// 결과를 출력 큐로 전송
				if(this.getOutqueue() != null && messages != null) {
					for(Message message: messages) {
						
						this.getOutqueue().offer(message, this.getOutqueueTimeout(), TimeUnit.SECONDS);
						
						if(message instanceof EODMessage) {
							hasEOD = true;
						}
					}
				}
				
			} catch(Exception ex) {
				//TODO 로깅
			}
			
		} while(hasEOD == false);
		
		// 종료시 호출
		this.destroy();
	}
	
	/**
	 * 시작시 호출되는 메소드
	 */
	protected void init() {
		// do nothing
		// 상속받은 클래스에서 필요시 오버라이드하여 구현
	}
	
	/**
	 * 종료시 호출되는 메소드
	 */
	protected void destroy() {
		// do nothing
		// 상속받은 클래스에서 필요시 오버라이드하여 구현
	}

}
