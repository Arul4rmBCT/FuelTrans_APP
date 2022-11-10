package com.bct.UserMangement.utils;

public enum Errors {

	INVALID(999,""),
	INVALID_TOKEN(998,"Token is invalid"),
	INVALID_INPUT(997,"Invalid request"),
	ROLE_ORG_UNIQUE (2000,"Role for given Organization already exists"),
	ROLE_NOT_FOUND(2001,"Role not found"),
	USER_ROLE_UNIQUE (3000,"Given user with role already exists"),
	MODULE_NOT_FOUND(2010,"Module not found"),
	SCREEN_NOT_FOUND(2020,"Screen not found"),
	ENTRY_NOT_FOUND(2030,"Entry not found"),
	ROLE_SCREEN_NOT_FOUND(2040,"Role for screen not found"),	
	USER_NOT_FOUND(3001,"User not found");
    
	private int code;
	private String errorMsg;
	
	public String getErrorMsg() {
		return errorMsg;
	}

	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	
	public int getCode() {
		return code;
	}

	public void setCode(int code) {
		this.code = code;
	}

	private Errors(int code,String errorMsg) {
		this.code = code;
		this.errorMsg = errorMsg;
	}

	
}
