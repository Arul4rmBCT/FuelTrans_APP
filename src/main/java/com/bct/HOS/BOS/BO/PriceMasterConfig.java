package com.bct.HOS.BOS.BO;

public class PriceMasterConfig {

	private String Grade_id;

	private String Apply_Price;

	private String COD_No;

	private String Apply2Time;

	private String Apply_Date;

	public String getGrade_id() {
		return Grade_id;
	}

	public void setGrade_id(String Grade_id) {
		this.Grade_id = Grade_id;
	}

	public String getApply_Price() {
		return Apply_Price;
	}

	public void setApply_Price(String Apply_Price) {
		this.Apply_Price = Apply_Price;
	}

	public String getCOD_No() {
		return COD_No;
	}

	public void setCOD_No(String COD_No) {
		this.COD_No = COD_No;
	}

	public String getApply2Time() {
		return Apply2Time;
	}

	public void setApply2Time(String Apply2Time) {
		this.Apply2Time = Apply2Time;
	}

	public String getApply_Date() {
		return Apply_Date;
	}

	public void setApply_Date(String Apply_Date) {
		this.Apply_Date = Apply_Date;
	}

	@Override
	public String toString() {
		return "ClassPojo [Grade_id = " + Grade_id + ", Apply_Price = " + Apply_Price + ", COD_No = " + COD_No
				+ ", Apply2Time = " + Apply2Time + ", Apply_Date = " + Apply_Date + "]";
	}

}
