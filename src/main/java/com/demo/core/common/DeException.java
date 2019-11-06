package com.demo.core.common;

public class DeException extends RuntimeException{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public DeException(Throwable t) {
		super(t);
	}
	
	public DeException(String t) {
		super(t);
	}
}
