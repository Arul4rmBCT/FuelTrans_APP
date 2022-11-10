package com.bct.HOS.BOS.BO;

public class NozzleListConfig {

	private String Nozzle_No;

	private String Grade_Id;

	private String Pump_No;

	private String Tank_No2;

	private String DU_No;

	private String Grade_No;

	private String Ph_Id;

	private String Nozzle_Lock;

	private String Tank_No1;

	public String getNozzle_No() {
		return Nozzle_No;
	}

	public void setNozzle_No(String Nozzle_No) {
		this.Nozzle_No = Nozzle_No;
	}

	public String getGrade_Id() {
		return Grade_Id;
	}

	public void setGrade_Id(String Grade_Id) {
		this.Grade_Id = Grade_Id;
	}

	public String getPump_No() {
		return Pump_No;
	}

	public void setPump_No(String Pump_No) {
		this.Pump_No = Pump_No;
	}

	public String getTank_No2() {
		return Tank_No2;
	}

	public void setTank_No2(String Tank_No2) {
		this.Tank_No2 = Tank_No2;
	}

	public String getDU_No() {
		return DU_No;
	}

	public void setDU_No(String DU_No) {
		this.DU_No = DU_No;
	}

	public String getGrade_No() {
		return Grade_No;
	}

	public void setGrade_No(String Grade_No) {
		this.Grade_No = Grade_No;
	}

	public String getPh_Id() {
		return Ph_Id;
	}

	public void setPh_Id(String Ph_Id) {
		this.Ph_Id = Ph_Id;
	}

	public String getNozzle_Lock() {
		return Nozzle_Lock;
	}

	public void setNozzle_Lock(String Nozzle_Lock) {
		this.Nozzle_Lock = Nozzle_Lock;
	}

	public String getTank_No1() {
		return Tank_No1;
	}

	public void setTank_No1(String Tank_No1) {
		this.Tank_No1 = Tank_No1;
	}

	@Override
	public String toString() {
		return "ClassPojo [Nozzle_No = " + Nozzle_No + ", Grade_Id = " + Grade_Id + ", Pump_No = " + Pump_No
				+ ", Tank_No2 = " + Tank_No2 + ", DU_No = " + DU_No + ", Grade_No = " + Grade_No + ", Ph_Id = " + Ph_Id
				+ ", Nozzle_Lock = " + Nozzle_Lock + ", Tank_No1 = " + Tank_No1 + "]";
	}

}
