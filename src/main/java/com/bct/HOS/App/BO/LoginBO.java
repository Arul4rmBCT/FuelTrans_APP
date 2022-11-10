package com.bct.HOS.App.BO;

public class LoginBO {
	
	private String methodName = "";
	private String strPassword = "";
	private String strNewPassword = "";
	private String strConfPassword = "";
	private String strUserId = "";
	private String strToken = "";


	public LoginBO(String methodName, String strUserId) {
		super();
		this.methodName = methodName;
		this.strUserId = strUserId;
	}
	 
	public LoginBO(String methodName, String strPassword, String strNewPassword, String strConfPassword) {
		super();
		this.methodName = methodName;
		this.strPassword = strPassword;
		this.strNewPassword = strNewPassword;
		this.strConfPassword = strConfPassword;
	}
	
	public LoginBO(String methodName, String strPassword, String strNewPassword, String strConfPassword,
			String strUserId, String ftToken) {
		super();
		this.methodName = methodName;
		this.strPassword = strPassword;
		this.strNewPassword = strNewPassword;
		this.strConfPassword = strConfPassword;
		this.strUserId = strUserId;
		this.strToken = ftToken;
	}

	// Getter Methods 
	 public String getMethodName() {
	  return methodName;
	 }

	 public String getStrPassword() {
	  return strPassword;
	 }

	 public String getStrNewPassword() {
	  return strNewPassword;
	 }

	 public String getStrConfPassword() {
	  return strConfPassword;
	 }

	 public String getStrUserId() {
	  return strUserId;
	 }

	 // Setter Methods 
	 public void setMethodName(String methodName) {
	  this.methodName = methodName;
	 }

	 public void setStrPassword(String strPassword) {
	  this.strPassword = strPassword;
	 }

	 public void setStrNewPassword(String strNewPassword) {
	  this.strNewPassword = strNewPassword;
	 }

	 public void setStrConfPassword(String strConfPassword) {
	  this.strConfPassword = strConfPassword;
	 }

	 public void setStrUserId(String strUserId) {
	  this.strUserId = strUserId;
	 }
	

	public String getStrToken() {
		return strToken;
	}

	public void setStrToken(String strToken) {
		this.strToken = strToken;
	}
}
	