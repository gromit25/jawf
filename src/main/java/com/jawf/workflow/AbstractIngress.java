package com.jawf.workflow;

import java.util.ArrayList;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import lombok.Getter;
import lombok.Setter;

/**
 * 메시지 수집 추상 컴포넌트
 * 
 * @author jmsohn
 */
public abstract class AbstractIngress extends AbstractComponent {
	
	/** 로깅 객체 */
	private static final Logger logger = LoggerFactory.getLogger(AbstractIngress.class);
	
	/** 출력 큐 */
	@Getter
	@Setter
	private PriorityBlockingQueue<Message> outqueue;
	
	/** 출력 큐 내에서 메시지 유지 시간(초) */
	@Getter
	@Setter
	private long outqueueTimeout; 
	
	/**
	 * 메시지(데이터) 수집 후 반환
	 * -> 수집을 더할 것이 없으면 null을 반환하여야 함
	 * 
	 * @return 수집된 메시지 목록
	 */
	protected abstract ArrayList<Message> acquisitMessages();
	
	/**
	 * 메시지 수집 컴포넌트 쓰레드 실행 메소드
	 */
	public void run() {
		
		// 시작시 호출
		logger.info("start:" + this.getName());
		this.init();
		
		// 수집된 메시지 목록 변수
		ArrayList<Message> messages = null;
		
		// EOD 메시지 수집 여부 변수
		boolean hasEOD = false;
		
		// 데이터 수집 및 출력 큐 전송
		do {
			
			try {
				
				// 데이터 수집
				messages = this.acquisitMessages();
				
				// 출력큐로 데이터 출력
				if(this.getOutqueue() != null && messages != null) {
					for(Message message: messages) {
						
						this.getOutqueue().offer(message, this.getOutqueueTimeout(), TimeUnit.SECONDS);
						
						if(message instanceof EODMessage) {
							hasEOD = true;
						}
						
					}
				}
				
			} catch(Exception ex) {
				logger.error(this.getName(), ex);
			}
			
		} while(hasEOD == false);
		
		// 종료시 호출
		logger.info("destroy:" + this.getName());
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
