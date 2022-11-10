package com.bct.UserMangement.utils;

public class UserMangementException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private Errors error;

	public UserMangementException(Errors error) {
		super();
		this.error = error;
	}
	
	public UserMangementException(Errors error, String msg) {
		super();
		this.error = error;
		this.error.setErrorMsg(msg);
	}

	public Errors getError() {
		return error;
	}

	public void setError(Errors error) {
		this.error = error;
	}
	
	
	
}
