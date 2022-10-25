package com.jawf.workflow;

import java.io.Serializable;
import java.time.ZonedDateTime;

import lombok.Getter;
import lombok.Setter;

/**
 * 메시지 클래스
 * 워크플로우에서 데이터 전달을 위한 메시지를 표현함
 * 
 * @author jmsohn
 */
public abstract class Message implements Serializable, Comparable<Message>{
	
	/** */
	private static final long serialVersionUID = 2089135740363342355L;
	
	/** 메시지 처리 중요도 */
	@Getter
	@Setter
	private int priority;
	
	/** 메시지 생성 시간 */
	@Getter
	@Setter
	private ZonedDateTime time;
	
	/**
	 * 다른 메시지와의 비교 메소드 구현(Comparable Interface)
	 * 
	 * @param o 비교 대상 메시지
	 */
	@Override
	public int compareTo(Message o) {
		
		// priority가 높은 것부터 보냄
		if(this.getPriority() < o.getPriority()) {
			return 1;
		} else if(this.getPriority() > o.getPriority()) {
			return -1;
		}
		
		// priority가 동일하면 시간이 빠른 메시지부터 보냄
		return this.getTime().compareTo(o.getTime());
	}
}
