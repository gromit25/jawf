package com.jawf.workflow;

import java.util.concurrent.PriorityBlockingQueue;

import lombok.Getter;
import lombok.Setter;

public abstract class AbstractIngress extends AbstractComponent {
	
	@Getter
	@Setter
	private PriorityBlockingQueue<Message> outqueue;
	
	public void run() {
		
	}

}
