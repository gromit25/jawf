package com.jawf.parser;

import java.util.ArrayList;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 * @author jmsohn
 */
public class TreeNode<T> {
	
	/** 현재 노드의 데이터 */
	@Getter
	@Setter
	private T data;
	
	/** 자식 노드 목록 */
	@Getter
	private ArrayList<TreeNode<T>> childs;
	
	/**
	 * 생성자
	 */
	public TreeNode() {
		this(null);
	}
	
	/**
	 * 생성자
	 * @param data 현재 노드의 데이터
	 */
	public TreeNode(T data) {
		this.data = data;
		this.childs = new ArrayList<TreeNode<T>>();
	}
	
	/**
	 * 
	 * @param node
	 */
	public void addChild(TreeNode<T> node) {
		this.childs.add(node);
	}
	
	/**
	 * 
	 * @return
	 */
	public ArrayList<T> travelPostOrder() {
		
		//
		ArrayList<T> list = new ArrayList<T>();
		for(TreeNode<T> child: this.childs) {
			list.addAll(child.travelPostOrder());
		}
		
		//
		list.add(this.data);
		
		//
		return list;
	}

}
