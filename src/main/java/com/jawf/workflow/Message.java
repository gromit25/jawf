package com.jawf.workflow;

import java.util.HashMap;

import lombok.Getter;
import lombok.Setter;

public class Message {
	
	@Getter
	@Setter
	private int priority;
	
	private HashMap<String, Object> data;

}
