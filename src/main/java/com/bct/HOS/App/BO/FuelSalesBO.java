package com.bct.HOS.App.BO;

public class FuelSalesBO {

	String transaction_no = null;
	int nozzle_no;
	int pump_no;
	int tank_no;
	double opening_reading ;
	double closing_reading ;
	double volume;
	
	public int getNozzle_no() {
		return nozzle_no;
	}
	public void setNozzle_no(int nozzle_no) {
		this.nozzle_no = nozzle_no;
	}
	public double getOpening_reading() {
		return opening_reading;
	}
	public void setOpening_reading(double opening_reading) {
		this.opening_reading = opening_reading;
	}
	public double getClosing_reading() {
		return closing_reading;
	}
	public void setClosing_reading(double closing_reading) {
		this.closing_reading = closing_reading;
	}
	public double getVolume() {
		return volume;
	}
	public void setVolume(double volume) {
		this.volume = volume;
	}
	public int getPump_no() {
		return pump_no;
	}
	public void setPump_no(int pump_no) {
		this.pump_no = pump_no;
	}
	public int getTank_no() {
		return tank_no;
	}
	public void setTank_no(int tank_no) {
		this.tank_no = tank_no;
	}
	public String getTransaction_no() {
		return transaction_no;
	}
	public void setTransaction_no(String transaction_no) {
		this.transaction_no = transaction_no;
	}
	
	
	
}
