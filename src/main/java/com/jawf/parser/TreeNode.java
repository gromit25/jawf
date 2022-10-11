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
	 * 자식 노드 추가
	 * @param node
	 */
	public void addChild(TreeNode<T> node) {
		this.childs.add(node);
	}
	
	/**
	 * post order로 방문
	 * @return post order로 방문한 목록
	 */
	public ArrayList<T> travelPostOrder() {
		
		// post order로 방문한 목록 변수
		ArrayList<T> list = new ArrayList<T>();
		
		// 자식 노드 방문
		for(TreeNode<T> child: this.childs) {
			list.addAll(child.travelPostOrder());
		}
		
		// 현재 노드 데이터 추가
		list.add(this.data);
		
		// 방문 목록 반환
		return list;
	}

}
