package com.haidang.sampleapp.earthquake.exception;

public class NetworkNotAvailableException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	public NetworkNotAvailableException(String msg){
		super(msg);
	
	}
	public NetworkNotAvailableException(){
		super();
	
	}
}
