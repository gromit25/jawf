package com.epozen.meslogparser;

import java.io.PushbackReader;
import java.io.StringReader;
import java.util.ArrayList;

/**
 * 전이 함수 클래스
 * @author jmsohn
 */
public class TransferFunction {
	
	/** 문자 패턴 변수 */
	private CharPattern pattern;
	/** 전이할 다음 상태 변수 */
	private String nextStatus;
	
	/**
	 * 생성자
	 * @param patternStr 다음 전이 상태로 이동시킬 문자 패턴
	 * @param nextStatus 다음 전이 상태
	 */
	public TransferFunction(String patternStr, String nextStatus) throws Exception {
		this.pattern = CharPatternParser.parse(patternStr);
		this.nextStatus = nextStatus;
	}
	
	/**
	 * 주어진 문자가 문자 패턴에 일치하는지 검사하여 반환
	 * @param ch 문자
	 * @return 일치 여부
	 */
	public boolean isValid(char ch) {
		return this.pattern.isValid(ch);
	}

	/**
	 * 다음 전이 상태 
	 * @return
	 */
	public String getNextStatus() {
		return this.nextStatus;
	}
	
	/**
	 * 문자 패턴 클래스
	 * @author jmsohn
	 */
	private static class CharPattern {
		
		/** not 조건 변수 */
		private boolean not = false;
		/** 문자 패턴 목록 변수 */
		private ArrayList<PatternChecker> checkers;
		
		/**
		 * 생성자
		 * @param checkers 문자 패턴 목록
		 * @param not not 조건
		 */
		private CharPattern(ArrayList<PatternChecker> checkers, boolean not) {
			this.checkers = checkers;
			this.not = not;
		}
		
		/**
		 * 문자가 문자 패턴에 일치하는지 여부 검사
		 * @param ch 검사할 문자
		 * @return 일치 여부
		 */
		private boolean isValid(char ch) {
			
			if(this.checkers.size() == 0) {
				return true;
			}
			
			//
			boolean result = false;
			
			for(PatternChecker checker: this.checkers){
				if(checker.isValid(ch) == true) {
					result = true;
					break;
				}
			}
			
			if(this.not == false) {
				return result;
			} else {
				return !result;
			}
		}
		
		/**
		 * CharPattern의 객체의 정보를 문자열로 반환
		 * @return 객체 정보 문자열
		 */
		public String toString() {
			
			StringBuffer buffer = new StringBuffer("");
			
			buffer.append("not=").append(this.not).append("\n");
			
			for(PatternChecker checker: this.checkers) {
				buffer.append(checker.toString()).append("\n");
			}
			
			return buffer.toString();
		}
	}
	
	/**
	 * 문자 패턴 검사 클래스
	 * @author jmsohn
	 */
	private static interface PatternChecker {
		public boolean isValid(char ch);
	}
	
	/**
	 * 한문자와 일치하는지 검사하는 클래스
	 * @author jmsohn
	 */
	private static class CharPatternChecker implements PatternChecker {
		private char ch;
		
		CharPatternChecker(char ch) {
			this.ch = ch;
		}
		
		public boolean isValid(char ch) {
			return this.ch == ch;
		}
		
		public String toString() {
			return "char: '" + this.ch + "'";
		}
	}
	
	/**
	 * 문자가 범위내에 있는지 검사하는 클래스
	 * @author jmsohn
	 */
	private static class RangePatternChecker implements PatternChecker{
		
		/** 범위 중 아래측 변수 */
		private char lowerCh;
		/** 범위 중 위측 변수 */
		private char upperCh;
		
		/**
		 * 
		 * @param lowerCh 범위 중 아래측 값
		 * @param upperCh 범위 중 위측 값
		 */
		RangePatternChecker(char lowerCh, char upperCh) {
			this.lowerCh = lowerCh;
			this.upperCh = upperCh;
		}

		/**
		 * 
		 * @param ch
		 * @return
		 */
		public boolean isValid(char ch) {
			return this.lowerCh <= ch && ch <= this.upperCh;
		}
		
		public String toString() {
			return "range '" + this.lowerCh + "' - '" + this.upperCh + "'";
		}
	}
	
	/**
	 * 
	 * @author jmsohn
	 */
	private static class CharPatternParser {
		
		enum ParsingStatus {
			START,
			CHAR,
			ESCAPE,
			RANGE,
			UPPER_RANGE_ESCAPE
		}
		
		static CharPattern parse(String patternStr) throws Exception {
						
			ArrayList<PatternChecker> checkers = new ArrayList<PatternChecker>();
			
			ParsingStatus status = ParsingStatus.START;
			
			PushbackReader in = new PushbackReader(new StringReader(patternStr));
			int read = in.read();
			char preCh = (char)read;
			boolean not = false;
			
			while(read != -1) {
				
				char ch = (char)read;
				
				switch(status) {
				case START:
					if(ch == '\\') {
						status = ParsingStatus.ESCAPE;
					} else {
						if(ch == '^') {
							not = true;
						} else {
							preCh = ch;
							checkers.add(new TransferFunction.CharPatternChecker(ch));
						}
						
						status = ParsingStatus.CHAR;
					}
					
					break;
					
				case CHAR:
					if(ch == '\\') {
						status = ParsingStatus.ESCAPE;
					} else if (ch == '-') {
						status = ParsingStatus.RANGE;
					} else {
						preCh = ch;
						checkers.add(new TransferFunction.CharPatternChecker(ch));
					}
					
					break;
					
				case ESCAPE:
					preCh = ch;
					checkers.add(new TransferFunction.CharPatternChecker(ch));
					status = ParsingStatus.CHAR;
					
					break;
					
				case RANGE:
					if(ch == '\\') {
						status = ParsingStatus.UPPER_RANGE_ESCAPE;
					} else {
						checkers.remove(checkers.size() - 1);
						checkers.add(new TransferFunction.RangePatternChecker(preCh, ch));
						status = ParsingStatus.CHAR;
					}
					
					break;
					
				case UPPER_RANGE_ESCAPE:
					checkers.remove(checkers.size() - 1);
					checkers.add(new TransferFunction.RangePatternChecker(preCh, ch));
					status = ParsingStatus.CHAR;
					break;
					
				}
				
				read = in.read();
			}
			
			if(status != ParsingStatus.CHAR && status != ParsingStatus.START) {
				throw new Exception("invalid end status: " + status);
			}
			
			return new CharPattern(checkers, not);
		}
		
	}

}