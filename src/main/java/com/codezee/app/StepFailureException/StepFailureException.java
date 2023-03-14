package com.codezee.app.StepFailureException;

@SuppressWarnings("serial")
public class StepFailureException extends RuntimeException{
	
	
	public StepFailureException(String message) {
		super (message);
	}
	
	public StepFailureException(String message, Throwable ex) {
		super (message, ex);
	}
}
