package com.bct.HOS.BOS.BO;

public class FTP_EmailConfig {

	private String Para_Unit;

	private String Para_Value;

	private String Module;

	private String Para_Text;

	public String getPara_Unit() {
		return Para_Unit;
	}

	public void setPara_Unit(String Para_Unit) {
		this.Para_Unit = Para_Unit;
	}

	public String getPara_Value() {
		return Para_Value;
	}

	public void setPara_Value(String Para_Value) {
		this.Para_Value = Para_Value;
	}

	public String getModule() {
		return Module;
	}

	public void setModule(String Module) {
		this.Module = Module;
	}

	public String getPara_Text() {
		return Para_Text;
	}

	public void setPara_Text(String Para_Text) {
		this.Para_Text = Para_Text;
	}

	@Override
	public String toString() {
		return "ClassPojo [Para_Unit = " + Para_Unit + ", Para_Value = " + Para_Value + ", Module = " + Module
				+ ", Para_Text = " + Para_Text + "]";
	}

}
