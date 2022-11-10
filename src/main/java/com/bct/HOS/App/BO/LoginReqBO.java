package com.bct.HOS.App.BO;

public class LoginReqBO {
		
	private static final String FORGET_PWD_METHOD_NAME = "validateUserPassword";
	private static final String CHANGE_PWD_METHOD_NAME = "changePassword";
	private static final String FORGET_PWD_PROCESS_NAME = "ForgotPassword";
	private static final String CHANGE_PWD_PROCESS_NAME = "Screen";
	private static final String CHANGE_PWD_WORK_FLOW_NAME = "CoreAdminService";
	
	private String processType;
	private String workFlowName = "CoreLoginService";
	LoginBO workFlowParams;
	
	public LoginReqBO(String processType, LoginBO workFlowParams) {
		this.processType = processType;
		this.workFlowParams = workFlowParams;
	}
	
	public LoginReqBO(String processType, String workFlowName, LoginBO workFlowParams) {
		this.processType = processType;
		this.workFlowParams = workFlowParams;
		this.workFlowName = workFlowName;
	}

    public static LoginReqBO frameForgetPasswordReq(String userID) {
    	
		LoginBO obj = new LoginBO(FORGET_PWD_METHOD_NAME,userID);
	    return new LoginReqBO(FORGET_PWD_PROCESS_NAME, obj);
	}
    
    public static LoginReqBO frameEnterOTPReq(String otp, String newPwd, String confrimPwd) {
    	
		LoginBO obj = new LoginBO(FORGET_PWD_METHOD_NAME,otp,newPwd,confrimPwd);
	    return new LoginReqBO(FORGET_PWD_PROCESS_NAME, obj);
	}

    public static LoginReqBO frameChangePasswordReq(String oldPwd, String newPwd, String confrimPwd, String userID, String ftToken) {
    	
		LoginBO obj = new LoginBO(CHANGE_PWD_METHOD_NAME,oldPwd,newPwd,confrimPwd,userID,ftToken);
		return new LoginReqBO(CHANGE_PWD_PROCESS_NAME, CHANGE_PWD_WORK_FLOW_NAME, obj);
	}

	public String getProcessType() {
		return processType;
	}

	public void setProcessType(String processType) {
		this.processType = processType;
	}

	public String getWorkFlowName() {
		return workFlowName;
	}

	public void setWorkFlowName(String workFlowName) {
		this.workFlowName = workFlowName;
	}

	public LoginBO getWorkFlowParams() {
		return workFlowParams;
	}

	public void setWorkFlowParams(LoginBO workFlowParams) {
		this.workFlowParams = workFlowParams;
	}

	
    
    
}

