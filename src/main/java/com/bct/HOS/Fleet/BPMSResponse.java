package com.bct.HOS.Fleet;

public class BPMSResponse {

	BPMSOut_Msg out_msg = null;
	String bpms_error_code = null;
	String bpms_error_msg = null;

	public BPMSOut_Msg getOut_msg() {
		return out_msg;
	}

	public void setOut_msg(BPMSOut_Msg out_msg) {
		this.out_msg = out_msg;
	}

	public String getBpms_error_code() {
		return bpms_error_code;
	}

	public void setBpms_error_code(String bpms_error_code) {
		this.bpms_error_code = bpms_error_code;
	}

	public String getBpms_error_msg() {
		return bpms_error_msg;
	}

	public void setBpms_error_msg(String bpms_error_msg) {
		this.bpms_error_msg = bpms_error_msg;
	}

}
